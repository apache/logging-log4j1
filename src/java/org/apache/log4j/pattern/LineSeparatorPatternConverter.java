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

import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;


/**
 * Return the line separator for the current system.
 *  
 * @author Ceki G&uuml;lc&uuml;
 */
public class LineSeparatorPatternConverter extends PatternConverter {

  StringBuffer buf;

  public LineSeparatorPatternConverter() {
    super();
    this.buf = new StringBuffer(Layout.LINE_SEP_LEN);
    buf.append(Layout.LINE_SEP);
  }

  public StringBuffer convert(LoggingEvent event) {
    return buf;
  }
  
  public String getName()
  {
      return "Line Sep";
  }
  
}
