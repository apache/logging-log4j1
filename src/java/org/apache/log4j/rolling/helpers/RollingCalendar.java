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

package org.apache.log4j.rolling.helpers;

import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;


/**

/**
 * RollingCalendar is a helper class to DailyRollingFileAppender or similar
 * timed-based rolling policies. Given a periodicity type and the current
 * time, it computes the start of the next interval.
 *
 * @author Ceki G&uuml;lc&uuml;
 *
 * */
public class RollingCalendar extends GregorianCalendar {
  static final Logger logger = Logger.getLogger(RollingCalendar.class);

  // The gmtTimeZone is used only in computeCheckPeriod() method.
  static final TimeZone GMT_TIMEZONE = TimeZone.getTimeZone("GMT");

  // The code assumes that the following constants are in a increasing
  // sequence.
  public static final int TOP_OF_TROUBLE = -1;
  public static final int TOP_OF_SECOND = 0;
  public static final int TOP_OF_MINUTE = 1;
  public static final int TOP_OF_HOUR = 2;
  public static final int HALF_DAY = 3;
  public static final int TOP_OF_DAY = 4;
  public static final int TOP_OF_WEEK = 5;
  public static final int TOP_OF_MONTH = 6;
  int type = TOP_OF_TROUBLE;

  public RollingCalendar() {
    super();
  }

  public RollingCalendar(TimeZone tz, Locale locale) {
    super(tz, locale);
  }

  public void init(String datePattern) {
    type = computeTriggeringPeriod(datePattern);
  }

  public void setType(int type) {
    this.type = type;
  }

  public long getNextCheckMillis(Date now) {
    return getNextCheckDate(now).getTime();
  }

  // This method computes the roll over period by looping over the
  // periods, starting with the shortest, and stopping when the r0 is
  // different from from r1, where r0 is the epoch formatted according
  // the datePattern (supplied by the user) and r1 is the
  // epoch+nextMillis(i) formatted according to datePattern. All date
  // formatting is done in GMT and not local format because the test
  // logic is based on comparisons relative to 1970-01-01 00:00:00
  // GMT (the epoch).
  public int computeTriggeringPeriod(String datePattern) {
    RollingCalendar rollingCalendar =
      new RollingCalendar(GMT_TIMEZONE, Locale.getDefault());

    // set sate to 1970-01-01 00:00:00 GMT
    Date epoch = new Date(0);

    if (datePattern != null) {
      for (int i = TOP_OF_SECOND; i <= TOP_OF_MONTH; i++) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);
        simpleDateFormat.setTimeZone(GMT_TIMEZONE); // do all date formatting in GMT

        String r0 = simpleDateFormat.format(epoch);
        rollingCalendar.setType(i);

        Date next = new Date(rollingCalendar.getNextCheckMillis(epoch));
        String r1 = simpleDateFormat.format(next);

        //System.out.println("Type = "+i+", r0 = "+r0+", r1 = "+r1);
        if ((r0 != null) && (r1 != null) && !r0.equals(r1)) {
          return i;
        }
      }
    }

    return TOP_OF_TROUBLE; // Deliberately head for trouble...
  }

  public void printPeriodicity() {
    switch (type) {
    case TOP_OF_SECOND:
      logger.debug("Rollover every second.");

      break;

    case TOP_OF_MINUTE:
      logger.debug("Rollover every minute.");

      break;

    case TOP_OF_HOUR:
      logger.debug("Rollover at the top of every hour.");

      break;

    case HALF_DAY:
      logger.debug("Rollover at midday and midnight.");

      break;

    case TOP_OF_DAY:
      logger.debug("Rollover at midnight.");

      break;

    case TOP_OF_WEEK:
      logger.debug("Rollover at the start of week.");

      break;

    case TOP_OF_MONTH:
      logger.debug("Rollover at start of every month.");

      break;

    default:
      logger.warn("Unknown periodicity.");
    }
  }

  public Date getNextCheckDate(Date now) {
    this.setTime(now);

    switch (type) {
    case TOP_OF_SECOND:
      this.set(Calendar.MILLISECOND, 0);
      this.add(Calendar.SECOND, 1);

      break;

    case TOP_OF_MINUTE:
      this.set(Calendar.SECOND, 0);
      this.set(Calendar.MILLISECOND, 0);
      this.add(Calendar.MINUTE, 1);

      break;

    case TOP_OF_HOUR:
      this.set(Calendar.MINUTE, 0);
      this.set(Calendar.SECOND, 0);
      this.set(Calendar.MILLISECOND, 0);
      this.add(Calendar.HOUR_OF_DAY, 1);

      break;

    case HALF_DAY:
      this.set(Calendar.MINUTE, 0);
      this.set(Calendar.SECOND, 0);
      this.set(Calendar.MILLISECOND, 0);

      int hour = get(Calendar.HOUR_OF_DAY);

      if (hour < 12) {
        this.set(Calendar.HOUR_OF_DAY, 12);
      } else {
        this.set(Calendar.HOUR_OF_DAY, 0);
        this.add(Calendar.DAY_OF_MONTH, 1);
      }

      break;

    case TOP_OF_DAY:
      this.set(Calendar.HOUR_OF_DAY, 0);
      this.set(Calendar.MINUTE, 0);
      this.set(Calendar.SECOND, 0);
      this.set(Calendar.MILLISECOND, 0);
      this.add(Calendar.DATE, 1);

      break;

    case TOP_OF_WEEK:
      this.set(Calendar.DAY_OF_WEEK, getFirstDayOfWeek());
      this.set(Calendar.HOUR_OF_DAY, 0);
      this.set(Calendar.SECOND, 0);
      this.set(Calendar.MILLISECOND, 0);
      this.add(Calendar.WEEK_OF_YEAR, 1);

      break;

    case TOP_OF_MONTH:
      this.set(Calendar.DATE, 1);
      this.set(Calendar.HOUR_OF_DAY, 0);
      this.set(Calendar.SECOND, 0);
      this.set(Calendar.MILLISECOND, 0);
      this.add(Calendar.MONTH, 1);

      break;

    default:
      throw new IllegalStateException("Unknown periodicity type.");
    }

    return getTime();
  }
}
