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
package org.apache.log4j.pattern;

import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;


/**
 * Outputs the ThrowableInformation portion of the LoggingiEvent as a full stacktrace
 * unless this converter's option is 'short', where it just outputs the first line of the trace.
 *
 * @author Paul Smith
 * @since 1.3
 *
 */
public class ThrowableInformationPatternConverter extends PatternConverter {

    /* (non-Javadoc)
     * @see org.apache.log4j.pattern.PatternConverter#convert(org.apache.log4j.spi.LoggingEvent)
     */
    protected StringBuffer convert(LoggingEvent event) {

        StringBuffer buf = new StringBuffer(32);

        ThrowableInformation information = event.getThrowableInformation();

        if (information == null) {

            return buf;
        }

        String[] stringRep = information.getThrowableStrRep();

        int length = 0;

        if (getOption() == null) {
            length = stringRep.length;
        } else if (getOption().equals("full")) {
            length = stringRep.length;
        } else if (getOption().equals("short")) {
            length = 1;
        } else {
            length = stringRep.length;
        }

        for (int i = 0; i < length; i++) {

            String string = stringRep[i];
            buf.append(string).append("\n");
        }

        return buf;
    }
    
    public String getName()
    {
        return "Throwable";
    }

}
