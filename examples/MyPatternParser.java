package org.log4j.examples;

import org.log4j.*;
import org.log4j.helpers.FormattingInfo;
import org.log4j.helpers.PatternConverter;
import org.log4j.helpers.PatternParser;
import org.log4j.spi.LoggingEvent;

/**
  Example showing how to extend PatternParser to recognize additional
  conversion characters.  The examples shows that minimum and maximum
  width and alignment settings apply for "extension" conversion
  characters just as they do for PatternLayout recognized characters.
  
  <p>In this case MyPatternParser recognizes %# and outputs the value
  of an internal counter which is also incremented at each call.

  See <a href=doc-files/MyPatternParser.java><b>source</b></a> code
   for more details.
  
  @see org.log4j.examples.MyPatternLayout
  @see org.log4j.helpers.PatternParser
  @see org.log4j.PatternLayout

  @author Anders Kristensen 
*/
public class MyPatternParser extends PatternParser {

  int counter = 0;

  public
  MyPatternParser(String pattern) {
    super(pattern);
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
    String convert(LoggingEvent event) {
      return String.valueOf(++counter);
    }
  }  
}
