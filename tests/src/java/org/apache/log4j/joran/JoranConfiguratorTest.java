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

package org.apache.log4j.joran;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.util.Compare;
import org.apache.log4j.util.ControlFilter;
import org.apache.log4j.util.Filter;
import org.apache.log4j.util.JunitTestRunnerFilter;
import org.apache.log4j.util.LineNumberFilter;
import org.apache.log4j.util.SunReflectFilter;
import org.apache.log4j.util.Transformer;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Various tests verifying that JoranConfigurator can effectively
 * parse config files.
 * 
 * @author ceki
 *
 */
public class JoranConfiguratorTest extends TestCase {
  Logger root; 
  Logger logger;

  public final static String FILTERED = "output/filtered";
  public final static String TEMP = "output/temp";
  
  static String TEST1_PAT = "(DEBUG|INFO|WARN|ERROR|FATAL) - Message \\d";
  static String EXCEPTION1 = "java.lang.Exception: Just testing";
  static String EXCEPTION2 = "\\s*at .*\\(.*:\\d{1,4}\\)";
  static String EXCEPTION3 = "\\s*at .*\\(Native Method\\)";
  
  public JoranConfiguratorTest(String name) {
    super(name);
  }

  public void setUp() {
    root = Logger.getRootLogger();
    logger = Logger.getLogger(JoranConfiguratorTest.class);
  }
 
  public void tearDown() {  
    root.getLoggerRepository().resetConfiguration();
  }
  
  
  public void test1() {
    JoranConfigurator jc = new JoranConfigurator();
    jc.doConfigure("./input/joran/simple2.xml", LogManager.getLoggerRepository());
    
    List errorList = jc.getExecutionContext().getErrorList();
    for(int i = 0; i < errorList.size(); i++) {
      System.out.println(errorList.get(i));
    }
  }
  
  public void testAsync() throws Exception {
    JoranConfigurator joc = new JoranConfigurator();
    joc.doConfigure("./input/joran/asyncTest.xml", LogManager.getLoggerRepository());
    joc.dumpErrors();
    common();
    
    // allow time for the Aync thread to do its job
    Thread.sleep(2000);
    
    ControlFilter cf = new ControlFilter(new String[]{TEST1_PAT, EXCEPTION1, EXCEPTION2, EXCEPTION3});
    

    Transformer.transform(TEMP, FILTERED, new Filter[] {cf, 
        new LineNumberFilter(), 
        new SunReflectFilter(), 
        new JunitTestRunnerFilter()});

     assertTrue(Compare.compare(FILTERED, "witness/joran/async"));
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
    suite.addTest(new JoranConfiguratorTest("testAsync"));
    return suite;
   }

}
