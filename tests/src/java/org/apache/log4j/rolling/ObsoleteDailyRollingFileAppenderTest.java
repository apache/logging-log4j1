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
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;


/**
 * Tests the emulation of org.apache.log4j.DailyRollingFileAppender
 *
 * @author Curt Arnold
 *
 */
public class ObsoleteDailyRollingFileAppenderTest extends TestCase {
  Logger logger = Logger.getLogger(ObsoleteDailyRollingFileAppenderTest.class);
  Logger root = Logger.getRootLogger();

  public ObsoleteDailyRollingFileAppenderTest(final String name) {
    super(name);
  }

  public void setUp() {
  }

  public void tearDown() {
    LogManager.shutdown();
  }

  /**
   * Test basic rolling functionality.
   */
  public void test1() throws Exception {
    PropertyConfigurator.configure("input/rolling/obsoleteDRFA1.properties");

    int preCount = getFileCount("output", "obsoleteDRFA-test1.log.");

    for (int i = 0; i < 25; i++) {
      Thread.sleep(100);
      logger.debug("Hello---" + i);
    }

    int postCount = getFileCount("output", "obsoleteDRFA-test1.log.");
    assertTrue(postCount > preCount);
  }

  /**
   * Test basic rolling functionality.
   * @deprecated Class under test is deprecated.
   */
  public void test2() throws Exception {
    PatternLayout layout = new PatternLayout("%m%n");
    org.apache.log4j.DailyRollingFileAppender rfa =
      new org.apache.log4j.DailyRollingFileAppender();
    rfa.setName("ROLLING");
    rfa.setLayout(layout);
    rfa.setAppend(false);
    rfa.setFile("output/obsoleteDRFA-test2.log");
    rfa.setDatePattern("'.'yyyy-MM-dd-HH_mm_ss");
    rfa.activateOptions();
    root.addAppender(rfa);

    int preCount = getFileCount("output", "obsoleteDRFA-test2.log.");

    for (int i = 0; i < 25; i++) {
      Thread.sleep(100);
      logger.debug("Hello---" + i);
    }

    int postCount = getFileCount("output", "obsoleteDRFA-test2.log.");
    assertTrue(postCount > preCount);
  }

  private static int getFileCount(String dir, String initial) {
    String[] files = new File(dir).list();
    int count = 0;

    for (int i = 0; i < files.length; i++) {
      if (files[i].startsWith(initial)) {
        count++;
      }
    }

    return count;
  }
}
