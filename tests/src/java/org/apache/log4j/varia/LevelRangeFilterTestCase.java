/*
 * ============================================================================
 *                   The Apache Software License, Version 1.1
 * ============================================================================
 *
 *    Copyright (C) 1999 The Apache Software Foundation. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modifica-
 * tion, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of  source code must  retain the above copyright  notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any, must
 *    include  the following  acknowledgment:  "This product includes  software
 *    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
 *    Alternately, this  acknowledgment may  appear in the software itself,  if
 *    and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "log4j" and  "Apache Software Foundation"  must not be used to
 *    endorse  or promote  products derived  from this  software without  prior
 *    written permission. For written permission, please contact
 *    apache@apache.org.
 *
 * 5. Products  derived from this software may not  be called "Apache", nor may
 *    "Apache" appear  in their name,  without prior written permission  of the
 *    Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 * APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 * DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 * ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 * (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * This software  consists of voluntary contributions made  by many individuals
 * on  behalf of the Apache Software  Foundation.  For more  information on the
 * Apache Software Foundation, please see <http://www.apache.org/>.
 *
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
