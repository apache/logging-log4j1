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

package org.apache.log4j.varia;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;


/**
 * A very basic appender that takes the events and stores them in to a
 * ListModel for late retrieval.
 *
 * @author Paul Smith <psmith@apache.org>
 *
 */
public final class ListModelAppender extends AppenderSkeleton {
  private final DefaultListModel model = new DefaultListModel();

  /**
   * Returns a reference to the ListModel that contains all the LoggingEvents
   * that have been appended to this class.
   * 
   * @return
   */
  public final ListModel getModel() {
    return model;
  }

  /* (non-Javadoc)
   * @see org.apache.log4j.AppenderSkeleton#append(org.apache.log4j.spi.LoggingEvent)
   */
  protected void append(LoggingEvent event) {
    model.addElement(event);
  }

  /* (non-Javadoc)
   * @see org.apache.log4j.Appender#close()
   */
  public void close() {
    clearModel();
  }

  /* (non-Javadoc)
   * @see org.apache.log4j.Appender#requiresLayout()
   */
  public boolean requiresLayout() {
    return false;
  }

  /**
   * Removes all the Events from the model
   */
  public void clearModel() {
    model.clear();
  }
}
