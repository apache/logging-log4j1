/* Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */


package org.apache.log4j.net;

import java.net.Socket;
import java.net.ServerSocket;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.net.SocketNode;
import org.apache.log4j.net.SocketServer;

/**
   This SocketServer exits after just one connection from a client.

   @author Ceki Gulcu
*/

public class ShortSocketServer  {

  static Logger cat = Logger.getLogger(ShortSocketServer.class);

  public 
  static 
  void main(String args[]) throws Exception {
    int totalTests = 0;
    String prefix = null;

    if(args.length == 2) {
      totalTests = Integer.parseInt(args[0]);
      prefix = args[1];
    } else {
      usage("Wrong number of arguments."); 
    }
    

      LogLog.debug("Listening on port " + SocketServerTestCase.PORT);
      ServerSocket serverSocket = new ServerSocket(SocketServerTestCase.PORT);

      for(int i = 1; i <= totalTests; i++) {
	PropertyConfigurator.configure(prefix+i+".properties");
	LogLog.debug("Waiting to accept a new client.");
	Socket socket = serverSocket.accept();
	LogLog.debug("Connected to client at " + socket.getInetAddress());
	LogLog.debug("Starting new socket node.");	
	SocketNode sn = new SocketNode(socket, LogManager.getLoggerRepository());
	Thread t = new Thread(sn);
	t.start(); 
	t.join();
      }
  }

  
  static
  void  usage(String msg) {
    System.err.println(msg);
    System.err.println(
      "Usage: java " +ShortSocketServer.class.getName() + " totalTests configFilePrefix");
    System.exit(1);
  }    
}
