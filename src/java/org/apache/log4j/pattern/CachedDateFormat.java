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

import java.util.Date;
import java.util.TimeZone;


/**
 * Caches the results of a DateFormat.
 *  @author Curt Arnold
 *  @since 1.3
 */
final class CachedDateFormat extends DateFormat {
  private static final int BAD_PATTERN = -1;
  private static final int NO_MILLISECONDS = -2;
  
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
  
 

  public CachedDateFormat(DateFormat dateFormat) {
    if (dateFormat == null) {
      throw new IllegalArgumentException("dateFormat cannot be null");
    }
    formatter = dateFormat;
    
    numberFormat = new DecimalFormat();
    // numberFormat will zero as necessary
    numberFormat.setMinimumIntegerDigits(JVM_MAX_MILLI_DIGITS);
    
    Date now = new Date();
    long nowTime = now.getTime();
    slotBegin = (nowTime / 1000L) * 1000L;

    slotBeginDate = new Date(slotBegin);
    String formatted = formatter.format(slotBeginDate);
    cache.append(formatted);
    millisecondStart = findMillisecondStart(slotBegin, formatted, formatter);
    //System.out.println("millisecondStart="+millisecondStart);
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
    String plus987 = formatter.format(new Date(time + 987));

    //System.out.println("-formatted="+formatted);
    //System.out.println("plus987="+plus987);
    // find first difference between values
    for (int i = 0; i < formatted.length(); i++) {
      if (formatted.charAt(i) != plus987.charAt(i)) {
        //   if one string has "000" and the other "987"
        //      we have found the millisecond field
        if (i + 3 <= formatted.length() 
            && "000".equals(formatted.substring(i, i + JVM_MAX_MILLI_DIGITS))  
            && "987".equals(plus987.substring(i, i + JVM_MAX_MILLI_DIGITS))) {
          return i;
        } else {
          return BAD_PATTERN;  
        }
      }
    }
    return  NO_MILLISECONDS;
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

      // If there are NO_MILLISECONDS we don't bother computing the millisecs.
      if (millisecondStart >= 0) {
        int millis = (int) (now - slotBegin);
        int cacheLength = cache.length();

        milliBuf.setLength(0);
        numberFormat.format(millis, milliBuf, fieldPosition);
        //System.out.println("milliBuf="+milliBuf);
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
