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

package org.apache.log4j.pattern;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

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

    event =
      new LoggingEvent(
        Logger.class.getName(), logger, now, Level.INFO, "msg 1", null);
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
