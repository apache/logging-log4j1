/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.APL file.  */

package org.apache.log4j.examples;

import org.apache.log4j.*;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.log4j.xml.examples.XLevel;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.helpers.LogLog;

/**
   A simple example showing logger subclassing. 

   <p>The example should make it clear that subclasses follow the
   hierarchy. You should also try running this example with a <a
   href="doc-files/mycat.bad">bad</a> and <a
   href="doc-files/mycat.good">good</a> configuration file samples.

   <p>See <b><a
   href="doc-files/MyLogger.java">source code</a></b> for more details.
*/
public class MyLoggerTest {

  /**
     When called wihtout arguments, this program will just print 
     <pre>
       DEBUG [main] some.cat - Hello world.
     </pre>
     and exit.
     
     <b>However, it can be called with a configuration file in XML or
     properties format.

   */
  static public void main(String[] args) {
    
    if(args.length == 0) {
      // Note that the appender is added to root but that the log
      // request is made to an instance of MyLogger. The output still
      // goes to System.out.
      Logger root = Logger.getRoot();
      Layout layout = new PatternLayout("%p [%t] %c (%F:%L) - %m%n");
      root.addAppender(new ConsoleAppender(layout, ConsoleAppender.SYSTEM_OUT));
    }
    else if(args.length == 1) {
      if(args[0].endsWith("xml")) {
	DOMConfigurator.configure(args[0]);
      } else {
	PropertyConfigurator.configure(args[0]);
      }
    } else {
      usage("Incorrect number of parameters.");
    }
    try {
      MyLogger c = (MyLogger) MyLogger.getInstance("some.cat");    
      c.trace("Hello");
      c.debug("Hello");
    } catch(ClassCastException e) {
      LogLog.error("Did you forget to set the factory in the config file?", e);
    }
  }

  static
  void usage(String errMsg) {
    System.err.println(errMsg);
    System.err.println("\nUsage: "+MyLogger.class.getName() + "[configFile]\n"
                + " where *configFile* is an optional configuration file, "+
		       "either in properties or XML format.");
    System.exit(1);
  }
}
