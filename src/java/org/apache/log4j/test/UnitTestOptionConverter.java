
// Log4j uses the JUnit framework for internal unit testing. JUnit
// is available from "http://www.junit.org".

package org.apache.log4j.test;

import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.Priority;
import org.apache.log4j.xml.examples.XPriority;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;
import java.util.Properties;

/**
   Test variable substitution code.   
   @author Ceki G&uuml;lc&uuml;
   
   @since 1.0
*/
public class UnitTestOptionConverter extends TestCase {

  Properties props;
  
  public UnitTestOptionConverter(String name) {
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
      assertEquals('"'+val+ "\" has no closing brace. Opening brace at position 6.", errorMsg);
    }
  }


  public
  void toPriorityTest1() {
    String val = "INFO";
    Priority p = OptionConverter.toPriority(val, null);
    assertEquals(p, Priority.INFO);
  }

  public
  void toPriorityTest2() {
    String val = "INFO#org.apache.log4j.xml.examples.XPriority";
    Priority p = OptionConverter.toPriority(val, null);
    assertEquals(p, Priority.INFO);
  }

  public
  void toPriorityTest3() {
    String val = "TRACE#org.apache.log4j.xml.examples.XPriority";
    Priority p = OptionConverter.toPriority(val, null);    
    assertEquals(p, XPriority.TRACE);
  }

  public
  void toPriorityTest4() {
    String val = "TR#org.apache.log4j.xml.examples.XPriority";
    Priority p = OptionConverter.toPriority(val, null);    
    assertEquals(p, null);
  }


  public
  static
  Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(new UnitTestOptionConverter("varSubstTest1"));
    suite.addTest(new UnitTestOptionConverter("varSubstTest2"));
    suite.addTest(new UnitTestOptionConverter("varSubstTest3"));
    suite.addTest(new UnitTestOptionConverter("varSubstTest4"));

    suite.addTest(new UnitTestOptionConverter("toPriorityTest1"));
    suite.addTest(new UnitTestOptionConverter("toPriorityTest2"));
    suite.addTest(new UnitTestOptionConverter("toPriorityTest3"));
    suite.addTest(new UnitTestOptionConverter("toPriorityTest4"));
    return suite;
  }

}
