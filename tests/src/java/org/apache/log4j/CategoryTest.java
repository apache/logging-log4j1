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

import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.log4j.spi.LoggingEvent;


/**
 * Tests of Category.
 *
 * @author Curt Arnold
 * @since 1.2.14
 */
public class CategoryTest extends TestCase {
  /**
   * Constructs new instance of test.
   * @param name test name.
   */
  public CategoryTest(final String name) {
    super(name);
  }
  
  StringWriter sw = new StringWriter();
  Priority debug = Level.DEBUG;
  Logger logger = Logger.getLogger("org.example.foo");
  
  protected void setUp() {
    WriterAppender a = new WriterAppender();
    a.setWriter(sw);
    a.setLayout(new PatternLayout("%m "));
    a.activateOptions();
    BasicConfigurator.configure(a);
    Category.getRoot().setLevel(Level.ALL);
    Category.getRoot().setResourceBundle(getTestBundle());
    logger.setLevel(Level.ALL);
  }
  
  static ResourceBundle getTestBundle() {
    return ResourceBundle.getBundle("L7D", new Locale("en", "US"));
  }

  /**
   * Tests Category.forcedLog.
   */
  public void testForcedLog() {
    MockCategory category = new MockCategory("org.example.foo");
    category.setAdditivity(false);
    category.addAppender(new VectorAppender());
    category.info("Hello, World");
  }

  /**
   * Tests that the return type of getChainedPriority is Priority.
   * @throws Exception thrown if Category.getChainedPriority can not be found.
   */
  public void testGetChainedPriorityReturnType() throws Exception {
    Method method = Category.class.getMethod("getChainedPriority", (Class[]) null);
    assertTrue(method.getReturnType() == Priority.class);
  }

  /**
   * Tests l7dlog(Priority, String, Throwable).
   */
  public void testL7dlog() {
    logger.l7dlog(debug, "test", (Throwable)null);
    assertEquals("This is the English, US test.", sw.toString().trim());
  }

  /**
   * Tests l7dlog(Priority, String) log.
   */
  public void testL7dlog2() {    
    logger.l7dlog(debug, "test");
    assertEquals("This is the English, US test.", sw.toString().trim());
  }

  /**
   * Tests l7dlog(Priority, String, Throwable) no resource.
   */
  public void testL7dlogNoResource() {    
    logger.l7dlog(debug, "XYZ", (Throwable)null);
    assertEquals("No resource is associated with key \"XYZ\". XYZ ", sw.toString());
  }

  /**
   * Tests l7dlog(Priority, String, Throwable) log nothing.
   */
  public void testL7dlogNothing() {    
    logger.setLevel(Level.ERROR);
    logger.l7dlog(debug, "msg1", (Throwable)null);
    assertEquals("no logging", "", sw.toString());
  }

  /**
   * Tests l7dlog(FQN, Priority, String, Object[], Throwable) log.
   */
  public void testL7dlogFormat() {    
    Object o[] = new Object[] { new Integer(1), "X" };
    logger.l7dlog(debug, "msg1", o, null);
    assertEquals("This is test number 1 with string argument X. ", sw.toString());
  }

  /**
   * Tests l7dlog(FQN, Priority, String, Object[]) log.
   */
  public void testL7dlogFormat2() {    
    Object o[] = new Object[] { new Integer(1), "X" };
    logger.l7dlog(debug, "msg1", o);
    assertEquals("This is test number 1 with string argument X. ", sw.toString());
  }

  /**
   * Tests l7dlog(FQN, Priority, String, Object[], Throwable) log.
   * @since 1.3
   */
  public void testL7dlogFQN() {    
    VectorAppender va = new VectorAppender();
    logger.addAppender(va);    
    logger.l7dlog("myFQN", debug, "msg1", new Object[0], new Throwable());
    LoggingEvent le = (LoggingEvent) va.getVector().get(0);
    assertEquals("myFQN", le.getFQNOfLoggerClass());
    assertNotNull(le.getThrowableInformation());
  }

  /**
   * Tests setPriority(Priority).
   * @deprecated
   */
  public void testSetPriority() {
    Logger logger = Logger.getLogger("org.example.foo");
    Priority debug = Level.DEBUG;
    logger.setPriority(debug);
  }

  /**
   * Derived category to check method signature of forcedLog.
   */
  private static class MockCategory extends Logger {
    /**
     * Create new instance of MockCategory.
     * @param name category name
     */
    public MockCategory(final String name) {
      super(name);
      repository = new Hierarchy(this);
    }

    /**
     * Request an info level message.
     * @param msg message
     */
    public void info(final String msg) {
      Priority info = Level.INFO;
      forcedLog(MockCategory.class.toString(), info, msg, null);
    }
  }
}
