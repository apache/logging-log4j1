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

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;


/**
   This test program sits in a loop and logs things. Its logging is
   configured by a configuration file. Changes to this configuration
   file are monitored and when a change occurs, the config file is re-read.


   @author Ceki G&uuml;lc&uuml; */
public class DelayedLoop {
  static Logger cat = Logger.getLogger(DelayedLoop.class);
  static int loopLength;

  public static void main(String[] argv) {
    if (argv.length == 1) {
      init(argv[0]);
    } else {
      usage("Wrong number of arguments.");
    }

    test();
  }

  static void usage(String msg) {
    System.err.println(msg);
    System.err.println(
      "Usage: java " + DelayedLoop.class.getName() + "configFile");
    System.exit(1);
  }

  static void init(String configFile) {
    if (configFile.endsWith("xml")) {
      DOMConfigurator.configureAndWatch(configFile, 3000);
    } else {
      PropertyConfigurator.configureAndWatch(configFile, 3000);
    }
  }

  static void test() {
    int i = 0;

    while (true) {
      cat.debug("MSG " + i++);

      try {
        Thread.sleep(1000);
      } catch (Exception e) {
      }
    }
  }
}
