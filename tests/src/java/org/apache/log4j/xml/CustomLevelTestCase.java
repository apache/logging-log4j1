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

package org.apache.log4j.xml;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.*;
import org.apache.log4j.util.Compare;


public class CustomLevelTestCase extends TestCase {
  static String TEMP = "output/temp";
  Logger root;
  Logger logger;

  public CustomLevelTestCase(String name) {
    super(name);
  }

  public void setUp() {
    root = Logger.getRootLogger();
    logger = Logger.getLogger(CustomLevelTestCase.class);
  }

  public void tearDown() {
    root.getLoggerRepository().resetConfiguration();

    Logger logger = LogManager.getLoggerRepository().getLogger("LOG4J");
    logger.setAdditivity(false);
    logger.addAppender(
      new ConsoleAppender(new PatternLayout("log4j: %-22c{2} - %m%n")));
  }

  public void test1() throws Exception {
    DOMConfigurator.configure("input/xml/customLevel1.xml");
    common();
    assertTrue(Compare.compare(TEMP, "witness/customLevel.1"));
  }

  public void test2() throws Exception {
    DOMConfigurator.configure("input/xml/customLevel2.xml");
    common();
    assertTrue(Compare.compare(TEMP, "witness/customLevel.2"));
  }

  public void test3() throws Exception {
    DOMConfigurator.configure("input/xml/customLevel3.xml");
    common();
    assertTrue(Compare.compare(TEMP, "witness/customLevel.3"));
  }

  public void test4() throws Exception {
    DOMConfigurator.configure("input/xml/customLevel4.xml");
    common();
    assertTrue(Compare.compare(TEMP, "witness/customLevel.4"));
  }

  void common() {
    int i = 0;
    logger.debug("Message " + ++i);
    logger.info("Message " + ++i);
    logger.warn("Message " + ++i);
    logger.error("Message " + ++i);
    logger.log(XLevel.TRACE, "Message " + ++i);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(new CustomLevelTestCase("test1"));
    suite.addTest(new CustomLevelTestCase("test2"));
    suite.addTest(new CustomLevelTestCase("test3"));
    suite.addTest(new CustomLevelTestCase("test4"));

    return suite;
  }
}
