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


public class EvaluationTest extends TestCase {
	Node top;
	Node left;
	Node right;
	
  public EvaluationTest(String arg0) {
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
    
    assertTrue(parser.evaluate(top));
    
   }
  
  public void testTrueOrFalse() throws IOException, ScanError {
    StringReader sr = new StringReader("true OR false");
    TokenStream ts = new TokenStream(sr);
    Parser parser = new Parser(ts);
    top = parser.parse();
  }
  
  public void testNotTrue() throws IOException, ScanError {
    StringReader sr = new StringReader("not true");
    TokenStream ts = new TokenStream(sr);
    Parser parser = new Parser(ts);
    top = parser.parse();
    assertTrue(!parser.evaluate(top));
  }
  
  public void testAndOr() throws IOException, ScanError {
    StringReader sr = new StringReader("true or false and true");
    TokenStream ts = new TokenStream(sr);
    Parser parser = new Parser(ts);
    top = parser.parse();
    assertTrue(parser.evaluate(top));
  }

  public void testAndOr2() throws IOException, ScanError {
    StringReader sr = new StringReader("false or false and true");
    TokenStream ts = new TokenStream(sr);
    Parser parser = new Parser(ts);
    top = parser.parse();
    assertTrue(!parser.evaluate(top));
  }

  public void testAndOr3() throws IOException, ScanError {
    StringReader sr = new StringReader("false or true  and true");
    TokenStream ts = new TokenStream(sr);
    Parser parser = new Parser(ts);
    top = parser.parse();
    assertTrue(parser.evaluate(top));
  }
  
  public void testParatheses() throws IOException, ScanError {
    StringReader sr = new StringReader("(true or false)");
    TokenStream ts = new TokenStream(sr);
    Parser parser = new Parser(ts);
    top = parser.parse();
    assertTrue(parser.evaluate(top));
  }

  public void testParatheses2() throws IOException, ScanError {
    StringReader sr = new StringReader("(not (true or false)) and true");
    TokenStream ts = new TokenStream(sr);
    Parser parser = new Parser(ts);
    top = parser.parse();
    assertTrue(!parser.evaluate(top));
  }
 
  public void testNotPrecedence() throws IOException, ScanError {
    StringReader sr = new StringReader("not true or false and true");
    TokenStream ts = new TokenStream(sr);
    Parser parser = new Parser(ts);
    top = parser.parse();
    assertTrue(!parser.evaluate(top));
  }

  public void testNotPrecedence2() throws IOException, ScanError {
    StringReader sr = new StringReader("not true or true and true");
    TokenStream ts = new TokenStream(sr);
    Parser parser = new Parser(ts);
    top = parser.parse();
    assertTrue(parser.evaluate(top));
  }

  public void testNotPrecedence3() throws IOException, ScanError {
    StringReader sr = new StringReader("not true and true or false");
    TokenStream ts = new TokenStream(sr);
    Parser parser = new Parser(ts);
    top = parser.parse();
    assertTrue(!parser.evaluate(top));
  }
  
  public void testNotPrecedence4() throws IOException, ScanError {
    StringReader sr = new StringReader("not true and true or false");
    TokenStream ts = new TokenStream(sr);
    Parser parser = new Parser(ts);
    top = parser.parse();
    assertTrue(!parser.evaluate(top));
  }
  
  
  public static Test XXsuite() {
    TestSuite suite = new TestSuite();
    //suite.addTest(new ParserTest("testAndOr"));
    suite.addTest(new EvaluationTest("testTrue"));
    return suite;
  }
  
}
