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

package org.apache.log4j.test;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;


/**
   Very simple log4j usage example.

   @author  Ceki G&uuml;lc&uuml;
 */
public class Hello {
  static Logger logger = Logger.getLogger(Hello.class);

  public static void main(String[] argv) {
    BasicConfigurator.configure();
    logger.debug("Hello world.");
    logger.info("What a beatiful day.");
  }
}
