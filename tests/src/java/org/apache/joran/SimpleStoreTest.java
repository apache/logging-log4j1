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
 * Created on Aug 25, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.joran;

import org.apache.joran.action.Action;
import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;

import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


/**
 * @author ceki
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SimpleStoreTest extends TestCase {
  /**
   * Constructor for SimpleStoreTestCase.
   * @param name
   */
  public SimpleStoreTest(String name) {
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
    public void begin(ExecutionContext ec, String name, Attributes attributes, Locator locator) {
    }

    public void end(ExecutionContext ec, String name) {
    }

    public void finish(ExecutionContext ec) {
    }
  }

  class YAction extends Action {
		public void begin(ExecutionContext ec, String name, Attributes attributes, Locator locator) {
		}

		public void end(ExecutionContext ec, String name) {
		}

		public void finish(ExecutionContext ec) {
		}  }

  class ZAction extends Action {
		public void begin(ExecutionContext ec, String name, Attributes attributes, Locator locator) {
		}

		public void end(ExecutionContext ec, String name) {
		}

		public void finish(ExecutionContext ec) {
		}  }
}
