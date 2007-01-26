/*
 * Copyright 2006 The Apache Software Foundation.
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

package org.apache.log4j.concurrent;

import junit.framework.TestCase;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.filter.DenyAllFilter;
import org.apache.log4j.filter.StringMatchFilter;

/**
 * Tests of ConcurrentAppender.
 */
public class ConcurrentAppenderTest extends TestCase {

  private Logger log = Logger.getLogger(ConcurrentAppenderTest.class);

  private MyConcurrentAppender a = new MyConcurrentAppender();

  private String name = "name";

  private String msg = "Hello, World";

  private SimpleLayout layout = new SimpleLayout();

  private DenyAllFilter denyFilter = new DenyAllFilter();

  private StringMatchFilter stringMatchFilter = new StringMatchFilter();
  {
    stringMatchFilter.setStringToMatch("yo");
  }

  /**
   * Tests set and get methods.
   */
  public void testSetGet() {
    assertEquals(false, a.isActive());
    assertEquals(false, a.isClosed());
    assertEquals(false, a.getClosed());
    assertEquals(null, a.getName());
    a.setName(name);
    assertEquals(name, a.getName());
    assertEquals(null, a.getThreshold());
    a.setThreshold(Level.INFO);
    assertEquals(Level.INFO, a.getThreshold());
    assertEquals(null, a.getLayout());
    a.setLayout(layout);
    assertEquals(layout, a.getLayout());

    assertNotNull(a.getErrorHandler());
    a.setErrorHandler(null);
    assertNotNull(a.toString());
  }

  /**
   * Tests log methods, threshold, filter.
   */
  public void testLog() {
    log.addAppender(a);
    log.debug(msg);
    assertEquals("not activated", null, a.event);
    a.activateOptions();
    log.debug(msg);
    assertNotNull(a.event);

    a.setThreshold(Level.INFO);
    a.event = null;
    log.debug(msg);
    assertEquals("filtered", null, a.event);
    log.fatal(msg);
    assertNotNull(a.event);

    a.event = null;
    stringMatchFilter.setStringToMatch("yo");
    a.addFilter(stringMatchFilter);
    a.addFilter(denyFilter);
    log.fatal("Not y and o");
    assertEquals("filtered", null, a.event);
    log.fatal("yo yo yo");
    assertNotNull(a.event);
    assertEquals(stringMatchFilter, a.getFilter());

    a.clearFilters();
    a.event = null;
    log.fatal("Not y and o");
    assertNotNull("no longer filtered", a.event);
  }

  /**
   * Tests active and close methods.
   */
  public void testClose() {
    log.addAppender(a);
    log.debug("not active");
    a.activateOptions();
    assertEquals(true, a.isActive());
    a.close();
    assertEquals(true, a.internalClosed);
    assertEquals(true, a.isClosed());
    assertEquals(true, a.getClosed());
    log.debug("not logged");
    assertEquals("closed", null, a.event);
    a.close(); // shouldn't call internalClose() twice
    assertEquals(true, a.internalClosed);
    assertEquals(true, a.isClosed());
  }

  class MyConcurrentAppender extends ConcurrentAppender {

    LoggingEvent event;

    boolean internalClosed;

    MyConcurrentAppender() {
      super(false);
    }

    protected void append(LoggingEvent event) {
      this.event = event;
    }

    public boolean requiresLayout() {
      return true;
    }

    protected void internalClose() {
      if (internalClosed)
        throw new IllegalStateException();
      internalClosed = true;
    }

  }

}
