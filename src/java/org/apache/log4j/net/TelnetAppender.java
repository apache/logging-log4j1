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
import org.apache.log4j.Layout;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

import java.io.*;

import java.net.*;

import java.util.*;


/**
  <p>The TelnetAppender is a log4j appender that specializes in
  writing to a read-only socket.  The output is provided in a
  telnet-friendly way so that a log can be monitored over TCP/IP.
  Clients using telnet connect to the socket and receive log data.
  This is handy for remote monitoring, especially when monitoring a
  servlet.

  <p>Here is a list of the available configuration options:

  <table border=1>
   <tr>
   <th>Name</th>
   <th>Requirement</th>
   <th>Description</th>
   <th>Sample Value</th>
   </tr>

   <tr>
   <td>Port</td>
   <td>optional</td>
   <td>This parameter determines the port to use for announcing log events.  The default port is 23 (telnet).</td>
   <td>5875</td>
   </table>

   @author <a HREF="mailto:jay@v-wave.com">Jay Funnell</a>
*/
public class TelnetAppender extends AppenderSkeleton {
  private SocketHandler sh;
  private int port = 23;

  /**
      This appender requires a layout to format the text to the
      attached client(s). */
  public boolean requiresLayout() {
    return true;
  }

  /** all of the options have been set, create the socket handler and
      wait for connections. */
  public void activateOptions() {
    try {
      sh = new SocketHandler(port);
      sh.start();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  /** shuts down the appender. */
  public void close() {
    sh.finalize();
  }

  /** Handles a log event.  For this appender, that means writing the
    message to each connected client.  */
  protected void append(LoggingEvent event) {
    sh.send(this.layout.format(event));

    if (layout.ignoresThrowable()) {
      String[] s = event.getThrowableStrRep();

      if (s != null) {
        int len = s.length;

        for (int i = 0; i < len; i++) {
          sh.send(s[i]);
          sh.send(Layout.LINE_SEP);
        }
      }
    }
  }

  //---------------------------------------------------------- SocketHandler:

  /** The SocketHandler class is used to accept connections from
      clients.  It is threaded so that clients can connect/disconnect
      asynchronously. */
  protected class SocketHandler extends Thread {
    private boolean done = false;
    private Vector writers = new Vector();
    private Vector connections = new Vector();
    private ServerSocket serverSocket;
    private int MAX_CONNECTIONS = 20;

    public SocketHandler(int port) throws IOException {
      serverSocket = new ServerSocket(port);
    }

    /** make sure we close all network connections when this handler is destroyed. */
    public void finalize() {
      for (Enumeration e = connections.elements(); e.hasMoreElements();) {
        try {
          ((Socket) e.nextElement()).close();
        } catch (Exception ex) {
        }
      }

      try {
        serverSocket.close();
      } catch (Exception ex) {
      }

      done = true;
    }

    /** sends a message to each of the clients in telnet-friendly output. */
    public void send(String message) {
      Enumeration ce = connections.elements();

      for (Enumeration e = writers.elements(); e.hasMoreElements();) {
        Socket sock = (Socket) ce.nextElement();
        PrintWriter writer = (PrintWriter) e.nextElement();
        writer.print(message);

        if (writer.checkError()) {
          // The client has closed the connection, remove it from our list:
          connections.remove(sock);
          writers.remove(writer);
        }
      }
    }

    /**
        Continually accepts client connections.  Client connections
        are refused when MAX_CONNECTIONS is reached.
    */
    public void run() {
      while (!done) {
        try {
          Socket newClient = serverSocket.accept();
          PrintWriter pw = new PrintWriter(newClient.getOutputStream());

          if (connections.size() < MAX_CONNECTIONS) {
            connections.addElement(newClient);
            writers.addElement(pw);
            pw.print(
              "TelnetAppender v1.0 (" + connections.size()
              + " active connections)\r\n\r\n");
            pw.flush();
          } else {
            pw.print("Too many connections.\r\n");
            pw.flush();
            newClient.close();
          }
        } catch (Exception e) {
          LogLog.error("Encountered error while in SocketHandler loop.", e);
        }
      }
    }
  }
}
