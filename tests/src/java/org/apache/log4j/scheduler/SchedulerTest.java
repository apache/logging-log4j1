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

package org.apache.log4j.scheduler;

import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import junit.framework.TestCase;


/**
 * @author Ceki Gulcu
 *
 */
public class SchedulerTest extends TestCase {
  static final long TOLERATED_GAP = 50;
  
  public SchedulerTest(String arg0) {
    super(arg0);
  }

  protected void setUp() throws Exception {
    super.setUp();
  }

  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void testBasic() {
    Scheduler scheduler = new Scheduler();
    scheduler.start();
    long now = System.currentTimeMillis();
    long expected = now + 100;
    CountingJob cj = new CountingJob(expected);
    scheduler.schedule(cj, new Date(expected));
    sleep(300);
    assertEquals(1, cj.count);
  }
  
  public void testMultipleEvent() {
    Vector jobs = new Vector();
    Scheduler scheduler = new Scheduler();
    scheduler.start();
    long now = System.currentTimeMillis();
    
    for(int i = 0; i < 40; i++) {
      long expected = now + i*100;
      CountingJob cj = new CountingJob(expected);
      jobs.add(cj);
      scheduler.schedule(cj, new Date(expected));
    }
    
    sleep(100*40+200);
    for(Iterator i = jobs.iterator(); i.hasNext();) {
      CountingJob cj = (CountingJob) i.next();
      assertEquals(1, cj.count);
    }  
  }
  
  
  void sleep(long duration) {
    try {
      Thread.sleep(duration);
    } catch(InterruptedException ie) {
   }
  }
}


class CountingJob implements Job {

  int count = 0; 
  long scheduledTime;
  CountingJob(long scheduledTime) {
    this.scheduledTime = scheduledTime;
  }
  
  public void execute() {
    long now = System.currentTimeMillis();
    if(now < scheduledTime) {
     throw new IllegalStateException("Job executed too early.");
    } else if((now - scheduledTime) > SchedulerTest.TOLERATED_GAP) {
      throw new IllegalStateException("Job executed too late");    
    }
    count++;    
  }
}