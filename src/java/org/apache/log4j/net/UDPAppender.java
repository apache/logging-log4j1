/*
 * Copyright 1999,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.helpers.*;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;


/**
 * 
 * 
 *  Sends log information as a UDP datagrams.
 *
 *  <p>The UDPAppender is meant to be used as a diagnostic logging tool
 *  so that logging can be monitored by a simple UDP client.
 *
 *  <p>Messages are not sent as LoggingEvent objects but as text after
 *  applying the designated Layout.
 *
 *  <p>The port and remoteHost properties can be set in configuration properties.
 *  By setting the remoteHost to a broadcast address any number of clients can
 *  listen for log messages.
 *
 *  <p>This was inspired and really extended/copied from {@link SocketAppender}.  Please
 *  see the docs for the proper credit to the authors of that class.
 *
 *  @author  <a href="mailto:kbrown@versatilesolutions.com">Kevin Brown</a>
 *  @author Scott Deboy <sdeboy@apache.org>
 */
public class UDPAppender extends AppenderSkeleton implements PortBased{
  /**
     The default port number for the UDP packets. (9991).
  */
  public static final int DEFAULT_PORT = 9991;

  private static final int PACKET_LENGTH = 16384;

  /**
     The default reconnection delay (30000 milliseconds or 30 seconds).
  */
  static final int DEFAULT_RECONNECTION_DELAY = 30000;

  /**
     We remember host name as String in addition to the resolved
     InetAddress so that it can be returned via getOption().
  */
  String hostname;
  String remoteHost;
  String application;
  String encoding;
  String overrideProperties = "true";
  InetAddress address;
  int port = DEFAULT_PORT;
  DatagramSocket outSocket;
  int reconnectionDelay = DEFAULT_RECONNECTION_DELAY;
  int count = 0;
  private Connector connector;

  public UDPAppender() {
  }

  /**
     Sends UDP packets to the <code>address</code> and <code>port</code>.
  */
  public UDPAppender(InetAddress address, int port) {
    this.address = address;
    this.remoteHost = address.getHostName();
    this.port = port;
    connect(address, port);
  }

  /**
     Sends UDP packets to the <code>address</code> and <code>port</code>.
  */
  public UDPAppender(String host, int port) {
    this.port = port;
    this.address = getAddressByName(host);
    this.remoteHost = host;
    connect(address, port);
  }

  /**
     Open the UDP sender for the <b>RemoteHost</b> and <b>Port</b>.
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

    //allow system property of application to be primary
    if (application == null) {
      application = System.getProperty(Constants.APPLICATION_KEY);
    } else {
      if (System.getProperty(Constants.APPLICATION_KEY) != null) {
        application = application + "-" + System.getProperty(Constants.APPLICATION_KEY);
      }
    }

    //if not passed in, allow null app (app property won't be set)
    connect(address, port);
  }

  /**
     Close this appender.
     <p>This will mark the appender as closed and
     call then {@link #cleanUp} method.
  */
  public synchronized void close() {
    if (closed) {
      return;
    }

    this.closed = true;
    cleanUp();
  }

  /**
     Close the UDP Socket and release the underlying
     connector thread if it has been created
   */
  public void cleanUp() {
    if (outSocket != null) {
      try {
        outSocket.close();
      } catch (Exception e) {
        LogLog.error("Could not close outSocket.", e);
      }

      outSocket = null;
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
      outSocket = new DatagramSocket();
      outSocket.connect(address, port);
    } catch (IOException e) {
      LogLog.error(
        "Could not open UDP Socket for sending. We will try again later.", e);
      fireConnector();
    }
  }

  public void append(LoggingEvent event) {
    if (event == null) {
      return;
    }

    if (address == null) {
      errorHandler.error(
        "No remote host is set for UDPAppender named \"" + this.name + "\".");

      return;
    }

    if (outSocket != null) {
      //if the values already exist, don't set (useful when forwarding from a simplesocketserver
      if (
        (overrideProperties != null)
          && overrideProperties.equalsIgnoreCase("true")) {
        event.setProperty(Constants.HOSTNAME_KEY, hostname);

        if (application != null) {
          event.setProperty(Constants.APPLICATION_KEY, application);
        }
      }

      try {
        StringBuffer buf=new StringBuffer(layout.format(event).trim());
        if (buf.length() < PACKET_LENGTH) {        
           buf.append(new char[PACKET_LENGTH - buf.length()]);
        }
        //the implementation of string.getBytes accepts a null encoding and uses the system charset
        DatagramPacket dp =
           new DatagramPacket(buf.toString().getBytes(encoding), buf.length(), address, port);
        outSocket.send(dp);
        //remove these properties, in case other appenders need to set them to different values 
        event.setProperty(Constants.HOSTNAME_KEY, null);
        event.setProperty(Constants.APPLICATION_KEY, null);
      } catch (IOException e) {
        outSocket = null;
        LogLog.warn("Detected problem with UDP connection: " + e);

        if (reconnectionDelay > 0) {
          fireConnector();
        }
      }
    }
  }

  void fireConnector() {
    if (connector == null) {
      LogLog.debug("Starting a new connector thread.");
      connector = new Connector();
      connector.setDaemon(true);
      connector.setPriority(Thread.MIN_PRIORITY);
      connector.start();
    }
  }

  static InetAddress getAddressByName(String host) {
    try {
      return InetAddress.getByName(host);
    } catch (Exception e) {
      LogLog.error("Could not find address of [" + host + "].", e);

      return null;
    }
  }

  /**
     The UDPAppender uses layouts. Hence, this method returns
     <code>true</code>.
  */
  public boolean requiresLayout() {
    return true;
  }

  /**
     The <b>RemoteHost</b> option takes a string value which should be
     the host name or ipaddress to send the UDP packets.
   */
  public void setRemoteHost(String host) {
    address = getAddressByName(host);
    remoteHost = host;
  }

  /**
     Returns value of the <b>RemoteHost</b> option.
   */
  public String getRemoteHost() {
    return remoteHost;
  }

  /**
     The <b>App</b> option takes a string value which should be the name of the application getting logged.
     If property was already set (via system property), don't set here.
   */
  public void setApplication(String app) {
    this.application = app;
  }

  /**
     Returns value of the <b>App</b> option.
   */
  public String getApplication() {
    return application;
  }

  /**
     The <b>Encoding</b> option specifies how the bytes are encoded.  If this option is not specified, 
     the System encoding is used.
   */
  public void setEncoding(String encoding) {
    this.encoding = encoding;
  }

  /**
     Returns value of the <b>Encoding</b> option.
   */
  public String getEncoding() {
    return encoding;
  }

  /**
     The <b>OverrideProperties</b> option allows configurations where the appender does not apply
     the machinename/appname properties - the properties will be used as provided.
   */
  public void setOverrideProperties(String overrideProperties) {
    this.overrideProperties = overrideProperties;
  }

  /**
     Returns value of the <b>OverrideProperties</b> option.
   */
  public String getOverrideProperties() {
    return overrideProperties;
  }

    /**
     The <b>Port</b> option takes a positive integer representing
     the port where UDP packets will be sent.
   */
  public void setPort(int port) {
    this.port = port;
  }

  /**
     Returns value of the <b>Port</b> option.
   */
  public int getPort() {
    return port;
  }

  /**
     The <b>ReconnectionDelay</b> option takes a positive integer
     representing the number of milliseconds to wait between each
     failed attempt to establish an outgoing socket. The default value of
     this option is 30000 which corresponds to 30 seconds.

     <p>Setting this option to zero turns off reconnection
     capability.
   */
  public void setReconnectionDelay(int delay) {
    this.reconnectionDelay = delay;
  }

  /**
     Returns value of the <b>ReconnectionDelay</b> option.
   */
  public int getReconnectionDelay() {
    return reconnectionDelay;
  }

  /**
     The Connector will retry the UDP socket.
     It does this by attempting to open a new UDP socket every
     <code>reconnectionDelay</code> milliseconds.

     <p>It stops trying whenever a connection is established. It will
     restart to try reconnect to the server when previpously open
     connection is droppped.

     @author  Ceki G&uuml;lc&uuml;
     @since 0.8.4
  */
  class Connector extends Thread {
    boolean interrupted = false;

    public void run() {
      DatagramSocket socket;

      while (!interrupted) {
        try {
          sleep(reconnectionDelay);
          LogLog.debug("Attempting to establish UDP Datagram Socket");
          socket = new DatagramSocket();

          synchronized (this) {
            outSocket = socket;
            connector = null;

            break;
          }
        } catch (InterruptedException e) {
          LogLog.debug("Connector interrupted. Leaving loop.");

          return;
        } catch (IOException e) {
          LogLog.debug("Could not establish an outgoing MulticastSocket." + e);
        }
      }

      //LogLog.debug("Exiting Connector.run() method.");
    }
  }

  /* (non-Javadoc)
   * @see org.apache.log4j.net.NetworkBased#isActive()
   */
  public boolean isActive() {
    // TODO handle active/inactive
    return true;
  }
}
