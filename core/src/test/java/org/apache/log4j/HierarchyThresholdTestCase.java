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

package org.apache.log4j;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;

import org.apache.log4j.util.Compare;
import org.apache.log4j.xml.XLevel;

/**
   Test the configuration of the hierarchy-wide threshold.

   @author  Ceki G&uuml;lc&uuml;
*/
public class HierarchyThresholdTestCase extends TestCase {

  static final String FILE_PREFIX = "target/test-classes";
  static final String INPUT_DIR = FILE_PREFIX + "/input";
  static final String WITNESS_DIR = FILE_PREFIX + "/witness";
  
  static String TEMP = "output/temp";
  static Logger logger = Logger.getLogger(HierarchyThresholdTestCase.class);

  public HierarchyThresholdTestCase(String name) {
    super(name);
  }

  public void setUp() {
  }
  
  public void tearDown() {
    System.out.println("Tearing down test case.");
    logger.getLoggerRepository().resetConfiguration();
  }
  
  public void test1() throws Exception {
    PropertyConfigurator.configure(INPUT_DIR + "/hierarchyThreshold1.properties");
    common();
    assertTrue(Compare.compare(TEMP, WITNESS_DIR + "/hierarchyThreshold.1"));
  }

  public void test2() throws Exception {
    PropertyConfigurator.configure(INPUT_DIR + "/hierarchyThreshold2.properties");
    common();
    assertTrue(Compare.compare(TEMP, WITNESS_DIR + "/hierarchyThreshold.2"));
  }

  public void test3() throws Exception {
    PropertyConfigurator.configure(INPUT_DIR + "/hierarchyThreshold3.properties");
    common();
    assertTrue(Compare.compare(TEMP, WITNESS_DIR + "/hierarchyThreshold.3"));
  }

  public void test4() throws Exception {
    PropertyConfigurator.configure(INPUT_DIR + "/hierarchyThreshold4.properties");
    common();
    assertTrue(Compare.compare(TEMP, WITNESS_DIR + "/hierarchyThreshold.4"));
  }

  public void test5() throws Exception {
    PropertyConfigurator.configure(INPUT_DIR + "/hierarchyThreshold5.properties");
    common();
    assertTrue(Compare.compare(TEMP, WITNESS_DIR + "/hierarchyThreshold.5"));
  }

  public void test6() throws Exception {
    PropertyConfigurator.configure(INPUT_DIR + "/hierarchyThreshold6.properties");
    common();
    assertTrue(Compare.compare(TEMP, WITNESS_DIR + "/hierarchyThreshold.6"));
  }

  public void test7() throws Exception {
    PropertyConfigurator.configure(INPUT_DIR + "/hierarchyThreshold7.properties");
    common();
    assertTrue(Compare.compare(TEMP, WITNESS_DIR + "/hierarchyThreshold.7"));
  }

  public void test8() throws Exception {
    PropertyConfigurator.configure(INPUT_DIR + "/hierarchyThreshold8.properties");
    common();
    assertTrue(Compare.compare(TEMP, WITNESS_DIR + "/hierarchyThreshold.8"));
  }


  static 
  void common() {
    String oldThreadName = Thread.currentThread().getName();
    Thread.currentThread().setName("main");

    logger.log(XLevel.TRACE, "m0");
    logger.debug("m1");
    logger.info("m2");
    logger.warn("m3");
    logger.error("m4");
    logger.fatal("m5");

    Thread.currentThread().setName(oldThreadName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(new HierarchyThresholdTestCase("test1"));
    suite.addTest(new HierarchyThresholdTestCase("test2"));
    suite.addTest(new HierarchyThresholdTestCase("test3"));
    suite.addTest(new HierarchyThresholdTestCase("test4"));
    suite.addTest(new HierarchyThresholdTestCase("test5"));
    suite.addTest(new HierarchyThresholdTestCase("test6"));
    suite.addTest(new HierarchyThresholdTestCase("test7"));
    suite.addTest(new HierarchyThresholdTestCase("test8"));
    return suite;
  }
}
