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
import org.apache.log4j.varia.LevelRangeFilter;


/**
   Test case for varia/LevelRangeFilter.java.

   <p>Please note that in the witness file some passes are not
   recorded.  This is due to the max level being set to less
   than the min level, thus no messages are logged on that pass.
   This is correct behavior.
 */
public class LevelRangeFilterTestCase extends TestCase {
  static String ACCEPT_FILE = "output/LevelRangeFilter_accept";
  static String ACCEPT_WITNESS = "witness/LevelRangeFilter_accept";
  static String NEUTRAL_FILE = "output/LevelRangeFilter_neutral";
  static String NEUTRAL_WITNESS = "witness/LevelRangeFilter_neutral";
  Logger root;

  public LevelRangeFilterTestCase(String name) {
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

    // create LevelRangeFilter
    LevelRangeFilter rangeFilter = new LevelRangeFilter();

    // set it to accept on a match
    rangeFilter.setAcceptOnMatch(true);

    // attach match filter to appender
    appender.addFilter(rangeFilter);

    // set appender on root and set level to debug
    root.addAppender(appender);
    root.setLevel(Level.DEBUG);

    int passCount = 0;

    // test with no min or max set
    common("pass " + passCount + "; no min or max set");
    passCount++;

    // test with a min set
    rangeFilter.setLevelMin(Level.WARN);
    common("pass " + passCount + "; min set to WARN, max not set");
    passCount++;

    // create a clean filter
    appender.clearFilters();
    rangeFilter = new LevelRangeFilter();
    appender.addFilter(rangeFilter);

    //test with max set
    rangeFilter.setLevelMax(Level.WARN);
    common("pass " + passCount + "; min not set, max set to WARN");
    passCount++;

    Level[] levelArray =
      new Level[] { Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR, Level.FATAL };

    for (int x = 0; x < levelArray.length; x++) {
      // set the min level to match
      rangeFilter.setLevelMin(levelArray[x]);

      for (int y = levelArray.length - 1; y >= 0; y--) {
        // set max level to match
        rangeFilter.setLevelMax(levelArray[y]);

        common(
          "pass " + passCount + "; filter set to accept between "
          + levelArray[x].toString() + " and " + levelArray[y].toString()
          + " msgs");

        // increment passCount
        passCount++;
      }
    }

    assertTrue(Compare.compare(ACCEPT_FILE, ACCEPT_WITNESS));
  }

  public void neutral() throws Exception {
    // set up appender
    Layout layout = new SimpleLayout();
    Appender appender = new FileAppender(layout, NEUTRAL_FILE, false);

    // create LevelRangeFilter
    LevelRangeFilter rangeFilter = new LevelRangeFilter();

    // set it to not accept on a match
    rangeFilter.setAcceptOnMatch(false);

    // attach match filter to appender
    appender.addFilter(rangeFilter);

    // set appender on root and set level to debug
    root.addAppender(appender);
    root.setLevel(Level.DEBUG);

    int passCount = 0;

    // test with no min or max set
    common("pass " + passCount + "; no min or max set");
    passCount++;

    // test with a min set
    rangeFilter.setLevelMin(Level.WARN);
    common("pass " + passCount + "; min set to WARN, max not set");
    passCount++;

    // create a clean filter
    appender.clearFilters();
    rangeFilter = new LevelRangeFilter();
    appender.addFilter(rangeFilter);

    //test with max set
    rangeFilter.setLevelMax(Level.WARN);
    common("pass " + passCount + "; min not set, max set to WARN");
    passCount++;

    Level[] levelArray =
      new Level[] { Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR, Level.FATAL };

    for (int x = 0; x < levelArray.length; x++) {
      // set the min level to match
      rangeFilter.setLevelMin(levelArray[x]);

      for (int y = levelArray.length - 1; y >= 0; y--) {
        // set max level to match
        rangeFilter.setLevelMax(levelArray[y]);

        common(
          "pass " + passCount + "; filter set to accept between "
          + levelArray[x].toString() + " and " + levelArray[y].toString()
          + " msgs");

        // increment passCount
        passCount++;
      }
    }

    assertTrue(Compare.compare(NEUTRAL_FILE, NEUTRAL_WITNESS));
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
    suite.addTest(new LevelRangeFilterTestCase("accept"));
    suite.addTest(new LevelRangeFilterTestCase("neutral"));

    return suite;
  }
}
