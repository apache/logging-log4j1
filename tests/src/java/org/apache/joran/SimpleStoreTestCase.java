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
 * Created on Aug 25, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.joran;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


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
