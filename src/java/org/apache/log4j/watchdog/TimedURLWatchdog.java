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

package org.apache.log4j.watchdog;

import java.net.URL;

import org.apache.log4j.scheduler.Job;
import org.apache.log4j.watchdog.WatchdogSkeleton;

/**
  Implements functionality of a watchdog that periodically checks a URL for
  updated configuration data.
  
  This class can be used as the base class for any Watchdog that needs to
  watch a single URL for changes.  Subclasses must implement the
  getModificationTime method to return the current modification time of the
  watched source (URL), since this can be source specific.  When a change in
  modification time is detected, the log4j environment will be reconfigured,
  using the URL as the source for the new configuration data.
  
  The URL will be checked peridoically.  This period of time is defined by the
  <code>interval</code> property.

  @author Mark Womack <mwomack@apache.org>
  @since 1.3
*/
public abstract class TimedURLWatchdog extends WatchdogSkeleton implements Job {
  
  /** Default interval of time between checks, in milliseconds. */
  public static long DEFAULT_INTERVAL = 60000;
  
  /** The url to watch. */
  protected URL watchedURL;
  
  /** The interval of time between checks. */
  protected long interval = DEFAULT_INTERVAL;
  
  /** The last time the url was modified. */
  private long lastModTime = -1;
  
  /**
   * Set the URL to watch.
   *
   * @param urlToWatch The URL to watch.
   */
  public void setURL(URL urlToWatch) {
    this.watchedURL = urlToWatch;
  }
  
  /**
   * Returns the URL that will be watched.
   *
   * @return The URL being watched.
   */
  public URL getURL() {
    return watchedURL;
  }
  
  /**
   * Sets the interval of time, in milliseconds, between checks on the
   * URL.
   *
   * @param interval An interval of time, in milliseconds.
   */
  public void setInterval(long interval) {
    this.interval = interval;
  }
  
  /**
   * Returns the interval of time, in milliseconds, between checks on the URL.
   *
   * @return An interval of time, in milliseconds.
   */
  public long getInterval() {
    return interval;
  }
  
  /**
   * Reconfigures the log4j environment using the URL as the source of the
   * configuration data.
   */
  public void reconfigure() {
    reconfigureByURL(watchedURL);
  }
  
  /**
   * Returns the current modification time for the watched URL.  Subclasses
   * must implement specifically for the type of source they are watching.
   *
   * @return The current modification time of the URL.
   */
  public abstract long getModificationTime();
  
  /**
   * Implements the Job interface for the Scheduler.  When this method is called
   * by the Scheduler it checks the current modification time of the watched
   * source with the last recorded modification time.  If the modification times
   * are different, then the log4j environment is reconfigured using the
   * watched source for the configuration data.
   */
  public void execute() {
    long newModTime = getModificationTime();
    
    if (lastModTime != newModTime) {
      reconfigure();
      lastModTime = newModTime;
    }
  }
  
  /**
   * Called to activate the watchdog and start the watching of the source.
   */
  public void activateOptions() {
    if (watchedURL == null) {
      this.getLogger().error("{} watchdog not configured with URL to watch",
        this.getName());
      return;
    }
    
    // get the current modification time of the watched source
    lastModTime = getModificationTime();
    
    // schedule this Wathdog as a Job with the Scheduler
    getLoggerRepository().getScheduler().schedule(this,
      System.currentTimeMillis() + interval, interval);
  }
  
  /**
   * Shutdown this watchdog.  Since implemented as a scheduled Job, this method
   * simply removes the watchdog from the Scheduler.
   */
  public void shutdown() {
    getLoggerRepository().getScheduler().delete(this);
  }
}
