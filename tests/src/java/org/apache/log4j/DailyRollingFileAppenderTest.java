/*
 * Created on May 28, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.log4j;

import org.apache.log4j.helpers.LogLog;

import junit.framework.TestCase;

/**
 * 
 * Test the original RollingFileAppender
 * 
 * @author Ceki Gulcu
 */
public class DailyRollingFileAppenderTest extends TestCase {
 
  protected void setUp() throws Exception {
    super.setUp();
  }
  
  protected void tearDown() throws Exception {
    super.tearDown();
  }
 
  public DailyRollingFileAppenderTest(String arg0) {
    super(arg0);
  }
  
  public void testWriteFooter() {
    LogLog.setInternalDebugging(true);
    DailyRollingFileAppender drfa = new DailyRollingFileAppender();
    PatternLayout patternLayout = new PatternLayout("%m%n");
    patternLayout.setHeader("BEGIN\r\n");
    patternLayout.setFooter("END\r\n");
    drfa.setFile("output/test");
    drfa.setDatePattern("'.'yyyy-MM-dd-HH-mm-ss'.log'");
  
    drfa.setLayout(patternLayout);
    drfa.activateOptions();
    Logger root = Logger.getRootLogger();
    root.addAppender(drfa);
    
    for(int i = 0; i < 2; i++) {
      try { Thread.sleep(1100); } catch (Exception e) {}
      root.debug("hello " + i);
    }    
    LogManager.shutdown();
  }
}
