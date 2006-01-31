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

package org.apache.log4j.varia;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.util.Compare;
import org.apache.log4j.filter.DenyAllFilter;
import org.apache.log4j.filter.LevelMatchFilter;


/**
   Test case for varia/LevelMatchFilter.java.  Test both the accept
   and deny cases.  Testing the accept requires the use of the
   DenyAllFilter to prevent non-matched messages from still getting
   logged to the appender.
 */
public class LevelMatchFilterTestCase extends TestCase {
  static String ACCEPT_FILE = "output/LevelMatchFilter_accept";
  static String ACCEPT_WITNESS = "witness/LevelMatchFilter_accept";
  static String DENY_FILE = "output/LevelMatchFilter_deny";
  static String DENY_WITNESS = "witness/LevelMatchFilter_deny";
  Logger root;
  Logger logger;

  public LevelMatchFilterTestCase(String name) {
    super(name);
  }

  public void setUp() {
    root = Logger.getRootLogger();
    root.removeAllAppenders();
  }

  public void tearDown() {
    root.getLoggerRepository().resetConfiguration();
  }

  public void accept() throws Exception {
    // set up appender
    Layout layout = new SimpleLayout();
    Appender appender = new FileAppender(layout, ACCEPT_FILE, false);

    // create LevelMatchFilter
    LevelMatchFilter matchFilter = new LevelMatchFilter();

    // attach match filter to appender
    appender.addFilter(matchFilter);

    // attach DenyAllFilter to end of filter chain to deny neutral
    // (non matching) messages
    appender.addFilter(new DenyAllFilter());

    // set appender on root and set level to debug
    root.addAppender(appender);
    root.setLevel(Level.DEBUG);

    Level[] levelArray =
      new Level[] { Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR, Level.FATAL };

    for (int x = 0; x < levelArray.length; x++) {
      // set the level to match
      matchFilter.setLevelToMatch(levelArray[x].toString());
      common(
        "pass " + x + "; filter set to accept only "
        + levelArray[x].toString() + " msgs");
    }

    assertTrue(Compare.compare(ACCEPT_FILE, ACCEPT_WITNESS));
  }

  public void deny() throws Exception {
    // set up appender
    Layout layout = new SimpleLayout();
    Appender appender = new FileAppender(layout, DENY_FILE, false);

    // create LevelMatchFilter, set to deny matches
    LevelMatchFilter matchFilter = new LevelMatchFilter();
    matchFilter.setAcceptOnMatch(false);

    // attach match filter to appender
    appender.addFilter(matchFilter);

    // set appender on root and set level to debug
    root.addAppender(appender);
    root.setLevel(Level.DEBUG);

    Level[] levelArray =
      new Level[] { Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR, Level.FATAL };

    for (int x = 0; x < levelArray.length; x++) {
      // set the level to match
      matchFilter.setLevelToMatch(levelArray[x].toString());
      common(
        "pass " + x + "; filter set to deny only " + levelArray[x].toString()
        + " msgs");
    }

    assertTrue(Compare.compare(DENY_FILE, DENY_WITNESS));
  }

  void common(String msg) {
    Logger logger = Logger.getLogger("test");
    logger.debug(msg);
    logger.info(msg);
    logger.warn(msg);
    logger.error(msg);
    logger.fatal(msg);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(new LevelMatchFilterTestCase("accept"));
    suite.addTest(new LevelMatchFilterTestCase("deny"));

    return suite;
  }
}
