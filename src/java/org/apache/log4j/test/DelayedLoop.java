/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.APL file.  */

package org.apache.log4j.test; 

import org.apache.log4j.Category;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

/**
   This test program sits in a loop and logs things. Its logging is
   configured by a configuration file. Changes to this configuration
   file are monitored and when a change occurs, the config file is re-read.

   
   @author Ceki G&uuml;lc&uuml; */
public class DelayedLoop {

  static Category cat = Category.getInstance(DelayedLoop.class);
  static int loopLength;

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
    System.err.println( "Usage: java " + DelayedLoop.class.getName() +
			"configFile");
    System.exit(1);
  }

  
  static
  void init(String configFile) {
    if(configFile.endsWith("xml")) {
      DOMConfigurator.configureAndWatch(configFile, 3000);
    } else {
      PropertyConfigurator.configureAndWatch(configFile, 3000);
    }
  }

  static
  void test() {
    int i = 0;
    while(true) {
      cat.debug("MSG "+i++);
      try {
	      Thread.sleep(1000);
      } catch(Exception e) {}
    }
  }
}
