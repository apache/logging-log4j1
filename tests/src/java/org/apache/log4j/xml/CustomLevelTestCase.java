
package org.apache.log4j.xml;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.apache.log4j.util.Filter;
import org.apache.log4j.util.LineNumberFilter;
import org.apache.log4j.util.ControlFilter;
import org.apache.log4j.util.ISO8601Filter;
import org.apache.log4j.util.Transformer;
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

  public void test1() throws Exception {
    DOMConfigurator.configure("input/xml/customLevel1.xml");
    common();
    assert(Compare.compare(TEMP, "witness/customLevel.1"));
  }

  public void test2() throws Exception {
    DOMConfigurator.configure("input/xml/customLevel2.xml");
    common();
    assert(Compare.compare(TEMP, "witness/customLevel.2"));
  }

  public void test3() throws Exception {
    DOMConfigurator.configure("input/xml/customLevel3.xml");
    common();
    assert(Compare.compare(TEMP, "witness/customLevel.3"));
  }

  void common() {
    int i = 0;
    logger.debug("Message " + ++i);
    logger.info ("Message " + ++i);
    logger.warn ("Message " + ++i);
    logger.error("Message " + ++i);
    logger.log(XLevel.TRACE, "Message " + ++i);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(new CustomLevelTestCase("test1"));
    suite.addTest(new CustomLevelTestCase("test2"));
    suite.addTest(new CustomLevelTestCase("test3"));
    return suite;
  }

}
