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
 */
package org.apache.log4j.chainsaw;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.swing.event.EventListenerList;


/**
 * An implementation of LoggerNameModel which can be used as a delegate
 * 
 * @author Paul Smith <psmith@apache.org>
 */
public class LoggerNameModelSupport implements LoggerNameModel {
  
  private Set loggerNameSet = new HashSet();
  private EventListenerList listenerList = new EventListenerList();
  

  /* (non-Javadoc)
   * @see org.apache.log4j.chainsaw.LoggerNameModel#getLoggerNames()
   */
  public Collection getLoggerNames() {
    return Collections.unmodifiableSet(loggerNameSet);
  }

  /* (non-Javadoc)
   * @see org.apache.log4j.chainsaw.LoggerNameModel#addLoggerName(java.lang.String)
   */
  public boolean addLoggerName(String loggerName) {
    boolean isNew = loggerNameSet.add(loggerName);
    
    if(isNew)
    {
      notifyListeners(loggerName);
    }
    
    return isNew;
  }

  /**
   * Notifies all the registered listeners that a new unique
   * logger name has been added to this model
   * @param loggerName
   */
  private void notifyListeners(String loggerName)
  {
    LoggerNameListener[] eventListeners = (LoggerNameListener[]) listenerList.getListeners(LoggerNameListener.class);

    for (int i = 0; i < eventListeners.length; i++)
    {
      LoggerNameListener listener = eventListeners[i];
      listener.loggerNameAdded(loggerName);
    }    
  }

  /* (non-Javadoc)
   * @see org.apache.log4j.chainsaw.LoggerNameModel#addLoggerNameListener(org.apache.log4j.chainsaw.LoggerNameListener)
   */
  public void addLoggerNameListener(LoggerNameListener l) {
    listenerList.add(LoggerNameListener.class, l);
  }

  /* (non-Javadoc)
   * @see org.apache.log4j.chainsaw.LoggerNameModel#removeLoggerNameListener(org.apache.log4j.chainsaw.LoggerNameListener)
   */
  public void removeLoggerNameListener(LoggerNameListener l) {
    listenerList.remove(LoggerNameListener.class, l);
  }
}
