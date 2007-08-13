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
import junit.framework.TestSuite;
import junit.framework.Test;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.log4j.util.Filter;
import org.apache.log4j.util.LineNumberFilter;
import org.apache.log4j.util.ControlFilter;
import org.apache.log4j.util.ISO8601Filter;
import org.apache.log4j.util.Transformer;
import org.apache.log4j.util.Compare;

public class ErrorHandlerTestCase extends TestCase {

  static String TEMP_A1 = "output/temp.A1";
  static String TEMP_A2 = "output/temp.A2";
  static String FILTERED_A1 = "output/filtered.A1";
  static String FILTERED_A2 = "output/filtered.A2";


  static String EXCEPTION1 = "java.lang.Exception: Just testing";
  static String EXCEPTION2 = "\\s*at .*\\(.*:\\d{1,4}\\)";
  static String EXCEPTION3 = "\\s*at .*\\(Native Method\\)";

  static String TEST1_1A_PAT = 
                       "(DEBUG|INFO |WARN |ERROR|FATAL) \\w*\\.\\w* - Message \\d";

  static String TEST1_1B_PAT = "(DEBUG|INFO |WARN |ERROR|FATAL) root - Message \\d";

  static String TEST1_2_PAT = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2},\\d{3} "+
                        "\\[main]\\ (DEBUG|INFO|WARN|ERROR|FATAL) .* - Message \\d";



  Logger root; 
  Logger logger;

  public ErrorHandlerTestCase(String name) {
    super(name);
  }

  public void setUp() {
    root = Logger.getRootLogger();
    logger = Logger.getLogger("test");
  }

  public void tearDown() {  
    root.getLoggerRepository().resetConfiguration();
  }

  public void test1() throws Exception {
    DOMConfigurator.configure("input/xml/fallback1.xml");
    common();

    ControlFilter cf1 = new ControlFilter(new String[]{TEST1_1A_PAT, TEST1_1B_PAT, 
					       EXCEPTION1, EXCEPTION2, EXCEPTION3});

    ControlFilter cf2 = new ControlFilter(new String[]{TEST1_2_PAT, 
					       EXCEPTION1, EXCEPTION2, EXCEPTION3});

    Transformer.transform(TEMP_A1, FILTERED_A1, new Filter[] {cf1, 
							new LineNumberFilter()});

    Transformer.transform(TEMP_A2, FILTERED_A2, new Filter[] {cf2,
                                      new LineNumberFilter(), new ISO8601Filter()});

    assertTrue(Compare.compare(FILTERED_A1, "witness/dom.A1.1"));
    assertTrue(Compare.compare(FILTERED_A2, "witness/dom.A2.1"));
  }

  void common() {
    int i = -1;
 
    logger.debug("Message " + ++i);
    root.debug("Message " + i);        

    logger.info ("Message " + ++i);
    root.info("Message " + i);        

    logger.warn ("Message " + ++i);
    root.warn("Message " + i);        

    logger.error("Message " + ++i);
    root.error("Message " + i);
    
    logger.log(Level.FATAL, "Message " + ++i);
    root.log(Level.FATAL, "Message " + i);    
    
    Exception e = new Exception("Just testing");
    logger.debug("Message " + ++i, e);
    root.debug("Message " + i, e);
    
    logger.error("Message " + ++i, e);
    root.error("Message " + i, e);    

  }

  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(new ErrorHandlerTestCase("test1"));
    return suite;
  }

}
