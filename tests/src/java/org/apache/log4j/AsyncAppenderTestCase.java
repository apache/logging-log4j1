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

import org.apache.log4j.spi.LoggingEvent;


/**
   A superficial but general test of log4j.
 */
public class AsyncAppenderTestCase extends TestCase {
  static int DELAY = 10;

  Logger root = Logger.getRootLogger();;
  Layout layout = new SimpleLayout();;
  VectorAppender vectorAppender;
  AsyncAppender asyncAppender = new AsyncAppender();
  
  public AsyncAppenderTestCase(String name) {
    super(name);
  }

  public void setUp() {
    vectorAppender = new VectorAppender();
    vectorAppender.setDelay(DELAY);
    asyncAppender.addAppender(vectorAppender);
    asyncAppender.activateOptions();
    root.addAppender(asyncAppender);
  }

  public void tearDown() {
    LogManager.shutdown();
  }

  // this test checks whether it is possible to write to a closed AsyncAppender
  public void test1() throws Exception {
    asyncAppender.setName("async-CloseTest");
    root.debug("m1");
    asyncAppender.close();
    root.debug("m2");
    Vector v = vectorAppender.getVector();
    assertEquals(v.size(), 1);
  }

  // this test checks whether appenders embedded within an AsyncAppender are also 
  // closed 
  public void closeTest() {
    asyncAppender.setName("async-test2");

    root.debug("m1");
    asyncAppender.close();
    root.debug("m2");

    Vector v = vectorAppender.getVector();
    assertEquals(v.size(), 1);
    assertTrue(vectorAppender.isClosed());
  }

  // this test checks whether appenders embedded within an AsyncAppender are also 
  // closed 
  public void test2() {
    int LEN = 200;
    asyncAppender.setName("async-test3");
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

  private static class NullPointerAppender extends AppenderSkeleton {
        public NullPointerAppender() {
          super(true);
        }


        /**
           This method is called by the {@link AppenderSkeleton#doAppend}
           method.

        */
        public void append(LoggingEvent event) {
            throw new NullPointerException();
        }

        public void close() {
        }

        public boolean requiresLayout() {
          return false;
        }
  }


  public void testBadAppender() throws Exception {
      Appender nullPointerAppender = new NullPointerAppender();
      asyncAppender.addAppender(nullPointerAppender);
      asyncAppender.setBufferSize(5);
      asyncAppender.activateOptions();
      root.addAppender(nullPointerAppender);
      try {
         root.info("Message");
         Thread.sleep(10);
         root.info("Message");
         fail("Should have thrown exception");
      } catch(NullPointerException ex) {

      }
  }

}
