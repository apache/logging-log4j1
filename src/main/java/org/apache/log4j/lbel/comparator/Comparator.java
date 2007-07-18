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

package org.apache.log4j.lbel.comparator;

import org.apache.log4j.spi.LoggingEvent;


/**
 * A Comparator instance is a node in the syntax tree returned by the parser. 
 * It compares events according to criteria proper to the comparator.
 * 
 * @author <a href="http://www.qos.ch/log4j/">Ceki G&uuml;lc&uuml;</a>
 *
 */
public interface Comparator {
  
  /**
   * 
   * @param event
   * @return
   * @throws NullPointerException thrown if the lef or right side is null and 
   * the comparison operator is other than equals not equals ('=' or '!=').
   */
  public boolean compare(LoggingEvent event) throws NullPointerException;
}
