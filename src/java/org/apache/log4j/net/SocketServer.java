/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.APL file.  */

package org.apache.log4j.net;

import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.io.IOException;
import java.io.File;
import java.util.Hashtable;

import org.apache.log4j.Category;
import org.apache.log4j.Hierarchy;
import org.apache.log4j.Priority;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.spi.RootCategory;


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

public class SocketServer  {

  static String DEFAULT_PREFIX = "default";
  static String CONFIG_FILE_EXT = ".lcf";

  static Category cat = Category.getInstance(SocketServer.class.getName());  
  static SocketServer server;
  static int port;

  // key=inetAddress, value=hierarchy
  static Hashtable hierarchyMap = new Hashtable(11);
  static Hierarchy defaultHierarchy;
  static File dir;


  public 
  static 
  void main(String argv[]) {
    if(argv.length == 3) 
      init(argv[0], argv[1], argv[2]);
    else 
      usage("Wrong number of arguments.");     
  
    
    
    try {
      cat.info("Listening on port " + port);
      ServerSocket serverSocket = new ServerSocket(port);
      while(true) {
	cat.info("Waiting to accept a new client.");
	Socket socket = serverSocket.accept();
	InetAddress inetAddress =  socket.getInetAddress();
	cat.info("Connected to client at " + inetAddress);

	Hierarchy h = (Hierarchy) hierarchyMap.get(inetAddress);
	if(h == null) {
	  h = configureHierarchy(inetAddress);
	} 

	cat.info("Starting new socket node.");	
	new Thread(new SocketNode(socket, Category.getDefaultHierarchy())).start();
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
      "Usage: java " +SocketServer.class.getName() + " port configFile directory");
    System.exit(1);
  }
    
  static
  void init(String portStr, String configFile, String dirStr) {
    try {
      port = Integer.parseInt(portStr);      
    }
    catch(java.lang.NumberFormatException e) {
      e.printStackTrace();
      usage("Could not interpret port number ["+ portStr +"].");
    }
    
    PropertyConfigurator.configure(configFile);
    
    
    dir = new File(dirStr);    
    if(!dir.isDirectory()) {
      usage("["+dirStr+"] is not a directory.");
    }
  }



  // This method assumes that there is no hiearchy for inetAddress
  // yet. It will configure one and return it.
  static
  Hierarchy configureHierarchy(InetAddress inetAddress) {
    // We assume that the toSting method of InetAddress returns is in
    // the format hostname/d1.d2.d3.d4 e.g. torino/192.168.1.1
    String s = inetAddress.toString();
    int i = s.indexOf("/");
    if(i == -1) {
      cat.warn("Could not parse the inetAddress ["+inetAddress+
	       "]. Using default hierarchy.");
      return defaultHierarchy();
    } else {
      String key = s.substring(0, i);
      System.out.println("key = "+key);
      
      File configFile = new File(dir, key+CONFIG_FILE_EXT);
      if(configFile.exists()) {
	Hierarchy h = new Hierarchy(new RootCategory(Priority.DEBUG));
	hierarchyMap.put(inetAddress, h);
	
	try {
	  new PropertyConfigurator().doConfigure(configFile.toURL(), h);
	} catch(MalformedURLException e) {
	  cat.error("Could not convert"+configFile+" to a URL.", e);
	}
	return h;	
      } else {
	cat.warn("Could not find config file ["+configFile+"].");
	return defaultHierarchy();
      }
    }
  }

  static
  Hierarchy defaultHierarchy() {
    if(defaultHierarchy == null) {
      File f = new File(dir, DEFAULT_PREFIX+CONFIG_FILE_EXT);
      if(f.exists()) {
	defaultHierarchy = new Hierarchy(new RootCategory(Priority.DEBUG));
	try {
	  new PropertyConfigurator().doConfigure(f.toURL(),
					       defaultHierarchy);
	} catch(MalformedURLException e) {
	  cat.error("Could not convert"+f+" to a URL.", e);
	}
      } else {
	cat.warn("Could not find config file ["+f+"].");
	defaultHierarchy = Category.getDefaultHierarchy();
      }
    }
    return defaultHierarchy;
  }
}
