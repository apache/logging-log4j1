/*
 * ============================================================================
 *                   The Apache Software License, Version 1.1
 * ============================================================================
 *
 *    Copyright (C) 1999 The Apache Software Foundation. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modifica-
 * tion, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of  source code must  retain the above copyright  notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any, must
 *    include  the following  acknowledgment:  "This product includes  software
 *    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
 *    Alternately, this  acknowledgment may  appear in the software itself,  if
 *    and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "log4j" and  "Apache Software Foundation"  must not be used to
 *    endorse  or promote  products derived  from this  software without  prior
 *    written permission. For written permission, please contact
 *    apache@apache.org.
 *
 * 5. Products  derived from this software may not  be called "Apache", nor may
 *    "Apache" appear  in their name,  without prior written permission  of the
 *    Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 * APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 * DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 * ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 * (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * This software  consists of voluntary contributions made  by many individuals
 * on  behalf of the Apache Software  Foundation.  For more  information on the
 * Apache Software Foundation, please see <http://www.apache.org/>.
 *
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
      new RollingCalendar(GMT_TIMEZONE, Locale.ENGLISH);

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
