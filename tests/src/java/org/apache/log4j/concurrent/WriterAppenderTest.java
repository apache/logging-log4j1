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

import java.io.Writer;
import java.io.IOException;
import junit.framework.TestCase;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

/**
 * Tests of WriterAppender.
 */
public class WriterAppenderTest extends TestCase {

  private Logger log = Logger.getLogger(ConcurrentAppenderTest.class);

  private WriterAppender appender = new WriterAppender();

  private SimpleLayout layout = new SimpleLayout();

  {
    log.addAppender(appender);
    layout.setFooter("F");
    layout.setHeader("H");
  }

  private MyStringWriter sw = new MyStringWriter();

  private static class MyStringWriter extends Writer {
    public boolean closed = false;

    public boolean flushed = false;

    public boolean toss = false;

    StringBuffer sb = new StringBuffer();

    public void write(char[] cbuf, int off, int len) throws IOException {
      sb.append(cbuf, off, len);
      if (toss)
        throw new IOException();
    }

    public void flush() {
      flushed = true;
    }

    public void close() throws IOException {
      closed = true;
    }

    public String toString() {
      return sb.toString();
    }

  }

  /**
   * Tests WriterAppender get set and toString.
   */
  public void testBasic() {
    assertEquals(true, appender.getImmediateFlush());
    appender.setImmediateFlush(false);
    assertEquals(false, appender.getImmediateFlush());
    appender.activateOptions();
    assertEquals(false, appender.isActive());
    appender.setLayout(layout);
    appender.activateOptions();
    assertEquals(false, appender.isActive());
    appender.setWriter(sw);
    appender.activateOptions();
    assertEquals(true, appender.isActive());
    appender.close();
    assertEquals(true, sw.closed);
    assertEquals(true, sw.flushed);
    assertEquals(true, appender.requiresLayout());
    appender.setEncoding("ASCII");
    assertEquals("ASCII", appender.getEncoding());
  }

  /**
   * Tests WriterAppender output.
   */
  public void testOutput() {
    appender.setLayout(layout);
    appender.setWriter(sw);
    appender.activateOptions();
    log.debug("HI");
    assertEquals(true, sw.flushed);
    assertEquals("HDEBUG - HI", sw.toString().trim());
    appender.close();
    log.debug("HI");
    assertEquals("HDEBUG - HI" + Layout.LINE_SEP + "F", sw.toString().trim());
  }

  /**
   * Tests Throwable output.
   */
  public void testThrowable() {
    appender.setLayout(layout);
    appender.setWriter(sw);
    appender.activateOptions();
    appender.setImmediateFlush(false);
    sw.flushed = false;
    log.debug("HI", new Throwable());
    assertEquals(false, sw.flushed);
    String s = sw.toString();
    assertTrue(":" + s, s.startsWith("HDEBUG - HI"));
    assertTrue("has a stack trace", s.length() > 40);

    appender.setImmediateFlush(true);
    assertEquals(true, appender.isActive());
    sw.toss = true;
    log.debug("HI");
    log.debug("HI");
    assertEquals(false, appender.isActive());
  }

}
