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

package org.apache.log4j;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.config.ConfiguratorBase;
import org.apache.log4j.joran.JoranConfigurator;
import org.apache.log4j.spi.LoggerRepository;


/**
 * Simple application used for manual testing.
 *
 * @author Ceki G&uuml;lc&uuml;
 */
public class SimpleLog {

  public static void main(String[] args) throws Exception {
    if (args.length != 1) {
      usage("Wrong number of arguments.");
    }

    String configFile = args[0];
    LoggerRepository repo = LogManager.getLoggerRepository();
    ConfiguratorBase configurator;
    if (configFile.endsWith(".xml")) {
      JoranConfigurator jc = new JoranConfigurator();
      jc.doConfigure(configFile, repo);
      jc.dumpErrors();
    } else {
      PropertyConfigurator pc = new PropertyConfigurator();
      pc.doConfigure(configFile, repo);
      pc.dumpErrors();
    }

    Logger logger = Logger.getLogger(SimpleLog.class);
    int i = 0;

    logger.debug("This message number: " + (i++));
    logger.debug("This message number: " + (i++));
    logger.error("This message number: " + (i++), new Exception("Bogus."));
  }

  static void usage(String msg) {
    System.err.println(msg);
    System.err.println(
      "Usage: java " + SimpleLog.class.getName() + " configFile\n"
      + "   configFile a log4j configuration file, either in properties "
      + "   or XML format. XML files must have a '.xml' extension.");
    System.exit(1);
  }
}
