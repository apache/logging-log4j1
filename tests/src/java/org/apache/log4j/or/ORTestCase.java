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


//
// Log4j uses the JUnit framework for internal unit testing. JUnit
// available from
//
//     http://www.junit.org
package org.apache.log4j.or;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.or.ObjectRenderer;
import org.apache.log4j.or.RendererMap;

import java.io.Serializable;


/**
   Unit test the {@link ObjectRenderer}.
   @author Ceki G&uuml;lc&uuml;
   @since 1.0 */
public class ORTestCase extends TestCase {
  static UTObjectRenderer aor;
  static UTObjectRenderer bor;
  static UTObjectRenderer xor;
  static UTObjectRenderer yor;
  static UTObjectRenderer oor;
  static UTObjectRenderer nor;
  static UTObjectRenderer ior;
  static UTObjectRenderer cor;
  static UTObjectRenderer sor;

  public ORTestCase(String name) {
    super(name);
  }

  public void setUp() {
    aor = new UTObjectRenderer("A");
    bor = new UTObjectRenderer("B");
    xor = new UTObjectRenderer("X");
    yor = new UTObjectRenderer("Y");

    oor = new UTObjectRenderer("Object");
    nor = new UTObjectRenderer("Number");
    ior = new UTObjectRenderer("Integer");
    cor = new UTObjectRenderer("Comparable");
    sor = new UTObjectRenderer("Serializable");
  }

  // Add: no renderer
  // Expect: defaultRenderer
  public void test1() {
    RendererMap map = new RendererMap();
    ObjectRenderer dr = map.getDefaultRenderer();
    ObjectRenderer r = map.get(Integer.class);
    assertEquals(r, dr);
  }

  // Add: Integer renderer
  // Expect: Integer renderer
  public void test2() {
    RendererMap map = new RendererMap();
    map.put(Integer.class, ior);

    ObjectRenderer r = map.get(Integer.class);
    assertEquals(r, ior);
  }

  // Add: Number renderer
  // Expect: Number
  public void test3() {
    RendererMap map = new RendererMap();
    map.put(Number.class, ior);

    ObjectRenderer r = map.get(Integer.class);
    assertEquals(r, ior);
  }

  // Add: Object renderer
  // Result: Object
  public void test4() {
    RendererMap map = new RendererMap();
    map.put(Object.class, oor);

    ObjectRenderer r = map.get(Integer.class);
    assertEquals(r, oor);
  }

  // Add: Object, Number, Integer
  // Expect: Integer
  public void test5() {
    RendererMap map = new RendererMap();

    map.put(Object.class, oor);
    map.put(Number.class, nor);
    map.put(Integer.class, ior);

    ObjectRenderer r = map.get(Integer.class);
    assertEquals(r, ior);
  }

  // Add: Object, Number
  // Expect: Number
  public void test6() {
    RendererMap map = new RendererMap();

    map.put(Object.class, oor);
    map.put(Number.class, nor);

    ObjectRenderer r = map.get(Integer.class);
    assertEquals(r, nor);
  }

  // Add: Comparable
  // Expect: Comparable
  public void test7() {
    RendererMap map = new RendererMap();
    map.put(Comparable.class, cor);

    ObjectRenderer r = map.get(Integer.class);
    assertEquals(r, cor);
  }

  // Add: Serializable
  // Expect: Serializablee
  public void test8() {
    RendererMap map = new RendererMap();
    map.put(Serializable.class, sor);

    ObjectRenderer r = map.get(Integer.class);
    assertEquals(r, sor);
  }

  // Add: Y
  // Expect: Y
  public void test9() {
    RendererMap map = new RendererMap();
    map.put(Y.class, yor);

    ObjectRenderer r = map.get(B.class);
    assertEquals(r, yor);
  }

  // Add: X
  // Expect: X
  public void test10() {
    RendererMap map = new RendererMap();
    map.put(X.class, xor);

    ObjectRenderer r = map.get(B.class);
    assertEquals(r, xor);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(new ORTestCase("test1"));
    suite.addTest(new ORTestCase("test2"));
    suite.addTest(new ORTestCase("test3"));
    suite.addTest(new ORTestCase("test4"));
    suite.addTest(new ORTestCase("test5"));
    suite.addTest(new ORTestCase("test6"));
    suite.addTest(new ORTestCase("test7"));
    suite.addTest(new ORTestCase("test8"));
    suite.addTest(new ORTestCase("test9"));
    suite.addTest(new ORTestCase("test10"));

    return suite;
  }
}


class UTObjectRenderer implements ObjectRenderer {
  String name;

  UTObjectRenderer(String name) {
    this.name = name;
  }

  public String doRender(Object o) {
    return name;
  }

  public String toString() {
    return ("UTObjectRenderer: " + name);
  }
}


interface X {
}


interface Y extends X {
}


class A implements Y {
}


class B extends A {
}
