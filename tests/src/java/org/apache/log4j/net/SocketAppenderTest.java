/* Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.APL file.
 */

package org.apache.log4j.test; 
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.log4j.PropertyConfigurator;

import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;
import org.apache.log4j.NDC;
import org.apache.log4j.xml.XLevel;
import org.apache.log4j.Priority;
import java.io.IOException;
import java.util.Enumeration;

/**
   @author  Ceki G&uuml;lc&uuml;
*/
public class SocketAppenderTest {
  
  static Logger logger = Logger.getLogger(SocketAppenderTest.class);
  
  public 
  static 
  void main(String argv[]) {
    if(argv.length == 1) 
      init(argv[0]);
    else 
      usage("Wrong number of arguments.");
    test();
  }

  static
  void usage(String msg) {
    System.err.println(msg);
    System.err.println( "Usage: java "+ SocketAppenderTest.class.getName()+
			" configFile");
    System.exit(1);
  }

  static 
  void init(String configFile) {
    if(configFile.endsWith(".xml"))
      DOMConfigurator.configure(configFile);
    else 
      PropertyConfigurator.configure(configFile);
  }

  static 
  void test() {
    int i = -1; 
    NDC.push("NDC"); 
    Logger root = Logger.getRootLogger();

    logger.log(XLevel.TRACE, "Message " + ++i);
    logger.debug("Message " + ++i);
    root.debug("Message " + ++i);
    logger.info("Message " + ++i);
    logger.warn("Message " + ++i);
    logger.log(XLevel.LETHAL, "Message " + ++i); //5
    
    Exception e = new Exception("Just testing");
    logger.debug("Message " + ++i, e);
    root.error("Message " + ++i, e);
    
    LogManager.shutdown();
  }


  static
  void delay(int amount) {
    try {
      Thread.currentThread().sleep(amount);
    }
    catch(Exception e) {}
  }
}
