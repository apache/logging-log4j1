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

import java.util.Vector;

import org.apache.log4j.*;
import org.apache.log4j.performance.NullAppender;

/**
   A superficial but general test of log4j.
 */
public class AsyncAppenderTestCase extends TestCase {

  public AsyncAppenderTestCase(String name) {
    super(name);
  }

  public void setUp() {
  }

  public void tearDown() {
    LogManager.shutdown();
  }

  // this test checks whether it is possible to write to a closed AsyncAppender
  public void closeTest() throws Exception {    
    Logger root = Logger.getRootLogger();
    Layout layout = new SimpleLayout();
    VectorAppender vectorAppender = new VectorAppender();
    AsyncAppender asyncAppender = new AsyncAppender();
    asyncAppender.setName("async-CloseTest");
    asyncAppender.addAppender(vectorAppender);
    root.addAppender(asyncAppender); 

    root.debug("m1");
    asyncAppender.close();
    root.debug("m2");
    
    Vector v = vectorAppender.getVector();
    assertEquals(v.size(), 1);
  }

  // this test checks whether appenders embedded within an AsyncAppender are also 
  // closed 
  public void test2() {
    Logger root = Logger.getRootLogger();
    Layout layout = new SimpleLayout();
    VectorAppender vectorAppender = new VectorAppender();
    AsyncAppender asyncAppender = new AsyncAppender();
    asyncAppender.setName("async-test2");
    asyncAppender.addAppender(vectorAppender);
    root.addAppender(asyncAppender); 

    root.debug("m1");
    asyncAppender.close();
    root.debug("m2");
    
    Vector v = vectorAppender.getVector();
    assertEquals(v.size(), 1);
    assertTrue(vectorAppender.isClosed());
  }

  // this test checks whether appenders embedded within an AsyncAppender are also 
  // closed 
  public void test3() {
    int LEN = 200;
    Logger root = Logger.getRootLogger();
    Layout layout = new SimpleLayout();
    VectorAppender vectorAppender = new VectorAppender();
    AsyncAppender asyncAppender = new AsyncAppender();
    asyncAppender.setName("async-test3");
    asyncAppender.addAppender(vectorAppender);
    root.addAppender(asyncAppender); 

    for(int i = 0; i < LEN; i++) {
      root.debug("message"+i);
    }
    
    System.out.println("Done loop.");
    System.out.flush();
    asyncAppender.close();
    root.debug("m2");
    
    Vector v = vectorAppender.getVector();
    assertEquals(v.size(), LEN);
    assertTrue(vectorAppender.isClosed());
  }


  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(new AsyncAppenderTestCase("closeTest"));
    suite.addTest(new AsyncAppenderTestCase("test2"));
    suite.addTest(new AsyncAppenderTestCase("test3"));
    return suite;
  }

}
