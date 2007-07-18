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
import org.apache.log4j.spi.ThrowableInformation;


/**
 * Outputs the ThrowableInformation portion of the LoggingiEvent as a full stacktrace
 * unless this converter's option is 'short', where it just outputs the first line of the trace.
 *
 * @author Paul Smith
 * @since 1.3
 *
 */
public class ThrowableInformationPatternConverter
  extends LoggingEventPatternConverter {
  /**
   * If "short", only first line of throwable report will be formatted.
   */
  private final String option;

  /**
   * Private constructor.
   * @param options options, may be null.
   * @param logger logger for diagnostic messages, may be null.
   */
  private ThrowableInformationPatternConverter(
    final String[] options, final ULogger logger) {
    super("Throwable", "throwable");

    if ((options != null) && (options.length > 0)) {
      option = options[0];
    } else {
      option = null;
    }
  }

  /**
   * Gets an instance of the class.
    * @param options pattern options, may be null.  If first element is "short",
   * only the first line of the throwable will be formatted.
   * @param logger logger, unused and may be null.
   * @return instance of class.
   */
  public static ThrowableInformationPatternConverter newInstance(
    final String[] options, final ULogger logger) {
    return new ThrowableInformationPatternConverter(options, logger);
  }

  /**
   * {@inheritDoc}
   */
  public void format(final LoggingEvent event, final StringBuffer toAppendTo) {
    ThrowableInformation information = event.getThrowableInformation();

    if (information != null) {
      String[] stringRep = information.getThrowableStrRep();

      int length = 0;

      if (option == null) {
        length = stringRep.length;
      } else if (option.equals("full")) {
        length = stringRep.length;
      } else if (option.equals("short")) {
        length = 1;
      } else {
        length = stringRep.length;
      }

      for (int i = 0; i < length; i++) {
        String string = stringRep[i];
        toAppendTo.append(string).append("\n");
      }
    }
  }

  /**
   * This converter obviously handles throwables.
   * @return true.
   */
  public boolean handlesThrowable() {
    return true;
  }
}
