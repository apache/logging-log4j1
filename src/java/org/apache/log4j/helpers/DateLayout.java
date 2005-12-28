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

// Contributors: Christopher Williams
//               Mathias Bogaert
package org.apache.log4j.helpers;

import org.apache.log4j.Layout;
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
   @deprecated since 1.3
 */
abstract public class DateLayout extends Layout {

    /**
       String constant designating no time information. Current value of
       this constant is <b>NULL</b>.

    */
    public final static String NULL_DATE_FORMAT = "NULL";

    /**
       String constant designating relative time. Current value of
       this constant is <b>RELATIVE</b>.
     */
    public final static String RELATIVE_TIME_DATE_FORMAT = "RELATIVE";

    protected FieldPosition pos = new FieldPosition(0);

    /**
       @deprecated Options are now handled using the JavaBeans paradigm.
       This constant is not longer needed and will be removed in the
       <em>near</em> term.
    */
    final static public String DATE_FORMAT_OPTION = "DateFormat";

    /**
       @deprecated Options are now handled using the JavaBeans paradigm.
       This constant is not longer needed and will be removed in the
       <em>near</em> term.
    */
    final static public String TIMEZONE_OPTION = "TimeZone";

    private String timeZoneID;
    private String dateFormatOption;

    protected DateFormat dateFormat;
    protected Date date = new Date();


    /**
     * Instantiate a DateLayout object with in the ISO8601 format as the date
     * formatter.
     * */
    public DateLayout() {
    }

    /**
       Instantiate a DateLayout object using the local time zone. The
       DateFormat used will depend on the <code>dateFormatType</code>.

       <p>This constructor just calls the {@link #setDateFormat} method.
    */
    public DateLayout(final String dateFormatType) {
      this.setDateFormat(dateFormatType);
    }


    /**
       @deprecated Use the setter method for the option directly instead
       of the generic <code>setOption</code> method.
    */
    public
    String[] getOptionStrings() {
      return new String[] {DATE_FORMAT_OPTION, TIMEZONE_OPTION};
    }

    /**
       @deprecated Use the setter method for the option directly instead
       of the generic <code>setOption</code> method.
    */
    public
    void setOption(final String option, final String value) {
      if(option.equalsIgnoreCase(DATE_FORMAT_OPTION)) {
        dateFormatOption = value.toUpperCase();
      } else if(option.equalsIgnoreCase(TIMEZONE_OPTION)) {
        timeZoneID = value;
      }
    }

    /**
      The value of the <b>DateFormat</b> option should be either an
      argument to the constructor of {@link SimpleDateFormat} or one of
      the srings "NULL", "RELATIVE", "ABSOLUTE", "DATE" or "ISO8601.
     */
    public
    void setDateFormat(String dateFormat) {
      if (dateFormat != null) {
          dateFormatOption = dateFormat;
      }
      setDateFormat(dateFormatOption, TimeZone.getDefault());
    }



    /**
       Returns value of the <b>DateFormat</b> option.
     */
    public
    String getDateFormat() {
      return dateFormatOption;
    }

    /**
      The <b>TimeZoneID</b> option is a time zone ID string in the format
      expected by the {@link TimeZone#getTimeZone} method.
     */
    public
    void setTimeZone(String timeZone) {
      this.timeZoneID = timeZone;
    }

    /**
       Returns value of the <b>TimeZone</b> option.
     */
    public
    String getTimeZone() {
      return timeZoneID;
    }


  public void activateOptions() {
  }

    /**
       Sets the {@link DateFormat} used to format time and date in the
       zone determined by <code>timeZone</code>.
     */
    public
    void setDateFormat(DateFormat dateFormat, TimeZone timeZone) {
      this.dateFormat = dateFormat;
      this.dateFormat.setTimeZone(timeZone);
    }


  public void setDateFormat(final String dateFormatStr, final TimeZone timeZone) {
    if (dateFormatStr == null) {
      this.dateFormat = null;
      return;
    }

    if (!dateFormatStr.equalsIgnoreCase("NULL")) {
       if (dateFormatStr.equalsIgnoreCase(RELATIVE_TIME_DATE_FORMAT)) {
          this.dateFormat =  new RelativeTimeDateFormat();
       } else {
           if (dateFormatStr.equalsIgnoreCase(Constants.ABSOLUTE_FORMAT)) {
              dateFormat = new SimpleDateFormat(Constants.ABSOLUTE_TIME_PATTERN);
           } else if (dateFormatStr.equalsIgnoreCase(Constants.DATE_AND_TIME_FORMAT)) {
              dateFormat = new SimpleDateFormat(Constants.DATE_AND_TIME_PATTERN);
           } else if (dateFormatStr.equalsIgnoreCase(Constants.ISO8601_FORMAT)) {
              dateFormat = new SimpleDateFormat(Constants.ISO8601_PATTERN);
           } else {
              dateFormat = new SimpleDateFormat(dateFormatStr);
           }
           if (timeZone != null) {
              dateFormat.setTimeZone(timeZone);
           }
       }
    }
  }


    public
    void dateFormat(StringBuffer buf, LoggingEvent event) {
      if(dateFormat != null) {
        date.setTime(event.timeStamp);
        dateFormat.format(date, buf, this.pos);
        buf.append(' ');
      }
    }
}
