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

package org.apache.log4j.chainsaw.filter;

import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * This class is used as a Model for Filtering, and retains the unique entries that 
 * come through over a set of LoggingEvents
 * 
 * @author Paul Smith <psmith@apache.org>
 * @author Scott Deboy <sdeboy@apache.org>
 */
public class FilterModel {
  private Map eventTypeMap = new HashMap();

  public void processNewLoggingEvent(String eventType, LoggingEvent event) {
    /**
     * we update the Entry maps for this identifier,
     *
     */
    EventTypeEntryContainer container = getContainer(eventType);

    container.Levels.add(event.getLevel());
    container.Loggers.add(event.getLoggerName());
    container.Threads.add(event.getThreadName());
    container.NDCs.add(event.getNDC());
    container.MDCKeys.addAll(event.getMDCKeySet());

    if (event.getLocationInformation() != null) {
      LocationInfo info = event.getLocationInformation();
      container.Classes.add(info.getClassName());
      container.Methods.add(info.getMethodName());
      container.FileNames.add(info.getFileName());
    }
  }

  /**
   * @param eventType
   * @return
   */
  private EventTypeEntryContainer getContainer(String eventType) {
    EventTypeEntryContainer container = null;

    if (eventTypeMap.containsKey(eventType)) {
      container = (EventTypeEntryContainer) eventTypeMap.get(eventType);
    } else {
      container = new EventTypeEntryContainer();
      eventTypeMap.put(eventType, container);
    }

    return container;
  }

  private static class EventTypeEntryContainer {
    List LeveList = new ArrayList();
    Set ColumnNames = new HashSet();
    Set Methods = new HashSet();
    Set Classes = new HashSet();
    Set MDCKeys = new HashSet();
    Set NDCs = new HashSet();
    Set Levels = new HashSet();
    Set Loggers = new HashSet();
    Set Threads = new HashSet();
    Set FileNames = new HashSet();
  }
}
