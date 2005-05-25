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

import java.io.File;


/**
 * Tests the emulation of org.apache.log4j.RollingFileAppender
 *
 * @author Curt Arnold
 *
 */
public class ObsoleteRollingFileAppenderTest extends TestCase {
  Logger logger = Logger.getLogger(ObsoleteRollingFileAppenderTest.class);
  Logger root = Logger.getRootLogger();

  public ObsoleteRollingFileAppenderTest(final String name) {
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
    PropertyConfigurator.configure("input/rolling/obsoleteRFA1.properties");

    // Write exactly 10 bytes with each log
    for (int i = 0; i < 25; i++) {
      if (i < 10) {
        logger.debug("Hello---" + i);
      } else if (i < 100) {
        logger.debug("Hello--" + i);
      }
    }

    assertTrue(new File("output/obsoleteRFA-test1.log").exists());
    assertTrue(new File("output/obsoleteRFA-test1.log.1").exists());
  }

  /**
   * Test basic rolling functionality.
   * @deprecated Class under test is deprecated. 
   */
  public void test2() throws Exception {
    PatternLayout layout = new PatternLayout("%m\n");
    org.apache.log4j.RollingFileAppender rfa =
      new org.apache.log4j.RollingFileAppender();
    rfa.setName("ROLLING");
    rfa.setLayout(layout);
    rfa.setAppend(false);
    rfa.setMaximumFileSize(100);
    rfa.setFile("output/obsoleteRFA-test2.log");
    rfa.activateOptions();
    root.addAppender(rfa);

    // Write exactly 10 bytes with each log
    for (int i = 0; i < 25; i++) {
      if (i < 10) {
        logger.debug("Hello---" + i);
      } else if (i < 100) {
        logger.debug("Hello--" + i);
      }
    }

    assertTrue(new File("output/obsoleteRFA-test2.log").exists());
    assertTrue(new File("output/obsoleteRFA-test2.log.1").exists());
  }
}
