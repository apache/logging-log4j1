/*
 * Copyright 1999,2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.log4j.pattern;

import org.apache.log4j.Logger;
import org.apache.log4j.helpers.Constants;
import org.apache.log4j.spi.LoggingEvent;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.List;


/**
 * Convert and format the event's date in a StringBuffer.
 *
 * @author Ceki G&uuml;lc&uuml;
 */
public class DatePatternConverter extends PatternConverter {
  // We assume that each PatternConveter instance is unique within a layout, 
  // which is unique within an appender. We further assume that calls to the 
  // appender method are serialized (per appender).
  Logger logger = Logger.getLogger(DatePatternConverter.class);
  private CachedDateFormat df;
  private final StringBuffer buf = new StringBuffer(30); 

  //  public DatePatternConverter(FormattingInfo formattingInfo) {
  //    super(formattingInfo);
  //    this.buf = new StringBuffer(32);
  //    date = new Date();
  //  }
  public DatePatternConverter() {
  }

  /**
   * The option string can be one of the strings 'DATE', 'ABSOLUTE', 'ISO8601'
   * or any date and time pattern accepted by java.text.SimpleDateFormat.
   */
  public void setOptions(List optionList) {
    
    String patternOption;
    
    if(optionList == null || optionList.size() == 0) {
      // the branch could be optimized, but here we are making explicit
      // that null values for patternOption are allowed.
      patternOption = null;
    } else {
      patternOption = (String) optionList.get(0);
    }
    
    String pattern;
    if (patternOption == null || patternOption.equalsIgnoreCase(Constants.ISO8601_FORMAT)) {
      pattern = Constants.ISO8601_PATTERN;
    } else if (
      patternOption.equalsIgnoreCase(Constants.ABSOLUTE_FORMAT)) {
      pattern = Constants.ABSOLUTE_TIME_PATTERN;
    } else if (
      patternOption.equalsIgnoreCase(Constants.DATE_AND_TIME_FORMAT)) {
      pattern = Constants.DATE_AND_TIME_PATTERN;
    } else {
      pattern = patternOption;
    }

    int maximumCacheValidity = 1000;
    SimpleDateFormat simpleFormat = null;
    try {
      simpleFormat = new SimpleDateFormat(pattern);
      maximumCacheValidity = CachedDateFormat.getMaximumCacheValidity(pattern);
    } catch (IllegalArgumentException e) {
      logger.warn(
        "Could not instantiate SimpleDateFormat with pattern " + patternOption, e);
      // default to the ISO8601 format
      simpleFormat = new SimpleDateFormat(Constants.ISO8601_PATTERN);
    }
   
     
    // if the option list contains a TZ option, then set it.
    if (optionList != null && optionList.size() > 1) {
      TimeZone tz = TimeZone.getTimeZone((String) optionList.get(1));
      simpleFormat.setTimeZone(tz);
    }

    df = new CachedDateFormat(simpleFormat, maximumCacheValidity);
  }
  
  public StringBuffer convert(LoggingEvent event) {
    buf.setLength(0);
    df.format(event.getTimeStamp(), buf);
    return buf;
  }

  public String getName() {
    return "Date";
  }

  public String getStyleClass(LoggingEvent e) {
    return "date";
  }
}
