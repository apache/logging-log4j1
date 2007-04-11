/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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
 * <p/>
 * This implementation will work very well when the number of scheduled job is
 * small, say less than 100 jobs. If a larger number of events need to be
 * scheduled, than a better adapted data structure for the jobList can give
 * improved performance.
 *
 * @author Ceki
 */
public class Scheduler extends Thread {

    /**
     * Job list.
     */
    List jobList;
    /**
     * If set true, scheduler has or should shut down.
     */
    boolean shutdown = false;

    /**
     * Create new instance.
     */
    public Scheduler() {
        super();
        jobList = new Vector();
    }

    /**
     * Find the index of a given job.
     * @param job job
     * @return -1 if the job could not be found.
     */
    int findIndex(final Job job) {
        int size = jobList.size();
        boolean found = false;

        int i = 0;
        for (; i < size; i++) {
            ScheduledJobEntry se = (ScheduledJobEntry) jobList.get(i);
            if (se.job == job) {
                found = true;
                break;
            }
        }
        if (found) {
            return i;
        } else {
            return -1;
        }
    }

    /**
     * Delete the given job.
     * @param job job.
     * @return true if the job could be deleted, and
     * false if the job could not be found or if the Scheduler is about to
     * shutdown in which case deletions are not permitted.
     */
    public synchronized boolean delete(final Job job) {
        // if already shutdown in the process of shutdown, there is no
        // need to remove Jobs as they will never be executed.
        if (shutdown) {
            return false;
        }
        int i = findIndex(job);
        if (i != -1) {
            ScheduledJobEntry se = (ScheduledJobEntry) jobList.remove(i);
            if (se.job != job) { // this should never happen
                new IllegalStateException("Internal programming error");
            }
            // if the job is the first on the list,
            // then notify the scheduler thread to schedule a new job
            if (i == 0) {
                this.notifyAll();
            }
            return true;
        } else {
            return false;
        }
    }


    /**
     * Schedule a {@link Job} for execution at system time given by
     * the <code>desiredTime</code> parameter.
     * @param job job to schedule.
     * @param desiredTime desired time of execution.
     */
    public synchronized void schedule(final Job job,
                                      final long desiredTime) {
        schedule(new ScheduledJobEntry(job, desiredTime));
    }

    /**
     * Schedule a {@link Job} for execution at system time given by
     * the <code>desiredTime</code> parameter.
     * <p/>
     * The job will be rescheduled. It will execute with a frequency determined
     * by the period parameter.
     * @param job job to schedule.
     * @param desiredTime desired time of execution.
     * @param period repeat period.
     */
    public synchronized void schedule(final Job job,
                                      final long desiredTime,
                                      final long period) {
        schedule(new ScheduledJobEntry(job, desiredTime, period));
    }

    /**
     * Change the period of a job. The original job must exist for its period
     * to be changed.
     * <p/>
     * The method returns true if the period could be changed, and false
     * otherwise.
     * @param job job.
     * @param newPeriod new repeat period.
     * @return true if period could be changed.
     */
    public synchronized boolean changePeriod(final Job job,
                                             final long newPeriod) {
        if (newPeriod <= 0) {
            throw new IllegalArgumentException(
                    "Period must be an integer langer than zero");
        }

        int i = findIndex(job);
        if (i == -1) {
            return false;
        } else {
            ScheduledJobEntry se = (ScheduledJobEntry) jobList.get(i);
            se.period = newPeriod;
            return true;
        }
    }

    /**
     * Schedule a job.
     * @param newSJE new job entry.
     */
    private synchronized void schedule(final ScheduledJobEntry newSJE) {
        // disallow new jobs after shutdown
        if (shutdown) {
            return;
        }
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
        if (i == 0) {
            this.notifyAll();
        }
    }

    /**
     * Shut down scheduler.
     */
    public synchronized void shutdown() {
        shutdown = true;
    }

    /**
     * Run scheduler.
     */
    public synchronized void run() {
        while (!shutdown) {
            if (jobList.isEmpty()) {
                linger();
            } else {
                ScheduledJobEntry sje = (ScheduledJobEntry) jobList.get(0);
                long now = System.currentTimeMillis();
                if (now >= sje.desiredExecutionTime) {
                    executeInABox(sje.job);
                    jobList.remove(0);
                    if (sje.period > 0) {
                        sje.desiredExecutionTime = now + sje.period;
                        schedule(sje);
                    }
                } else {
                    linger(sje.desiredExecutionTime - now);
                }
            }
        }
        // clear out the job list to facilitate garbage collection
        jobList.clear();
        jobList = null;
        System.out.println("Leaving scheduler run method");
    }

    /**
     * We do not want a single failure to affect the whole scheduler.
     * @param job job to execute.
     */
    void executeInABox(final Job job) {
        try {
            job.execute();
        } catch (Exception e) {
            System.err.println("The execution of the job threw an exception");
            e.printStackTrace(System.err);
        }
    }

    /**
     * Wait for notification.
     */
    void linger() {
        try {
            while (jobList.isEmpty() && !shutdown) {
                this.wait();
            }
        } catch (InterruptedException ie) {
            shutdown = true;
        }
    }

    /**
     * Wait for notification or time to elapse.
     * @param timeToLinger time to linger.
     */
    void linger(final long timeToLinger) {
        try {
            this.wait(timeToLinger);
        } catch (InterruptedException ie) {
            shutdown = true;
        }
    }

    /**
     * Represents an entry in job scheduler.
     */
    static final class ScheduledJobEntry {
        /**
         * Desired execution time.
         */
        long desiredExecutionTime;
        /**
         * Job to run.
         */
        Job job;
        /**
         * Repeat period.
         */
        long period = 0;

        /**
         * Create new instance.
         * @param job job
         * @param desiredTime desired time.
         */
        ScheduledJobEntry(final Job job, final long desiredTime) {
            this(job, desiredTime, 0);
        }

        /**
         * Create new instance.
         * @param job job
         * @param desiredTime desired time
         * @param period repeat period
         */
        ScheduledJobEntry(final Job job,
                          final long desiredTime,
                          final long period) {
            super();
            this.desiredExecutionTime = desiredTime;
            this.job = job;
            this.period = period;
        }
    }

}


