

package org.apache.log4j.test;

import java.net.Socket;
import java.net.ServerSocket;
import java.io.IOException;

import org.apache.log4j.Category;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.net.SocketNode;
import org.apache.log4j.net.SocketServer;

/**
*/

public class ShortSocketServer  {

  static Category cat = Category.getInstance(ShortSocketServer.class.getName());

  static int port;
  static int delay;

  static
  void delay(int amount) {
    try {
      Thread.currentThread().sleep(amount);
    }
    catch(Exception e) {}
  }
  
  public 
  static 
  void main(String argv[]) {
    if(argv.length == 3) 
      init(argv[0], argv[1], argv[2]);
    else 
      usage("Wrong number of arguments.");     
    
    try {
      LogLog.debug("Listening on port " + port);
      ServerSocket serverSocket = new ServerSocket(port);
      LogLog.debug("Waiting to accept a new client.");
      Socket socket = serverSocket.accept();
      LogLog.debug("Connected to client at " + socket.getInetAddress());
      LogLog.debug("Starting new socket node.");	
      SocketNode sn = new SocketNode(socket, Category.getDefaultHierarchy());
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
      "Usage: java " +ShortSocketServer.class.getName() + 
                    " port configFile delay");
    System.exit(1);
  }
    
  static
  void init(String portStr, String configFile, String delayStr) {
    try {
      port   = Integer.parseInt(portStr);      
    }
    catch(java.lang.NumberFormatException e) {
      e.printStackTrace();
      usage("Could not interpret port number ["+ portStr +"].");
    }
    try {
      delay  = Integer.parseInt(delayStr);
    }
    catch(java.lang.NumberFormatException e) {
      e.printStackTrace();
      usage("Could not interpret delay number ["+ delayStr +"].");
    }
    PropertyConfigurator.configure(configFile);    
  }
}
