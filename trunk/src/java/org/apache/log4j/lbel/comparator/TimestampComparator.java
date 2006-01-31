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
 * Compare the timestamp of 
 * 
 * @author <a href="http://www.qos.ch/log4j/">Ceki G&uuml;lc&uuml;</a>
 */
public class TimestampComparator implements Comparator {

  Operator operator;
  long rightTimestamp;
  
  public TimestampComparator(Operator operator, long rightTimestamp) {
    this.operator = operator;
    this.rightTimestamp = rightTimestamp;
  
  }
  
  public boolean compare(LoggingEvent event) {
    long leftTimestamp = event.getTimeStamp();
    
    switch(operator.getCode()) {
    case Operator.EQUAL: return leftTimestamp == rightTimestamp;   
    case Operator.NOT_EQUAL: return leftTimestamp != rightTimestamp;   
    case Operator.GREATER: return leftTimestamp > rightTimestamp;   
    case Operator.GREATER_OR_EQUAL: return leftTimestamp >= rightTimestamp;   
    case Operator.LESS: return leftTimestamp < rightTimestamp;   
    case Operator.LESS_OR_EQUAL: return leftTimestamp <= rightTimestamp;   
    }
    throw new IllegalStateException("Unreachable state reached, "+operator);
  }
}
