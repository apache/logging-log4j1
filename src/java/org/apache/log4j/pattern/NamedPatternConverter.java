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

import java.util.List;

import org.apache.log4j.spi.LoggingEvent;


/**
 * 
 * Base class for other pattern converters which can return only parts of their name.
 *  
 * @author Ceki G&uuml;lc&uuml;
 */
abstract class NamedPatternConverter extends PatternConverter {
	
  // We assume that each PatternConveter instance is unique within a layout, 
  // which is unique within an appender. We further assume that callas to the 
  // appender method are serialized (per appender).
  StringBuffer buf;
  int precision;

  public NamedPatternConverter() {
    super();
    this.buf = new StringBuffer(32);
  }

  abstract String getFullyQualifiedName(LoggingEvent event);

  public void setOptions(List optionList) {
    if(optionList == null || optionList.size() == 0) {
      return;
    }

    String option = (String) optionList.get(0);

    /**
       The option is expected to be in decimal and positive. In case of
       error, zero is returned.  */
    int r = 0;

    if (option != null) {
      try {
        precision = Integer.parseInt(option);
        //System.out.println("Precision is "+precision);
        if (precision <= 0) {
          getLogger().error(
            "Precision option (" + option + ") isn't a positive integer.");
          precision = 0;
        }
      } catch (NumberFormatException e) {
        getLogger().error(
          "Category option \"" + option + "\" not a decimal integer.", e);
      }
    }
  }

  public StringBuffer convert(LoggingEvent event) {
    buf.setLength(0);

    String n = getFullyQualifiedName(event);
    if (precision <= 0) {
      buf.append(n);
    } else {
      int len = n.length();

      // We substract 1 from 'len' when assigning to 'end' to avoid out of
      // bounds exception in return r.substring(end+1, len). This can happen if
      // precision is 1 and the category name ends with a dot.
      int end = len - 1;

      for (int i = precision; i > 0; i--) {
        end = n.lastIndexOf('.', end - 1);

        if (end == -1) {
        	// not enough dot characters. The whole string should be returned
          return buf.append(n);
        }
      }

      // The end variable should point to the left-most dot character to
      // the right of which all character should be returned.
      buf.append(n.substring(end + 1, len));
    }
    return buf;
  }
}
