/*
 * Copyright 1999-2005 The Apache Software Foundation.
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
package org.apache.log4j.test;

import org.apache.log4j.spi.*;
import org.apache.log4j.*;

/**
 * This configurator simply always adds a FileAppender writing to
 * System.out to the root Logger and ignores whatever is in the
 * properties file.
 */
public class SysoutConfigurator implements Configurator {
  public
  void
  doConfigure(java.net.URL url,  LoggerRepository hierarchy) {
    Logger.getRootLogger().addAppender(
        new ConsoleAppender(
            new SimpleLayout(), ConsoleAppender.SYSTEM_OUT));
  }
}
