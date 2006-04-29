/*
 * Copyright 2005-2006 The Apache Software Foundation.
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

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;

/**
 *  Test of RollingFileAppender.
 *
 * @author Curt Arnold
 */
public class RFATestCase extends TestCase {

  public RFATestCase(String name) {
    super(name);
  }

  public void tearDown() {
      LogManager.resetConfiguration();
  }

    /**
     * Test basic rolling functionality using property file configuration.
     */
    public void test1() throws Exception {
     Logger logger = Logger.getLogger(RFATestCase.class);
      PropertyConfigurator.configure("input/RFA1.properties");

      // Write exactly 10 bytes with each log
      for (int i = 0; i < 25; i++) {
        if (i < 10) {
          logger.debug("Hello---" + i);
        } else if (i < 100) {
          logger.debug("Hello--" + i);
        }
      }

      assertTrue(new File("output/RFA-test1.log").exists());
      assertTrue(new File("output/RFA-test1.log.1").exists());
    }

    /**
     * Test basic rolling functionality using API configuration.
     */
    public void test2() throws Exception {
      Logger logger = Logger.getLogger(RFATestCase.class);
      Logger root = Logger.getRootLogger();
      PatternLayout layout = new PatternLayout("%m\n");
      org.apache.log4j.RollingFileAppender rfa =
        new org.apache.log4j.RollingFileAppender();
      rfa.setName("ROLLING");
      rfa.setLayout(layout);
      rfa.setAppend(false);
      rfa.setMaxBackupIndex(3);
      rfa.setMaximumFileSize(100);
      rfa.setFile("output/RFA-test2.log");
      rfa.activateOptions();
      root.addAppender(rfa);

      // Write exactly 10 bytes with each log
      for (int i = 0; i < 55; i++) {
        if (i < 10) {
          logger.debug("Hello---" + i);
        } else if (i < 100) {
          logger.debug("Hello--" + i);
        }
      }

      assertTrue(new File("output/RFA-test2.log").exists());
      assertTrue(new File("output/RFA-test2.log.1").exists());
      assertTrue(new File("output/RFA-test2.log.2").exists());
      assertTrue(new File("output/RFA-test2.log.3").exists());
      assertFalse(new File("output/RFA-test2.log.4").exists());
    }

    /**
     * Tests 2 parameter constructor.
     * @throws IOException if IOException during test.
     */
    public void test2ParamConstructor() throws IOException {
        SimpleLayout layout = new SimpleLayout();
        RollingFileAppender appender =
                new RollingFileAppender(layout,"output/rfa_2param.log");
        assertEquals(1, appender.getMaxBackupIndex());
        assertEquals(10*1024*1024, appender.getMaximumFileSize());
    }
    /**
     * Tests 3 parameter constructor.
     * @throws IOException if IOException during test.
     */
    public void test3ParamConstructor() throws IOException {
        SimpleLayout layout = new SimpleLayout();
        RollingFileAppender appender =
                new RollingFileAppender(layout,"output/rfa_3param.log", false);
        assertEquals(1, appender.getMaxBackupIndex());
    }

}
