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
   Formats a {@link Date} in the format "dd MMM YYYY HH:mm:ss,SSS" for example,
   "06 Nov 1994 15:49:37,459".

   @author Ceki G&uuml;lc&uuml;
   @since 0.7.5
*/
public class DateTimeDateFormat extends SimpleDateFormat {

  /**
   *  Constructs a DateTimeDateFormat using the default locale and time zone.
   */
  public DateTimeDateFormat() {
    super("dd MMM yyyy HH:mm:ss,SSS");
  }

  /**
   *  Constructs a DateTimeDateFormat using the default locale and 
   *      a given time zone.
   *
   *  @param timeZone the given time zone.
   */
  public DateTimeDateFormat(final TimeZone timeZone) {
    super("dd MMM yyyy HH:mm:ss,SSS");
    setTimeZone(timeZone);
  }

}
