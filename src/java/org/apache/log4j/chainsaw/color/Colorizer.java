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

package org.apache.log4j.chainsaw.color;

import org.apache.log4j.spi.LoggingEvent;

import java.awt.Color;


/**
 * Given a LoggingEvent, can determine an appropriate
 * Color to use based on whatever this implementation
 * has been coded.
 * 
 * @author Paul Smith <psmith@apache.org>
 * @author Scott Deboy <sdeboy@apache.org>
 */
public interface Colorizer {
  /**
   * Given a LoggingEvent, returns a Color to use for background, 
   * or null if this instance cannot determine one, or that
   * the stanard color should be used.
   * @param event
   * @return background color 
   */
  public Color getBackgroundColor(LoggingEvent event);
  
  public Color getForegroundColor(LoggingEvent event);
}
