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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.IOException;
import java.io.StringReader;

public class ParserTest extends TestCase {
	Node top;
	Node left;
	Node right;
	
  public ParserTest(String arg0) {
    super(arg0);
  }

  protected void setUp() throws Exception {
    super.setUp();
  }

  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void testTrue() throws IOException, ScanError {
    StringReader sr = new StringReader("true");
    TokenStream ts = new TokenStream(sr);
    Parser parser = new Parser(ts);
    top = parser.parse();
    
    assertEquals(Node.TRUE, top.getType());
    assertNull(top.getLeft());
    assertNull(top.getRight());
    //System.out.println("============");
    //top.leftFirstDump("");
  }
  
  public void testTrueOrFalse() throws IOException, ScanError {
    StringReader sr = new StringReader("true OR false");
    TokenStream ts = new TokenStream(sr);
    Parser parser = new Parser(ts);
    top = parser.parse();
//    assertEquals(Node.E, top.getType());
//    
//    top = top.getLeftSide();
//    assertEquals(Node.DIGIT, top.getType());
//    assertEquals(9, ((Integer) top.getValue()).intValue());
//    assertNull(top.getMiddle());
//    assertNull(top.getRightSide());
    System.out.println("============");
    top.leftFirstDump("");
  }
  
  public void testNotTrue() throws IOException, ScanError {
    StringReader sr = new StringReader("not true");
    TokenStream ts = new TokenStream(sr);
    Parser parser = new Parser(ts);
    top = parser.parse();
  }
  
  public void testAndOr() throws IOException, ScanError {
    StringReader sr = new StringReader("true or false and true");
    TokenStream ts = new TokenStream(sr);
    Parser parser = new Parser(ts);
    top = parser.parse();
    System.out.println("============");
    top.leftFirstDump("");
  }

  public void testParatheses() throws IOException, ScanError {
    StringReader sr = new StringReader("(true or false)");
    TokenStream ts = new TokenStream(sr);
    Parser parser = new Parser(ts);
    top = parser.parse();
    System.out.println("============");
    top.leftFirstDump("");
  }

  public void testParatheses2() throws IOException, ScanError {
    StringReader sr = new StringReader("(not (true or false)) and true");
    TokenStream ts = new TokenStream(sr);
    Parser parser = new Parser(ts);
    top = parser.parse();
    System.out.println("============");
    top.leftFirstDump("");
  }
  
  public static Test suite() {
    TestSuite suite = new TestSuite();
    //suite.addTest(new ParserTest("testAndOr"));
    suite.addTest(new ParserTest("testParatheses2"));
    return suite;
  }
  
}
