/*
 * ============================================================================
 *                   The Apache Software License, Version 1.1
 * ============================================================================
 *
 *    Copyright (C) 1999 The Apache Software Foundation. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modifica-
 * tion, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of  source code must  retain the above copyright  notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any, must
 *    include  the following  acknowledgment:  "This product includes  software
 *    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
 *    Alternately, this  acknowledgment may  appear in the software itself,  if
 *    and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "log4j" and  "Apache Software Foundation"  must not be used to
 *    endorse  or promote  products derived  from this  software without  prior
 *    written permission. For written permission, please contact
 *    apache@apache.org.
 *
 * 5. Products  derived from this software may not  be called "Apache", nor may
 *    "Apache" appear  in their name,  without prior written permission  of the
 *    Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 * APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 * DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 * ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 * (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * This software  consists of voluntary contributions made  by many individuals
 * on  behalf of the Apache Software  Foundation.  For more  information on the
 * Apache Software Foundation, please see <http://www.apache.org/>.
 *
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
    PropertyConfigurator.configure("input/hierarchyThreshold1.properties");
    common();
    assertTrue(Compare.compare(TEMP, "witness/hierarchyThreshold.1"));
  }

  public void test2() throws Exception {
    PropertyConfigurator.configure("input/hierarchyThreshold2.properties");
    common();
    assertTrue(Compare.compare(TEMP, "witness/hierarchyThreshold.2"));
  }

  public void test3() throws Exception {
    PropertyConfigurator.configure("input/hierarchyThreshold3.properties");
    common();
    assertTrue(Compare.compare(TEMP, "witness/hierarchyThreshold.3"));
  }

  public void test4() throws Exception {
    PropertyConfigurator.configure("input/hierarchyThreshold4.properties");
    common();
    assertTrue(Compare.compare(TEMP, "witness/hierarchyThreshold.4"));
  }

  public void test5() throws Exception {
    PropertyConfigurator.configure("input/hierarchyThreshold5.properties");
    common();
    assertTrue(Compare.compare(TEMP, "witness/hierarchyThreshold.5"));
  }

  public void test6() throws Exception {
    PropertyConfigurator.configure("input/hierarchyThreshold6.properties");
    common();
    assertTrue(Compare.compare(TEMP, "witness/hierarchyThreshold.6"));
  }

  public void test7() throws Exception {
    PropertyConfigurator.configure("input/hierarchyThreshold7.properties");
    common();
    assertTrue(Compare.compare(TEMP, "witness/hierarchyThreshold.7"));
  }

  public void test8() throws Exception {
    PropertyConfigurator.configure("input/hierarchyThreshold8.properties");
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
