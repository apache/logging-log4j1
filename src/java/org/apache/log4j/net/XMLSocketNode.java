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

import org.apache.log4j.*;
import org.apache.log4j.plugins.Receiver;
import org.apache.log4j.spi.*;

import java.io.IOException;
import java.io.InputStream;

import java.net.Socket;

import java.util.Iterator;
import java.util.Vector;


/**
   Read {@link LoggingEvent} objects sent from a remote client using XML over
   Sockets (TCP). These logging events are logged according to local
   policy, as if they were generated locally.

   <p>For example, the socket node might decide to log events to a
   local file and also resent them to a second socket node.

    @author  Scott Deboy <sdeboy@apache.org>;

    @since 0.8.4
*/
public class XMLSocketNode implements Runnable {
  static Logger logger = Logger.getLogger(SocketNode.class);
  Socket socket;
  LoggerRepository hierarchy;
  Receiver receiver;
  Decoder decoder;
  SocketNodeEventListener listener;

  /**
    Constructor for socket and logger repository. */
  public XMLSocketNode(
    String decoder, Socket socket, LoggerRepository hierarchy) {
    try {
      Class c = Class.forName(decoder);
      Object o = c.newInstance();

      if (o instanceof Decoder) {
        this.decoder = (Decoder) o;
      }
    } catch (ClassNotFoundException cnfe) {
    } catch (IllegalAccessException iae) {
    } catch (InstantiationException ie) {
    }

    this.socket = socket;
    this.hierarchy = hierarchy;
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
    } catch (IllegalAccessException iae) {
    } catch (InstantiationException ie) {
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
      logger.error(
        "Exception constructing XML Socket Receiver", listenerException);
    }

    try {
      is = socket.getInputStream();
    } catch (Exception e) {
      is = null;
      listenerException = e;
      logger.error("Exception opening ObjectInputStream to " + socket, e);
    }

    if (is != null) {
      String remoteInfo =
        socket.getInetAddress().getHostName() + ":" + socket.getPort();

      try {
            //read data from the socket
            //it's up to the individual decoder to handle incomplete event data
            while (true) {
                byte[] b=new byte[1024];
                is.read(b);
                Vector v= decoder.decodeEvents(new String(b).trim());

            if (v != null) {
              Iterator iter = v.iterator();

              while (iter.hasNext()) {
                LoggingEvent e = (LoggingEvent) iter.next();
                //if machinename property was not set (the case if properties
                //not supported by the DTD), use remoteinfo as machine name
                if (e.getProperty("log4jmachinename")==null) {
                	e.setProperty("log4jmachinename", remoteInfo);
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
                  remoteLogger = hierarchy.getLogger(e.getLoggerName());

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
        logger.info("Caught java.io.EOFException closing conneciton.");
        listenerException = e;
      } catch (java.net.SocketException e) {
        logger.info("Caught java.net.SocketException closing conneciton.");
        listenerException = e;
      } catch (IOException e) {
        logger.info("Caught java.io.IOException: " + e);
        logger.info("Closing connection.");
        listenerException = e;
      } catch (Exception e) {
        logger.error("Unexpected exception. Closing connecition.", e);
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
