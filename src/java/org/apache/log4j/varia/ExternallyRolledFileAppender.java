//      Copyright 2000, Ceki Gulcu. 
//
//      See the LICENCE file for the terms of usage and distribution.

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
   This appender listen on a socket on the port specified by the
   {@link #PORT_OPTION} for a "RollOver" message. If and and when such
   a message is received, the underlying log file is rolled over and
   an acknowledgement message is sent back to the process initiating
   the roll over.

   <p>Compared to the the {@link ResilientFileAppender}, this method
   of triggering roll over has the advantage of being operating system
   independent, fast and reliable.
   
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

     <p>All option keys are case sensitive.  */

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
     Thia default constructor does nothing but call its super-class
     constructor.  */
  public
  ExternallyRolledFileAppender() { 
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

  /**
     Retuns the option names for this component, namely {@link
     #PORT_OPTION} in addition to the options of its super class {@link
     RollingFileAppender#getOptionStrings FileAppender}.  */
  public
  String[] getOptionStrings() {

    return OptionConverter.concatanateArrays(super.getOptionStrings(),
		 new String[] {PORT_OPTION});
  }

  /**
     The <b>Port</b> options takes a 

   */
  public
  void setOption(String option, String value) {
    super.setOption(option, value);    
    if(option.equalsIgnoreCase(PORT_OPTION)) {
      port = OptionConverter.toInt(value, port);
      LogLog.debug("Port option set to "+port); 
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
      LogLog.error("Unexptected exception. Exiting HUPNode.", e);
    }    
  }
}

