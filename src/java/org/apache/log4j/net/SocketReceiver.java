/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j.net;

import java.util.Vector;
import java.net.Socket;
import java.net.ServerSocket;

import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.plugins.Receiver;
import org.apache.log4j.helpers.LogLog;

/**
  SocketReceiver receives a remote logging event on a configured
  socket and "posts" it to a LoggerRepository as if the event was 
  generated locally. This class is designed to receive events from 
  the SocketAppender class (or classes that send compatible events).
  
  <p>Once the event has been "posted", it will be handled by the 
  appenders currently configured in the LoggerRespository.
  
  @author Mark Womack
  @since 1.3
*/
public class SocketReceiver extends Receiver implements Runnable {
  
  protected int port;
  protected boolean active = false;
  
  private ServerSocket serverSocket;
  private Vector socketList = new Vector();
  
  public SocketReceiver() { }
  
  public SocketReceiver(int _port) {
    port = _port;
  }
  
  public SocketReceiver(int _port, LoggerRepository _repository) {
    port = _port;
    repository = _repository;
  }
  
  /**
    Get the port to receive logging events on. */
  public int getPort() {
    return port;
  }
  
  /**
    Set the port to receive logging events on. */
  public void setPort(int _port) {
    port = _port;
  }
  
  /**
    Returns true if the receiver is the same class and they are
    configured for the same port, logger repository, and name.
    This is used when determining if the same receiver is being
    configured.  */
  public boolean equals(Object obj) {
    if (obj != null && obj instanceof SocketReceiver) {
      SocketReceiver sReceiver = (SocketReceiver)obj;
      String sName = sReceiver.getName();
      return (repository == sReceiver.getLoggerRepository() &&
        port == sReceiver.getPort() &&
        ((sName != null && sName.equals(sReceiver.getName()) || 
         (sName == null && sReceiver.getName() == null))));
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
      Thread rThread = new Thread(this);
      rThread.setDaemon(true);
      rThread.start();
    }
  }
  
  /**
    Called when the receiver should be stopped. Closes the
    server socket and all of the open sockets. */
  public synchronized void shutdown() {
    // mark this as no longer running
    active = false;
    
    // close the server socket
    try {
      if (serverSocket != null)
        serverSocket.close();
    } catch (Exception e) {
      // ignore for now
    }
    
    // close all of the accepted sockets
    for (int x = 0; x < socketList.size(); x++) {
      try {
        ((Socket)socketList.get(x)).close();
      } catch (Exception e) {
        // ignore for now
      }
    }
    
    // clear member variables
    serverSocket = null;
    socketList.clear();
  }
  
  /**
    Loop, accepting new socket connections. */
  public void run() {    
    setActive(true);
    
    // start the server socket
    try {
      serverSocket = new ServerSocket(port);
    } catch (Exception e) {
      LogLog.error("error starting SocketReceiver (" + 
        this.getName() + "), receiver did not start", e);
      setActive(false);
      return;
    }

    try {
      Socket socket = null;
      
      while(isActive()) {
        // if we have a socket, start watching it
        if (socket != null) {
          socketList.add(socket);
          SocketNode node = new SocketNode(socket, this);
          new Thread(node).start();
          socket = null;
        }
        
        // wait for a socket to open, then loop to start it
        socket = serverSocket.accept();
      }
      
      // socket not watched because we a no longer running
      // so close it now.
      if (socket != null) {
        socket.close();
      }
    } catch (Exception e) {
      LogLog.warn("exception while watching socket server in SocketReceiver (" +
        this.getName() + "), stopping", e);
    }
    
    setActive(false);
  }
}