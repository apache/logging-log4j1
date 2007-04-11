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


// Contributors: Dan MacDonald <dan@redknee.com>
package org.apache.log4j.net;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.helpers.Constants;
import org.apache.log4j.spi.LoggingEvent;

import java.io.IOException;
import java.io.ObjectOutputStream;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


/**
 * Sends {@link LoggingEvent} objects to a remote a log server, usually a 
 * {@link SocketNode}.
 * <p>
 * The SocketAppender has the following properties:
 * 
 * <ul>
 *   <li>If sent to a {@link SocketNode}, remote logging is non-intrusive as 
 * far as the log event is concerned. In other words, the event will be logged 
 * with the same time stamp, {@link org.apache.log4j.NDC}, location info as if 
 * it were logged locally by the client.
 * </li>
 * 
 * <li>SocketAppenders do not use a layout. They ship a serialized 
 * {@link LoggingEvent} object to the server side.</li>
 * 
 * <li>Remote logging uses the TCP protocol. Consequently, if the server is 
 * reachable, then log events will eventually arrive at the server.
 * 
 * <p><li>If the remote server is down, the logging requests are simply dropped.
 *  However, if and when the server comes back up then event transmission is 
 * resumed transparently. This transparent reconneciton is performed by a 
 * <em>connector</em> thread which periodically attempts to connect to 
 * the server.
 * 
 * <p><li>Logging events are automatically <em>buffered</em> by the  native TCP 
 * implementation. This means that if the link to server is slow but still 
 * faster than the rate of (log) event production by the client, the client will
 * not be affected by the slow network connection. However, if the network 
 * connection is slower then the rate of event production, then the client can 
 * only progress at the network rate. In particular, if the network link to the 
 * the server is down, the client will be blocked.
 * 
 * <p>On the other hand, if the network link is up, but the server is down, the 
 * client will not be blocked when making log requests but the log events will 
 * be lost due to server unavailability.
 * 
 * <p><li>Even if a <code>SocketAppender</code> is no longer attached to any 
 * category, it will not be garbage collected in the presence of a connector 
 * thread. A connector thread exists only if the connection to the server is 
 * down. To avoid this garbage collection problem, you should {@link #close} 
 * the the <code>SocketAppender</code> explicitly. See also next item.
 * 
 * <p>Long lived applications which create/destroy many 
 * <code>SocketAppender</code> instances should be aware of this garbage 
 * collection problem. Most other applications can safely ignore it.
 * 
 * <p><li>If the JVM hosting the <code>SocketAppender</code> exits before the 
 * <code>SocketAppender</code> is closed either explicitly or subsequent to 
 * garbage collection, then there might be untransmitted data in the pipe 
 * which might be lost. This is a common problem on Windows based systems.
 * 
 * <p>To avoid lost data, it is usually sufficient to {@link #close} the 
 * <code>SocketAppender</code> either explicitly or by calling the 
 * {@link org.apache.log4j.LogManager#shutdown} method before exiting the 
 * application.
 * 
 * </ul>
 * 
 * @author  Ceki G&uuml;lc&uuml;
 * @since 0.8.4 
 * */
public class SocketAppender extends AppenderSkeleton {
  /**
   * The default port number of remote logging server (4560).
   */
  public static final int DEFAULT_PORT = 4560;

  /**
   * The default reconnection delay (30000 milliseconds or 30 seconds).
   */
  static final int DEFAULT_RECONNECTION_DELAY = 30000;

  // reset the ObjectOutputStream every 70 calls
  //private static final int RESET_FREQUENCY = 70;
  private static final int RESET_FREQUENCY = 1;

  /**
   * We remember host name as String in addition to the resolved
   * InetAddress so that it can be returned via getOption().
   */
  String remoteHost;
  InetAddress address;
  int port = DEFAULT_PORT;
  ObjectOutputStream oos;
  int reconnectionDelay = DEFAULT_RECONNECTION_DELAY;
  boolean locationInfo = false;
  private Connector connector;
  int counter = 0;
  String hostname;
  String application;

  public SocketAppender() {
      super(false);
  }

  /**
         Connects to remote server at <code>address</code> and <code>port</code>.
  */
  public SocketAppender(InetAddress address, int port) {
    super(false);
    this.address = address;
    this.remoteHost = address.getHostName();
    this.port = port;
    activateOptions();
  }

  /**
         Connects to remote server at <code>host</code> and <code>port</code>.
  */
  public SocketAppender(String host, int port) {
    super(false);
    this.port = port;
    this.address = getAddressByName(host);
    this.remoteHost = host;
    activateOptions();
  }

  /**
         Connect to the specified <b>RemoteHost</b> and <b>Port</b>.
  */
  public void activateOptions() {
    try {
      hostname = InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException uhe) {
      try {
        hostname = InetAddress.getLocalHost().getHostAddress();
      } catch (UnknownHostException uhe2) {
        hostname = "unknown";
      }
    }
    if(remoteHost != null) {
      address = getAddressByName(remoteHost);
      connect(address, port);
    } else {
      String err = "The RemoteHost property is required for SocketAppender named "+ name;
      getLogger().error(err);
      throw new IllegalStateException(err);
    }
    // all is dandy on the eastern front.
    super.activateOptions();
  }

  /**
   * Close this appender.
   *
   * <p>This will mark the appender as closed and call then {@link
   * #cleanUp} method.
   * */
  public synchronized void close() {
    if (closed) {
      return;
    }

    this.closed = true;
    cleanUp();
  }

  /**
   * Drop the connection to the remote host and release the underlying
   * connector thread if it has been created
   * */
  public void cleanUp() {
    if (oos != null) {
      try {
        oos.close();
      } catch (IOException e) {
        getLogger().error("Could not close oos.", e);
      }

      oos = null;
    }

    if (connector != null) {
      //LogLog.debug("Interrupting the connector.");
      connector.interrupted = true;
      connector = null; // allow gc
    }
  }

  void connect(InetAddress address, int port) {
    if (this.address == null) {
      return;
    }

    try {
      // First, close the previous connection if any.
      cleanUp();
      oos =
        new ObjectOutputStream(new Socket(address, port).getOutputStream());
    } catch (IOException e) {
      String msg =
        "Could not connect to remote log4j server at ["
        + address.getHostName() + "].";

      if (reconnectionDelay > 0) {
        msg += " We will try again later.";
        fireConnector(); // fire the connector thread
      }

      /**
       * Rather than log an ugly stack trace, output the msg
       */
      getLogger().error(msg + "(" + e.getMessage() + ")");
    }
  }

  public void append(LoggingEvent event) {
    if (event == null) {
      return;
    }

   if (oos != null) {
      try {
        if (locationInfo) {
          event.getLocationInformation();
        }

        if (hostname != null) {
          event.setProperty(Constants.HOSTNAME_KEY, hostname);
        }

        if (application != null) {
          event.setProperty(Constants.APPLICATION_KEY, application);
        }

        oos.writeObject(event);
        oos.flush();

        if (++counter >= RESET_FREQUENCY) {
          counter = 0;

          // Failing to reset the object output stream every now and
          // then creates a serious memory leak.
          //System.err.println("Doing oos.reset()");
          oos.reset();
        }
      } catch (IOException e) {
        oos = null;
        getLogger().warn("Detected problem with connection: " + e);

        if (reconnectionDelay > 0) {
          fireConnector();
        }
      }
    }
  }

  void fireConnector() {
    if (connector == null) {
      getLogger().debug("Starting a new connector thread.");
      connector = new Connector();
      connector.setDaemon(true);
      connector.setPriority(Thread.MIN_PRIORITY);
      connector.start();
    }
  }

  InetAddress getAddressByName(String host) {
    try {
      return InetAddress.getByName(host);
    } catch (Exception e) {
      getLogger().error("Could not find address of [" + host + "].", e);
      return null;
    }
  }

  /**
   * The <b>RemoteHost</b> option takes a string value which should be
   * the host name of the server where a {@link SocketNode} or a 
   * {@link SocketReceiver} is running.
   * */
  public void setRemoteHost(String host) {
    remoteHost = host;
  }

  /**
   * Returns value of the <b>RemoteHost</b> option.
   */
  public String getRemoteHost() {
    return remoteHost;
  }

  /**
   * The <b>Port</b> option takes a positive integer representing the port 
   * where the server is waiting for connections.
   */
  public void setPort(int port) {
    this.port = port;
  }

  /**
   * Returns value of the <b>Port</b> option.
   */
  public int getPort() {
    return port;
  }

  /**
   * The <b>LocationInfo</b> option takes a boolean value. If true, the 
   * information sent to the remote host will include location information. 
   * By default no location information is sent to the server.
   */
  public void setLocationInfo(boolean locationInfo) {
    this.locationInfo = locationInfo;
  }

  /**
   * Returns value of the <b>LocationInfo</b> option.
   */
  public boolean getLocationInfo() {
    return locationInfo;
  }

  /**
   * The <b>App</b> option takes a string value which should be the name of the 
   * application getting logged.
   * If property was already set (via system property), don't set here.
   */
  public void setApplication(String lapp) {
    this.application = lapp;
  }

  /**
   *  Returns value of the <b>Application</b> option.
   */
  public String getApplication() {
    return application;
  }

  /**
   * The <b>ReconnectionDelay</b> option takes a positive integer representing 
   * the number of milliseconds to wait between each failed connection attempt 
   * to the server. The default value of this option is 30000 which corresponds 
   * to 30 seconds. 
   * <p>
   * Setting this option to zero turns off reconnection capability.
   */
  public void setReconnectionDelay(int delay) {
    this.reconnectionDelay = delay;
  }

  /**
   * Returns value of the <b>ReconnectionDelay</b> option.
   */
  public int getReconnectionDelay() {
    return reconnectionDelay;
  }

  /**
   * The Connector will reconnect when the server becomes available again.  
   * It does this by attempting to open a new connection every 
   * <code>reconnectionDelay</code> milliseconds.
   * <p>
   * It stops trying whenever a connection is established. It will restart to 
   * try reconnect to the server when previpously open connection is droppped.
   * 
   * @author  Ceki G&uuml;lc&uuml;
   * @since 0.8.4
  */
  class Connector extends Thread {
    boolean interrupted = false;

    public void run() {
      Socket socket;

      while (!interrupted) {
        try {
          sleep(reconnectionDelay);
          getLogger().debug("Attempting connection to {}", address.getHostName());
          socket = new Socket(address, port);

          synchronized (this) {
            oos = new ObjectOutputStream(socket.getOutputStream());
            connector = null;
            getLogger().debug("Connection established. Exiting connector thread.");

            break;
          }
        } catch (InterruptedException e) {
          getLogger().debug("Connector interrupted. Leaving loop.");

          return;
        } catch (java.net.ConnectException e) {
          getLogger().debug(
            "Remote host " + address.getHostName() + " refused connection.");
        } catch (IOException e) {
          getLogger().debug(
            "Could not connect to {}. Exception is {}", address.getHostName(), e);
        }
      }

      //LogLog.debug("Exiting Connector.run() method.");
    }

    /**
       public
       void finalize() {
       LogLog.debug("Connector finalize() has been called.");
       }
    */
  }

    /**
     * Gets whether appender requires a layout.
     * @return false
     */
  public boolean requiresLayout() {
      return false;
  }

}
