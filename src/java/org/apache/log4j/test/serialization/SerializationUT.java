/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;
import java.util.Hashtable;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;
import java.lang.reflect.Method;

/**
   The SerializationUnitTest checks whether the {@link LoggingEvent}
   objects are compatible across different log4j versions.

   @author Ceki G&uuml;lc&uuml;

*/
public class SerializationUT extends TestCase {

  // String categoryName
  // String ndc
  // boolan ndcLookupRequired
  // String renderedMessage
  // String threadName
  // long timeStamp


  static URLClassLoader classLoader113;
  static Class class113; 
  static Object o113;
  static Method serMethod113;
  static Method deserMethod113;

  static URLClassLoader classLoader12a7;
  static Class class12a7;
  static Object o12a7;
  static Method serMethod12a7;
  static Method deserMethod12a7;
  
  public SerializationUT(String name) {
    super(name);
  }

  public
  void setUp() throws Exception {
    
    try {
      URL urlLocal = new URL("file:T/");

      URL url113 = new URL("file:T/log4j-1.1.3.jar");
   
      classLoader113 = new URLClassLoader(new URL[] {urlLocal, url113}); 
      class113  = classLoader113.loadClass("T113");
      o113 = class113.newInstance();
      serMethod113 = class113.getMethod("serialize", 
					new Class[] {java.util.Hashtable.class});
      
      deserMethod113 = class113.getMethod("deserialize", 
					  new Class[] {byte[].class}); 
      

      URL url2a7 = new URL("file:T/log4j-1.2alpha7.jar");
   
      classLoader12a7 = new URLClassLoader(new URL[] {urlLocal, url2a7});
      class12a7 = classLoader12a7.loadClass("T12");
      o12a7 = class12a7.newInstance();
      serMethod12a7 = class12a7.getMethod("serialize", 
					 new Class[] {java.util.Hashtable.class});
      deserMethod12a7 = class12a7.getMethod("deserialize", 
					   new Class[] {byte[].class});     
    } catch(Exception e) {
      e.printStackTrace();
      throw e;
    }
    
    System.out.println("---Exiting setup");
  }

  public
  void tearDown() {
  }


  /**
     Test writing in 1.1.3 and reading from 1.1.3. Here we are testing
     the test.  */
  public 
  void test1() throws Exception {
    
    Hashtable inHt = new Hashtable();
    Hashtable witness = new Hashtable();

    inHt.put("categoryName", "a.b.c");
    inHt.put("priorityStr", "DEBUG");
    inHt.put("message", "hello");
   
    witness.put("categoryName", "a.b.c");
    witness.put("priorityStr", "DEBUG");
    witness.put("renderedMessage", "hello");

    byte[] buf = (byte[]) serMethod113.invoke(o113, new Object[] {inHt});

    Hashtable outHt = (Hashtable) deserMethod113.invoke(o113, new Object[] {buf});

    System.out.println("witness"+witness);
    System.out.println("outHt  "+outHt);

    assertEquals(witness, outHt);
  }

  /**
     Test writing 1.2 and reading from 1.2. Here we are testing the test.
  */
  public 
  void test2() throws Exception {
    
    Hashtable inHt = new Hashtable();
    Hashtable witness = new Hashtable();

    inHt.put("categoryName", "a.b.c");
    inHt.put("priorityStr", "DEBUG");
    inHt.put("message", "hello");
   
    witness.put("categoryName", "a.b.c");
    witness.put("priorityStr", "DEBUG");
    witness.put("renderedMessage", "hello");

    byte[] buf = (byte[]) serMethod12a7.invoke(o12a7, new Object[] {inHt});

    Hashtable outHt = (Hashtable) deserMethod12a7.invoke(o12a7, new Object[] {buf});
    assertEquals(witness, outHt);
  }

  /**
     Test writing 1.1.3 and reading from 1.2. 
  */
  public 
  void test3() throws Exception {
    
    Hashtable inHt = new Hashtable();
    Hashtable witness = new Hashtable();

    inHt.put("categoryName", "a.b.c");
    inHt.put("priorityStr", "DEBUG");
    inHt.put("message", "hello");
   
    witness.put("categoryName", "a.b.c");
    witness.put("priorityStr", "DEBUG");
    witness.put("renderedMessage", "hello");
    byte[] buf = (byte[]) serMethod113.invoke(o113, new Object[] {inHt});
    Hashtable outHt = (Hashtable) deserMethod12a7.invoke(o12a7, new Object[] {buf});
    assertEquals(witness, outHt);
  }

  /**
     Test writing 1.2 and reading from 1.1.3.
  */
  public 
  void test4() throws Exception {
    
    Hashtable inHt = new Hashtable();
    Hashtable witness = new Hashtable();

    inHt.put("categoryName", "a.b.c");
    inHt.put("priorityStr", "DEBUG");
    inHt.put("message", "hello");
    Exception e = new ComparableException("test4");
    inHt.put("throwable", e);

    witness.put("categoryName", "a.b.c");
    witness.put("priorityStr", "DEBUG");
    witness.put("renderedMessage", "hello");
    witness.put("throwable", e);

    byte[] buf = (byte[]) serMethod12a7.invoke(o12a7, new Object[] {inHt});

    Hashtable outHt = (Hashtable) deserMethod113.invoke(o113, new Object[] {buf});

    //System.out.println("witness"+witness);
    //System.out.println("outHt  "+outHt);

    assertEquals(witness, outHt);
  }

 
  public
  static
  Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(new SerializationUT("test1"));
    suite.addTest(new SerializationUT("test2"));
    suite.addTest(new SerializationUT("test3"));
    suite.addTest(new SerializationUT("test4"));
    return suite;
  }   
}

class ComparableException extends Exception {

  public ComparableException(String msg) {
    super(msg);
  }

  public boolean equals(Object o) {
    System.out.println("ComparableException.equals called.");
    if(!(o instanceof ComparableException)) 
      return false;

    ComparableException r = (ComparableException) o;
    
    if(r.getMessage() == null) {
      if(getMessage() != null)
	return false;
    } else if(!r.getMessage().equals(getMessage())) {
      return false;
    }
    return true;
  }
}

