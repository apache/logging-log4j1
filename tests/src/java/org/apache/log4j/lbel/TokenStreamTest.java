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

import junit.framework.TestCase;

import java.io.IOException;
import java.io.StringReader;


public class TokenStreamTest extends TestCase {
  Token t;

  public TokenStreamTest(String arg0) {
    super(arg0);
  }

  protected void setUp() throws Exception {
    super.setUp();
  }

  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void testSingleDigit() throws IOException, ScanError {
    StringReader sr = new StringReader("9");
    TokenStream ts = new TokenStream(sr);

    assertNull(ts.getCurrent());
    ts.next();

    t = ts.getCurrent();
    assertEquals(Token.NUMBER, t.getType());
    assertEquals(9, ((Long) t.getValue()).longValue());

    ts.next();
    t = ts.getCurrent();
    assertEquals(Token.EOF, t.getType());
  }

  public void testLongerDigit() throws IOException, ScanError {
    StringReader sr = new StringReader(" 980 ");
    TokenStream ts = new TokenStream(sr);
    ts.next();
    t = ts.getCurrent();
    assertEquals(Token.NUMBER, t.getType());
    assertEquals(980, ((Long) t.getValue()).longValue());

    ts.next();
    t = ts.getCurrent();
    assertEquals(Token.EOF, t.getType());
  }

  public void testComparison() throws IOException, ScanError {
    StringReader sr = new StringReader(" time >= 17");
    TokenStream ts = new TokenStream(sr);
    ts.next();
    t = ts.getCurrent();

    assertEquals(Token.LITERAL, t.getType());
    assertEquals("time", t.getValue());

    ts.next();
    t = ts.getCurrent();
    assertEquals(Token.OPERATOR, t.getType());
    assertEquals(">=", t.getValue());

    ts.next();
    t = ts.getCurrent();
    assertEquals(Token.NUMBER, t.getType());
    assertEquals(17, ((Long) t.getValue()).longValue());

    ts.next();
    t = ts.getCurrent();
    assertEquals(Token.EOF, t.getType());
  }

  public void testRegex() throws IOException, ScanError {
    StringReader sr = new StringReader(" time ~ x");
    TokenStream ts = new TokenStream(sr);
    
    ts.next(); t = ts.getCurrent();
    assertEquals(Token.LITERAL, t.getType());
    assertEquals("time", t.getValue());
    
    ts.next(); t = ts.getCurrent();
    assertEquals(Token.OPERATOR, t.getType());
    assertEquals("~", t.getValue());
    
    ts.next(); t = ts.getCurrent();
    assertEquals(Token.LITERAL, t.getType());
    assertEquals("x", t.getValue());
  }

  public void testNotRegex() throws IOException, ScanError {
    StringReader sr = new StringReader(" t !~ x");
    TokenStream ts = new TokenStream(sr);
    
    ts.next(); t = ts.getCurrent();
    assertEquals(Token.LITERAL, t.getType());
    assertEquals("t", t.getValue());
    
    ts.next(); t = ts.getCurrent();
    assertEquals(Token.OPERATOR, t.getType());
    assertEquals("!~", t.getValue());
    
    ts.next(); t = ts.getCurrent();
    assertEquals(Token.LITERAL, t.getType());
    assertEquals("x", t.getValue());
  }

  
  public void testFull() throws IOException, ScanError {
    StringReader sr = new StringReader(" time >= 19 NOT \"hello world\" ");
    TokenStream ts = new TokenStream(sr);
    ts.next();
    t = ts.getCurrent();

    assertEquals(Token.LITERAL, t.getType());
    assertEquals("time", t.getValue());

    ts.next();
    t = ts.getCurrent();
    assertEquals(Token.OPERATOR, t.getType());
    assertEquals(">=", t.getValue());

    ts.next();
    t = ts.getCurrent();
    assertEquals(Token.NUMBER, t.getType());
    assertEquals(19, ((Long) t.getValue()).longValue());

    ts.next();
    t = ts.getCurrent();
    assertEquals(Token.NOT, t.getType());

    ts.next();
    t = ts.getCurrent();
    assertEquals(Token.LITERAL, t.getType());
    assertEquals("hello world", t.getValue());

    ts.next();
    t = ts.getCurrent();
    assertEquals(Token.EOF, t.getType());
  }

  public void testNoSpaceFull() throws IOException, ScanError {
    StringReader sr = new StringReader(" time>=19 NOT \"hello world\" ");
    TokenStream ts = new TokenStream(sr);
    ts.next();
    t = ts.getCurrent();

    assertEquals(Token.LITERAL, t.getType());
    assertEquals("time", t.getValue());

    ts.next();
    t = ts.getCurrent();
    assertEquals(Token.OPERATOR, t.getType());
    assertEquals(">=", t.getValue());

    ts.next();
    t = ts.getCurrent();
    assertEquals(Token.NUMBER, t.getType());
    assertEquals(19, ((Long) t.getValue()).longValue());

    ts.next();
    t = ts.getCurrent();
    assertEquals(Token.NOT, t.getType());

    ts.next();
    t = ts.getCurrent();
    assertEquals(Token.LITERAL, t.getType());
    assertEquals("hello world", t.getValue());

    ts.next();
    t = ts.getCurrent();
    assertEquals(Token.EOF, t.getType());
  }

  public void testSingleQuote() throws IOException, ScanError {
    StringReader sr = new StringReader(" logger ~ 'hello world' ");
    TokenStream ts = new TokenStream(sr);
    ts.next();
    t = ts.getCurrent();

    assertEquals(Token.LOGGER, t.getType());

    ts.next();
    t = ts.getCurrent();
    assertEquals(Token.OPERATOR, t.getType());
    assertEquals("~", t.getValue());

    ts.next();
    t = ts.getCurrent();
    assertEquals(Token.LITERAL, t.getType());
    assertEquals("hello world", t.getValue());

    ts.next();
    t = ts.getCurrent();
    assertEquals(Token.EOF, t.getType());
  }

  public void testTrueOrFalse() throws IOException, ScanError {
    StringReader sr = new StringReader(" true OR false");
    TokenStream ts = new TokenStream(sr);

    ts.next();
    t = ts.getCurrent();
    assertEquals(Token.TRUE, t.getType());

    ts.next();
    t = ts.getCurrent();
    assertEquals(Token.OR, t.getType());

    ts.next();
    t = ts.getCurrent();
    assertEquals(Token.FALSE, t.getType());

    ts.next();
    t = ts.getCurrent();
    assertEquals(Token.EOF, t.getType());
  }
  
  public void testProperty() throws IOException, ScanError {
    StringReader sr = new StringReader(" property.x property.xyz property.");
    TokenStream ts = new TokenStream(sr);

    ts.next(); t = ts.getCurrent();
    assertEquals(Token.PROPERTY, t.getType());
    ts.next(); t = ts.getCurrent();
    assertEquals(Token.DOT, t.getType());
    ts.next(); t = ts.getCurrent();
    assertEquals(Token.LITERAL, t.getType());
    assertEquals("x", t.getValue());

    ts.next(); t = ts.getCurrent();
    assertEquals(Token.PROPERTY, t.getType());
    ts.next(); t = ts.getCurrent();
    assertEquals(Token.DOT, t.getType());
    ts.next(); t = ts.getCurrent();
    assertEquals(Token.LITERAL, t.getType());
    assertEquals("xyz", t.getValue());

    ts.next(); t = ts.getCurrent();
    assertEquals(Token.PROPERTY, t.getType());
    ts.next(); t = ts.getCurrent();
    assertEquals(Token.DOT, t.getType());
    
  }

  
  public void testQuotedProperty() throws IOException, ScanError {
    
    StringReader sr = new StringReader(" property.'toto a' etc");
    TokenStream ts = new TokenStream(sr);

    ts.next(); t = ts.getCurrent();
    assertEquals(Token.PROPERTY, t.getType());
    ts.next(); t = ts.getCurrent();
    assertEquals(Token.DOT, t.getType());
    ts.next(); t = ts.getCurrent();
    assertEquals(Token.LITERAL, t.getType());
    assertEquals("toto a", t.getValue());
  }
}
