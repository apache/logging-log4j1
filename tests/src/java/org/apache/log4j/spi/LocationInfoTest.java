/*
 * Created on Dec 6, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.log4j.spi;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import junit.framework.TestCase;

/**
 * @author ceki
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LocationInfoTest extends TestCase {

  Logger logger = Logger.getLogger(LocationInfoTest.class);
  
  /*
   * @see TestCase#setUp()
   */
  protected void setUp() throws Exception {
    super.setUp();
    BasicConfigurator.configure();
  }

  /*
   * @see TestCase#tearDown()
   */
  protected void tearDown() throws Exception {
    super.tearDown();
    LogManager.shutdown();
  }

  /**
   * Constructor for LocationInfoTest.
   * @param arg0
   */
  public LocationInfoTest(String arg0) {
    super(arg0);
  }

  /*
   * Class to test for boolean equals(Object)
   */
  public void testEqualsObject() {
    
    Throwable t1 = new Throwable();
    Throwable t2 = new Throwable();
    
    LoggingEvent le = new LoggingEvent();
    le.setLogger(logger);
    le.setTimeStamp(System.currentTimeMillis());
    le.setLevel(Level.DEBUG);
    le.setMessage("toto");
    
    
    LocationInfo l1 = le.getLocationInformation();
  
    logger.debug("classname: "+ l1.getClassName()); 
    logger.debug("filename: "+ l1.getFileName()); 
    logger.debug("classname: "+ l1.getClassName()); 
     logger.debug("filename: "+ l1.getFileName()); 
    
    //logger.error("toto", t1);
    
    /*ThrowableInformation te1 = new ThrowableInformation(e1);
    ThrowableInformation te2 = new ThrowableInformation(e2);
    
    assertEquals(te1, te1);
    assertEquals(te2, te2);
    
    boolean eq1 = te1.equals(te2);
    assertEquals(false, eq1);

    boolean eq2 = te1.equals(null);
    assertEquals(false, eq2);
    */
  }

}
