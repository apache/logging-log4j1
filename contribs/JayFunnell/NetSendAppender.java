/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j.net;

import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.helpers.LogLog;

/**
  <p>The NetSendAppender is a log4j appender that uses the popular Windows NET SEND
     command to announce log events.  This is handy for situations where immediate
     notification is required for critical log events.  It's extremely unhandy for
     low-priority log messages because it will quickly annoy the user(s) receiving them :)

  <p>A JNI component is used to perform the actual network communication.  It must be
     in your path when using this appender.  The source code for this component was
     originally found
     <a href="http://www.experts-exchange.com/Programming/Programming_Languages/Java/Q_20286388.html">here</a>

  <p>Here is a list of the available configuration options:
  <p>
  <table border=1>
   <tr>
   <th>Name</th>
   <th>Requirement</th>
   <th>Description</th>
   <th>Sample Value</th>
   </tr>
   <tr>
   <td>computer</td>
   <td>required</td>
   <td>This parameter determines the destination computer for the messages.  This is the Windows machine name.</td>
   <td>sideshow-bob</td>
   </tr>
   <tr>
   <td>from</td>
   <td>required</td>
   <td>This parameter determines the name of the user sending each log message.  This is a Windows user name.</td>
   <td>mary</td>
   </tr>
   <tr>
   <td>to</td>
   <td>required</td>
   <td>This parameter determines which user will receive the log messages.  This is a Windows user name.</td>
   <td>fred</td>
   </tr>
   </table>

   <p>Here is a sample configuration.  It assigns a "netsend" appender to the emergency channel.  The destination computer
      is called "sideshow-bob" and the destination user is "fred".  Each time a message is logged to the emergency channel,
      fred is sent a copy to his machine, "sideshow-bob".
   <p>
   <pre>
     log4j.logger.emergency=DEBUG, netsend
     log4j.appender.netsend=org.apache.log4j.net.NetSendAppender
     log4j.appender.netsend.computer=sideshow-bob
     log4j.appender.netsend.to=fred
     log4j.appender.netsend.from=Emergency Monitor
     log4j.appender.netsend.layout=org.apache.log4j.PatternLayout
     log4j.appender.netsend.layout.ConversionPattern=%-5p %c{1} - %m%n
   </pre>

   <p>
   A common problem with NET SENDs is that the recipient can receive duplicates messages.  Note that this is because
   the message is sent on every available transport layer.  So if you have TCP/IP and IPX enabled, each will
   be used to send the message to the user.  This is not a problem with the NetSendAppender.

   @author <a HREF="mailto:jay-funnell@shaw.ca">Jay Funnell</a>

*/

public class NetSendAppender extends AppenderSkeleton {

  static boolean libraryLoaded = false;

  String computer = null;
  String toUser = null;
  String fromUser = null;
  boolean activated = false;

  /** Default constructor */
  public NetSendAppender() {
    if (!libraryLoaded) {
      try {
        System.loadLibrary("NetSendAppender");
        libraryLoaded = true;
      } catch (UnsatisfiedLinkError err) {
        LogLog.warn("problem loading NetSendAppender.dll:", err);
      } catch (SecurityException ex) {
        LogLog.warn("problem loading NetSendAppender.dll:", ex);
      }
    }
  }

  /** This appender requires a layout to format the text to the
      attached client(s). */
  public boolean requiresLayout() {
    return true;
  }

  /** Validates the configuration settings. */
  public void activateOptions() {

    activated = true;
    if (computer == null) {
       LogLog.error("'computer' setting not found.  You must provide a destination computer when configuring this appender.");
       activated = false;
    }
    if (toUser == null) {
       LogLog.error("'to' setting not found.  You must provide a destination user when configuring this appender.");
       activated = false;
    }
    if (fromUser == null) {
       LogLog.error("'from' setting not found.  You must provide a source user when configuring this appender.");
       activated = false;
    }

  }

  /** Assigns the name of the computer that will receive log messages. */
  public void setComputer(String computer) {
    this.computer = computer;
  }

  /** Retrieves the name of the computer that will receive log messages. */
  public String getComputer() {
    return computer;
  }

  /** Assigns the name of the user that will send the log messages. */
  public void setFrom(String fromUser) {
    this.fromUser = fromUser;
  }

  /** Retrieves the name of the user that will send the log messages. */
  public String getFrom() {
    return fromUser;
  }

  /** Assigns the name of the user that will receive log messages. */
  public void setTo(String toUser) {
    this.toUser = toUser;
  }

  /** Retrieves the name of the user that will receive log messages. */
  public String getTo() {
    return toUser;
  }

  /** Shuts down the appender. */
  public void close() {
  }

  /** Handles a log event.  For this appender, that means writing the
      message to a JNI component that can handle the NET SEND.  */
  protected void append(LoggingEvent event) {
    if (libraryLoaded && activated) {
      sendMessage(computer, fromUser, toUser, this.layout.format(event));
      if(layout.ignoresThrowable()) {
        String[] s = event.getThrowableStrRep();
        if (s != null) {
          int len = s.length;
          for(int i = 0; i < len; i++) {
            sendMessage(computer, fromUser, toUser, s[i]);
          }
        }
      }
    }
  }

  /** This method is provided in NetSendAppender.dll and performs the actual network calls. */
  public native boolean sendMessage(String computer, String from, String to, String message);

}
