/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j.test; 

import org.apache.log4j.*;
import org.apache.log4j.helpers.LogLog;

/**
   Very simple log4j usage example.

   @author  Ceki G&uuml;lc&uuml;   
 */
public class Hello {

  static Category log = Category.getInstance(Hello.class);

  public 
  static 
  void main(String argv[]) {
    Category root = Category.getRoot();
    Layout layout = new PatternLayout("%p [%t] %c - %m%n");
    try {
      root.addAppender(new FileAppender(layout, System.out));
    } catch(Exception e) {
      LogLog.warn("Could not open file appender.");
    }

    log.debug("Hello world.");
    log.info("What a beatiful day.");
  }
}
