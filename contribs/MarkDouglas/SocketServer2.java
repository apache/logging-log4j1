

package com.systemsunion.LoggingServer;

import java.net.Socket;
import java.net.ServerSocket;
import java.io.IOException;

import org.apache.log4j.Category;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.NDC;

/**
   A simple {@link SocketNode} based server.

   <pre>
	 <b>Usage:</b> java org.apache.log4j.net.SocketServer port configFile

	 where <em>port</em> is a part number where the server listens and
	 <em>configFile</em> is a configuration file fed to the {@link
	 PropertyConfigurator}.
   </pre>





	@author  Ceki G&uuml;lc&uuml;

	@since 0.8.4 */

public class SocketServer2  {

  static Category cat = Category.getInstance(SocketServer2.class.getName());

  static int port;

  public
  static
  void main(String argv[]) {
	if(argv.length == 2)
	  init(argv[0], argv[1]);
	else
	  usage("Wrong number of arguments.");

	try {
	  cat.info("Listening on port " + port);
	  ServerSocket serverSocket = new ServerSocket(port);
	  while(true) {
	cat.info("Waiting to accept a new client.");
	Socket socket = serverSocket.accept();
	cat.info("Connected to client at " + socket.getInetAddress());
	cat.info("Starting new socket node.");
	new Thread(new SocketNode2(socket)).start();
	  }
	}
	catch(Exception e) {
	  e.printStackTrace();
	}
  }


  static
  void  usage(String msg) {
	System.err.println(msg);
	System.err.println(
	  "Usage: java " + SocketServer2.class.getName() + " port configFile");
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
	NDC.push("Server");
  }
}
