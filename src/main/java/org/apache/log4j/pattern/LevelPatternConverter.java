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

import org.apache.log4j.Level;
import org.apache.log4j.ULogger;
import org.apache.log4j.spi.LoggingEvent;


/**
 * Return the event's level in a StringBuffer.
 *
 * @author Ceki G&uuml;lc&uuml;
 * @since 1.3
 */
public final class LevelPatternConverter extends LoggingEventPatternConverter {
  /**
   * Singleton.
   */
  private static final LevelPatternConverter INSTANCE =
    new LevelPatternConverter();

  /**
   * Private constructor.
   */
  private LevelPatternConverter() {
    super("Level", "level");
  }

  /**
   * Obtains an instance of pattern converter.
   * @param options options, may be null.
   * @param logger  logger, current ignored, may be null.
   * @return instance of pattern converter.
   */
  public static LevelPatternConverter newInstance(
    final String[] options, final ULogger logger) {
    return INSTANCE;
  }

  /**
   * {@inheritDoc}
   */
  public void format(final LoggingEvent event, final StringBuffer output) {
    output.append(event.getLevel().toString());
  }

  /**
   * {@inheritDoc}
   */
  public String getStyleClass(Object e) {
    if (e instanceof LoggingEvent) {
      int lint = ((LoggingEvent) e).getLevel().toInt();

      switch (lint) {
      case Level.TRACE_INT:
        return "level trace";

      case Level.DEBUG_INT:
        return "level debug";

      case Level.INFO_INT:
        return "level info";

      case Level.WARN_INT:
        return "level warn";

      case Level.ERROR_INT:
        return "level error";

      case Level.FATAL_INT:
        return "level fatal";

      default:
        return "level " + ((LoggingEvent) e).getLevel().toString();
      }
    }

    return "level";
  }
}
