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
  Logger logger = Logger.getLogger(PatternParserTest.class);

  public PatternParserTest(String name) {
    super(name);
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
      System.out.println("pc "+c);
      c.format(charArrayWriter, event);
      c = c.next;
    }

    return charArrayWriter.toString();
  }

  public void testNewWord() throws Exception {
    PatternParser patternParser = new PatternParser("%zum343");
    HashMap ruleRegistry = new HashMap(5);

    ruleRegistry.put("zum343", Num343PatternConverter.class.getName());
    patternParser.setConverterRegistry(ruleRegistry);

    PatternConverter head = patternParser.parse();

    LoggingEvent event =
      new LoggingEvent(
        Logger.class.getName(), logger, Level.DEBUG, "msg 1", null);
    String result = convert(event, head);
    System.out.println("Resuls is["+result+"]");
    assertEquals("343", result);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(new PatternParserTest("testNewWord"));

    return suite;
  }
}
