/*
 * Copyright 1999,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.Vector;


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

    for (int i = 0; i < LEN; i++) {
      root.debug("message" + i);
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
