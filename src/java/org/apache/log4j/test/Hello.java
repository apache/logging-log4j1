/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.APL file.  */

package org.apache.log4j.test; 

import org.apache.log4j.Category;
import org.apache.log4j.BasicConfigurator;

/**
   Very simple log4j usage example.

   @author  Ceki G&uuml;lc&uuml;   
 */
public class Hello {

  static Category cat = Category.getInstance(Hello.class);

  public 
  static 
  void main(String argv[]) {
    BasicConfigurator.configure();
    cat.debug("Hello world.");
    cat.info("What a beatiful day.");
  }
}
