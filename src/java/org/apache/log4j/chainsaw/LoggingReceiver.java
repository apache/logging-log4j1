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

package org.apache.log4j.chainsaw;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;


/**
 * A daemon thread the processes connections from a
 * <code>org.apache.log4j.net.SocketAppender.html</code>.
 *
 * @author <a href="mailto:oliver@puppycrawl.com">Oliver Burn</a>
 * @deprecated Chainsaw now uses the Receiver architecture to receive
 *   remote logging events from various sources.
 */
class LoggingReceiver extends Thread {
  /** used to log messages **/
  private static final Logger LOG = Logger.getLogger(LoggingReceiver.class);

  /** where to put the events **/
  private final EventDetailSink mEventSink;

  /** server for listening for connections **/
  private final ServerSocket mSvrSock;

  /**
   * Creates a new <code>LoggingReceiver</code> instance.
   *
   * @param aEventSink eventSink to place put received into
   * @param aPort port to listen on
   * @throws IOException if an error occurs
   */
  LoggingReceiver(EventDetailSink aEventSink, int aPort)
    throws IOException {
    setDaemon(true);
    this.mEventSink = aEventSink;
    mSvrSock = new ServerSocket(aPort);
  }

  /** Listens for client connections **/
  public void run() {
    LOG.info("Thread started");

    try {
      while (true) {
        LOG.debug("Waiting for a connection");

        final Socket client = mSvrSock.accept();
        LOG.debug(
          "Got a connection from " + client.getInetAddress().getHostName());

        final Thread t = new Thread(new Slurper(client));
        t.setDaemon(true);
        t.start();
      }
    } catch (IOException e) {
      LOG.error("Error in accepting connections, stopping.", e);
    }
  }

  /**
   * Helper that actually processes a client connection. It receives events
   * and adds them to the supplied model.
   *
   * @author <a href="mailto:oliver@puppycrawl.com">Oliver Burn</a>
   */
  private class Slurper implements Runnable {
    /** socket connection to read events from **/
    private final Socket mClient;

    /**
     * Creates a new <code>Slurper</code> instance.
     *
     * @param aClient socket to receive events from
     */
    Slurper(Socket aClient) {
      mClient = aClient;
    }

    /** loops getting the events **/
    public void run() {
      LOG.debug("Starting to get data");

      try {
        final ObjectInputStream ois =
          new ObjectInputStream(mClient.getInputStream());

        while (true) {
          final LoggingEvent event = (LoggingEvent) ois.readObject();
          mEventSink.addEvent(new EventDetails(event));
        }
      } catch (EOFException e) {
        LOG.info("Reached EOF, closing connection");
      } catch (SocketException e) {
        LOG.info("Caught SocketException, closing connection");
      } catch (IOException e) {
        LOG.warn("Got IOException, closing connection", e);
      } catch (ClassNotFoundException e) {
        LOG.warn("Got ClassNotFoundException, closing connection", e);
      }

      try {
        mClient.close();
      } catch (IOException e) {
        LOG.warn("Error closing connection", e);
      }
    }
  }
}
