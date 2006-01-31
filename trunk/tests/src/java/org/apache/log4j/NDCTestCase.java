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

package org.apache.log4j;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.util.*;


/**
   Test the configuration of the hierarchy-wide threshold.

   @author  Ceki G&uuml;lc&uuml;
*/
public class NDCTestCase extends TestCase {
  static String TEMP = "output/temp";
  static Logger logger = Logger.getLogger(NDCTestCase.class);

  public NDCTestCase(String name) {
    super(name);
  }

  public void setUp() {
  }

  public void tearDown() {
    System.out.println("Tearing down test case.");
    logger.getLoggerRepository().resetConfiguration();
  }

  public void test1() throws Exception {
    PropertyConfigurator.configure("input/ndc/NDC1.properties");
    common();
    assertTrue(Compare.compare(TEMP, "witness/ndc/NDC.1"));
  }

  static void common() {
    commonLog();
    NDC.push("n1");
    commonLog();
    NDC.push("n2");
    NDC.push("n3");
    commonLog();
    NDC.pop();
    commonLog();
    NDC.clear();
    commonLog();
  }

  static void commonLog() {
    logger.debug("m1");
    logger.info("m2");
    logger.warn("m3");
    logger.error("m4");
    logger.fatal("m5");
  }

  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(new NDCTestCase("test1"));
    return suite;
  }
}
