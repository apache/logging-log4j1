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

package org.apache.log4j.spi;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;


/**
 * Adapter class that wraps an object that implements
 * HierarchyEventListener and exposes the log4j 1.3 introduced
 * LoggerEventListener interface.
 *
 * @deprecated
 */
public final class HierarchyEventListenerAdapter implements LoggerEventListener {
  /**
   * Wrapped listener.
   */
  private final org.apache.log4j.spi.HierarchyEventListener listener;

  /**
   * Constructs a new instance of HierarchyEventListenerAdapter.
   * @param listener
   * @deprecated
   */
  public HierarchyEventListenerAdapter(
    final org.apache.log4j.spi.HierarchyEventListener listener) {
    if (listener == null) {
      throw new IllegalArgumentException("listener");
    }

    this.listener = listener;
  }

  /**
    Called when an appender is added to the logger.

    @param logger The logger to which the appender was added.
    @param appender The appender added to the logger. */
  public void appenderAddedEvent(final Logger logger, final Appender appender) {
    listener.addAppenderEvent(logger, appender);
  }

  /**
    Called when an appender is removed from the logger.

    @param logger The logger from which the appender was removed.
    @param appender The appender removed from the logger. */
  public void appenderRemovedEvent(
    final Logger logger, final Appender appender) {
    listener.removeAppenderEvent(logger, appender);
  }

  /**
    Called when level changed on the logger.

    @param logger The logger that changed levels. */
  public void levelChangedEvent(final Logger logger) {
  }
}
