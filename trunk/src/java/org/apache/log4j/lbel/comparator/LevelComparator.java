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

import org.apache.log4j.lbel.Operator;
import org.apache.log4j.spi.LoggingEvent;


/**
 * Compare the level of an event passed as parameter to the level set in the
 * constructor. 
 * 
 * @author <a href="http://www.qos.ch/log4j/">Ceki G&uuml;lc&uuml;</a>
 */
public class LevelComparator implements Comparator {

  Operator operator;
  int rightLevel;
  
  /**
   * This constructor is called by the parser.
   * 
   * @param operator the comparison operator to use
   * @param level the level (in integer form) to compare to 
   */
  public LevelComparator(Operator operator, int level) {
    this.operator = operator;
    this.rightLevel = level;
  }
  
  /**
   * Compare the level event passed as parameter with the level of this instance
   * according to the operator of this instance.
   */
  public boolean compare(LoggingEvent event) {
    int leftLevel = event.getLevel().toInt();
    
    switch(operator.getCode()) {
    case Operator.EQUAL: return leftLevel == rightLevel;   
    case Operator.NOT_EQUAL: return leftLevel != rightLevel;   
    case Operator.GREATER: return leftLevel > rightLevel;   
    case Operator.GREATER_OR_EQUAL: return leftLevel >= rightLevel;   
    case Operator.LESS: return leftLevel < rightLevel;   
    case Operator.LESS_OR_EQUAL: return leftLevel <= rightLevel;   
    }
    throw new IllegalStateException("Unreachable state reached, operator "+operator);
  }
}
