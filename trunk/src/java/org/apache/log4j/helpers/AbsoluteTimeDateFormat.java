/*
 * Copyright 1999,2006 The Apache Software Foundation.
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

package org.apache.log4j.helpers;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.TimeZone;


/**
   Formats a {@link Date} in the format "HH:mm:ss,SSS" for example,
   "15:49:37,459".

   @author Ceki G&uuml;lc&uuml;
   @author Andrew Vajoczki

   @since 0.7.5
   @deprecated use java.text.SimpleDateFormat to perform date conversion
       or use org.apache.log4j.helpers.CachedDateFormat to optimize
       high-frequency date formatting.
*/
public class AbsoluteTimeDateFormat extends DateFormat {
  /**
     String constant used to specify the Absolute time and date format.
  */
  public static final String ABS_TIME_DATE_FORMAT = "ABSOLUTE";

  /**
     String constant used to specify the "Date and Time" date format.
  */
  public static final String DATE_AND_TIME_DATE_FORMAT = "DATE";

  /**
     String constant used to specify the ISO8601 format.
  */
  public static final String ISO8601_DATE_FORMAT = "ISO8601";

  /**
     Equivalent SimpleDateFormat pattern.
   */
  public static final String PATTERN = "HH:mm:ss,SSS";

  /**
   * SimpleDateFormat used to perform format requests.
   */
  private final SimpleDateFormat format;

  /**
   *  Create a new instance of AbsoluteTimeDateFormat.
   */
  public AbsoluteTimeDateFormat() {
    format = new SimpleDateFormat(PATTERN);
  }

  /**
   *   Create a new instance of AbsoluteTimeDateFormat.
   *   @param timeZone time zone used in conversion, may not be null.
   */
  public AbsoluteTimeDateFormat(final TimeZone timeZone) {
    format = new SimpleDateFormat(PATTERN);
    format.setTimeZone(timeZone);
  }

    /**
     *   Create a new instance of AbsoluteTimeDateFormat.
     *   @param pattern SimpleDateFormat pattern.
     */
   protected AbsoluteTimeDateFormat(final String pattern) {
      format = new SimpleDateFormat(pattern);
    }

    /**
     *   Create a new instance of AbsoluteTimeDateFormat.
     *   @param pattern SimpleDateFormat pattern.
     *   @param timeZone time zone used in conversion, may not be null.
     */
   protected AbsoluteTimeDateFormat(final String pattern,
                                    final TimeZone timeZone) {
      format = new SimpleDateFormat(pattern);
      format.setTimeZone(timeZone);
    }

  /**
   * {@inheritDoc}
    */
  public StringBuffer format(
    Date date, StringBuffer sbuf, FieldPosition fieldPosition) {
    return format.format(date, sbuf, fieldPosition);
  }

  /**
   * {@inheritDoc}
   */
  public Date parse(String s, ParsePosition pos) {
    return format.parse(s, pos);
  }
}
