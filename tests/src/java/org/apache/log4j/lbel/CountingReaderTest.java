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

package org.apache.log4j.lbel;

import java.io.IOException;
import java.io.StringReader;

import junit.framework.TestCase;


/**
 * 
 * @author <a href="http://www.qos.ch/log4j/">Ceki G&uuml;lc&uuml;</a>
 */
public class CountingReaderTest extends TestCase {
  public CountingReaderTest(String arg0) {
    super(arg0);
  }

  protected void setUp() throws Exception {
    super.setUp();
  }

  protected void tearDown() throws Exception {
    super.tearDown();
  }
  
  public void testOneLiner() throws IOException {
    StringReader sr = new StringReader("abc");
    CountingReader cr = new CountingReader(sr);
    int c; 
    c = cr.read();
    assertEquals('a', c);
    assertEquals(0, cr.getLineNumber());
    assertEquals(1, cr.getColumnNumber());
    c = cr.read();
    assertEquals('b', c);
    assertEquals(0, cr.getLineNumber());
    assertEquals(2, cr.getColumnNumber());
    c = cr.read();
    assertEquals('c', c);
    assertEquals(0, cr.getLineNumber());
    assertEquals(3, cr.getColumnNumber());

    c = cr.read();
    assertEquals(-1, c);
    assertEquals(0, cr.getLineNumber());
    assertEquals(3, cr.getColumnNumber());
  }
  
  public void testTwoLinerR() throws IOException {
    StringReader sr = new StringReader("a\rA");
    CountingReader cr = new CountingReader(sr);
    int c; 
    c = cr.read();
    assertEquals('a', c);
    assertEquals(0, cr.getLineNumber());
    assertEquals(1, cr.getColumnNumber());
    c = cr.read();
    assertEquals('\r', c);
    assertEquals(1, cr.getLineNumber());
    assertEquals(0, cr.getColumnNumber());
    c = cr.read();
    assertEquals('A', c);
    assertEquals(1, cr.getLineNumber());
    assertEquals(1, cr.getColumnNumber());
    c = cr.read();
    assertEquals(-1, c);
    assertEquals(1, cr.getLineNumber());
    assertEquals(1, cr.getColumnNumber());
  }
  public void testTwoLinerN() throws IOException {
    StringReader sr = new StringReader("a\nA");
    CountingReader cr = new CountingReader(sr);
    int c; 
    c = cr.read();
    assertEquals('a', c);
    assertEquals(0, cr.getLineNumber());
    assertEquals(1, cr.getColumnNumber());
    c = cr.read();
    assertEquals('\n', c);
    assertEquals(1, cr.getLineNumber());
    assertEquals(0, cr.getColumnNumber());
    c = cr.read();
    assertEquals('A', c);
    assertEquals(1, cr.getLineNumber());
    assertEquals(1, cr.getColumnNumber());
    c = cr.read();
    assertEquals(-1, c);
    assertEquals(1, cr.getLineNumber());
    assertEquals(1, cr.getColumnNumber());
  }
  
  public void testTwoLinerRN() throws IOException {
    StringReader sr = new StringReader("a\r\nA");
    CountingReader cr = new CountingReader(sr);
    int c; 
    c = cr.read();
    assertEquals('a', c);
    assertEquals(0, cr.getLineNumber());
    assertEquals(1, cr.getColumnNumber());
    c = cr.read();
    assertEquals('\r', c);
    assertEquals(1, cr.getLineNumber());
    assertEquals(0, cr.getColumnNumber());
    c = cr.read();
    assertEquals('\n', c);
    assertEquals(1, cr.getLineNumber());
    assertEquals(0, cr.getColumnNumber());
    c = cr.read();
    assertEquals('A', c);
    assertEquals(1, cr.getLineNumber());
    assertEquals(1, cr.getColumnNumber());
    c = cr.read();
    assertEquals(-1, c);
    assertEquals(1, cr.getLineNumber());
    assertEquals(1, cr.getColumnNumber());
  }
}
