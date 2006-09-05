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

import org.apache.log4j.*;
import org.apache.log4j.net.SocketAppender;

public class Loop {

  public static void main(String[] args) {
    
    
    Logger root = Logger.getRootLogger();
    Logger cat = Logger.getLogger(Loop.class.getName());

    if(args.length != 2) 
      usage("Wrong number of arguments.");     

    String host = args[0];
    int port = 0;

    try {
      port = Integer.valueOf(args[1]).intValue();
    }
    catch (NumberFormatException e) {
        usage("Argument [" + args[1]  + "] is not in proper int form.");
    }

    SocketAppender sa = new SocketAppender(host, port);
    Layout layout = new PatternLayout("%5p [%t] %x %c - %m\n");
    Appender so = new ConsoleAppender(layout, "System.out");
    root.addAppender(sa);
    root.addAppender(so);

    int i = 0;

    while(true) {
      NDC.push(""+ (i++));
      cat.debug("Debug message.");
      root.info("Info message.");
      NDC.pop();
    }

  }

  static
  void  usage(String msg) {
    System.err.println(msg);
    System.err.println(
      "Usage: java " +Loop.class.getName() + " host port");
    System.exit(1);
  }
    

}
