/*
 * Copyright 2004 The Apache Software Foundation.
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

package org.apache.log4j.pattern;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.helpers.AbsoluteTimeDateFormat;
import org.apache.log4j.pattern.CachedDateFormat;

import java.text.DateFormat;
import java.util.TimeZone;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Calendar;

/**
   Unit test {@link AbsoluteTimeDateFormat}.
   @author Curt Arnold
   @since 1.3.0 */
public final class CachedDateFormatTest extends TestCase {

  /**
   * Test constructor
   * @param name String test name
   */
  public CachedDateFormatTest(String name) {
    super(name);
  }

  /**
   * Asserts that formatting the provided date results
   * in the expected string.
   *
   * @param date Date date
   * @param timeZone TimeZone timezone for conversion
   * @param expected String expected string
   */
  private final void assertFormattedTime(Date date,
                                         TimeZone timeZone,
                                         String expected) {
    DateFormat formatter = new AbsoluteTimeDateFormat(timeZone);
    String actual = formatter.format(date);
    assertEquals(expected, actual);
  }

  /**
   * Timezone representing GMT.
   */
  private static final TimeZone GMT = TimeZone.getTimeZone("GMT");

  /**
   * Timezone for Chicago, Ill. USA.
   */
  private static final TimeZone CHICAGO = TimeZone.getTimeZone(
      "America/Chicago");

  /**
   * Test multiple calls in close intervals.
   */
  public void test1() {
    //   subsequent calls within one minute
    //     are optimized to reuse previous formatted value
    //     make a couple of nearly spaced calls
    DateFormat gmtFormat = new CachedDateFormat(AbsoluteTimeDateFormat.ABS_TIME_DATE_PATTERN);
    gmtFormat.setTimeZone(GMT);
    long ticks = 12601L * 86400000L;
    Date jul1 = new Date(ticks);
    String r = gmtFormat.format(jul1);
    assertEquals("00:00:00,000", r);
    
    Date plus8ms = new Date(ticks + 8);
    assertEquals("00:00:00,008", gmtFormat.format(plus8ms));
    Date plus17ms = new Date(ticks + 17);
    assertEquals("00:00:00,017", gmtFormat.format(plus17ms));
    Date plus237ms = new Date(ticks + 237);
    assertEquals("00:00:00,237", gmtFormat.format(plus237ms));
    Date plus1415ms = new Date(ticks + 1415);
    assertEquals("00:00:01,415", gmtFormat.format(plus1415ms));
  }

  /**
   *  Check for interaction between caches.
   */

  public void test2() {
      Date jul2 = new Date(12602L * 86400000L);
      DateFormat gmtFormat = new CachedDateFormat(AbsoluteTimeDateFormat.ABS_TIME_DATE_PATTERN);
      gmtFormat.setTimeZone(GMT);
      DateFormat chicagoFormat = new CachedDateFormat(AbsoluteTimeDateFormat.ABS_TIME_DATE_PATTERN);
      chicagoFormat.setTimeZone(CHICAGO);
      assertEquals("00:00:00,000", gmtFormat.format(jul2));
      assertEquals("19:00:00,000", chicagoFormat.format(jul2));
      assertEquals("00:00:00,000", gmtFormat.format(jul2));
  }

  /**
   * Test multiple calls in close intervals prior to 1 Jan 1970.
   */
  public void test3() {
    //   subsequent calls within one minute
    //     are optimized to reuse previous formatted value
    //     make a couple of nearly spaced calls
    DateFormat gmtFormat = new CachedDateFormat(AbsoluteTimeDateFormat.ABS_TIME_DATE_PATTERN);
    gmtFormat.setTimeZone(GMT);
    long ticks = -7L * 86400000L;
    Date jul1 = new Date(ticks);
    assertEquals("00:00:00,000", gmtFormat.format(jul1));
    Date plus8ms = new Date(ticks + 8);
    assertEquals("00:00:00,008", gmtFormat.format(plus8ms));
    Date plus17ms = new Date(ticks + 17);
    assertEquals("00:00:00,017", gmtFormat.format(plus17ms));
    Date plus237ms = new Date(ticks + 237);
    assertEquals("00:00:00,237", gmtFormat.format(plus237ms));
    Date plus1415ms = new Date(ticks + 1415);
    assertEquals("00:00:01,415", gmtFormat.format(plus1415ms));
  }

  public void test4() {
    //   subsequent calls within one minute
    //     are optimized to reuse previous formatted value
    //     make a couple of nearly spaced calls
    SimpleDateFormat baseFormat =
         new SimpleDateFormat("EEE, MMM dd, HH:mm:ss.SSS Z");
    //     new SimpleDateFormat("EEE, MMM dd, HH:mm:ss.SSS Z", Locale.ENGLISH);

    DateFormat cachedFormat = new CachedDateFormat("EEE, MMM dd, HH:mm:ss.SSS Z");
    //
    //   use a date in 2000 to attempt to confuse the millisecond locator
    long ticks = 11141L * 86400000L;
    Date jul1 = new Date(ticks);
    assertEquals(baseFormat.format(jul1), cachedFormat.format(jul1));
    Date plus8ms = new Date(ticks + 8);
    String base = baseFormat.format(plus8ms);
    String cached = cachedFormat.format(plus8ms);
    assertEquals(baseFormat.format(plus8ms), cachedFormat.format(plus8ms));
    Date plus17ms = new Date(ticks + 17);
    assertEquals(baseFormat.format(plus17ms), cachedFormat.format(plus17ms));
    Date plus237ms = new Date(ticks + 237);
    assertEquals(baseFormat.format(plus237ms), cachedFormat.format(plus237ms));
    Date plus1415ms = new Date(ticks + 1415);
    assertEquals(baseFormat.format(plus1415ms), cachedFormat.format(plus1415ms));
  }

  public void test5() {
    //   subsequent calls within one minute
    //     are optimized to reuse previous formatted value
    //     make a couple of nearly spaced calls
    Locale thai = new Locale("th");
    String pattern = "EEE, MMM dd, HH:mm:ss.SSS Z";
    SimpleDateFormat baseFormat =
         new SimpleDateFormat("EEE, MMM dd, HH:mm:ss.SSS Z", thai);
    DateFormat cachedFormat = new CachedDateFormat(pattern, thai);
    //
    //   use a date in 2000 to attempt to confuse the millisecond locator
    long ticks = 11141L * 86400000L;
    Date jul1 = new Date(ticks);
    assertEquals(baseFormat.format(jul1), cachedFormat.format(jul1));
    Date plus8ms = new Date(ticks + 8);
    assertEquals(baseFormat.format(plus8ms), cachedFormat.format(plus8ms));
    Date plus17ms = new Date(ticks + 17);
    assertEquals(baseFormat.format(plus17ms), cachedFormat.format(plus17ms));
    Date plus237ms = new Date(ticks + 237);
    assertEquals(baseFormat.format(plus237ms), cachedFormat.format(plus237ms));
    Date plus1415ms = new Date(ticks + 1415);
    assertEquals(baseFormat.format(plus1415ms), cachedFormat.format(plus1415ms));
  }

  /**
   * Checks that getNumberFormat does not return null.
   */
  public void test6() {
    assertNotNull(new SimpleDateFormat().getNumberFormat());
  }

  /**
   * Attempt to cache a RelativeTimeDateFormat which isn't compatible
   * with caching.  Should just delegate to the RelativeTimeDateFormat.
   */
//  public void test7() {
//    DateFormat baseFormat = new RelativeTimeDateFormat();
//    DateFormat cachedFormat = new CachedDateFormat(baseFormat);
//    long ticks = 12603L * 86400000L;
//    Date jul3 = new Date(ticks);
//    assertEquals(baseFormat.format(jul3), cachedFormat.format(jul3));
//    Date plus8ms = new Date(ticks + 8);
//    assertEquals(baseFormat.format(plus8ms), cachedFormat.format(plus8ms));
//    Date plus17ms = new Date(ticks + 17);
//    assertEquals(baseFormat.format(plus17ms), cachedFormat.format(plus17ms));
//    Date plus237ms = new Date(ticks + 237);
//    assertEquals(baseFormat.format(plus237ms), cachedFormat.format(plus237ms));
//    Date plus1415ms = new Date(ticks + 1415);
//    assertEquals(baseFormat.format(plus1415ms), cachedFormat.format(plus1415ms));
//  }

  /**
   * Set time zone on cached and check that it is effective.
   */
  public void test8() {
    String pattern = "yyyy-MM-dd HH:mm:ss,SSS";
    DateFormat baseFormat = new SimpleDateFormat(pattern);
    DateFormat cachedFormat = new CachedDateFormat(pattern);
    cachedFormat.setTimeZone(TimeZone.getTimeZone("GMT-6"));
    Date jul4 = new Date(12603L * 86400000L);
    assertEquals("2004-07-03 18:00:00,000", cachedFormat.format(jul4));
  }


  /**
   * Test of caching when less than three millisecond digits are specified.
   */
  public void test9() {
    String pattern = "yyyy-MMMM-dd HH:mm:ss,SS Z";
    DateFormat baseFormat = new SimpleDateFormat(pattern, Locale.US);
    CachedDateFormat cachedFormat = new CachedDateFormat(pattern);
    TimeZone cet = TimeZone.getTimeZone("GMT+1");
    cachedFormat.setTimeZone(cet);
    
    Calendar c = Calendar.getInstance();
    c.set(2004, Calendar.DECEMBER, 12, 20, 0);
    c.set(Calendar.SECOND, 37);
    c.set(Calendar.MILLISECOND, 23);
    c.setTimeZone(cet);

    String s = cachedFormat.format(c.getTime());
    assertEquals("2004-December-12 20:00:37,23 +0100", s);

    c.set(2005, Calendar.JANUARY, 1, 0, 0);
    c.set(Calendar.SECOND, 13);
    c.set(Calendar.MILLISECOND, 905);

    s = cachedFormat.format(c.getTime());
    assertEquals("2005-January-01 00:00:13,905 +0100", s);
  }
  

  /**
   * Test when millisecond position moves but length remains constant.
   */
  public void test10() {
    String pattern = "MMMM SSS EEEEEE";
    DateFormat cachedFormat = new CachedDateFormat(pattern);
    
    Calendar c = Calendar.getInstance();
    c.set(2004, Calendar.OCTOBER, 5, 20, 0);
    c.set(Calendar.SECOND, 37);
    c.set(Calendar.MILLISECOND, 23);

    String s = cachedFormat.format(c.getTime());
    assertEquals("October 023 Tuesday", s);

    // since we are in a different slot, cachedFormat will return correct
    // results since it will freshly format
    c.set(2004, Calendar.NOVEMBER, 1, 0, 0);
    c.set(Calendar.MILLISECOND, 6);
    s = cachedFormat.format(c.getTime());
    assertEquals("November 006 Monday", s);

    // exercise the cache, (there should be none)
    s = cachedFormat.format(c.getTime());
    assertEquals("November 006 Monday", s);

  }
  
  public void testS2() {
    String pattern = "HH:mm:ss,SS";
    DateFormat cachedFormat = new CachedDateFormat(pattern);
    DateFormat sdf = new SimpleDateFormat(pattern);
    String s;
    
    Calendar c = Calendar.getInstance();
    c.set(2004, Calendar.OCTOBER, 5, 20, 54);
    c.set(Calendar.SECOND, 37);
    c.set(Calendar.MILLISECOND, 7);

    s = cachedFormat.format(c.getTime());
    assertEquals("20:54:37,07", s);
    
    // excercise the cache (if there is any)
    s = cachedFormat.format(c.getTime());
    assertEquals("20:54:37,07", s);
  }
  
  
  public static Test xsuite() {
    TestSuite suite = new TestSuite();
    suite.addTest(new CachedDateFormatTest("test9"));
    //suite.addTest(new CachedDateFormatTest("testS2"));
    return suite;
  }
}
