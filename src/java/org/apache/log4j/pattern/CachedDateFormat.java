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
 * CachedDateFormat optimizes the performance of a wrapped
 * DateFormat.  The implementation is not thread-safe.
 * If the millisecond pattern is not recognized,
 * the class will only use the cache if the 
 * same value is requested.
 *
 *  @author Curt Arnold
 *  @since 1.3
 */
final class CachedDateFormat extends DateFormat {
  /*
   *  Constant used to represent that there was no change
   *  observed when changing the millisecond count.
   */
  public static final int NO_MILLISECONDS = -2;

  /**
   *  Supported digit set.  If the wrapped DateFormat uses
   *  a different unit set, the millisecond pattern
   *  will not be recognized and duplicate requests
   *  will use the cache.
   */
  private final static String digits = "0123456789";
  
  /*
   *  Constant used to represent that there was an 
   *  observed change, but was an expected change.
   */
  public static final int UNRECOGNIZED_MILLISECONDS = -1;
  
  /**
   *  First magic number used to detect the millisecond position.
   */
  private static final int magic1 = 654;

  /**
   *  Expected representation of first magic number.
   */
  private static final String magicString1 = "654";

  /**
   *  Second magic number used to detect the millisecond position.
   */
  private static final int magic2 = 987;

  /**
   *  Expected representation of second magic number.
   */
  private static final String magicString2 = "987";


  /**
   *  Expected representation of 0 milliseconds.
   */
  private static final String zeroString = "000";
  
  /**
   *   Wrapped formatter.
   */
  private final DateFormat formatter;
  
  /**
   *  Index of initial digit of millisecond pattern or 
   *   UNRECOGNIZED_MILLISECONDS or NO_MILLISECONDS.
   */
  private int millisecondStart;
  
  /**
   *  Cache of previous conversion.
   */
  private StringBuffer cache = new StringBuffer(50);
  
  /**
   *  Integral second preceding the previous convered Date.
   */
  private long slotBegin;
  
  /**
   *  Maximum validity period for the cache.  
   *  Typically 1, use cache for duplicate requests only, or
   *  1000, use cache for requests within the same integral second.
   */
  private final int expiration;
  
  /**
   *  Date requested in previous conversion.
   */
  private long previousTime;
  
  /**
   *   Scratch date object used to minimize date object creation.
   */
   private final Date tmpDate = new Date(0);
 

  /**
   *  Creates a new CachedDateFormat object.
   *  @param dateFormat Date format, may not be null.
   *  @param expiration maximum cached range in milliseconds.
   *    If the dateFormat is known to be incompatible with the
   *      caching algorithm, use a value of 0 to totally disable
   *      caching or 1 to only use cache for duplicate requests.
   */
  public CachedDateFormat(final DateFormat dateFormat,
          final int expiration) {
    if (dateFormat == null) {
      throw new IllegalArgumentException("dateFormat cannot be null");
    }
    formatter = dateFormat;
    this.expiration = expiration;
    
    Date now = new Date();
    long nowTime = now.getTime();
    slotBegin = (nowTime / 1000L) * 1000L;
    //
    //   if now is before 1970 and slot begin
    //     was truncated forward
    //
    if (slotBegin > nowTime) {
        slotBegin -= 1000;
    }

    String formatted = formatter.format(now);
    cache.append(formatted);
    previousTime = nowTime;
    millisecondStart = findMillisecondStart(nowTime, formatted, formatter);
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
  public static int findMillisecondStart(
    final long time, final String formatted, final DateFormat formatter) {
    
    long slotBegin = (time / 1000) * 1000;
    if (slotBegin > time) {
       slotBegin -= 1000;
    }
    int millis = (int) (time - slotBegin);
    
    int magic = magic1;
    String magicString = magicString1;
    if (millis == magic1) {
        magic = magic2;
        magicString = magicString2;
    }
    String plusMagic = formatter.format(new Date(slotBegin + magic));
    
    /**
     *   If the string lengths differ then 
     *      we can't use the cache except for duplicate requests.
     */
    if (plusMagic.length() != formatted.length()) {
        return UNRECOGNIZED_MILLISECONDS;
    } else {
        // find first difference between values
       for (int i = 0; i < formatted.length(); i++) {
          if (formatted.charAt(i) != plusMagic.charAt(i)) {
             //
             //   determine the expected digits for the base time
             StringBuffer formattedMillis = new StringBuffer("ABC");
             millisecondFormat(millis, formattedMillis, 0);
             
             String plusZero = formatter.format(new Date(slotBegin));

             //   If the next 3 characters match the magic
             //      string and the expected string
             if (plusZero.length() == formatted.length()
                && magicString.regionMatches(0, plusMagic, i, magicString.length()) 
                && formattedMillis.toString().regionMatches(0, formatted, i, magicString.length())
                && zeroString.regionMatches(0, plusZero, i, zeroString.length())) {
                return i;
             } else {
                return UNRECOGNIZED_MILLISECONDS;
            }
          }
       }
    }
    return  NO_MILLISECONDS;
  }

  /**
   * Formats a Date into a date/time string.
   *
   *  @param date the date to format
   *  @param sbuf the string buffer to write to
   *  @param fieldPosition remains untouched
   */
  public StringBuffer format(
    Date date, StringBuffer sbuf, FieldPosition fieldPosition){
       format(date.getTime(), sbuf);
    return sbuf;
  }
   
  /**
   * Formats a millisecond count into a date/time string.
   *
   *  @param now Number of milliseconds after midnight 1 Jan 1970 GMT.
   *  @param sbuf the string buffer to write to
   */
  public StringBuffer format(long now, 
          StringBuffer buf ){
    
    //
    // If an identical request, append the buffer and return.
    //
    if (now == previousTime) {
         buf.append(cache);
         return buf;
    }
    
    //
    // If not a recognized millisecond pattern,
    // use the wrapped formatter to update the cache
    // and append the cache to the buffer.
    //
    if (millisecondStart != UNRECOGNIZED_MILLISECONDS) {
    
        //
        //    If the requested time is within the same integral second
        //       as the last request and a shorter expiration was not requested.
        if (now < slotBegin + expiration
            && now >= slotBegin
            && now < slotBegin + 1000L) {
        
            // 
            //    if there was a millisecond field then update it
            //
            if (millisecondStart >= 0 ) {
                millisecondFormat((int) (now - slotBegin), cache, millisecondStart);
            }
            previousTime = now;
            buf.append(cache);
            return buf;
        }
    }

    
    //
    //  could not use previous value.  
    //    Call underlying formatter to format date.
    cache.setLength(0);
    tmpDate.setTime(now);
    cache.append(formatter.format(tmpDate));
    buf.append(cache);
    previousTime = now;
    //
    //   find the start of the millisecond field again
    //      if we had previously been able to find it.
    if (millisecondStart != UNRECOGNIZED_MILLISECONDS) {
        millisecondStart = findMillisecondStart(now, cache.toString(), formatter);
    }
    return buf;
  }

  /**
   *   Formats a count of milliseconds (0-999) into a numeric representation.
   *   @param millis Millisecond coun between 0 and 999.
   *   @buf String buffer, may not be null.
   *   @offset Starting position in buffer, the length of the
   *       buffer must be at least offset + 3.
   */
   private static void millisecondFormat(final int millis, 
       final StringBuffer buf,
       final int offset) {
             buf.setCharAt(offset,
                 digits.charAt( millis / 100));
             buf.setCharAt(offset + 1,
                  digits.charAt((millis / 10) % 10));
             buf.setCharAt(offset + 2,
                  digits.charAt(millis  % 10));
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
  
  /**
   * Gets maximum cache validity for the specified SimpleDateTime
   *    conversion pattern.
   *  @param pattern conversion pattern, may not be null.
   *  @returns Duration in milliseconds from an integral second
   *      that the cache will return consistent results.
   */
  public static int getMaximumCacheValidity(final String pattern) {
     //
     //   If there are more "S" in the pattern than just one "SSS"
     //      prevent caching anything more than duplicate requests
     //
     int firstS = pattern.indexOf('S');
     if (firstS >= 0 && firstS != pattern.lastIndexOf("SSS")) {
         return 1;
     }
     return 1000;
  }
}
