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
import java.text.DecimalFormat;
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
  private static final int BAD_PATTERN = -1;
  
  // We take advantage of structure of the sentinel, in particular
  // the incremental decrease in the digits 9, 8, and 7.
  private static final int SENTINEL = 987;
  
  // Given that the JVM precision is 1/1000 of a second, 3 digit millisecond
  // precision is the best we can ever expect.
  private static final int JVM_MAX_MILLI_DIGITS = 3;
  
  private DateFormat formatter;
  private int millisecondStart;
  private StringBuffer cache = new StringBuffer();
  private long slotBegin;
  private Date slotBeginDate;
  private int milliDigits;
  private StringBuffer milliBuf = new StringBuffer(JVM_MAX_MILLI_DIGITS);
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
    
    String cleanedPattern = CacheUtil.removeLiterals(pattern);
    milliDigits = CacheUtil.computeSuccessiveS(cleanedPattern);
   
    if(!CacheUtil.isPatternSafeForCaching(cleanedPattern)) {
      millisecondStart = BAD_PATTERN;
      return;
    }
    
    if(milliDigits == 0) {
      // millisecondStart value won't be used
      millisecondStart = 0;
    } else if (milliDigits < JVM_MAX_MILLI_DIGITS) {
      // we don't deal well with these
      millisecondStart = BAD_PATTERN;
    } else if(milliDigits >= JVM_MAX_MILLI_DIGITS) {
      // if the number if millisecond digits is 3 or more, we can safely reduce
      // the precision to 3, because the values for the extra digits will always
      // be 0, thus immutable across iterations.
      milliDigits = JVM_MAX_MILLI_DIGITS;
      numberFormat = new DecimalFormat();
      // Have numberFormat use of all available digits, it'll zero pad for
      // smaller numbers 
      numberFormat.setMinimumIntegerDigits(JVM_MAX_MILLI_DIGITS);
    
      Date now = new Date();
      long nowTime = now.getTime();
      slotBegin = (nowTime / 1000L) * 1000L;

      slotBeginDate = new Date(slotBegin);
      String formatted = formatter.format(slotBeginDate);
      cache.append(formatted);
      millisecondStart = findMillisecondStart(slotBegin, formatted, formatter);
    } 
    
//    if(millisecondStart == BAD_PATTERN) {
//      System.out.println("BAD PATTERN");
//    } else {
//      System.out.println("millisecondStart="+millisecondStart);
//    }
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
            if ((formatted.charAt(i + j) != '0')
                || (plus987.charAt(i + j) != ('9' - j))) {
              return BAD_PATTERN;  
            }
          }
          return i;
        } else {
          return BAD_PATTERN;
        }
      }
    }
    return BAD_PATTERN;
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
    if (millisecondStart == BAD_PATTERN) {
      return formatter.format(date, sbuf, fieldPosition);
    }
    long now = date.getTime();
    if ((now < (slotBegin + 1000L)) && (now >= slotBegin)) {
      //System.out.println("Using cached val:"+date);

      // caching is safe only for milliDigits == 3, if milliDigits == 0
      // we don't have to bother  with milliseonds at all.
      if (millisecondStart >= 0 && milliDigits == JVM_MAX_MILLI_DIGITS) {
        int millis = (int) (now - slotBegin);
        int cacheLength = cache.length();

        milliBuf.setLength(0);
        numberFormat.format(millis, milliBuf, fieldPosition);
        for(int j = 0; j < JVM_MAX_MILLI_DIGITS; j++) {
          cache.setCharAt(millisecondStart+j, milliBuf.charAt(j));
        }
      }
    } else {
      //System.out.println("Refreshing the cache: "+date+","+(date.getTime()%1000));
      slotBegin = (now / 1000L) * 1000L;
      int prevLength = cache.length();
      cache.setLength(0);
      formatter.format(date, cache, fieldPosition);
     
      //   if the length changed then
      //      recalculate the millisecond position
      if (cache.length() != prevLength && (milliDigits > 0)) {
        //System.out.println("Recomputing cached len changed oldLen="+prevLength
        //      +", newLen="+cache.length());
        //
        //    format the previous integral second
        StringBuffer tempBuffer = new StringBuffer(cache.length());
        slotBeginDate.setTime(slotBegin);
        formatter.format(slotBeginDate, tempBuffer, fieldPosition);
        //
        //    detect the start of the millisecond field
        millisecondStart = findMillisecondStart(slotBegin,
                                                tempBuffer.toString(),
                                                formatter);
      }
    }
    return sbuf.append(cache);
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
    // invalidate the cache
    slotBegin = 0;
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
