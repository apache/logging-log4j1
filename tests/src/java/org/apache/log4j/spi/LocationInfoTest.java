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

package org.apache.log4j.spi;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import junit.framework.TestCase;

/**
 * 
 * Very simple test verifying that LocationInfo extraction works, at least in
 * simple cases.
 * 
 * @author Ceki
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
    
    
    LoggingEvent le = new LoggingEvent("org.apache.log4j.spi.LoggingEvent", logger, Level.DEBUG,
        "toto", null);
 
    // line extraction is done from on line 74 (following line)
    LocationInfo li = le.getLocationInformation();
    
    if(li == LocationInfo.NA_LOCATION_INFO) {
      fail("For regular events, location info should not be LocationInfo.NA_LOCATION_INFO ");
    }
    
    assertEquals(this.getClass().getName(), li.getClassName()); 
    assertEquals("LocationInfoTest.java", li.getFileName()); 
    assertEquals("74", li.getLineNumber()); 
    assertEquals("testEqualsObject", li.getMethodName()); 
    
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
