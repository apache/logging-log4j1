/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j.filters;

import java.io.File;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;

import org.apache.log4j.*;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.log4j.filters.LevelMatchFilter;
import org.apache.log4j.varia.DenyAllFilter;

import org.apache.log4j.util.Compare;

/**
   Test case for varia/LevelMatchFilter.java.  Test both the accept
   and deny cases.  Testing the accept requires the use of the
   DenyAllFilter to prevent non-matched messages from still getting
   logged to the appender.
 */
public class LevelMatchFilterTestCase extends TestCase {
  
  static String TEST1_FILE    = "output/filters.LevelMatchFilter.test1.txt";
  static String TEST1_WITNESS = "witness/filters.LevelMatchFilter.test1.txt";
  static String TEST2_FILE    = "output/filters.LevelMatchFilter.test2.txt";
  static String TEST2_WITNESS = "witness/filters.LevelMatchFilter.test2.txt";
  static String TEST3_FILE    = "output/filters.LevelMatchFilter.test3.txt";
  static String TEST3_WITNESS = "witness/filters.LevelMatchFilter.test3.txt";
  static String TEST4_FILE    = "output/filters.LevelMatchFilter.test4.txt";
  static String TEST4_WITNESS = "witness/filters.LevelMatchFilter.test4.txt";
  static String TEST4_CONFIG  = "input/xml//filters.LevelMatchFilter.test4.";

  public LevelMatchFilterTestCase(String name) {
    super(name);
  }

  Logger root;
  
  public void setUp() {
    root = Logger.getRootLogger();
    root.removeAllAppenders();
    
    // delete the output file if they happen to exist
    File file = new File(TEST1_FILE);
    file.delete();
    file = new File(TEST2_FILE);
    file.delete();
    file = new File(TEST3_FILE);
    file.delete();
    file = new File(TEST4_FILE);
    file.delete();
  }

  public void tearDown() {  
    root.getLoggerRepository().resetConfiguration();  
  }

  /**
    Test #1: Simple accept/deny configuration set programatically. */
  public void test1() throws Exception {
    
    // set up appender
    Layout layout = new SimpleLayout();
    Appender appender = new FileAppender(layout, TEST1_FILE, false);
    
    // create LevelMatchFilter
    LevelMatchFilter matchFilter = new LevelMatchFilter();
    matchFilter.setMatchReturnValue("accept");
    matchFilter.setNoMatchReturnValue("deny");
    
     // attach match filter to appender
    appender.addFilter(matchFilter);
           
    // set appender on root and set level to debug
    root.addAppender(appender);
    root.setLevel(Level.DEBUG);
    
    Level[] levelArray = new Level[] {Level.DEBUG, Level.INFO, Level.WARN, 
				      Level.ERROR, Level.FATAL};
    for (int x = 0; x < levelArray.length; x++) {
      // set the level to match
      matchFilter.setLevelToMatch(levelArray[x].toString());
      common("pass " + x + "; filter set to accept only " 
	     + levelArray[x].toString() + " msgs");
    }
    
    assertTrue(Compare.compare(TEST1_FILE, TEST1_WITNESS));
  }

  /**
    Test #2: Simple deny/accept configuration set programatically. */
  public void test2() throws Exception {
    
    // set up appender
    Layout layout = new SimpleLayout();
    Appender appender = new FileAppender(layout, TEST2_FILE, false);
    
    // create LevelMatchFilter, set to deny matches
    LevelMatchFilter matchFilter = new LevelMatchFilter();
    matchFilter.setMatchReturnValue("deny");
    matchFilter.setNoMatchReturnValue("accept");
 
     // attach match filter to appender
    appender.addFilter(matchFilter);
           
    // set appender on root and set level to debug
    root.addAppender(appender);
    root.setLevel(Level.DEBUG);
    
    Level[] levelArray = new Level[] {Level.DEBUG, Level.INFO, Level.WARN,
				      Level.ERROR, Level.FATAL};
    for (int x = 0; x < levelArray.length; x++) {
      // set the level to match
      matchFilter.setLevelToMatch(levelArray[x].toString());
      common("pass " + x + "; filter set to deny only " + levelArray[x].toString()
              + " msgs");
    }
    
    assertTrue(Compare.compare(TEST2_FILE, TEST2_WITNESS));
  }

  /**
    Test #3: Simple chain policy configuration set programatically. */
  public void test3() throws Exception {
    // set up appender
    Layout layout = new SimpleLayout();
    Appender appender = new FileAppender(layout, TEST3_FILE, false);
    
    // create LevelMatchFilter, set chain policy to accept/neutral
    LevelMatchFilter match1Filter = new LevelMatchFilter();
    match1Filter.setChainPolicy("AcceptOnMatch");
    
    LevelMatchFilter match2Filter = new LevelMatchFilter();
    match2Filter.setChainPolicy("AcceptOnMatch");
 
     // attach match filter to appender
    appender.addFilter(match1Filter);
    appender.addFilter(match2Filter);

    // attach DenyAllFilter to end of filter chain to deny neutral
    // (non matching) messages
    appender.addFilter(new DenyAllFilter());
           
    // set appender on root and set level to debug
    root.addAppender(appender);
    root.setLevel(Level.DEBUG);
    
    Level[] levelArray = new Level[] {Level.DEBUG, Level.INFO, Level.WARN,
				      Level.ERROR, Level.FATAL};
    for (int x = 0, passCount = 0; x < levelArray.length; x++) {
      // set the level to match
      match1Filter.setLevelToMatch(levelArray[x].toString());
      for (int y = levelArray.length-1; y >= 0; y--,passCount++) {
        match2Filter.setLevelToMatch(levelArray[y].toString());
        common("pass " + passCount + "; filter chain set to accept " 
        + levelArray[x].toString() + " or " + levelArray[y].toString() 
        + " msgs");
      }
    }
    
    assertTrue(Compare.compare(TEST3_FILE, TEST3_WITNESS));
  }

  /**
    Test #4: Same test at Test #1, but set via configuration file. */
  public void test4() throws Exception {
    Level[] levelArray = new Level[] {Level.DEBUG, Level.INFO, Level.WARN, 
				      Level.ERROR, Level.FATAL};

    for (int x = 0; x < levelArray.length; x++) {
      DOMConfigurator.configure(TEST4_CONFIG + x + ".xml");
      
      common("pass " + x + "; filter should accept only " 
	     + levelArray[x].toString() + " msgs");
      
      tearDown();
    }
    
    assertTrue(Compare.compare(TEST4_FILE, TEST4_WITNESS));
  }
  
  void common(String msg) {
    Logger logger = Logger.getLogger("test");
    logger.debug(msg);
    logger.info(msg);
    logger.warn(msg);
    logger.error(msg);
    logger.fatal(msg);
  }
  
  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(new LevelMatchFilterTestCase("test1"));
    suite.addTest(new LevelMatchFilterTestCase("test2"));
    suite.addTest(new LevelMatchFilterTestCase("test3"));
    suite.addTest(new LevelMatchFilterTestCase("test4"));
    return suite;
  }

}
