/*
 */
package org.apache.log4j.pattern;

import org.apache.log4j.spi.LoggingEvent;

import java.util.*;


/**
 * Able to handle the contents of the LoggingEvent's Property bundle and either
 * output the entire contents of the properties in a similar format to the java.util.Hashtable.toString()
 * , or to output the value of a specific key within the property bundle
 * when this pattern converter has the option set. 
 *
 * @author Paul Smith (but totally based (i.e 'copied') on the MDCPatternConverter by Ceki G&uuml;lc&uuml;)
 * with only minor alterations
 *
 */
public class PropertiesPatternConverter extends PatternConverter {

    public StringBuffer convert(LoggingEvent event) {

        StringBuffer buf = new StringBuffer(32);

        /**
         * if there is no additional options, we output every single
         * Key/Value pair for the MDC in a similar format to Hashtable.toString()
         */
        if (option == null) {
            buf.append("{");

            Set keySet = event.getPropertyKeySet();

            for (Iterator i = keySet.iterator(); i.hasNext();) {

                Object item = i.next();
                Object val = event.getProperty(item.toString());
                buf.append("{").append(item).append(",").append(val).append(
                    "}");
            }

            buf.append("}");

            return buf;
        }

        /**
         * otherwise they just want a single key output
         */
        Object val = event.getProperty(option);

        if (val != null) {

            return buf.append(val);
        }

        return buf;
    }
}
