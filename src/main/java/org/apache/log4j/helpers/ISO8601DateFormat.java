/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import java.util.Calendar;
import java.util.TimeZone;
import java.util.Date;
import java.text.FieldPosition;
import java.text.ParsePosition;

// Contributors: Arndt Schoenewald <arndt@ibm23093i821.mc.schoenewald.de>

/**
   Formats a {@link Date} in the format "yyyy-MM-dd HH:mm:ss,SSS" for example
   "1999-11-27 15:49:37,459".

   <p>Refer to the <a
   href=http://www.cl.cam.ac.uk/~mgk25/iso-time.html>summary of the
   International Standard Date and Time Notation</a> for more
   information on this format.

   @author Ceki G&uuml;lc&uuml;
   @author Andrew Vajoczki

   @since 0.7.5
*/
public class ISO8601DateFormat extends AbsoluteTimeDateFormat {
  private static final long serialVersionUID = -759840745298755296L;

  public
  ISO8601DateFormat() {
  }

  public
  ISO8601DateFormat(TimeZone timeZone) {
    super(timeZone);
  }

  static private long   lastTime;
  static private char[] lastTimeString = new char[20];

  /**
     Appends a date in the format "YYYY-mm-dd HH:mm:ss,SSS"
     to <code>sbuf</code>. For example: "1999-11-27 15:49:37,459".

     @param sbuf the <code>StringBuffer</code> to write to
  */
  public
  StringBuffer format(Date date, StringBuffer sbuf,
		      FieldPosition fieldPosition) {

    long now = date.getTime();
    int millis = (int)(now % 1000);

    if ((now - millis) != lastTime || lastTimeString[0] == 0) {
      // We reach this point at most once per second
      // across all threads instead of each time format()
      // is called. This saves considerable CPU time.

      calendar.setTime(date);

      int start = sbuf.length();

      int year =  calendar.get(Calendar.YEAR);
      sbuf.append(year);

      String month;
      switch(calendar.get(Calendar.MONTH)) {
      case Calendar.JANUARY: month = "-01-"; break;
      case Calendar.FEBRUARY: month = "-02-";  break;
      case Calendar.MARCH: month = "-03-"; break;
      case Calendar.APRIL: month = "-04-";  break;
      case Calendar.MAY: month = "-05-"; break;
      case Calendar.JUNE: month = "-06-";  break;
      case Calendar.JULY: month = "-07-"; break;
      case Calendar.AUGUST: month = "-08-";  break;
      case Calendar.SEPTEMBER: month = "-09-"; break;
      case Calendar.OCTOBER: month = "-10-"; break;
      case Calendar.NOVEMBER: month = "-11-";  break;
      case Calendar.DECEMBER: month = "-12-";  break;
      default: month = "-NA-"; break;
      }
      sbuf.append(month);

      int day = calendar.get(Calendar.DAY_OF_MONTH);
      if(day < 10)
	sbuf.append('0');
      sbuf.append(day);

      sbuf.append(' ');

      int hour = calendar.get(Calendar.HOUR_OF_DAY);
      if(hour < 10) {
	sbuf.append('0');
      }
      sbuf.append(hour);
      sbuf.append(':');

      int mins = calendar.get(Calendar.MINUTE);
      if(mins < 10) {
	sbuf.append('0');
      }
      sbuf.append(mins);
      sbuf.append(':');

      int secs = calendar.get(Calendar.SECOND);
      if(secs < 10) {
	sbuf.append('0');
      }
      sbuf.append(secs);

      sbuf.append(',');

      // store the time string for next time to avoid recomputation
      sbuf.getChars(start, sbuf.length(), lastTimeString, 0);
      lastTime = now - millis;
    }
    else {
      sbuf.append(lastTimeString);
    }


    if (millis < 100)
      sbuf.append('0');
    if (millis < 10)
      sbuf.append('0');

    sbuf.append(millis);
    return sbuf;
  }

  /**
    This method does not do anything but return <code>null</code>.
   */
  public
  Date parse(java.lang.String s, ParsePosition pos) {
    return null;
  }
}

