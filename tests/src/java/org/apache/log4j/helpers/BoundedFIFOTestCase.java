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
import org.apache.log4j.helpers.BoundedFIFO;
import org.apache.log4j.spi.LoggingEvent;


/**
   Unit test the {@link BoundedFIFO}.
   @author Ceki G&uuml;lc&uuml;
   @since 0.9.1 */
public class BoundedFIFOTestCase extends TestCase {
  static Category cat = Category.getInstance("x");
  static int MAX = 1000;
  static LoggingEvent[] e = new LoggingEvent[MAX];

  {
    for (int i = 0; i < MAX; i++) {
      e[i] = new LoggingEvent("", cat, Priority.DEBUG, "e" + i, null);
    }
  }

  public BoundedFIFOTestCase(String name) {
    super(name);
  }

  public void setUp() {
  }

  /**
     Pattern: +++++..-----..
   */
  public void test1() {
    for (int size = 1; size <= 128; size *= 2) {
      BoundedFIFO bf = new BoundedFIFO(size);

      assertEquals(bf.getMaxSize(), size);
      assertNull(bf.get());

      int i;
      int j;
      int k;

      for (i = 1; i < (2 * size); i++) {
        for (j = 0; j < i; j++) {
          //System.out.println("Putting "+e[j]);
          bf.put(e[j]);
          assertEquals(bf.length(), (j < size) ? (j + 1) : size);
        }

        int max = (size < j) ? size : j;
        j--;

        for (k = 0; k <= j; k++) {
          //System.out.println("max="+max+", j="+j+", k="+k);
          assertEquals(bf.length(), ((max - k) > 0) ? (max - k) : 0);

          Object r = bf.get();

          //System.out.println("Got "+r);
          if (k >= size) {
            assertNull(r);
          } else {
            assertEquals(r, e[k]);
          }
        }
      }

      //System.out.println("Passed size="+size);
    }
  }

  /**
     Pattern: ++++--++--++
   */
  public void test2() {
    int size = 3;
    BoundedFIFO bf = new BoundedFIFO(size);

    bf.put(e[0]);
    assertEquals(bf.get(), e[0]);
    assertNull(bf.get());

    bf.put(e[1]);
    assertEquals(bf.length(), 1);
    bf.put(e[2]);
    assertEquals(bf.length(), 2);
    bf.put(e[3]);
    assertEquals(bf.length(), 3);
    assertEquals(bf.get(), e[1]);
    assertEquals(bf.length(), 2);
    assertEquals(bf.get(), e[2]);
    assertEquals(bf.length(), 1);
    assertEquals(bf.get(), e[3]);
    assertEquals(bf.length(), 0);
    assertNull(bf.get());
    assertEquals(bf.length(), 0);
  }

  int min(int a, int b) {
    return (a < b) ? a : b;
  }

  /**
     Pattern ++++++++++++++++++++ (insert only);
   */
  public void testResize1() {
    int size = 10;

    for (int n = 1; n < (size * 2); n++) {
      for (int i = 0; i < (size * 2); i++) {
        BoundedFIFO bf = new BoundedFIFO(size);

        for (int f = 0; f < i; f++) {
          bf.put(e[f]);
        }

        bf.resize(n);

        int expectedSize = min(n, min(i, size));
        assertEquals(bf.length(), expectedSize);

        for (int c = 0; c < expectedSize; c++) {
          assertEquals(bf.get(), e[c]);
        }
      }
    }
  }

  /**
     Pattern ++...+ --...-
   */
  public void testResize2() {
    int size = 10;

    for (int n = 1; n < (size * 2); n++) {
      for (int i = 0; i < (size * 2); i++) {
        for (int d = 0; d < min(i, size); d++) {
          BoundedFIFO bf = new BoundedFIFO(size);

          for (int p = 0; p < i; p++) {
            bf.put(e[p]);
          }

          for (int g = 0; g < d; g++) {
            bf.get();
          }

          // x = the number of elems in 
          int x = bf.length();

          bf.resize(n);

          int expectedSize = min(n, x);
          assertEquals(bf.length(), expectedSize);

          for (int c = 0; c < expectedSize; c++) {
            assertEquals(bf.get(), e[c + d]);
          }

          assertNull(bf.get());
        }
      }
    }
  }

  /**
     Pattern: i inserts, d deletes, r inserts
   */
  public void testResize3() {
    int size = 10;

    for (int n = 1; n < (size * 2); n++) {
      for (int i = 0; i < size; i++) {
        for (int d = 0; d < i; d++) {
          for (int r = 0; r < d; r++) {
            BoundedFIFO bf = new BoundedFIFO(size);

            for (int p0 = 0; p0 < i; p0++)
              bf.put(e[p0]);

            for (int g = 0; g < d; g++)
              bf.get();

            for (int p1 = 0; p1 < r; p1++)
              bf.put(e[i + p1]);

            int x = bf.length();

            bf.resize(n);

            int expectedSize = min(n, x);
            assertEquals(bf.length(), expectedSize);

            for (int c = 0; c < expectedSize; c++) {
              assertEquals(bf.get(), e[c + d]);
            }

            //assertNull(bf.get());
          }
        }
      }
    }
  }

  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(new BoundedFIFOTestCase("test1"));
    suite.addTest(new BoundedFIFOTestCase("test2"));
    suite.addTest(new BoundedFIFOTestCase("testResize1"));
    suite.addTest(new BoundedFIFOTestCase("testResize2"));
    suite.addTest(new BoundedFIFOTestCase("testResize3"));

    return suite;
  }
}
