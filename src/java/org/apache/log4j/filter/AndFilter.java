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

package org.apache.log4j.filter;

import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;


/**
 * A filter that 'and's the results of two filters together.
 * 
 * For the filter to process events, both contained filters must return Filter.ACCEPT.
 * 
 * If both filters do not return Filter.ACCEPT, Filter.NEUTRAL is returned.
 * 
 * If acceptOnMatch is set to true, Filter.ACCEPT is returned.
 * If acceptOnMatch is set to false, Filter.DENY is returned.
 * 
 * Here is an example config that will accept only events that contain BOTH
 * a DEBUG level AND 'test' in the message:
 * 
 *<appender name="STDOUT" class="org.apache.log4j.ConsoleAppender">
 * <filter class="org.apache.log4j.filter.AndFilter">
 *  <filter1 class="org.apache.log4j.filter.LevelMatchFilter">
 *        <param name="levelToMatch" value="DEBUG" />
 *        <param name="acceptOnMatch" value="true" />
 *  </filter1>
 *  <filter2 class="org.apache.log4j.filter.StringMatchFilter">
 *        <param name="stringToMatch" value="test" />
 *        <param name="acceptOnMatch" value="true" />
 *  </filter2>
 *  <param name="acceptOnMatch" value="false"/>
 * </filter>
 * <filter class="org.apache.log4j.filter.DenyAllFilter"/>
 *<layout class="org.apache.log4j.SimpleLayout"/>
 *</appender>
 * 
 * To accept all events EXCEPT those events that contain a 
 * DEBUG level and 'test' in the message: 
 * change the AndFilter's acceptOnMatch param to false and remove the DenyAllFilter
 * 
 * NOTE: If you are defining a filter that is only relying on logging event content 
 * (no external or filter-managed state), you could opt instead
 * to use an ExpressionFilter with one of the following expressions:
 * 
 * LEVEL == DEBUG && MSG ~= 'test'
 * or
 * ! ( LEVEL == DEBUG && MSG ~= 'test' )
 *  
 * @author Scott Deboy sdeboy@apache.org
 */
public class AndFilter extends Filter {
  Filter filter1;
  Filter filter2;
  boolean acceptOnMatch = true;
  
  public void activateOptions() {
    //nothing to do
  }

  public void setFilter1(Filter filter1) {
    System.out.println("filter 1 set to: " + filter1);
    this.filter1 = filter1;
  }
  
  public void setFilter2(Filter filter2) {
    System.out.println("filter 2 set to: " + filter2);
    this.filter2 = filter2;
  }

  public void setAcceptOnMatch(boolean acceptOnMatch) {
    this.acceptOnMatch = acceptOnMatch;
  }
  /**
   * If this event does not already contain location information, 
   * evaluate the event against the expression.
   * 
   * If the expression evaluates to true, generate a LocationInfo instance 
   * by creating an exception and set this LocationInfo on the event.
   * 
   * Returns {@link Filter#NEUTRAL}
   */
  public int decide(LoggingEvent event) {
    if ((Filter.ACCEPT == filter1.decide(event)) &&
        Filter.ACCEPT == filter2.decide(event)) {
      if(acceptOnMatch) {
        return Filter.ACCEPT;
      }
       return Filter.DENY;
    }
//    System.out.println("neutral: " + event.getLevel() + ".." + event.getMessage()); 
    return Filter.NEUTRAL;
  }
}
