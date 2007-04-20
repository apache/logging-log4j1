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

package org.apache.log4j.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.StringTokenizer;

import junit.framework.TestCase;

import org.apache.log4j.AsyncAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.helpers.SyslogWriter;
import org.apache.log4j.spi.LoggingEvent;


/**
 *    Tests for SyslogAppender
 *
 * @author Curt Arnold
 **/
public class SyslogAppenderTest extends TestCase {
  
  DatagramSocket ds;
  DatagramPacket p = new DatagramPacket(new byte[1000], 0, 1000);
  
  /**
   * Create new instance of SyslogAppenderTest.
   * @param testName test name
   */
  public SyslogAppenderTest(final String testName) {
    super(testName);
  }
  
  protected void setUp() throws Exception {
    ds = new DatagramSocket();
    ds.setSoTimeout(2000);    
  }

  /**
    * Resets configuration after every test.
  */
  protected void tearDown() {
    ds.close();    
    LogManager.resetConfiguration();
  }

  /**
   * Test default constructor.
   * @deprecated Since getFacilityPrinting is deprecated
   */
  public void testDefaultConstructor() {
    SyslogAppender appender = new SyslogAppender();
    assertEquals("user", appender.getFacility());
    assertEquals(false, appender.getFacilityPrinting());
    assertNull(appender.getLayout());
    assertNull(appender.getSyslogHost());
    assertTrue(appender.requiresLayout());
  }

  /**
   * Test two parameter constructor.
   * @deprecated Since getFacilityPrinting is deprecated
   */
  public void testTwoParamConstructor() {
    Layout layout = new PatternLayout();
    SyslogAppender appender = new SyslogAppender(layout, 24);
    assertEquals("daemon", appender.getFacility());
    assertEquals(false, appender.getFacilityPrinting());
    assertEquals(layout, appender.getLayout());
    assertNull(appender.getSyslogHost());
    assertTrue(appender.requiresLayout());
  }

  /**
   * Test two parameter constructor with unexpected facility.
   * @deprecated Since getFacilityPrinting is deprecated
   */
  public void testTwoParamConstructorBadFacility() {
    Layout layout = new PatternLayout();
    SyslogAppender appender = new SyslogAppender(layout, 25);
    assertEquals("user", appender.getFacility());
    assertEquals(false, appender.getFacilityPrinting());
    assertEquals(layout, appender.getLayout());
    assertNull(appender.getSyslogHost());
    assertTrue(appender.requiresLayout());
  }

  /**
   * Test three parameter constructor.
   * @deprecated Since getFacilityPrinting is deprecated
   */
  public void testThreeParamConstructor() {
    Layout layout = new PatternLayout();
    SyslogAppender appender =
      new SyslogAppender(layout, "syslog.example.org", 24);
    assertEquals("daemon", appender.getFacility());
    assertEquals(false, appender.getFacilityPrinting());
    assertEquals(layout, appender.getLayout());
    assertEquals("syslog.example.org", appender.getSyslogHost());
    assertTrue(appender.requiresLayout());
  }

  /**
   * Test getFacilityString for expected facility codes.
   * @deprecated Since getFacilityString is deprecated
   */
  public void testGetFacilityString() {
    String expected =
      "kern user mail daemon auth syslog lpr news "
      + "uucp cron authpriv ftp local0 local1 local2 local3 "
      + "local4 local5 local6 local7 ";
    StringBuffer actual = new StringBuffer();

    for (int i = 0; i <= 11; i++) {
      actual.append(SyslogAppender.getFacilityString(i << 3));
      actual.append(' ');
    }

    for (int i = 16; i <= 23; i++) {
      actual.append(SyslogAppender.getFacilityString(i << 3));
      actual.append(' ');
    }

    assertEquals(expected, actual.toString());
  }

  /**
   * Test getFacilityString for some unexpected facility codes.
   * @deprecated Since getFacilityString is deprecated
   */
  public void testGetFacilityStringUnexpected() {
    assertNull(SyslogAppender.getFacilityString(1));
    assertNull(SyslogAppender.getFacilityString(12 << 3));
  }

  /**
   * Test getFacility with a bogus facility name.
   */
  public void testGetFacilityBogus() {
    assertEquals(-1, SyslogAppender.getFacility("bogus"));
  }

  /**
   * Test getFacility with a null facility name.
   */
  public void testGetFacilityNull() {
    assertEquals(-1, SyslogAppender.getFacility(null));
  }

  /**
   * Test getFacility for expected system facility names.
   */
  public void testGetFacilitySystemNames() {
    String[] names =
      new String[] {
        "kErn", "usEr", "MaIL", "daemOn", "auTh", "syslOg", "lPr", "newS",
        "Uucp", "croN", "authprIv", "ftP"
      };

    for (int i = 0; i <= 11; i++) {
      assertEquals(i << 3, SyslogAppender.getFacility(names[i]));
    }
  }

  /**
   * Test getFacility for expected system facility names.
   */
  public void testGetFacilityLocalNames() {
    String[] names =
      new String[] {
        "lOcal0", "LOCAL1", "loCal2", "locAl3", "locaL4", "local5", "LOCal6",
        "loCAL7"
      };

    for (int i = 0; i <= 7; i++) {
      assertEquals((16 + i) << 3, SyslogAppender.getFacility(names[i]));
    }
  }

  /**
   * Test setFacilityPrinting.
   * @deprecated Since get/setFacilityPrinting are deprecated
   */
  public void testSetFacilityPrinting() {
    SyslogAppender appender = new SyslogAppender();
    assertFalse(appender.getFacilityPrinting());
    appender.setFacilityPrinting(true);
    assertTrue(appender.getFacilityPrinting());
    appender.setFacilityPrinting(false);
    assertFalse(appender.getFacilityPrinting());
  }

  /**
   * Test of SyslogAppender constants.
   */
  public void testConstants() {
    assertEquals(0 << 3, SyslogAppender.LOG_KERN);
    assertEquals(1 << 3, SyslogAppender.LOG_USER);
    assertEquals(2 << 3, SyslogAppender.LOG_MAIL);
    assertEquals(3 << 3, SyslogAppender.LOG_DAEMON);
    assertEquals(4 << 3, SyslogAppender.LOG_AUTH);
    assertEquals(5 << 3, SyslogAppender.LOG_SYSLOG);
    assertEquals(6 << 3, SyslogAppender.LOG_LPR);
    assertEquals(7 << 3, SyslogAppender.LOG_NEWS);
    assertEquals(8 << 3, SyslogAppender.LOG_UUCP);
    assertEquals(9 << 3, SyslogAppender.LOG_CRON);
    assertEquals(10 << 3, SyslogAppender.LOG_AUTHPRIV);
    assertEquals(11 << 3, SyslogAppender.LOG_FTP);
    assertEquals(16 << 3, SyslogAppender.LOG_LOCAL0);
    assertEquals(17 << 3, SyslogAppender.LOG_LOCAL1);
    assertEquals(18 << 3, SyslogAppender.LOG_LOCAL2);
    assertEquals(19 << 3, SyslogAppender.LOG_LOCAL3);
    assertEquals(20 << 3, SyslogAppender.LOG_LOCAL4);
    assertEquals(21 << 3, SyslogAppender.LOG_LOCAL5);
    assertEquals(22 << 3, SyslogAppender.LOG_LOCAL6);
    assertEquals(23 << 3, SyslogAppender.LOG_LOCAL7);
  }

  /**
   * Test setFacility with null.
   * Should have no effect.
   */
  public void testSetFacilityKern() {
    SyslogAppender appender = new SyslogAppender();
    appender.setFacility("kern");
    appender.setFacility(null);
    assertEquals("kern", appender.getFacility());
  }

  /**
   * Test setFacility with null.
   * Should have no effect.
   */
  public void testSetFacilityNull() {
    SyslogAppender appender = new SyslogAppender();
    appender.setFacility("kern");
    appender.setFacility(null);
    assertEquals("kern", appender.getFacility());
  }

  /**
   * Test setFacility with bogus value.
   * Should reset to user.
   */
  public void testSetFacilityBogus() {
    SyslogAppender appender = new SyslogAppender();
    appender.setFacility("kern");
    appender.setFacility("bogus");
    assertEquals("user", appender.getFacility());
  }

  /**
   * Tests calling setFacility after appender has been activated.
   * @deprecated Since setErrorHandler is deprecated
   */
  public void testSetFacilityAfterActivation() {
    SyslogAppender appender = new SyslogAppender();
    appender.setName("foo");
    appender.setThreshold(Level.INFO);
    appender.setSyslogHost("localhost");
    appender.setFacility("user");
    appender.setLayout(new PatternLayout("%m%n"));

    org.apache.log4j.VectorErrorHandler errorHandler =
      new org.apache.log4j.VectorErrorHandler();
    appender.setErrorHandler(errorHandler);
    appender.activateOptions();
    appender.setFacility("kern");
    assertEquals("kern", appender.getFacility());
  }

  /**
   * Tests that append method drops messages below threshold.
   * Can't reach isSevereAsThreshold call in SyslogAppender.append
   * since it is checked in AppenderSkeleton.doAppend.
   */
  public void testAppendBelowThreshold() {
    SyslogAppender appender = new SyslogAppender();
    appender.setThreshold(Level.ERROR);
    appender.setSyslogHost("localhost");
    appender.setLayout(new PatternLayout("%m%n"));
    appender.activateOptions();

    Logger logger = Logger.getRootLogger();
    logger.addAppender(appender);
    logger.info(
      "Should not be logged by SyslogAppenderTest.testAppendBelowThreshold.");
  }

  /**
   * Tests that append method drops messages below threshold.
   * @deprecated Since setErrorHandler is deprecated
   */
  public void testAppendNoHost() {
    SyslogAppender appender = new SyslogAppender();
    appender.setName("foo");
    appender.setThreshold(Level.INFO);

    org.apache.log4j.VectorErrorHandler errorHandler =
      new org.apache.log4j.VectorErrorHandler();
    appender.setErrorHandler(errorHandler);
    appender.setLayout(new PatternLayout("%m%n"));

    //
    //   log4j 1.2 would not throw exception on activateOptions
    //     but would call ErrorHandler on first log attempt
    //
    try {
      appender.activateOptions();
    } catch (IllegalStateException ex) {
      return;
    }

    fail("Expected IllegalStateException");
  }

  /**
   * Tests append method under normal conditions.
   * @deprecated Since setErrorHandler is deprecated
   */
  public void testAppend() {
    SyslogAppender appender = new SyslogAppender();
    appender.setName("foo");
    appender.setThreshold(Level.INFO);
    appender.setSyslogHost("localhost");
    appender.setFacility("user");
    appender.setLayout(new PatternLayout("%m%n"));

    org.apache.log4j.VectorErrorHandler errorHandler =
      new org.apache.log4j.VectorErrorHandler();
    appender.setErrorHandler(errorHandler);
    appender.activateOptions();

    //
    //  wrap SyslogAppender with an Async since appender may
    //    hang if syslogd is not accepting network messages
    //
    AsyncAppender asyncAppender = new AsyncAppender();
    asyncAppender.addAppender(appender);
    asyncAppender.activateOptions();

    Logger logger = Logger.getRootLogger();
    logger.addAppender(asyncAppender);

    Exception e =
      new Exception("Expected exception from SyslogAppenderTest.testAppend");
    logger.info(
      "Expected message from log4j unit test SyslogAppenderTest.testAppend.", e);
    assertEquals(0, errorHandler.size());
  }

  /**
    *  Tests SyslogAppender with IPv6 address.
    */  
  public void testIPv6() {
      SyslogAppender appender = new SyslogAppender();
      appender.setSyslogHost("::1");
  }

  /**
    *  Tests SyslogAppender with IPv6 address enclosed in square brackets.
    */  
  public void testIPv6InBrackets() {
      SyslogAppender appender = new SyslogAppender();
      appender.setSyslogHost("[::1]");
  }

  /**
    *  Tests SyslogAppender with IPv6 address enclosed in square brackets
    *     followed by port specification.
    */  
  public void testIPv6AndPort() {
      SyslogAppender appender = new SyslogAppender();
      appender.setSyslogHost("[::1]:1514");
  }

  /**
    *  Tests SyslogAppender with host name enclosed in square brackets
    *     followed by port specification.
    */  
  public void testHostNameAndPort() {
      SyslogAppender appender = new SyslogAppender();
      appender.setSyslogHost("localhost:1514");
  }

  /**
    *  Tests SyslogAppender with IPv4 address followed by port specification.
    */  
  public void testIPv4AndPort() {
      SyslogAppender appender = new SyslogAppender();
      appender.setSyslogHost("127.0.0.1:1514");
  }
  
  private String toString(DatagramPacket p){
    return new String(p.getData(), 0, p.getLength());
  }
  
  public void testActualLogging() throws Exception {
    SyslogAppender appender = new SyslogAppender();
    appender.setSyslogHost("localhost:" + ds.getLocalPort());
    appender.setName("name");
    PatternLayout pl = new PatternLayout("%m");
    pl.setFooter("EOF");
    appender.setLayout(pl);
    appender.activateOptions();
    
    Logger l = Logger.getRootLogger();
    l.addAppender(appender);
    l.info("greetings");
    ds.receive(p);
    String s = toString(p);
    StringTokenizer st = new StringTokenizer(s, "<>() ");
    assertEquals("14", st.nextToken());
    assertEquals(3, st.nextToken().length());
    st.nextToken(); // date
    st.nextToken(); // time
    assertEquals(appender.getLocalHostname(), st.nextToken());
    assertEquals("greetings", st.nextToken());
  }
  
  public void testLoggingHeaderFooter() throws Exception { 
    SyslogAppender appender = new SyslogAppender();
    appender.setSyslogHost("localhost:" + ds.getLocalPort());
    appender.setName("name");

    PatternLayout pl = new PatternLayout("%m");
    pl.setHeader("HI");
    pl.setFooter("BYE");
    appender.setLayout(pl);
    appender.activateOptions();
    ds.receive(p);
    String s = toString(p);
    assertEquals(true, s.endsWith("HI"));

    appender.close();
    ds.receive(p);
    s = toString(p);
    assertEquals(true, s.endsWith("BYE"));
  }
  
  public void testLeak() throws IOException { 
    DatagramSocket ds = new DatagramSocket();
    for (int i = 0; i < 100; i++) {
      SyslogWriter sw = new SyslogWriter("localhost:" + ds.getLocalPort());
      sw.close();
    }
    ds.close();
  }
  
}
