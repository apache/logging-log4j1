/*
 * ============================================================================
 *                   The Apache Software License, Version 1.1
 * ============================================================================
 *
 *    Copyright (C) 1999 The Apache Software Foundation. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modifica-
 * tion, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of  source code must  retain the above copyright  notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any, must
 *    include  the following  acknowledgment:  "This product includes  software
 *    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
 *    Alternately, this  acknowledgment may  appear in the software itself,  if
 *    and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "log4j" and  "Apache Software Foundation"  must not be used to
 *    endorse  or promote  products derived  from this  software without  prior
 *    written permission. For written permission, please contact
 *    apache@apache.org.
 *
 * 5. Products  derived from this software may not  be called "Apache", nor may
 *    "Apache" appear  in their name,  without prior written permission  of the
 *    Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 * APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 * DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 * ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 * (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * This software  consists of voluntary contributions made  by many individuals
 * on  behalf of the Apache Software  Foundation.  For more  information on the
 * Apache Software Foundation, please see <http://www.apache.org/>.
 *
 */

package org.apache.log4j;

import org.apache.log4j.Layout;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OnlyOnceErrorHandler;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.OptionHandler;


/**
 * Abstract superclass of the other appenders in the package. This class
 * provides the code for common functionality, such as support for threshold
 * filtering and support for general filters.
 *
 * @author Ceki G&uuml;lc&uuml;
 *
 * @since 0.8.1
 */
public abstract class AppenderSkeleton implements Appender, OptionHandler {
  /**
   * The layout variable does not need to be set if the appender
   * implementation has its own layout.
   */
  protected Layout layout;

  /**
   * Appenders are named.
   */
  protected String name;

  /**
   * There is no level threshold filtering by default.
   */
  protected Priority threshold;

  /**
   * It is assumed and enforced that errorHandler is never null.
   */
  protected ErrorHandler errorHandler = new OnlyOnceErrorHandler();

  /**
   * The first filter in the filter chain. Set to <code>null</code> initially.
   */
  protected Filter headFilter;

  /**
   * The last filter in the filter chain.
   */
  protected Filter tailFilter;

  /**
   * Is this appender closed?
   */
  protected boolean closed = false;

  /**
   * The guard prevents an appender from repeatedly calling its own doAppend
   * method.
   */
  private boolean guard = false;

  /**
   * Derived appenders should override this method if option structure
   * requires it.
   */
  public void activateOptions() {
  }

  /**
   * Add a filter to end of the filter list.
   *
   * @since 0.9.0
   */
  public void addFilter(Filter newFilter) {
    if (headFilter == null) {
      headFilter = newFilter;
      tailFilter = newFilter;
    } else {
      tailFilter.next = newFilter;
      tailFilter = newFilter;
    }
  }

  /**
   * Subclasses of <code>AppenderSkeleton</code> should implement this method
   * to perform actual logging. See also {@link #doAppend
   * AppenderSkeleton.doAppend} method.
   *
   * @since 0.9.0
   */
  protected abstract void append(LoggingEvent event);

  /**
   * Clear the filters chain.
   *
   * @since 0.9.0
   */
  public void clearFilters() {
    headFilter = null;
    tailFilter = null;
  }

  /**
   * Finalize this appender by calling the derived class' <code>close</code>
   * method.
   *
   * @since 0.8.4
   */
  public void finalize() {
    // An appender might be closed then garbage collected. There is no
    // point in closing twice.
    if (this.closed) {
      return;
    }

    LogLog.debug("Finalizing appender named [" + name + "].");
    close();
  }

  /**
   * Return the currently set {@link ErrorHandler} for this Appender.
   *
   * @since 0.9.0
   */
  public ErrorHandler getErrorHandler() {
    return this.errorHandler;
  }

  /**
   * Returns the head Filter.
   *
   * @since 1.1
   */
  public Filter getFilter() {
    return headFilter;
  }

  /**
   * Return the first filter in the filter chain for this Appender. The return
   * value may be <code>null</code> if no is filter is set.
   */
  public final Filter getFirstFilter() {
    return headFilter;
  }

  /**
   * Returns the layout of this appender. The value may be null.
   */
  public Layout getLayout() {
    return layout;
  }

  /**
   * Returns the name of this FileAppender.
   */
  public final String getName() {
    return this.name;
  }

  /**
   * Returns this appenders threshold level. See the {@link #setThreshold}
   * method for the meaning of this option.
   *
   * @since 1.1
   */
  public Priority getThreshold() {
    return threshold;
  }

  /**
   * Check whether the message level is below the appender's threshold. If
   * there is no threshold set, then the return value is always
   * <code>true</code>.
   */
  public boolean isAsSevereAsThreshold(Priority priority) {
    return ((threshold == null) || priority.isGreaterOrEqual(threshold));
  }

  /**
   * This method performs threshold checks and invokes filters before
   * delegating actual logging to the subclasses specific {@link
   * AppenderSkeleton#append} method.
   */
  public synchronized void doAppend(LoggingEvent event) {
    if (closed) {
      LogLog.error(
        "Attempted to append to closed appender named [" + name + "].");

      return;
    }

    // prevent re-entry
    if (guard) {
      return;
    }

    try {
      guard = true;

      if (!isAsSevereAsThreshold(event.getLevel())) {
        return;
      }

      Filter f = this.headFilter;

FILTER_LOOP: 
      while (f != null) {
        switch (f.decide(event)) {
        case Filter.DENY:
          return;

        case Filter.ACCEPT:
          break FILTER_LOOP;

        case Filter.NEUTRAL:
          f = f.next;
        }
      }

      this.append(event);
    } finally {
      guard = false;
    }
  }

  /**
   * Set the {@link ErrorHandler} for this Appender.
   *
   * @since 0.9.0
   */
  public synchronized void setErrorHandler(ErrorHandler eh) {
    if (eh == null) {
      // We do not throw exception here since the cause is probably a
      // bad config file.
      LogLog.warn("You have tried to set a null error-handler.");
    } else {
      this.errorHandler = eh;
    }
  }

  /**
   * Set the layout for this appender. Note that some appenders have their own
   * (fixed) layouts or do not use one. For example, the {@link
   * org.apache.log4j.net.SocketAppender} ignores the layout set here.
   */
  public void setLayout(Layout layout) {
    this.layout = layout;
  }

  /**
   * Set the name of this Appender.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Set the threshold level. All log events with lower level than the
   * threshold level are ignored by the appender.
   * 
   * <p>
   * In configuration files this option is specified by setting the value of
   * the <b>Threshold</b> option to a level string, such as "DEBUG", "INFO"
   * and so on.
   * </p>
   *
   * @since 0.8.3
   */
  public void setThreshold(Priority threshold) {
    this.threshold = threshold;
  }
}
