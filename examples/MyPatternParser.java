/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package examples;

import org.apache.log4j.*;
import org.apache.log4j.pattern.FormattingInfo;
import org.apache.log4j.pattern.PatternConverter;
import org.apache.log4j.pattern.PatternParser;
import org.apache.log4j.spi.LoggingEvent;

/**
  Example showing how to extend PatternParser to recognize additional
  conversion characters.  The examples shows that minimum and maximum
  width and alignment settings apply for "extension" conversion
  characters just as they do for PatternLayout recognized characters.
  
  <p>In this case MyPatternParser recognizes %# and outputs the value
  of an internal counter which is also incremented at each call.

  See <a href=doc-files/MyPatternParser.java><b>source</b></a> code
   for more details.
  
  @see org.apache.log4j.examples.MyPatternLayout
  @see org.apache.log4j.helpers.PatternParser
  @see org.apache.log4j.PatternLayout

  @author Anders Kristensen 
*/
public class MyPatternParser extends PatternParser {

  int counter = 0;
  StringBuffer buf;

  public
  MyPatternParser(String pattern) {
    super(pattern);
    buf = new StringBuffer();
  }
    
  public
  void finalizeConverter(char c) {
    if (c == '#') {
      addConverter(new UserDirPatternConverter(formattingInfo));
      currentLiteral.setLength(0);
    } else {
      super.finalizeConverter(c);
    }
  }
  
  private class UserDirPatternConverter extends PatternConverter {
    UserDirPatternConverter(FormattingInfo formattingInfo) {
      super(formattingInfo);
    }

    public
    StringBuffer convert(LoggingEvent event) {
      buf.setLength(0);
      return buf.append(String.valueOf(++counter));
    }
  }  
}
