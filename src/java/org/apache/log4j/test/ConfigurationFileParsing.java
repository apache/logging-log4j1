/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.APL file.  */

package org.apache.log4j.test; 

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Category;
import org.apache.log4j.NDC;

public class ConfigurationFileParsing {
  
  public 
  static 
  void main(String argv[]) {

    if(argv.length == 1) {
      NDC.push("testing");
      PropertyConfigurator.configure(argv[0]);
      Category root = Category.getRoot();
      root.debug("Message 1");
      root.debug("Message 2");      
      NDC.pop();
    }
    else {
      Usage("Wrong number of arguments.");
    }
    
  }

  static
  void Usage(String msg) {
    System.err.println(msg);
    System.err.println("Usage: java "+ConfigurationFileParsing.class.getName()
		       + " fileName");
    System.exit(1);
  }

  
}
