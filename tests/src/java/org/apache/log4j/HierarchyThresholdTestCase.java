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

/* Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.log4j;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.util.*;
import org.apache.log4j.xml.XLevel;


/**
   Test the configuration of the hierarchy-wide threshold.

   @author  Ceki G&uuml;lc&uuml;
*/
public class HierarchyThresholdTestCase extends TestCase {
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
    PropertyConfigurator.configure("input/hierarchy/hierarchyThreshold1.properties");
    common();
    assertTrue(Compare.compare(TEMP, "witness/hierarchyThreshold.1"));
  }

  public void test2() throws Exception {
    PropertyConfigurator.configure("input/hierarchy/hierarchyThreshold2.properties");
    common();
    assertTrue(Compare.compare(TEMP, "witness/hierarchyThreshold.2"));
  }

  public void test3() throws Exception {
    PropertyConfigurator.configure("input/hierarchy/hierarchyThreshold3.properties");
    common();
    assertTrue(Compare.compare(TEMP, "witness/hierarchyThreshold.3"));
  }

  public void test4() throws Exception {
    PropertyConfigurator.configure("input/hierarchy/hierarchyThreshold4.properties");
    common();
    assertTrue(Compare.compare(TEMP, "witness/hierarchyThreshold.4"));
  }

  public void test5() throws Exception {
    PropertyConfigurator.configure("input/hierarchy/hierarchyThreshold5.properties");
    common();
    assertTrue(Compare.compare(TEMP, "witness/hierarchyThreshold.5"));
  }

  public void test6() throws Exception {
    PropertyConfigurator.configure("input/hierarchy/hierarchyThreshold6.properties");
    common();
    assertTrue(Compare.compare(TEMP, "witness/hierarchyThreshold.6"));
  }

  public void test7() throws Exception {
    PropertyConfigurator.configure("input/hierarchy/hierarchyThreshold7.properties");
    common();
    assertTrue(Compare.compare(TEMP, "witness/hierarchyThreshold.7"));
  }

  public void test8() throws Exception {
    PropertyConfigurator.configure("input/hierarchy/hierarchyThreshold8.properties");
    common();
    assertTrue(Compare.compare(TEMP, "witness/hierarchyThreshold.8"));
  }

  static void common() {
    logger.log(XLevel.TRACE, "m0");
    logger.debug("m1");
    logger.info("m2");
    logger.warn("m3");
    logger.error("m4");
    logger.fatal("m5");
  }

  public static Test XXXsuite() {
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
