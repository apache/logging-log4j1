/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package examples.subclass;

import org.apache.log4j.*;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.xml.DOMConfigurator;
import examples.customLevel.XLevel;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.helpers.LogLog;

/**
   A simple example showing logger subclassing. 

   <p>See <b><a href="doc-files/MyLogger.java">source code</a></b>
   for more details.

   <p>See {@link MyLoggerTest} for a usage example.
   
 */
public class MyLogger extends Logger {

  // It's usually a good idea to add a dot suffix to the fully
  // qualified class name. This makes caller localization to work
  // properly even from classes that have almost the same fully
  // qualified class name as MyLogger, e.g. MyLoggerTest.
  static String FQCN = MyLogger.class.getName() + ".";

  // It's enough to instantiate a factory once and for all.
  private static MyLoggerFactory myFactory = new MyLoggerFactory();

  /**
     Just calls the parent constuctor.
   */
  public MyLogger(String name) {
    super(name);
  }

  /**
     Overrides the standard debug method by appending " world" at the
     end of each message.  */
  public 
  void debug(Object message) {
    super.log(FQCN, Level.DEBUG, message + " world.", null);    
  }

  /**
     This method overrides {@link Logger#getLogger} by supplying
     its own factory type as a parameter.
  */
  public 
  static
  Logger getLogger(String name) {
    return Logger.getLogger(name, myFactory); 
  }

  public
  void trace(Object message) {
    super.log(FQCN, XLevel.TRACE, message, null); 
  }
}


