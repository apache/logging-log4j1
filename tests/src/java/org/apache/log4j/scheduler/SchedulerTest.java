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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.Iterator;
import java.util.Random;
import java.util.Vector;


/**
 * @author Ceki Gulcu
 *
 */
public class SchedulerTest extends TestCase {
  static final long TOLERATED_GAP = 1000;
  Random random = new Random(480361007);
  final long START_TIME = System.currentTimeMillis();
  
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
    CountingJob cj = new CountingJob(0, expected);
    scheduler.schedule(cj, expected);
    sleep(300);
    assertEquals(1, cj.count);
  }

  public void testMultipleEvents() {
    Vector jobs = new Vector();
    Scheduler scheduler = new Scheduler();
    scheduler.start();

    long now = System.currentTimeMillis();

    int loopLength = 100;

    for (int i = 0; i < loopLength; i++) {
      long expected = now + (i * 100);
      CountingJob cj = new CountingJob(i, expected);
      jobs.add(cj);
      scheduler.schedule(cj, expected);
    }

    sleep((100 * loopLength) + 200);

    for (Iterator i = jobs.iterator(); i.hasNext();) {
      CountingJob cj = (CountingJob) i.next();
      assertEquals(1, cj.count);
    }
  }

  public void testDelete() {
    Vector jobs = new Vector();
    Scheduler scheduler = new Scheduler();
    scheduler.start();

    long now = System.currentTimeMillis();

    long expected0 = now + 200;
    CountingJob cj0 = new CountingJob(0, expected0);
    long expected1 = expected0 + 200;
    CountingJob cj1 = new CountingJob(1, expected1);
    scheduler.schedule(cj0, expected0);
    scheduler.schedule(cj1, expected1);
    scheduler.delete(cj0);
    sleep(100 + (3 * 200));
    assertEquals(0, cj0.count);
    assertEquals(1, cj1.count);
  }

  /**
   * A test that inserts and deltes a large number of jobs at random
   */
  public void testRandom() {
    Scheduler scheduler = new Scheduler();
    scheduler.start();

    Vector jobVector = new Vector();
    Vector deletedVector = new Vector();

    // the approximative duration of this test in millisecs
    final int TEST_DURATION = 50000;

    // the frequncy of operations in millisecs
    final int OP_FREQUENCY = 10;

    // The number of times we will perform an operation on the scheduler
    final int MAX_OPS = TEST_DURATION / OP_FREQUENCY;

    long start = System.currentTimeMillis();

    for (long i = 0; i < MAX_OPS; i++) {
      if (shouldDelete() && !jobVector.isEmpty()) {
        int indexToDelete = getRandomIndexToDelete(jobVector.size());
        CountingJob j = (CountingJob) jobVector.remove(indexToDelete);
        if (j.count == 0) {
          scheduler.delete(j);
          deletedVector.add(j);
          if(j.count == 1) {
            fail("Error in the test code itself.");
          }
        }
      } else {
        long expected = start + random.nextInt(TEST_DURATION);
        CountingJob cj = new CountingJob((int) i, expected);
        jobVector.add(cj);
        scheduler.schedule(cj, expected);
      }
    }
    long loopEnd = System.currentTimeMillis();
    sleep(TEST_DURATION - (loopEnd - start) + 2000);

    if (deletedVector.size() > (MAX_OPS/2)) {
      fail("too many deleted jobs: " + deletedVector.size());
    }
     if (jobVector.size() < (MAX_OPS/2)) {
      fail("too few jobs: " + jobVector.size());
    }
    
    for (Iterator i = jobVector.iterator(); i.hasNext();) {
      CountingJob cj = (CountingJob) i.next();
      assertEquals(1, cj.count);
    }
    for (Iterator i = deletedVector.iterator(); i.hasNext();) {
      CountingJob cj = (CountingJob) i.next();
      assertEquals(0, cj.count);
    }
  }

  boolean shouldDelete() {
    int r = random.nextInt(2);

    if (r == 2) {
      return true;
    } else {
      return false;
    }
  }

  // On average, make the index of 1 out of 5 deletes zero
  int getRandomIndexToDelete(int range) {
     int r = random.nextInt(5);
     if(r == 0) {
       return 0;
     } else {
       return random.nextInt(range);
     }
  }

  void sleep(long duration) {
    try {
      Thread.sleep(duration);
    } catch (InterruptedException ie) {
    }
  }

  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(new SchedulerTest("testRandom"));
    return suite;
  }
  
  
  
  class CountingJob implements Job {
  int count = 0;
  int id;
  long scheduledTime;

  CountingJob(int id, long scheduledTime) {
    this.id = id;
    this.scheduledTime = scheduledTime;
  }

  public void execute() {
    long now = System.currentTimeMillis();
    count++;
    if (now < scheduledTime) {
      throw new IllegalStateException("Job executed too early.");
    } else if ((now - scheduledTime) > SchedulerTest.TOLERATED_GAP) {
      String msg =
        "Job id "+id+" executed " + (now - scheduledTime) + " too late "+
        "diff "+(scheduledTime-SchedulerTest.this.START_TIME);
        System.out.println(msg);
      throw new IllegalStateException(msg);
    }
  }
}
}




