package org.apache.log4j.helpers;


import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;
import java.util.Properties;

/**
 * Test variable substitution code in OptionConverter.substVars method.
 * 
 * @author Ceki G&uuml;lc&uuml;
 * 
 * @since 1.0
 */
public class OptionSubstitutionTest extends TestCase {

  Properties props;

  public OptionSubstitutionTest(String name) {
    super(name);
  }

  public void setUp() {
    props = new Properties();
    props.put("TOTO", "wonderful");
    props.put("key1", "value1");
    props.put("key2", "value2");
    System.setProperties(props);

  }

  public void tearDown() {
    props = null;
  }

  public void testVarSubst1() {
    String r;

    r = OptionConverter.substVars("hello world.", null);
    assertEquals("hello world.", r);

    r = OptionConverter.substVars("hello ${TOTO} world.", null);

    assertEquals("hello wonderful world.", r);
  }

  public void testVarSubst2() {
    String r;

    r = OptionConverter.substVars("Test2 ${key1} mid ${key2} end.", null);
    assertEquals("Test2 value1 mid value2 end.", r);
  }

  public void testVarSubst3() {
    String r;

    r = OptionConverter.substVars("Test3 ${unset} mid ${key1} end.", null);
    assertEquals("Test3  mid value1 end.", r);
  }

  public void testVarSubst4() {
    String res;
    String val = "Test4 ${incomplete ";
    try {
      res = OptionConverter.substVars(val, null);
    } catch (IllegalArgumentException e) {
      String errorMsg = e.getMessage();
      //System.out.println('['+errorMsg+']');
      assertEquals('"' + val
          + "\" has no closing brace. Opening brace at position 6.", errorMsg);
    }
  }

  /**
   * Test recursive variable substitution.
   */
  public void testRecursiveVarSubst() {
    Properties props = new Properties();
    props.put("p1", "x1");
    props.put("p2", "${p1}");
    String res = OptionConverter.substVars("${p2}", props);
    System.out.println("Result is [" + res + "].");
    assertEquals("x1", res);
  }

  /**
   * In this test we check wheter the :- operator substitutes the default value
   * when the properties does not contain a value for the substitution key.
   *
   */
  public void testVarSubstWithDefault1() {
    Properties props = new Properties();
    String res = OptionConverter.substVars("HELLO ${name:-world}.", props);
    System.out.println("Result is [" + res + "].");
    assertEquals("HELLO world.", res);
  }


  /**
   * In this test we check wheter the :- operator substitutes the default value
   * when the properties does not contain a value for the substitution key.
   *
   */
  public void testVarSubstWithDefault2() {
    Properties props = new Properties();
    props.put("name", "John");
    String res = OptionConverter.substVars("HELLO ${name:-world}.", props);
    System.out.println("Result is [" + res + "].");
    assertEquals("HELLO John.", res);
  }
  
  /**
   * Test whether recursive substitution works recursively
   *
   */
  public void testRecursiveVarSubstWithDefault() {
    Properties props = new Properties();
    props.put("p1", "${name:-world}");
    String res = OptionConverter.substVars("HELLO ${p1}.", props);
    //System.out.println("Result is [" + res + "].");
    assertEquals("HELLO world.", res);

    props.put("name", "John");
    res = OptionConverter.substVars("HELLO ${p1}.", props);
    //System.out.println("Result is [" + res + "].");
    assertEquals("HELLO John.", res);
  }
  
  /**
   * Tests FileAppender.stripDuplicateBackslashes.
   * @remarks This should be moved to a FileAppenderTestCase
   *  
   * @since 1.3
   */
  public void testStripDuplicateBackslashes() {
     assertEquals("\\foo\\bar\\foo", OptionConverter.stripDuplicateBackslashes("\\foo\\bar\\foo"));
     assertEquals("\\foo\\bar\\foo\\", OptionConverter.stripDuplicateBackslashes("\\\\foo\\\\bar\\\\foo\\\\"));
     assertEquals("\\foo\\bar\\foo\\", OptionConverter.stripDuplicateBackslashes("\\foo\\bar\\foo\\"));
     //
     //   UNC's should either start with two backslashes and contain additional singles
     //       or four back slashes and addition doubles
     assertEquals("\\\\foo\\bar\\foo", OptionConverter.stripDuplicateBackslashes("\\\\\\\\foo\\\\bar\\\\foo"));
     assertEquals("\\\\foo\\bar\\foo", OptionConverter.stripDuplicateBackslashes("\\\\foo\\bar\\foo"));
	 //
	 //   it it starts with doubles but has no other path component
	 //      then it is a file path
     assertEquals("\\foo.log", OptionConverter.stripDuplicateBackslashes("\\\\foo.log"));
	 //
	 //   it it starts with quads but has no other path component
	 //      then it is a UNC
     assertEquals("\\\\foo.log", OptionConverter.stripDuplicateBackslashes("\\\\\\\\foo.log"));
  }  
  
  
  public static Test Xsuite() {
    TestSuite suite = new TestSuite();
//    suite.addTest(new OptionSubstitutionTest("testVarSubst1"));
//    suite.addTest(new OptionSubstitutionTest("testVarSubst2"));
//    suite.addTest(new OptionSubstitutionTest("testVarSubst3"));
//    suite.addTest(new OptionSubstitutionTest("testVarSubst4"));
//    suite.addTest(new OptionSubstitutionTest("testRecursiveVarSubst"));
    suite.addTest(new OptionSubstitutionTest("testVarSubstWithDefault1"));
    suite.addTest(new OptionSubstitutionTest("testVarSubstWithDefault2"));
    return suite;
  }

}