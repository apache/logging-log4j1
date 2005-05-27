/*
 * Copyright 1999-2005 The Apache Software Foundation.
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


package org.apache.log4j.test;

import java.net.Socket;
import java.net.ServerSocket;

import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.net.SocketNode;

/**
*/

public class ShortSocketServer  {

  static Logger cat = Logger.getLogger(ShortSocketServer.class.getName());

  static int port;

  public
  static
  void main(String argv[]) {
    if(argv.length == 2) {
      init(argv[0], argv[1]);
    } else {
      usage("Wrong number of arguments.");
    }

    try {
      LogLog.debug("Listening on port " + port);
      ServerSocket serverSocket = new ServerSocket(port);
      LogLog.debug("Waiting to accept a new client.");
      Socket socket = serverSocket.accept();
      LogLog.debug("Connected to client at " + socket.getInetAddress());
      LogLog.debug("Starting new socket node.");
      SocketNode sn = new SocketNode(socket, LogManager.getLoggerRepository());
      Thread t = new Thread(sn);
      t.start();
      t.join();
    }
    catch(Exception e) {
      cat.error("Error while in main.", e);
    }
  }


  static
  void  usage(String msg) {
    System.err.println(msg);
    System.err.println(
      "Usage: java " +ShortSocketServer.class.getName() + " port configFile");
    System.exit(1);
  }

  static
  void init(String portStr, String configFile) {
    try {
      port   = Integer.parseInt(portStr);
    }
    catch(java.lang.NumberFormatException e) {
      e.printStackTrace();
      usage("Could not interpret port number ["+ portStr +"].");
    }
    PropertyConfigurator.configure(configFile);
  }
}
