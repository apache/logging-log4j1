/*
 * ============================================================================
 *                   The Apache Software License, Version 1.1
 * ============================================================================
 *
 *    Copyright (C) 1999 The Apache Software Foundation. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modifica-
 * tion, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of  source code must  retain the above copyright  notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any, must
 *    include  the following  acknowledgment:  "This product includes  software
 *    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
 *    Alternately, this  acknowledgment may  appear in the software itself,  if
 *    and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "log4j" and  "Apache Software Foundation"  must not be used to
 *    endorse  or promote  products derived  from this  software without  prior
 *    written permission. For written permission, please contact
 *    apache@apache.org.
 *
 * 5. Products  derived from this software may not  be called "Apache", nor may
 *    "Apache" appear  in their name,  without prior written permission  of the
 *    Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 * APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 * DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 * ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 * (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * This software  consists of voluntary contributions made  by many individuals
 * on  behalf of the Apache Software  Foundation.  For more  information on the
 * Apache Software Foundation, please see <http://www.apache.org/>.
 *
 */


//
// Log4j uses the JUnit framework for internal unit testing. JUnit
// available from
//
//     http://www.junit.org
package org.apache.log4j.helpers;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.Category;
import org.apache.log4j.Priority;
import org.apache.log4j.helpers.CyclicBuffer;
import org.apache.log4j.spi.LoggingEvent;


/**
   Unit test the {@link CyclicBuffer}.

   @author Ceki G&uuml;lc&uuml;

*/
public class CyclicBufferTestCase extends TestCase {
  static Category cat = Category.getInstance("x");
  static int MAX = 1000;
  static LoggingEvent[] e = new LoggingEvent[MAX];

  {
    for (int i = 0; i < MAX; i++) {
      e[i] = new LoggingEvent("", cat, Priority.DEBUG, "e" + i, null);
    }
  }

  public CyclicBufferTestCase(String name) {
    super(name);
  }

  public void setUp() {
  }

  public void test0() {
    int size = 2;

    CyclicBuffer cb = new CyclicBuffer(size);
    assertEquals(cb.getMaxSize(), size);

    cb.add(e[0]);
    assertEquals(cb.length(), 1);
    assertEquals(cb.get(), e[0]);
    assertEquals(cb.length(), 0);
    assertNull(cb.get());
    assertEquals(cb.length(), 0);

    cb = new CyclicBuffer(size);
    cb.add(e[0]);
    cb.add(e[1]);
    assertEquals(cb.length(), 2);
    assertEquals(cb.get(), e[0]);
    assertEquals(cb.length(), 1);
    assertEquals(cb.get(), e[1]);
    assertEquals(cb.length(), 0);
    assertNull(cb.get());
    assertEquals(cb.length(), 0);
  }

  /**
     Test a buffer of size 1,2,4,8,..,128
   */
  public void test1() {
    for (int bufSize = 1; bufSize <= 128; bufSize *= 2)
      doTest1(bufSize);
  }

  void doTest1(int size) {
    //System.out.println("Doing test with size = "+size);
    CyclicBuffer cb = new CyclicBuffer(size);

    assertEquals(cb.getMaxSize(), size);

    for (int i = -(size + 10); i < (size + 10); i++) {
      assertNull(cb.get(i));
    }

    for (int i = 0; i < MAX; i++) {
      cb.add(e[i]);

      int limit = (i < (size - 1)) ? i : (size - 1);

      //System.out.println("\nLimit is " + limit + ", i="+i);
      for (int j = limit; j >= 0; j--) {
        //System.out.println("i= "+i+", j="+j);
        assertEquals(cb.get(j), e[i - (limit - j)]);
      }

      assertNull(cb.get(-1));
      assertNull(cb.get(limit + 1));
    }
  }

  public void testResize() {
    for (int isize = 1; isize <= 128; isize *= 2) {
      doTestResize(isize, (isize / 2) + 1, (isize / 2) + 1);
      doTestResize(isize, (isize / 2) + 1, isize + 10);
      doTestResize(isize, isize + 10, (isize / 2) + 1);
      doTestResize(isize, isize + 10, isize + 10);
    }
  }

  void doTestResize(int initialSize, int numberOfAdds, int newSize) {
    //System.out.println("initialSize = "+initialSize+", numberOfAdds="
    //	       +numberOfAdds+", newSize="+newSize);
    CyclicBuffer cb = new CyclicBuffer(initialSize);

    for (int i = 0; i < numberOfAdds; i++) {
      cb.add(e[i]);
    }

    cb.resize(newSize);

    int offset = numberOfAdds - initialSize;

    if (offset < 0) {
      offset = 0;
    }

    int len = (newSize < numberOfAdds) ? newSize : numberOfAdds;
    len = (len < initialSize) ? len : initialSize;

    //System.out.println("Len = "+len+", offset="+offset);
    for (int j = 0; j < len; j++) {
      assertEquals(cb.get(j), e[offset + j]);
    }
  }

  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(new CyclicBufferTestCase("test0"));
    suite.addTest(new CyclicBufferTestCase("test1"));
    suite.addTest(new CyclicBufferTestCase("testResize"));

    return suite;
  }
}
