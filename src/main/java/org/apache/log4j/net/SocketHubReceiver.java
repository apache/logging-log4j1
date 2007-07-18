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
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.plugins.Plugin;
import org.apache.log4j.plugins.Receiver;
import org.apache.log4j.spi.LoggerRepository;

/**
  SocketHubReceiver receives a remote logging event on a configured
  socket and "posts" it to a LoggerRepository as if the event was
  generated locally. This class is designed to receive events from
  the SocketHubAppender class (or classes that send compatible events).

  <p>Once the event has been "posted", it will be handled by the
  appenders currently configured in the LoggerRespository.

  @author Mark Womack
  @author Ceki G&uuml;lc&uuml;
  @author Paul Smith (psmith@apache.org)
  @since 1.3
*/
public class SocketHubReceiver
extends Receiver implements SocketNodeEventListener, PortBased {

    /**
     * Default reconnection delay.
     */
  static final int DEFAULT_RECONNECTION_DELAY   = 30000;

    /**
     * Host.
     */
  protected String host;

    /**
     * Port.
     */
  protected int port;
    /**
     * Reconnection delay.
     */
  protected int reconnectionDelay = DEFAULT_RECONNECTION_DELAY;

    /**
     * Active.
     */
  protected boolean active = false;

    /**
     * Connector.
     */
  protected Connector connector;

    /**
     * Socket.
     */
  protected Socket socket;

    /**
     * Listener list.
     */
  private List listenerList = Collections.synchronizedList(new ArrayList());

    /**
     * Create new instance.
     */
  public SocketHubReceiver() {
     super();
  }

    /**
     * Create new instance.
     * @param h host
     * @param p port
     */
  public SocketHubReceiver(final String h,
                           final int p) {
    super();
    host = h;
    port = p;
  }

    /**
     * Create new instance.
     * @param h host
     * @param p port
     * @param repo logger repository
     */
  public SocketHubReceiver(final String h,
                           final int p,
                           final LoggerRepository repo) {
    super();
    host = h;
    port = p;
    repository = repo;
  }

  /**
   * Adds a SocketNodeEventListener to this receiver to be notified
   * of SocketNode events.
   * @param l listener
   */
  public void addSocketNodeEventListener(final SocketNodeEventListener l) {
    listenerList.add(l);
  }

  /**
   * Removes a specific SocketNodeEventListener from this instance
   * so that it will no  longer be notified of SocketNode events.
   * @param l listener
   */
  public void removeSocketNodeEventListener(
          final SocketNodeEventListener l) {
    listenerList.remove(l);
  }

  /**
    Get the remote host to connect to for logging events.
    @return host
   */
  public String getHost() {
    return host;
  }

  /**
   * Configures the Host property, this will require activateOptions
   * to be called for this to take effect.
   * @param remoteHost address of remote host.
   */
  public void setHost(final String remoteHost) {
    this.host = remoteHost;
  }
  /**
    Set the remote host to connect to for logging events.
   Equivalent to setHost.
   @param remoteHost address of remote host.
   */
  public void setPort(final String remoteHost) {
    host = remoteHost;
  }

  /**
    Get the remote port to connect to for logging events.
   @return port
   */
  public int getPort() {
    return port;
  }

  /**
    Set the remote port to connect to for logging events.
    @param p port
   */
  public void setPort(final int p) {
    this.port = p;
  }

  /**
     The <b>ReconnectionDelay</b> option takes a positive integer
     representing the number of milliseconds to wait between each
     failed connection attempt to the server. The default value of
     this option is 30000 which corresponds to 30 seconds.

     <p>Setting this option to zero turns off reconnection
     capability.
   @param delay milliseconds to wait or zero to not reconnect.
   */
  public void setReconnectionDelay(final int delay) {
    int oldValue = this.reconnectionDelay;
    this.reconnectionDelay = delay;
    firePropertyChange("reconnectionDelay", oldValue, this.reconnectionDelay);
  }

  /**
     Returns value of the <b>ReconnectionDelay</b> option.
   @return value of reconnection delay option.
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
  public boolean isEquivalent(final Plugin testPlugin) {
    if (testPlugin != null && testPlugin instanceof SocketHubReceiver) {
      SocketHubReceiver sReceiver = (SocketHubReceiver) testPlugin;

      return (port == sReceiver.getPort()
              && host.equals(sReceiver.getHost())
              && reconnectionDelay == sReceiver.getReconnectionDelay()
              && super.isEquivalent(testPlugin));
    }
    return false;
  }

  /**
    Returns true if this receiver is active.
   @return true if receiver is active
   */
  public synchronized boolean isActive() {
    return active;
  }

  /**
    Sets the flag to indicate if receiver is active or not.
   @param b new value
   */
  protected synchronized void setActive(final boolean b) {
    active = b;
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
      if (socket != null) {
        socket.close();
      }
    } catch (Exception e) {
      // ignore for now
    }
    socket = null;

    // stop the connector
    if (connector != null) {
      connector.interrupted = true;
      connector = null;  // allow gc
    }
  }

  /**
    Listen for a socketClosedEvent from the SocketNode. Reopen the
    socket if this receiver is still active.
   @param e exception not used.
   */
  public void socketClosedEvent(final Exception e) {
    // we clear the connector object here
    // so that it actually does reconnect if the
    // remote socket dies.
    connector = null;
    fireConnector(true);
  }

    /**
     * Fire connectors.
     * @param isReconnect true if reconnect.
     */
  private synchronized void fireConnector(final boolean isReconnect) {
    if (active && connector == null) {
      getLogger().debug("Starting a new connector thread.");
      connector = new Connector(isReconnect);
      connector.setDaemon(true);
      connector.setPriority(Thread.MIN_PRIORITY);
      connector.start();
    }
  }

    /**
     * Set socket.
     * @param newSocket new value for socket.
     */
  private synchronized void setSocket(final Socket newSocket) {
    connector = null;
    socket = newSocket;
    SocketNode node = new SocketNode(socket, this);
    node.addSocketNodeEventListener(this);

    synchronized (listenerList) {
        for (Iterator iter = listenerList.iterator(); iter.hasNext();) {
            SocketNodeEventListener listener =
                    (SocketNodeEventListener) iter.next();
            node.addSocketNodeEventListener(listener);
        }
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
   */
  private final class Connector extends Thread {

      /**
       * Interruption status.
       */
    boolean interrupted = false;
      /**
       * If true, then delay on next iteration.
       */
    boolean doDelay;

      /**
       * Create new instance.
       * @param isReconnect true if reconnecting.
       */
    public Connector(final boolean isReconnect) {
      super();
      doDelay = isReconnect;
    }

      /**
       * Attempt to connect until interrupted.
       */
    public void run() {
      while (!interrupted) {
        try {
          if (doDelay) {
            getLogger().debug("waiting for " + reconnectionDelay
              + " milliseconds before reconnecting.");
            sleep(reconnectionDelay);
          }
          doDelay = true;
          getLogger().debug("Attempting connection to " + host);
          Socket s = new Socket(host, port);
          setSocket(s);
          getLogger().debug(
                  "Connection established. Exiting connector thread.");
          break;
        } catch (InterruptedException e) {
          getLogger().debug("Connector interrupted. Leaving loop.");
          return;
        } catch (java.net.ConnectException e) {
          getLogger().debug("Remote host {} refused connection.", host);
        } catch (IOException e) {
          getLogger().debug("Could not connect to {}. Exception is {}.",
                  host, e);
        }
      }
    }
  }

    /**
     * This method does nothing.
     * @param remoteInfo remote info.
     */
  public void socketOpened(final String remoteInfo) {

    // This method does nothing.
  }

}
