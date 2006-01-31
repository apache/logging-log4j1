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

package org.apache.log4j.joran;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.joran.action.BadBeginAction;
import org.apache.log4j.joran.action.BadEndAction;
import org.apache.log4j.joran.action.HelloAction;
import org.apache.log4j.joran.action.TouchAction;
import org.apache.log4j.joran.spi.ExecutionContext;
import org.apache.log4j.joran.spi.Interpreter;
import org.apache.log4j.joran.spi.Pattern;
import org.apache.log4j.joran.spi.RuleStore;
import org.apache.log4j.joran.spi.SimpleRuleStore;
import org.apache.log4j.spi.ErrorItem;

import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


/**
 * Test the way Interpreter skips elements in case of exceptions thrown by
 * Actions.
 * 
 * @author Ceki Gulcu
 */
public class SkippingInInterpreterTest extends TestCase {
  static final Logger logger = Logger.getLogger(SkippingInInterpreterTest.class);

  public SkippingInInterpreterTest(String name) {
    super(name);
  }

  /*
   * @see TestCase#setUp()
   */
  protected void setUp() throws Exception {
    super.setUp();

    Logger root = Logger.getRootLogger();
    root.addAppender(
      new ConsoleAppender(new PatternLayout("%r %5p [%t] %c - %m%n")));
    
  }

  /*
   * @see TestCase#tearDown()
   */
  protected void tearDown() throws Exception {
    super.tearDown();
    LogManager.shutdown();
  }

  SAXParser createParser() throws Exception {
    SAXParserFactory spf = SAXParserFactory.newInstance();
    return spf.newSAXParser();
  }
   
  public void testChildrenSkipping() throws Exception {
    logger.debug("Starting testException1");
  
    RuleStore rs = new SimpleRuleStore();
    rs.addRule(new Pattern("test"), new NOPAction());
    rs.addRule(new Pattern("test/badBegin"), new BadBeginAction());
    rs.addRule(new Pattern("test/badBegin/touch"), new TouchAction());
    rs.addRule(new Pattern("test/hello"), new HelloAction());

    Interpreter jp = new Interpreter(rs);
    ExecutionContext ec = jp.getExecutionContext();
    Map omap = ec.getObjectMap();

    SAXParser saxParser = createParser();
    saxParser.parse("file:input/joran/exception1.xml", jp);
    List el = jp.getExecutionContext().getErrorList();
    for(int i = 0; i < el.size(); i++) {
      ((ErrorItem) el.get(i)).dump(); 
    }
    String str = (String) ec.getObjectMap().get("hello");
    assertEquals("Hello John Doe.", str);
    
    Integer i = (Integer) ec.getObjectMap().get(TouchAction.KEY);
    assertNull(i);
  }

  public void testSkipSiblings() throws Exception {
    logger.debug("Starting testException2");
  
    RuleStore rs = new SimpleRuleStore();
    rs.addRule(new Pattern("test"), new NOPAction());
    rs.addRule(new Pattern("test/badEnd"), new BadEndAction());
    rs.addRule(new Pattern("test/badEnd/touch"), new TouchAction());
    rs.addRule(new Pattern("test/hello"), new HelloAction());

    Interpreter jp = new Interpreter(rs);
    ExecutionContext ec = jp.getExecutionContext();
    Map omap = ec.getObjectMap();

    SAXParser saxParser = createParser();
    saxParser.parse("file:input/joran/badEnd1.xml", jp);
    String str = (String) ec.getObjectMap().get("hello");
    assertNull(str);
    Integer i = (Integer) ec.getObjectMap().get(TouchAction.KEY);
    assertEquals(2, i.intValue());
  }

  public void testSkipSiblings2() throws Exception {
    logger.debug("Starting testException2");
  
    RuleStore rs = new SimpleRuleStore();
    rs.addRule(new Pattern("test"), new NOPAction());
    rs.addRule(new Pattern("test/badEnd"), new BadEndAction());
    rs.addRule(new Pattern("test/badEnd/touch"), new TouchAction());
    rs.addRule(new Pattern("test/hello"), new HelloAction());

    Interpreter jp = new Interpreter(rs);
    ExecutionContext ec = jp.getExecutionContext();
    Map omap = ec.getObjectMap();

    SAXParser saxParser = createParser();
    saxParser.parse("file:input/joran/badEnd2.xml", jp);
 
    String str = (String) ec.getObjectMap().get("hello");
    assertEquals("Hello John Doe.", str);
    Integer i = (Integer) ec.getObjectMap().get(TouchAction.KEY);
    assertNull(i);
  }
  
  public static Test RUNALLsuite() {
    TestSuite suite = new TestSuite();
    suite.addTest(new SkippingInInterpreterTest("testChildrenSkipping"));
    suite.addTest(new SkippingInInterpreterTest("testNoSkipping"));
    return suite;
  }

}
