/*
 * Created on Aug 25, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.joran;

import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author ceki
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SimpleStoreTestCase extends TestCase {

	/**
	 * Constructor for SimpleStoreTestCase.
	 * @param name
	 */
	public SimpleStoreTestCase(String name) {
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

	public void test1() throws Exception {

		//Document doc = getW3Document("file:input/joran/parser1.xml");

		SimpleRuleStore srs = new SimpleRuleStore();
		srs.addRule(new Pattern("a/b"), new XAction());

		List r = srs.matchActions(new Pattern("a/b"));
		assertNotNull(r);
		assertEquals(1, r.size());
		if (!(r.get(0) instanceof XAction)) {
			fail("Wring type");
		}

		srs = new SimpleRuleStore();
		srs.addRule(new Pattern("a/b"), new XAction());
		srs.addRule(new Pattern("a/b"), new YAction());

		r = srs.matchActions(new Pattern("a/b"));
		assertNotNull(r);
		assertEquals(2, r.size());
		if (!(r.get(0) instanceof XAction)) {
			fail("Wrong type");
		}
		if (!(r.get(1) instanceof YAction)) {
			fail("Wrong type");
		}

		//jp.parse(doc);
	}

	public void test2() throws Exception {
		SimpleRuleStore srs = new SimpleRuleStore();
		srs.addRule(new Pattern("*/b"), new XAction());
		List r = srs.matchActions(new Pattern("a/b"));
		assertNotNull(r);
		//System.out.println(r);
		assertEquals(1, r.size());
		if (!(r.get(0) instanceof XAction)) {
			fail("Wring type");
		}
	}

	public void test3() throws Exception {
		SimpleRuleStore srs = new SimpleRuleStore();
		srs.addRule(new Pattern("*/b"), new XAction());
		srs.addRule(new Pattern("*/a/b"), new YAction());

		List r = srs.matchActions(new Pattern("a/b"));
		assertNotNull(r);
		//System.out.println("restulg list is: "+r);
		assertEquals(1, r.size());
		if (!(r.get(0) instanceof YAction)) {
			fail("Wring type");
		}
	}

	public void test4() throws Exception {
		SimpleRuleStore srs = new SimpleRuleStore();
		srs.addRule(new Pattern("*/b"), new XAction());
		srs.addRule(new Pattern("*/a/b"), new YAction());
		srs.addRule(new Pattern("a/b"), new ZAction());

		List r = srs.matchActions(new Pattern("a/b"));
		assertNotNull(r);
		//System.out.println("restulg list is: "+r);
		assertEquals(1, r.size());
		if (!(r.get(0) instanceof ZAction)) {
			fail("Wring type");
		}
	}

	Document getW3Document(String file) throws Exception {
		DocumentBuilderFactory dbf = null;
		dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbf.newDocumentBuilder();
		//inputSource.setSystemId("dummy://log4j.dtd");
		return docBuilder.parse(file);
	}

	class XAction extends Action {
		public void begin(Element e) {
		}
		public void end(Element e) {
		}
		public void finish() {
		}
	}

	class YAction extends Action {
		public void begin(Element e) {
		}
		public void end(Element e) {
		}
		public void finish() {
		}
	}
	class ZAction extends Action {
		public void begin(Element e) {
		}
		public void end(Element e) {
		}
		public void finish() {
		}
	}

}