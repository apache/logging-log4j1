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
public class PropertiesPatternConverter extends PatternConverter {
  
  String name;
  
  public StringBuffer convert(LoggingEvent event) {

    StringBuffer buf = new StringBuffer(32);

    // if there is no additional options, we output every single
    // Key/Value pair for the MDC in a similar format to Hashtable.toString()
    if (option == null) {
      buf.append("{");

      Set keySet = event.getPropertyKeySet();

      for (Iterator i = keySet.iterator(); i.hasNext();) {
        Object item = i.next();
        Object val = event.getProperty(item.toString());
        buf.append("{").append(item).append(",").append(val).append("}");
      }

      buf.append("}");

      return buf;
    }

    // otherwise they just want a single key output
    Object val = event.getProperty(option);

    if (val != null) {
      return buf.append(val);
    }

    return buf;
  }

  public String getName() {
    if(name == null) {
      if(option != null) {
        name += "Property{"+option+"}";
      } else {
        name = "Properties";
      }
    }
    return name;
  }
}
