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
import java.util.TimeZone;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.DateFormat;

/**
   Formats a {@link Date} by printing the number of milliseconds
   elapsed since the construction of the format.  This is the fastest
   printing DateFormat in the package.
   
   @author Ceki G&uuml;lc&uuml;
   @since 0.7.5
*/
public class RelativeTimeDateFormat extends DateFormat {

  protected final long startTime;

  public
  RelativeTimeDateFormat() {
    this.startTime = System.currentTimeMillis();
  }
  
  /**
     Appends to <code>sbuf</code> the number of milliseconds elapsed
     since the start of the application. 
     
     @since 0.7.5
  */
  public
  StringBuffer format(Date date, StringBuffer sbuf,
		      FieldPosition fieldPosition) {
    //System.err.println(":"+ date.getTime() + " - " + startTime);
    return sbuf.append((date.getTime() - startTime));
  }

  /**
     This method does not do anything but return <code>null</code>.
   */
  public
  Date parse(java.lang.String s, ParsePosition pos) {
    return null;
  }  

  /**
   * Sets the timezone.
   * Ignored by this formatter, but intercepted to prevent
   * NullPointerException in superclass.
   * @param tz TimeZone timezone
   */
  public void setTimeZone(final TimeZone tz) {
  }
  
}
