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
import org.apache.log4j.util.*;
import org.apache.log4j.MDCOrderFilter;
import org.apache.log4j.spi.ThrowableInformation;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.io.*;


public class EnhancedPatternLayoutTestCase extends TestCase {
  static String TEMP = TestFile.temp(EnhancedPatternLayoutTestCase.class);
  static String FILTERED = TestFile.filtered(EnhancedPatternLayoutTestCase.class);
  static String EXCEPTION1 = "java.lang.Exception: Just testing";
  static String EXCEPTION2 = "\\s*at .*\\(.*\\)";
  static String EXCEPTION3 = "\\s*at .*\\((Native Method|Unknown Source)\\)";
  static String EXCEPTION4 = "\\s*at .*\\(.*Compiled Code\\)";

  static String PAT0 =
    "\\[main]\\ (DEBUG|INFO|WARN|ERROR|FATAL) .* - Message \\d{1,2}";
  static String PAT1 = Filter.ISO8601_PAT + " " + PAT0;
  static String PAT2 = Filter.ABSOLUTE_DATE_AND_TIME_PAT + " " + PAT0;
  static String PAT3 = Filter.ABSOLUTE_TIME_PAT + " " + PAT0;
  static String PAT4 = Filter.RELATIVE_TIME_PAT + " " + PAT0;
  static String PAT5 =
    "\\[main]\\ (DEBUG|INFO|WARN|ERROR|FATAL) .* : Message \\d{1,2}";
  static String PAT6 =
    "\\[main]\\ (DEBUG|INFO |WARN |ERROR|FATAL) org.apache.log4j.EnhancedPatternLayoutTestCase.common\\(EnhancedPatternLayoutTestCase.java(:\\d{1,4})?\\): Message \\d{1,2}";
  static String PAT11a =
    "^(DEBUG|INFO |WARN |ERROR|FATAL) \\[main]\\ log4j.EnhancedPatternLayoutTest: Message \\d{1,2}";
  static String PAT11b =
    "^(DEBUG|INFO |WARN |ERROR|FATAL) \\[main]\\ root: Message \\d{1,2}";
  static String PAT12 =
    "^\\[main]\\ (DEBUG|INFO |WARN |ERROR|FATAL) "
    + "org.apache.log4j.EnhancedPatternLayoutTestCase.common\\(EnhancedPatternLayoutTestCase.java:\\d{3}\\): "
    + "Message \\d{1,2}";
  static String PAT13 =
    "^\\[main]\\ (DEBUG|INFO |WARN |ERROR|FATAL) "
    + "apache.log4j.EnhancedPatternLayoutTestCase.common\\(EnhancedPatternLayoutTestCase.java:\\d{3}\\): "
    + "Message \\d{1,2}";
  static String PAT14 =
    "^(TRACE|DEBUG| INFO| WARN|ERROR|FATAL)\\ \\d{1,2}\\ *- Message \\d{1,2}";
  static String PAT_MDC_1 = "";
  Logger root;
  Logger logger;
  

  public EnhancedPatternLayoutTestCase(final String name) {
    super(name);
  }

  public void setUp() {
    root = Logger.getRootLogger();
    logger = Logger.getLogger(EnhancedPatternLayoutTest.class);
  }

  public void tearDown() {
    root.getLoggerRepository().resetConfiguration();
  }

    /**
     * Configures log4j from a properties file resource in class loader path.
     * @param fileName resource name, only last element is significant.
     * @throws IOException if resource not found or error reading resource.
     */
  private static void configure(final String fileName) throws IOException {
        PropertyConfigurator.configure(fileName);
  }

    /**
     * Compares actual and expected files.
     * @param actual file name for file generated by test
     * @param expected resource name containing expected output
     * @return true if files are the same after adjustments
     * @throws IOException if IO error during comparison.
     */
  private static boolean compare(final String actual,
                                 final String expected) throws IOException {
      return Compare.compare(actual, expected);
  }

  public void test1() throws Exception {
    configure("input/pattern/enhancedPatternLayout1.properties");
    common();
    Transformer.transform(
      TEMP, FILTERED,
      new Filter[] {
        new EnhancedLineNumberFilter(), new SunReflectFilter(),
        new EnhancedJunitTestRunnerFilter()
      });
    assertTrue(compare(FILTERED, "witness/pattern/enhancedPatternLayout.1"));
  }

  public void test2() throws Exception {
    configure("input/pattern/enhancedPatternLayout2.properties");
    common();

    ControlFilter cf1 =
      new ControlFilter(
        new String[] { PAT1, EXCEPTION1, EXCEPTION2, EXCEPTION3 });
    Transformer.transform(
      TEMP, FILTERED,
      new Filter[] {
        cf1, new EnhancedLineNumberFilter(), new ISO8601Filter(),
        new SunReflectFilter(), new EnhancedJunitTestRunnerFilter()
      });
    assertTrue(compare(FILTERED, "witness/pattern/enhancedPatternLayout.2"));
  }

  public void test3() throws Exception {
    configure("input/pattern/enhancedPatternLayout3.properties");
    common();

    ControlFilter cf1 =
      new ControlFilter(
        new String[] { PAT1, EXCEPTION1, EXCEPTION2, EXCEPTION3 });
    Transformer.transform(
      TEMP, FILTERED,
      new Filter[] {
        cf1, new EnhancedLineNumberFilter(), new ISO8601Filter(),
        new SunReflectFilter(), new EnhancedJunitTestRunnerFilter()
      });
    assertTrue(compare(FILTERED, "witness/pattern/enhancedPatternLayout.3"));
  }

  // Output format:
  // 06 avr. 2002 18:30:58,937 [main] DEBUG atternLayoutTest - Message 0  
  public void test4() throws Exception {
    configure("input/pattern/enhancedPatternLayout4.properties");
    common();

    ControlFilter cf1 =
      new ControlFilter(
        new String[] { PAT2, EXCEPTION1, EXCEPTION2, EXCEPTION3 });
    Transformer.transform(
      TEMP, FILTERED,
      new Filter[] {
        cf1, new EnhancedLineNumberFilter(), new AbsoluteDateAndTimeFilter(),
        new SunReflectFilter(), new EnhancedJunitTestRunnerFilter()
      });
    assertTrue(compare(FILTERED, "witness/pattern/enhancedPatternLayout.4"));
  }

  public void test5() throws Exception {
    configure("input/pattern/enhancedPatternLayout5.properties");
    common();

    ControlFilter cf1 =
      new ControlFilter(
        new String[] { PAT2, EXCEPTION1, EXCEPTION2, EXCEPTION3 });
    Transformer.transform(
      TEMP, FILTERED,
      new Filter[] {
        cf1, new EnhancedLineNumberFilter(), new AbsoluteDateAndTimeFilter(),
        new SunReflectFilter(), new EnhancedJunitTestRunnerFilter()
      });
    assertTrue(compare(FILTERED, "witness/pattern/enhancedPatternLayout.5"));
  }

  // 18:54:19,201 [main] DEBUG atternLayoutTest - Message 0
  public void test6() throws Exception {
    configure("input/pattern/enhancedPatternLayout6.properties");
    common();

    ControlFilter cf1 =
      new ControlFilter(
        new String[] { PAT3, EXCEPTION1, EXCEPTION2, EXCEPTION3 });
    Transformer.transform(
      TEMP, FILTERED,
      new Filter[] {
        cf1, new EnhancedLineNumberFilter(), new AbsoluteTimeFilter(),
        new SunReflectFilter(), new EnhancedJunitTestRunnerFilter()
      });
    assertTrue(compare(FILTERED, "witness/pattern/enhancedPatternLayout.6"));
  }

  public void test7() throws Exception {
    configure("input/pattern/enhancedPatternLayout7.properties");
    common();

    ControlFilter cf1 =
      new ControlFilter(
        new String[] { PAT3, EXCEPTION1, EXCEPTION2, EXCEPTION3 });
    Transformer.transform(
      TEMP, FILTERED,
      new Filter[] {
        cf1, new EnhancedLineNumberFilter(), new AbsoluteTimeFilter(),
        new SunReflectFilter(), new EnhancedJunitTestRunnerFilter()
      });
    assertTrue(compare(FILTERED, "witness/pattern/enhancedPatternLayout.7"));
  }

  public void test8() throws Exception {
    configure("input/pattern/enhancedPatternLayout8.properties");
    common();

    ControlFilter cf1 =
      new ControlFilter(
        new String[] { PAT4, EXCEPTION1, EXCEPTION2, EXCEPTION3 });
    Transformer.transform(
      TEMP, FILTERED,
      new Filter[] {
        cf1, new EnhancedLineNumberFilter(), new RelativeTimeFilter(),
        new SunReflectFilter(), new EnhancedJunitTestRunnerFilter()
      });
    assertTrue(compare(FILTERED, "witness/pattern/enhancedPatternLayout.8"));
  }

  public void test9() throws Exception {
    configure("input/pattern/enhancedPatternLayout9.properties");
    common();

    ControlFilter cf1 =
      new ControlFilter(
        new String[] { PAT5, EXCEPTION1, EXCEPTION2, EXCEPTION3 });
    Transformer.transform(
      TEMP, FILTERED,
      new Filter[] {
        cf1, new EnhancedLineNumberFilter(), new SunReflectFilter(),
        new EnhancedJunitTestRunnerFilter()
      });
    assertTrue(compare(FILTERED, "witness/pattern/enhancedPatternLayout.9"));
  }

  public void test10() throws Exception {
    configure("input/pattern/enhancedPatternLayout10.properties");
    common();

    ControlFilter cf1 =
      new ControlFilter(
        new String[] { PAT6, EXCEPTION1, EXCEPTION2, EXCEPTION3 });
    Transformer.transform(
      TEMP, FILTERED,
      new Filter[] {
        cf1, new EnhancedLineNumberFilter(), new SunReflectFilter(),
        new EnhancedJunitTestRunnerFilter()
      });
    assertTrue(compare(FILTERED, "witness/pattern/enhancedPatternLayout.10"));
  }

  public void test11() throws Exception {
    configure("input/pattern/enhancedPatternLayout11.properties");
    common();

    ControlFilter cf1 =
      new ControlFilter(
        new String[] { PAT11a, PAT11b, EXCEPTION1, EXCEPTION2, EXCEPTION3 });
    Transformer.transform(
      TEMP, FILTERED,
      new Filter[] {
        cf1, new EnhancedLineNumberFilter(), new SunReflectFilter(),
        new EnhancedJunitTestRunnerFilter()
      });
    assertTrue(compare(FILTERED, "witness/pattern/enhancedPatternLayout.11"));
  }

  public void test12() throws Exception {
    configure("input/pattern/enhancedPatternLayout12.properties");
    common();

    ControlFilter cf1 =
      new ControlFilter(
        new String[] { PAT12, EXCEPTION1, EXCEPTION2, EXCEPTION3 });
    Transformer.transform(
      TEMP, FILTERED,
      new Filter[] {
        cf1, new EnhancedLineNumberFilter(), new SunReflectFilter(),
        new EnhancedJunitTestRunnerFilter()
      });
    assertTrue(compare(FILTERED, "witness/pattern/enhancedPatternLayout.12"));
  }

  public void test13() throws Exception {
    configure("input/pattern/enhancedPatternLayout13.properties");
    common();

    ControlFilter cf1 =
      new ControlFilter(
        new String[] { PAT13, EXCEPTION1, EXCEPTION2, EXCEPTION3 });
    Transformer.transform(
      TEMP, FILTERED,
      new Filter[] {
        cf1, new EnhancedLineNumberFilter(), new SunReflectFilter(),
        new EnhancedJunitTestRunnerFilter()
      });
    assertTrue(compare(FILTERED, "witness/pattern/enhancedPatternLayout.13"));
  }

    /**
     * Test of class abbreviation.
     *
     * @throws Exception
     */
    public void test14() throws Exception {
      configure("input/pattern/enhancedPatternLayout14.properties");
      common();

      Transformer.transform(
        TEMP, FILTERED,
        new Filter[] {
          new EnhancedLineNumberFilter(), new SunReflectFilter(),
          new EnhancedJunitTestRunnerFilter()
        });
      assertTrue(compare(FILTERED, "witness/pattern/enhancedPatternLayout.14"));
    }


    private static void clearMDC() throws Exception {
        java.util.Hashtable context = MDC.getContext();
        if (context != null) {
            context.clear();
        }
    }

  public void testMDC1() throws Exception {
    configure("input/pattern/enhancedPatternLayout.mdc.1.properties");
    clearMDC();
    MDC.put("key1", "va11");
    MDC.put("key2", "va12");
    logger.debug("Hello World");
    MDC.remove("key1");
    MDC.remove("key2");

    Transformer.transform(
      TEMP, FILTERED,
      new Filter[] {
        new EnhancedLineNumberFilter(), new SunReflectFilter(),
        new EnhancedJunitTestRunnerFilter(),
        new MDCOrderFilter()
      });
    assertTrue(compare(FILTERED, "witness/pattern/enhancedPatternLayout.mdc.1"));
  }
    /**
     * Tests log4j 1.2 style extension of EnhancedPatternLayout.
     * Was test14 in log4j 1.2.
     * @throws Exception
     */
    public void test15() throws Exception {
      configure("input/pattern/enhancedPatternLayout15.properties");
      common();
      ControlFilter cf1 = new ControlFilter(new String[]{PAT14, EXCEPTION1,
                                 EXCEPTION2, EXCEPTION3, EXCEPTION4});
      Transformer.transform(
        TEMP, FILTERED,
        new Filter[] {
          cf1, new EnhancedLineNumberFilter(), new SunReflectFilter(),
          new EnhancedJunitTestRunnerFilter()
        });
      assertTrue(compare(FILTERED, "witness/pattern/enhancedPatternLayout.15"));
    }
    /**
     * Tests explicit UTC time zone in pattern.
     * @throws Exception
     */
    public void test16() throws Exception {
      final long start = new Date().getTime();
      configure("input/pattern/enhancedPatternLayout16.properties");
      common();
      final long end = new Date().getTime();
      FileReader reader = new FileReader("output/patternLayout16.log");
      char chars[] = new char[50];
      reader.read(chars, 0, chars.length);
      reader.close();
      SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      format.setTimeZone(TimeZone.getTimeZone("GMT+0"));
      String utcStr = new String(chars, 0, 19);
      Date utcDate = format.parse(utcStr, new ParsePosition(0));
      assertTrue(utcDate.getTime() >= start - 1000 && utcDate.getTime() < end + 1000);
      String cstStr = new String(chars, 21, 19);
      format.setTimeZone(TimeZone.getTimeZone("GMT-6"));
      Date cstDate = format.parse(cstStr, new ParsePosition(0));
      assertFalse(cstStr.equals(utcStr));
      assertTrue(cstDate.getTime() >= start - 1000 && cstDate.getTime() < end + 1000);
    }

  void common() {
    int i = -1;

    logger.debug("Message " + ++i);
    root.debug("Message " + i);

    logger.info("Message " + ++i);
    root.info("Message " + i);

    logger.warn("Message " + ++i);
    root.warn("Message " + i);

    logger.error("Message " + ++i);
    root.error("Message " + i);

    logger.log(Level.FATAL, "Message " + ++i);
    root.log(Level.FATAL, "Message " + i);

    Exception e = new Exception("Just testing");

    logger.debug("Message " + ++i, e);
    logger.info("Message " + ++i, e);
    logger.warn("Message " + ++i, e);
    logger.error("Message " + ++i, e);
    logger.log(Level.FATAL, "Message " + ++i, e);
  }

  /**
    Test case for MDC conversion pattern. */
  public void testMDC2() throws Exception {
    String OUTPUT_FILE   = "output/patternLayout.mdc.2";
    String WITNESS_FILE  = "witness/pattern/enhancedPatternLayout.mdc.2";
    
    String mdcMsgPattern1 = "%m : %X%n";
    String mdcMsgPattern2 = "%m : %X{key1}%n";
    String mdcMsgPattern3 = "%m : %X{key2}%n";
    String mdcMsgPattern4 = "%m : %X{key3}%n";
    String mdcMsgPattern5 = "%m : %X{key1},%X{key2},%X{key3}%n";
    
    // set up appender
    EnhancedPatternLayout layout = new EnhancedPatternLayout("%m%n");
    Appender appender = new FileAppender(layout, OUTPUT_FILE, false);
            
    // set appender on root and set level to debug
    root.addAppender(appender);
    root.setLevel(Level.DEBUG);

    clearMDC();
    // output starting message
    root.debug("starting mdc pattern test");
 
    layout.setConversionPattern(mdcMsgPattern1);
    layout.activateOptions();
    root.debug("empty mdc, no key specified in pattern");
    
    layout.setConversionPattern(mdcMsgPattern2);
    layout.activateOptions();
    root.debug("empty mdc, key1 in pattern");
    
    layout.setConversionPattern(mdcMsgPattern3);
    layout.activateOptions();
    root.debug("empty mdc, key2 in pattern");
    
    layout.setConversionPattern(mdcMsgPattern4);
    layout.activateOptions();
    root.debug("empty mdc, key3 in pattern");
    
    layout.setConversionPattern(mdcMsgPattern5);
    layout.activateOptions();
    root.debug("empty mdc, key1, key2, and key3 in pattern");

    MDC.put("key1", "value1");
    MDC.put("key2", "value2");

    layout.setConversionPattern(mdcMsgPattern1);
    layout.activateOptions();
    root.debug("filled mdc, no key specified in pattern");
    
    layout.setConversionPattern(mdcMsgPattern2);
    layout.activateOptions();
    root.debug("filled mdc, key1 in pattern");
    
    layout.setConversionPattern(mdcMsgPattern3);
    layout.activateOptions();
    root.debug("filled mdc, key2 in pattern");
    
    layout.setConversionPattern(mdcMsgPattern4);
    layout.activateOptions();
    root.debug("filled mdc, key3 in pattern");
    
    layout.setConversionPattern(mdcMsgPattern5);
    layout.activateOptions();
    root.debug("filled mdc, key1, key2, and key3 in pattern");

    MDC.remove("key1");
    MDC.remove("key2");

    layout.setConversionPattern("%m%n");
    layout.activateOptions();
    root.debug("finished mdc pattern test");


      Transformer.transform(
        OUTPUT_FILE, FILTERED,
        new Filter[] {
          new EnhancedLineNumberFilter(), new SunReflectFilter(),
          new EnhancedJunitTestRunnerFilter(),
          new MDCOrderFilter()
        });

    assertTrue(compare(FILTERED, WITNESS_FILE));
  }
  /**
    Test case for throwable conversion pattern. */
  public void testThrowable() throws Exception {
    String OUTPUT_FILE   = "output/patternLayout.throwable";
    String WITNESS_FILE  = "witness/pattern/enhancedPatternLayout.throwable";
    
    
    // set up appender
    EnhancedPatternLayout layout = new EnhancedPatternLayout("%m%n");
    Appender appender = new FileAppender(layout, OUTPUT_FILE, false);
            
    // set appender on root and set level to debug
    root.addAppender(appender);
    root.setLevel(Level.DEBUG);
    
    // output starting message
    root.debug("starting throwable pattern test");
     Exception ex = new Exception("Test Exception");
    root.debug("plain pattern, no exception");
    root.debug("plain pattern, with exception", ex);
    layout.setConversionPattern("%m%n%throwable");
    layout.activateOptions();
    root.debug("%throwable, no exception");
    root.debug("%throwable, with exception", ex);

    layout.setConversionPattern("%m%n%throwable{short}");
    layout.activateOptions();
    root.debug("%throwable{short}, no exception");
    root.debug("%throwable{short}, with exception", ex);

    layout.setConversionPattern("%m%n%throwable{none}");
    layout.activateOptions();
    root.debug("%throwable{none}, no exception");
    root.debug("%throwable{none}, with exception", ex);

    layout.setConversionPattern("%m%n%throwable{0}");
    layout.activateOptions();
    root.debug("%throwable{0}, no exception");
    root.debug("%throwable{0}, with exception", ex);

    layout.setConversionPattern("%m%n%throwable{1}");
    layout.activateOptions();
    root.debug("%throwable{1}, no exception");
    root.debug("%throwable{1}, with exception", ex);

    layout.setConversionPattern("%m%n%throwable{100}");
    layout.activateOptions();
    root.debug("%throwable{100}, no exception");
    root.debug("%throwable{100}, with exception", ex);

    //
    //  manufacture a pattern to get just the first two lines
    //
    String[] trace = new ThrowableInformation(ex).getThrowableStrRep();
    layout.setConversionPattern("%m%n%throwable{" + (2 - trace.length) + "}");
    layout.activateOptions();
    root.debug("%throwable{-n}, no exception");
    root.debug("%throwable{-n}, with exception", ex);


      Transformer.transform(
        OUTPUT_FILE, FILTERED,
        new Filter[] {
          new EnhancedLineNumberFilter(), new SunReflectFilter(),
          new EnhancedJunitTestRunnerFilter(),
          new MDCOrderFilter()
        });

    assertTrue(compare(FILTERED, WITNESS_FILE));
  }
}
