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


/**
 * A <code>TriggeringPolicy</code> determines the time where rollover
 * should occur.
 *
 * @author Ceki G&uuml;lc&uuml;
 * */
package org.apache.log4j.rolling;

import java.io.File;

import org.apache.log4j.spi.OptionHandler;

public interface TriggeringPolicy extends OptionHandler {
  /**
   * Should rolllover be triggered at this time?
   * 
   * A reference to the active log file is supplied as a parameter.
   * 
   * */
  public boolean isTriggeringEvent(File file);

  //public boolean isTriggeringEvent();

  //public boolean isSizeSensitive();
}
