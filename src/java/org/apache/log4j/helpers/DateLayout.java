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

package org.apache.log4j.helpers;

import org.apache.log4j.Layout;
import org.apache.log4j.helpers.AbsoluteTimeDateFormat;
import org.apache.log4j.helpers.DateTimeDateFormat;
import org.apache.log4j.helpers.ISO8601DateFormat;
import org.apache.log4j.helpers.RelativeTimeDateFormat;
import org.apache.log4j.spi.LoggingEvent;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.TimeZone;


/**
   This abstract layout takes care of all the date related options and
   formatting work.

   @author Ceki G&uuml;lc&uuml;
 */
public abstract class DateLayout extends Layout {
  /**
     String constant designating no time information. Current value of
     this constant is <b>NULL</b>.

  */
  public static final String NULL_DATE_FORMAT = "NULL";

  /**
     String constant designating relative time. Current value of
     this constant is <b>RELATIVE</b>.
   */
  public static final String RELATIVE_TIME_DATE_FORMAT = "RELATIVE";

  /**
     @deprecated Options are now handled using the JavaBeans paradigm.
     This constant is not longer needed and will be removed in the
     <em>near</em> term.
  */
  public static final String DATE_FORMAT_OPTION = "DateFormat";

  /**
     @deprecated Options are now handled using the JavaBeans paradigm.
     This constant is not longer needed and will be removed in the
     <em>near</em> term.
  */
  public static final String TIMEZONE_OPTION = "TimeZone";
  protected FieldPosition pos = new FieldPosition(0);
  private String timeZoneID;
  private String dateFormatOption;
  protected DateFormat dateFormat;
  protected Date date = new Date();

  /**
    The value of the <b>DateFormat</b> option should be either an
    argument to the constructor of {@link SimpleDateFormat} or one of
    the srings "NULL", "RELATIVE", "ABSOLUTE", "DATE" or "ISO8601.
   */
  public void setDateFormat(String dateFormat) {
    if (dateFormat != null) {
      dateFormatOption = dateFormat;
    }

    setDateFormat(dateFormatOption, TimeZone.getDefault());
  }

  /**
     Returns value of the <b>DateFormat</b> option.
   */
  public String getDateFormat() {
    return dateFormatOption;
  }

  /**
    The <b>TimeZoneID</b> option is a time zone ID string in the format
    expected by the {@link TimeZone#getTimeZone} method.
   */
  public void setTimeZone(String timeZone) {
    this.timeZoneID = timeZone;
  }

  /**
     Returns value of the <b>TimeZone</b> option.
   */
  public String getTimeZone() {
    return timeZoneID;
  }

  public void activateOptions() {
    setDateFormat(dateFormatOption);

    if ((timeZoneID != null) && (dateFormat != null)) {
      dateFormat.setTimeZone(TimeZone.getTimeZone(timeZoneID));
    }
  }

  public void dateFormat(StringBuffer buf, LoggingEvent event) {
    if (dateFormat != null) {
      date.setTime(event.timeStamp);
      dateFormat.format(date, buf, this.pos);
      buf.append(' ');
    }
  }

  /**
     Sets the {@link DateFormat} used to format time and date in the
     zone determined by <code>timeZone</code>.
   */
  public void setDateFormat(DateFormat dateFormat, TimeZone timeZone) {
    this.dateFormat = dateFormat;
    this.dateFormat.setTimeZone(timeZone);
  }

  /**
     Sets the DateFormat used to format date and time in the time zone
     determined by <code>timeZone</code> parameter. The {@link DateFormat} used
     will depend on the <code>dateFormatType</code>.

     <p>The recognized types are {@link #NULL_DATE_FORMAT}, {@link
     #RELATIVE_TIME_DATE_FORMAT} {@link
     AbsoluteTimeDateFormat#ABS_TIME_DATE_FORMAT}, {@link
     AbsoluteTimeDateFormat#DATE_AND_TIME_DATE_FORMAT} and {@link
     AbsoluteTimeDateFormat#ISO8601_DATE_FORMAT}. If the
     <code>dateFormatType</code> is not one of the above, then the
     argument is assumed to be a date pattern for {@link
     SimpleDateFormat}.
  */
  public void setDateFormat(String dateFormatType, TimeZone timeZone) {
    if (dateFormatType == null) {
      this.dateFormat = null;

      return;
    }

    if (dateFormatType.equalsIgnoreCase(NULL_DATE_FORMAT)) {
      this.dateFormat = null;
    } else if (dateFormatType.equalsIgnoreCase(RELATIVE_TIME_DATE_FORMAT)) {
      this.dateFormat = new RelativeTimeDateFormat();
    } else if (
      dateFormatType.equalsIgnoreCase(
          AbsoluteTimeDateFormat.ABS_TIME_DATE_FORMAT)) {
      this.dateFormat = new AbsoluteTimeDateFormat(timeZone);
    } else if (
      dateFormatType.equalsIgnoreCase(
          AbsoluteTimeDateFormat.DATE_AND_TIME_DATE_FORMAT)) {
      this.dateFormat = new DateTimeDateFormat(timeZone);
    } else if (
      dateFormatType.equalsIgnoreCase(
          AbsoluteTimeDateFormat.ISO8601_DATE_FORMAT)) {
      this.dateFormat = new ISO8601DateFormat(timeZone);
    } else {
      this.dateFormat = new SimpleDateFormat(dateFormatType);
      this.dateFormat.setTimeZone(timeZone);
    }
  }
}
