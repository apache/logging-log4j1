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

/*
 * Created on Apr 19, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.log4j.scheduler;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;


/**
 * @author ceki
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Scheduler extends Thread {
  // 
  List jobList;
  boolean interrupted;
  long key;
  
  public Scheduler() {
    jobList = new Vector();
  }

  public synchronized void delete(Job job) {
    int size = jobList.size();
    boolean found = false;
    // find the index i such that 
    int i = 0;

    for (; i < size; i++) {
      ScheduledJobEntry se = (ScheduledJobEntry) jobList.get(i);
      if(se.job == job) {
        found = true;
        break;
      }
    }
    
    if(found) {
      ScheduledJobEntry se = (ScheduledJobEntry) jobList.remove(i);
      if(se.job != job) {
        new IllegalStateException();
      }
      // if the job is the first on the list, then notify the scheduler thread
      // to schedule a new job
      if(i == 0) {
        this.notify();
      }
    } else {
      throw new NoSuchElementException();
    }
  }
  
  public synchronized void schedule(Job job, long expectedTime) {
   

    int size = jobList.size();

    // find the index i such that timeInMillis < jobList[i]
    int i = 0;

    for (; i < size; i++) {
      ScheduledJobEntry se = (ScheduledJobEntry) jobList.get(i);

      if (expectedTime < se.timeInMillis) {
        break;
      }
    }
    jobList.add(i, new ScheduledJobEntry(job, expectedTime));
    // if the jobList was empty, then notify the scheduler thread
    if(i == 0) {
      this.notify();
    }
  }

  public synchronized void run() {
    while (true) {
      if (jobList.isEmpty()) {
        linger();
      } else {
        ScheduledJobEntry se = (ScheduledJobEntry) jobList.get(0);
        long now = System.currentTimeMillis();
        if(now >= se.timeInMillis) {
          se.job.execute();
          jobList.remove(0);
        } else {
          linger(se.timeInMillis - now);
        }
      }
    }
  }
  
  void linger() {
    try {
      this.wait();
     } catch (InterruptedException ie) {
       interrupted = true;
     }
  }
  
   void linger(long timeToLinger) {
    try {
      this.wait(timeToLinger);
     } catch (InterruptedException ie) {
       interrupted = true;
     }
  }
}



class ScheduledJobEntry {
  long timeInMillis;
  Job job;
  
  ScheduledJobEntry(Job job, long timeInMillis) {
    this.timeInMillis = timeInMillis;
    this.job = job;
  }
}
