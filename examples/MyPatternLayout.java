/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.APL file.  */

package org.apache.log4j.examples;

import org.apache.log4j.*;
import org.apache.log4j.helpers.PatternParser;

/**

  Example showing how to extend PatternLayout to recognize additional
  conversion characters.  
  
  <p>In this case MyPatternLayout recognizes %# conversion pattern. It
  outputs the value of an internal counter which is also incremented
  at each call.

  <p>See <a href=doc-files/MyPatternLayout.java><b>source</b></a> code
  for more details.

  @see MyPatternParser
  @see org.apache.log4j.PatternLayout
  @author Anders Kristensen 
*/
public class MyPatternLayout extends PatternLayout {
  public
  MyPatternLayout() {
    this(DEFAULT_CONVERSION_PATTERN);
  }

  public
  MyPatternLayout(String pattern) {
    super(pattern);
  }
    
  public
  PatternParser createPatternParser(String pattern) {
    return new MyPatternParser(
      pattern == null ? DEFAULT_CONVERSION_PATTERN : pattern);
  }
  
  public
  static void main(String[] args) {
    Layout layout = new MyPatternLayout("[counter=%.10#] - %m%n");
    Category cat = Category.getInstance("some.cat");
    cat.addAppender(new FileAppender(layout, System.out));
    cat.debug("Hello, log");
    cat.info("Hello again...");    
  }
}
