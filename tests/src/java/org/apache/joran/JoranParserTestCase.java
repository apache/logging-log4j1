/*
 * Created on Aug 24, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.joran;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.joran.action.LevelAction;
import org.apache.joran.action.LoggerAction;
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
public class JoranParserTestCase extends TestCase {

   final static Logger logger = Logger.getLogger(JoranParserTestCase.class);  
	
	/**
	 * Constructor for JoranParserTestCase.
	 * @param name
	 */
	public JoranParserTestCase(String name) {
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

  public void testLoop() throws Exception {
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
	   JoranParser jp = new JoranParser(rs);
		 ExecutionContext ec = jp.getExecutionContext();
		 ec.pushObject(LogManager.getLoggerRepository());
	   jp.parse(doc);
  }



}
