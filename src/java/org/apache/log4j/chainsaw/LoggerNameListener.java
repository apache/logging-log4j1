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

import java.util.EventListener;


/**
 * Implementations are notified when new Logger names
 * are added to the related LoggerNameModel instance.
 * 
 * @author Paul Smith <psmith@apache.org>
 */
public interface LoggerNameListener extends EventListener {
  /**
   * Called by LoggerNameModel instances when a new unique
   * Logger name string has been introduced into the model
   * @param loggerName the new, unique loggerName
   */
  public void loggerNameAdded(String loggerName);
}
