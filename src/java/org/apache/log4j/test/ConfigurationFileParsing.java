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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.NDC;
import org.apache.log4j.PropertyConfigurator;


public class ConfigurationFileParsing {
  public static void main(String[] argv) {
    if (argv.length == 1) {
      NDC.push("testing");
      PropertyConfigurator.configure(argv[0]);

      Logger root = Logger.getRootLogger();
      root.debug("Message 1");
      root.debug("Message 2");
      NDC.pop();
      LogManager.shutdown();
    } else {
      Usage("Wrong number of arguments.");
    }
  }

  static void Usage(String msg) {
    System.err.println(msg);
    System.err.println(
      "Usage: java " + ConfigurationFileParsing.class.getName() + " fileName");
    System.exit(1);
  }
}
