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
import org.apache.log4j.FileAppender;
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
import org.apache.log4j.rolling.RollingFileAppender;
import org.apache.log4j.rolling.SizeBasedTriggeringPolicy;
import org.apache.log4j.rolling.SlidingWindowRollingPolicy;

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
public class InterpreterTest extends TestCase {
  static final Logger logger = Logger.getLogger(InterpreterTest.class);

  /**
   * Constructor for JoranParserTestCase.
   * @param name
   */
  public InterpreterTest(String name) {
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
  
  /** 
   * Tests the basic looping contruct in Interpreter.
   * 
   * The parser is set up to push 2 string objects for each element encountered.
   * The results are compared with a witness stack.
   */
  public void testBasicLoop() throws Exception {
    
    RuleStore rs = new SimpleRuleStore();
    rs.addRule(
        new Pattern("log4j:configuration"), new StackCounterAction());
    rs.addRule(
        new Pattern("log4j:configuration/root"), new StackCounterAction());
    rs.addRule(
      new Pattern("log4j:configuration/root/level"), new StackCounterAction());

    Interpreter jp = new Interpreter(rs);
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
  public void testParsing1() throws Exception {
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

    Interpreter jp = new Interpreter(rs);
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

  /**
   * This tests verifies the handling of logger, logger/level, root, root/level
   * logger/appender-ref, root/appender-ref, appender, appender/layout,
   * and param actions.
   * 
   * These cover a fairly significant part of log4j configuration directives.
   * 
   * */
  public void testParsing2() throws Exception {
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

    Interpreter jp = new Interpreter(rs);
    ExecutionContext ec = jp.getExecutionContext();
    HashMap omap = ec.getObjectMap();
    omap.put(ActionConst.APPENDER_BAG, new HashMap());
    ec.pushObject(LogManager.getLoggerRepository());
    SAXParser saxParser = createParser();
    saxParser.parse("file:input/joran/parser2.xml", jp);

    // the following assertions depend on the contensts of parser2.xml
    Logger rootLogger = LogManager.getLoggerRepository().getRootLogger();
    assertSame(Level.DEBUG, rootLogger.getLevel());
 
    Logger asdLogger = LogManager.getLoggerRepository().getLogger("asd");
    assertSame(Level.INFO, asdLogger.getLevel());
    
    FileAppender a1Back = (FileAppender) asdLogger.getAppender("A1");  
    assertFalse("a1.append should be false", a1Back.getAppend());
    assertEquals("output/temp.A1", a1Back.getFile());
    PatternLayout plBack = (PatternLayout) a1Back.getLayout();
    assertEquals("%-5p %c{2} - %m%n", plBack.getConversionPattern());
    
    a1Back = (FileAppender) rootLogger.getAppender("A1");  
    
    assertEquals(3, ec.getErrorList().size());
    String e0 = (String) ec.getErrorList().get(0);
    if(!e0.startsWith("No 'name' attribute in element")) {
      fail("Expected error string [No 'name' attribute in element]");
    }
    String e1 = (String) ec.getErrorList().get(1);
    if(!e1.startsWith("For element <level>")) {
      fail("Expected error string [For element <level>]");
    }
    String e2 = (String) ec.getErrorList().get(2);
    if(!e2.startsWith("Could not find an AppenderAttachable at the top of execution stack. Near")) {
      fail("Expected error string [Could not find an AppenderAttachable at the top of execution stack. Near]");
    }
    
  }

  public void testParsing3() throws Exception {
    logger.debug("Starting testLoop3");

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

    Interpreter jp = new Interpreter(rs);
    jp.addImplcitAction(new NestComponentIA());

    ExecutionContext ec = jp.getExecutionContext();
    HashMap omap = ec.getObjectMap();
    omap.put(ActionConst.APPENDER_BAG, new HashMap());
    ec.pushObject(LogManager.getLoggerRepository());
    logger.debug("About to parse doc");
   
    SAXParser saxParser = createParser();
    saxParser.parse("file:input/joran/parser3.xml", jp);

    // the following assertions depend on the contensts of parser3.xml
    Logger rootLogger = LogManager.getLoggerRepository().getRootLogger();
    assertSame(Level.WARN, rootLogger.getLevel());
 
    RollingFileAppender a1Back = (RollingFileAppender) rootLogger.getAppender("A1");  
    assertFalse("a1.append should be false", a1Back.getAppend());
    PatternLayout plBack = (PatternLayout) a1Back.getLayout();
    assertEquals("%-5p %c{2} - %m%n", plBack.getConversionPattern());
         
    SlidingWindowRollingPolicy swrp = (SlidingWindowRollingPolicy) a1Back.getRollingPolicy();
    assertEquals("output/parser3", swrp.getActiveFileName());
    assertEquals("output/parser3.%i", swrp.getFileNamePattern());
    
    SizeBasedTriggeringPolicy sbtp = (SizeBasedTriggeringPolicy) a1Back.getTriggeringPolicy();
    assertEquals(100, sbtp.getMaxFileSize());
    
    //System.out.println(ec.getErrorList());
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

    Interpreter jp = new Interpreter(rs);
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

    Interpreter jp = new Interpreter(rs);
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
    //suite.addTest(new InterpreterTest("testBasicLoop"));
    //suite.addTest(new InterpreterTest("testParsing1"));
    //suite.addTest(new InterpreterTest("testParsing2"));
    suite.addTest(new InterpreterTest("testParsing3"));
    return suite;
  }

}
