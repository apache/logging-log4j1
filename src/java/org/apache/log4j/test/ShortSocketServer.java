

package org.apache.log4j.test;

import java.net.Socket;
import java.net.ServerSocket;

import org.apache.log4j.Category;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.net.SocketNode;

/**
*/

public class ShortSocketServer  {

  static Category cat = Category.getInstance(ShortSocketServer.class.getName());

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
