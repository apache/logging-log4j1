/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.APL file.  */

package org.apache.log4j.test;

import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.Priority;
import org.apache.log4j.FileAppender;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Category;
import org.apache.log4j.Hierarchy;
import org.apache.log4j.spi.RootCategory;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;
import java.util.Enumeration;
import java.util.ResourceBundle;
import java.util.Locale;

/**
   Used for internal unit testing the Category class.

   @author Ceki G&uuml;lc&uuml;

*/
public class UnitTestCategory extends TestCase {

  Category cat;
  Appender a1;
  Appender a2;

  ResourceBundle rbUS;
  ResourceBundle rbFR; 
  ResourceBundle rbCH; 

  // A short message.
  static String MSG = "M";
  

  public UnitTestCategory(String name) {
    super(name);
  }

  public
  void setUp() {
    rbUS = ResourceBundle.getBundle("L7D", new Locale("en", "US"));
    assertNotNull(rbUS);

    rbFR = ResourceBundle.getBundle("L7D", new Locale("fr", "FR"));
    assertNotNull("Got a null resource bundle.", rbFR);

    rbCH = ResourceBundle.getBundle("L7D", new Locale("fr", "CH"));
    assertNotNull("Got a null resource bundle.", rbCH);

  }

  public
  void tearDown() {
    // Regular users should not use the clear method lightly!
    Category.getDefaultHierarchy().clear();
    BasicConfigurator.resetConfiguration();
    a1 = null;
    a2 = null;
  }

  /**
     Add an appender and see if it can be retrieved.
  */
  public
  void testAppender1() {
    cat = Category.getInstance("test");
    a1 = new FileAppender();
    a1.setName("testAppender1");             
    cat.addAppender(a1);

    Enumeration enum = cat.getAllAppenders();
    Appender aHat = (Appender) enum.nextElement();    
    assertEquals(a1, aHat);    
  }

  /**
     Add an appender X, Y, remove X and check if Y is the only
     remaining appender.
  */
  public
  void testAppender2() {
    a1 = new FileAppender();
    a1.setName("testAppender2.1");           
    a2 = new FileAppender();
    a2.setName("testAppender2.2");           

    cat = Category.getInstance("test");
    cat.addAppender(a1);
    cat.addAppender(a2);    
    cat.removeAppender("testAppender2.1");
    Enumeration enum = cat.getAllAppenders();
    Appender aHat = (Appender) enum.nextElement();    
    assertEquals(a2, aHat);
    assert(!enum.hasMoreElements());
  }

  /**
     Test if category a.b inherits its appender from a.
   */
  public
  void testAdditivity1() {
    Category a = Category.getInstance("a");
    Category ab = Category.getInstance("a.b");
    CountingAppender ca = new CountingAppender();
    a.addAppender(ca);
    
                   assertEquals(ca.counter, 0);
    ab.debug(MSG); assertEquals(ca.counter, 1);
    ab.info(MSG);  assertEquals(ca.counter, 2);
    ab.warn(MSG);  assertEquals(ca.counter, 3);
    ab.error(MSG); assertEquals(ca.counter, 4);    
    

  }

  /**
     Test multiple additivity.

   */
  public
  void testAdditivity2() {
    
    Category a = Category.getInstance("a");
    Category ab = Category.getInstance("a.b");
    Category abc = Category.getInstance("a.b.c");
    Category x   = Category.getInstance("x");

    CountingAppender ca1 = new CountingAppender();
    CountingAppender ca2 = new CountingAppender();

    a.addAppender(ca1);
    abc.addAppender(ca2);

    assertEquals(ca1.counter, 0); 
    assertEquals(ca2.counter, 0);        
    
    ab.debug(MSG);  
    assertEquals(ca1.counter, 1); 
    assertEquals(ca2.counter, 0);        

    abc.debug(MSG);
    assertEquals(ca1.counter, 2); 
    assertEquals(ca2.counter, 1);        

    x.debug(MSG);
    assertEquals(ca1.counter, 2); 
    assertEquals(ca2.counter, 1);    
  }

  /**
     Test additivity flag.

   */
  public
  void testAdditivity3() {

    Category root = Category.getRoot();    
    Category a = Category.getInstance("a");
    Category ab = Category.getInstance("a.b");
    Category abc = Category.getInstance("a.b.c");
    Category x   = Category.getInstance("x");

    CountingAppender caRoot = new CountingAppender();
    CountingAppender caA = new CountingAppender();
    CountingAppender caABC = new CountingAppender();

    root.addAppender(caRoot);
    a.addAppender(caA);
    abc.addAppender(caABC);

    assertEquals(caRoot.counter, 0); 
    assertEquals(caA.counter, 0); 
    assertEquals(caABC.counter, 0);        
    
    ab.setAdditivity(false);


    a.debug(MSG);  
    assertEquals(caRoot.counter, 1); 
    assertEquals(caA.counter, 1); 
    assertEquals(caABC.counter, 0);        

    ab.debug(MSG);  
    assertEquals(caRoot.counter, 1); 
    assertEquals(caA.counter, 1); 
    assertEquals(caABC.counter, 0);        

    abc.debug(MSG);  
    assertEquals(caRoot.counter, 1); 
    assertEquals(caA.counter, 1); 
    assertEquals(caABC.counter, 1);        
    
  }


  public
  void testDisable1() {
    CountingAppender caRoot = new CountingAppender();
    Category root = Category.getRoot();    
    root.addAppender(caRoot);

    BasicConfigurator.disableDebug();
    assertEquals(caRoot.counter, 0);     

    root.debug(MSG); assertEquals(caRoot.counter, 0);  
    root.info(MSG); assertEquals(caRoot.counter, 1);  
    root.log(Priority.WARN, MSG); assertEquals(caRoot.counter, 2);  
    root.warn(MSG); assertEquals(caRoot.counter, 3);  

    BasicConfigurator.disableInfo();
    root.debug(MSG); assertEquals(caRoot.counter, 3);  
    root.info(MSG); assertEquals(caRoot.counter, 3);  
    root.log(Priority.WARN, MSG); assertEquals(caRoot.counter, 4);  
    root.error(MSG); assertEquals(caRoot.counter, 5);  
    root.log(Priority.ERROR, MSG); assertEquals(caRoot.counter, 6);  

    BasicConfigurator.disableAll();
    root.debug(MSG); assertEquals(caRoot.counter, 6);  
    root.info(MSG); assertEquals(caRoot.counter, 6);  
    root.log(Priority.WARN, MSG); assertEquals(caRoot.counter, 6);  
    root.error(MSG); assertEquals(caRoot.counter, 6);  
    root.log(Priority.FATAL, MSG); assertEquals(caRoot.counter, 6);  
    root.log(Priority.FATAL, MSG); assertEquals(caRoot.counter, 6);  

    BasicConfigurator.disable(Priority.FATAL);
    root.debug(MSG); assertEquals(caRoot.counter, 6);  
    root.info(MSG); assertEquals(caRoot.counter, 6);  
    root.log(Priority.WARN, MSG); assertEquals(caRoot.counter, 6);  
    root.error(MSG); assertEquals(caRoot.counter, 6);
    root.log(Priority.ERROR, MSG); assertEquals(caRoot.counter, 6);  
    root.log(Priority.FATAL, MSG); assertEquals(caRoot.counter, 6);  
  }


  public
  void testRB1() {
    Category root = Category.getRoot(); 
    root.setResourceBundle(rbUS);
    ResourceBundle t = root.getResourceBundle();
    assertSame(t, rbUS);

    Category x = Category.getInstance("x");
    Category x_y = Category.getInstance("x.y");
    Category x_y_z = Category.getInstance("x.y.z");

    t = x.getResourceBundle();     assertSame(t, rbUS);
    t = x_y.getResourceBundle();   assertSame(t, rbUS);
    t = x_y_z.getResourceBundle(); assertSame(t, rbUS);
  }

  public
  void testRB2() {
    Category root = Category.getRoot(); 
    root.setResourceBundle(rbUS);
    ResourceBundle t = root.getResourceBundle();
    assertSame(t, rbUS);

    Category x = Category.getInstance("x");
    Category x_y = Category.getInstance("x.y");
    Category x_y_z = Category.getInstance("x.y.z");

    x_y.setResourceBundle(rbFR);
    t = x.getResourceBundle();     assertSame(t, rbUS);
    t = x_y.getResourceBundle();   assertSame(t, rbFR);
    t = x_y_z.getResourceBundle(); assertSame(t, rbFR);    
  }


  public
  void testRB3() {
    Category root = Category.getRoot(); 
    root.setResourceBundle(rbUS);
    ResourceBundle t = root.getResourceBundle();
    assertSame(t, rbUS);

    Category x = Category.getInstance("x");
    Category x_y = Category.getInstance("x.y");
    Category x_y_z = Category.getInstance("x.y.z");

    x_y.setResourceBundle(rbFR);
    x_y_z.setResourceBundle(rbCH);
    t = x.getResourceBundle();     assertSame(t, rbUS);
    t = x_y.getResourceBundle();   assertSame(t, rbFR);
    t = x_y_z.getResourceBundle(); assertSame(t, rbCH);    
  }

  public
  void testExists() {
    Category a = Category.getInstance("a");
    Category a_b = Category.getInstance("a.b");
    Category a_b_c = Category.getInstance("a.b.c");
    
    Category t;
    t = Category.exists("xx");    assertNull(t);
    t = Category.exists("a");     assertSame(a, t);
    t = Category.exists("a.b");   assertSame(a_b, t);
    t = Category.exists("a.b.c"); assertSame(a_b_c, t);
  }

  public
  void testHierarchy1() {
    Hierarchy h = new Hierarchy( new RootCategory(Priority.ERROR));
    Category a0 = h.getInstance("a");
    assertEquals("a", a0.getName());
    assertNull(a0.getPriority());
    assertSame(Priority.ERROR, a0.getChainedPriority());

    Category a1 = h.getInstance("a");
    assertSame(a0, a1);

    

    
  }

  public
  static
  Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(new UnitTestCategory("testAppender1"));
    suite.addTest(new UnitTestCategory("testAppender2"));
    suite.addTest(new UnitTestCategory("testAdditivity1"));        
    suite.addTest(new UnitTestCategory("testAdditivity2"));        
    suite.addTest(new UnitTestCategory("testAdditivity3"));        
    suite.addTest(new UnitTestCategory("testDisable1"));        
    suite.addTest(new UnitTestCategory("testRB1"));        
    suite.addTest(new UnitTestCategory("testRB2"));        
    suite.addTest(new UnitTestCategory("testRB3"));        
    suite.addTest(new UnitTestCategory("testExists"));        
    suite.addTest(new UnitTestCategory("testHierarchy1"));        
    return suite;
  }


  static private class CountingAppender extends AppenderSkeleton {

    int counter;

    CountingAppender() {
      counter = 0;
    }
    public void close() {
    }

    public
    void append(LoggingEvent event) {
      counter++;
    }
    
    public 
    boolean requiresLayout() {
      return true;
    }
  }
}
