/*
 * Created on Aug 24, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.joran;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import junit.framework.TestCase;

/**
 * @author ceki
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class JoranParserTestCase extends TestCase {

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
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

  public void testLoop() throws Exception {
  	System.out.println("Starting testLoop");

	 DocumentBuilderFactory dbf = null;

   dbf = DocumentBuilderFactory.newInstance();

   DocumentBuilder docBuilder = dbf.newDocumentBuilder();
	   
   //inputSource.setSystemId("dummy://log4j.dtd");

	   Document doc = docBuilder.parse("file:input/joran/parser1.xml");
	   
	   JoranParser jp = new JoranParser(null);
	   jp.parse(doc);
  }



}
