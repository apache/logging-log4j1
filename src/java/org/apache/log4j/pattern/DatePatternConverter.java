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
import org.apache.log4j.spi.LoggingEvent;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;

import java.util.Date;


/**
 * Return the events thread (usually the current thread) in a StringBuffer.
 * This buffer is recycled!
 *
 * @author Ceki G&uuml;lc&uuml;
 */
public class DatePatternConverter extends PatternConverter {
  // We assume that each PatternConveter instance is unique within a layout, 
  // which is unique within an appender. We further assume that callas to the 
  // appender method are serialized (per appender).
  StringBuffer buf;
  Logger logger = Logger.getLogger(DatePatternConverter.class);
  private DateFormat df;
  private Date date;
  protected FieldPosition pos = new FieldPosition(0);

  public DatePatternConverter(FormattingInfo formattingInfo) {
    super(formattingInfo);
    this.buf = new StringBuffer(32);
    date = new Date();
  }

  public void setOption(String option) {
    super.setOption(option);

    try {
      df = new SimpleDateFormat(option);
    } catch (IllegalArgumentException e) {
      logger.error("Could not instantiate SimpleDateFormat with " + option, e);

      // detault for the IDO8601 format
      df = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss,SSS");
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

  public String getName()
  {
      return "Date";
  }
  
}
