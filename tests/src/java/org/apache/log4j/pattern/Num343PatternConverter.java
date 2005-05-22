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

import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.ULogger;


public class Num343PatternConverter extends LoggingEventPatternConverter {
  
  private Num343PatternConverter() {
      super("Num34", "num34");
  }
  private static final Num343PatternConverter INSTANCE = new Num343PatternConverter();

  public static PatternConverter newInstance(final String[] options,
                                      final ULogger logger) {
      return INSTANCE;
  }
    
  public void format(LoggingEvent event, StringBuffer toAppendTo) {
    toAppendTo.append("343");
  }
  
}
