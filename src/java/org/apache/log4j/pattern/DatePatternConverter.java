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
import java.text.SimpleDateFormat;

import java.util.Date;


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
  private DateFormat df;
  private Date date;
  protected FieldPosition pos = new FieldPosition(0);

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
  public void setOption(String option) {
    super.setOption(option);

    String pattern;
    if (option == null) {
      pattern = "yyyy-MM-dd HH:mm:ss,SSS";
    } else if (
      option.equalsIgnoreCase(AbsoluteTimeDateFormat.ISO8601_DATE_FORMAT)) {
      pattern = "yyyy-MM-dd HH:mm:ss,SSS";
    } else if (
      option.equalsIgnoreCase(AbsoluteTimeDateFormat.ABS_TIME_DATE_FORMAT)) {
      pattern = "HH:mm:ss,SSS";
    } else if (
      option.equalsIgnoreCase(
          AbsoluteTimeDateFormat.DATE_AND_TIME_DATE_FORMAT)) {
      pattern = "dd MMM yyyy HH:mm:ss,SSS";
    } else {
      pattern = option;
    }

    try {
      df = new SimpleDateFormat(pattern);
    } catch (IllegalArgumentException e) {
      logger.warn(
        "Could not instantiate SimpleDateFormat with pattern " + option, e);
      // detault for the ISO8601 format
      df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
    }
  }

  public StringBuffer convert(LoggingEvent event) {
    buf.setLength(0);

    date.setTime(event.getTimeStamp());

    String converted = null;

    try {
      df.format(date, buf, pos);
    } catch (Exception ex) {
      logger.error("Error occured while converting date.", ex);
    }

    return buf;
  }

  public String getName() {
    return "Date";
  }

  public String getStyleClass(LoggingEvent e) {
    return "date";
  }
}
