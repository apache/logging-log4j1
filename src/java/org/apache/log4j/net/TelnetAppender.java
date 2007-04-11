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

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
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
     * Creates a TelnetAppender.
     */
  public TelnetAppender() {
      super(false);
  }

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
    } catch (IOException e) {
      getLogger().error("Could not active TelnetAppender options for TelnetAppender named "+getName(), e);
      throw new IllegalStateException("Could not create a SocketHandler for TelnetAppender named "+getName());
    }
    super.activateOptions();
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  /** shuts down the appender. */
  public void close() {
    if (sh != null) {
      sh.close();
      try {
        sh.join();
      } catch (InterruptedException e) {
      }
    }
  }

  /** Handles a log event.  For this appender, that means writing the
    message to each connected client.  */
  protected void append(LoggingEvent event) {
    if(sh == null || !sh.hasConnections()) {
      return;
    }

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
    private Vector writers = new Vector();
    private Vector connections = new Vector();
    private ServerSocket serverSocket;
    private int MAX_CONNECTIONS = 20;
    
    private String encoding = "UTF-8";
    
    public SocketHandler(int port) throws IOException {
      serverSocket = new ServerSocket(port);
      setName("TelnetAppender-" + getName() + "-" + port);
    }

    public void finalize() {
      close();
    }
    
    /** make sure we close all network connections when this handler is destroyed. */
    public void close() {
      for (Enumeration e = connections.elements(); e.hasMoreElements();) {
        try {
          ((Socket) e.nextElement()).close();
        } catch (IOException ex) {
        }
      }
      
      interrupt();
      try {
        serverSocket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }      
    }

    /** sends a message to each of the clients in telnet-friendly output. */
    public void send(String message) {
      boolean hasWriterError = false;
      for (Enumeration e = writers.elements(); e.hasMoreElements();) {
        PrintWriter writer = (PrintWriter) e.nextElement();
        writer.print(message);
        hasWriterError |= writer.checkError();

      }
      //
      //   if any writer had an error then
      //       check all writers and remove any bad ones
      if(hasWriterError) {
         for (int i = writers.size() - 1; i >= 0; i--) {
            if (((PrintWriter) writers.elementAt(i)).checkError()) {
                writers.remove(i);
                connections.remove(i);
            }
         }
      }
    }

    /**
        Continually accepts client connections.  Client connections
        are refused when MAX_CONNECTIONS is reached.
    */
    public void run() {
      while (!Thread.interrupted()) {
        try {
          Socket newClient = serverSocket.accept();

          // Bugzilla 26117: use an encoding to support EBCDIC machines
          // Could make encoding a JavaBean property or even make TelnetAppender
          // extend WriterAppender, which already has encoding support.
          PrintWriter pw = new PrintWriter(new OutputStreamWriter(newClient.getOutputStream(), encoding));

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
          if (!Thread.interrupted())
            getLogger().error("Encountered error while in SocketHandler loop.", e);
          break;
        }
      }

      try {
        serverSocket.close();
      } catch (IOException ex) {
      }

    }
    
    /**
     *  Determines if socket hander has any active connections.
     *  @return true if any active connections.
     */
    public boolean hasConnections() {
       return connections.size() > 0;
    }
  }
}
