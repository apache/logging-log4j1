/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j.defaultInit;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;

import java.util.Vector;
import java.util.Enumeration;

import org.apache.log4j.*;
import org.apache.log4j.performance.NullAppender;

public class TestCase4 extends TestCase {

  public TestCase4(String name) {
    super(name);
  }

  public void setUp() {
  }

  public void tearDown() {
    LogManager.shutdown();
  }

  public void combinedTest() {
    Logger root = Logger.getRootLogger();
    boolean rootIsConfigured = root.getAllAppenders().hasMoreElements();
    assertTrue(rootIsConfigured);
    Enumeration e = root.getAllAppenders();
    Appender appender = (Appender) e.nextElement();
    assertEquals(appender.getName(), "D1");
    assertEquals(e.hasMoreElements(), false);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(new TestCase4("combinedTest"));
    return suite;
  }

}

