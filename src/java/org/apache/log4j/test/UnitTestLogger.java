/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j.test;

import org.apache.log4j.*;
import org.apache.log4j.spi.*;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;
import java.util.Enumeration;
import java.util.ResourceBundle;
import java.util.Locale;

/**
   Used for internal unit testing the Logger class.

   @author Ceki G&uuml;lc&uuml;

*/
public class UnitTestLogger extends TestCase {

  Logger cat;
  Appender a1;
  Appender a2;

  ResourceBundle rbUS;
  ResourceBundle rbFR; 
  ResourceBundle rbCH; 

  // A short message.
  static String MSG = "M";
  

  public UnitTestLogger(String name) {
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
    //Logger.getDefaultHierarchy().clear();
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
     Test if logger a.b inherits its appender from a.
   */
  public
  void testAdditivity1() {
    Logger a = Category.getInstance("a");
    Logger ab = Category.getInstance("a.b");
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
    
    Logger a = Category.getInstance("a");
    Logger ab = Category.getInstance("a.b");
    Logger abc = Category.getInstance("a.b.c");
    Logger x   = Category.getInstance("x");

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

    Logger root = Category.getRoot();    
    Logger a = Category.getInstance("a");
    Logger ab = Category.getInstance("a.b");
    Logger abc = Category.getInstance("a.b.c");
    Logger x   = Category.getInstance("x");

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
    Logger root = Category.getRoot();    
    root.addAppender(caRoot);

    LoggerRepository h = Category.getDefaultHierarchy();
    //h.disableDebug();
    h.setThreshold(Level.INFO);
    assertEquals(caRoot.counter, 0);     

    root.debug(MSG); assertEquals(caRoot.counter, 0);  
    root.info(MSG); assertEquals(caRoot.counter, 1);  
    root.log(Level.WARN, MSG); assertEquals(caRoot.counter, 2);  
    root.warn(MSG); assertEquals(caRoot.counter, 3);  

    //h.disableInfo();
    h.setThreshold(Level.WARN);
    root.debug(MSG); assertEquals(caRoot.counter, 3);  
    root.info(MSG); assertEquals(caRoot.counter, 3);  
    root.log(Level.WARN, MSG); assertEquals(caRoot.counter, 4);  
    root.error(MSG); assertEquals(caRoot.counter, 5);  
    root.log(Level.ERROR, MSG); assertEquals(caRoot.counter, 6);  

    //h.disableAll();
    h.setThreshold(Level.OFF);
    root.debug(MSG); assertEquals(caRoot.counter, 6);  
    root.info(MSG); assertEquals(caRoot.counter, 6);  
    root.log(Level.WARN, MSG); assertEquals(caRoot.counter, 6);  
    root.error(MSG); assertEquals(caRoot.counter, 6);  
    root.log(Level.FATAL, MSG); assertEquals(caRoot.counter, 6);  
    root.log(Level.FATAL, MSG); assertEquals(caRoot.counter, 6);  

    //h.disable(Level.FATAL);
    h.setThreshold(Level.OFF);
    root.debug(MSG); assertEquals(caRoot.counter, 6);  
    root.info(MSG); assertEquals(caRoot.counter, 6);  
    root.log(Level.WARN, MSG); assertEquals(caRoot.counter, 6);  
    root.error(MSG); assertEquals(caRoot.counter, 6);
    root.log(Level.ERROR, MSG); assertEquals(caRoot.counter, 6);  
    root.log(Level.FATAL, MSG); assertEquals(caRoot.counter, 6);  
  }


  public
  void testRB1() {
    Logger root = Category.getRoot(); 
    root.setResourceBundle(rbUS);
    ResourceBundle t = root.getResourceBundle();
    assertSame(t, rbUS);

    Logger x = Category.getInstance("x");
    Logger x_y = Category.getInstance("x.y");
    Logger x_y_z = Category.getInstance("x.y.z");

    t = x.getResourceBundle();     assertSame(t, rbUS);
    t = x_y.getResourceBundle();   assertSame(t, rbUS);
    t = x_y_z.getResourceBundle(); assertSame(t, rbUS);
  }

  public
  void testRB2() {
    Logger root = Category.getRoot(); 
    root.setResourceBundle(rbUS);
    ResourceBundle t = root.getResourceBundle();
    assertSame(t, rbUS);

    Logger x = Category.getInstance("x");
    Logger x_y = Category.getInstance("x.y");
    Logger x_y_z = Category.getInstance("x.y.z");

    x_y.setResourceBundle(rbFR);
    t = x.getResourceBundle();     assertSame(t, rbUS);
    t = x_y.getResourceBundle();   assertSame(t, rbFR);
    t = x_y_z.getResourceBundle(); assertSame(t, rbFR);    
  }


  public
  void testRB3() {
    Logger root = Category.getRoot(); 
    root.setResourceBundle(rbUS);
    ResourceBundle t = root.getResourceBundle();
    assertSame(t, rbUS);

    Logger x = Category.getInstance("x");
    Logger x_y = Category.getInstance("x.y");
    Logger x_y_z = Category.getInstance("x.y.z");

    x_y.setResourceBundle(rbFR);
    x_y_z.setResourceBundle(rbCH);
    t = x.getResourceBundle();     assertSame(t, rbUS);
    t = x_y.getResourceBundle();   assertSame(t, rbFR);
    t = x_y_z.getResourceBundle(); assertSame(t, rbCH);    
  }

  public
  void testExists() {
    Logger a = Category.getInstance("a");
    Logger a_b = Category.getInstance("a.b");
    Logger a_b_c = Category.getInstance("a.b.c");
    
    Logger t;
    t = Logger.exists("xx");    assertNull(t);
    t = Logger.exists("a");     assertSame(a, t);
    t = Logger.exists("a.b");   assertSame(a_b, t);
    t = Logger.exists("a.b.c"); assertSame(a_b_c, t);
  }

  public
  void testHierarchy1() {
    Hierarchy h = new Hierarchy( new RootCategory(Level.ERROR));
    Logger a0 = h.getLogger("a");
    assertEquals("a", a0.getName());
    assertNull(a0.getLevel());
    assertSame(Level.ERROR, a0.getChainedLevel());

    Logger a1 = h.getLogger("a");
    assertSame(a0, a1);

    

    
  }

  public
  static
  Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(new UnitTestLogger("testAppender1"));
    suite.addTest(new UnitTestLogger("testAppender2"));
    suite.addTest(new UnitTestLogger("testAdditivity1"));        
    suite.addTest(new UnitTestLogger("testAdditivity2"));        
    suite.addTest(new UnitTestLogger("testAdditivity3"));        
    suite.addTest(new UnitTestLogger("testDisable1"));        
    suite.addTest(new UnitTestLogger("testRB1"));        
    suite.addTest(new UnitTestLogger("testRB2"));        
    suite.addTest(new UnitTestLogger("testRB3"));        
    suite.addTest(new UnitTestLogger("testExists"));        
    suite.addTest(new UnitTestLogger("testHierarchy1"));        
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
