/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.APL file.
 */

package org.apache.log4j.varia;

import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.helpers.LogLog;

/**
   This appender listens on a socket on the port specified by the
   {@link #PORT_OPTION} for a "RollOver" message. When such a message
   is received, the underlying log file is rolled over and an 
   acknowledgement message is sent back to the process initiating
   the roll over.

   <p>This method of triggering roll over has the advantage of being
   operating system independent, fast and reliable.
   
   <p>A simple application {@link Roller} is provided to initiate the
   roll over.

   <p>Note that the intiator is not authenticated. Anyone can trigger
   a rollover. In production environments, it is recommended that you
   add some form of protection to prevent undesired rollovers.


   @author Ceki G&uuml;lc&uuml;
   @since version 0.9.0 */
public class ExternallyRolledFileAppender extends RollingFileAppender {

  /**
     A string constant used in naming the option for setting the port
     for listening to external roll over messages. Current value of
     this string constant is <b>Port</b>.  

     <p>All option keys are case sensitive. 
      
     @deprecated Options are now handled using the JavaBeans paradigm.
     This constant is not longer needed and will be removed in the
     <em>near</em> term.
   */
  static final public String PORT_OPTION = "Port";

  /**
     The string constant sent to initiate a roll over.   Current value of
     this string constant is <b>RollOver</b>.  
  */
  static final public String ROLL_OVER = "RollOver";

  /**
     The string constant sent to acknowledge a roll over.   Current value of
      this string constant is <b>OK</b>.
  */
  static final public String OK = "OK";

  int port = 0;
  HUP hup;

  /**
     The default constructor does nothing but calls its super-class
     constructor.  */
  public
  ExternallyRolledFileAppender() { 
  }
  
  /**
     Returns the option names for this component, namely {@link
     #PORT_OPTION} in addition to the options of its super class {@link
     RollingFileAppender#getOptionStrings FileAppender}. 
     
     @deprecated We now use JavaBeans introspection to configure
     components. Options strings are no longer needed.
 */
  public
  String[] getOptionStrings() {

    return OptionConverter.concatanateArrays(super.getOptionStrings(),
		 new String[] {PORT_OPTION});
  }

  /**
     Set ExternallyRolledFileAppender specific options.

     In addition to {@link org.apache.log4j.FileAppender#setOption FileAppender
     options} and {@link RollingFileAppender#setOption RollingFileAppender
     options}, ExternallyRolledFileAppender recognizes the option
     <b>Port</b>.

     <p>The <b>Port</b> option is used for setting the port for 
     listening to external roll over messages.

     @deprecated Use the setter method for the option directly instead
     of the generic <code>setOption</code> method. 
   */
  public
  void setOption(String option, String value) {
    super.setOption(option, value);    
    if(option.equalsIgnoreCase(PORT_OPTION)) {
      port = OptionConverter.toInt(value, port);
      LogLog.debug("Port option set to "+port); 
    }
  }
  
  /**
     The <b>Port</b> option is used for setting the port for 
     listening to external roll over messages.
   */
  public
  void setPort(int port) {
    this.port = port;
  }
  
  /**
     Returns value of the <b>Port</b> option.
   */
  public
  int getPort() {
    return port;
  }
  
  /**
     Start listening on the port specified by a preceding call to
     {@link #setOption}.  */
  public
  void activateOptions() {
    super.activateOptions();
    if(port != 0) {
      if(hup != null) {
	hup.interrupt();
      }
      hup = new HUP(this, port);      
      hup.setDaemon(true);
      hup.start();
    }
  }
}


class HUP extends Thread {

  int port;
  ExternallyRolledFileAppender er;

  HUP(ExternallyRolledFileAppender er, int port) {
    this.er = er;
    this.port = port;
  }
  
  public
  void run() {
    while(!isInterrupted()) {
      try {	
	ServerSocket serverSocket = new ServerSocket(port);
	while(true) {
	  Socket socket = serverSocket.accept();
	  LogLog.debug("Connected to client at " + socket.getInetAddress());
	  new Thread(new HUPNode(socket, er)).start();
	}
      }
      catch(Exception e) {
	e.printStackTrace();
      }
    } 
  }
}

class HUPNode implements Runnable {

  Socket socket;
  DataInputStream dis;
  DataOutputStream dos;
  ExternallyRolledFileAppender er;
  
  public
  HUPNode(Socket socket, ExternallyRolledFileAppender er) {
    this.socket = socket;
    this.er = er;
    try {
      dis = new DataInputStream(socket.getInputStream());
      dos = new DataOutputStream(socket.getOutputStream());
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  
  public void run() {
    try {
      String line = dis.readUTF();
      LogLog.debug("Got external roll over signal.");      
      if(ExternallyRolledFileAppender.ROLL_OVER.equals(line)) {
	synchronized(er) {
	  er.rollOver();
	}
	dos.writeUTF(ExternallyRolledFileAppender.OK);
      }
      else {
	dos.writeUTF("Expecting [RollOver] string.");
      }
      dos.close();
    }
    catch(Exception e) {
      LogLog.error("Unexpected exception. Exiting HUPNode.", e);
    }    
  }
}

