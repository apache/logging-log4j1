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

import java.util.List;


/**
 *
 * Base class for other pattern converters which can return only parts of their name.
 *
 * @author Ceki G&uuml;lc&uuml;
 * @author Curt Arnold
 */
public abstract class NamedPatternConverter extends PatternConverter {
  // We assume that each PatternConveter instance is unique within a layout, 
  // which is unique within an appender. We further assume that callas to the 
  // appender method are serialized (per appender).
  private final StringBuffer buf = new StringBuffer(32);
  private NameAbbreviator abbreviator =
    NameAbbreviator.getDefaultAbbreviator();

  protected NamedPatternConverter() {
  }

  /**
   * Gets fully qualified name from event.
   * @param event event, will not be null.
   * @return name, must not be null.
   */
  protected abstract String getFullyQualifiedName(final LoggingEvent event);

  /**
   * Sets converter options.
   *
   * NamedPatternConverter interprets the first parameter as an
   * abbreviation specification.
   *
   * @param optionList option list.
   */
  public void setOptions(final List optionList) {
    if ((optionList != null) && (optionList.size() > 0)) {
      String option = (String) optionList.get(0);
      abbreviator = NameAbbreviator.getAbbreviator(option);
    }
  }

  /**
   * Convert event.
   *
   * @param event event, may not be null.
   * @return string buffer used in conversion.
   */
  public StringBuffer convert(final LoggingEvent event) {
    buf.setLength(0);

    String n = getFullyQualifiedName(event);
    abbreviator.abbreviate(buf, n);

    return buf;
  }
}
