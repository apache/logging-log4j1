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
  }

  public void closeTest() throws Exception {    
    Logger root = Logger.getRootLogger();
    Layout layout = new SimpleLayout();
    VectorAppender vectorAppender = new VectorAppender();
    AsyncAppender asyncAppender = new AsyncAppender();
    asyncAppender.addAppender(vectorAppender);
    root.addAppender(asyncAppender); 

    root.debug("m1");
    asyncAppender.close();
    root.debug("m2");
    
    Vector v = vectorAppender.getVector();
    assertEquals(v.size(), 1);
    

  }

  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(new AsyncAppenderTestCase("closeTest"));
    return suite;
  }

}
