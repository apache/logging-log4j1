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
public class RollingFileAppenderTest extends TestCase {
 
  protected void setUp() throws Exception {
    super.setUp();
  }
  
  protected void tearDown() throws Exception {
    super.tearDown();
  }
 
  public RollingFileAppenderTest(String arg0) {
    super(arg0);
  }
  
  public void testWriteFooter() {
    LogLog.setInternalDebugging(true);
    RollingFileAppender rfa = new RollingFileAppender();
    PatternLayout patternLayout = new PatternLayout("%m%n");
    patternLayout.setHeader("BEGIN\r\n");
    patternLayout.setFooter("END\r\n");
    rfa.setFile("output/test");
    rfa.setMaximumFileSize(9);
    rfa.setMaxBackupIndex(10);
    rfa.setLayout(patternLayout);
    rfa.activateOptions();
    Logger root = Logger.getRootLogger();
    root.addAppender(rfa);
    
    for(int i = 0; i < 2; i++) {
      root.debug("hello " + i);
    }    
    LogManager.shutdown();
  }
}
