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

package org.apache.log4j.db;

import org.apache.log4j.plugins.Pauseable;
import org.apache.log4j.plugins.Receiver;
import org.apache.log4j.scheduler.Scheduler;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.LoggerRepositoryEx;

/**
 *
 * @author Scott Deboy <sdeboy@apache.org>
 * @author Ceki G&uuml;lc&uuml;
 *
 */
public class DBReceiver extends Receiver implements Pauseable {
  /**
   * By default we refresh data every 1000 milliseconds.
   * @see #setRefreshMillis
   */
  static int DEFAULT_REFRESH_MILLIS = 1000;
  ConnectionSource connectionSource;
  int refreshMillis = DEFAULT_REFRESH_MILLIS;
  DBReceiverJob receiverJob;
  boolean paused = false;

  public void activateOptions() {
    
    if(connectionSource == null)  {
      throw new IllegalStateException(
        "DBAppender cannot function without a connection source");
    }
  
    receiverJob = new DBReceiverJob(this);
    receiverJob.setLoggerRepository(repository);
      
    if(this.repository == null) {
      throw new IllegalStateException(
      "DBAppender cannot function without a reference to its owning repository");
    }

    if (repository instanceof LoggerRepositoryEx) {
        Scheduler scheduler = ((LoggerRepositoryEx) repository).getScheduler();
    
        scheduler.schedule(
            receiverJob, System.currentTimeMillis() + 500, refreshMillis);
    }
   
  }

  public void setRefreshMillis(int refreshMillis) {
    this.refreshMillis = refreshMillis;
  }

  public int getRefreshMillis() {
    return refreshMillis;
  }


  /**
   * @return Returns the connectionSource.
   */
  public ConnectionSource getConnectionSource() {
    return connectionSource;
  }


  /**
   * @param connectionSource The connectionSource to set.
   */
  public void setConnectionSource(ConnectionSource connectionSource) {
    this.connectionSource = connectionSource;
  }


  /* (non-Javadoc)
   * @see org.apache.log4j.plugins.Plugin#shutdown()
   */
  public void shutdown() {
    getLogger().info("removing receiverJob from the Scheduler.");

    if(this.repository instanceof LoggerRepositoryEx) {
      Scheduler scheduler = ((LoggerRepositoryEx) repository).getScheduler();
      scheduler.delete(receiverJob);
    }
  }


  /* (non-Javadoc)
   * @see org.apache.log4j.plugins.Pauseable#setPaused(boolean)
   */
  public void setPaused(boolean paused) {
    this.paused = paused;
  }

  /* (non-Javadoc)
   * @see org.apache.log4j.plugins.Pauseable#isPaused()
   */
  public boolean isPaused() {
    return paused;
  }
}
