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

package org.apache.log4j.helpers;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.Level;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.xml.XLevel;

import java.util.Properties;


/**
 * Test variable substitution code.
 * 
 * @author Ceki G&uuml;lc&uuml; 
*/
public class LevelOptionConverterTest extends TestCase {
  Properties props;

  public LevelOptionConverterTest(String name) {
    super(name);
  }

  public void setUp() {
  }

  public void tearDown() {
    props = null;
  }

  public void toLevelTest1() {
    String val = "INFO";
    Level p = OptionConverter.toLevel(val, null);
    assertEquals(p, Level.INFO);
  }

  public void toLevelTest2() {
    String val = "INFO#org.apache.log4j.xml.XLevel";
    Level p = OptionConverter.toLevel(val, null);
    assertEquals(p, Level.INFO);
  }

  public void toLevelTest3() {
    String val = "TRACE#org.apache.log4j.xml.XLevel";
    Level p = OptionConverter.toLevel(val, null);
    assertEquals(p, XLevel.TRACE);
  }

  public void toLevelTest4() {
    String val = "TR#org.apache.log4j.xml.XLevel";
    Level p = OptionConverter.toLevel(val, null);
    assertEquals(p, null);
  }

  public void toLevelTest5() {
    String val = "INFO#org.apache.log4j.xml.TOTO";
    Level p = OptionConverter.toLevel(val, null);
    assertEquals(p, null);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(new LevelOptionConverterTest("toLevelTest1"));
    suite.addTest(new LevelOptionConverterTest("toLevelTest2"));
    suite.addTest(new LevelOptionConverterTest("toLevelTest3"));
    suite.addTest(new LevelOptionConverterTest("toLevelTest4"));
    suite.addTest(new LevelOptionConverterTest("toLevelTest5"));
    return suite;
  }
}
