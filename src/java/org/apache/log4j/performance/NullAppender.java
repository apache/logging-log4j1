/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.APL file.  */

package org.apache.log4j.performance;

import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.Category;
import org.apache.log4j.Priority;
import org.apache.log4j.AppenderSkeleton;

/**
   A bogus appender which calls the format method of its layout object
   but does not write the result anywhere.   
 */
public class NullAppender extends AppenderSkeleton {

  public static String s;
  public String t;	

  public
  NullAppender() {}
  
  public
  NullAppender(Layout layout) {
    this.layout = layout;
  }

  public
  void close() {}
  
  public
  void doAppend(LoggingEvent event) {
    if(layout != null) {
      t = layout.format(event);
      s = t;
    }
  }

  public
  void append(LoggingEvent event) {
  }

  /**
     This is a bogus appender but it still uses a layout.
  */
  public
  boolean requiresLayout() {
    return true;
  }  
}
