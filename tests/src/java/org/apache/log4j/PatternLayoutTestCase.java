/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.apache.log4j.MDC;
import org.apache.log4j.NDC;

import org.apache.log4j.util.LineNumberFilter;
import org.apache.log4j.util.Transformer;
import org.apache.log4j.util.Filter;
import org.apache.log4j.util.ControlFilter;
import org.apache.log4j.util.Compare;
import org.apache.log4j.util.ISO8601Filter;
import org.apache.log4j.util.AbsoluteTimeFilter;
import org.apache.log4j.util.RelativeTimeFilter;
import org.apache.log4j.util.SunReflectFilter;
import org.apache.log4j.util.AbsoluteDateAndTimeFilter;

public class PatternLayoutTestCase extends TestCase {

  static String TEMP = "output/temp";
  static String FILTERED = "output/filtered";

  Logger root; 
  Logger logger;

  static String EXCEPTION1 = "java.lang.Exception: Just testing";
  static String EXCEPTION2 = "\\s*at .*\\(.*:\\d{1,4}\\)";
  static String EXCEPTION3 = "\\s*at .*\\(Native Method\\)";

  static String PAT0 = "\\[main]\\ (DEBUG|INFO|WARN|ERROR|FATAL) .* - Message \\d{1,2}";
  static String PAT1 = Filter.ISO8601_PAT + " " + PAT0;
  static String PAT2 = Filter.ABSOLUTE_DATE_AND_TIME_PAT+ " " + PAT0;
  static String PAT3 = Filter.ABSOLUTE_TIME_PAT+ " " + PAT0;
  static String PAT4 = Filter.RELATIVE_TIME_PAT+ " " + PAT0;

  static String PAT5 = "\\[main]\\ (DEBUG|INFO|WARN|ERROR|FATAL) .* : Message \\d{1,2}";
  static String PAT6 = "\\[main]\\ (DEBUG|INFO |WARN |ERROR|FATAL) org.apache.log4j.PatternLayoutTestCase.common\\(PatternLayoutTestCase.java:\\d{1,4}\\): Message \\d{1,2}";

  static String PAT11a = "^(DEBUG|INFO |WARN |ERROR|FATAL) \\[main]\\ log4j.PatternLayoutTestCase: Message \\d{1,2}";
  static String PAT11b = "^(DEBUG|INFO |WARN |ERROR|FATAL) \\[main]\\ root: Message \\d{1,2}";

  static String PAT12 = "^\\[main]\\ (DEBUG|INFO |WARN |ERROR|FATAL) "+
    "org.apache.log4j.PatternLayoutTestCase.common\\(PatternLayoutTestCase.java:\\d{3}\\): "+
    "Message \\d{1,2}";

  static String PAT13 = "^\\[main]\\ (DEBUG|INFO |WARN |ERROR|FATAL) "+
    "apache.log4j.PatternLayoutTestCase.common\\(PatternLayoutTestCase.java:\\d{3}\\): "+
    "Message \\d{1,2}";

  static String PAT14 = "^(DEBUG| INFO| WARN|ERROR|FATAL)\\ \\d{1,2}\\ *- Message \\d{1,2}";

  static String PAT_MDC_1 = "";

  public PatternLayoutTestCase(String name) {
    super(name);
  }

  public void setUp() {
    root = Logger.getRootLogger();
    logger = Logger.getLogger(PatternLayoutTestCase.class);
  }

  public void tearDown() {  
    root.getLoggerRepository().resetConfiguration();
  }

  public void test1() throws Exception {
    PropertyConfigurator.configure("input/patternLayout1.properties");
    common();
    Transformer.transform(TEMP, FILTERED, new Filter[] {
                                          new LineNumberFilter(),
                                          new SunReflectFilter()});
    assertTrue(Compare.compare(FILTERED, "witness/patternLayout.1"));
  }

  public void test2() throws Exception {
    PropertyConfigurator.configure("input/patternLayout2.properties");
    common();
    ControlFilter cf1 = new ControlFilter(new String[]{PAT1, EXCEPTION1, 
						       EXCEPTION2, EXCEPTION3});
    Transformer.transform(TEMP, FILTERED, new Filter[] {cf1, 
                                                        new LineNumberFilter(), 
                                                        new ISO8601Filter(),
                                                        new SunReflectFilter()});
    assertTrue(Compare.compare(FILTERED, "witness/patternLayout.2"));
  }

  public void test3() throws Exception {
    PropertyConfigurator.configure("input/patternLayout3.properties");
    common();
    ControlFilter cf1 = new ControlFilter(new String[]{PAT1, EXCEPTION1, 
						       EXCEPTION2, EXCEPTION3});
    Transformer.transform(TEMP, FILTERED, new Filter[] {cf1, new LineNumberFilter(), 
							  new ISO8601Filter(), 
                                                          new SunReflectFilter()});
    assertTrue(Compare.compare(FILTERED, "witness/patternLayout.3"));
  }

  // Output format:
  // 06 avr. 2002 18:30:58,937 [main] DEBUG rnLayoutTestCase - Message 0  
  public void test4() throws Exception {
    PropertyConfigurator.configure("input/patternLayout4.properties");
    common();
    ControlFilter cf1 = new ControlFilter(new String[]{PAT2, EXCEPTION1, 
						       EXCEPTION2, EXCEPTION3});
    Transformer.transform(TEMP, FILTERED, new Filter[] {cf1, 
                                                        new LineNumberFilter(), 
                                                        new AbsoluteDateAndTimeFilter(),
                                                        new SunReflectFilter()});
    assertTrue(Compare.compare(FILTERED, "witness/patternLayout.4"));
  }

  public void test5() throws Exception {
    PropertyConfigurator.configure("input/patternLayout5.properties");
    common();
    ControlFilter cf1 = new ControlFilter(new String[]{PAT2, EXCEPTION1, 
						       EXCEPTION2, EXCEPTION3});
    Transformer.transform(TEMP, FILTERED, new Filter[] {cf1, 
                                                        new LineNumberFilter(), 
                                                        new AbsoluteDateAndTimeFilter(),
                                                        new SunReflectFilter()});
    assertTrue(Compare.compare(FILTERED, "witness/patternLayout.5"));
  }

  // 18:54:19,201 [main] DEBUG rnLayoutTestCase - Message 0
  public void test6() throws Exception {
    PropertyConfigurator.configure("input/patternLayout6.properties");
    common();
    ControlFilter cf1 = new ControlFilter(new String[]{PAT3, EXCEPTION1, 
						       EXCEPTION2, EXCEPTION3});
    Transformer.transform(TEMP, FILTERED, new Filter[] {cf1, 
                                                        new LineNumberFilter(), 
                                                        new AbsoluteTimeFilter(),
                                                        new SunReflectFilter()});
    assertTrue(Compare.compare(FILTERED, "witness/patternLayout.6"));
  }


  public void test7() throws Exception {
    PropertyConfigurator.configure("input/patternLayout7.properties");
    common();
    ControlFilter cf1 = new ControlFilter(new String[]{PAT3, EXCEPTION1, 
						       EXCEPTION2, EXCEPTION3});
    Transformer.transform(TEMP, FILTERED, new Filter[] {cf1, 
                                                        new LineNumberFilter(), 
                                                        new AbsoluteTimeFilter(),
                                                        new SunReflectFilter()});
    assertTrue(Compare.compare(FILTERED, "witness/patternLayout.7"));
  }

  public void test8() throws Exception {
    PropertyConfigurator.configure("input/patternLayout8.properties");
    common();
    ControlFilter cf1 = new ControlFilter(new String[]{PAT4, EXCEPTION1, 
						       EXCEPTION2, EXCEPTION3});
    Transformer.transform(TEMP, FILTERED, new Filter[] {cf1, 
                                                        new LineNumberFilter(), 
                                                        new RelativeTimeFilter(),
                                                        new SunReflectFilter()});
    assertTrue(Compare.compare(FILTERED, "witness/patternLayout.8"));
  }

  public void test9() throws Exception {
    PropertyConfigurator.configure("input/patternLayout9.properties");
    common();
    ControlFilter cf1 = new ControlFilter(new String[]{PAT5, EXCEPTION1, 
						       EXCEPTION2, EXCEPTION3});
    Transformer.transform(TEMP, FILTERED, new Filter[] {cf1, new LineNumberFilter(),
                                                        new SunReflectFilter()});
    assertTrue(Compare.compare(FILTERED, "witness/patternLayout.9"));
  }

  public void test10() throws Exception {
    PropertyConfigurator.configure("input/patternLayout10.properties");
    common();
    ControlFilter cf1 = new ControlFilter(new String[]{PAT6, EXCEPTION1, 
						       EXCEPTION2, EXCEPTION3});
    Transformer.transform(TEMP, FILTERED, new Filter[] {cf1,   
                                                      new LineNumberFilter(),
                                                      new SunReflectFilter()});
    assertTrue(Compare.compare(FILTERED, "witness/patternLayout.10"));
  }

  public void test11() throws Exception {
    PropertyConfigurator.configure("input/patternLayout11.properties");
    common();
    ControlFilter cf1 = new ControlFilter(new String[]{PAT11a, PAT11b, EXCEPTION1, 
						       EXCEPTION2, EXCEPTION3});
    Transformer.transform(TEMP, FILTERED, new Filter[] {cf1,   
                                                      new LineNumberFilter(),
                                                      new SunReflectFilter()});
    assertTrue(Compare.compare(FILTERED, "witness/patternLayout.11"));
  }

  public void test12() throws Exception {
    PropertyConfigurator.configure("input/patternLayout12.properties");
    common();
    ControlFilter cf1 = new ControlFilter(new String[]{PAT12, EXCEPTION1, 
						       EXCEPTION2, EXCEPTION3});
    Transformer.transform(TEMP, FILTERED, new Filter[] {cf1,   
                                                      new LineNumberFilter(),
                                                      new SunReflectFilter()});
    assertTrue(Compare.compare(FILTERED, "witness/patternLayout.12"));
  }

  public void test13() throws Exception {
    PropertyConfigurator.configure("input/patternLayout13.properties");
    common();
    ControlFilter cf1 = new ControlFilter(new String[]{PAT13, EXCEPTION1, 
						       EXCEPTION2, EXCEPTION3});
    Transformer.transform(TEMP, FILTERED, new Filter[] {cf1,   
                                                      new LineNumberFilter(),
                                                      new SunReflectFilter()});
    assertTrue(Compare.compare(FILTERED, "witness/patternLayout.13"));
  }

  public void test14() throws Exception {
    PropertyConfigurator.configure("input/patternLayout14.properties");
    common();
    ControlFilter cf1 = new ControlFilter(new String[]{PAT14, EXCEPTION1, 
						       EXCEPTION2, EXCEPTION3});
    Transformer.transform(TEMP, FILTERED, new Filter[] {cf1,   
                                                      new LineNumberFilter(), 
                                                      new SunReflectFilter()});
    assertTrue(Compare.compare(FILTERED, "witness/patternLayout.14"));
  }

  public void testMDCAllowAllKeys() throws Exception {
    PropertyConfigurator.configure("input/patternLayout.mdc.1.properties");
    MDC.put("key1", "va11");
    MDC.put("key2", "va12");
    logger.debug("Hello World");
    MDC.clear();

    Transformer.transform(TEMP, FILTERED, new Filter[] {
                                                      new LineNumberFilter(),
                                                      new SunReflectFilter()});
    assertTrue(Compare.compare(FILTERED, "witness/patternLayout.mdc.1"));

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
    logger.info("Message " + ++i, e);
    logger.warn("Message " + ++i , e);
    logger.error("Message " + ++i, e);
    logger.log(Level.FATAL, "Message " + ++i, e);
  }


  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(new PatternLayoutTestCase("test1"));
    suite.addTest(new PatternLayoutTestCase("test2"));
    suite.addTest(new PatternLayoutTestCase("test3"));
    suite.addTest(new PatternLayoutTestCase("test4"));
    suite.addTest(new PatternLayoutTestCase("test5"));
    suite.addTest(new PatternLayoutTestCase("test6"));
    suite.addTest(new PatternLayoutTestCase("test7"));
    suite.addTest(new PatternLayoutTestCase("test8"));
    suite.addTest(new PatternLayoutTestCase("test9"));
    suite.addTest(new PatternLayoutTestCase("test10"));
    suite.addTest(new PatternLayoutTestCase("test11"));
    suite.addTest(new PatternLayoutTestCase("test12"));
    suite.addTest(new PatternLayoutTestCase("test13"));
    suite.addTest(new PatternLayoutTestCase("test14"));
    suite.addTest(new PatternLayoutTestCase("testMDCAllowAllKeys"));

    return suite;
  }

}
