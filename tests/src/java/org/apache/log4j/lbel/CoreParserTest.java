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

/*
 * Verify that the parser can handle the core language correctly. 
 * 
 * @author <a href="http://www.qos.ch/log4j/">Ceki G&uuml;lc&uuml;</a>
 */
public class CoreParserTest extends TestCase {
  LBELEventEvaluator evaluator;

  public CoreParserTest(String arg0) {
    super(arg0);
  }

  protected void setUp() throws Exception {
    super.setUp();
  }

  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void testTrue() throws ScanError {
    evaluator = new LBELEventEvaluator("true");
    assertTrue(evaluator.evaluate(null));
   }
  
  public void testTrueOrFalse() throws ScanError {
    evaluator = new LBELEventEvaluator("true OR false");
    assertTrue(evaluator.evaluate(null));
  }
  
  public void testNotTrue() throws ScanError {
    evaluator = new LBELEventEvaluator("not true");
    assertTrue(!evaluator.evaluate( null));
  }
  
  public void testAndOr() throws ScanError {
    evaluator = new LBELEventEvaluator("true or false and true");
    assertTrue(evaluator.evaluate( null));
  }

  public void testAndOr2() throws ScanError {
    evaluator = new LBELEventEvaluator("false or false and true");
    assertTrue(!evaluator.evaluate(null));
  }

  public void testAndOr3() throws ScanError {
    evaluator = new LBELEventEvaluator("false or true  and true");
    assertTrue(evaluator.evaluate( null));
  }
  
  public void testParatheses() throws ScanError {
    evaluator = new LBELEventEvaluator("(true or false)");
    assertTrue(evaluator.evaluate( null));
  }

  public void testParatheses2() throws ScanError {
    evaluator = new LBELEventEvaluator("(not (true or false)) and true");
    assertTrue(!evaluator.evaluate( null));
  }
 
  public void testNotPrecedence() throws IOException, ScanError {
    evaluator = new LBELEventEvaluator("not true or false and true");
    assertTrue(!evaluator.evaluate( null));
  }

  public void testNotPrecedence2() throws IOException, ScanError {
    evaluator = new LBELEventEvaluator("not true or true and true");
    assertTrue(evaluator.evaluate( null));
  }

  public void testNotPrecedence3() throws IOException, ScanError {
    evaluator = new LBELEventEvaluator("not true and true or false");
    assertTrue(!evaluator.evaluate( null));
  }
  
  public void testNotPrecedence4() throws IOException, ScanError {
    evaluator = new LBELEventEvaluator("not true and true or false");
    assertTrue(!evaluator.evaluate( null));
  }
  
  
  public static Test XXsuite() {
    TestSuite suite = new TestSuite();
    //suite.addTest(new ParserTest("testAndOr"));
    suite.addTest(new CoreParserTest("testTrue"));
    return suite;
  }
  
}
