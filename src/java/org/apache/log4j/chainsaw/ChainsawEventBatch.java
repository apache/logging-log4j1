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

package org.apache.log4j.chainsaw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;


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
   * @param eventType
   * @param convertedEventVector
   */
  void addEvent(String ident, String eventType, Vector convertedEventVector) {
    List events = null;

    if (!identEventMap.containsKey(ident)) {
      events = new ArrayList();
      identEventMap.put(ident, events);
    } else {
      events = (List) identEventMap.get(ident);
    }
    
    events.add(new Entry(ident, eventType, convertedEventVector));
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
   * Returns a Collection of ChainsawEventBatch.Entry objects that
   * are bound to the identifier
   * @param identifier
   * @return Collection of ChainsawEventBatch.Entry instances
   */
  List entrySet(String identifier) {
  	return (List)identEventMap.get(identifier);
  }

  static class Entry {
    private String eventType;
    private Vector eventVector;
    private String identifier;

    Entry(String identifier, String eventType, Vector eventVector) {
      this.identifier = identifier;
      this.eventType = eventType;
      this.eventVector = eventVector;
    }

    String getEventType() {
      return eventType;
    }

    Vector getEventVector() {
      return eventVector;
    }

    public String getIdentifier() {
      return identifier;
    }
  }
}
