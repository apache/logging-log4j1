
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
public class LevelOptionConverterTest extends TestCase {

  Properties props;
  
  public LevelOptionConverterTest(String name) {
    super(name);
  }

  public
  void setUp() {
  }  
  
  public
  void tearDown() {
    props = null;
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
    suite.addTest(new LevelOptionConverterTest("toLevelTest1"));
    suite.addTest(new LevelOptionConverterTest("toLevelTest2"));
    suite.addTest(new LevelOptionConverterTest("toLevelTest3"));
    suite.addTest(new LevelOptionConverterTest("toLevelTest4"));
    suite.addTest(new LevelOptionConverterTest("toLevelTest5"));
    return suite;
  }

}
