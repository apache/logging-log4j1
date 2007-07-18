/*
 * Copyright 1999,2005 The Apache Software Foundation.
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

import org.apache.log4j.ULogger;
import org.apache.log4j.helpers.Constants;
import org.apache.log4j.spi.LoggingEvent;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.TimeZone;


/**
 * Convert and format the event's date in a StringBuffer.
 *
 * @author Ceki G&uuml;lc&uuml;
 * @since 1.3
 */
public final class DatePatternConverter extends LoggingEventPatternConverter {
  /**
   * Date format.
   */
  private final CachedDateFormat df;

  /**
   * Private constructor.
   * @param options options, may be null.
   * @param logger logger for diagnostic messages, may be null.
   */
  private DatePatternConverter(final String[] options, final ULogger logger) {
    super("Date", "date");

    String patternOption;

    if ((options == null) || (options.length == 0)) {
      // the branch could be optimized, but here we are making explicit
      // that null values for patternOption are allowed.
      patternOption = null;
    } else {
      patternOption = options[0];
    }

    String pattern;

    if (
      (patternOption == null)
        || patternOption.equalsIgnoreCase(Constants.ISO8601_FORMAT)) {
      pattern = Constants.ISO8601_PATTERN;
    } else if (patternOption.equalsIgnoreCase(Constants.ABSOLUTE_FORMAT)) {
      pattern = Constants.ABSOLUTE_TIME_PATTERN;
    } else if (patternOption.equalsIgnoreCase(Constants.DATE_AND_TIME_FORMAT)) {
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
      if (logger != null) {
        logger.warn(
          "Could not instantiate SimpleDateFormat with pattern "
          + patternOption, e);
      }

      // default to the ISO8601 format
      simpleFormat = new SimpleDateFormat(Constants.ISO8601_PATTERN);
    }

    // if the option list contains a TZ option, then set it.
    if ((options != null) && (options.length > 1)) {
      TimeZone tz = TimeZone.getTimeZone((String) options[1]);
      simpleFormat.setTimeZone(tz);
    }

    df = new CachedDateFormat(simpleFormat, maximumCacheValidity);
  }

  /**
   * Obtains an instance of pattern converter.
   * @param options options, may be null.
   * @param logger  logger, current ignored, may be null.
   * @return instance of pattern converter.
   */
  public static DatePatternConverter newInstance(
    final String[] options, final ULogger logger) {
    return new DatePatternConverter(options, logger);
  }

  /**
   * {@inheritDoc}
   */
  public void format(final LoggingEvent event, final StringBuffer output) {
    synchronized(this) {
    	df.format(event.getTimeStamp(), output);
    }
  }

  /**
   * {@inheritDoc}
   */
  public void format(final Object obj, final StringBuffer output) {
    if (obj instanceof Date) {
      format((Date) obj, output);
    }

    super.format(obj, output);
  }

  /**
   * Append formatted date to string buffer.
   * @param date date
   * @param toAppendTo buffer to which formatted date is appended.
   */
  public void format(final Date date, final StringBuffer toAppendTo) {
    synchronized(this) {
    	df.format(date.getTime(), toAppendTo);
    }
  }
}
