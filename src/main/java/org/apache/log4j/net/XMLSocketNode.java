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

import org.apache.log4j.*;
import org.apache.log4j.helpers.Constants;
import org.apache.log4j.plugins.Receiver;
import org.apache.log4j.spi.*;

import java.io.IOException;
import java.io.InputStream;

import java.net.Socket;

import java.util.Iterator;
import java.util.List;


/**
   Read {@link LoggingEvent} objects sent from a remote client using XML over
   Sockets (TCP). These logging events are logged according to local
   policy, as if they were generated locally.

   <p>For example, the socket node might decide to log events to a
   local file and also resent them to a second socket node.

    @author  Scott Deboy <sdeboy@apache.org>;

    @since 0.8.4
*/
public class XMLSocketNode extends ComponentBase implements Runnable {
  Socket socket;
  Receiver receiver;
  Decoder decoder;
  SocketNodeEventListener listener;

  /**
    Constructor for socket and logger repository. */
  public XMLSocketNode(
    String decoder, Socket socket, LoggerRepository hierarchy) {
    this.repository = hierarchy;
    try {
      Class c = Class.forName(decoder);
      Object o = c.newInstance();

      if (o instanceof Decoder) {
        this.decoder = (Decoder) o;
      }
    } catch (ClassNotFoundException cnfe) {
      getLogger().warn("Unable to find decoder", cnfe);
    } catch (IllegalAccessException iae) {
      getLogger().warn("Unable to construct decoder", iae);
    } catch (InstantiationException ie) {
      getLogger().warn("Unable to construct decoder", ie);
    }

    this.socket = socket;
  }

  /**
    Constructor for socket and reciever. */
  public XMLSocketNode(String decoder, Socket socket, Receiver receiver) {
    try {
      Class c = Class.forName(decoder);
      Object o = c.newInstance();

      if (o instanceof Decoder) {
        this.decoder = (Decoder) o;
      }
    } catch (ClassNotFoundException cnfe) {
      getLogger().warn("Unable to find decoder", cnfe);
    } catch (IllegalAccessException iae) {
      getLogger().warn("Unable to construct decoder", iae);
    } catch (InstantiationException ie) {
      getLogger().warn("Unable to construct decoder", ie);
    }

    this.socket = socket;
    this.receiver = receiver;
  }

  /**
    Set the event listener on this node. */
  public void setListener(SocketNodeEventListener _listener) {
    listener = _listener;
  }

  public void run() {
    Logger remoteLogger;
    Exception listenerException = null;
    InputStream is = null;

    if ((this.receiver == null) || (this.decoder == null)) {
      is = null;
      listenerException =
        new Exception(
          "No receiver or decoder provided.  Cannot process xml socket events");
      getLogger().error(
        "Exception constructing XML Socket Receiver", listenerException);
    }

    try {
      is = socket.getInputStream();
    } catch (Exception e) {
      is = null;
      listenerException = e;
      getLogger().error("Exception opening ObjectInputStream to " + socket, e);
    }

    if (is != null) {
      String remoteInfo =
        socket.getInetAddress().getHostName() + ":" + socket.getPort();

      try {
        //read data from the socket
        //it's up to the individual decoder to handle incomplete event data
        while (true) {
          byte[] b = new byte[1024];
          int length = is.read(b);
          if (length == -1) {
            getLogger().info(
              "no bytes read from stream - closing connection.");
            break;
          }
          List v = decoder.decodeEvents(new String(b, 0, length));

          if (v != null) {
            Iterator iter = v.iterator();

            while (iter.hasNext()) {
              LoggingEvent e = (LoggingEvent) iter.next();

              //if machinename property was not set (the case if properties
              //not supported by the DTD), use remoteinfo as machine name
              if (e.getProperty(Constants.HOSTNAME_KEY) == null) {
                e.setProperty(Constants.HOSTNAME_KEY, remoteInfo);
              }

              // store the known remote info in an event property
              e.setProperty("log4j.remoteSourceInfo", remoteInfo);

              // if configured with a receiver, tell it to post the event
              if (receiver != null) {
                receiver.doPost(e);

                // else post it via the hierarchy
              } else {
                // get a logger from the hierarchy. The name of the logger
                // is taken to be the name contained in the event.
                remoteLogger = repository.getLogger(e.getLoggerName());

                //event.logger = remoteLogger;
                // apply the logger-level filter
                if (
                  e.getLevel().isGreaterOrEqual(
                      remoteLogger.getEffectiveLevel())) {
                  // finally log the event as if was generated locally
                  remoteLogger.callAppenders(e);
                }
              }
            }
          }
        }
      } catch (java.io.EOFException e) {
        getLogger().info("Caught java.io.EOFException closing connection.");
        listenerException = e;
      } catch (java.net.SocketException e) {
        getLogger().info(
          "Caught java.net.SocketException closing connection.");
        listenerException = e;
      } catch (IOException e) {
        getLogger().info("Caught java.io.IOException: " + e);
        getLogger().info("Closing connection.");
        listenerException = e;
      } catch (Exception e) {
        getLogger().error("Unexpected exception. Closing connection.", e);
        listenerException = e;
      }
    }

    // close the socket
    try {
      if (is != null) {
        is.close();
      }
    } catch (Exception e) {
      //logger.info("Could not close connection.", e);
    }

    // send event to listener, if configured
    if (listener != null) {
      listener.socketClosedEvent(listenerException);
    }
  }
}
