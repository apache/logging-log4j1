/*
 * Copyright 1999,2006 The Apache Software Foundation.
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

import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.LoggerRepositoryEx;
import org.apache.log4j.scheduler.Job;

import java.io.*;

/**
 * Implements a watchdog to watch a file.  When the file changes, determined by
 * a change in the file's modification date, the contents of the file are use to
 * reconfigure the log4j environment.
 */
public class FileWatchdog extends WatchdogSkeleton implements Job {

  public static final long DEFAULT_INTERVAL = 60000;

  private String filePath;
  private long interval = DEFAULT_INTERVAL;
  private boolean initialConfigure = false;

  /** The file being watched. */
  private File watchedFile;
  private long lastModTime = -1;

  /**
   * Sets the path of the file to use and watch for configuration changes.
   *
   * @param filePath
   */
  public void setFile(String filePath) {
    this.filePath = filePath;
  }

  /**
   * Returns the path of the file being watched for confirguration changes.
   *
   * @return the path of the file being watched
   */
  public String getFile() {
    return filePath;
  }

  /**
   * Sets the interval of time between checks for file modifications.
   *
   * @param interval
   */
  public void setInterval(long interval) {
    this.interval = interval;
  }

  /**
   * Returns the interval of time between checks for file modifications.
   *
   * @return interval of time
   */
  public long getInterval() {
    return interval;
  }

  /**
   * Set to true if watchdog should configure with file before starting watch
   * in activateOptions.
   *
   * @param initialConfigure
   */
  public void setInitialConfigure(boolean initialConfigure) {
    this.initialConfigure = initialConfigure;
  }

  /**
   * Returns true if watchdog will configure before starting watch in
   * activateOptions.
   *
   * @return
   */
  public boolean getInitialConfigure() {
    return initialConfigure;
  }

  /**
   * Returns the last modification time of the watched file.
   * 
   * @return
   */
  public long getLastModTime() {
    return lastModTime;
  }

  /**
   * Sets up the reference to the file being watched, then calls the version
   * in the super class.
   */
  public void activateOptions() {
    getLogger().debug("activateOptions called for watchdog " + this.getName());

    if (filePath == null) {
      getLogger().error("watchdog \"{}\" not configured with path to watch",
        this.getName());
      return;
    }

    watchedFile = new File(filePath);

    // do an initial configure or record the current mod time
    if (initialConfigure) {
      execute();
    } else if (watchedFile.exists()) {
      lastModTime = watchedFile.lastModified();
    }

    LoggerRepository repo = getLoggerRepository();
    if (repo instanceof LoggerRepositoryEx) {
        ((LoggerRepositoryEx) repo).getScheduler().schedule(this,
      System.currentTimeMillis() + interval, interval);
    } else {
        this.getLogger().error("{} watchdog requires repository that supports LoggerRepositoryEx",
          this.getName());
    }
  }

    /**
   * Implements the Job interface for the Scheduler.  When this method is called
   * by the Scheduler it checks the current modification time of the watched
   * source with the last recorded modification time.  If the modification times
   * are different, then the log4j environment is reconfigured using the
   * watched source for the configuration data.
   */
  public void execute() {
    getLogger().debug("FileWatchdog \"{}\" executing", this.getName());
    if (watchedFile.exists()) {
      long curModTime = watchedFile.lastModified();
      getLogger().debug("Checking times for watchdog " + this.getName() +
        " :(lastModTime - " + lastModTime + ") ?? (curModTime - " +
        curModTime + ")");
      if (curModTime != lastModTime) {
        if (reconfigure()) {
          lastModTime = curModTime;
          getLogger().debug("Reconfiguration successful for watchdog " +
            this.getName());
        } else {
          getLogger().debug("Reconfiguration not successful for watchdog " +
            this.getName() + ", not updating mod time");
        }
      } else {
        getLogger().debug("Times matched, doing nothing");
      }
    } else {
      getLogger().debug("File does not exist, doing nothing");
    }
  }
  /**
   * Shutdown this watchdog.  Since implemented as a scheduled Job, this method
   * simply removes the watchdog from the Scheduler.
   */
  public void shutdown() {
    LoggerRepository repo = getLoggerRepository();
    if (repo instanceof LoggerRepositoryEx) {
        ((LoggerRepositoryEx) repo).getScheduler().delete(this);
    }
  }
  /**
   * Reconfigures the log4j environment using the file as the source of the
   * configuration data.
   */
  public boolean reconfigure() {
    InputStream stream = null;
    try {
      stream = new FileInputStream(watchedFile);
      return reconfigureByStream(stream);
    } catch (FileNotFoundException e) {
      return false;
    } finally {
      if (stream != null) {
        try {
          stream.close();
        } catch (IOException e2) {
          // ignore
        }
      }
    }
  }
}
