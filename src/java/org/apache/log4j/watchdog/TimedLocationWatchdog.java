package org.apache.log4j.watchdog;

import org.apache.log4j.scheduler.Job;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.LoggerRepositoryEx;

/**
 * Implements functionality of a watchdog that periodically checks a location
 * for updated configuration data.
 *
 * This class can be used as the base class for any Watchdog that needs to
 * watch a single location (File or URL) for changes.  Subclasses must implement
 * the getModificationTime method to return the current modification time of the
 * watched source, since this can be source specific.  When a change in
 * modification time is detected, the log4j environment will be reconfigured,
 * using the location as the source for the new configuration data.  Subclasses
 * must implement the reconfigure method to use the specific source for
 * reconfiguration.
 *
 * The location will be checked peridoically.  This period of time is defined
 * by the <code>interval</code> property.
 *
 * @author Mark Womack <mwomack@apache.org>
 * @since 1.3
 */
public abstract class TimedLocationWatchdog  extends WatchdogSkeleton
    implements Job {

  /** Default interval of time between checks, in milliseconds. */
  public static long DEFAULT_INTERVAL = 60000;

  /** The interval of time between checks. */
  protected long interval = DEFAULT_INTERVAL;

  /** The last time the location was modified. */
  private long lastModTime = -1;

  /**
   * Sets the interval of time, in milliseconds, between checks on the
   * location.
   *
   * @param interval An interval of time, in milliseconds.
   */
  public void setInterval(long interval) {
    this.interval = interval;
  }

  /**
   * Returns the interval of time, in milliseconds, between checks on the
   * location.
   *
   * @return An interval of time, in milliseconds.
   */
  public long getInterval() {
    return interval;
  }

  /**
   * Returns the current modification time for the watched location.  Subclasses
   * must implement specifically for the type of source they are watching.
   *
   * @return The current modification time of the location.
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
    // get the current modification time of the watched source
    lastModTime = getModificationTime();

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
   * Shutdown this watchdog.  Since implemented as a scheduled Job, this method
   * simply removes the watchdog from the Scheduler.
   */
  public void shutdown() {
    LoggerRepository repo = getLoggerRepository();
    if (repo instanceof LoggerRepositoryEx) {
        ((LoggerRepositoryEx) repo).getScheduler().delete(this);
    }
  }
}
