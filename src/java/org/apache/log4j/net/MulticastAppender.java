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
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.helpers.Constants;
import org.apache.log4j.spi.LoggingEvent;


/**
 *  Multicast-based Appender.  Works in conjunction with the MulticastReceiver, which expects
 *  a LoggingEvent encoded using XMLLayout. 
 * 
 *  Sends log information as a multicast datagrams.
 *
 *  <p>Messages are not sent as LoggingEvent objects but as text after
 *  applying XMLLayout.
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
 * 
 */
public class MulticastAppender extends AppenderSkeleton implements PortBased {
  /**
     The default port number for the multicast packets. (9991).
  */
  static final int DEFAULT_PORT = 9991;
  
  /**
     We remember host name as String in addition to the resolved
     InetAddress so that it can be returned via getOption().
  */
  String hostname;
  String remoteHost;
  String application;
  String overrideProperties = "true";
  int timeToLive;
  InetAddress address;
  int port = DEFAULT_PORT;
  MulticastSocket outSocket;
  private String encoding;
  
  public MulticastAppender() {
     super(false);
  }

  /**
     Open the multicast sender for the <b>RemoteHost</b> and <b>Port</b>.
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

    if(remoteHost != null) {
      address = getAddressByName(remoteHost);
    } else {
      String err = "The RemoteHost property is required for SocketAppender named "+ name;
      getLogger().error(err);
      throw new IllegalStateException(err);
    }
    
    connect();
    super.activateOptions();
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
     Close the Socket and release the underlying
     connector thread if it has been created
   */
  public void cleanUp() {
    if (outSocket != null) {
      try {
        outSocket.close();
      } catch (Exception e) {
        getLogger().error("Could not close outSocket.", e);
      }

      outSocket = null;
    }
  }

  void connect() {
    if (this.address == null) {
      return;
    }

    try {
      // First, close the previous connection if any.
      cleanUp();
      outSocket = new MulticastSocket();
      outSocket.setTimeToLive(timeToLive);
    } catch (IOException e) {
      getLogger().error("Error in connect method of MulticastAppender named "+name, e);
    }
  }

  public void append(LoggingEvent event) {
    if (event == null) {
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
        StringBuffer buf = new StringBuffer(layout.format(event));

        byte[] payload;
        if(encoding == null) {
          payload = buf.toString().getBytes();
        } else {
          payload = buf.toString().getBytes(encoding);
        }

        DatagramPacket dp =
           new DatagramPacket(payload, payload.length, address, port);
        outSocket.send(dp);
        //remove these properties, in case other appenders need to set them to different values 
        event.setProperty(Constants.HOSTNAME_KEY, null);
        event.setProperty(Constants.APPLICATION_KEY, null);
      } catch (IOException e) {
        outSocket = null;
        getLogger().warn("Detected problem with Multicast connection: " + e);
      }
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
     The <b>RemoteHost</b> option takes a string value which should be
     the host name or ipaddress to send the multicast packets.
   */
  public void setRemoteHost(String host) {
    remoteHost = host;
  }

  /**
     Returns value of the <b>RemoteHost</b> option.
   */
  public String getRemoteHost() {
    return remoteHost;
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
     The <b>Time to live</b> option takes a positive integer representing
     the time to live value.
   */
  public void setTimeToLive(int timeToLive) {
    this.timeToLive = timeToLive;
  }

  /**
     Returns value of the <b>Time to Live</b> option.
   */
  public int getTimeToLive() {
    return timeToLive;
  }

  /**
     The <b>Port</b> option takes a positive integer representing
     the port where multicast packets will be sent.
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

  /* (non-Javadoc)
   * @see org.apache.log4j.net.NetworkBased#isActive()
   */
  public boolean isActive() {
    // TODO handle active/inactive
    return true;
  }

    /**
     * Gets whether appender requires a layout.
     * @return false
     */
  public boolean requiresLayout() {
      return true;
  }

}
