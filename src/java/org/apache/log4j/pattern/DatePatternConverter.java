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
import org.apache.log4j.helpers.AbsoluteTimeDateFormat;
import org.apache.log4j.spi.LoggingEvent;

import java.text.DateFormat;
import java.text.FieldPosition;

import java.util.Date;
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
  StringBuffer buf;
  Logger logger = Logger.getLogger(DatePatternConverter.class);
  private DateFormat cdf;
  private Date date;
  protected FieldPosition pos = new FieldPosition(0);
  long lastTimestamp = 0;
  boolean alreadyWarned = false;
  
  //  public DatePatternConverter(FormattingInfo formattingInfo) {
  //    super(formattingInfo);
  //    this.buf = new StringBuffer(32);
  //    date = new Date();
  //  }
  public DatePatternConverter() {
    this.buf = new StringBuffer(32);
    date = new Date();
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
    if (patternOption == null) {
      pattern = "yyyy-MM-dd HH:mm:ss,SSS";
    } else if (
      patternOption.equalsIgnoreCase(AbsoluteTimeDateFormat.ISO8601_DATE_FORMAT)) {
      pattern = "yyyy-MM-dd HH:mm:ss,SSS";
    } else if (
      patternOption.equalsIgnoreCase(AbsoluteTimeDateFormat.ABS_TIME_DATE_FORMAT)) {
      pattern = "HH:mm:ss,SSS";
    } else if (
      patternOption.equalsIgnoreCase(
          AbsoluteTimeDateFormat.DATE_AND_TIME_DATE_FORMAT)) {
      pattern = "dd MMM yyyy HH:mm:ss,SSS";
    } else {
      pattern = patternOption;
    }

    try {
      cdf = new CachedDateFormat(pattern);
    } catch (IllegalArgumentException e) {
      logger.warn(
        "Could not instantiate SimpleDateFormat with pattern " + patternOption, e);
      // detault for the ISO8601 format
      cdf = new CachedDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
    }

    // if the option list contains a TZ option, then set it.
    if (optionList != null && optionList.size() > 1) {
      TimeZone tz = TimeZone.getTimeZone((String) optionList.get(1));
      cdf.setTimeZone(tz);
    }
  }
  
  public StringBuffer convert(LoggingEvent event) {
    long timestamp = event.getTimeStamp();
    // if called multiple times within the same milliseconds
    // return old value
    if(timestamp == lastTimestamp) {
      return buf;
    } else {
      buf.setLength(0);
      lastTimestamp = timestamp;
      date.setTime(timestamp);
      try {
        cdf.format(date, buf, pos);
        lastTimestamp = timestamp;
      } catch (Exception ex) {
        // this should never happen
        buf.append("DATE_CONV_ERROR");
        if(!alreadyWarned) {
          alreadyWarned = true;
          logger.error("Exception while converting date", ex);
        }
      }
      return buf;
    }
  }

  public String getName() {
    return "Date";
  }

  public String getStyleClass(LoggingEvent e) {
    return "date";
  }
}
