/*
 * ============================================================================
 *                   The Apache Software License, Version 1.1
 * ============================================================================
 *
 *    Copyright (C) 1999 The Apache Software Foundation. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modifica-
 * tion, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of  source code must  retain the above copyright  notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any, must
 *    include  the following  acknowledgment:  "This product includes  software
 *    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
 *    Alternately, this  acknowledgment may  appear in the software itself,  if
 *    and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "log4j" and  "Apache Software Foundation"  must not be used to
 *    endorse  or promote  products derived  from this  software without  prior
 *    written permission. For written permission, please contact
 *    apache@apache.org.
 *
 * 5. Products  derived from this software may not  be called "Apache", nor may
 *    "Apache" appear  in their name,  without prior written permission  of the
 *    Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 * APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 * DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 * ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 * (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * This software  consists of voluntary contributions made  by many individuals
 * on  behalf of the Apache Software  Foundation.  For more  information on the
 * Apache Software Foundation, please see <http://www.apache.org/>.
 *
 */

/*
 * Created on Aug 24, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.joran;

import junit.framework.TestCase;

import org.apache.joran.action.ActionConst;
import org.apache.joran.action.AppenderAction;
import org.apache.joran.action.AppenderRefAction;
import org.apache.joran.action.ConversionRuleAction;
import org.apache.joran.action.LayoutAction;
import org.apache.joran.action.LevelAction;
import org.apache.joran.action.LoggerAction;
import org.apache.joran.action.NestComponentIA;
import org.apache.joran.action.NewRuleAction;
import org.apache.joran.action.ParamAction;
import org.apache.joran.action.RootLoggerAction;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import org.w3c.dom.Document;

import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


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

  public void xtestLoop() throws Exception {
    logger.debug("Starting testLoop");

    DocumentBuilderFactory dbf = null;

    dbf = DocumentBuilderFactory.newInstance();

    DocumentBuilder docBuilder = dbf.newDocumentBuilder();

    //inputSource.setSystemId("dummy://log4j.dtd");
    Document doc = docBuilder.parse("file:input/joran/parser1.xml");
    RuleStore rs = new SimpleRuleStore();
    logger.debug("pattern: " + new Pattern("log4j:configuration/logger"));
    rs.addRule(new Pattern("log4j:configuration/logger"), new LoggerAction());
    rs.addRule(
      new Pattern("log4j:configuration/logger/level"), new LevelAction());
    rs.addRule(
      new Pattern("log4j:configuration/root"), new RootLoggerAction());
    rs.addRule(
      new Pattern("log4j:configuration/root"), new RootLoggerAction());

    JoranParser jp = new JoranParser(rs);
    ExecutionContext ec = jp.getExecutionContext();
    HashMap omap = ec.getObjectMap();
    omap.put(ActionConst.APPENDER_BAG, new HashMap());
    ec.pushObject(LogManager.getLoggerRepository());
    jp.parse(doc);
  }

  public void xtestLoop2() throws Exception {
    logger.debug("Starting testLoop2");

    DocumentBuilderFactory dbf = null;

    dbf = DocumentBuilderFactory.newInstance();

    DocumentBuilder docBuilder = dbf.newDocumentBuilder();

    //inputSource.setSystemId("dummy://log4j.dtd");
    Document doc = docBuilder.parse("file:input/joran/parser2.xml");
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
    jp.parse(doc);
  }

  public void xtestLoop3() throws Exception {
    logger.debug("Starting testLoop3");

    DocumentBuilderFactory dbf = null;

    dbf = DocumentBuilderFactory.newInstance();

    DocumentBuilder docBuilder = dbf.newDocumentBuilder();

    //inputSource.setSystemId("dummy://log4j.dtd");
    Document doc = docBuilder.parse("file:input/joran/parser3.xml");
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
    jp.parse(doc);
  }

  public void xtestNewConversionWord() throws Exception {
    logger.debug("Starting testNewConversionWord");

    DocumentBuilderFactory dbf = null;

    dbf = DocumentBuilderFactory.newInstance();

    DocumentBuilder docBuilder = dbf.newDocumentBuilder();

    //inputSource.setSystemId("dummy://log4j.dtd");
    Document doc = docBuilder.parse("file:input/joran/conversionRule.xml");
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
    jp.parse(doc);

    HashMap appenderBag =
      (HashMap) ec.getObjectMap().get(ActionConst.APPENDER_BAG);
    Appender appender = (Appender) appenderBag.get("A1");
    PatternLayout pl = (PatternLayout) appender.getLayout();
    assertEquals("org.apache.log4j.toto", pl.getRuleRegistry().get("toto"));
  }
  
  public void testNewRule1() throws Exception {
    logger.debug("Starting testNewConversionWord");

    DocumentBuilderFactory dbf = null;

    dbf = DocumentBuilderFactory.newInstance();

    DocumentBuilder docBuilder = dbf.newDocumentBuilder();

    //inputSource.setSystemId("dummy://log4j.dtd");
    Document doc = docBuilder.parse("file:input/joran/newRule1.xml");
    RuleStore rs = new SimpleRuleStore();
    rs.addRule(
      new Pattern("log4j:configuration/newRule"),
      new NewRuleAction());

    JoranParser jp = new JoranParser(rs);
    ExecutionContext ec = jp.getExecutionContext();
    HashMap omap = ec.getObjectMap();
    omap.put(ActionConst.APPENDER_BAG, new HashMap());
    ec.pushObject(LogManager.getLoggerRepository());
    jp.parse(doc);

    String str = (String) ec.getObjectMap().get("hello");
    assertEquals("Hello John Doe.", str);
  }
}
