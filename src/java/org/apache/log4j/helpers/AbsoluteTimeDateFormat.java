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

import java.util.TimeZone;
import java.text.SimpleDateFormat;

/**
   Formats a {@link Date} in the format "HH:mm:ss,SSS" for example,
   "15:49:37,459".
   
   @author Ceki G&uuml;lc&uuml;
   @author Andrew Vajoczki    

   @since 0.7.5
*/
public class AbsoluteTimeDateFormat extends SimpleDateFormat {

  /**
     String constant used to specify {@link
     org.apache.log4j.helpers.AbsoluteTimeDateFormat} in layouts. Current
     value is <b>ABSOLUTE</b>.  */
  public static final String ABS_TIME_DATE_FORMAT = "ABSOLUTE";

  public static final String ABS_TIME_DATE_PATTERN = "HH:mm:ss,SSS";
  
  /**
     String constant used to specify {@link
     org.apache.log4j.helpers.DateTimeDateFormat} in layouts.  Current
     value is <b>DATE</b>.
  */
  public static final String DATE_AND_TIME_DATE_FORMAT = "DATE";

  /**
     String constant used to specify {@link
     org.apache.log4j.helpers.ISO8601DateFormat} in layouts. Current
     value is <b>ISO8601</b>.
  */
  public static final String ISO8601_DATE_FORMAT = "ISO8601";

  /**
   *  Constructs a AbsoluteTimeDateFormat using the default locale and time zone.
   */
  public AbsoluteTimeDateFormat() {
    super("HH:mm:ss,SSS");
  }

  /**
   *  Constructs a AbsoluteTimeDateFormat using the default locale and 
   *      a given time zone.
   *
   *  @param timeZone the given time zone.
   */
  public AbsoluteTimeDateFormat(final TimeZone timeZone) {
    super("HH:mm:ss,SSS");
    setTimeZone(timeZone);
  }

}
