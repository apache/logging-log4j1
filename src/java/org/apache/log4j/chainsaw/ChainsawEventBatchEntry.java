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

/*
 * Created on 2/09/2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.log4j.chainsaw;

import org.apache.log4j.spi.LoggingEvent;

/**
 * A simple container of Events, mapped to an identifier
 * @author Paul Smith <psmith@apache.org>
 *
 */
class ChainsawEventBatchEntry {
  private String eventType;

  //  private Vector eventVector;
  private LoggingEvent event;
  private String identifier;

  ChainsawEventBatchEntry(String identifier, String eventType, LoggingEvent e) {
    this.identifier = identifier;
    this.eventType = eventType;
    this.event = e;
  }

  String getEventType() {
    return eventType;
  }

  public LoggingEvent getEvent() {
    return event;
  }

  public String getIdentifier() {
    return identifier;
  }
  
  public String toString()
  {
      StringBuffer buffer = new StringBuffer(this.getClass().getName());
      buffer.append("[");
      buffer.append("ident=").append(getIdentifier());
      buffer.append(",");
      buffer.append("eventType=").append(getEventType());
      buffer.append(",");
      buffer.append("event=").append(getEvent());
      buffer.append("]");
      
        return buffer.toString();
  }
}
