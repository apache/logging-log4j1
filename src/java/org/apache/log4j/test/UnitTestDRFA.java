/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.APL file.  */



package org.apache.log4j;


import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.TestFailure;
import junit.framework.Test;


public class UnitTestDRFA extends TestCase {

  public UnitTestDRFA(String name) {
    super(name);
  }

  public
  void test1() {
    DailyRollingFileAppender drfa = new DailyRollingFileAppender();
    drfa.setOption(DailyRollingFileAppender.DATE_PATTERN_OPTION, 
		   "YY-MM-dd");

    assertEquals(drfa.computeCheckPeriod(), 
		 DailyRollingFileAppender.TOP_OF_DAY);
  }

  public 
  static
  Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(new UnitTestDRFA("test1"));
    return suite;
  }
  
}
