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

import java.text.SimpleDateFormat;
import java.util.Calendar;

import junit.framework.TestCase;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.util.Compare;


/**
 * 
 * This test case aims to unit test/reproduce problems encountered while
 * renaming the log file under windows.
 * 
 * @author Ceki
 *
 */
public class RenamingTest extends TestCase {
  Logger logger = Logger.getLogger(RenamingTest.class);
  
  Logger root;
  PatternLayout layout;
  
  public RenamingTest(String arg0) {
    super(arg0);
  }

  protected void setUp() throws Exception {
    super.setUp();
    root = Logger.getRootLogger();
    layout = new PatternLayout("%c{1} - %m%n");
    root.addAppender(new ConsoleAppender(new PatternLayout()));
    
  }

  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void testRename() throws Exception {
  
    RollingFileAppender rfa = new RollingFileAppender();
    rfa.setLayout(layout);

    // rollover by the second
    String datePattern = "yyyy-MM-dd_HH_mm_ss";
    SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
    String[] filenames = new String[2];

    TimeBasedRollingPolicy tbrp = new TimeBasedRollingPolicy();
    tbrp.setFileNamePattern("output/test-%d{" + datePattern + "}");
    tbrp.setActiveFileName("output/test.log");
    tbrp.activateOptions();
    rfa.setRollingPolicy(tbrp);
    rfa.activateOptions();

    Calendar cal = Calendar.getInstance();

    root.addAppender(rfa);
    logger.debug("Hello   " + 0);
    Thread.sleep(5000);
    logger.debug("Hello   " + 1);
    
    filenames[0] = "output/test-" + sdf.format(cal.getTime());
    filenames[1] = "output/test.log";

    for (int i = 0; i < filenames.length; i++) {
      assertTrue(Compare.compare(filenames[i], "witness/rolling/renaming." + i));
    }
  }
}
