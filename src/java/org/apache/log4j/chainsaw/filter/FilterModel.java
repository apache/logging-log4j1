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

package org.apache.log4j.chainsaw.filter;

import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;

/**
 * This class is used as a Model for Filtering, and retains the unique entries that
 * come through over a set of LoggingEvents
 *
 * @author Paul Smith <psmith@apache.org>
 * @author Scott Deboy <sdeboy@apache.org>
 */
public class FilterModel {
  private EventTypeEntryContainer eventContainer =
    new EventTypeEntryContainer();

  public void processNewLoggingEvent(String eventType, LoggingEvent event) {
    EventTypeEntryContainer container = getContainer(eventType);

    container.addLevel(event.getLevel());
    container.addLogger(event.getLoggerName());
    container.addThread(event.getThreadName());
    container.addNDC(event.getNDC());
    container.addMDCKeys(event.getMDCKeySet());

    if (event.locationInformationExists()) {
      LocationInfo info = event.getLocationInformation();
      container.addClass(info.getClassName());
      container.addMethod(info.getMethodName());
      container.addFileName(info.getFileName());
    }
  }

  public EventTypeEntryContainer getContainer() {
    return eventContainer;
  }

  EventTypeEntryContainer getContainer(String eventType) {
    return this.eventContainer;
  }
}
