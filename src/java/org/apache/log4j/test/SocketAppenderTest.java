/* Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.APL file.
 */

package org.apache.log4j.test; 
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Category;
import org.apache.log4j.NDC;
import org.apache.log4j.xml.examples.XLogger;
import org.apache.log4j.Priority;
import java.io.IOException;
import java.util.Enumeration;

/**
   @author  Ceki G&uuml;lc&uuml;
*/
public class SocketAppenderTest {
  
  static XLogger cat = (XLogger) XLogger.getInstance(SocketAppenderTest.class);
  
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
    Category root = Category.getRoot();

    cat.trace("Message " + ++i);
    cat.debug("Message " + ++i);
    root.debug("Message " + ++i);
    cat.info("Message " + ++i);
    cat.warn("Message " + ++i);
    cat.lethal("Message " + ++i); //5
    
    Exception e = new Exception("Just testing");
    cat.debug("Message " + ++i, e);
    root.error("Message " + ++i, e);
    
    Category.shutdown();
  }


  static
  void delay(int amount) {
    try {
      Thread.currentThread().sleep(amount);
    }
    catch(Exception e) {}
  }
}
