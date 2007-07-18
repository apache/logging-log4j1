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

package org.apache.log4j.varia;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

/**
   A simple application to send roll over messages to a potentially
   remote {@link ExternallyRolledFileAppender}. 

   <p>It takes two arguments, the <code>host_name</code> and
   <code>port_number</code> where the
   <code>ExternallyRolledFileAppender</code> is listening.
   

   @author Ceki G&uuml;lc&uuml;
   @since version 0.9.0
   @deprecated 1.3
 */
public class Roller {

  private static final Logger logger = Logger.getLogger(Roller.class);
  

  private static String host;
  private static int port;

  // Static class.
  private Roller() {
  }

  /**
     Send a "RollOver" message to
     <code>ExternallyRolledFileAppender</code> on <code>host</code>
     and <code>port</code>.

   */
  public 
  static 
  void main(final String argv[]) {

    BasicConfigurator.configure();

    if(argv.length == 2) 
      init(argv[0], argv[1]);
    else 
      usage("Wrong number of arguments.");
    
    roll();
  }

  private static
  void usage(final String msg) {
    System.err.println(msg);
    System.err.println( "Usage: java " + Roller.class.getName() +
			"host_name port_number");
    System.exit(1);
  }

  private static
  void init(final String hostArg, final String portArg) {
    host = hostArg;
    try {
      port =  Integer.parseInt(portArg);
    }
    catch(java.lang.NumberFormatException e) {
      usage("Second argument "+portArg+" is not a valid integer.");
    }
  }

  /**
   * Sends a roll request on the specified port.
   *
   * @deprecated Deprecated since ExternallyRolledFileAppender is deprecated
   */
  static
  void roll() {
    try {
      Socket socket = new Socket(host, port);
      DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
      DataInputStream dis = new DataInputStream(socket.getInputStream());
      dos.writeUTF(ExternallyRolledFileAppender.ROLL_OVER);
      String rc = dis.readUTF();
      if(ExternallyRolledFileAppender.OK.equals(rc)) {
	     logger.info("Roll over signal acknowledged by remote appender.");
      } else {
        logger.warn("Unexpected return code "+rc+" from remote entity.");
	    System.exit(2);
      }
    } catch(IOException e) {
      logger.error("Could not send roll signal on host "+host+" port "+port+" .",
		e);
      System.exit(2);
    }
    System.exit(0);
  }
}
