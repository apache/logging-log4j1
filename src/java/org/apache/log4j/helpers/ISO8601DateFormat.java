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

// Contributors: Arndt Schoenewald <arndt@ibm23093i821.mc.schoenewald.de>

/**
   Formats a {@link Date} in the format "YYYY-mm-dd HH:mm:ss,SSS" for example
   "1999-11-27 15:49:37,459".

   <p>Refer to the <a
   href=http://www.cl.cam.ac.uk/~mgk25/iso-time.html>summary of the
   International Standard Date and Time Notation</a> for more
   information on this format.

   @author Ceki G&uuml;lc&uuml;
   @author Andrew Vajoczki

   @since 0.7.5
*/
public class ISO8601DateFormat extends SimpleDateFormat {

  /**
   *  Constructs an ISOTimeDateFormat using the default locale and time zone.
   */
  public ISO8601DateFormat() {
     super("yyyy-MM-dd HH:mm:ss,SSS");
  }

  /**
   *  Constructs an ISO8601DateFormat using the default locale and 
   *      a given time zone.
   *
   *  @param timeZone the given time zone.
   */
  public ISO8601DateFormat(final TimeZone timeZone) {
     super("yyyy-MM-dd HH:mm:ss,SSS");
     setTimeZone(timeZone);
  }
}

