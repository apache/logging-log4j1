/*
 * ============================================================================
 *                   The Apache Software License, Version 1.1
 * ============================================================================
 *
 *    Copyright (C) 1999 The Apache Software Foundation. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modifica-
 * tion, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of  source code must  retain the above copyright  notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any, must
 *    include  the following  acknowledgment:  "This product includes  software
 *    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
 *    Alternately, this  acknowledgment may  appear in the software itself,  if
 *    and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "log4j" and  "Apache Software Foundation"  must not be used to
 *    endorse  or promote  products derived  from this  software without  prior
 *    written permission. For written permission, please contact
 *    apache@apache.org.
 *
 * 5. Products  derived from this software may not  be called "Apache", nor may
 *    "Apache" appear  in their name,  without prior written permission  of the
 *    Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 * APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 * DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 * ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 * (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * This software  consists of voluntary contributions made  by many individuals
 * on  behalf of the Apache Software  Foundation.  For more  information on the
 * Apache Software Foundation, please see <http://www.apache.org/>.
 *
 */

package org.apache.log4j.net;

import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.plugins.Pauseable;
import org.apache.log4j.plugins.Receiver;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.LoggingEvent;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.event.EventListenerList;


/**
  SocketReceiver receives a remote logging event on a configured
  socket and "posts" it to a LoggerRepository as if the event was
  generated locally. This class is designed to receive events from
  the SocketAppender class (or classes that send compatible events).

  <p>Once the event has been "posted", it will be handled by the
  appenders currently configured in the LoggerRespository.

  @author Mark Womack
  @author Scott Deboy <sdeboy@apache.org>
  @author Paul Smith <psmith@apache.org>
  @since 1.3
*/
public class SocketReceiver extends Receiver implements Runnable, PortBased,
  Pauseable {
  private Map socketMap = new HashMap();
  private boolean paused;
  private boolean shutdown;
  private Thread rThread;
  protected int port;
  private ServerSocket serverSocket;
  private Vector socketList = new Vector();
  private SocketNodeEventListener listener = null;
  private EventListenerList listenerList = new EventListenerList();

  public SocketReceiver() {
  }

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
    if ((obj != null) && obj instanceof SocketReceiver) {
      SocketReceiver sReceiver = (SocketReceiver) obj;
      String sName = sReceiver.getName();

      return ((repository == sReceiver.getLoggerRepository())
      && (port == sReceiver.getPort())
      && (((sName != null) && sName.equals(sReceiver.getName()))
      || ((sName == null) && (sReceiver.getName() == null))));
    }

    return false;
  }

  /**
    Starts the SocketReceiver with the current options. */
  public void activateOptions() {
    if (!isActive()) {
      //      shutdown();
      rThread = new Thread(this);
      rThread.setDaemon(true);
      rThread.start();
      active = true;
      setShutdown(false);
    }
  }

  /**
   * Called when the receiver should be stopped. Closes the
   * server socket and all of the open sockets.
   */
  public synchronized void shutdown() {
    LogLog.debug(getName() + " received shutdown request");

    // mark this as no longer running
    active = false;

    if (rThread != null) {
      rThread.interrupt();
      rThread = null;
    }

    doShutdown();
  }

  /**
   * Does the actual shutting down by closing the server socket
   * and any connected sockets that have been created.
   */
  private synchronized void doShutdown() {
    active = false;

    LogLog.debug(getName() + " doShutdown called");

    // close the server socket
    closeServerSocket();

    // close all of the accepted sockets
    closeAllAcceptedSockets();

    setShutdown(true);
  }

  /**
   * Closes the server socket, if created.
   */
  private void closeServerSocket() {
    LogLog.debug(getName() + " closing server socket");

    try {
      if (serverSocket != null) {
        serverSocket.close();
      }
    } catch (Exception e) {
      // ignore for now
    }

    serverSocket = null;
  }

  /**
   * Closes all the connected sockets in the List.
   */
  private synchronized void closeAllAcceptedSockets() {
    for (int x = 0; x < socketList.size(); x++) {
      try {
        ((Socket) socketList.get(x)).close();
      } catch (Exception e) {
        // ignore for now
      }
    }

    // clear member variables
    socketMap.clear();
    socketList.clear();
  }

  /**
    Loop, accepting new socket connections. */
  public void run() {
    /**
     * Ensure we start fresh.
     */
    closeServerSocket();
    closeAllAcceptedSockets();

    // start the server socket
    try {
      serverSocket = new ServerSocket(port);
    } catch (Exception e) {
      LogLog.error(
        "error starting SocketReceiver (" + this.getName()
        + "), receiver did not start", e);
      active = false;
      setShutdown(true);

      return;
    }

    Socket socket = null;

    try {
      LogLog.debug("in run-about to enter while not interrupted loop");

      active = true;

      while (!rThread.isInterrupted()) {
        // if we have a socket, start watching it
        if (socket != null) {
          LogLog.debug("socket not null - creating and starting socketnode");
          socketList.add(socket);

          SocketNode node = new SocketNode(socket, this);
          SocketNodeEventListener[] listeners =
            (SocketNodeEventListener[]) listenerList.getListeners(
              SocketNodeEventListener.class);

          for (int i = 0; i < listeners.length; i++) {
            node.addSocketNodeEventListener(listeners[i]);
          }

          socketMap.put(socket, node);
          new Thread(node).start();
          socket = null;
        }

        LogLog.debug("waiting to accept socket");

        // wait for a socket to open, then loop to start it
        socket = serverSocket.accept();
        LogLog.debug("accepted socket");
      }
    } catch (Exception e) {
      LogLog.warn(
        "exception while watching socket server in SocketReceiver ("
        + this.getName() + "), stopping", e);
    }

    LogLog.debug(getName() + " has exited the not interrupted loop");

    // socket not watched because we a no longer running
    // so close it now.
    if (socket != null) {
      try {
        socket.close();
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    }

    LogLog.debug(getName() + " is exiting main run loop");
  }

  /**
   * Returns a Vector of SocketDetail representing the IP/Domain name
   * of the currently connected sockets that this receiver has
   * been responsible for creating.
   * @return Vector of SocketDetails
   */
  public Vector getConnectedSocketDetails() {
    Vector details = new Vector(socketList.size());

    for (Enumeration enum = socketList.elements(); enum.hasMoreElements();) {
      Socket socket = (Socket) enum.nextElement();
      details.add(
        new SocketDetail(socket, (SocketNode) socketMap.get(socket)));
    }

    return details;
  }

  /**
   * Returns the currently configured SocketNodeEventListener that
   * will be automatically set for each SocketNode created
   * @return SocketNodeEventListener currently configured
   *
   * @deprecated This receiver now supports multiple listeners
   */
  public SocketNodeEventListener getListener() {
    return listener;
  }

  /**
   * Adds the listener to the list of listeners to be notified of the
   * respective event
   * @param listener the listener to add to the list
   */
  public void addSocketNodeEventListener(SocketNodeEventListener listener) {
    listenerList.add(SocketNodeEventListener.class, listener);
  }

  /**
   * Removes the registered Listener from this instances list of
   * listeners.  If the listener has not been registered, then invoking
   * this method has no effect.
   *
   * @param listener the SocketNodeEventListener to remove
   */
  public void removeSocketNodeEventListener(SocketNodeEventListener listener) {
    listenerList.remove(SocketNodeEventListener.class, listener);
  }

  /**
   * Sets the SocketNodeEventListener that will be used for each
   * created SocketNode
   * @param listener the listener to set on each creation of a SocketNode
   * @deprecated This receiver now supports multiple listeners and
   * so this method simply removes the listener (if there already)
   * and readds it to the list.
   *
   * The passed listener will also be returned via the getListener()
   * method still, but this is also deprecated
   */
  public void setListener(SocketNodeEventListener listener) {
    removeSocketNodeEventListener(listener);
    addSocketNodeEventListener(listener);
    this.listener = listener;
  }

  /**
   * Returns the shutdown property of this Receiver
   * @return
   */
  private boolean isShutdown() {
    return shutdown;
  }

  /**
   * @param b
   */
  private void setShutdown(boolean b) {
    shutdown = b;
  }

  public boolean isPaused() {
    return paused;
  }

  public void setPaused(boolean b) {
    paused = b;
  }

  public static class SocketDetail implements AddressBased, PortBased,
    Pauseable {
    private String address;
    private int port;
    private SocketNode socketNode;

    private SocketDetail(Socket socket, SocketNode socketNode) {
      this.address = socket.getInetAddress().getHostName();
      this.port = socket.getPort();
      this.socketNode = socketNode;
    }

    public String getAddress() {
      return address;
    }

    public int getPort() {
      return port;
    }

    public String getName() {
      return "Socket";
    }

    public boolean isActive() {
      return true;
    }

    public boolean isPaused() {
      return socketNode.isPaused();
    }

    public void setPaused(boolean paused) {
      socketNode.setPaused(paused);
    }
  }
  /* (non-Javadoc)
   * @see org.apache.log4j.plugins.Receiver#doPost(org.apache.log4j.spi.LoggingEvent)
   */
  public void doPost(LoggingEvent event) {
    if(!isPaused()){
      super.doPost(event);
    }
  }

}
