/*
 * Copyright 1999-2006 The Apache Software Foundation.
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
import java.util.Date;
import java.text.DateFormatSymbols;

/**
 * Formats a {@link Date} in the format "dd MMM yyyy HH:mm:ss,SSS" for example,
 * "06 Nov 1994 15:49:37,459".
 *
 * @author Ceki G&uuml;lc&uuml;
 * @since 0.7.5
 * @deprecated
 */
public class DateTimeDateFormat extends AbsoluteTimeDateFormat {
    /**
     * Equivalent format string for SimpleDateFormat.
     */
    private final static String PATTERN = "dd MMM yyyy HH:mm:ss,SSS";
    /** Short names for the months. */
    String[] shortMonths = new DateFormatSymbols().getShortMonths();

    /**
     * Create a new instance of DateTimeDateFormat.
     */
    public DateTimeDateFormat() {
        super(PATTERN);
    }


    /**
     * Create a new instance of DateTimeDateFormat.
     *
     * @param timeZone time zone used in conversion, may not be null.
     */
    public DateTimeDateFormat(final TimeZone timeZone) {
        super(PATTERN, timeZone);
    }

}
