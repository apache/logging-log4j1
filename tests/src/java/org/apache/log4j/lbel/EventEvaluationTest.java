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

import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;



public class EventEvaluationTest extends TestCase {
	LoggingEvent event;
	EventEvaluator evaluator;
  
  public EventEvaluationTest(String arg0) {
    super(arg0);
  }

  protected void setUp() throws Exception {
    super.setUp();
    event = new LoggingEvent();
    event.setLevel(Level.INFO);
    event.setMessage("hello world");
    event.setLoggerName("org.wombat");
    
  }

  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void testLevel() throws ScanError {
    evaluator = new LBELEventEvaluator("level = DEBUG");
    assertTrue(!evaluator.evaluate(event));
    
    evaluator = new LBELEventEvaluator("level = INFO");
    assertTrue(evaluator.evaluate(event));
  }
 
  public void testMessage() throws ScanError {
    evaluator = new LBELEventEvaluator("message = 'hello world'");
    assertTrue(evaluator.evaluate(event));

    evaluator = new LBELEventEvaluator("message = hello");
    assertTrue(!evaluator.evaluate(event));

    evaluator = new LBELEventEvaluator("message != hello");
    assertTrue(evaluator.evaluate(event));

    
    evaluator = new LBELEventEvaluator("message > hello");
    assertTrue(evaluator.evaluate(event));

    evaluator = new LBELEventEvaluator("message >= hello");
    assertTrue(evaluator.evaluate(event));

    evaluator = new LBELEventEvaluator("message < hello");
    assertTrue(!evaluator.evaluate(event));

    evaluator = new LBELEventEvaluator("message <= hello");
    assertTrue(!evaluator.evaluate(event));
  }
  
  public void testRegexMessage() throws Exception, ScanError {
    evaluator = new LBELEventEvaluator("message ~ 'hello'");
    assertTrue(evaluator.evaluate(event));
    
    evaluator = new LBELEventEvaluator("message ~ 'h[a-z]* world'");
    assertTrue(evaluator.evaluate(event));

    // the following test cannot be run because of a bug in the way 
    // java.io.StreamTokenizer incorrectly interprets the '\' character within quotes
    // evaluator = new LBELEventEvaluator("message ~ 'h\\w* world'");
    //assertTrue(evaluator.evaluate(event));

    LBELEventEvaluator evaluator = new LBELEventEvaluator("message !~ 'x'");
    assertTrue(evaluator.evaluate(event));
    
    evaluator = new LBELEventEvaluator("message !~ 'x[a-z]* world'");
    assertTrue(evaluator.evaluate(event));
  }
  
  public void testLogger() throws Exception, ScanError {
    evaluator = new LBELEventEvaluator("logger = org.wombat");
    assertTrue(evaluator.evaluate(event));
    
    evaluator = new LBELEventEvaluator("logger = org.wombat.x");
    assertTrue(!evaluator.evaluate(event));
    
    evaluator = new LBELEventEvaluator("logger != org.wombat.x");
    assertTrue(evaluator.evaluate(event));
    
    evaluator = new LBELEventEvaluator("logger < org.wombat.x");
    assertTrue(evaluator.evaluate(event));

    evaluator = new LBELEventEvaluator("logger <= org.wombat.x");
    assertTrue(evaluator.evaluate(event));

    evaluator = new LBELEventEvaluator("logger > org");
    assertTrue(evaluator.evaluate(event));

    evaluator = new LBELEventEvaluator("logger >= org");
    assertTrue(evaluator.evaluate(event));
    
    evaluator = new LBELEventEvaluator("logger CHILDOF org");
    assertTrue(evaluator.evaluate(event));
  }
    
  public static Test XXsuite() {
    TestSuite suite = new TestSuite();
    //suite.addTest(new ParserTest("testAndOr"));
    suite.addTest(new EventEvaluationTest("testLevel"));
    return suite;
  }
  
}
