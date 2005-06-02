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


/**
 * Formats a logger name.
 *
 * @author Ceki G&uuml;lc&uuml;
 *
 * @since 1.3
 */
public final class LoggerPatternConverter extends NamePatternConverter {
  /**
   * Singleton.
   */
  private static final LoggerPatternConverter INSTANCE =
    new LoggerPatternConverter(null, null);

  /**
   * Private constructor.
   * @param options options, may be null.
   * @param logger logger for diagnostic messages, may be null.
   */
  private LoggerPatternConverter(final String[] options, final ULogger logger) {
    super("Logger", "logger", options);
  }

  /**
   * Obtains an instance of pattern converter.
   * @param options options, may be null.
   * @param logger  logger, current ignored, may be null.
   * @return instance of pattern converter.
   */
  public static LoggerPatternConverter newInstance(
    final String[] options, final ULogger logger) {
    if ((options == null) || (options.length == 0)) {
      return INSTANCE;
    }

    return new LoggerPatternConverter(options, logger);
  }

  /**
   * {@inheritDoc}
   */
  public void format(final LoggingEvent event, final StringBuffer toAppendTo) {
    final int initialLength = toAppendTo.length();
    toAppendTo.append(event.getLoggerName());
    abbreviate(initialLength, toAppendTo);
  }
}
