/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.log4j.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.plugins.Pauseable;
import org.apache.log4j.plugins.Plugin;
import org.apache.log4j.plugins.Receiver;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.LoggingEvent;


/**
  SocketReceiver receives a remote logging event on a configured
  socket and "posts" it to a LoggerRepository as if the event was
  generated locally. This class is designed to receive events from
  the SocketAppender class (or classes that send compatible events).

  <p>Once the event has been "posted", it will be handled by the
  appenders currently configured in the LoggerRespository.

  @author Mark Womack
  @author Scott Deboy (sdeboy@apache.org)
  @author Paul Smith (psmith@apache.org)
  @since 1.3
*/
public class SocketReceiver extends Receiver implements Runnable, PortBased,
  Pauseable {
    /**
     * socket map.
     */
  private Map socketMap = new HashMap();
    /**
     * Paused.
     */
  private boolean paused;
    /**
     * Thread.
     */
  private Thread rThread;
    /**
     * Port.
     */
  protected int port;
    /**
     * Server socket.
     */
  private ServerSocket serverSocket;
    /**
     * Socket list.
     */
  private Vector socketList = new Vector();
    /**
     * Listener.
     */
  private SocketNodeEventListener listener = null;
    /**
     * Listeners.
     */
  private List listenerList = Collections.synchronizedList(new ArrayList());

    /**
     * Create new instance.
     */
  public SocketReceiver() {
        super();
  }

    /**
     * Create new instance.
     * @param p port
     */
  public SocketReceiver(final int p) {
    super();
    port = p;
  }

    /**
     * Create new instance.
     * @param p port
     * @param repo logger repository
     */
  public SocketReceiver(final int p, final LoggerRepository repo) {
    super();
    this.port = p;
    repository = repo;
  }

    /** {@inheritDoc} */
  public int getPort() {
    return port;
  }

  /** {@inheritDoc} */
  public void setPort(final int p) {
    port = p;
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
  public boolean isEquivalent(final Plugin testPlugin) {
    if ((testPlugin != null) && testPlugin instanceof SocketReceiver) {
      SocketReceiver sReceiver = (SocketReceiver) testPlugin;

      return (port == sReceiver.getPort() && super.isEquivalent(testPlugin));
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
    }
  }

  /**
   * Called when the receiver should be stopped. Closes the
   * server socket and all of the open sockets.
   */
  public synchronized void shutdown() {
    getLogger().debug(getName() + " received shutdown request");

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

    getLogger().debug(getName() + " doShutdown called");

    // close the server socket
    closeServerSocket();

    // close all of the accepted sockets
    closeAllAcceptedSockets();
  }

  /**
   * Closes the server socket, if created.
   */
  private void closeServerSocket() {
    getLogger().debug("{} closing server socket", getName());

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
      getLogger().error(
        "error starting SocketReceiver (" + this.getName()
        + "), receiver did not start", e);
      active = false;

      return;
    }

    Socket socket = null;

    try {
      getLogger().debug("in run-about to enter while not interrupted loop");

      active = true;

      while (!rThread.isInterrupted()) {
        // if we have a socket, start watching it
        if (socket != null) {
          getLogger().debug(
                  "socket not null - creating and starting socketnode");
          socketList.add(socket);

          SocketNode node = new SocketNode(socket, this);
          synchronized (listenerList) {
            for (Iterator iter = listenerList.iterator();
                 iter.hasNext();) {
                SocketNodeEventListener l =
                        (SocketNodeEventListener) iter.next();
                node.addSocketNodeEventListener(l);
            }
          }
          socketMap.put(socket, node);
          new Thread(node).start();
          socket = null;
        }

        getLogger().debug("waiting to accept socket");

        // wait for a socket to open, then loop to start it
        socket = serverSocket.accept();
        getLogger().debug("accepted socket");
      }
    } catch (Exception e) {
      getLogger().warn(
        "exception while watching socket server in SocketReceiver ("
        + this.getName() + "), stopping");
    }

    getLogger().debug("{} has exited the not interrupted loop", getName());

    // socket not watched because we a no longer running
    // so close it now.
    if (socket != null) {
      try {
        socket.close();
      } catch (IOException e1) {
        getLogger().warn("socket exception caught - socket closed");
      }
    }

    getLogger().debug("{} is exiting main run loop", getName());
  }

  /**
   * Returns a Vector of SocketDetail representing the IP/Domain name
   * of the currently connected sockets that this receiver has
   * been responsible for creating.
   * @return Vector of SocketDetails
   */
  public Vector getConnectedSocketDetails() {
    Vector details = new Vector(socketList.size());

    for (Enumeration enumeration = socketList.elements();
         enumeration.hasMoreElements();
            ) {
      Socket socket = (Socket) enumeration.nextElement();
      details.add(
        new SocketDetail(socket, (SocketNode) socketMap.get(socket)));
    }

    return details;
  }

  /**
   * Returns the currently configured SocketNodeEventListener that
   * will be automatically set for each SocketNode created.
   * @return SocketNodeEventListener currently configured
   *
   * @deprecated This receiver now supports multiple listeners
   */
  public SocketNodeEventListener getListener() {
    return listener;
  }

  /**
   * Adds the listener to the list of listeners to be notified of the
   * respective event.
   * @param l the listener to add to the list
   */
  public void addSocketNodeEventListener(
          final SocketNodeEventListener l) {
    listenerList.add(l);
  }

  /**
   * Removes the registered Listener from this instances list of
   * listeners.  If the listener has not been registered, then invoking
   * this method has no effect.
   *
   * @param l the SocketNodeEventListener to remove
   */
  public void removeSocketNodeEventListener(
          final SocketNodeEventListener l) {
    listenerList.remove(l);
  }

  /**
   * Sets the SocketNodeEventListener that will be used for each
   * created SocketNode.
   * @param l the listener to set on each creation of a SocketNode
   * @deprecated This receiver now supports multiple listeners and
   * so this method simply removes the listener (if there already)
   * and readds it to the list.
   *
   * The passed listener will also be returned via the getListener()
   * method still, but this is also deprecated
   */
  public void setListener(final SocketNodeEventListener l) {
    removeSocketNodeEventListener(l);
    addSocketNodeEventListener(l);
    this.listener = l;
  }

    /** {@inheritDoc} */
  public boolean isPaused() {
    return paused;
  }

    /** {@inheritDoc} */
  public void setPaused(final boolean b) {
    paused = b;
  }

    /**
     * Socket detail.
     */
  public static final class SocketDetail implements AddressBased, PortBased,
    Pauseable {
      /**
       * Address.
       */
    private String address;
      /**
       * Port.
       */
    private int port;
      /**
       * Socket node.
       */
    private SocketNode socketNode;

      /**
       * Create new instance.
       * @param socket socket
       * @param node socket node
       */
    private SocketDetail(final Socket socket,
                         final SocketNode node) {
      super();
      this.address = socket.getInetAddress().getHostName();
      this.port = socket.getPort();
      this.socketNode = node;
    }

      /** {@inheritDoc} */
    public String getAddress() {
      return address;
    }

      /** {@inheritDoc} */
    public int getPort() {
      return port;
    }

      /** {@inheritDoc} */
    public String getName() {
      return "Socket";
    }

      /** {@inheritDoc} */
    public boolean isActive() {
      return true;
    }

      /** {@inheritDoc} */
    public boolean isPaused() {
      return socketNode.isPaused();
    }

      /** {@inheritDoc} */
    public void setPaused(final boolean b) {
      socketNode.setPaused(b);
    }
  }
    /** {@inheritDoc} */
  public void doPost(final LoggingEvent event) {
    if (!isPaused()) {
      super.doPost(event);
    }
  }

}
