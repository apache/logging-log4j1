/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.APL file.  */

//
// Log4j uses the JUnit framework for internal unit testing. JUnit
// available from
//
//     http://www.junit.org


package org.apache.log4j.test;

import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.Category;
import org.apache.log4j.Priority;

import org.apache.log4j.helpers.BoundedFIFO;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.TestFailure;
import junit.framework.Test;




/**
   Unit test the {@link BoundedFIFO}.
   @author Ceki G&uuml;lc&uuml;
   @since 0.9.1 */
public class UnitTestBoundedFIFO extends TestCase {
  static Category cat = Category.getInstance("x");

  static int MAX = 1000;  

  static LoggingEvent[] e = new LoggingEvent[MAX];

  {
    for (int i = 0; i < MAX; i++) {
      e[i] =  new LoggingEvent("", cat, Priority.DEBUG, "e"+i, null);
    }
  }


  public UnitTestBoundedFIFO(String name) {
    super(name);
  }


  public
  void setUp() {

  }


  /**
     Pattern: +++++..-----..
   */
  public
  void test1() {
    for(int size = 1; size <= 128; size *=2) {
      BoundedFIFO bf = new BoundedFIFO(size);
    
      assertEquals(bf.getMaxSize(), size);
      assertNull(bf.get());
      
      int i;
      int j;
      int k;

      for(i = 1; i < 2*size; i++) {      
	for(j = 0; j < i; j++) {
	  //System.out.println("Putting "+e[j]);
	  bf.put(e[j]); assertEquals(bf.length(), j < size ?  j+1 : size);
	}
	int max = size < j ? size : j;
	j--;
	for(k = 0; k <= j; k++) {	  
	  //System.out.println("max="+max+", j="+j+", k="+k);
	  assertEquals(bf.length(), max - k > 0 ? max - k : 0); 
	  Object r = bf.get();
	  //System.out.println("Got "+r);
	  if(k >= size) 
	    assertNull(r);
	  else 
	    assertEquals(r, e[k]);
	}
      }
      System.out.println("Passed size="+size);
    }
  }


  /**
     Pattern: ++++--++--++
   */
  public
  void test2() {
    int size = 3;
    BoundedFIFO bf = new BoundedFIFO(size);
    
    bf.put(e[0]);	
    assertEquals(bf.get(), e[0]);
    assertNull(bf.get());

    bf.put(e[1]); assertEquals(bf.length(), 1);
    bf.put(e[2]); assertEquals(bf.length(), 2);
    bf.put(e[3]); assertEquals(bf.length(), 3);
    assertEquals(bf.get(), e[1]); assertEquals(bf.length(), 2);
    assertEquals(bf.get(), e[2]); assertEquals(bf.length(), 1);
    assertEquals(bf.get(), e[3]); assertEquals(bf.length(), 0);
    assertNull(bf.get()); assertEquals(bf.length(), 0);
  }
  

  public
  static
  Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(new UnitTestBoundedFIFO("test1"));
    suite.addTest(new UnitTestBoundedFIFO("test2"));
    return suite;
  }
}
