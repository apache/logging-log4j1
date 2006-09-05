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

package org.apache.log4j.net.test;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.net.SocketAppender;
import org.apache.log4j.Priority;
import org.apache.log4j.NDC;
import java.io.InputStreamReader;

public class SocketMin {

  static Logger cat = Logger.getLogger(SyslogMin.class.getName());
  static SocketAppender s;

  public
  static
  void main(String argv[]) {
    if(argv.length == 3)
      init(argv[0], argv[1]);
    else
      usage("Wrong number of arguments.");

    NDC.push("some context");
    if(argv[2].equals("true"))
      loop();
    else
      test();

    s.close();
  }

  static
  void usage(String msg) {
    System.err.println(msg);
    System.err.println("Usage: java " + SocketMin.class
		       + " host port true|false");
    System.exit(1);
  }

  static
  void init(String host, String portStr) {
    Logger root = Logger.getRootLogger();
    BasicConfigurator.configure();
    try {
      int port   = Integer.parseInt(portStr);
      cat.info("Creating socket appender ("+host+","+port+").");
      s = new SocketAppender(host, port);
      s.setName("S");
      root.addAppender(s);
    }
    catch(java.lang.NumberFormatException e) {
      e.printStackTrace();
      usage("Could not interpret port number ["+ portStr +"].");
    }
    catch(Exception e) {
      System.err.println("Could not start!");
      e.printStackTrace();
      System.exit(1);
    }
  }

  static
  void loop() {
    Logger root = Logger.getRootLogger();
    InputStreamReader in = new InputStreamReader(System.in);
    System.out.println("Type 'q' to quit");
    int i;
    int k = 0;
    while (true) {
      cat.debug("Message " + k++);
      cat.info("Message " + k++);
      cat.warn("Message " + k++);
      cat.error("Message " + k++, new Exception("Just testing"));
      try {i = in.read(); }
      catch(Exception e) { return; }
      if(i == -1) break;
      if(i == 'q') break;
      if(i == 'r') {
	System.out.println("Removing appender S");
	root.removeAppender("S");
      }
    }
  }

  static
  void test() {
    int i  = 0;
    cat.debug( "Message " + i++);
    cat.info( "Message " + i++);
    cat.warn( "Message " + i++);
    cat.error( "Message " + i++);
    cat.log(Priority.FATAL, "Message " + i++);
    cat.debug("Message " + i++,  new Exception("Just testing."));
  }
}
