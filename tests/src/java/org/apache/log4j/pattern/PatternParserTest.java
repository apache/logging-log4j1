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

package org.apache.log4j.pattern;

import junit.framework.TestCase;

import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

import java.io.CharArrayWriter;

import java.util.HashMap;


/**
   Test case for helpers/PatternParser.java. Tests the various
   conversion patterns supported by PatternParser. This test
   class tests PatternParser via the PatternLayout class which
   uses it.
 */
public class PatternParserTest extends TestCase {
  public CharArrayWriter charArrayWriter = new CharArrayWriter(1024);
  Logger logger = Logger.getLogger("org.foobar");
  LoggingEvent event;
  long now;

  public PatternParserTest(String name) {
    super(name);
    now = System.currentTimeMillis() + 13;

    event = new LoggingEvent();
    event.setLogger(logger);
    event.setTimeStamp(now);
    event.setLevel(Level.INFO);
    event.setMessage("msg 1");
  }

  public void setUp() {
    charArrayWriter.reset();
  }

  public void tearDown() {
  }

  String convert(LoggingEvent event, PatternConverter head)
    throws Exception {
    PatternConverter c = head;

    while (c != null) {
      System.out.println("pc " + c);
      c.format(charArrayWriter, event);
      c = c.next;
    }

    return charArrayWriter.toString();
  }

  public void testNewWord() throws Exception {
    PatternParser patternParser = new PatternParser("%z343");
    HashMap ruleRegistry = new HashMap(5);

    ruleRegistry.put("z343", Num343PatternConverter.class.getName());
    patternParser.setConverterRegistry(ruleRegistry);

    PatternConverter head = patternParser.parse();

    String result = convert(event, head);
    System.out.println("Result is[" + result + "]");
    assertEquals("343", result);
  }

  /* Test whether words starting with the letter 'n' are treated differently,
   * which was previously the case by mistake.
   */
  public void testNewWord2() throws Exception {
    PatternParser patternParser = new PatternParser("%n343");
    HashMap ruleRegistry = new HashMap(5);

    ruleRegistry.put("n343", Num343PatternConverter.class.getName());
    patternParser.setConverterRegistry(ruleRegistry);

    PatternConverter head = patternParser.parse();

    String result = convert(event, head);
    System.out.println("Result is[" + result + "]");
    assertEquals("343", result);
  }

  public void testBogusWord1() throws Exception {
    PatternParser patternParser = new PatternParser("%, foobar");
    PatternConverter head = patternParser.parse();

    String result = convert(event, head);
    System.out.println("Result is[" + result + "]");
    assertEquals("%, foobar", result);
  }

  public void testBogusWord2() throws Exception {
    PatternParser patternParser = new PatternParser("xyz %, foobar");
    PatternConverter head = patternParser.parse();

    String result = convert(event, head);
    System.out.println("Result is[" + result + "]");
    assertEquals("xyz %, foobar", result);
  }

  public void testBasic1() throws Exception {
    PatternParser patternParser = new PatternParser("hello %-5level - %m%n");
    PatternConverter head = patternParser.parse();

    String result = convert(event, head);
    System.out.println("Result is[" + result + "]");
    assertEquals("hello INFO  - msg 1" + Layout.LINE_SEP, result);
  }

  public void testBasic2() throws Exception {
    PatternParser patternParser =
      new PatternParser("%relative %-5level [%thread] %logger - %m%n");
    PatternConverter head = patternParser.parse();

    String result = convert(event, head);
    long expectedRelativeTime = now - LoggingEvent.getStartTime();
    System.out.println("Result is[" + result + "]");
    assertEquals(expectedRelativeTime + " INFO  [main] "+logger.getName()+" - msg 1" + Layout.LINE_SEP, result);
  }

//  public static Test suite() {
//    TestSuite suite = new TestSuite();
//    suite.addTest(new PatternParserTest("testBasic2"));
//
//    return suite;
//  }
}
