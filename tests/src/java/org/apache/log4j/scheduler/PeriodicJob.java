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

import java.util.Vector;


class PeriodicJob extends CountingJob {
  long period;
  Vector desiredTimeVector;
  Vector actualExecutionTime;

  PeriodicJob(int id, long desiredExecutionTime, long period) {
    super(id, desiredExecutionTime);
    this.period = period;
    actualExecutionTime = new Vector();
    desiredTimeVector = new Vector();
    desiredTimeVector.add(new Long(desiredExecutionTime));
  }

  public void execute() {
    if (deleted) {
      throw new IllegalStateException(id + "has already been deleted");
    }

    long now = System.currentTimeMillis();
    count++;


    //System.out.println(
    //id + " - execute called: count" + count + ", now=" + now);
    long lastDesiredTime =
      ((Long) desiredTimeVector.lastElement()).longValue();
    desiredTimeVector.add(new Long(lastDesiredTime + period));
    actualExecutionTime.add(new Long(now));

    if (now < lastDesiredTime) {
      throw new IllegalStateException("Job executed too early.");
    } else if ((now - lastDesiredTime) > SchedulerTest.TOLERATED_GAP) {
      String msg =
        "Job id " + id + " executed " + (now - lastDesiredTime) + " too late ";
      System.out.println(msg);
      throw new IllegalStateException(msg);
    }
  }

  void sanityCheck(long currentTime) {
    System.out.println("sanity check on job " + id);

    if (!deleted) {
      int expectedNumberOfExecutions =
        (int) ((currentTime - desiredTime) / period);

      // allow for 15% error margin
      if ((actualExecutionTime.size()*1.15) < expectedNumberOfExecutions) {
        throw new IllegalStateException(
          "Too few executions. Was " + actualExecutionTime.size()
          + " expected " + expectedNumberOfExecutions + " period="+period);
      }
    }

    for (int i = 0; i < actualExecutionTime.size(); i++) {
      long actual = ((Long) actualExecutionTime.get(i)).longValue();
      long desired = ((Long) desiredTimeVector.get(i)).longValue();

      if (actual < desired) {
        throw new IllegalStateException("Job executed too early.");
      } else if ((actual - desired) > (SchedulerTest.TOLERATED_GAP * (i + 1))) {
        String msg =
          "Job id " + id + " executed " + (actual - desired) + " too late ";
        System.out.println(msg);
        throw new IllegalStateException(msg);
      }
    }
  }
}
