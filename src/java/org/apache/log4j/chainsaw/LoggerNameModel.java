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


/**
 * Implementations of this model contain all the known Logger
 * names within it's model space.
 * 
 * @author Paul Smith psmith@apache.org
 */
public interface LoggerNameModel {
  
  /**
   * Returns an unmodifiable Collection of the uniquely
   * known LoggerNames within this model.
   * 
   * @return unmodifiable Collection of Logger name Strings
   */
  public Collection getLoggerNames();
  
  /**
   * Attempts to add the loggerName to the model, and returns
   * true if it does, i.e that the loggerName is new, otherwise
   * it is ignored.
   * 
   * If the loggerName is new for this model, all the LoggerNameListeners
   * are notified using this thread.
   * 
   * @param loggerName
   */
  public boolean addLoggerName(String loggerName);
  
  public void addLoggerNameListener(LoggerNameListener l);
  public void removeLoggerNameListener(LoggerNameListener l);
}
