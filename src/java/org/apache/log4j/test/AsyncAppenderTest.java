/* Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.APL file.
 */

package org.apache.log4j.test;

import org.apache.log4j.Category;
import org.apache.log4j.xml.DOMConfigurator;

/**
   Test the coordination of the AsyncAppender with its Dispatcher.
   @author  Ceki G&uuml;lc&uuml;
*/
public class AsyncAppenderTest {

  static Category cat = Category.getInstance(AsyncAppenderTest.class);
  static int delayBeforeClose;
  

  public 
  static 
  void main(String argv[]) {

    if(argv.length == 2) 
      init(argv[0], argv[1]);
    else 
      usage("Wrong number of arguments.");
    test();
  }


  static
  void usage(String msg) {
    System.err.println(msg);
    System.err.println( "Usage: java "+ Shallow.class.getName() +
			"configFile");
    System.exit(1);
  }

  static
  void init(String configFile, String delayBeforeCloseStr) {
    DOMConfigurator.configure(configFile);
    try {
      delayBeforeClose   = Integer.parseInt(delayBeforeCloseStr);
    } catch(java.lang.NumberFormatException e) {
      e.printStackTrace();
      usage("Could not convert ["+delayBeforeCloseStr+"] to Integer.");
    }      
  }


  static
  void test() {    
    Category root = Category.getRoot();    
    for(int i = 0; i < 100; i++) {      
      root.debug("Message " + i);        
    }

    try{Thread.currentThread().sleep(delayBeforeClose);}catch(Exception e){}
    Category.shutdown();
  }

  
}
