
// Log4j uses the JUnit framework for internal unit testing. JUnit
// is available from "http://www.junit.org".

package org.apache.log4j.test;

import org.apache.log4j.helpers.OptionConverter;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;
import java.util.Properties;

/**
   Test variable substitution code.   
   @author Ceki G&uuml;lc&uuml;
   
   @since 1.0
*/
public class UnitTestVarSubst extends TestCase {

  Properties props;
  
  public UnitTestVarSubst(String name) {
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
  static
  Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(new UnitTestVarSubst("varSubstTest1"));
    suite.addTest(new UnitTestVarSubst("varSubstTest2"));
    suite.addTest(new UnitTestVarSubst("varSubstTest3"));
    suite.addTest(new UnitTestVarSubst("varSubstTest4"));
    return suite;
  }

}
