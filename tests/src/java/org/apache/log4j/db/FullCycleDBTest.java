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

import java.io.FileInputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import javax.naming.Context;
import javax.naming.InitialContext;

import junit.framework.TestCase;

import org.apache.log4j.Hierarchy;
import org.apache.log4j.LogManager;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
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
  /*
   * @see TestCase#setUp()
   */
  protected void setUp()
         throws Exception {
    super.setUp();
  }


  /*
   * @see TestCase#tearDown()
   */
  protected void tearDown()
         throws Exception {
    super.tearDown();
  }

  /**
   * Constructor for DBReeceiverTest.
   * @param arg0
   */
  public FullCycleDBTest(String arg0) {
    super(arg0);
  }

  
  /**
   * This test starts by writing a single element to a DB using DBAppender
   * and then reads it back using DBReceiver.
   * 
   * @throws Exception
   */
  public void testSingleOutput()
         throws Exception {
    LoggerRepository lrWrite = new Hierarchy(new RootLogger(Level.DEBUG));
    IntializationUtil.log4jInternalConfiguration(lrWrite);
    JoranConfigurator jc1 = new JoranConfigurator();
    jc1.doConfigure("input/db/writeCS1.xml", lrWrite);
  
    long startTime = System.currentTimeMillis();
    //LogLog.info("***start time is "+startTime);
    
    // Write out just one log message
    Logger out = lrWrite.getLogger("testSingleOutput");
    long sn = LoggingEvent.getSequenceCount();
    out.debug("test1");

    // now read it back
    LoggerRepository lrRead = new Hierarchy(new RootLogger(Level.DEBUG));
    IntializationUtil.log4jInternalConfiguration(lrRead);
    JoranConfigurator jc2 = new JoranConfigurator();
    jc2.doConfigure("input/db/readCS1.xml", lrRead);

    VectorAppender va = (VectorAppender) lrRead.getRootLogger().getAppender("VECTOR");
    try { Thread.sleep(1100); } catch(Exception e) {}
    Vector v = getCleanedVector(va, startTime);
    assertEquals(1, v.size());
    LoggingEvent eventBack = (LoggingEvent) v.get(0);
    assertEquals("testSingleOutput", eventBack.getLoggerName());
    if(eventBack.getTimeStamp() < startTime) {
      fail("Returned event cannot preceed start of the test");
    }
    assertEquals("testSingleOutput", eventBack.getLoggerName());
    assertEquals(sn, eventBack.getSequenceNumber());
    LogManager.getLoggerRepository().shutdown();
  }


  Vector getCleanedVector(VectorAppender va, long startTime) {
    assertNotNull(va);
    Vector v = va.getVector();
    // remove all elements older than startTime
    for(Iterator i = v.iterator(); i.hasNext(); ) {
      LoggingEvent event = (LoggingEvent) i.next();  
      if(startTime > event.getTimeStamp()) {
        LogLog.info("***Removing event with timestamp "+event.getTimeStamp());
        i.remove();
      } else {
        LogLog.info("***Keeping event with timestamo"+event.getTimeStamp());
      }
    }
    return v;
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
}
