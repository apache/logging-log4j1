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
import org.apache.log4j.helpers.IntializationUtil;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.joran.JoranConfigurator;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.RootLogger;


/**
 * @author Ceki G&uuml;lc&uuml;
 *
 */
public class FullCycleDBTest
       extends TestCase {
  
  Vector witnessEvents;
  LoggerRepository lrWrite;
  LoggerRepository lrRead;
  
  /*
   * @see TestCase#setUp()
   */
  protected void setUp()
         throws Exception {
    super.setUp();
    witnessEvents = new Vector();
    lrWrite = new Hierarchy(new RootLogger(Level.DEBUG));
    IntializationUtil.log4jInternalConfiguration(lrWrite);
    lrRead = new Hierarchy(new RootLogger(Level.DEBUG));
    IntializationUtil.log4jInternalConfiguration(lrRead);
  }


  /*
   * @see TestCase#tearDown()
   */
  protected void tearDown()
         throws Exception {
    super.tearDown();
    lrWrite.shutdown();
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
    jc1.doConfigure("input/db/writeCS1.xml", lrWrite);
  
    long startTime = System.currentTimeMillis();
    LogLog.info("***startTime is  "+startTime);
    
    // Write out just one log message
    Logger out = lrWrite.getLogger("testSingleOutput.out");
    out.debug("some message"+startTime);
    
    VectorAppender witnessAppender = (VectorAppender) lrWrite.getRootLogger().getAppender("VECTOR");
    witnessEvents = witnessAppender.getVector();
    assertEquals(1, witnessEvents.size());    
    
    // now read it back
    readBack("input/db/readCS1.xml", startTime);

  }

  public void testDataSource()
         throws Exception {

    LogLog.setInternalDebugging(true);
    JoranConfigurator jc1 = new JoranConfigurator();
    jc1.doConfigure("input/db/append-with-datasource1.xml", lrWrite);
  
    
    long startTime = System.currentTimeMillis();
    LogLog.info("startTime is  "+startTime);
    
    // Write out just one log message
    Logger out = lrWrite.getLogger("testSingleOutput.out");
    out.debug("some message"+startTime);

    VectorAppender witnessAppender = (VectorAppender) lrWrite.getRootLogger().getAppender("VECTOR");
    witnessEvents = witnessAppender.getVector();
    assertEquals(1, witnessEvents.size());    
    
    LogLog.info("----------------------------------------------");
    // now read it back
    readBack("input/db/read-with-datasource1.xml", startTime);
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
    jc1.doConfigure("input/db/writeCS1.xml", lrWrite);
  
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

    readBack("input/db/readCS1.xml", startTime);
  }


  void readBack(String configfile, long startTime) {
    // now read it back
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
      if(le.getProperties() == null || le.getProperties().size() == 0) {
        if(!(re.getProperties() == null || re.getProperties().size() == 0)) {
          fail("Returned event should have been empty");
        }
      }
      else {
        assertEquals(le.getProperties(), re.getProperties());
      }
      comprareStringArrays( le.getThrowableStrRep(),  re.getThrowableStrRep());
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
  

  Vector getRelevantEventsFromVA(VectorAppender va, long startTime) {
    assertNotNull(va);
    Vector v = va.getVector();
    Vector r = new Vector();
    // remove all elements older than startTime
    for(Iterator i = v.iterator(); i.hasNext(); ) {
      LoggingEvent event = (LoggingEvent) i.next();  
      if(startTime > event.getTimeStamp()) {
        LogLog.info("***Removing event with timestamp "+event.getTimeStamp());
      } else {
        LogLog.info("***Keeping event with timestamo"+event.getTimeStamp());
        r.add(event);
      }
    }
    return r;
  }
  
  
//  public void xtestJNDI()
//         throws Exception {
//    Hashtable env = new Hashtable();
//
//    env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.fscontext.RefFSContextFactory");
//    env.put(Context.PROVIDER_URL, "file:///home/jndi");
//
//    Context ctx = new InitialContext(env);
//
//    //ctx.addToEnvironment("toto", new Integer(1));
//    ctx.bind("toto", new Integer(1));
//  }
  
  public static Test suite() {
    TestSuite suite = new TestSuite();
    //suite.addTest(new FullCycleDBTest("test1"));
    //suite.addTest(new FullCycleDBTest("testSingleOutput"));
    suite.addTest(new FullCycleDBTest("testDataSource"));


    return suite;
  }
}
