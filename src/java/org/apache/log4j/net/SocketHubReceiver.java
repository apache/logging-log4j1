/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j.net;

import java.io.IOException;
import java.net.Socket;

import javax.swing.event.EventListenerList;

import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.plugins.Receiver;
import org.apache.log4j.plugins.Plugin;
import org.apache.log4j.helpers.LogLog;

/**
  SocketHubReceiver receives a remote logging event on a configured
  socket and "posts" it to a LoggerRepository as if the event was 
  generated locally. This class is designed to receive events from 
  the SocketHubAppender class (or classes that send compatible events).
  
  <p>Once the event has been "posted", it will be handled by the 
  appenders currently configured in the LoggerRespository.
  
  @author Mark Womack
  @author Ceki G&uuml;lc&uuml;
  @author Paul Smith <psmith@apache.org>
  @since 1.3
*/
public class SocketHubReceiver
extends Receiver implements SocketNodeEventListener, PortBased {
  
  static final int DEFAULT_RECONNECTION_DELAY   = 30000;

  protected String host;
  protected int port;
  protected int reconnectionDelay = DEFAULT_RECONNECTION_DELAY;
  protected boolean active = false;
  protected Connector connector;
  
  protected Socket socket;
  
  private EventListenerList listenerList = new EventListenerList();
    
  public SocketHubReceiver() { }
  
  public SocketHubReceiver(String _host, int _port) {
    host = _host;
    port = _port;
  }
  
  public SocketHubReceiver(String _host, int _port, LoggerRepository _repository) {
    host = _host;
    port = _port;
    repository = _repository;
  }
  
  /**
   * Adds a SocketNodeEventListener to this receiver to be notified
   * of SocketNode events
   * @param l
   */
  public void addSocketNodeEventListener(SocketNodeEventListener l){
  	listenerList.add(SocketNodeEventListener.class, l);
  }
  
  /**
   * Removes a specific SocketNodeEventListener from this instance
   * so that it will no  longer be notified of SocketNode events.
   * @param l
   */
  public void removeSocketNodeEventListener(SocketNodeEventListener l){
  	listenerList.remove(SocketNodeEventListener.class, l);
  }
  
  /**
    Get the remote host to connect to for logging events. */
  public String getHost() {
    return host;
  }
  
  /**
   * Configures the Host property, this will require activateOptions
   * to be called for this to take effect.
   * @param host
   */
  public void setHost(String host){
  	this.host = host;
  }
  /**
    Set the remote host to connect to for logging events. */
  public void setPort(String _host) {
    host = _host;
  }
  
  /**
    Get the remote port to connect to for logging events. */
  public int getPort() {
    return port;
  }
  
  /**
    Set the remote port to connect to for logging events. */
  public void setPort(int _port) {
    port = _port;
  }

  /**
     The <b>ReconnectionDelay</b> option takes a positive integer
     representing the number of milliseconds to wait between each
     failed connection attempt to the server. The default value of
     this option is 30000 which corresponds to 30 seconds.

     <p>Setting this option to zero turns off reconnection
     capability.
   */
  public void setReconnectionDelay(int delay) {
    int oldValue = this.reconnectionDelay;
    this.reconnectionDelay = delay;
    firePropertyChange("reconnectionDelay", oldValue,this.reconnectionDelay);
  }

  /**
     Returns value of the <b>ReconnectionDelay</b> option.
   */
  public int getReconnectionDelay() {
    return reconnectionDelay;
  }
  
  /**
   * Returns true if the receiver is the same class and they are
   * configured for the same properties, and super class also considers
   * them to be equivalent. This is used by PluginRegistry when determining
   * if the a similarly configured receiver is being started.
   * 
   * @param testPlugin The plugin to test equivalency against.
   * @return boolean True if the testPlugin is equivalent to this plugin.
   */
  public boolean isEquivalent(Plugin testPlugin) {
    if (testPlugin != null && testPlugin instanceof SocketHubReceiver) {
      SocketHubReceiver sReceiver = (SocketHubReceiver)testPlugin;

      return (port == sReceiver.getPort() &&
        host.equals(sReceiver.getHost()) &&
        reconnectionDelay == sReceiver.getReconnectionDelay() &&
        super.isEquivalent(testPlugin));
    }
    
    return false;
  }
  
  /**
    Returns true if this receiver is active. */
  public synchronized boolean isActive() {
    return active;
  }
  
  /**
    Sets the flag to indicate if receiver is active or not. */
  protected synchronized void setActive(boolean _active) {
    active = _active;
  }
  
  /**
    Starts the SocketReceiver with the current options. */
  public void activateOptions() {
    if (!isActive()) {
      setActive(true);
      fireConnector(false);
    }
  }
  
  /**
    Called when the receiver should be stopped. Closes the socket */
  public synchronized void shutdown() {
    // mark this as no longer running
    active = false;
    
    // close the socket
    try {
      if (socket != null)
        socket.close();
    } catch (Exception e) {
      // ignore for now
    }
    socket = null;
    
    // stop the connector
    if(connector != null) {
      connector.interrupted = true;
      connector = null;  // allow gc
    }
  }
  
  /**
    Listen for a socketClosedEvent from the SocketNode. Reopen the
    socket if this receiver is still active. */
  public void socketClosedEvent(Exception e) {
  	// we clear the connector object here so that it actually does reconnect if the 
  	// remote socket dies.
  	connector = null;
    fireConnector(true);
  }
  
  private synchronized void fireConnector(boolean isReconnect) {
    if (active && connector == null) {
      LogLog.debug("Starting a new connector thread.");
      connector = new Connector(isReconnect);
      connector.setDaemon(true);
      connector.setPriority(Thread.MIN_PRIORITY);
      connector.start();
    }
  }
  
  private synchronized void setSocket(Socket _socket) {
    connector = null;
    socket = _socket;
    SocketNode node = new SocketNode(socket, this);
    node.addSocketNodeEventListener(this);
	SocketNodeEventListener[] listeners =
	  (SocketNodeEventListener[]) listenerList.getListeners(
		SocketNodeEventListener.class);

	for (int i = 0; i < listeners.length; i++) {
	  node.addSocketNodeEventListener(listeners[i]);
	}
    new Thread(node).start();
  }
  
  /**
   The Connector will reconnect when the server becomes available
   again.  It does this by attempting to open a new connection every
   <code>reconnectionDelay</code> milliseconds.

   <p>It stops trying whenever a connection is established. It will
   restart to try reconnect to the server when previpously open
   connection is droppped.

   @author  Ceki G&uuml;lc&uuml;
   @since 0.8.4  */
  class Connector extends Thread {

    boolean interrupted = false;
    boolean doDelay;
    
    public Connector(boolean isReconnect) {
      doDelay = isReconnect;
    }
    
    public void run() {
      while(!interrupted) {
        try {
       	  if (doDelay) {
       	    LogLog.debug("waiting for " + reconnectionDelay + 
       	      " milliseconds before reconnecting.");
       	    sleep(reconnectionDelay);
       	  }
       	  doDelay = true;
     	    LogLog.debug("Attempting connection to "+ host);
      	  Socket socket = new Socket(host, port);
      	  setSocket(socket);
      	  LogLog.debug("Connection established. Exiting connector thread.");
      	  break;
      	} catch(InterruptedException e) {
      	  LogLog.debug("Connector interrupted. Leaving loop.");
      	  return;
      	} catch(java.net.ConnectException e) {
      	  LogLog.debug("Remote host "+ host
      		       +" refused connection.");
      	} catch(IOException e) {
      	  LogLog.debug("Could not connect to " + host +
      		       ". Exception is " + e);
      	}
      }
    }
  }

  public void socketOpened(String remoteInfo) {

    // This method does nothing.    
  }

}