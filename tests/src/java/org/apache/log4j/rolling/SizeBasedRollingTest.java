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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.util.Compare;

import java.io.File;


/**
 *
 * Do not forget to call activateOptions when configuring programatically.
 * 
 * @author Ceki G&uuml;lc&uuml;
 *
 */
public class SizeBasedRollingTest extends TestCase {
  Logger logger = Logger.getLogger(SizeBasedRollingTest.class);
  Logger root = Logger.getRootLogger();

  public SizeBasedRollingTest(String name) {
    super(name);
  }

  public void setUp() {
    Appender ca = new ConsoleAppender(new PatternLayout("%d %level %c -%m%n"));
    ca.setName("CONSOLE");
    root.addAppender(ca);    
  }

  public void tearDown() {
    LogManager.shutdown();
  }

    /**
     * Test whether FixedWindowRollingPolicy throws an exception when
     * the ActiveFileName is not set.
     */
  public void test1() throws Exception {    
    // We purposefully use the \n as the line separator. 
    // This makes the regression test system independent.
    PatternLayout layout = new PatternLayout("%m\n");
    RollingFileAppender rfa = new RollingFileAppender();
    rfa.setLayout(layout);

    FixedWindowRollingPolicy fwrp = new FixedWindowRollingPolicy();
    SizeBasedTriggeringPolicy sbtp = new SizeBasedTriggeringPolicy();
    sbtp.setMaxFileSize(100);
    sbtp.activateOptions();
    fwrp.setFileNamePattern("output/sizeBased-test1.%i");
    try {
      fwrp.activateOptions();
      fail("The absence of activeFileName option should have caused an exception.");
    } catch(IllegalStateException e) {
      return;
    }
  }

    /** 
     * Test basic rolling functionality. 
     */ 
  public void test2() throws Exception {
    PatternLayout layout = new PatternLayout("%m\n");
    RollingFileAppender rfa = new RollingFileAppender();
    rfa.setName("ROLLING"); 
    rfa.setLayout(layout);

    FixedWindowRollingPolicy swrp = new FixedWindowRollingPolicy();
    SizeBasedTriggeringPolicy sbtp = new SizeBasedTriggeringPolicy();

    sbtp.setMaxFileSize(100);
    swrp.setMinIndex(0);
    swrp.setActiveFileName("output/sizeBased-test2.log");

    swrp.setFileNamePattern("output/sizeBased-test2.%i");
    swrp.activateOptions();
    
    rfa.setRollingPolicy(swrp);
    rfa.setTriggeringPolicy(sbtp);
    rfa.activateOptions();
    root.addAppender(rfa);

    // Write exactly 10 bytes with each log
    for (int i = 0; i < 25; i++) {
      Thread.sleep(100);
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

    /**
     * Same as testBasic but also with GZ compression.
     */
  public void test3() throws Exception {
     PatternLayout layout = new PatternLayout("%m\n");
     RollingFileAppender rfa = new RollingFileAppender();
     rfa.setLayout(layout);

     FixedWindowRollingPolicy  fwrp = new FixedWindowRollingPolicy();
     SizeBasedTriggeringPolicy sbtp = new SizeBasedTriggeringPolicy();

     sbtp.setMaxFileSize(100);
     fwrp.setMinIndex(0);
     fwrp.setActiveFileName("output/sbr-test3.log");
     fwrp.setFileNamePattern("output/sbr-test3.%i.gz");
     fwrp.activateOptions();
     rfa.setRollingPolicy(fwrp);
     rfa.setTriggeringPolicy(sbtp);
     rfa.activateOptions();
     root.addAppender(rfa);

     // Write exactly 10 bytes with each log
     for (int i = 0; i < 25; i++) {
       Thread.sleep(100);
       if (i < 10) {
         logger.debug("Hello---" + i);
       } else if (i < 100) {
         logger.debug("Hello--" + i);
       }
     }

    assertTrue(new File("output/sbr-test3.log").exists());
    assertTrue(new File("output/sbr-test3.0.gz").exists());
    assertTrue(new File("output/sbr-test3.1.gz").exists());

    assertTrue(Compare.compare("output/sbr-test3.log",  "witness/rolling/sbr-test3.log"));
    assertTrue(Compare.gzCompare("output/sbr-test3.0.gz", "witness/rolling/sbr-test3.0.gz"));
    assertTrue(Compare.gzCompare("output/sbr-test3.1.gz", "witness/rolling/sbr-test3.1.gz"));
  }


    /**
     * Build test suite using this class and ObsoleteRollingFileAppenderTest.
     *
     * @deprecated Marked deprecated since suite contains tests of deprecated classes
     * @return test suite.
     */
  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTestSuite(SizeBasedRollingTest.class);
    suite.addTestSuite(ObsoleteRollingFileAppenderTest.class);
    return suite;
  }
}
