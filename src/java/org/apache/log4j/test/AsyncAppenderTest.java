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
import org.apache.log4j.xml.DOMConfigurator;


/**
 * Test the coordination of the AsyncAppender with its Dispatcher.
 *
 * @author Ceki G&uuml;lc&uuml;
 */
public class AsyncAppenderTest {
  final static Logger logger = Logger.getLogger(AsyncAppenderTest.class);

  static int delayBeforeClose;

  public static void main(String[] argv) {
    if (argv.length == 2) {
      init(argv[0], argv[1]);
    } else {
      usage("Wrong number of arguments.");
    }

    test();
  }

  static void usage(String msg) {
    System.err.println(msg);
    System.err.println(
      "Usage: java " + Shallow.class.getName() + "configFile");
    System.exit(1);
  }

  static void init(String configFile, String delayBeforeCloseStr) {
    DOMConfigurator.configure(configFile);

    try {
      delayBeforeClose = Integer.parseInt(delayBeforeCloseStr);
    } catch (java.lang.NumberFormatException e) {
      e.printStackTrace();
      usage("Could not convert [" + delayBeforeCloseStr + "] to Integer.");
    }
  }

  static void test() {
    Logger root = Logger.getRootLogger();

    for (int i = 0; i < 100; i++) {
      root.debug("Message " + i);
    }

    try {
      Thread.sleep(delayBeforeClose);
    } catch (Exception e) {
    }

    LogManager.shutdown();
  }
}
