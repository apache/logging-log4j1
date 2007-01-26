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

package org.apache.log4j.concurrent;

import java.io.File;

import junit.framework.TestCase;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.rolling.FixedWindowRollingPolicy;
import org.apache.log4j.rolling.SizeBasedTriggeringPolicy;
import org.apache.log4j.util.Compare;

/**
 * Tests RollingFileAppender in the concurrent library. Taken from
 * SizeBasedRollingTest. Not a complete functionality test.
 */
public class RollingFileAppenderTest extends TestCase {
  Logger logger = Logger.getLogger(RollingFileAppenderTest.class);

  public RollingFileAppenderTest(String name) {
    super(name);
  }

  public void setUp() {
  }

  public void tearDown() {
    LogManager.shutdown();
  }

  /**
   * Test basic rolling functionality with explicit setting of
   * FileAppender.file.
   */
  public void test2() throws Exception {
    PatternLayout layout = new PatternLayout("%m\n");
    RollingFileAppender rfa = new RollingFileAppender();
    rfa.setName("ROLLING");
    rfa.setAppend(false);
    rfa.setLayout(layout);
    rfa.setFile("output/sizeBased-test2.log");

    FixedWindowRollingPolicy swrp = new FixedWindowRollingPolicy();
    SizeBasedTriggeringPolicy sbtp = new SizeBasedTriggeringPolicy();

    sbtp.setMaxFileSize(100);
    swrp.setMinIndex(0);

    swrp.setFileNamePattern("output/sizeBased-test2.%i");
    swrp.activateOptions();

    rfa.setRollingPolicy(swrp);
    rfa.setTriggeringPolicy(sbtp);
    rfa.activateOptions();
    logger.addAppender(rfa);

    // Write exactly 10 bytes with each log
    for (int i = 0; i < 25; i++) {
      if (i < 10) {
        logger.debug("Hello---" + i);
      } else if (i < 100) {
        logger.debug("Hello--" + i);
      }
    }

    assertTrue(new File("output/sizeBased-test2.log").exists());
    assertTrue(new File("output/sizeBased-test2.0").exists());
    assertTrue(new File("output/sizeBased-test2.1").exists());

    assertTrue(Compare.compare("output/sizeBased-test2.log",
        "witness/rolling/sbr-test2.log"));
    assertTrue(Compare.compare("output/sizeBased-test2.0",
        "witness/rolling/sbr-test2.0"));
    assertTrue(Compare.compare("output/sizeBased-test2.1",
        "witness/rolling/sbr-test2.1"));
  }

}
