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
package org.apache.log4j.filter;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.joran.JoranConfigurator;
import org.apache.log4j.util.Compare;
import org.apache.log4j.util.ControlFilter;
import org.apache.log4j.util.Filter;
import org.apache.log4j.util.JunitTestRunnerFilter;
import org.apache.log4j.util.LineNumberFilter;
import org.apache.log4j.util.SunReflectFilter;
import org.apache.log4j.util.Transformer;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Various tests verifying that filters work properly and that 
 * JoranConfigurator can effectively parse config files containing them.
 * 
 * @author Ceki Gulcu
 *
 */
public class SimpleFilterTest extends TestCase {
  Logger root; 
  Logger logger;

  public final static String FILTERED = "output/filtered";
  public final static String TEMP = "output/temp";
  
  static String TEST1_PAT = "(DEBUG|INFO|WARN|ERROR|FATAL) - Message \\d";
  static String TEST8_PAT = "WARN org.apache.log4j.filter.SimpleFilterTest - Message \\d";
  static String EXCEPTION1 = "java.lang.Exception: Just testing";
  static String EXCEPTION2 = "\\s*at .*\\(.*:\\d{1,4}\\)";
  static String EXCEPTION3 = "\\s*at .*\\(Native Method\\)";
  
  public SimpleFilterTest(String name) {
    super(name);
  }

  public void setUp() {
    root = Logger.getRootLogger();
    logger = Logger.getLogger(SimpleFilterTest.class);
  }
 
  public void tearDown() {  
    root.getLoggerRepository().resetConfiguration();
  }
  
  
  public void test1() throws Exception {
    JoranConfigurator joc = new JoranConfigurator();
    joc.doConfigure("./input/filter/simpleFilter1.xml", LogManager.getLoggerRepository());
    joc.dumpErrors();
    common();
    
    ControlFilter cf = new ControlFilter(new String[]{TEST1_PAT, EXCEPTION1, EXCEPTION2, EXCEPTION3});
    

    Transformer.transform(TEMP, FILTERED, new Filter[] {cf, 
        new LineNumberFilter(), 
        new SunReflectFilter(), 
        new JunitTestRunnerFilter()});

     assertTrue(Compare.compare(FILTERED, "witness/filter/simpleFilter.1"));
  }

    public void test6() throws Exception {
      JoranConfigurator joc = new JoranConfigurator();
      joc.doConfigure("./input/filter/simpleFilter6.xml", LogManager.getLoggerRepository());
      joc.dumpErrors();
      common();

      ControlFilter cf = new ControlFilter(new String[]{TEST1_PAT, EXCEPTION1, EXCEPTION2, EXCEPTION3});


      Transformer.transform(TEMP, FILTERED, new Filter[] {cf,
          new LineNumberFilter(),
          new SunReflectFilter(),
          new JunitTestRunnerFilter()});

       assertTrue(Compare.compare(FILTERED, "witness/filter/simpleFilter.6"));
    }

    public void test7() throws Exception {
      JoranConfigurator joc = new JoranConfigurator();
      joc.doConfigure("./input/filter/simpleFilter7.xml", LogManager.getLoggerRepository());
      joc.dumpErrors();
      common();

      ControlFilter cf = new ControlFilter(new String[]{TEST1_PAT, EXCEPTION1, EXCEPTION2, EXCEPTION3});


      Transformer.transform(TEMP, FILTERED, new Filter[] {cf,
          new LineNumberFilter(),
          new SunReflectFilter(),
          new JunitTestRunnerFilter()});

       assertTrue(Compare.compare(FILTERED, "witness/filter/simpleFilter.7"));
    }

    public void test8() throws Exception {
      JoranConfigurator joc = new JoranConfigurator();
      joc.doConfigure("./input/filter/simpleFilter8.xml", LogManager.getLoggerRepository());
      joc.dumpErrors();
      common();

      ControlFilter cf = new ControlFilter(new String[]{TEST8_PAT, EXCEPTION1, EXCEPTION2, EXCEPTION3});


      Transformer.transform(TEMP, FILTERED, new Filter[] {cf,
          new LineNumberFilter(),
          new SunReflectFilter(),
          new JunitTestRunnerFilter()});

       assertTrue(Compare.compare(FILTERED, "witness/filter/simpleFilter.8"));
    }

    public void test9() throws Exception {
      JoranConfigurator joc = new JoranConfigurator();
      joc.doConfigure("./input/filter/simpleFilter9.xml", LogManager.getLoggerRepository());
      joc.dumpErrors();
      common();

      ControlFilter cf = new ControlFilter(new String[]{TEST1_PAT, EXCEPTION1, EXCEPTION2, EXCEPTION3});


      Transformer.transform(TEMP, FILTERED, new Filter[] {cf,
          new LineNumberFilter(),
          new SunReflectFilter(),
          new JunitTestRunnerFilter()});

       assertTrue(Compare.compare(FILTERED, "witness/filter/simpleFilter.1"));
    }

    public void test10() throws Exception {
      JoranConfigurator joc = new JoranConfigurator();
      joc.doConfigure("./input/filter/simpleFilter10.xml", LogManager.getLoggerRepository());
      joc.dumpErrors();
      common();

      ControlFilter cf = new ControlFilter(new String[]{TEST1_PAT, EXCEPTION1, EXCEPTION2, EXCEPTION3});


      Transformer.transform(TEMP, FILTERED, new Filter[] {cf,
          new LineNumberFilter(),
          new SunReflectFilter(),
          new JunitTestRunnerFilter()});

       assertTrue(Compare.compare(FILTERED, "witness/filter/simpleFilter.6"));
    }

    public void test11() throws Exception {
      JoranConfigurator joc = new JoranConfigurator();
      joc.doConfigure("./input/filter/simpleFilter11.xml", LogManager.getLoggerRepository());
      joc.dumpErrors();
      common();

      ControlFilter cf = new ControlFilter(new String[]{TEST1_PAT, EXCEPTION1, EXCEPTION2, EXCEPTION3});


      Transformer.transform(TEMP, FILTERED, new Filter[] {cf,
          new LineNumberFilter(),
          new SunReflectFilter(),
          new JunitTestRunnerFilter()});

       assertTrue(Compare.compare(FILTERED, "witness/filter/simpleFilter.11"));
    }

    public void test12() throws Exception {
      JoranConfigurator joc = new JoranConfigurator();
      joc.doConfigure("./input/filter/simpleFilter12.xml", LogManager.getLoggerRepository());
      joc.dumpErrors();
      common();

      ControlFilter cf = new ControlFilter(new String[]{TEST8_PAT, EXCEPTION1, EXCEPTION2, EXCEPTION3});


      Transformer.transform(TEMP, FILTERED, new Filter[] {cf,
          new LineNumberFilter(),
          new SunReflectFilter(),
          new JunitTestRunnerFilter()});

       assertTrue(Compare.compare(FILTERED, "witness/filter/simpleFilter.8"));
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
    root.debug("Message " + i, e);
    
    logger.error("Message " + ++i, e);
    root.error("Message " + i, e);    
  }
  
}
