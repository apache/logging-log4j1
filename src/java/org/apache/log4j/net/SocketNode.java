//      Copyright 1996-2000, International Business Machines 
//      Corporation. All Rights Reserved.

//      Copyright 2000, Ceki Gulcu. All Rights Reserved.

//      See the LICENCE file for the terms of usage and distribution.


package org.apache.log4j.net;

import java.net.InetAddress;
import java.net.Socket;
import java.net.ServerSocket;
import java.io.InputStream;
import java.io.IOException;
import java.io.ObjectInputStream;


import org.apache.log4j.*;
import org.apache.log4j.spi.*;

// Contributors:  Moses Hohman <mmhohman@rainbow.uchicago.edu>

/**
   Read {@link LoggingEvent} objects sent from a remote client using
   Sockets (TCP). These logging events are logged according to local
   policy, as if they were generated locally.

   <p>For example, the socket node might decide to log events to a
   local file and also resent them to a second socket node.
   
    @author  Ceki G&uuml;lc&uuml;
 
    @since 0.8.4
*/
public class SocketNode implements Runnable {

  Socket socket;
  LoggerRepository hierarchy;
  ObjectInputStream ois;

  static Logger logger = Category.getInstance(SocketNode.class);

  public 
  SocketNode(Socket socket, LoggerRepository hierarchy) {
    this.socket = socket;
    this.hierarchy = hierarchy;
    try {
      ois = new ObjectInputStream(socket.getInputStream());
    }
    catch(Exception e) {
      logger.error("Could not open ObjectInputStream to "+socket, e);
    }
  }

  //public
  //void finalize() {
  //System.err.println("-------------------------Finalize called");
  // System.err.flush();
  //}

  public void run() {
    LoggingEvent event;
    Logger remoteLogger;

    try {
      while(true) {	
	event = (LoggingEvent) ois.readObject();	
	remoteLogger = hierarchy.getLogger(event.categoryName);
	event.logger = remoteLogger;
	if(event.level.isGreaterOrEqual(remoteLogger.getChainedLevel())) {
	  remoteLogger.callAppenders(event);	
	}
      }
    }
    catch(java.io.EOFException e) {
      logger.info("Caught java.io.EOFException closing conneciton.");
    } catch(java.net.SocketException e) {
      logger.info("Caught java.net.SocketException closing conneciton.");
    } catch(IOException e) {
      logger.info("Caught java.io.IOException: "+e);
      logger.info("Closing connection.");
    } catch(Exception e) {
      logger.error("Unexpected exception. Closing conneciton.", e);
    }
    
    try {
      ois.close();
    } catch(Exception e) {
      logger.info("Could not close connection.", e);	
    }  
  }
}
