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

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Logger;
import org.apache.log4j.Layout;

/**
 * 
 * Example showing how to extend PatternLayout to recognize additional 
 * conversion words without adding.
 * 
 * <p>In this case MyPatternLayout recognizes %counter conversion word. 
 * It outputs the value of an internal counter which is also incremented at 
 * each call.
 * 
 * @see org.apache.log4j.PatternLayout
 * @author Anders Kristensen
 * @author Ceki G&uuml;lc&uuml;
 */

public class MyPatternLayout extends PatternLayout {
  public MyPatternLayout() {
    super();
  }

  public MyPatternLayout(String pattern) {
    super(pattern);
  }

  /**
    Activates the conversion pattern. Do not forget to call this method after
    you change the parameters of the PatternLayout instance.
  */
  public void activateOptions() {
    this.addConversionRule("counter", CountingPatternConverter.class.getName());
    super.activateOptions();
  }

  public static void main(String[] args) {
    Layout layout = new MyPatternLayout("[counter=%.10#] - %m%n");
    Logger logger = Logger.getLogger("some.cat");
    logger.addAppender(new ConsoleAppender(layout, ConsoleAppender.SYSTEM_OUT));
    logger.debug("Hello, log");
    logger.info("Hello again...");
  }
}
