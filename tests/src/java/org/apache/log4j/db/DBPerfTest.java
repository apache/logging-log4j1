/*
 * Created on May 21, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.log4j.db;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.joran.JoranConfigurator;

import junit.framework.TestCase;

/**
 * @author ceki
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DBPerfTest extends TestCase {
  final static Logger logger = Logger.getLogger(DBPerfTest.class);
  
  String appendConfigFile;
  
  public DBPerfTest(String arg0) {
    super(arg0);
  }

  
  protected void setUp() throws Exception {
    //appendConfigFile = System.getProperty("appendConfigFile");
    //assertNotNull("[appendConfigFile] property must be set for this test", appendConfigFile);
  }

  protected void tearDown() throws Exception {
    super.tearDown();
  }
  
  public void testLoop() {
    //appendConfigFile = "./input/db/append-with-drivermanager1.xml";
    appendConfigFile = "./input/db/append-with-pooled-datasource1.xml";
    //appendConfigFile = "./input/db/append-with-c3p0.xml";
    JoranConfigurator jc1 = new JoranConfigurator();
    jc1.doConfigure(appendConfigFile, LogManager.getLoggerRepository());
    
    long startTime = System.currentTimeMillis();
    int i;
    for(i = 0; i < 500; i++) {
      logger.debug("message "+i);
    }
    long endTime = System.currentTimeMillis();
    LogLog.info("writing "+i+" events took "+(endTime-startTime)+" millis.");
    LogLog.info("or "+(endTime-startTime)/i+" millis per event.");
  }
}
