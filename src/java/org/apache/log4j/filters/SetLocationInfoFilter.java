/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j.filters;

import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

/**
  SetLocationInfoFilter is pass through filter that simply calls
  {@link LoggingEvent#getLocationInformation} method of every 
  LoggingEvent that is sent to it. After calling the method, it 
  returns {@link Filter#NEUTRAL} to send the event to the next 
  filter.
  
  <p><bold>Use of this filter is probably not typical.</bold>
  Its primary purpose is to increase throughput performance for 
  appenders like SocketAppender and SocketHubAppender that send
  logging events to remote clients. These appenders have the option 
  to set the location info for every event appended to them so that
  the client can see where the event was logged in the code. 
  However, resolving the location info for every event can be costly 
  performance-wise, and will reduce the number of events per second 
  that can be appended. This can affect performance in the 
  application that is logging the event. Chances are that one does 
  not want the location info for every event, but rather for a smaller
  set of events that are of interest.
  
  <p>SetLocationInfoFilter can be placed at the end of a filter
  chain configured for an appender. After the event has been 
  filtered through the chain, it will pass through the 
  SetLocationInfoFilter, thus setting the location info for just 
  that event (this assumes that the location info setting of the 
  appender has been set to false).  Using subclasses of the 
  MatchFilterBase class, one can configure the filter chain to 
  accept all events sent to the appender, while only setting the 
  location info for a select set of events. Please see the examples
  for information on how to do this.
  
  <p>Please review the available filters in the 
  org.apache.log4j.filters package. Most of these subclass the
  MatchFilterBase class and are easily configurable for use in
  log4j filter chains. 
  
  <p>(Note that any log4j filter can be used in an appender filter 
  chain, but it needs to support the return of the
  {@link Filter#NEUTRAL} value from its decide method.
  MatchFilterBase subclasses simply expose this functionality 
  directly as part of their configuration.)
  
  @author Mark Womack
  
  @since 1.3
*/
public class SetLocationInfoFilter extends Filter {
  
  /**
    Sets the LocationInfo for the event and returns 
    {@link Filter#NEUTRAL} to pass the event to the next filter. */
  public int decide(LoggingEvent event) {
    event.getLocationInformation();
    return Filter.NEUTRAL;
  }
}