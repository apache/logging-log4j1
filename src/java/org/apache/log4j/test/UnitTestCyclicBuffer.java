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
import org.apache.log4j.helpers.CyclicBuffer;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.TestFailure;
import junit.framework.Test;

/**
   Unit test the {@link CyclicBuffer}.

   @author Ceki G&uuml;lc&uuml;</a> 


   @since 0.9.0
*/
public class UnitTestCyclicBuffer extends TestCase {

  static Category cat = Category.getInstance("x");

  static int MAX = 1000;
  
  static LoggingEvent[] e = new LoggingEvent[MAX];

  {
    for (int i = 0; i < MAX; i++) {
      e[i] =  new LoggingEvent("", cat, Priority.DEBUG, "e"+i, null);
    }
  }


  public UnitTestCyclicBuffer(String name) {
    super(name);
  }


  public
  void setUp() {

  }

  
  public
  void test0() {
    int size = 2;

    CyclicBuffer cb = new CyclicBuffer(size);    
    assertEquals(cb.getMaxSize(), size);    

    cb.add(e[0]);
    assertEquals(cb.length(), 1);    
    assertEquals(cb.get(), e[0]); assertEquals(cb.length(), 0);
    assertNull(cb.get()); assertEquals(cb.length(), 0);


    cb = new CyclicBuffer(size);    
    cb.add(e[0]);
    cb.add(e[1]);
    assertEquals(cb.length(), 2);    
    assertEquals(cb.get(), e[0]); assertEquals(cb.length(), 1);
    assertEquals(cb.get(), e[1]); assertEquals(cb.length(), 0);
    assertNull(cb.get()); assertEquals(cb.length(), 0);

    
  }
  
  /**
     Test a buffer of size 1,2,4,8,..,128
   */
  public
  void test1() {
    for(int bufSize = 1; bufSize <= 128; bufSize *=2) 
      doTest1(bufSize);
  }

  void doTest1(int size) {
    //System.out.println("Doing test with size = "+size);
    CyclicBuffer cb = new CyclicBuffer(size);

    assertEquals(cb.getMaxSize(), size);

    for(int i = -(size+10); i < (size+10); i++) {
      assertNull(cb.get(i));
    }
    
    for(int i = 0; i < MAX; i++) {
      cb.add(e[i]);
      int limit = i < size-1 ? i : size-1;

      //System.out.println("\nLimit is " + limit + ", i="+i);

      for(int j = limit; j >= 0; j--) {
	//System.out.println("i= "+i+", j="+j);
	assertEquals(cb.get(j), e[i-(limit-j)]);
      }
      assertNull(cb.get(-1));
      assertNull(cb.get(limit+1));
    }
  }

  public
  void testResize() {
    for(int isize = 1; isize <= 128; isize *=2) {      
      doTestResize(isize, isize/2+1, isize/2+1);
      doTestResize(isize, isize/2+1, isize+10);
      doTestResize(isize, isize+10, isize/2+1);
      doTestResize(isize, isize+10, isize+10);
    }
  }
  
  void doTestResize(int initialSize, int numberOfAdds, int newSize) {
    //System.out.println("initialSize = "+initialSize+", numberOfAdds="
    //	       +numberOfAdds+", newSize="+newSize);
    CyclicBuffer cb = new CyclicBuffer(initialSize);
    for(int i = 0; i < numberOfAdds; i++) {
      cb.add(e[i]);
    }    
    cb.resize(newSize);

    int offset = numberOfAdds - initialSize;
    if(offset< 0)
      offset = 0;

    int len = newSize < numberOfAdds ? newSize : numberOfAdds;
    len = len < initialSize ? len : initialSize;
    //System.out.println("Len = "+len+", offset="+offset);
    for(int j = 0; j < len; j++) {
      assertEquals(cb.get(j), e[offset+j]);
    }

  }
  

  public
  static
  Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(new UnitTestCyclicBuffer("test0"));
    suite.addTest(new UnitTestCyclicBuffer("test1"));
    suite.addTest(new UnitTestCyclicBuffer("testResize"));
    return suite;
  }
}
