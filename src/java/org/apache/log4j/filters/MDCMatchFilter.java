/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j.filters;

import org.apache.log4j.MDC;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

/**
  The MDCMatchFilter matches a configured value against the
  value of a configured key in the MDC of a logging event.

  <p>The filter admits three options <b>KeyToMatch</b>, 
  <b>ValueToMatch</b>, and <b>ExactMatch</b>.
  
  <p>The value of <b>KeyToMatch</b> property determines which
  key is used to match against in the MDC. The value of that
  key is used to test against the <b>ValueToMatch</b property.
  The <b>KeyToMatch</b> property must be set before this filter
  can function properly.
  
  <p>The value of <b>ValueToMatch</b> property determines the 
  string value to match against. If <b>ExactMatch</b> is set
  to true, a match will occur only when <b>ValueToMatch</b> exactly
  matches the MDC value of the logging event.  Otherwise, if the
  <b>ExactMatch</b> property is set to <code>false</code>, a match
  will occur if <b>ValueToMatch</b> is contained anywhere within the
  MDC value. The <b>ExactMatch</b> property is set to
  <code>false</code> by default.
  
  <p>Note that by default the value to match is set to
  <code>null</code> and will only match if the key is not contained
  or the value is null in the MDC.

  <p>For more information about how the logging event will be
  passed to the appender for reporting, please see 
  the {@link MatchFilterBase} class.

  @author Mark Womack
  
  @since 1.3
*/
public class MDCMatchFilter extends MatchFilterBase {
  
  /**
    The key to match in the MDC of the LoggingEvent. */
  String keyToMatch;
  
  /**
    The value to match in the MDC value of the LoggingEvent. */
  String valueToMatch;
  
  /**
    Do we look for an exact match or just a "contains" match? */
  boolean exactMatch = false;

  /**
    Sets the key to match in the MDC of the LoggingEvent. */
  public void setKeyToMatch(String key) {
    keyToMatch = key;
  }
  
  /**
    Gets the key to match in the MDC of the LoggingEvent. */
  public String getKeyToMatch() {
    return keyToMatch;
  }

  /**
    Sets the value to match in the NDC value of the LoggingEvent. */
  public void setValueToMatch(String value) {
    valueToMatch = value;
  }
  
  /**
    Gets the value to match in the NDC value of the LoggingEvent. */
  public String getValueToMatch() {
    return valueToMatch;
  }

  /**
    Set to true if configured value must exactly match the MDC
    value of the LoggingEvent. Set to false if the configured
    value must only be contained in the MDC value of the
    LoggingEvent. Default is false. */
  public void setExactMatch(boolean exact) {
    exactMatch = exact;
  }
  
  public boolean getExactMatch() {
    return exactMatch;
  }
  
  protected boolean canMatch() {
    return (keyToMatch != null);
  }
  
  /**
    If <b>ExactMatch</b> is set to true, returns true only when
    <b>ValueToMatch</b> exactly matches the MDC value of the 
    logging event. If the <b>ExactMatch</b> property
    is set to <code>false</code>, returns true when 
    <b>ValueToMatch</b> is contained anywhere within the MDC
    value. Otherwise, false is returned. */
  protected boolean match(LoggingEvent event) {
      
    // get the mdc value for the key from the event
    // use the toString() value of the value object
    Object mdcObject = event.getMDC(keyToMatch);
    String mdcValue;
    if (mdcObject != null) {
      mdcValue = mdcObject.toString();
    } else {
      mdcValue = null;
    }
    
    // check for a match
    if (mdcValue == null) {
      if (valueToMatch == null) {
        return true;
      } else {
        return false;
      }
    } else {
      if (valueToMatch != null) {
        if (exactMatch) {
          return mdcValue.equals(valueToMatch);
        } else {
          return (mdcValue.indexOf(valueToMatch) != -1);
        }
      } else {
        return false;
      }
    }
  }
}
