/*
 * Copyright 1999,2005 The Apache Software Foundation.
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

package org.apache.log4j.rolling;

import org.apache.log4j.Appender;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;


/**
 * FilterBasedTriggeringPolicy determines if rolling should be triggered
 * by evaluating the current message against a set of filters.  Unless a
 * filter rejects a message, a rolling event will be triggered.
 *
 * @author Curt Arnold
 * @since 1.3
 *
 */
public final class FilterBasedTriggeringPolicy implements TriggeringPolicy {
  /**
   * The first filter in the filter chain. Set to <code>null</code> initially.
   */
  private Filter headFilter;

  /**
   * The last filter in the filter chain.
   */
  private Filter tailFilter;

  /**
   *  Creates a new FilterBasedTriggeringPolicy.
   */
  public FilterBasedTriggeringPolicy() {
  }

  /**
   * {@inheritDoc}
   *
   */
  public boolean isTriggeringEvent(
    final Appender appender, final LoggingEvent event, final String file,
    final long fileLength) {
    //
    //   in the abnormal case of no contained filters
    //     always return true to avoid each logging event 
    //     from having its own file.
    if (headFilter == null) {
      return false;
    }

    //
    //    otherwise loop through the filters
    //
    for (Filter f = headFilter; f != null; f = f.getNext()) {
      switch (f.decide(event)) {
      case Filter.DENY:
        return false;

      case Filter.ACCEPT:
        return true;
      }
    }

    return true;
  }

  /**
   * Add a filter to end of the filter list.
   * @param newFilter filter to add to end of list.
   */
  public void addFilter(final Filter newFilter) {
    if (headFilter == null) {
      headFilter = newFilter;
      tailFilter = newFilter;
    } else {
      tailFilter.setNext(newFilter);
      tailFilter = newFilter;
    }
  }

  /**
   * Clear the filters chain.
   *
   */
  public void clearFilters() {
    headFilter = null;
    tailFilter = null;
  }

  /**
   * Returns the head Filter.
   * @return head of filter chain, may be null.
   *
   */
  public Filter getFilter() {
    return headFilter;
  }

  /**
   *  {@inheritDoc}
   */
  public void activateOptions() {
    for (Filter f = headFilter; f != null; f = f.getNext()) {
      f.activateOptions();
    }
  }
}
