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

import org.apache.log4j.Logger;
import org.apache.log4j.plugins.Pauseable;
import org.apache.log4j.plugins.Receiver;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.LoggingEvent;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import java.net.Socket;

import java.util.EventListener;

import javax.swing.event.EventListenerList;


// Contributors:  Moses Hohman <mmhohman@rainbow.uchicago.edu>

/**
   Read {@link LoggingEvent} objects sent from a remote client using
   Sockets (TCP). These logging events are logged according to local
   policy, as if they were generated locally.

   <p>For example, the socket node might decide to log events to a
   local file and also resent them to a second socket node.

    @author  Ceki G&uuml;lc&uuml;
    @author  Paul Smith <psmith@apache.org>

    @since 0.8.4
*/
public class SocketNode implements Runnable, Pauseable {
  static Logger logger = Logger.getLogger(SocketNode.class);
  private boolean paused;
  private Socket socket;
  private LoggerRepository hierarchy;
  private Receiver receiver;
  private SocketNodeEventListener listener;
  private EventListenerList listenerList = new EventListenerList();

  /**
    Constructor for socket and logger repository. */
  public SocketNode(Socket socket, LoggerRepository hierarchy) {
    this.socket = socket;
    this.hierarchy = hierarchy;
  }

  /**
    Constructor for socket and reciever. */
  public SocketNode(Socket socket, Receiver receiver) {
    this.socket = socket;
    this.receiver = receiver;
  }

  /**
   * Set the event listener on this node.
   *
   * @deprecated Now supports mutliple listeners, this method
   * simply invokes the removeSocketNodeEventListener() to remove
   * the listener, and then readds it.
   */
  public void setListener(SocketNodeEventListener _listener) {
    removeSocketNodeEventListener(_listener);
    addSocketNodeEventListener(_listener);
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

  public void run() {
    LoggingEvent event;
    Logger remoteLogger;
    Exception listenerException = null;
    ObjectInputStream ois = null;

    try {
      ois =
        new ObjectInputStream(
          new BufferedInputStream(socket.getInputStream()));
    } catch (Exception e) {
      ois = null;
      listenerException = e;
      logger.error("Exception opening ObjectInputStream to " + socket, e);
    }

    if (ois != null) {
      String remoteInfo =
        socket.getInetAddress().getHostName() + ":" + socket.getPort();

      /**
       * notify the listener that the socket has been
       * opened and this SocketNode is ready and waiting
       */
      fireSocketOpened(remoteInfo);

      try {
        while (true) {
          // read an event from the wire
          event = (LoggingEvent) ois.readObject();

          // store the known remote info in an event property
          event.setProperty("log4j.remoteSourceInfo", remoteInfo);

          // if configured with a receiver, tell it to post the event
          if (!isPaused()) {
            if ((receiver != null)) {
              receiver.doPost(event);

              // else post it via the hierarchy
            } else {
              // get a logger from the hierarchy. The name of the logger
              // is taken to be the name contained in the event.
              remoteLogger = hierarchy.getLogger(event.getLoggerName());

              //event.logger = remoteLogger;
              // apply the logger-level filter
              if (event
                .getLevel()
                .isGreaterOrEqual(remoteLogger.getEffectiveLevel())) {
                // finally log the event as if was generated locally
                remoteLogger.callAppenders(event);
              }
            }
          } else {
            //we simply discard this event.
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
      if (ois != null) {
        ois.close();
      }
    } catch (Exception e) {
      //logger.info("Could not close connection.", e);
    }

    // send event to listener, if configured
    if (listener != null) {
      fireSocketClosedEvent(listenerException);
    }
  }

  /**
   * Notifies all registered listeners regarding the closing of the Socket
   * @param listenerException
   */
  private void fireSocketClosedEvent(Exception listenerException) {
    EventListener[] listeners =
      listenerList.getListeners(SocketNodeEventListener.class);

    for (int i = 0; i < listeners.length; i++) {
      SocketNodeEventListener snel = (SocketNodeEventListener) listeners[i];

      if (snel != null) {
        snel.socketClosedEvent(listenerException);
      }
    }
  }

  /**
   * Notifies all registered listeners regarding the opening of a Socket
   * @param remoteInfo
   */
  private void fireSocketOpened(String remoteInfo) {
    EventListener[] listeners =
      listenerList.getListeners(SocketNodeEventListener.class);

    for (int i = 0; i < listeners.length; i++) {
      SocketNodeEventListener snel = (SocketNodeEventListener) listeners[i];

      if (snel != null) {
        snel.socketOpened(remoteInfo);
      }
    }
  }

  public void setPaused(boolean paused) {
    this.paused = paused;
  }

  public boolean isPaused() {
    return this.paused;
  }
}
