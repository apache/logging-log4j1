/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j.filters;

import org.apache.log4j.NDC;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.helpers.OptionConverter;

/**
  The NDCMatchFilter matches a configured value against the
  NDC value of a logging event.
  
  <p>The filter admits two options <b>ValueToMatch</b> and
  <b>ExactMatch</b>.
  
  <p>As the name indicates, the value of <b>ValueToMatch</b> property
  determines the string value to match. If <b>ExactMatch</b> is set
  to true, a match will occur only when <b>ValueToMatch</b> exactly
  matches the NDC value of the logging event.  Otherwise, if the
  <b>ExactMatch</b> property is set to <code>false</code>, a match
  will occur when <b>ValueToMatch</b> is contained anywhere within the
  NDC value. The <b>ExactMatch</b> property is set to
  <code>false</code> by default.
  
  <p>Note that by default <b>ValueToMatch</b> is set to
  <code>null</code> and will only match an empty NDC stack.

  <p>For more information about how the logging event will be
  passed to the appender for reporting, please see 
  the {@link MatchFilterBase} class.

  @author Mark Womack
  
  @since 1.3
*/
public
class NDCMatchFilter extends MatchFilterBase {
  
  /**
    The value to match in the NDC value of the LoggingEvent. */
  String valueToMatch;
  
  /**
    Do we look for an exact match or just a "contains" match? */
  boolean exactMatch = false;

  /**
    Sets the value to match in the NDC value of the LoggingEvent. */
  public
  void setValueToMatch(String value) {
    valueToMatch = value;
  }
  
  /**
    Gets the value to match in the NDC value of the LoggingEvent. */
  public
  String getValueToMatch() {
    return valueToMatch;
  }

  /**
    Set to true if configured value must exactly match the NDC
    value of the LoggingEvent. Set to false if the configured
    value must only be contained in the NDC value of the
    LoggingEvent. Default is false. */
  public
  void setExactMatch(boolean exact) {
    exactMatch = exact;
  }
  
  public
  boolean getExactMatch() {
    return exactMatch;
  }
  
  /**
    If <b>ExactMatch</b> is set to true, returns true only when
    <b>ValueToMatch</b> exactly matches the NDC value of the 
    logging event. If the <b>ExactMatch</b> property
    is set to <code>false</code>, returns true when 
    <b>ValueToMatch</b> is contained anywhere within the NDC
    value. Otherwise, false is returned. */
  protected
  boolean match(LoggingEvent event) {
    
    // get the ndc value for the event
    String eventNDC = event.getNDC();
    
    // check for a match
    boolean matchOccured = false;
    
    // if the NDC stack is empty
    if (eventNDC == null) {
      // return true if are we matching a null
      if (valueToMatch == null) {
        return true;
      // else return false
      } else {
        return false;
      }
    } else {
      // try to match the configured non-null value
      if (valueToMatch != null) {
        if (exactMatch) {
          return eventNDC.equals(valueToMatch);
        } else {
          return (eventNDC.indexOf(valueToMatch) != -1);
        }
      // else the value to match is null, so return false
      } else {
        return false;
      }
    }
  }
}
