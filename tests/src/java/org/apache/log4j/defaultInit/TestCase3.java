/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j.defaultInit;

import junit.framework.TestCase;

import java.util.Enumeration;

import org.apache.log4j.*;

public class TestCase3 extends TestCase {

  public TestCase3(String name) {
    super(name);
  }

  public void setUp() {
  }

  public void tearDown() {
    LogManager.shutdown();
  }

  public void testProperties() {
    Logger root = Logger.getRootLogger();
    boolean rootIsConfigured = root.getAllAppenders().hasMoreElements();
    assertTrue(rootIsConfigured);
    Enumeration e = root.getAllAppenders();
    Appender appender = (Appender) e.nextElement();
    assertEquals(appender.getName(), "D3");
  }

/**  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(new TestCase3("propertiesTest"));
    return suite;
  }
**/
}

