/*
 * Copyright 1999,2005 The Apache Software Foundation.
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

package org.apache.log4j.rolling;

import junit.framework.TestCase;


import org.apache.log4j.*;
import org.apache.log4j.filter.LevelRangeFilter;
import org.apache.log4j.joran.JoranConfigurator;
import org.apache.log4j.util.Compare;


/**
 *
 * Tests of rolling file appender with a filter based triggering policy.
 *
 * @author Curt Arnold
 * @since 1.3
 *
 */
public class FilterBasedRollingTest extends TestCase {
  public FilterBasedRollingTest(String name) {
    super(name);
  }

  public void setUp() {
      Appender ca = new ConsoleAppender(new PatternLayout("%d %level %c -%m%n"));
      ca.setName("CONSOLE");
      Logger.getRootLogger().addAppender(ca);
  }

  public void tearDown() {
    LogManager.shutdown();
  }

  /**
   * Test basic rolling functionality using configuration file.
   */
  public void test1() throws Exception {
    JoranConfigurator jc = new JoranConfigurator();
    jc.doConfigure(
      "./input/rolling/filter1.xml", LogManager.getLoggerRepository());
    jc.dumpErrors();

    common("output/filterBased-test1");
  }

  /**
   * Test basic rolling functionality using explicit configuration.
   * Test fails when run immediately after test1.
   */
  public void xtest2() throws Exception {
    PatternLayout layout = new PatternLayout("%m\n");
    RollingFileAppender rfa = new RollingFileAppender();
    rfa.setName("ROLLING");
    rfa.setLayout(layout);

    FixedWindowRollingPolicy swrp = new FixedWindowRollingPolicy();
    FilterBasedTriggeringPolicy fbtp = new FilterBasedTriggeringPolicy();

    LevelRangeFilter rf = new LevelRangeFilter();
    rf.setLevelMin(Level.INFO);
    fbtp.addFilter(rf);
    fbtp.activateOptions();

    swrp.setMinIndex(0);
    swrp.setActiveFileName("output/filterBased-test2.log");

    swrp.setFileNamePattern("output/filterBased-test2.%i");
    swrp.activateOptions();

    rfa.setRollingPolicy(swrp);
    rfa.setTriggeringPolicy(fbtp);
    rfa.activateOptions();
    Logger.getRootLogger().addAppender(rfa);
    Logger.getRootLogger().setLevel(Level.DEBUG);

    common("output/filterBased-test2");
  }

  /**
   *   Common aspects of test1 and test2
   */
  private void common(String baseName) throws Exception {
    Logger logger = Logger.getLogger(FilterBasedRollingTest.class);

    // Write exactly 10 bytes with each log
    for (int i = 0; i < 25; i++) {
      Thread.sleep(100);

      if (i < 10) {
        logger.debug("Hello---" + i);
      } else if (i < 100) {
        if ((i % 10) == 0) {
          //  on the 10th and 20th request, raise the severity
          logger.warn("Hello--" + i);
        } else {
          logger.debug("Hello--" + i);
        }
      }
    }

    //
    //  test was constructed to mimic SizeBasedRollingTest.test2
    //
    assertTrue(
      Compare.compare(baseName + ".log", "witness/rolling/sbr-test2.log"));
    assertTrue(
      Compare.compare(baseName + ".0", "witness/rolling/sbr-test2.0"));
    assertTrue(
      Compare.compare(baseName + ".1", "witness/rolling/sbr-test2.1"));
  }
}
