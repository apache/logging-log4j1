/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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
import junit.framework.TestCase;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RFATestCase;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

/**
 *  Test of ExternallyRolledFileAppender.
 *
 * @author Curt Arnold
 */
public class ERFATestCase extends TestCase {

    /**
     * Create new instance of test.
     * @param name test name.
     */
  public ERFATestCase(final String name) {
    super(name);
  }

    /**
     * Reset configuration after test.
     */
  public void tearDown() {
      LogManager.resetConfiguration();
  }

    /**
     * Test ExternallyRolledFileAppender constructor.
     */
  public void testConstructor() {
      ExternallyRolledFileAppender appender =
              new ExternallyRolledFileAppender();
      assertEquals(0, appender.getPort());
  }

  public void testRolloverDoesNotWork() {
      ExternallyRolledFileAppender erfa =
              new ExternallyRolledFileAppender();

      int port = 5500;

      Logger logger = Logger.getLogger(RFATestCase.class);
      Logger root = Logger.getRootLogger();
      PatternLayout layout = new PatternLayout("%m\n");
      erfa.setLayout(layout);
      erfa.setAppend(false);
      erfa.setMaxBackupIndex(2);
      erfa.setPort(port);
      erfa.setFile("output/ERFA-test2.log");
      erfa.activateOptions();
      root.addAppender(erfa);

      // Write exactly 10 bytes with each log
      for (int i = 0; i < 55; i++) {
        if (i < 10) {
          logger.debug("Hello---" + i);
        } else {
          logger.debug("Hello--" + i);
        }
      }

      assertTrue(new File("output/ERFA-test2.log").exists());
      assertFalse(new File("output/ERFA-test2.log.1").exists());
  }
}
