/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.APL file.  */



package org.apache.log4j;

import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.TestFailure;
import junit.framework.Test;


public class UnitTestDRFA extends TestCase {

  public UnitTestDRFA(String name) {
    super(name);
  }

  public
  void testComputeCheckPeriod() {
    DailyRollingFileAppender drfa = new DailyRollingFileAppender();
    drfa.setName("testComputeCheckPeriod");
    drfa.setOption(DailyRollingFileAppender.DATE_PATTERN_OPTION, 
		   "yyyy-MM-dd.'log'");
    assertEquals(drfa.computeCheckPeriod(), 
		 DailyRollingFileAppender.TOP_OF_DAY);

    drfa.setOption(DailyRollingFileAppender.DATE_PATTERN_OPTION, 
		   "yyyy-MM-dd mm.'log'");
    assertEquals(drfa.computeCheckPeriod(), 
		 DailyRollingFileAppender.TOP_OF_MINUTE);

    drfa.setOption(DailyRollingFileAppender.DATE_PATTERN_OPTION, 
		   "yyyy-MM-dd a.'log'");
    assertEquals(drfa.computeCheckPeriod(), 
		 DailyRollingFileAppender.HALF_DAY);

    drfa.setOption(DailyRollingFileAppender.DATE_PATTERN_OPTION, 
		   "yyyy-MM-dd HH.'log'");
    assertEquals(drfa.computeCheckPeriod(), 
		 DailyRollingFileAppender.TOP_OF_HOUR);

    drfa.setOption(DailyRollingFileAppender.DATE_PATTERN_OPTION, 
		   "yyyy-MM.'log'");
    assertEquals(drfa.computeCheckPeriod(), 
		 DailyRollingFileAppender.TOP_OF_MONTH);

    drfa.setOption(DailyRollingFileAppender.DATE_PATTERN_OPTION, 
		   "HH'log'");
    assertEquals(drfa.computeCheckPeriod(), 
		 DailyRollingFileAppender.TOP_OF_HOUR);


  }


  public
  void testRC1() {  
    RollingCalendar rc = new RollingCalendar();
    rc.setType(DailyRollingFileAppender.TOP_OF_DAY);


    Calendar c = Calendar.getInstance();
    c.clear();
    c.set(Calendar.YEAR, 20);
    c.set(Calendar.MONTH, 2);
    c.set(Calendar.DAY_OF_MONTH, 10); 
    c.set(Calendar.HOUR_OF_DAY, 1);
    c.set(Calendar.MINUTE, 10); 
    c.set(Calendar.SECOND, 10);
    c.set(Calendar.MILLISECOND, 88);
    
    c.setTime(rc.getNextCheckDate(c.getTime()));
    assertEquals(c.get(Calendar.DAY_OF_MONTH), 11);
    assertEquals(c.get(Calendar.HOUR_OF_DAY), 0);
    assertEquals(c.get(Calendar.MINUTE), 0);
    assertEquals(c.get(Calendar.SECOND), 0);
    assertEquals(c.get(Calendar.MILLISECOND), 0);
  }

  public
  void testRC2() {  
    RollingCalendar rc = new RollingCalendar();
    rc.setType(DailyRollingFileAppender.TOP_OF_DAY);


    Calendar c = Calendar.getInstance();
    c.clear();
    c.set(Calendar.YEAR, 20);
    c.set(Calendar.MONTH, Calendar.JANUARY); 
    c.set(Calendar.DATE, 31); 
    c.set(Calendar.HOUR_OF_DAY, 1);
    c.set(Calendar.MINUTE, 10); 
    c.set(Calendar.SECOND, 10);
    c.set(Calendar.MILLISECOND, 88);

    System.out.println(c);
    System.out.println("\n");

    c.setTime(rc.getNextCheckDate(c.getTime()));
    assertEquals(c.get(Calendar.MONTH), Calendar.FEBRUARY);
    assertEquals(c.get(Calendar.DATE), 1);
    assertEquals(c.get(Calendar.HOUR_OF_DAY), 0);
    assertEquals(c.get(Calendar.MINUTE), 0);
    assertEquals(c.get(Calendar.SECOND), 0);
    assertEquals(c.get(Calendar.MILLISECOND), 0);

    System.out.println(c);
  }

  
  public 
  static
  Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(new UnitTestDRFA("testComputeCheckPeriod"));
    suite.addTest(new UnitTestDRFA("testRC1"));
    suite.addTest(new UnitTestDRFA("testRC2"));
    return suite;
  }
  
}
