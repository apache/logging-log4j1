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

package org.apache.log4j.rolling;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.util.Compare;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;


/**
 *
 * @author Ceki G&uuml;lc&uuml;
 *
 */
public class TimeBasedRollingTest extends TestCase {
  Logger logger = Logger.getLogger(TimeBasedRollingTest.class);
  Logger root;

  public TimeBasedRollingTest(String name) {
    super(name);
  }

  public void setUp() {
    root = Logger.getRootLogger();
    root.addAppender(
      new ConsoleAppender(new PatternLayout("%d{ABSOLUTE} %c{1} - %m%n")));
  }

  public void tearDown() {
    LogManager.shutdown();
  }

  /**
   * Test rolling without compression, activeFileName left blank
   *
   * The test is a regression test. It logs a few times using a RollingFileAppender.
   * It predicts the names of the files which will be generated and
   * compares them with witness files.
   */
  public void test1() throws Exception {
    PatternLayout layout = new PatternLayout("%c{1} - %m%n");
    RollingFileAppender rfa = new RollingFileAppender();
    rfa.setLayout(layout);

    String datePattern = "yyyy-MM-dd_HH_mm_ss";

    TimeBasedRollingPolicy tbrp = new TimeBasedRollingPolicy();
    tbrp.setFileNamePattern("output/test1-%d{" + datePattern + "}");
    rfa.setRollingPolicy(tbrp);
    rfa.activateOptions();
    logger.addAppender(rfa);

    SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
    String[] filenames = new String[4];

    Calendar cal = Calendar.getInstance();

    for (int i = 0; i < 4; i++) {
      filenames[i] = "output/test1-" + sdf.format(cal.getTime());
      cal.add(Calendar.SECOND, 1);
    }

    root.debug("Waiting until next second and 100 millis.");
    delayUntilNextSecond(100);
    root.debug("Done waiting.");

    for (int i = 0; i < 5; i++) {
      logger.debug("Hello---" + i);
      Thread.sleep(500);
    }

    for (int i = 0; i < 4; i++) {
      //System.out.println(i + " expected filename [" + filenames[i] + "].");
    }

    for (int i = 0; i < 4; i++) {
      Compare.compare(filenames[i], "witness/tbr-test1." + i);
    }
  }

  /**
   * No compression with stop/restart, activeFileName left blank
   *
   * The test is a regression test. It logs a few times using a RollingFileAppender.
   * It predicts the names of the files which will be generated and
   * compares them with witness files.
   */
  public void test2() throws Exception {
    String datePattern = "yyyy-MM-dd_HH_mm_ss";

    PatternLayout layout1 = new PatternLayout("%c{1} - %m%n");
    RollingFileAppender rfa1 = new RollingFileAppender();
    rfa1.setLayout(layout1);

    TimeBasedRollingPolicy tbrp1 = new TimeBasedRollingPolicy();
    tbrp1.setFileNamePattern("output/test2-%d{" + datePattern + "}");
    rfa1.setRollingPolicy(tbrp1);
    rfa1.activateOptions();
    logger.addAppender(rfa1);

    SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
    String[] filenames = new String[4];

    Calendar cal = Calendar.getInstance();

    for (int i = 0; i < 4; i++) {
      filenames[i] = "output/test2-" + sdf.format(cal.getTime());
      cal.add(Calendar.SECOND, 1);
    }

    root.debug("Waiting until next second and 100 millis.");
    delayUntilNextSecond(100);
    root.debug("Done waiting.");

    for (int i = 0; i <= 2; i++) {
      logger.debug("Hello---" + i);
      Thread.sleep(500);
    }

    logger.removeAppender(rfa1);
    rfa1.close();

    PatternLayout layout2 = new PatternLayout("%c{1} - %m%n");
    RollingFileAppender rfa2 = new RollingFileAppender();
    rfa2.setLayout(layout2);

    TimeBasedRollingPolicy tbrp2 = new TimeBasedRollingPolicy();
    tbrp2.setFileNamePattern("output/test2-%d{" + datePattern + "}");
    rfa2.setRollingPolicy(tbrp2);
    rfa2.activateOptions();
    logger.addAppender(rfa2);

    for (int i = 3; i <= 4; i++) {
      logger.debug("Hello---" + i);
      Thread.sleep(500);
    }

    for (int i = 0; i < 4; i++) {
      Compare.compare(filenames[i], "witness/tbr-test2." + i);
    }
  }

  /**
   * With compression, no stop/restart, activeFileName left blank
   *
   * The test is a regression test. It logs a few times using a RollingFileAppender.
   * It predicts the names of the files which will be generated and
   * compares them with witness files.
   */
  public void test3() throws Exception {
    PatternLayout layout = new PatternLayout("%c{1} - %m%n");
    RollingFileAppender rfa = new RollingFileAppender();
    rfa.setLayout(layout);

    String datePattern = "yyyy-MM-dd_HH_mm_ss";

    TimeBasedRollingPolicy tbrp = new TimeBasedRollingPolicy();
    tbrp.setFileNamePattern("output/test3-%d{" + datePattern + "}.gz");
    rfa.setRollingPolicy(tbrp);
    rfa.activateOptions();
    logger.addAppender(rfa);

    SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
    String[] filenames = new String[4];

    Calendar cal = Calendar.getInstance();

    for (int i = 0; i < 3; i++) {
      filenames[i] = "output/test3-" + sdf.format(cal.getTime()) + ".gz";
      cal.add(Calendar.SECOND, 1);
    }

    filenames[3] = "output/test3-" + sdf.format(cal.getTime());

    root.debug("Waiting until next second and 100 millis.");
    delayUntilNextSecond(100);
    root.debug("Done waiting.");

    for (int i = 0; i < 5; i++) {
      logger.debug("Hello---" + i);
      Thread.sleep(500);
    }

    for (int i = 0; i < 4; i++) {
      //System.out.println(i + " expected filename [" + filenames[i] + "].");
    }

    for (int i = 0; i < 3; i++) {
      Compare.gzCompare(filenames[i], "witness/tbr-test3." + i + ".gz");
    }

    Compare.compare(filenames[3], "witness/tbr-test3.3");
  }

  /**
   * With compression, with stop/restart, activeFileName left blank
   *
   * The test is a regression test. It logs a few times using a RollingFileAppender.
   * It predicts the names of the files which will be generated and
   * compares them with witness files.
   */
  public void test4() throws Exception {
  }

  /**
   * No compression, activeFileName set
   *
   * The test is a regression test. It logs a few times using a RollingFileAppender.
   * It predicts the names of the files which will be generated and
   * compares them with witness files.
   */
  public void test5() throws Exception {
    PatternLayout layout = new PatternLayout("%c{1} - %m%n");
    RollingFileAppender rfa = new RollingFileAppender();
    rfa.setLayout(layout);

    String datePattern = "yyyy-MM-dd_HH_mm_ss";

    TimeBasedRollingPolicy tbrp = new TimeBasedRollingPolicy();
    tbrp.setFileNamePattern("output/test5-%d{" + datePattern + "}");
    tbrp.setActiveFileName("output/test5.log");
    rfa.setRollingPolicy(tbrp);
    rfa.activateOptions();
    logger.addAppender(rfa);

    SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
    String[] filenames = new String[4];

    Calendar cal = Calendar.getInstance();

    for (int i = 0; i < 3; i++) {
      filenames[i] = "output/test5-" + sdf.format(cal.getTime());
      cal.add(Calendar.SECOND, 1);
    }

    filenames[3] = "output/test5.log";

    root.debug("Waiting until next second and 100 millis.");
    delayUntilNextSecond(100);
    root.debug("Done waiting.");

    for (int i = 0; i < 5; i++) {
      logger.debug("Hello---" + i);
      Thread.sleep(500);
    }

    for (int i = 0; i < 4; i++) {
      Compare.compare(filenames[i], "witness/tbr-test5." + i);
    }
  }

  /**
   * With compression, no stop/restart, activeFileName set
   *
   * The test is a regression test. It logs a few times using a RollingFileAppender.
   * It predicts the names of the files which will be generated and
   * compares them with witness files.
   */
  public void test6() throws Exception {
    PatternLayout layout = new PatternLayout("%c{1} - %m%n");
    RollingFileAppender rfa = new RollingFileAppender();
    rfa.setLayout(layout);

    String datePattern = "yyyy-MM-dd_HH_mm_ss";

    TimeBasedRollingPolicy tbrp = new TimeBasedRollingPolicy();
    tbrp.setFileNamePattern("output/test6-%d{" + datePattern + "}.gz");
    tbrp.setActiveFileName("output/test6.log");
    rfa.setRollingPolicy(tbrp);
    rfa.activateOptions();
    logger.addAppender(rfa);

    SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
    String[] filenames = new String[4];

    Calendar cal = Calendar.getInstance();

    for (int i = 0; i < 3; i++) {
      filenames[i] = "output/test6-" + sdf.format(cal.getTime()) + ".gz";
      cal.add(Calendar.SECOND, 1);
    }

    filenames[3] = "output/test6.log";

    root.debug("Waiting until next second and 100 millis.");
    delayUntilNextSecond(100);
    root.debug("Done waiting.");

    for (int i = 0; i < 5; i++) {
      logger.debug("Hello---" + i);
      Thread.sleep(500);
    }

    for (int i = 0; i < 4; i++) {
      //System.out.println(i + " expected filename [" + filenames[i] + "].");
    }

    for (int i = 0; i < 3; i++) {
      Compare.gzCompare(filenames[i], "witness/tbr-test6." + i + ".gz");
    }

    Compare.compare(filenames[3], "witness/tbr-test6.3");
  }

  void delayUntilNextSecond(int millis) {
    long now = System.currentTimeMillis();
    Calendar cal = Calendar.getInstance();
    cal.setTime(new Date(now));

    cal.set(Calendar.MILLISECOND, millis);
    cal.add(Calendar.SECOND, 1);

    long next = cal.getTime().getTime();

    try {
      Thread.sleep(next - now);
    } catch (Exception e) {
    }
  }

  void delayUntilNextMinute(int seconds) {
    long now = System.currentTimeMillis();
    Calendar cal = Calendar.getInstance();
    cal.setTime(new Date(now));

    cal.set(Calendar.SECOND, seconds);
    cal.add(Calendar.MINUTE, 1);

    long next = cal.getTime().getTime();

    try {
      Thread.sleep(next - now);
    } catch (Exception e) {
    }
  }

  public static Test suite() {
    TestSuite suite = new TestSuite();

    suite.addTest(new TimeBasedRollingTest("test1"));
    suite.addTest(new TimeBasedRollingTest("test2"));
    suite.addTest(new TimeBasedRollingTest("test3"));
    suite.addTest(new TimeBasedRollingTest("test4"));

    suite.addTest(new TimeBasedRollingTest("test5"));
    suite.addTest(new TimeBasedRollingTest("test6"));

    return suite;
  }
}
