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

/*
 * Created on Aug 24, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.joran;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.joran.action.NestComponentIA;
import org.apache.joran.action.NewRuleAction;
import org.apache.joran.action.ParamAction;
import org.apache.joran.action.StackCounterAction;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.joran.action.ActionConst;
import org.apache.log4j.joran.action.AppenderAction;
import org.apache.log4j.joran.action.AppenderRefAction;
import org.apache.log4j.joran.action.ConversionRuleAction;
import org.apache.log4j.joran.action.LayoutAction;
import org.apache.log4j.joran.action.LevelAction;
import org.apache.log4j.joran.action.LoggerAction;
import org.apache.log4j.joran.action.RootLoggerAction;

import java.util.HashMap;
import java.util.Stack;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


/**
 * @author ceki
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class JoranParserTest extends TestCase {
  static final Logger logger = Logger.getLogger(JoranParserTest.class);

  /**
   * Constructor for JoranParserTestCase.
   * @param name
   */
  public JoranParserTest(String name) {
    super(name);
  }

  /*
   * @see TestCase#setUp()
   */
  protected void setUp() throws Exception {
    super.setUp();

    Logger root = Logger.getRootLogger();
    root.addAppender(
      new ConsoleAppender(new PatternLayout("%r %5p [%t] %c{2} - %m%n")));
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
  public void testBasicLoop() throws Exception {
    
    RuleStore rs = new SimpleRuleStore();
    rs.addRule(
        new Pattern("log4j:configuration"), new StackCounterAction());
    rs.addRule(
        new Pattern("log4j:configuration/root"), new StackCounterAction());
    rs.addRule(
      new Pattern("log4j:configuration/root/level"), new StackCounterAction());

    JoranParser jp = new JoranParser(rs);
    ExecutionContext ec = jp.getExecutionContext();
    SAXParser saxParser = createParser();
    saxParser.parse("file:input/joran/basicLoop.xml", jp);
    
    Stack witness = new Stack();
    witness.push("log4j:configuration-begin");
    witness.push("root-begin");
    witness.push("level-begin");
    witness.push("level-end");
    witness.push("root-end");
    witness.push("log4j:configuration-end");
    assertEquals(witness, ec.getObjectStack());
  }
  
  /**
   * This test verifies that <logger>, <root> and embedded <level> elements
   * are handled correctly.  
   */
  public void testLoop() throws Exception {
    logger.debug("Starting testLoop");

    RuleStore rs = new SimpleRuleStore();
    logger.debug("pattern: " + new Pattern("log4j:configuration/logger"));
    rs.addRule(new Pattern("log4j:configuration/logger"), new LoggerAction());
    rs.addRule(
      new Pattern("log4j:configuration/logger/level"), new LevelAction());
    rs.addRule(
      new Pattern("log4j:configuration/root"), new RootLoggerAction());
    rs.addRule(
        new Pattern("log4j:configuration/root/level"), new LevelAction());

    JoranParser jp = new JoranParser(rs);
    ExecutionContext ec = jp.getExecutionContext();
    HashMap omap = ec.getObjectMap();
    omap.put(ActionConst.APPENDER_BAG, new HashMap());
    ec.pushObject(LogManager.getLoggerRepository());
    SAXParser saxParser = createParser();
    saxParser.parse("file:input/joran/parser1.xml", jp);
    
    Logger rootLogger = LogManager.getLoggerRepository().getRootLogger();
    assertSame(Level.WARN, rootLogger.getLevel());
 
    Logger asdLogger = LogManager.getLoggerRepository().getLogger("asd");
    assertSame(Level.DEBUG, asdLogger.getLevel());
 
    assertEquals(2, ec.getErrorList().size());
    String e0 = (String) ec.getErrorList().get(0);
    if(!e0.startsWith("No 'name' attribute in element")) {
      fail("Expected error string [No 'name' attribute in element]");
    }
    String e1 = (String) ec.getErrorList().get(1);
    if(!e1.startsWith("For element <level>")) {
      fail("Expected error string [For element <level>]");
    }
  }

  public void xtestLoop2() throws Exception {
    logger.debug("Starting testLoop2");
    RuleStore rs = new SimpleRuleStore();
    rs.addRule(new Pattern("log4j:configuration/logger"), new LoggerAction());
    rs.addRule(
      new Pattern("log4j:configuration/logger/level"), new LevelAction());
    rs.addRule(
      new Pattern("log4j:configuration/root"), new RootLoggerAction());
    rs.addRule(
      new Pattern("log4j:configuration/root/level"), new LevelAction());
    rs.addRule(
      new Pattern("log4j:configuration/logger/appender-ref"),
      new AppenderRefAction());
    rs.addRule(
      new Pattern("log4j:configuration/root/appender-ref"),
      new AppenderRefAction());
    rs.addRule(
      new Pattern("log4j:configuration/appender"), new AppenderAction());
    rs.addRule(
      new Pattern("log4j:configuration/appender/layout"), new LayoutAction());
    rs.addRule(new Pattern("*/param"), new ParamAction());

    JoranParser jp = new JoranParser(rs);
    ExecutionContext ec = jp.getExecutionContext();
    HashMap omap = ec.getObjectMap();
    omap.put(ActionConst.APPENDER_BAG, new HashMap());
    ec.pushObject(LogManager.getLoggerRepository());
    SAXParser saxParser = createParser();
    saxParser.parse("file:input/joran/parser2.xml", jp);
  }

  public void xtestLoop3() throws Exception {
    logger.debug("Starting testLoop3");

    RuleStore rs = new SimpleRuleStore();
    rs.addRule(new Pattern("log4j:configuration/logger"), new LoggerAction());
    rs.addRule(
      new Pattern("log4j:configuration/logger/level"), new LevelAction());
    rs.addRule(
      new Pattern("log4j:configuration/root"), new RootLoggerAction());

    //rs.addRule(
    //new Pattern("log4j:configuration/root/level"), new LevelAction());
    rs.addRule(
      new Pattern("log4j:configuration/logger/appender-ref"),
      new AppenderRefAction());
    rs.addRule(
      new Pattern("log4j:configuration/root/appender-ref"),
      new AppenderRefAction());
    rs.addRule(
      new Pattern("log4j:configuration/appender"), new AppenderAction());
    rs.addRule(
      new Pattern("log4j:configuration/appender/layout"), new LayoutAction());
    rs.addRule(new Pattern("*/param"), new ParamAction());

    JoranParser jp = new JoranParser(rs);
    jp.addImplcitAction(new NestComponentIA());

    ExecutionContext ec = jp.getExecutionContext();
    HashMap omap = ec.getObjectMap();
    omap.put(ActionConst.APPENDER_BAG, new HashMap());
    ec.pushObject(LogManager.getLoggerRepository());
    logger.debug("About to parse doc");
   
    SAXParser saxParser = createParser();
    saxParser.parse("file:input/joran/parser3.xml", jp);

  }

  public void testNewConversionWord() throws Exception {
    logger.debug("Starting testNewConversionWord");

    RuleStore rs = new SimpleRuleStore();
    rs.addRule(
      new Pattern("log4j:configuration/appender"), new AppenderAction());
    rs.addRule(
      new Pattern("log4j:configuration/appender/layout"), new LayoutAction());
    rs.addRule(
      new Pattern("log4j:configuration/appender/layout/conversionRule"),
      new ConversionRuleAction());

    rs.addRule(new Pattern("*/param"), new ParamAction());

    JoranParser jp = new JoranParser(rs);
    jp.addImplcitAction(new NestComponentIA());

    ExecutionContext ec = jp.getExecutionContext();
    HashMap omap = ec.getObjectMap();
    omap.put(ActionConst.APPENDER_BAG, new HashMap());
    ec.pushObject(LogManager.getLoggerRepository());

    SAXParser saxParser = createParser();
    saxParser.parse("file:input/joran/conversionRule.xml", jp);

    HashMap appenderBag =
      (HashMap) ec.getObjectMap().get(ActionConst.APPENDER_BAG);
    Appender appender = (Appender) appenderBag.get("A1");
    PatternLayout pl = (PatternLayout) appender.getLayout();
    assertEquals("org.apache.log4j.toto", pl.getRuleRegistry().get("toto"));
  }
  
  public void testNewRule1() throws Exception {
    logger.debug("Starting testNewConversionWord");
  
    RuleStore rs = new SimpleRuleStore();
    rs.addRule(
      new Pattern("log4j:configuration/newRule"),
      new NewRuleAction());

    JoranParser jp = new JoranParser(rs);
    ExecutionContext ec = jp.getExecutionContext();
    HashMap omap = ec.getObjectMap();
    omap.put(ActionConst.APPENDER_BAG, new HashMap());
    ec.pushObject(LogManager.getLoggerRepository());

    SAXParser saxParser = createParser();
    saxParser.parse("file:input/joran/newRule1.xml", jp);

    String str = (String) ec.getObjectMap().get("hello");
    assertEquals("Hello John Doe.", str);
  }
  
  public static Test suite() {
    TestSuite suite = new TestSuite();
    //suite.addTest(new JoranParserTest("testBasicLoop"));
    suite.addTest(new JoranParserTest("testLoop"));
    return suite;
  }

}
