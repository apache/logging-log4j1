/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
   Exhaustive test of the DailyRollingFileAppender compute algorithm.

   @author Ceki G&uuml;lc&uuml;
   @author Curt Arnold
 */
public class DRFATestCase extends TestCase {

    /**
     * Create new test.
     * @param name test name.
     */
  public DRFATestCase(final String name) {
    super(name);
  }

    /**
     * Reset configuration after every test.
     */
  public void tearDown() {
      LogManager.resetConfiguration();
  }

    /**
     * Test prediction of check period.
     */
  public
  void testComputeCheckPeriod() {
    DailyRollingFileAppender drfa = new DailyRollingFileAppender();
    drfa.setName("testComputeCheckPeriod");
    drfa.setDatePattern("yyyy-MM-dd.'log'");
    drfa.activateOptions();

    int x = drfa.computeCheckPeriod();
    int y = DailyRollingFileAppender.TOP_OF_DAY;
    assertEquals(drfa.computeCheckPeriod(),
         DailyRollingFileAppender.TOP_OF_DAY);

    drfa.setDatePattern("yyyy-MM-dd mm.'log'");
    assertEquals(drfa.computeCheckPeriod(),
         DailyRollingFileAppender.TOP_OF_MINUTE);

    drfa.setDatePattern("yyyy-MM-dd a.'log'");
    assertEquals(drfa.computeCheckPeriod(),
         DailyRollingFileAppender.HALF_DAY);

    drfa.setDatePattern("yyyy-MM-dd HH.'log'");
    assertEquals(drfa.computeCheckPeriod(),
         DailyRollingFileAppender.TOP_OF_HOUR);

    drfa.setDatePattern("yyyy-MM.'log'");
    assertEquals(drfa.computeCheckPeriod(),
         DailyRollingFileAppender.TOP_OF_MONTH);

    drfa.setDatePattern("'log'HH'log'");
    assertEquals(drfa.computeCheckPeriod(),
         DailyRollingFileAppender.TOP_OF_HOUR);
  }


    /**
     *   Test of RollingCalendar.
     */
  public
  void testRC1() {
    RollingCalendar rc = new RollingCalendar();
    rc.setType(DailyRollingFileAppender.TOP_OF_DAY);

    Calendar c = Calendar.getInstance();

    // jan, mar, may, july, aug, oct, dec have 31 days
    int [] M31 = {0,2,4,6,7,9,11};

    for(int i = 0; i < M31.length; i ++) {
      for(int d = 1; d <=31; d++) {
    for(int h = 0; h < 23; h++) {
      c.clear();
      c.set(Calendar.YEAR, 20);
      c.set(Calendar.MONTH, Calendar.JANUARY + M31[i]);
      c.set(Calendar.DAY_OF_MONTH, d);
      c.set(Calendar.HOUR_OF_DAY, h);
      c.set(Calendar.MINUTE, 10);
      c.set(Calendar.SECOND, 10);
      c.set(Calendar.MILLISECOND, 88);

      c.setTime(rc.getNextCheckDate(c.getTime()));
      if(d == 31) {
        assertEquals(c.get(Calendar.MONTH),(Calendar.JANUARY+M31[i]+1)%12);
        assertEquals(c.get(Calendar.DAY_OF_MONTH), 1);
      } else {
        assertEquals(c.get(Calendar.MONTH), Calendar.JANUARY+M31[i]);
        assertEquals(c.get(Calendar.DAY_OF_MONTH), d+1);
      }
      assertEquals(c.get(Calendar.HOUR_OF_DAY), 0);
      assertEquals(c.get(Calendar.MINUTE), 0);
      assertEquals(c.get(Calendar.SECOND), 0);
      assertEquals(c.get(Calendar.MILLISECOND), 0);
    }
      }
    }
  }

    /**
     * RollingCalendar test.
     */
  public
  void testRC2() {
    RollingCalendar rc = new RollingCalendar();

    rc.setType(DailyRollingFileAppender.TOP_OF_HOUR);

    Calendar c = Calendar.getInstance();
    TimeZone tz = c.getTimeZone();

    // jan, mar, may, july, aug, oct, dec have 31 days
    int [] M31 = {0,2,4,6,7,9,11};

    for(int i = 0; i < M31.length; i ++) {
      System.out.println("Month = "+(M31[i]+1));
      for(int d = 1; d <= 31; d++) {
    for(int h = 0; h < 23; h++) {
      for(int m = 0; m <= 59; m++) {
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

        if(dltState0 == dltState1) {
          assertEquals(c.get(Calendar.HOUR_OF_DAY), (h+1)%24);
        } else {
          // returning to standard time
          if(dltState0) {
        assertEquals(c.get(Calendar.HOUR_OF_DAY), h);
          } else { // switching to day light saving time
        //System.err.println("m="+m+", h="+h+", d="+d+", i="+i);
        //if(h==2) {
        // System.err.println(c);
        //}
        //assertEquals(c.get(Calendar.HOUR_OF_DAY), (h+2)%24);
          }
        }

        if(h == 23) {
          assertEquals(c.get(Calendar.DAY_OF_MONTH), (d+1)%32);
          if(d == 31) {
        assertEquals(c.get(Calendar.MONTH),
                 (Calendar.JANUARY+M31[i]+1)%12);
          } else {
        assertEquals(c.get(Calendar.MONTH),
                 Calendar.JANUARY+M31[i]);
          }
        } else {
          assertEquals(c.get(Calendar.DAY_OF_MONTH), d);
          assertEquals(c.get(Calendar.MONTH), Calendar.JANUARY+M31[i]);
        }
      }
    }
      }
    }
  }

    /**
     * RollingCalendar test.
     */
  public
  void testRC3() {
    RollingCalendar rc = new RollingCalendar();

    rc.setType(DailyRollingFileAppender.TOP_OF_MINUTE);

    int[] S = {0, 1, 5, 10, 21, 30, 59};
    int[] M = {0, 1, 5, 10, 21, 30, 59};
    Calendar c = Calendar.getInstance();

    // jan, mar, may, july, aug, oct, dec have 31 days
    int [] M31 = {2,9,0,4,6,7,11};

    for(int i = 0; i < M31.length; i ++) {
      System.out.println("Month = "+(M31[i]+1));
      for(int d = 1; d <= 31; d++) {
    for(int h = 0; h < 23; h++) {
      for(int m = 0; m < M.length; m++) {
        for(int s = 0; s < S.length; s++) {
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
          assertEquals(c.get(Calendar.MINUTE), (M[m]+1)%60);

          if(M[m] == 59) {
        if(dltState0 == dltState1) {
          assertEquals(c.get(Calendar.HOUR_OF_DAY), (h+1)%24);
        }
        if(h == 23) {
          assertEquals(c.get(Calendar.DAY_OF_MONTH), (d+1)%32);
          if(d == 31) {
              assertEquals(c.get(Calendar.MONTH),
                 (Calendar.JANUARY+M31[i]+1)%12);
          } else {
            assertEquals(c.get(Calendar.MONTH),
                 Calendar.JANUARY+M31[i]);
          }
        } else {
          assertEquals(c.get(Calendar.DAY_OF_MONTH), d);
        }
          } else {
        // allow discrepancies only if we are switching from std to dls time
        if(c.get(Calendar.HOUR_OF_DAY) != h) {
          c.add(Calendar.HOUR_OF_DAY, +1);
          boolean dltState2 = c.getTimeZone().inDaylightTime(c.getTime());
          if(dltState1 == dltState2) {
            fail("No switch");
          }
        }
        assertEquals(c.get(Calendar.DAY_OF_MONTH), d);
        assertEquals(c.get(Calendar.MONTH), Calendar.JANUARY+M31[i]);
          }
        }
      }
    }
      }
    }
  }


    /**
     * Common test code for 3 parameter constructor.
     *
     * @throws IOException if IOException during test.
     */
   public void test3Param(final String datePattern,
                          final String filename) throws IOException {
       Layout layout = new SimpleLayout();
       DailyRollingFileAppender appender =
               new DailyRollingFileAppender(layout, filename, datePattern);
       assertEquals(datePattern, appender.getDatePattern());
       Logger root = Logger.getRootLogger();
       root.addAppender(appender);
       root.info("Hello, World");
       assertTrue(new File(filename).exists());
    }

    /**
     * Creates an appender with an unrecognized top-of-year pattern.
     *
     * @throws IOException if IOException during test.
     */
    public void testTopOfYear() throws IOException {
        try {
            test3Param("'.'yyyy", "output/drfa_topOfYear.log");
            fail("Expected illegal state exception.");
        } catch(IllegalStateException ex) {
            assertNotNull(ex);
        }
    }

    /**
     * Creates an appender with a top-of-month pattern.
     *
     * @throws IOException if IOException during test.
     */
    public void testTopOfMonth() throws IOException {
        test3Param("'.'yyyy-MM", "output/drfa_topOfMonth.log");
    }


    /**
     * Creates an appender with a top-of-week pattern.
     *
     * @throws IOException if IOException during test.
     */
    public void testTopOfWeek() throws IOException {
        test3Param("'.'yyyy-w", "output/drfa_topOfWeek.log");
    }

    /**
     * Creates an appender with a top-of-day pattern.
     *
     * @throws IOException if IOException during test.
     */
    public void testTopOfDay() throws IOException {
        test3Param("'.'yyyy-MM-dd", "output/drfa_topOfDay.log");
    }


    /**
     * Creates an appender with a half day pattern.
     *
     * @throws IOException if IOException during test.
     */
    public void testHalfDay() throws IOException {
        test3Param("'.'yyyy-MM-dd-a", "output/drfa_halfDay.log");
    }

    /**
     * Creates an appender with a top-of-hour pattern.
     *
     * @throws IOException if IOException during test.
     */
    public void testTopOfHour() throws IOException {
        test3Param("'.'yyyy-MM-dd-HH", "output/drfa_topOfHour.log");
    }

    /**
     * Creates an appender with a top-of-day pattern.
     *
     * @throws IOException if IOException during test.
     */
    public void testTopOfMinute() throws IOException {
        test3Param("'.'yyyy-MM-dd-HH-mm", "output/drfa_topOfMinute.log");
    }

    /**
     * Attempts to rollOver with no date pattern set.
     *
     * @throws IOException if IOException during test.
     */
    public void testRolloverNoPattern() throws IOException {
        Layout layout = new SimpleLayout();
        DailyRollingFileAppender appender =
                new DailyRollingFileAppender(layout, "output/drfa_nopattern.log", null);

        VectorErrorHandler errorHandler = new VectorErrorHandler();
        appender.setErrorHandler(errorHandler);
        appender.rollOver();
        assertEquals(1, errorHandler.size());
        assertEquals("Missing DatePattern option in rollOver().",
                errorHandler.getMessage(0));
    }

    /**
     * Tests rollOver with a minute periodicity.
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public void testMinuteRollover() throws IOException, InterruptedException {
        Layout layout = new SimpleLayout();
        String filename = "output/drfa_minuteRollover.log";
        String pattern = "'.'yyyy-MM-dd-HH-mm";

        DailyRollingFileAppender appender =
                new DailyRollingFileAppender(layout,
                        filename,
                        pattern);
        Logger root = Logger.getRootLogger();
        root.addAppender(appender);
        File firstFile =
                new File(filename + new SimpleDateFormat(pattern).format(new Date()));
        root.info("Hello, World");
        //
        //   create a file by that name so it has to be deleted
        //       on rollover
        firstFile.createNewFile();
        assertTrue(firstFile.exists());
        assertEquals(0, firstFile.length());

        Calendar cal = Calendar.getInstance();
        long now = cal.getTime().getTime();
        cal.set(Calendar.SECOND, 3);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.MINUTE, 1);
        long until = cal.getTime().getTime();
        Thread.sleep(until - now);
        root.info("Hello, World");
        assertTrue(firstFile.exists());
        assertTrue(firstFile.length() > 0);

    }

}
