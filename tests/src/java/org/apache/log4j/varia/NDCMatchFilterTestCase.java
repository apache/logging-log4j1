/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j.varia;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.NDC;
import org.apache.log4j.varia.LevelMatchFilter;
import org.apache.log4j.varia.DenyAllFilter;

import org.apache.log4j.util.Transformer;
import org.apache.log4j.util.Compare;
import org.apache.log4j.util.LineNumberFilter;

/**
   Test case for varia/NDCMatchFilter.java.  Test both the accept
   and deny cases.  Testing the accept requires the use of the
   DenyAllFilter to prevent non-matched messages from still getting
   logged to the appender.
 */
public class NDCMatchFilterTestCase extends TestCase {
  
  static String ACCEPT_FILE     = "output/NDCMatchFilter_accept";
  static String ACCEPT_WITNESS  = "witness/NDCMatchFilter_accept";

  static String DENY_FILE       = "output/NDCMatchFilter_deny";
  static String DENY_WITNESS    = "witness/NDCMatchFilter_deny";

  Logger root; 
  Logger logger;

  public NDCMatchFilterTestCase(String name) {
    super(name);
  }

  public void setUp() {
    root = Logger.getRootLogger();
    root.removeAllAppenders();
  }

  public void tearDown() {  
    root.getLoggerRepository().resetConfiguration();
  }

  public void accept() throws Exception {
    
    // set up appender
    Layout layout = new PatternLayout("%m \"%x\"%n");
    Appender appender = new FileAppender(layout, ACCEPT_FILE, false);
    
    // create NDCMatchFilter
    NDCMatchFilter matchFilter = new NDCMatchFilter();
 
     // attach match filter to appender
    appender.addFilter(matchFilter);
   
    // attach DenyAllFilter to end of filter chain to deny neutral
    // (non matching) messages
    appender.addFilter(new DenyAllFilter());
        
    // set appender on root and set level to debug
    root.addAppender(appender);
    root.setLevel(Level.DEBUG);
    
    StringBuffer ndcMatchValue = new StringBuffer();
    String[] ndcValueArray = new String[] {"level_1", "level_2", "level_3"};
    
    //test for exact matches
    matchFilter.setExactMatch(true);
    for (int x = 0; x < ndcValueArray.length; x++) {
      ndcMatchValue.append(ndcValueArray[x]);
      
      // set the value to match
      matchFilter.setValueToMatch(ndcMatchValue.toString());
      
      // push all the values onto the ndc stack
      for (int y = 0; y < ndcValueArray.length; y++) {
        NDC.push(ndcValueArray[y]);
      }
      
      // print the test messages
      common(ndcValueArray.length, "pass " + x + ": \"" + 
        ndcMatchValue.toString() + "\" exactly matches");
      
      // append a space for the next pass
      ndcMatchValue.append(" ");
    }
    
    // test for contained matches
    matchFilter.setExactMatch(false);
    ndcMatchValue.setLength(0);
    for (int x = 0; x < ndcValueArray.length; x++) {
      ndcMatchValue.append(ndcValueArray[x]);
      
      // set the value to match
      matchFilter.setValueToMatch(ndcMatchValue.toString());
      
      // push all the values onto the ndc stack
      for (int y = 0; y < ndcValueArray.length; y++) {
        NDC.push(ndcValueArray[y]);
      }
      
      // print the test messages
      common(ndcValueArray.length, "pass " + (x+3) + ": \"" + 
        ndcMatchValue.toString() + "\" contained in");
      
      // append a space for the next pass
      ndcMatchValue.append(" ");
    }
 
    // test the null match with exact match
    matchFilter.setValueToMatch(null);
    matchFilter.setExactMatch(true);
    // push all the values onto the ndc stack
    for (int y = 0; y < ndcValueArray.length; y++) {
      NDC.push(ndcValueArray[y]);
    }
    
    // print the test messages
    common(ndcValueArray.length, "pass 6: \"\" exactly matches");

    // test the null match with contained match
    matchFilter.setExactMatch(false);
    for (int y = 0; y < ndcValueArray.length; y++) {
      NDC.push(ndcValueArray[y]);
    }
    
    // print the test messages
    common(ndcValueArray.length, "pass 7: \"\" contained in");

    assertTrue(Compare.compare(ACCEPT_FILE, ACCEPT_WITNESS));
  }

  public void deny() throws Exception {
    
    // set up appender
    Layout layout = new PatternLayout("%m \"%x\"%n");
    Appender appender = new FileAppender(layout, DENY_FILE, false);
    
    // create NDCMatchFilter, set to deny matches
    NDCMatchFilter matchFilter = new NDCMatchFilter();
    matchFilter.setAcceptOnMatch(false);
 
     // attach match filter to appender
    appender.addFilter(matchFilter);
        
    // set appender on root and set level to debug
    root.addAppender(appender);
    root.setLevel(Level.DEBUG);
    
    StringBuffer ndcMatchValue = new StringBuffer();
    String[] ndcValueArray = new String[] {"level_1", "level_2", "level_3"};
    
    //test for exact matches
    matchFilter.setExactMatch(true);
    for (int x = 0; x < ndcValueArray.length; x++) {
      ndcMatchValue.append(ndcValueArray[x]);
      
      // set the value to match
      matchFilter.setValueToMatch(ndcMatchValue.toString());
      
      // push all the values onto the ndc stack
      for (int y = 0; y < ndcValueArray.length; y++) {
        NDC.push(ndcValueArray[y]);
      }
      
      // print the test messages
      common(ndcValueArray.length, "pass " + x + ": \"" + 
        ndcMatchValue.toString() + "\" does not exactly match");
      
      // append a space for the next pass
      ndcMatchValue.append(" ");
    }
    
    // test for contained matches
    matchFilter.setExactMatch(false);
    ndcMatchValue.setLength(0);
    for (int x = 0; x < ndcValueArray.length; x++) {
      ndcMatchValue.append(ndcValueArray[x]);
      
      // set the value to match
      matchFilter.setValueToMatch(ndcMatchValue.toString());
      
      // push all the values onto the ndc stack
      for (int y = 0; y < ndcValueArray.length; y++) {
        NDC.push(ndcValueArray[y]);
      }
      
      // print the test messages
      common(ndcValueArray.length, "pass " + (x+3) + ": \"" + 
        ndcMatchValue.toString() + "\" not contained in");
      
      // append a space for the next pass
      ndcMatchValue.append(" ");
    }
 
    // test the null match with exact match
    matchFilter.setValueToMatch(null);
    matchFilter.setExactMatch(true);
    // push all the values onto the ndc stack
    for (int y = 0; y < ndcValueArray.length; y++) {
      NDC.push(ndcValueArray[y]);
    }
    
    // print the test messages
    common(ndcValueArray.length, "pass 6: \"\" does not exactly match");

    // test the null match with contained match
    matchFilter.setValueToMatch(null);
    matchFilter.setExactMatch(false);
    for (int y = 0; y < ndcValueArray.length; y++) {
      NDC.push(ndcValueArray[y]);
    }
    
    // print the test messages
    common(ndcValueArray.length, "pass 7: \"\" not contained in");

    assertTrue(Compare.compare(DENY_FILE, DENY_WITNESS));
  }

  void common(int popCount, String msg) {
    Logger logger = Logger.getLogger("test");
    
    // log message, popping the NDC each time
    for (int x = 0; x < popCount; x++) {
      logger.debug(msg);
      NDC.pop();
    }
    
    // log a message NDC is null/empty
    logger.debug(msg);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(new NDCMatchFilterTestCase("accept"));
    suite.addTest(new NDCMatchFilterTestCase("deny"));
    return suite;
  }

}
