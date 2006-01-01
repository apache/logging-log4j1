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
package org.apache.log4j.db;

import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.Hierarchy;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.log4j.VectorAppender;
import org.apache.log4j.helpers.Constants;
import org.apache.log4j.joran.JoranConfigurator;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.RootLogger;
import org.apache.log4j.spi.LocationInfo;


/**
 * This test case writes a few events into a databases and reads them
 * back comparing the event written and read back.
 * 
 * <p>It relies heavily on the proper configuration of its environment
 * in joran config files as well system properties.
 * </p>
 * 
 * <p>See also the Ant build file in the tests/ directory.</p> 
 * 
 * @author Ceki G&uuml;lc&uuml
 */
public class FullCycleDBTest
       extends TestCase {
  
  Vector witnessEvents;
  LoggerRepository lrWrite;
  LoggerRepository lrRead;
  String appendConfigFile = null;
  String readConfigFile = null;
  
  
  /*
   * @see TestCase#setUp()
   */
  protected void setUp()
         throws Exception {
    super.setUp();
    appendConfigFile = System.getProperty("appendConfigFile");
    assertNotNull("[appendConfigFile] property must be set for this test", appendConfigFile);
    readConfigFile = System.getProperty("readConfigFile");
    assertNotNull("[readConfigFile] property must be set for this test", readConfigFile);
    
    witnessEvents = new Vector();
    lrWrite = new Hierarchy(new RootLogger(Level.DEBUG));
    lrWrite.setName("lrWrite");
    lrRead = new Hierarchy(new RootLogger(Level.DEBUG));
    lrRead.setName("lrRead");
  }


  /*
   * @see TestCase#tearDown()
   */
  protected void tearDown()
         throws Exception {
    super.tearDown();
    lrRead.shutdown();
    witnessEvents = null;
  }

  /**
   * Constructor for DBReeceiverTest.
   * @param arg0
   */
  public FullCycleDBTest(String arg0) {
    super(arg0);
  }

  
  /**
   * This test starts by writing a single event to a DB using DBAppender
   * and then reads it back using DBReceiver.
   * 
   * DB related information is specified within the configuration files.
   * @throws Exception
   */
  public void testSingleOutput()
         throws Exception {
    JoranConfigurator jc1 = new JoranConfigurator();
    jc1.doConfigure(appendConfigFile, lrWrite);
  
    long startTime = System.currentTimeMillis();
    System.out.println("***startTime is  "+startTime);
    
    // Write out just one log message
    Logger out = lrWrite.getLogger("testSingleOutput.out");
    out.debug("some message"+startTime);

    VectorAppender witnessAppender = (VectorAppender) lrWrite.getRootLogger().getAppender("VECTOR");
    witnessEvents = witnessAppender.getVector();
    assertEquals(1, witnessEvents.size());    

    // We have to close all appenders before starting to read
    lrWrite.shutdown();

    // now read it back
    readBack(readConfigFile, startTime);

  }

  /**
   * This test starts by writing a single event to a DB using DBAppender
   * and then reads it back using DBReceiver.
   * 
   * The written event includes MDC and repository properties as well as
   * exception info.
   * 
   * DB related information is specified within the configuration files.
   * @throws Exception
   */
  public void testAllFields() {
    JoranConfigurator jc1 = new JoranConfigurator();
    jc1.doConfigure(appendConfigFile, lrWrite);
  
    long startTime = System.currentTimeMillis();
    
    // Write out just one log message
    lrWrite.setProperty("key1", "value1-"+startTime);
    MDC.put("key2", "value2-"+startTime);
    Map mdcMap = MDC.getContext();
//    LogLog.info("**********"+mdcMap.size());
    
    // Write out just one log message
    Logger out = lrWrite.getLogger("out"+startTime);
    long sn = LoggingEvent.getSequenceCount();

    out.debug("some message"+startTime);
    MDC.put("key3", "value2-"+startTime);
    out.error("some error message"+startTime, new Exception("testing"));
    
    // we clear the MDC to avoid interference with the events read back from
    // the db
    MDC.clear();

    VectorAppender witnessAppender = (VectorAppender) lrWrite.getRootLogger().getAppender("VECTOR");
    witnessEvents = witnessAppender.getVector();
    assertEquals(2, witnessEvents.size());    

    // We have to close all appenders just before starting to read
    lrWrite.shutdown();
    
    readBack(readConfigFile, startTime);
  }


  void readBack(String configfile, long startTime) {
    JoranConfigurator jc2 = new JoranConfigurator();
    jc2.doConfigure(configfile, lrRead);
    
    // wait a little to allow events to be read
    try { Thread.sleep(3100); } catch(Exception e) {}
    VectorAppender va = (VectorAppender) lrRead.getRootLogger().getAppender("VECTOR");
    Vector returnedEvents = getRelevantEventsFromVA(va, startTime);
    
    compareEvents(witnessEvents, returnedEvents);
    
  }
  
  void compareEvents(Vector l, Vector r) {
    assertNotNull("left vector of events should not be null");
    assertEquals(l.size(), r.size());
    
    for(int i = 0; i < r.size(); i++) {
      LoggingEvent le = (LoggingEvent) l.get(i);
      LoggingEvent re = (LoggingEvent) r.get(i);
      assertEquals(le.getMessage(),        re.getMessage());
      assertEquals(le.getSequenceNumber(), re.getSequenceNumber());
      assertEquals(le.getLoggerName(),     re.getLoggerName());
      assertEquals(le.getLevel(),          re.getLevel());
      assertEquals(le.getThreadName(), re.getThreadName());
      if(re.getTimeStamp() < le.getTimeStamp()) {
        fail("Returned event cannot preceed witness timestamp");
      }
      
      if((re.getProperties() != null) && re.getProperties().containsKey(Constants.LOG4J_ID_KEY)) {
        re.getProperties().remove(Constants.LOG4J_ID_KEY);
      }
      
      if(le.getProperties() == null || le.getProperties().size() == 0) {
        if(!(re.getProperties() == null || re.getProperties().size() == 0)) {
          System.out.println("properties are "+re.getProperties());
          fail("Returned event should have been empty");
        }
      } else {
        assertEquals(le.getProperties(), re.getProperties());
      }
      comprareStringArrays( le.getThrowableStrRep(),  re.getThrowableStrRep());
      compareLocationInfo(le, re);
    } 
  }
  
  void comprareStringArrays(String[] la, String[] ra) {
    if((la == null) && (ra == null)) {
      return;
    }
    assertEquals(la.length, ra.length);
    for(int i = 0; i < la.length; i++) {
      assertEquals(la[i], ra[i]);
    }
  }
  
  void compareLocationInfo(LoggingEvent l, LoggingEvent r) {
    if(l.locationInformationExists()) {
      assertEquals(l.getLocationInformation(), r.getLocationInformation());
    } else {
      assertEquals(LocationInfo.NA_LOCATION_INFO, r.getLocationInformation());
    }
  }
  
  Vector getRelevantEventsFromVA(VectorAppender va, long startTime) {
    assertNotNull(va);
    Vector v = va.getVector();
    Vector r = new Vector();
    // remove all elements older than startTime
    for(Iterator i = v.iterator(); i.hasNext(); ) {
      LoggingEvent event = (LoggingEvent) i.next();  
      if(startTime > event.getTimeStamp()) {
        System.out.println("***Removing event with timestamp "+event.getTimeStamp());
      } else {
        System.out.println("***Keeping event with timestamo"+event.getTimeStamp());
        r.add(event);
      }
    }
    return r;
  }

  void dump(Vector v) {
    for(int i = 0; i < v.size(); i++) {
      LoggingEvent le = (LoggingEvent) v.get(i);
      System.out.println("---"+le.getLevel()+" "+le.getLoggerName()+" "+le.getMessage());
    }
  }
  
  public static Test XXsuite() {
    TestSuite suite = new TestSuite();
    suite.addTest(new FullCycleDBTest("testSingleOutput"));
    suite.addTest(new FullCycleDBTest("testAllFields"));
    return suite;
  }
}
