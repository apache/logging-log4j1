/*
 * Created on Aug 25, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.joran;

import junit.framework.TestCase;

/**
 * @author ceki
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PatternTest extends TestCase {

	/**
	 * Constructor for PatternTestCase.
	 * @param name
	 */
	public PatternTest(String name) {
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

	public void test1() {
		Pattern p = new Pattern("a");
		assertEquals(1, p.size());
		assertEquals("a", p.get(0));
	}

	public void test2() {
		Pattern p = new Pattern("a/b");
		assertEquals(2, p.size());
		assertEquals("a", p.get(0));
		assertEquals("b", p.get(1));
	}

	public void test3() {
		Pattern p = new Pattern("a123/b1234/cvvsdf");
		assertEquals(3, p.size());
		assertEquals("a123", p.get(0));
		assertEquals("b1234", p.get(1));
		assertEquals("cvvsdf", p.get(2));
	}
	
	public void test4() {
		Pattern p = new Pattern("/a123/b1234/cvvsdf");
		assertEquals(3, p.size());
		assertEquals("a123", p.get(0));
		assertEquals("b1234", p.get(1));
		assertEquals("cvvsdf", p.get(2));
	}
	

	public void test5() {
		Pattern p = new Pattern("//a");
		assertEquals(1, p.size());
		assertEquals("a", p.get(0));
	}
	
	public void test6() {
			Pattern p = new Pattern("//a//b");
			assertEquals(2, p.size());
			assertEquals("a", p.get(0));
		assertEquals("b", p.get(1));
		}
	
	


}
