/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.APL file.
 */

package org.apache.log4j.helpers;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.Date;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.DateFormat;
import java.text.DateFormatSymbols;

// Contributors: Arndt Schoenewald <arndt@ibm23093i821.mc.schoenewald.de>

/**
   Formats a {@link Date} in the format "YYYY-mm-dd HH:mm:ss,SSS" for example
   "1999-11-27 15:49:37,459".

   <p>Refer to the <a
   href=http://www.cl.cam.ac.uk/~mgk25/iso-time.html>summary of the
   International Standard Date and Time Notation</a> for more
   infromation on this format.
   
   @author Ceki G&uuml;lc&uuml;
   
   @since 0.7.5
*/
public class ISO8601DateFormat extends AbsoluteTimeDateFormat {

  public
  ISO8601DateFormat() {
  }
      
  public
  ISO8601DateFormat(TimeZone timeZone) {
    super(timeZone);
  }
  
  /**
     Appends to <code>sbuf</code> the date in the format "YYYY-mm-dd 
     HH:mm:ss,SSS" for example, "1999-11-27 15:49:37,459".

     @param sbuf the string buffer to write to
  */
  public
  StringBuffer format(Date date, StringBuffer sbuf,
		      FieldPosition fieldPosition) {

    calendar.setTime(date);      

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
    return super.format(date, sbuf, fieldPosition);
  }

  /**
    This method does not do anything but return <code>null</code>.
   */
  public
  Date parse(java.lang.String s, ParsePosition pos) {
    return null;
  }  
}
