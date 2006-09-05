/*
 * Copyright 1999-2005 The Apache Software Foundation.
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

package org.apache.log4j.xml;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.apache.log4j.FileAppender;
import org.apache.log4j.xml.XMLLayout;
import org.apache.log4j.util.Filter;
import org.apache.log4j.util.ControlFilter;
import org.apache.log4j.util.XMLTimestampFilter;
import org.apache.log4j.util.XMLLineAttributeFilter;
import org.apache.log4j.util.LineNumberFilter;
import org.apache.log4j.util.Transformer;
import org.apache.log4j.util.Compare;
import org.apache.log4j.util.JunitTestRunnerFilter;
import org.apache.log4j.util.SunReflectFilter;

public class XMLLayoutTestCase extends TestCase {

  static String TEMP = "output/temp";
  static String FILTERED = "output/filtered";

  Logger root; 
  Logger logger;

  public XMLLayoutTestCase(String name) {
    super(name);
  }

  public void setUp() {
    root = Logger.getRootLogger();
    root.setLevel(Level.TRACE);
    logger = Logger.getLogger(XMLLayoutTestCase.class);
    logger.setLevel(Level.TRACE);
  }

  public void tearDown() {  
    root.getLoggerRepository().resetConfiguration();
  }

  public void basic() throws Exception {
    XMLLayout xmlLayout = new XMLLayout();
    root.addAppender(new FileAppender(xmlLayout, TEMP, false));
    common();
    Transformer.transform(
      TEMP, FILTERED,
      new Filter[] {
        new LineNumberFilter(),
        new XMLTimestampFilter(),
        new JunitTestRunnerFilter(),
        new SunReflectFilter()
      });
    assertTrue(Compare.compare(FILTERED, "witness/xmlLayout.1"));
  }

  public void locationInfo() throws Exception {
    XMLLayout xmlLayout = new XMLLayout();
    xmlLayout.setLocationInfo(true);
    root.addAppender(new FileAppender(xmlLayout, TEMP, false));
    common();
    Transformer.transform(
      TEMP, FILTERED,
      new Filter[] {
        new LineNumberFilter(),
        new XMLTimestampFilter(), 
        new XMLLineAttributeFilter(),
        new JunitTestRunnerFilter(),
        new SunReflectFilter()
      });
    assertTrue(Compare.compare(FILTERED, "witness/xmlLayout.2"));
  }

  public void testCDATA() throws Exception {
    XMLLayout xmlLayout = new XMLLayout();
    xmlLayout.setLocationInfo(true);
    root.addAppender(new FileAppender(xmlLayout, TEMP, false));
    
    logger.trace("Message with embedded <![CDATA[<hello>hi</hello>]]>.");
    logger.debug("Message with embedded <![CDATA[<hello>hi</hello>]]>.");

    Transformer.transform(
      TEMP, FILTERED,
      new Filter[] {
        new LineNumberFilter(), 
        new XMLTimestampFilter(),
        new XMLLineAttributeFilter(), 
        new SunReflectFilter(),
        new JunitTestRunnerFilter()

      });
    Transformer.transform(TEMP, FILTERED, new Filter[] {new LineNumberFilter(),
    						  new XMLTimestampFilter(),
    						  new XMLLineAttributeFilter()});
    assertTrue(Compare.compare(FILTERED, "witness/xmlLayout.3"));
  }

  public void testNull() throws Exception {
    XMLLayout xmlLayout = new XMLLayout();
    root.addAppender(new FileAppender(xmlLayout, TEMP, false));
    logger.debug("hi");
    logger.debug(null);
    Exception e = new Exception((String) null);
    logger.debug("hi", e);
    Transformer.transform(
      TEMP, FILTERED,
      new Filter[] { new LineNumberFilter(),
          new XMLTimestampFilter(),  
          new JunitTestRunnerFilter(),
          new SunReflectFilter()});
    assertTrue(Compare.compare(FILTERED, "witness/xmlLayout.null"));
  }
  
  void common() {
    int i = -1;
 
    X x = new X();

    logger.trace("Message " + ++i);
    root.trace("Message " + i);    

    logger.debug("Message " + ++i);
    root.debug("Message " + i);        

    logger.info("Message " + ++i);
    root.info("Message " + i);        

    logger.warn ("Message " + ++i);
    root.warn("Message " + i);        
 
    logger.error("Message " + ++i);
    root.error("Message " + i);
    
    logger.log(Level.FATAL, "Message " + ++i);
    root.log(Level.FATAL, "Message " + i);    
    
    Exception e = new Exception("Just testing");
    logger.debug("Message " + ++i, e);
    root.debug("Message " + i, e);
    
    logger.error("Message " + ++i, e);
    root.error("Message " + i, e);    
  }

  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(new XMLLayoutTestCase("basic"));
    suite.addTest(new XMLLayoutTestCase("locationInfo"));
    suite.addTest(new XMLLayoutTestCase("testCDATA"));
    suite.addTest(new XMLLayoutTestCase("testNull"));
    return suite;
  }


  class X {
    Logger logger = Logger.getLogger(X.class);
    public X() {
      logger.info("in X() constructor");
    }
  }
}
