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

package org.apache.log4j.helpers;

import java.util.Date;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.TimeZone;

/**
 * Caches the results of a DateFormat.
 *  @author Curt Arnold
 *  @since 1.3
 */
public final class CachedDateFormat
    extends DateFormat {

  private DateFormat formatter;
  private int millisecondStart;
  private StringBuffer cache = new StringBuffer();
  private long previousTime;
  private NumberFormat numberFormat;
  private static final int UNRECOGNIZED_MILLISECOND_PATTERN = -2;
  private static final int NO_MILLISECOND_PATTERN = -1;

  public CachedDateFormat(final DateFormat formatter) {
    if (formatter == null) {
      throw new NullPointerException("formatter");
    }
    this.formatter = formatter;
    numberFormat = formatter.getNumberFormat();
    if (numberFormat == null) {
      throw new NullPointerException("numberFormat");
    }
    Date now = new Date();
    long nowTime = now.getTime();
    previousTime = (nowTime / 1000L) * 1000L;
    //
    //    if now is before 1970 and previousTime was truncated forward
    //       set cached time back one second
    if (nowTime - previousTime < 0) {
      previousTime -= 1000;
    }
    Date lastSecond = new Date(previousTime);
    String formatted = formatter.format(lastSecond);
    cache.append(formatted);
    millisecondStart = findMillisecondStart(previousTime, formatted,
                                            formatter);
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
  private static int findMillisecondStart(final long time,
                                          final String formatted,
                                          final DateFormat formatter) {
      String plus987 = formatter.format(new Date(time + 987));
      //
      //    find first difference between values
      //
      for (int i = 0; i < formatted.length(); i++) {
        if (formatted.charAt(i) != plus987.charAt(i)) {
          //
          //   if one string has "000" and the other "987"
          //      we have found the millisecond field
          //
          if (i + 3 <= formatted.length() 
              && formatted.substring(i, i + 3) == "000" 
              && plus987.substring(i, i + 3) == "987") {
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
  public
      StringBuffer format(Date date, StringBuffer sbuf,
                          FieldPosition fieldPosition) {

    if (millisecondStart == UNRECOGNIZED_MILLISECOND_PATTERN) {
      return formatter.format(date, sbuf, fieldPosition);
    }
    long now = date.getTime();
    if (now < previousTime + 1000L && now >= previousTime) {
      if (millisecondStart >= 0) {
        cache.delete(millisecondStart, millisecondStart + 3);
        int millis = (int) (now - previousTime);
        int cacheLength = cache.length();
        //
        //   append milliseconds to the end of the cache
        numberFormat.format(millis, cache, fieldPosition);
        int milliLength = cache.length() - cacheLength;
        //
        //   if it didn't belong at the end, then move it
        if (cacheLength != millisecondStart) {
          String milli = cache.substring(cacheLength);
          cache.setLength(cacheLength);
          cache.insert(millisecondStart, milli);
        }
        for (int i = milliLength; i < 3; i++) {
          cache.insert(millisecondStart, "0");
        }
      }
      sbuf.append(cache);
    } else {
      previousTime = (now / 1000L) * 1000L;
      //
      //   if earlier than 1970 and rounded toward 1970
      //      then move back one second
      if (now - previousTime < 0) {
        previousTime -= 1000;
      }
      cache.setLength(0);
      formatter.format(new Date(previousTime), cache, fieldPosition);
      millisecondStart = findMillisecondStart(previousTime, cache.toString(), formatter);
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
    cache.append(formatter.format(new Date(previousTime)));
    millisecondStart = findMillisecondStart(previousTime,
                                              cache.toString(),
                                              formatter);
  }

  /**
     This method is delegated to the formatter which most
     likely returns null.
   */
  public
      Date parse(String s, ParsePosition pos) {
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
