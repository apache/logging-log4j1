
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

public class DOMTestCase extends TestCase {

  static String TEMP_A1 = "output/temp.A1";
  static String TEMP_A2 = "output/temp.A2";
  static String FILTERED_A1 = "output/filtered.A1";
  static String FILTERED_A2 = "output/filtered.A2";


  static String EXCEPTION1 = "java.lang.Exception: Just testing";
  static String EXCEPTION2 = "\\s*at .*\\(.*:\\d{1,4}\\)";
  static String EXCEPTION3 = "\\s*at .*\\(Native Method\\)";

  static String TEST1_1A_PAT = 
                       "(DEBUG|INFO |WARN |ERROR|FATAL) \\w*\\.\\w* - Message \\d";

  static String TEST1_1B_PAT = "(DEBUG|INFO |WARN |ERROR|FATAL) root - Message \\d";

  static String TEST1_2_PAT = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2},\\d{3} "+
                        "\\[main]\\ (DEBUG|INFO|WARN|ERROR|FATAL) .* - Message \\d";



  Logger root; 
  Logger logger;

  public DOMTestCase(String name) {
    super(name);
  }

  public void setUp() {
    System.out.println("===================SETUP======================");
    root = Logger.getRootLogger();
    logger = Logger.getLogger(DOMTestCase.class);
  
  }

  public void test1() throws Exception {
    DOMConfigurator.configure("input/xml/DOMTestCase1.xml");
    common();

    ControlFilter cf1 = new ControlFilter(new String[]{TEST1_1A_PAT, TEST1_1B_PAT, 
					       EXCEPTION1, EXCEPTION2, EXCEPTION3});

    ControlFilter cf2 = new ControlFilter(new String[]{TEST1_2_PAT, 
					       EXCEPTION1, EXCEPTION2, EXCEPTION3});

    Transformer.transform(TEMP_A1, FILTERED_A1, new Filter[] {cf1, 
								new LineNumberFilter()});

    Transformer.transform(TEMP_A2, FILTERED_A2, new Filter[] {cf2, 
								new LineNumberFilter(),
								new ISO8601Filter()});

    assert(Compare.compare(FILTERED_A1, "witness/dom.A1.1"));
    assert(Compare.compare(FILTERED_A2, "witness/dom.A2.1"));
  }

  void common() {
    int i = -1;
 
    logger.debug("Message " + ++i);
    root.debug("Message " + i);        

    logger.info ("Message " + ++i);
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
    suite.addTest(new DOMTestCase("test1"));
    return suite;
  }

}
