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
 * Return the event's line location information in a StringBuffer.
 *
 * @author Ceki G&uuml;lc&uuml;
 * @since 1.3
 */
public final class LineLocationPatternConverter
  extends LoggingEventPatternConverter {
  /**
   * Singleton.
   */
  private static final LineLocationPatternConverter INSTANCE =
    new LineLocationPatternConverter();

  /**
   * Private constructor.
   */
  private LineLocationPatternConverter() {
    super("Line", "line");
  }

  /**
   * Obtains an instance of pattern converter.
   * @param options options, may be null.
   * @param logger  logger, current ignored, may be null.
   * @return instance of pattern converter.
   */
  public static LineLocationPatternConverter newInstance(
    final String[] options, final ULogger logger) {
    return INSTANCE;
  }

  /**
   * {@inheritDoc}
   */
  public void format(final LoggingEvent event, final StringBuffer output) {
    LocationInfo locationInfo = event.getLocationInformation();

    if (locationInfo != null) {
      output.append(locationInfo.getLineNumber());
    }
  }
}
