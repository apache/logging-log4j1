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

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

import java.io.IOException;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;


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
  
  private static final int PACKET_LENGTH = 16384;

  /**
     The default reconnection delay (30000 milliseconds or 30 seconds).
  */
  static final int DEFAULT_RECONNECTION_DELAY = 30000;

  /**
     We remember host name as String in addition to the resolved
     InetAddress so that it can be returned via getOption().
  */
  String localMachine;
  String remoteHost;
  String log4japp;
  String overrideProperties = "true";
  int timeToLive;
  InetAddress address;
  int port = DEFAULT_PORT;
  MulticastSocket outSocket;
  int reconnectionDelay = DEFAULT_RECONNECTION_DELAY;
  boolean locationInfo = false;
  int count = 0;
  
  public MulticastAppender() {
  }

  /**
     Sends multicast packets to the <code>address</code> and <code>port</code>.
  */
  public MulticastAppender(InetAddress address, int port) {
    this.address = address;
    this.remoteHost = address.getHostName();
    this.port = port;
    connect(address, port);
  }

  /**
     Sends multicast packets to the <code>address</code> and <code>port</code>.
  */
  public MulticastAppender(String host, int port) {
    this.port = port;
    this.address = getAddressByName(host);
    this.remoteHost = host;
    connect(address, port);
  }

  /**
     Open the multicast sender for the <b>RemoteHost</b> and <b>Port</b>.
  */
  public void activateOptions() {
    try {
      localMachine = InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException uhe) {
      try {
        localMachine = InetAddress.getLocalHost().getHostAddress();
      } catch (UnknownHostException uhe2) {
        localMachine = "unknown";
      }
    }

    //allow system property of log4japp to be primary
    if (log4japp == null) {
      log4japp = System.getProperty("log4japp");
    } else {
      if (System.getProperty("log4japp") != null) {
        log4japp = log4japp + "-" + System.getProperty("log4japp");
      }
    }

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
     Close the Socket and release the underlying
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
  }

  void connect(InetAddress address, int port) {
    if (this.address == null) {
      return;
    }

    try {
      // First, close the previous connection if any.
      cleanUp();
      outSocket = new MulticastSocket();
      outSocket.setTimeToLive(timeToLive);
    } catch (IOException e) {e.printStackTrace();
    }
  }

  public void append(LoggingEvent event) {
    if (event == null) {
      return;
    }

    if (address == null) {
      errorHandler.error(
        "No remote host is set for MulticastAppender named \"" + this.name + "\".");

      return;
    }

    if (outSocket != null) {
      //if the values already exist, don't set (useful when forwarding from a simplesocketserver
      if (
        (overrideProperties != null)
          && overrideProperties.equalsIgnoreCase("true")) {
        event.setProperty("log4jmachinename", localMachine);

        if (log4japp != null) {
          event.setProperty("log4japp", log4japp);
        }
      }

      try {
        StringBuffer buf=new StringBuffer(layout.format(event).trim());
        if (buf.length() < PACKET_LENGTH) {
          buf.append(new char[PACKET_LENGTH - buf.length()]);
        }
        DatagramPacket dp =
          new DatagramPacket(buf.toString().getBytes("ASCII"), buf.length(), address, port);
        outSocket.send(dp);
      } catch (IOException e) {
        outSocket = null;
        LogLog.warn("Detected problem with Multicast connection: " + e);
      }
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
     The MulticastAppender uses layouts (should be configured with XMLLayout to use MulticastReceiver.
      Hence, this method returns
     <code>true</code>.
  */
  public boolean requiresLayout() {
    return true;
  }

  /**
     The <b>RemoteHost</b> option takes a string value which should be
     the host name or ipaddress to send the multicast packets.
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
  public void setLog4JApp(String log4japp) {
    this.log4japp = log4japp;
  }

  /**
     Returns value of the <b>App</b> option.
   */
  public String getLog4JApp() {
    return log4japp;
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

  /* (non-Javadoc)
   * @see org.apache.log4j.net.NetworkBased#isActive()
   */
  public boolean isActive() {
    // TODO handle active/inactive
    return true;
  }
}
