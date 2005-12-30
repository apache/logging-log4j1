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
import org.apache.log4j.spi.LocationInfo;


/**
 * Formats the class name of the site of the logging request.
 *
 * @author Ceki G&uuml;lc&uuml;
 * @since 1.3
 */
public final class ClassNamePatternConverter extends NamePatternConverter {
  /**
   * Private constructor.
   * @param options options, may be null.
   * @param logger logger for diagnostic messages, may be null.
   */
  private ClassNamePatternConverter(
    final String[] options, final ULogger logger) {
    super("Class Name", "class name", options);
  }

  /**
   * Gets an instance of ClassNamePatternConverter.
   * @param options options, may be null.
   * @param logger logger for diagnostic messages, may be null.
   * @return instance of pattern converter.
   */
  public static ClassNamePatternConverter newInstance(
    final String[] options, final ULogger logger) {
    return new ClassNamePatternConverter(options, logger);
  }

  /**
   * Format a logging event.
    * @param event event to format.
   * @param toAppendTo string buffer to which class name will be appended.
   */
  public void format(final LoggingEvent event, final StringBuffer toAppendTo) {
    final int initialLength = toAppendTo.length();
    LocationInfo li = event.getLocationInformation();

    if (li == null) {
      toAppendTo.append(LocationInfo.NA);
    } else {
      toAppendTo.append(li.getClassName());
    }

    abbreviate(initialLength, toAppendTo);
  }
}
