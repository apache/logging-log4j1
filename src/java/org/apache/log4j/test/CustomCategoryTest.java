/* Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.APL file.
 */

package org.apache.log4j.test; 
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.log4j.Category;
import org.apache.log4j.NDC;
import org.apache.log4j.Level;
import org.apache.log4j.xml.examples.XLevel;
import org.apache.log4j.xml.examples.XLogger;
import java.io.IOException;
import java.util.Enumeration;

/** 
   This class is used to test support for custom priorities.

   @author  Ceki G&uuml;lc&uuml;
*/

public class CustomCategoryTest {
  
  static XLogger cat = (XLogger) 
                          XLogger.getInstance(CustomCategoryTest.class);
  
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
    System.err.println( "Usage: java "+ CustomCategoryTest.class.getName()
			+" configFile");
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

    cat.trace("Message " + ++i);
    cat.debug("Message " + ++i);
    cat.info("Message " + ++i);
    cat.warn("Message " + ++i);
    cat.error("Message " + ++i);
    cat.lethal("Message " + ++i);

    
    // It is always a good idea to call this method when exiting an
    // application.
    Category.shutdown();
  }
}
