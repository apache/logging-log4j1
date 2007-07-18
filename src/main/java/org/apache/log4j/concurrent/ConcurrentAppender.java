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

package org.apache.log4j.concurrent;

import org.apache.log4j.Layout;
import org.apache.log4j.Appender;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.ComponentBase;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.OptionHandler;

/**
 * Base class for appenders that can benefit from a concurrency strategy.
 * Classes derived from this appender may have the {@link #append} method
 * called by multiple threads.  Derived classes must also override {@link
 * #internalClose}.
 * <p>
 * Locking strategy:  Internally, there is a read-write lock to handle
 * concurrent modification.  A <i>write</i> lock is obtained to change states
 * (including before {@link #close}.)  A <i>read</i> lock is obtained to read
 * options.  Subclasses interested in state may check state using a public
 * method or within their own {@link #append} method.
 * </p>
 * <p>
 * If Thread.interrupt() is called during append of the logger thread, 
 * the customary behavior in this package is to stop appending.
 * </p>
 * <p>
 * This class is heavily based on the {@link
 * #org.apache.log4j.AppenderSkeleton} class.  It may be a useful base class
 * for creating appenders that can benefit from concurrent I/O access.
 * </p>
 * 
 * @author Elias Ross
 */
public abstract class ConcurrentAppender 
  extends ComponentBase implements Appender, OptionHandler
{
  
  /**
   * The layout variable does not need to be set if the appender
   * implementation has its own layout.
   */
  private Layout layout;

  /**
   * The name of this appender.
   */
  protected String name;

  /**
   * There is no level threshold filtering by default.
   */
  private volatile Priority threshold;

  /**
   * Internal class, internally locked.
   */
  private FilterChain filters = new FilterChain();

  /**
   * Is this appender closed?
   */
  private SynchronizedBoolean closed = new SynchronizedBoolean(false);

  /**
   * Set to true when the appender is activated.
   * Subclasses can set this to false to indicate things are not in order.
   */
  protected SynchronizedBoolean active = new SynchronizedBoolean(false);
  
  /**
   * The guard prevents an appender from repeatedly calling its own doAppend
   * method.  This prevents same-thread re-entry looping.
   */
  private ThreadLocal guard = new ThreadLocal();

  /**
   * A write lock is obtained to change options, a read lock is obtained to
   * append events.
   * This is a re-entrant writer-preference read-write lock.
   */
  protected ReadWriteLock lock = new ReentrantWriterPreferenceReadWriteLock();

  /**
   * Constructs a ConcurrentAppender.
   *
   * @param isActive true if appender is ready for use upon construction.
   */
  protected ConcurrentAppender(final boolean isActive) {
    active.set(isActive);
  }

  /**
   * Derived appenders should override this method if option structure
   * requires it.
   * By default, sets {@link #active} to true.
   */
  public void activateOptions() {
    active.set(true);
  }

  /**
   * Indicates if the appender is active and not closed.
   */
  public boolean isActive() {
    return active.get() && !closed.get();
  }

  /**
   * Adds a filter to end of the filter list.
   * @param filter filter to use; cannot be null
   */
  public void addFilter(Filter filter) {
    filters.addFilter(filter);
  }

  /**
   * Clears the filters chain.
   */
  public void clearFilters() {
    filters.clear();
  }

  /**
   * Returns the first {@link Filter}.
   */
  public Filter getFilter() {
    return filters.getHead();
  }

  /**
   * Returns the layout of this appender. May return null if not set.
   */
  public Layout getLayout() {
    return this.layout;
  }

  /**
   * Returns the name of this appender.
   */
  public final String getName() {
    return this.name;
  }

  /**
   * Returns this appender's threshold level. See the {@link #setThreshold}
   * method for the meaning of this option.
   */
  public Priority getThreshold() {
    return threshold;
  }

  /**
   * Returns true if the message level is below the appender's threshold. If
   * there is no threshold set, returns <code>true</code>.
   */
  public boolean isAsSevereAsThreshold(final Priority level) {
    Priority copy = threshold;
    return ((copy == null) || level.isGreaterOrEqual(copy));
  }

  /**
   * Performs threshold checks and checks filters before delegating actual
   * logging to the subclasses specific {@link #append} method.
   * This implementation also checks if this thread already is logging using
   * this appender, preventing possible stack overflow.
   */
  public final void doAppend(LoggingEvent event) {

    if (!isAsSevereAsThreshold(event.getLevel()))
      return;

    if (!filters.accept(event))
      return;

    // Prevent concurrent re-entry by this thread
    // (There might be a cheaper way to do this)
    // (Or maybe this lock is not necessary)
    if (guard.get() != null)
      return;

    guard.set(this); // arbitrary thread lock object
    try {

      lock.readLock().acquire();
      try {


        if (closed.get()) {
          getNonFloodingLogger().error(
              "Attempted to use closed appender named [" + name + "].");
          return;
        }

        if (!active.get()) {
          getNonFloodingLogger().error(
              "Attempted to log with inactive named [" + name + "].");
          return;
        }

        append(event);

      } finally {
        lock.readLock().release();
      }

    } catch (InterruptedException e) {
      getLogger().info("interrupted", e);
    } finally {
      guard.set(null);
    }
  }

  /**
   * Sets the layout for this appender. Note that some appenders have their own
   * (fixed) layouts or do not use one. For example, the {@link
   * org.apache.log4j.net.SocketAppender} ignores the layout set here.
   * <p>
   * Note that the implementation of {@link Layout} must be thread-safe.
   * Common layouts such as {@link org.apache.log4j.PatternLayout} are
   * thread-safe.
   * </p>
   *
   * @param layout new layout to use; may be null
   */
  public void setLayout(Layout layout) {
    this.layout = layout;
  }

  /**
   * Sets the name of this Appender.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Sets the threshold level. 
   * All log events with lower level than the threshold level are ignored by
   * the appender.
   *
   * @param threshold new threshold; may be null
   */
  public void setThreshold(final Priority threshold) {
    this.threshold = threshold;
  }

  /**
   * Returns true if this appender is closed.
   * An appender, once closed, is closed forever.
   */
  public boolean getClosed() {
    return closed.get();
  }

  /**
   * Cleans up this appender.
   * Marked as <code>final</code> to prevent subclasses from accidentally
   * overriding and forgetting to call <code>super.close()</code> or obtain a
   * write lock.
   * Calls {@link #internalClose} when completed.
   * Implementation note:  Obtains a write lock before starting close.
   * Calling this method more than once does nothing.
   * @throws RuntimeException if the thread is interrupted
   */
  public final void close() {
    boolean wasClosed;
    try {
      lock.writeLock().acquire();
    } catch (InterruptedException e) {
      getLogger().warn("interrupted", e);
      return;
    }
    try {
      wasClosed = closed.set(true);
    } finally {
      lock.writeLock().release();
    }

    if (!wasClosed)
      internalClose();
  }

  /**
   * Called to check if the appender is closed.
   */
  public boolean isClosed() {
    return closed.get();
  }
  
  private static final org.apache.log4j.spi.ErrorHandler ERROR_HANDLER = 
      new org.apache.log4j.helpers.OnlyOnceErrorHandler();

  public final org.apache.log4j.spi.ErrorHandler getErrorHandler() {
    return ERROR_HANDLER;
  }

  public final void setErrorHandler(org.apache.log4j.spi.ErrorHandler eh) {
  }

  /**
   * Returns a string representation of this object.
   */
  public String toString() {
    return super.toString() + " name=" + name + 
      " threshold=" + threshold + 
      " layout=" + layout +
      " filters=" + filters;
  }

  // PROTECTED METHODS

  /**
   * Subclasses of <code>ConcurrentAppender</code> should implement this method
   * to perform actual logging. 
   * This object holds a read lock during this method.  This method may be
   * called simultaneously by multiple threads.
   */
  protected abstract void append(LoggingEvent event);

  /**
   * Subclasses must implement their own close routines.
   * This method is called by the {@link #close} method.
   * This is guaranteed to be called only once, even if {@link #close} is
   * invoked more than once.
   * Note that further locking is not required, as {@link #append} can no
   * longer be called.
   */
  protected abstract void internalClose();

  /**
   * Finalizes this appender by calling this {@link #close} method.
   */
  protected void finalize() {
    if (!getClosed())
      getLogger().debug("Finalizing appender named [{}].", name);
    close();
  }

  /**
   * A simple linked-list data structure containing filters.
   */
  private static class FilterChain {

    private Filter headFilter = null;
    private Filter tailFilter = null;
   
    public synchronized boolean accept(LoggingEvent event) {
      Filter f = headFilter;
      while (f != null) {
        switch (f.decide(event)) {
          case Filter.DENY:
            return false;
          case Filter.ACCEPT:
            return true;
          case Filter.NEUTRAL:
            f = f.getNext();
        }
      }
      return true;
    }

    public synchronized void addFilter(Filter newFilter) {
      if (newFilter == null)
        throw new NullPointerException();
      if (headFilter == null) {
        headFilter = newFilter;
        tailFilter = newFilter;
      } else {
        tailFilter.setNext(newFilter);
        tailFilter = newFilter;
      }
    }

    public synchronized Filter getHead() {
      return headFilter;
    }

    public synchronized void clear() {
      headFilter = null;
      tailFilter = null;
    }

    public synchronized String toString() {
      StringBuffer sb = new StringBuffer();
      Filter f = headFilter;
      while (f != null) {
        sb.append(f);
        f = f.getNext();
        if (f != null)
          sb.append(',');
      }
      return sb.toString();
    }

  }

}

