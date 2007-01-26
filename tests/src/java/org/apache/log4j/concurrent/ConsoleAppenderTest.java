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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

/**
 * Tests of ConsoleAppender.
 */
public class ConsoleAppenderTest extends TestCase {

  private Logger log = Logger.getLogger(ConsoleAppender.class);

  private ConsoleAppender appender = new ConsoleAppender();

  private SimpleLayout layout = new SimpleLayout();

  private ByteArrayOutputStream bo1 = new ByteArrayOutputStream();

  private ByteArrayOutputStream bo2 = new ByteArrayOutputStream();

  private PrintStream err = new PrintStream(bo1);

  private PrintStream out = new PrintStream(bo2);
  {
    appender.setLayout(layout);
    log.addAppender(appender);
  }

  /**
   * Tests ConsoleAppender get set and toString.
   */
  public void testBasic() {
    assertEquals(false, appender.isActive());
    System.setErr(err);
    appender.setTarget("System.err");
    assertEquals(false, appender.isActive());
    appender.activateOptions();
    assertEquals(true, appender.isActive());
    log.debug("HI");
    assertEquals("DEBUG - HI", bo1.toString().trim());
    assertEquals("", bo2.toString());

    appender.setTarget("System.out");
    appender.activateOptions();
    System.setOut(out);
    log.debug("HI");
    assertEquals("not following", "", bo2.toString().trim());

    appender.setFollow(true);
    appender.activateOptions();
    log.debug("HI");
    assertEquals("DEBUG - HI", bo2.toString().trim());
  }

}
