/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.APL file.
 */

package org.apache.log4j.varia;

import org.apache.log4j.NDC;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.helpers.OptionConverter;

/**
   This is a simple filter based on {@link NDC} value matching.

   <p>The filter admits three options <b>ValueToMatch</b> and
   <b>ExactMatch</b> and <b>AcceptOnMatch</b>. If <b>ExactMatch</b>
   is set to true, then the <b>ValueToMatch</b> must exactly match
   the NDC value of the {@link LoggingEvent}. By default the filter
   will match if the <b>ValueToMatch</b> is "contained" in the NDC
   value. Also by default, the <b>ValueToMatch</b> is set to null
   and will only match when the NDC stack is empty.
   
   <p>If the filter matches between the configured 
   <b>ValueToMatch</b> and the NDC value of the {@link 
   LoggingEvent}, then the {@link #decide} method returns {@link 
   Filter#ACCEPT} in case the <b>AcceptOnMatch</b> option value 
   is set to <code>true</code>, if it is <code>false</code> then 
   {@link Filter#DENY} is returned. If there is no match, 
   {@link Filter#NEUTRAL} is returned.

   @author Mark Womack

   @since 1.3 */
public class NDCMatchFilter extends Filter {
  
  /**
     Do we return ACCEPT when a match occurs. Default is
     <code>true</code>.  */
  boolean acceptOnMatch = true;

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
    If true, then the filter will return {@link Filter#ACCEPT}
    if the filter matches the configured value. If false, then
    {@link Filter#DENY} is returned. Default setting is true. */
  public
  void setAcceptOnMatch(boolean acceptOnMatch) {
    this.acceptOnMatch = acceptOnMatch;
  }
  
  /**
    Gets the AcceptOnMatch value. */
  public
  boolean getAcceptOnMatch() {
    return acceptOnMatch;
  }
  
  /**
     Return the decision of this filter.

     If there is a match, then the returned decision is {@link 
     Filter#ACCEPT} if the <b>AcceptOnMatch</b> property is set 
     to <code>true</code>. The returned decision is {@link 
     Filter#DENY} if the <b>AcceptOnMatch</b> property is set 
     to false. */
  public
  int decide(LoggingEvent event) {
    
    // get the ndc value for the event
    String eventNDC = event.getNDC();
    
    // check for a match
    boolean matchOccured = false;
    if (eventNDC == null) {
      if (valueToMatch == null)
        matchOccured = true;
    } else {
      if (valueToMatch != null) {
        if (exactMatch) {
          matchOccured = eventNDC.equals(valueToMatch);
        } else {
          matchOccured = (eventNDC.indexOf(valueToMatch) != -1);
        }
      }
    }

    if(matchOccured) {  
      if(this.acceptOnMatch)
        return Filter.ACCEPT;
      else
        return Filter.DENY;
    } else {
      return Filter.NEUTRAL;
    }
  }
}
