/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Log4j uses the JUnit framework for internal unit testing. JUnit
// is available from "http://www.junit.org".

package org.apache.log4j.helpers;

import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.Level;
import org.apache.log4j.xml.XLevel;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;
import java.util.Properties;

/**
   Test variable substitution code.   
   @author Ceki G&uuml;lc&uuml;
   
   @since 1.0
*/
public class OptionConverterTestCase extends TestCase {

  Properties props;
  
  public OptionConverterTestCase(String name) {
    super(name);
  }

  public
  void setUp() {
    props = new Properties();
    props.put("TOTO", "wonderful");
    props.put("key1", "value1");
    props.put("key2", "value2");
    System.setProperties(props);


  }  
  
  public
  void tearDown() {
    props = null;
  }

  public
  void varSubstTest1() {
    String r;

    r = OptionConverter.substVars("hello world.", null);
    assertEquals("hello world.", r);
    
    r = OptionConverter.substVars("hello ${TOTO} world.", null);
    
    assertEquals("hello wonderful world.", r);
  }


  public
  void varSubstTest2() {
    String r;

    r = OptionConverter.substVars("Test2 ${key1} mid ${key2} end.", null);
    assertEquals("Test2 value1 mid value2 end.", r);
  }

  public
  void varSubstTest3() {
    String r;

    r = OptionConverter.substVars(
				     "Test3 ${unset} mid ${key1} end.", null);
    assertEquals("Test3  mid value1 end.", r);
  }

  public
  void varSubstTest4() {
    String res;
    String val = "Test4 ${incomplete ";
    try {
      res = OptionConverter.substVars(val, null);
    }
    catch(IllegalArgumentException e) {
      String errorMsg = e.getMessage();
      //System.out.println('['+errorMsg+']');
      assertEquals('"'+val
		   + "\" has no closing brace. Opening brace at position 6.", 
		   errorMsg);
    }
  }

  public
  void varSubstTest5() {
    Properties props = new Properties();
    props.put("p1", "x1");
    props.put("p2", "${p1}");
    String res = OptionConverter.substVars("${p2}", props);
    System.out.println("Result is ["+res+"].");
    assertEquals(res, "x1");
  }

  public
  void toLevelTest1() {
    String val = "INFO";
    Level p = OptionConverter.toLevel(val, null);
    assertEquals(p, Level.INFO);
  }

  public
  void toLevelTest2() {
    String val = "INFO#org.apache.log4j.xml.XLevel";
    Level p = OptionConverter.toLevel(val, null);
    assertEquals(p, Level.INFO);
  }

  public
  void toLevelTest3() {
    String val = "TRACE#org.apache.log4j.xml.XLevel";
    Level p = OptionConverter.toLevel(val, null);    
    assertEquals(p, XLevel.TRACE);
  }

  public
  void toLevelTest4() {
    String val = "TR#org.apache.log4j.xml.XLevel";
    Level p = OptionConverter.toLevel(val, null);    
    assertEquals(p, null);
  }

  public
  void toLevelTest5() {
    String val = "INFO#org.apache.log4j.xml.TOTO";
    Level p = OptionConverter.toLevel(val, null);    
    assertEquals(p, null);
  }


  public
  static
  Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(new OptionConverterTestCase("varSubstTest5"));
    suite.addTest(new OptionConverterTestCase("varSubstTest1"));
    suite.addTest(new OptionConverterTestCase("varSubstTest2"));
    suite.addTest(new OptionConverterTestCase("varSubstTest3"));
    suite.addTest(new OptionConverterTestCase("varSubstTest4"));


    suite.addTest(new OptionConverterTestCase("toLevelTest1"));
    suite.addTest(new OptionConverterTestCase("toLevelTest2"));
    suite.addTest(new OptionConverterTestCase("toLevelTest3"));
    suite.addTest(new OptionConverterTestCase("toLevelTest4"));
    suite.addTest(new OptionConverterTestCase("toLevelTest5"));
    return suite;
  }

}
