/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.log4j.net;

import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.joran.JoranConfigurator;

import java.net.ServerSocket;
import java.net.Socket;


/**
 *  A simple {@link SocketNode} based server.
 *
   <pre>
   <b>Usage:</b> java org.apache.log4j.net.SimpleSocketServer port configFile

   where <em>port</em> is a part number where the server listens and
   <em>configFile</em> is a configuration file fed to the {@link
   PropertyConfigurator} or to {@link JoranConfigurator} if an XML file.
   </pre>
  *
  * @author  Ceki G&uuml;lc&uuml;
  *
  * @since 0.8.4
  * */
public class SimpleSocketServer {
  final static Logger logger = Logger.getLogger(SimpleSocketServer.class);
  static int port;

  public static void main(String[] argv) {
    if (argv.length == 2) {
      init(argv[0], argv[1]);
    } else {
      usage("Wrong number of arguments.");
    }

    try {
      logger.info("Listening on port " + port);

      ServerSocket serverSocket = new ServerSocket(port);

      while (true) {
        logger.info("Waiting to accept a new client.");

        Socket socket = serverSocket.accept();
        logger.info("Connected to client at " + socket.getInetAddress());
        logger.info("Starting new socket node.");
        new Thread(new SocketNode(socket, LogManager.getLoggerRepository()))
        .start();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  static void usage(String msg) {
    System.err.println(msg);
    System.err.println(
      "Usage: java " + SimpleSocketServer.class.getName() + " port configFile");
    System.exit(1);
  }

  static void init(String portStr, String configFile) {
    try {
      port = Integer.parseInt(portStr);
    } catch (java.lang.NumberFormatException e) {
      e.printStackTrace();
      usage("Could not interpret port number [" + portStr + "].");
    }

    if (configFile.endsWith(".xml")) {
      JoranConfigurator jc = new JoranConfigurator();
      jc.doConfigure(configFile, LogManager.getLoggerRepository());
    } else {
      PropertyConfigurator.configure(configFile);
    }
  }
}
