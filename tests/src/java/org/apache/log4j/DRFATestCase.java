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

package org.apache.log4j;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.Calendar;
import java.util.TimeZone;


/**
   Exhaustive test of the DailyRollingFileAppender compute algorithm.

   @author Ceki G&uuml;lc&uuml;
 */
public class DRFATestCase extends TestCase {
  public DRFATestCase(String name) {
    super(name);
  }

  public void testComputeCheckPeriod() {
    DailyRollingFileAppender drfa = new DailyRollingFileAppender();
    drfa.setName("testComputeCheckPeriod");
    drfa.setDatePattern("yyyy-MM-dd.'log'");
    drfa.activateOptions();

    int x = drfa.computeCheckPeriod();
    int y = DailyRollingFileAppender.TOP_OF_DAY;
    assertEquals(
      drfa.computeCheckPeriod(), DailyRollingFileAppender.TOP_OF_DAY);

    drfa.setDatePattern("yyyy-MM-dd mm.'log'");
    assertEquals(
      drfa.computeCheckPeriod(), DailyRollingFileAppender.TOP_OF_MINUTE);

    drfa.setDatePattern("yyyy-MM-dd a.'log'");
    assertEquals(drfa.computeCheckPeriod(), DailyRollingFileAppender.HALF_DAY);

    drfa.setDatePattern("yyyy-MM-dd HH.'log'");
    assertEquals(
      drfa.computeCheckPeriod(), DailyRollingFileAppender.TOP_OF_HOUR);

    drfa.setDatePattern("yyyy-MM.'log'");
    assertEquals(
      drfa.computeCheckPeriod(), DailyRollingFileAppender.TOP_OF_MONTH);

    drfa.setDatePattern("'log'HH'log'");
    assertEquals(
      drfa.computeCheckPeriod(), DailyRollingFileAppender.TOP_OF_HOUR);
  }

  public void testRC1() {
    RollingCalendar rc = new RollingCalendar();
    rc.setType(DailyRollingFileAppender.TOP_OF_DAY);

    Calendar c = Calendar.getInstance();

    // jan, mar, may, july, aug, oct, dec have 31 days
    int[] M31 = { 0, 2, 4, 6, 7, 9, 11 };

    for (int i = 0; i < M31.length; i++) {
      for (int d = 1; d <= 31; d++) {
        for (int h = 0; h < 23; h++) {
          c.clear();
          c.set(Calendar.YEAR, 20);
          c.set(Calendar.MONTH, Calendar.JANUARY + M31[i]);
          c.set(Calendar.DAY_OF_MONTH, d);
          c.set(Calendar.HOUR_OF_DAY, h);
          c.set(Calendar.MINUTE, 10);
          c.set(Calendar.SECOND, 10);
          c.set(Calendar.MILLISECOND, 88);

          c.setTime(rc.getNextCheckDate(c.getTime()));

          if (d == 31) {
            assertEquals(
              c.get(Calendar.MONTH), (Calendar.JANUARY + M31[i] + 1) % 12);
            assertEquals(c.get(Calendar.DAY_OF_MONTH), 1);
          } else {
            assertEquals(c.get(Calendar.MONTH), Calendar.JANUARY + M31[i]);
            assertEquals(c.get(Calendar.DAY_OF_MONTH), d + 1);
          }

          assertEquals(c.get(Calendar.HOUR_OF_DAY), 0);
          assertEquals(c.get(Calendar.MINUTE), 0);
          assertEquals(c.get(Calendar.SECOND), 0);
          assertEquals(c.get(Calendar.MILLISECOND), 0);
        }
      }
    }
  }

  public void testRC2() {
    RollingCalendar rc = new RollingCalendar();

    rc.setType(DailyRollingFileAppender.TOP_OF_HOUR);

    Calendar c = Calendar.getInstance();
    TimeZone tz = c.getTimeZone();

    // jan, mar, may, july, aug, oct, dec have 31 days
    int[] M31 = { 0, 2, 4, 6, 7, 9, 11 };

    for (int i = 0; i < M31.length; i++) {
      System.out.println("Month = " + (M31[i] + 1));

      for (int d = 1; d <= 31; d++) {
        for (int h = 0; h < 23; h++) {
          for (int m = 0; m <= 59; m++) {
            c.clear();
            c.set(Calendar.YEAR, 20);
            c.set(Calendar.MONTH, Calendar.JANUARY + M31[i]);
            c.set(Calendar.DAY_OF_MONTH, d);
            c.set(Calendar.HOUR_OF_DAY, h);
            c.set(Calendar.MINUTE, m);
            c.set(Calendar.SECOND, 12);
            c.set(Calendar.MILLISECOND, 88);

            boolean dltState0 = c.getTimeZone().inDaylightTime(c.getTime());
            c.setTime(rc.getNextCheckDate(c.getTime()));

            boolean dltState1 = c.getTimeZone().inDaylightTime(c.getTime());

            assertEquals(c.get(Calendar.MILLISECOND), 0);
            assertEquals(c.get(Calendar.SECOND), 0);
            assertEquals(c.get(Calendar.MINUTE), 0);

            if (dltState0 == dltState1) {
              assertEquals(c.get(Calendar.HOUR_OF_DAY), (h + 1) % 24);
            } else {
              // returning to standard time
              if (dltState0) {
                assertEquals(c.get(Calendar.HOUR_OF_DAY), h);
              } else { // switching to day light saving time
              }
            }

            if (h == 23) {
              assertEquals(c.get(Calendar.DAY_OF_MONTH), (d + 1) % 32);

              if (d == 31) {
                assertEquals(
                  c.get(Calendar.MONTH), (Calendar.JANUARY + M31[i] + 1) % 12);
              } else {
                assertEquals(c.get(Calendar.MONTH), Calendar.JANUARY + M31[i]);
              }
            } else {
              assertEquals(c.get(Calendar.DAY_OF_MONTH), d);
              assertEquals(c.get(Calendar.MONTH), Calendar.JANUARY + M31[i]);
            }
          }
        }
      }
    }
  }

  public void testRC3() {
    RollingCalendar rc = new RollingCalendar();

    rc.setType(DailyRollingFileAppender.TOP_OF_MINUTE);

    int[] S = { 0, 1, 5, 10, 21, 30, 59 };
    int[] M = { 0, 1, 5, 10, 21, 30, 59 };
    Calendar c = Calendar.getInstance();

    // jan, mar, may, july, aug, oct, dec have 31 days
    int[] M31 = { 2, 9, 0, 4, 6, 7, 11 };

    for (int i = 0; i < M31.length; i++) {
      System.out.println("Month = " + (M31[i] + 1));

      for (int d = 1; d <= 31; d++) {
        for (int h = 0; h < 23; h++) {
          for (int m = 0; m < M.length; m++) {
            for (int s = 0; s < S.length; s++) {
              c.clear();
              c.set(Calendar.YEAR, 20);
              c.set(Calendar.MONTH, Calendar.JANUARY + M31[i]);
              c.set(Calendar.DAY_OF_MONTH, d);
              c.set(Calendar.HOUR_OF_DAY, h);
              c.set(Calendar.MINUTE, M[m]);
              c.set(Calendar.SECOND, S[s]);
              c.set(Calendar.MILLISECOND, 88);
              c.add(Calendar.MILLISECOND, 1);

              boolean dltState0 = c.getTimeZone().inDaylightTime(c.getTime());

              c.setTime(rc.getNextCheckDate(c.getTime()));
              c.add(Calendar.MILLISECOND, 0);

              boolean dltState1 = c.getTimeZone().inDaylightTime(c.getTime());

              assertEquals(c.get(Calendar.MILLISECOND), 0);
              assertEquals(c.get(Calendar.SECOND), 0);
              assertEquals(c.get(Calendar.MINUTE), (M[m] + 1) % 60);

              if (M[m] == 59) {
                if (dltState0 == dltState1) {
                  assertEquals(c.get(Calendar.HOUR_OF_DAY), (h + 1) % 24);
                }

                if (h == 23) {
                  assertEquals(c.get(Calendar.DAY_OF_MONTH), (d + 1) % 32);

                  if (d == 31) {
                    assertEquals(
                      c.get(Calendar.MONTH),
                      (Calendar.JANUARY + M31[i] + 1) % 12);
                  } else {
                    assertEquals(
                      c.get(Calendar.MONTH), Calendar.JANUARY + M31[i]);
                  }
                } else {
                  assertEquals(c.get(Calendar.DAY_OF_MONTH), d);
                }
              } else {
                // allow discrepancies only if we are switching from std to dls time
                if (c.get(Calendar.HOUR_OF_DAY) != h) {
                  c.add(Calendar.HOUR_OF_DAY, +1);

                  boolean dltState2 =
                    c.getTimeZone().inDaylightTime(c.getTime());

                  if (dltState1 == dltState2) {
                    fail("No switch");
                  }
                }

                assertEquals(c.get(Calendar.DAY_OF_MONTH), d);
                assertEquals(c.get(Calendar.MONTH), Calendar.JANUARY + M31[i]);
              }
            }
          }
        }
      }
    }
  }

  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(new DRFATestCase("testComputeCheckPeriod"));
    suite.addTest(new DRFATestCase("testRC1"));
    suite.addTest(new DRFATestCase("testRC2"));
    suite.addTest(new DRFATestCase("testRC3"));

    return suite;
  }
}
