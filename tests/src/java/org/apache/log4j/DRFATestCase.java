/*
 * Copyright 1999,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.log4j;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.Calendar;
import java.util.TimeZone;

import org.apache.log4j.rolling.helper.RollingCalendar;


/**
   Exhaustive test of the DailyRollingFileAppender compute algorithm.

   @author Ceki G&uuml;lc&uuml;
 */
public class DRFATestCase extends TestCase {
  public DRFATestCase(String name) {
    super(name);
  }

  public void testComputeCheckPeriod() {
    RollingCalendar rc = new RollingCalendar();
    
    //DailyRollingFileAppender drfa = new DailyRollingFileAppender();
    //drfa.setName("testComputeCheckPeriod");
    //rc.setsetDatePattern("yyyy-MM-dd.'log'");
    //drfa.activateOptions();

    //int x = rc.computeTriggeringPeriod("yyyy-MM-dd.'log'");
    //int y = RollingCalendar.TOP_OF_DAY;
    assertEquals(
    rc.computeTriggeringPeriod("yyyy-MM-dd.'log'"), RollingCalendar.TOP_OF_DAY);

    assertEquals(rc.computeTriggeringPeriod("yyyy-MM-dd mm.'log'"), RollingCalendar.TOP_OF_MINUTE);

    assertEquals(rc.computeTriggeringPeriod("yyyy-MM-dd a.'log'"), RollingCalendar.HALF_DAY);

    assertEquals(
      rc.computeTriggeringPeriod("yyyy-MM-dd HH.'log'"), RollingCalendar.TOP_OF_HOUR);

    assertEquals(
      rc.computeTriggeringPeriod("yyyy-MM.'log'"), RollingCalendar.TOP_OF_MONTH);


    assertEquals(
      rc.computeTriggeringPeriod("'log'HH'log'"), RollingCalendar.TOP_OF_HOUR);
  }

  public void testRC1() {
    RollingCalendar rc = new RollingCalendar();
    rc.setType(RollingCalendar.TOP_OF_DAY);

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

    rc.setType(RollingCalendar.TOP_OF_HOUR);

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

    rc.setType(RollingCalendar.TOP_OF_MINUTE);

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
