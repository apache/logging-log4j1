/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.*;
import java.util.Iterator;
import java.util.Set;
import org.apache.log4j.helpers.*;


/**
 * Able to handle the contents of the LoggingEvent's Property bundle and either
 * output the entire contents of the properties in a similar format to the
 * java.util.Hashtable.toString(), or to output the value of a specific key
 * within the property bundle
 * when this pattern converter has the option set.
 *
 * @author Paul Smith
 * @author Ceki G&uuml;lc&uuml;
 *@since 1.3
 */
public final class PropertiesPatternConverter
  extends LoggingEventPatternConverter {
  /**
   * Name of property to output.
   */
  private final String option;
  private final Method getKeySetMethod;

  /**
   * Private constructor.
   * @param options options, may be null.
   */
  private PropertiesPatternConverter(
    final String[] options) {
    super(
      ((options != null) && (options.length > 0))
      ? ("Property{" + options[0] + "}") : "Properties", "property");

      //
      //  log4j 1.2.15 and later will have method to get names
      //     of all keys in MDC
      //
    Method getMethod = null;

    if ((options != null) && (options.length > 0)) {
      option = options[0];
    } else {
      option = null;
      try {
         getMethod = LoggingEvent.class.getMethod(
                    "getPropertyKeySet");
      } catch(Exception ex) {
          getMethod = null;
      }
    }
    getKeySetMethod = getMethod;
  }

  /**
   * Obtains an instance of PropertiesPatternConverter.
   * @param options options, may be null or first element contains name of property to format.
   * @return instance of PropertiesPatternConverter.
   */
  public static PropertiesPatternConverter newInstance(
    final String[] options) {
    return new PropertiesPatternConverter(options);
  }

  /**
   * {@inheritDoc}
   */
  public void format(final LoggingEvent event, final StringBuffer toAppendTo) {
    // if there is no additional options, we output every single
    // Key/Value pair for the MDC in a similar format to Hashtable.toString()
    if (option == null) {
      toAppendTo.append("{");

      //
      //  MDC keys are not visible prior to log4j 1.2.15
      //
      Set keySet = null;
      if (getKeySetMethod != null) {
          try {
            keySet = (Set) getKeySetMethod.invoke(event);
          } catch(InvocationTargetException ex) {
              LogLog.error("Exception while calling LoggingEvent.getKeySetMethod",
                      ex.getTargetException());
          } catch(Exception ex) {
              LogLog.error("Exception while calling LoggingEvent.getKeySetMethod",
                      ex);
          }
      } else {
          //
          //  for 1.2.14 and earlier could serialize and
          //    extract MDC content
          try {
            ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(outBytes);
            os.writeObject(event);
            os.close();

            byte[] raw = outBytes.toByteArray();
            //
            //   bytes 6 and 7 should be the length of the original classname
            //     should be the same as our substitute class name
            final String subClassName = LogEvent.class.getName();
            if (raw[6] == 0 || raw[7] == subClassName.length()) {
                //
                //  manipulate stream to use our class name
                //
                for (int i = 0; i < subClassName.length(); i++) {
                    raw[8 + i] = (byte) subClassName.charAt(i);
                }
                ByteArrayInputStream inBytes = new ByteArrayInputStream(raw);
                ObjectInputStream is = new ObjectInputStream(inBytes);
                Object cracked = is.readObject();
                if (cracked instanceof LogEvent) {
                    keySet = ((LogEvent) cracked).getPropertyKeySet();
                }
                is.close();
            }
          } catch(Exception ex) {
              LogLog.error("Unexpected exception while extracting MDC keys", ex);
          }
      }

      if (keySet != null) {
        for (Iterator i = keySet.iterator(); i.hasNext();) {
            Object item = i.next();
            Object val = event.getMDC(item.toString());
            toAppendTo.append("{").append(item).append(",").append(val).append(
            "}");
        }
      }

      toAppendTo.append("}");
    } else {
      // otherwise they just want a single key output
      Object val = event.getMDC(option);

      if (val != null) {
        toAppendTo.append(val);
      }
    }
  }
}
