/*
 * Created on Aug 24, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.joran;

import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.joran.action.ActionConst;
import org.apache.joran.action.AppenderAction;
import org.apache.joran.action.AppenderRefAction;
import org.apache.joran.action.LayoutAction;
import org.apache.joran.action.LevelAction;
import org.apache.joran.action.LoggerAction;
import org.apache.joran.action.ParamAction;
import org.apache.joran.action.RootLoggerAction;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.w3c.dom.Document;

import junit.framework.TestCase;

/**
 * @author ceki
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class JoranParserTest extends TestCase {

   final static Logger logger = Logger.getLogger(JoranParserTest.class);  
	
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
				 new ConsoleAppender(
					 new PatternLayout("%r %5p [%t] %c{2} - %m%n")));
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
	   logger.debug("pattern: "+new Pattern("log4j:configuration/logger"));
	   rs.addRule(new Pattern("log4j:configuration/logger"), new LoggerAction());
		 rs.addRule(new Pattern("log4j:configuration/logger/level"), new LevelAction());
		 rs.addRule(new Pattern("log4j:configuration/root"), new RootLoggerAction());
		rs.addRule(new Pattern("log4j:configuration/root"), new RootLoggerAction());
	   JoranParser jp = new JoranParser(rs);
		 ExecutionContext ec = jp.getExecutionContext();
	   HashMap omap = ec.getObjectMap();
	   omap.put(ActionConst.APPENDER_BAG, new HashMap());
		 ec.pushObject(LogManager.getLoggerRepository());
	   jp.parse(doc);
  }

	public void testLoop2() throws Exception {
			logger.debug("Starting testLoop2");

		 DocumentBuilderFactory dbf = null;

		 dbf = DocumentBuilderFactory.newInstance();

		 DocumentBuilder docBuilder = dbf.newDocumentBuilder();
	   
		 //inputSource.setSystemId("dummy://log4j.dtd");

			 Document doc = docBuilder.parse("file:input/joran/parser2.xml");
			 RuleStore rs = new SimpleRuleStore();
			 rs.addRule(new Pattern("log4j:configuration/logger"), new LoggerAction());
			 rs.addRule(new Pattern("log4j:configuration/logger/level"), new LevelAction());
			 rs.addRule(new Pattern("log4j:configuration/root"), new RootLoggerAction());
		   rs.addRule(new Pattern("log4j:configuration/logger/appender-ref"), new AppenderRefAction());
		   rs.addRule(new Pattern("log4j:configuration/root/appender-ref"), new AppenderRefAction());
		   rs.addRule(new Pattern("log4j:configuration/appender"), new AppenderAction());
		   rs.addRule(new Pattern("log4j:configuration/appender/layout"), new LayoutAction());
	     rs.addRule(new Pattern("*/param"), new ParamAction());
			 JoranParser jp = new JoranParser(rs);
			 ExecutionContext ec = jp.getExecutionContext();
			 HashMap omap = ec.getObjectMap();
			 omap.put(ActionConst.APPENDER_BAG, new HashMap());
			 ec.pushObject(LogManager.getLoggerRepository());
			 jp.parse(doc);
		}


}
