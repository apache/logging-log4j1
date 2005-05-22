/*
 * Copyright 1999,2005 The Apache Software Foundation.
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

import org.apache.log4j.ULogger;
import org.apache.log4j.spi.LoggingEvent;

import java.util.*;


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

  /**
   * Private constructor.
   * @param options options, may be null.
   * @param logger logger for diagnostic messages, may be null.
   */
  private PropertiesPatternConverter(
    final String[] options, final ULogger logger) {
    super(
      ((options != null) && (options.length > 0))
      ? ("Property{" + options[0] + "}") : "Properties", "property");

    if ((options != null) && (options.length > 0)) {
      option = options[0];
    } else {
      option = null;
    }
  }

  /**
   * Obtains an instance of PropertiesPatternConverter.
   * @param options options, may be null or first element contains name of property to format.
   * @param logger  logger, current ignored, may be null.
   * @return instance of PropertiesPatternConverter.
   */
  public static PropertiesPatternConverter newInstance(
    final String[] options, final ULogger logger) {
    return new PropertiesPatternConverter(options, logger);
  }

  /**
   * {@inheritDoc}
   */
  public void format(final LoggingEvent event, final StringBuffer toAppendTo) {
    // if there is no additional options, we output every single
    // Key/Value pair for the MDC in a similar format to Hashtable.toString()
    if (option == null) {
      toAppendTo.append("{");

      Set keySet = event.getPropertyKeySet();

      for (Iterator i = keySet.iterator(); i.hasNext();) {
        Object item = i.next();
        Object val = event.getProperty(item.toString());
        toAppendTo.append("{").append(item).append(",").append(val).append(
          "}");
      }

      toAppendTo.append("}");
    } else {
      // otherwise they just want a single key output
      Object val = event.getProperty(option);

      if (val != null) {
        toAppendTo.append(val);
      }
    }
  }
}
