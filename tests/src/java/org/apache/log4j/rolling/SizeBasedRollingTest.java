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


/**
 *
 * Do not forget to call activateOptions when configuring programatically.
 * 
 * @author Ceki G&uuml;lc&uuml;
 *
 */
public class SizeBasedRollingTest extends TestCase {
  Logger logger = Logger.getLogger(SizeBasedRollingTest.class);

  public SizeBasedRollingTest(String name) {
    super(name);
  }

  public void setUp() {
  }

  public void tearDown() {
    LogManager.shutdown();
  }

  public void test1() throws Exception {
    
    // this test is invalid because setting the activeFileName variable
    // is now mandatory.
    Logger root = Logger.getRootLogger();
    root.addAppender(new ConsoleAppender(new PatternLayout()));

    // We purposefully use the \n as the line separator. 
    // This makes the regression test system independent.
    PatternLayout layout = new PatternLayout("%m\n");
    RollingFileAppender rfa = new RollingFileAppender();
    rfa.setLayout(layout);

    SlidingWindowRollingPolicy swrp = new SlidingWindowRollingPolicy();
    SizeBasedTriggeringPolicy sbtp = new SizeBasedTriggeringPolicy();
    sbtp.setMaxFileSize(100);
    sbtp.activateOptions();
    swrp.setFileNamePattern("output/sizeBased-test1.%i");
    try {
      swrp.activateOptions();
      fail("The absence of activeFileName option should have caused an exception.");
    } catch(IllegalStateException e) {
      return;
    }
  }

  public void test2() throws Exception {
    Logger root = Logger.getRootLogger();
    root.addAppender(new ConsoleAppender(new PatternLayout()));

    PatternLayout layout = new PatternLayout("%m\n");
    RollingFileAppender rfa = new RollingFileAppender();
    rfa.setLayout(layout);

    SlidingWindowRollingPolicy swrp = new SlidingWindowRollingPolicy();
    SizeBasedTriggeringPolicy sbtp = new SizeBasedTriggeringPolicy();

    sbtp.setMaxFileSize(100);
    swrp.setActiveFileName("output/sizeBased-test2");
    swrp.setFileNamePattern("output/sizeBased-test2.%i");
    swrp.activateOptions();
    
    rfa.setRollingPolicy(swrp);
    rfa.setTriggeringPolicy(sbtp);
    rfa.activateOptions();
    root.addAppender(rfa);

    // Write exactly 10 bytes with each log
    for (int i = 0; i < 25; i++) {
      Thread.sleep(1000);
      if (i < 10) {
        logger.debug("Hello   " + i);
      } else if (i < 100) {
        logger.debug("Hello  " + i);
      }
    }

    // The File.length() method is not accurate under Windows    

     if(!isWindows()) {

      assertTrue(Compare.compare("output/sizeBased-test2.1",
         "witness/sizeBased-test1.1"));
      assertTrue(Compare.compare("output/sizeBased-test2.2",
         "witness/sizeBased-test1.2"));
      assertTrue(Compare.compare("output/sizeBased-test2.3",
         "witness/sizeBased-test1.3"));
     }
  }

  public void test3() throws Exception {
     Logger root = Logger.getRootLogger();
     root.addAppender(new ConsoleAppender(new PatternLayout()));

     PatternLayout layout = new PatternLayout("%m\n");
     RollingFileAppender rfa = new RollingFileAppender();
     rfa.setLayout(layout);

     SlidingWindowRollingPolicy swrp = new SlidingWindowRollingPolicy();
     SizeBasedTriggeringPolicy sbtp = new SizeBasedTriggeringPolicy();

     swrp.setCompressionMode("GZ");
     sbtp.setMaxFileSize(100);
     swrp.setActiveFileName("output/sizeBased-test3");
     swrp.setFileNamePattern("output/sizeBased-test3.%i");
     swrp.activateOptions();
     rfa.setRollingPolicy(swrp);
     rfa.setTriggeringPolicy(sbtp);
     rfa.activateOptions();
     root.addAppender(rfa);

     // Write exactly 10 bytes with each log
     for (int i = 0; i < 25; i++) {
       Thread.sleep(1000);
       if (i < 10) {
         logger.debug("Hello   " + i);
       } else if (i < 100) {
         logger.debug("Hello  " + i);
       }
     }
  }

  boolean isWindows() {
    return System.getProperty("os.name").indexOf("Windows") != -1;
  }

  public static Test xsuite() {
    TestSuite suite = new TestSuite();

    suite.addTest(new SizeBasedRollingTest("test1"));
    suite.addTest(new SizeBasedRollingTest("test2"));
    //suite.addTest(new SizeBasedRollingTestCase("test3"));

    return suite;
  }
}
