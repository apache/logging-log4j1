/*
 * Copyright 1999,2004 The Apache Software Foundation.
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

package org.apache.log4j.net;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.net.SocketNode;

import java.net.ServerSocket;
import java.net.Socket;


/**
 * This SocketServer exits after certain number of connections from a
 * client. This number is determined the totalsTest parameter, that is
 * the first argument on the commmand line. The second argument,
 * prefix, determines the prefix of the configuration file to
 * use.
 *
 * Each run of the server will use a different properties file. For the i-th
 * run, the path to the file is (prefix+i+".properties").
 *
 * There is strong coupling between this class and SocketServerTestCase. When
 * a test case in  SocketServerTestCase tears down its envrionment, it will
 * close its SocketAppender which will cause the SocketNode thread to die,
 * allowing the next test case to start. See the for loop within the main method
 * of this class.
 *
 * @author Ceki Gulcu */
public class ShortSocketServer {
  static Logger logger = Logger.getLogger(ShortSocketServer.class);

  public static void main(String[] args) throws Exception {
    int totalTests = 0;
    String prefix = null;

    if (args.length == 2) {
      totalTests = Integer.parseInt(args[0]);
      prefix = args[1];
    } else {
      usage("Wrong number of arguments.");
    }

    logger.debug("Listening on port " + SocketServerTestCase.PORT);

    ServerSocket serverSocket = new ServerSocket(SocketServerTestCase.PORT);

    MDC.put("hostID", "shortSocketServer");

    for (int i = 1; i <= totalTests; i++) {
      PropertyConfigurator.configure(prefix + i + ".properties");
      logger.debug("Waiting to accept a new client.");

      Socket socket = serverSocket.accept();
      logger.debug("Connected to client at " + socket.getInetAddress());
      logger.debug("Starting new socket node.");

      SocketNode sn = new SocketNode(socket, LogManager.getLoggerRepository());
      Thread t = new Thread(sn);
      t.start();
      // sn will die when an incoming connection is closed.
      t.join();
    }
  }

  static void usage(String msg) {
    System.err.println(msg);
    System.err.println(
      "Usage: java " + ShortSocketServer.class.getName()
      + " totalTests configFilePrefix");
    System.exit(1);
  }
}
