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

package org.apache.log4j.performance;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.joran.JoranConfigurator;

/**
 * Logs in a loop a number of times and measure the elapsed time.
 * 
 * @author Ceki G&uuml;lc&uuml;
 */
public class Loop {

  static int runLength;

  final static Logger logger = Logger.getLogger(Loop.class);

  public static void main(String[] args) throws Exception {
    if (args.length == 2)
      init(args[0], args[1]);
    else
      usage("Wrong number of arguments.");

    long res = loop(logger, "Some fix message of medium length.");
    double average = (res * 1000.0) / runLength;
    System.out.println("Loop completed in [" + res + "] milliseconds, or ["
        + average + "] microseconds per log.");

  }

  static void usage(String msg) {
    System.err.println(msg);
    System.err.println("Usage: java " + Loop.class.getName()
        + " runLength configFile");
    System.err.println("\trunLength (integer) is the length of test loop.");
    System.err.println("\tconfigFile is an XML configuration file");

    System.exit(1);
  }

  static void init(String runLengthStr, String configFile) throws Exception {
    runLength = Integer.parseInt(runLengthStr);
    JoranConfigurator jc = new JoranConfigurator();
    jc.doConfigure(configFile, LogManager.getLoggerRepository());
  }

  static long loop(Logger logger, String msg) {
    long before = System.currentTimeMillis();
    for (int i = 0; i < runLength; i++) {
      logger.debug(msg);
    }
    return (System.currentTimeMillis() - before);
  }
}