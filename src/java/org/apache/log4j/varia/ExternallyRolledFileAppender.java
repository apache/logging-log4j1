/*
 * Copyright 1999,2005 The Apache Software Foundation.
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

package org.apache.log4j.varia;

import org.apache.log4j.RollingFileAppender;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


/**
   This appender listens on a socket on the port specified by the
   <b>Port</b> property for a "RollOver" message. When such a message
   is received, the underlying log file is rolled over and an
   acknowledgment message is sent back to the process initiating the
   roll over.

   <p>This method of triggering roll over has the advantage of being
   operating system independent, fast and reliable.

   <p>A simple application {@link Roller} is provided to initiate the
   roll over.

   <p>Note that the initiator is not authenticated. Anyone can trigger
   a rollover. In production environments, it is recommended that you
   add some form of protection to prevent undesired rollovers.


   @author Ceki G&uuml;lc&uuml;
   @author Curt Arnold
   @since version 0.9.0
   @deprecated version 1.3
 */
public final class ExternallyRolledFileAppender extends RollingFileAppender {
  /**
     The string constant sent to initiate a roll over.   Current value of
     this string constant is <b>RollOver</b>.
  */
  public static final String ROLL_OVER = "RollOver";


  /**
     The string constant sent to acknowledge a roll over.   Current value of
      this string constant is <b>OK</b>.
  */
  public static final String OK = "OK";
  private int port = 0;
  private HUP hup;



  /**
     The default constructor does nothing but calls its super-class
     constructor.  */
  public ExternallyRolledFileAppender() {
  }

  /**
     The <b>Port</b> [roperty is used for setting the port for
     listening to external roll over messages.
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
     Start listening on the port specified by a preceding call to
     {@link #setPort}.  */
  public void activateOptions() {
    super.activateOptions();
    if (port != 0) {
        hup =  new HUP(this, port);
        hup.setDaemon(true);
        hup.start();
    }
  }

     /**
       *  Close this appender instance. The underlying stream or writer is
       *  also closed.
       *
       *  <p>Closed appenders cannot be reused.
      */
      public void close() {
        HUP dying = null;
        synchronized(this) {
            dying = hup;
            hup = null;
        }
        if (dying != null) {
            dying.interrupt();
            try {
               new Socket(InetAddress.getLocalHost(), port);
               dying.join();
            } catch (Exception ex) {
            }
        }
        super.close();
      }



  private static class HUP extends Thread {
     private final ExternallyRolledFileAppender er;
     private final int port;
      /**
       * Use of loggers within this code is a deadlock waiting to happen.
       *
       */
//     private static final Logger logger = LogManager.getLogger("org.apache.log4j.varia.HUP");
  
     public HUP(final ExternallyRolledFileAppender er, int port) {
        this.er = er;
        this.port = port;
     }

     public void run() {
        ServerSocket serverSocket = null;
        IOException ioex = null;
        //
        //   try to establish connection for 10 attempts
        //     since configuration may have just been reset
        //     and the previous HUP hasn't released the socket.
        for(int i = 0; i < 10; i++) {
            try {
                serverSocket = new ServerSocket(port);
                break;
            } catch(IOException ex) {
                ioex = ex;
                try {
                    Thread.sleep(100);
                } catch(InterruptedException intex) {
                }
            }
        }
        if (serverSocket != null) {
           try {
               while (true) {
                  Socket socket = serverSocket.accept();
                   if (isInterrupted()) {
                       break;
                   }
    //              logger.debug("Connected to client at " + socket.getInetAddress());
                   DataInputStream dis = new DataInputStream(socket.getInputStream());
                   DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                   try {
                     String line = dis.readUTF();
    //               logger.debug("Got external roll over signal.");
                     if (ExternallyRolledFileAppender.ROLL_OVER.equals(line)) {
                        synchronized(er) {
                            er.rollOver();
                        }
                        dos.writeUTF(ExternallyRolledFileAppender.OK);
                     } else {
                        dos.writeUTF("Expecting [RollOver] string.");
                     }
                   } catch (IOException ex) {
                   }
               }
            } catch (Exception e) {
               e.printStackTrace();
            }
            try {
                serverSocket.close();
            } catch(IOException ex) {
            }
        } else {
            if (ioex != null) {
                ioex.printStackTrace();
            }
        }
     }
   }
}