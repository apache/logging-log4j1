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

package org.apache.log4j.chainsaw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.spi.LoggingEvent;


/**
 * A container class that contains a group of events split up
 * into branches based on Identifiers
 * @author Paul Smith <psmith@apache.org>
 * @author Scott Deboy <sdeboy@apache.org>
 *
 */
class ChainsawEventBatch {
  private Map identEventMap = new HashMap();

  ChainsawEventBatch() {
  }

  /**
   * @param ident
   * @param e
   */
  void addEvent(String ident, LoggingEvent e) {
    List events = (List)identEventMap.get(ident);

    if (events == null) {
      events = new ArrayList();
      identEventMap.put(ident, events);
    }

    events.add(e);
  }

  /**
   * Returns an iterator of Identifier strings that this payload contains.
   *
   * The values returned from this iterator can be used to query the
   *
   * @return Iterator
   */
  Iterator identifierIterator() {
    return identEventMap.keySet().iterator();
  }

  /**
   * Returns a Collection of LoggingEvent objects that
   * are bound to the identifier
   * @param identifier
   * @return Collection of LoggingEvent instances
   */
  List entrySet(String identifier) {
    return (List) identEventMap.get(identifier);
  }
}
