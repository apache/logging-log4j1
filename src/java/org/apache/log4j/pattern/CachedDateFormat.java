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

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


/**
 * Caches the results of a DateFormat.
 *  @author Curt Arnold
 *  @since 1.3
 */
final class CachedDateFormat extends DateFormat {
  private static final int UNRECOGNIZED_MILLISECOND_PATTERN = -2;
  private static final int NO_MILLISECOND_PATTERN = -1;

  // We take advantage of structure of the sentinel, in particular
  // the incremental decrease in the digits 9, 8, and 7.
  private static final int SENTINEL = 987;
  private DateFormat formatter;
  private int millisecondStart;
  private StringBuffer cache = new StringBuffer();
  private long slotBegin;
  private int milliDigits;
  private StringBuffer milliBuf = new StringBuffer(3);
  private NumberFormat numberFormat;

  public CachedDateFormat(String pattern) {
    this(pattern, null);
  }

  public CachedDateFormat(String pattern, Locale locale) {
    if (pattern == null) {
      throw new IllegalArgumentException("Pattern cannot be null");
    }
    if (locale == null) {
      this.formatter = new SimpleDateFormat(pattern);
    } else {
      this.formatter = new SimpleDateFormat(pattern, locale);
    }
    milliDigits = CacheUtil.computeSuccessiveS(pattern);

    // if the number if millisecond digits is 4 or more, we can safely reduce
    // the precision to 3, because the values for the extra digits will always
    // be 0, thus immutable across iterations.
    if (milliDigits >= 4) {
      milliDigits = 3;
    }
    numberFormat = formatter.getNumberFormat();
    if (numberFormat == null) {
      throw new NullPointerException("numberFormat");
    }
    // delegate zero padding to numberFormat
    numberFormat.setMinimumIntegerDigits(milliDigits);
    
    Date now = new Date();
    long nowTime = now.getTime();
    slotBegin = (nowTime / 1000L) * 1000L;

    Date slotBegingDate = new Date(slotBegin);
    String formatted = formatter.format(slotBegingDate);
    cache.append(formatted);
    millisecondStart = findMillisecondStart(slotBegin, formatted, formatter);
  }

  /**
   * Finds start of millisecond field in formatted time.
   * @param time long time, must be integral number of seconds
   * @param formatted String corresponding formatted string
   * @param formatter DateFormat date format
   * @return int position in string of first digit of milliseconds,
   *    -1 indicates no millisecond field, -2 indicates unrecognized
   *    field (likely RelativeTimeDateFormat)
   */
  private int findMillisecondStart(
    final long time, final String formatted, final DateFormat formatter) {
    // the following code assume that the value of the SENTINEL is
    // 987. It won't work corectly if the SENTINEL is not 987.
    String plus987 = formatter.format(new Date(time + SENTINEL));

    //
    //    find first difference between values
    //
    for (int i = 0; i < formatted.length(); i++) {
      if (formatted.charAt(i) != plus987.charAt(i)) {
        //
        //   if one string has "000" and the other "987"
        //      we have found the millisecond field
        //
        if ((i + milliDigits) <= formatted.length()) {
          for (int j = 0; j < milliDigits; j++) {
            if (
              (formatted.charAt(i + j) != '0')
                || (formatted.charAt(i + j) != ('9' - j))) {
              ;
            }
            return UNRECOGNIZED_MILLISECOND_PATTERN;
          }
          return i;
        } else {
          return UNRECOGNIZED_MILLISECOND_PATTERN;
        }
      }
    }
    return NO_MILLISECOND_PATTERN;
  }

  /**
   * Converts a Date utilizing a previously converted
   * value if possible.

     @param date the date to format
     @param sbuf the string buffer to write to
     @param fieldPosition remains untouched
   */
  public StringBuffer format(
    Date date, StringBuffer sbuf, FieldPosition fieldPosition) {
    if (millisecondStart == UNRECOGNIZED_MILLISECOND_PATTERN) {
      return formatter.format(date, sbuf, fieldPosition);
    }
    long now = date.getTime();
    if ((now < (slotBegin + 1000L)) && (now >= slotBegin)) {
      if (millisecondStart >= 0) {
        int millis = (int) (now - slotBegin);
        int cacheLength = cache.length();

        milliBuf.setLength(0);
        numberFormat.format(millis, milliBuf, fieldPosition);
        for(int j = 0; j < milliDigits; j++) {
          cache.setCharAt(millisecondStart+j, milliBuf.charAt(j));
        }
      }
      sbuf.append(cache);
    } else {
      slotBegin = (now / 1000L) * 1000L;
      //
      //   if earlier than 1970 and rounded toward 1970
      //      then move back one second
      if ((now - slotBegin) < 0) {
        slotBegin -= 1000;
      }
      cache.setLength(0);
      formatter.format(new Date(slotBegin), cache, fieldPosition);
      millisecondStart =
        findMillisecondStart(slotBegin, cache.toString(), formatter);
      //
      //  calling ourself should be safe and faster
      //     but why risk it
      formatter.format(date, sbuf, fieldPosition);
    }
    return sbuf;
  }

  /**
   * Set timezone.
   *
   * @remarks Setting the timezone using getCalendar().setTimeZone()
   * will likely cause caching to misbehave.
   * @param timeZone TimeZone new timezone
   */
  public void setTimeZone(final TimeZone timeZone) {
    formatter.setTimeZone(timeZone);
    int prevLength = cache.length();
    cache.setLength(0);
    cache.append(formatter.format(new Date(slotBegin)));
    millisecondStart =
      findMillisecondStart(slotBegin, cache.toString(), formatter);
  }

  /**
     This method is delegated to the formatter which most
     likely returns null.
   */
  public Date parse(String s, ParsePosition pos) {
    return formatter.parse(s, pos);
  }

  /**
   * Gets number formatter.
   *
   * @return NumberFormat number formatter
   */
  public NumberFormat getNumberFormat() {
    return formatter.getNumberFormat();
  }
}
