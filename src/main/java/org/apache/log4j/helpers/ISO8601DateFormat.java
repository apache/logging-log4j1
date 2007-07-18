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

// Contributors: Arndt Schoenewald <arndt@ibm23093i821.mc.schoenewald.de>

/**
 * Formats a {@link java.util.Date} in the format "yyyy-MM-dd HH:mm:ss,SSS" for example
 * "1999-11-27 15:49:37,459".
 * <p/>
 * <p>Refer to the <a
 * href=http://www.cl.cam.ac.uk/~mgk25/iso-time.html>summary of the
 * International Standard Date and Time Notation</a> for more
 * information on this format.
 *
 * @author Ceki G&uuml;lc&uuml;
 * @author Andrew Vajoczki
 * @since 0.7.5
 * @deprecated
 */
public class ISO8601DateFormat extends AbsoluteTimeDateFormat {
    /**
     * Equivalent format string for SimpleDateFormat.
     */
    private static final String FORMAT = "yyyy-MM-dd HH:mm:ss,SSS";

    /**
     * Create a new instance of ISO8601DateFormat.
     */
    public ISO8601DateFormat() {
        super(FORMAT);
    }

    /**
     * Create a new instance of ISO8601DateFormat.
     *
     * @param timeZone time zone used in conversion, may not be null.
     */
    public ISO8601DateFormat(final TimeZone timeZone) {
        super(FORMAT, timeZone);
    }

}

