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
