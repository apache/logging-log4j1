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

import java.util.List;
import java.util.Vector;


/**
 * A simple but still useful implementation of a Scheduler (in memory only).
 * 
 * This implementation will work very well when the number of scheduled job is
 * small, say less than 100 jobs. If a larger number of events need to be
 * scheduled, than a better adapted data structure for the jobList can give
 * improved performance.
 * 
 * @author Ceki
 *
 */
public class Scheduler extends Thread {
  // 
  List jobList;
  boolean shutdown = false;
  
  public Scheduler() {
    jobList = new Vector();
  }

  /**
   * Find the index of a given job. Returns -1 if the job could not be found.
   * 
   */
  int findIndex(Job job) {
    int size = jobList.size();
    boolean found = false;

    int i = 0;
    for (; i < size; i++) {
      ScheduledJobEntry se = (ScheduledJobEntry) jobList.get(i);
      if(se.job == job) {
        found = true;
        break;
      }
    }
    if(found) {
      return i;
    } else {
      return -1;
    }
  }
  
  /**
   * Delete the given job. Returns true if the job could be deleted, and 
   * false if the job could not be found.
   */
  public synchronized boolean delete(Job job) {
    int i = findIndex(job);
    if(i != -1) {
      ScheduledJobEntry se = (ScheduledJobEntry) jobList.remove(i);
      if(se.job != job) { // this should never happen
        new IllegalStateException("Internal programming error");
      }
      // if the job is the first on the list, then notify the scheduler thread
      // to schedule a new job
      if(i == 0) {
        this.notify();
      }
      return true;
    } else {
      return false;
    }
  }


  
  /**
   * Schedule a {@link Job} for execution at system time given by
   * the <code>desiredTime</code> parameter.
   */
  public synchronized void schedule(Job job, long desiredTime) {
    schedule(new ScheduledJobEntry(job, desiredTime));
  }
  
  /**
   * Schedule a {@link Job} for execution at system time given by
   * the <code>desiredTime</code> parameter.
   *
   * The job will be rescheduled. It will execute with a frequency determined
   * by the period parameter.
   */
  public synchronized void schedule(Job job, long desiredTime, long period) {
    schedule(new ScheduledJobEntry(job, desiredTime, period));
  }

  /**
   * Change the period of a job. The original job must exist for its period
   * to be changed. 
   *
   * The method returns true if the period could be changes, and false
   * otherwise.
   */
  public synchronized boolean changePeriod(Job job, long newPeriod) {
     if(newPeriod <= 0) {
       throw new IllegalArgumentException("Period must be an integer langer than zero");
     }
     
     int i = findIndex(job);
     if(i == -1) {
       return false;
     } else {
        ScheduledJobEntry se = (ScheduledJobEntry) jobList.get(i);
        se.period = newPeriod;
        return true;
     }
  }
    
  private synchronized void schedule(ScheduledJobEntry newSJE) {
     int max = jobList.size();
     long desiredExecutionTime = newSJE.desiredExecutionTime;
     
    // find the index i such that timeInMillis < jobList[i]
    int i = 0;
    for (; i < max; i++) {
       
      ScheduledJobEntry sje = (ScheduledJobEntry) jobList.get(i);

      if (desiredExecutionTime < sje.desiredExecutionTime) {
        break;
      }
    }
    jobList.add(i, newSJE);
    // if the jobList was empty, then notify the scheduler thread
    if(i == 0) {
      this.notify();
    }
  }
  
  public void shutdown() {
    shutdown = true;
  }
  
  public synchronized void run() {
    while (!shutdown) {
      if (jobList.isEmpty()) {
        linger();
      } else {
        ScheduledJobEntry sje = (ScheduledJobEntry) jobList.get(0);
        long now = System.currentTimeMillis();
        if(now >= sje.desiredExecutionTime) {
          sje.job.execute();
          jobList.remove(0);
          if(sje.period > 0) {
            sje.desiredExecutionTime = now + sje.period;
            schedule(sje);
          }
        } else {
          linger(sje.desiredExecutionTime - now);
        }
      }
    }
    System.out.println("Leaving scheduler run method");
  }
  
  void linger() {
    try {
      this.wait();
     } catch (InterruptedException ie) {
       shutdown = true;
     }
  }
  
   void linger(long timeToLinger) {
    try {
      this.wait(timeToLinger);
     } catch (InterruptedException ie) {
       shutdown = true;
     }
  }
}



class ScheduledJobEntry {
  long desiredExecutionTime;
  Job job;
  long period = 0;
  
  ScheduledJobEntry(Job job, long desiredTime) {
    this(job, desiredTime, 0);
  }
  ScheduledJobEntry(Job job, long desiredTime, long period) {
    this.desiredExecutionTime = desiredTime;
    this.job = job;
    this.period = period;
  }
}
