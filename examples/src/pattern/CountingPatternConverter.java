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

package pattern;

import org.apache.log4j.pattern.PatternConverter;
import org.apache.log4j.spi.LoggingEvent;


public class CountingPatternConverter extends PatternConverter {
  static private final String NAME = "Count";
  
  StringBuffer buf;
  int counter = 0;

  public CountingPatternConverter() {
    super();
    this.buf = new StringBuffer(5);
  }

  public StringBuffer convert(LoggingEvent event) {
    if(buf.capacity() > 64) {
      buf = new StringBuffer(5);
    } else {
      buf.setLength(0);
    }
    return buf.append(String.valueOf(++counter));
  }
  
  public String getName() {
    return NAME;
  }
}
