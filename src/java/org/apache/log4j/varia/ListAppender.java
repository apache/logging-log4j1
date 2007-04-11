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

package org.apache.log4j.varia;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import java.util.ArrayList;
import java.util.List;


/**
 * A very basic appender that takes the events and stores them in to a
 * java.util.List for late retrieval.
 *
 * Note:  This implemenation intentionally does not allow direct modification
 * of the internal List model to reduce the synchronization complexity that 
 * this would require.
 *
 * @see org.apache.log4j.varia.ListModelAppender
 *
 * @author Paul Smith <psmith@apache.org>
 *
 */
public final class ListAppender extends AppenderSkeleton {
  private List list = new ArrayList();

  /**
   * Constructs a list appender.
   */
  public ListAppender() {
      super(true);
  }

  /**
   * Returns a writeable, BUT cloned List of all the LoggingEvents that are contained
   * in the internal model.  You are free to modify this list without
  * worry of synchronization, but note that any modifications to the returned list
  * that you do will have NO impact on the internal model of this Appender.
   *
   * @return Modifiable List
   */
  public final List getList() {
    synchronized (list) {
      return new ArrayList(list);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.apache.log4j.AppenderSkeleton#append(org.apache.log4j.spi.LoggingEvent)
   */
  protected void append(LoggingEvent event) {
    event.prepareForDeferredProcessing();
    
    // Extract location info now. Later it might not be possible.
    event.getLocationInformation();
    
    synchronized (list) {
      list.add(event);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.apache.log4j.Appender#close()
   */
  public void close() {
    closed = true;
  }

  /**
   * Removes all the Events from the model
   */
  public void clearList() {
    synchronized (list) {
      list.clear();
    }
  }

    /**
     * Gets whether appender requires a layout.
     * @return false
     */
  public boolean requiresLayout() {
      return false;
  }
}
