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

import org.apache.log4j.spi.LoggingEvent;


/**
 *
 *
 * @author Ceki G&uuml;lc&uuml;
 */
public class LoggerPatternConverter extends NamedPatternConverter {
  // We assume that each PatternConveter instance is unique within a layout, 
  // which is unique within an appender. We further assume that callas to the 
  // appender method are serialized (per appender).
  public LoggerPatternConverter() {
    super();
  }

  protected String getFullyQualifiedName(LoggingEvent event) {
    return event.getLoggerName();
  }

  public String getName() {
    return "Logger";
  }

  public String getStyleClass(LoggingEvent e) {
    return "logger";
  }
}
