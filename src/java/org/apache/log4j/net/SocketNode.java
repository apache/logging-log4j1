/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.log4j.net;

import java.net.Socket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.BufferedInputStream;

import org.apache.log4j.*;
import org.apache.log4j.spi.*;
import org.apache.log4j.plugins.Receiver;

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
  Receiver receiver;
  SocketNodeEventListener listener;

  static Logger logger = Logger.getLogger(SocketNode.class);

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
    Set the event listener on this node. */
  public void setListener(SocketNodeEventListener _listener) {
    listener = _listener;
  }
  

  public void run() {
    LoggingEvent event;
    Logger remoteLogger;
    Exception listenerException = null;
    ObjectInputStream ois = null;
    
    try {
      ois = new ObjectInputStream(
        new BufferedInputStream(socket.getInputStream()));
    } catch (Exception e) {
      ois = null;
      listenerException = e;
      logger.error("Exception opening ObjectInputStream to " + socket, e);
    }

    if (ois != null) {
      try {
        while(true) {
          // read an event from the wire
        	event = (LoggingEvent) ois.readObject();
        	
        	// if configured with a receiver, tell it to post the event
          if (receiver != null) {
            receiver.doPost(event);
          // else post it via the hierarchy
          } else {
            // get a logger from the hierarchy. The name of the logger
            // is taken to be the name contained in the event.
            remoteLogger = hierarchy.getLogger(event.categoryName);
            //event.logger = remoteLogger;
            // apply the logger-level filter
            if(event.level.isGreaterOrEqual(remoteLogger.getEffectiveLevel())) {
              // finally log the event as if was generated locally
              remoteLogger.callAppenders(event);
            }
          }
        }
      } catch(java.io.EOFException e) {
        logger.info("Caught java.io.EOFException closing conneciton.");
        listenerException = e;
      } catch(java.net.SocketException e) {
        logger.info("Caught java.net.SocketException closing conneciton.");
        listenerException = e;
      } catch(IOException e) {
        logger.info("Caught java.io.IOException: "+e);
        logger.info("Closing connection.");
        listenerException = e;
      } catch(Exception e) {
        logger.error("Unexpected exception. Closing connecition.", e);
        listenerException = e;
      }
    }

    // close the socket
    try {
      if (ois != null) {
        ois.close();
      }
    } catch(Exception e) {
      //logger.info("Could not close connection.", e);
    }
    
    // send event to listener, if configured
    if (listener != null) {
      listener.socketClosedEvent(listenerException);
    }
  }
}
