/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j.filters;

import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.helpers.LogLog;

/**
  This is the abstract base class for many useful filters implemented
  in the log4j.filter package. It extends the base {@link Filter} 
  class to allow a different return value for a match or a nomatch. 
  What is specifically tested for matching is implemented in specific
  subclasses.
  
  <p>Properties matchReturnValue and noMatchReturnValue can be
  set programmitcally or from a configuration file. They set
  the value that will be returned when there is match or when there
  is not a match, respectively.  By default matchReturnValue is
  set to Filter.ACCEPT and noMatchReturnValue is set to Filter.DENY.
  
  <p>In addition to being able to set the match and nomatch return
  values directly, one can instead use the chainPolicy property.
  Log4j allows for filters to be chained together, each filter
  deciding whether the logging event should be accepted, denied,
  or passed on to the next filter.  However, for the event to passed
  to the next filter, one of the return values must be set to 
  Filter.NEUTRAL. One can use chainPolicy to accomplish this, passing
  it one of the four valid policies: ACCEPT_ON_MATCH (match = 
  Filter.ACCEPT/nomatch = Filter.NEUTRAL), DENY_ON_MATCH (match = 
  Filter.DENY/nomatch = Filter.NEUTRAL), ACCEPT_ON_NOMATCH
  (match = Filter.NEUTRAL/nomatch = Filter.ACCEPT), and 
  DENY_ON_NOMATCH (match = Filter.NEUTRAL/nomatch = Filter.DENY).
  Which policy is used can be set programmatically or from a
  configuration file.  If more than one filter will be attached
  to a single appender, all but the last one should probably be
  configured via the chainPolicy property.
  
  <p>Subclasses are required to implement the match() method. The
  implementation should test for a match, returning true if there
  is a match and false if there is nomatch.  Subclasses can also
  implement their own version of the canMatch() method, but should
  only do so if they will be unable to perform the match code
  due to misconfiguration.  By default, canMatch() will always
  return true.
  
  <p>Developers are encouraged to extend this base class when
  implementing their own filters. For examples of how
  to use and extend this base class, please see the various 
  filters implemented in the log4j.filters package.
  
  @author Mark Womack
  
  @since 1.3
*/
public abstract class MatchFilterBase extends Filter {
  
  /** Chain policy constant = AcceptOnMatch. */
  public static final String ACCEPT_ON_MATCH    = "AcceptOnMatch";

  /** Chain policy constant = DenyOnMatch. */
  public static final String DENY_ON_MATCH      = "DenyOnMatch";

  /** Chain policy constant = AcceptOnNomatch. */
  public static final String ACCEPT_ON_NOMATCH  = "AcceptOnNomatch";

  /** Chain policy constant = DenyOnNomatch. */
  public static final String DENY_ON_NOMATCH    = "DenyOnNomatch";

  /** Chain policy constant = UnknownPolicy. */
  public static final String UNKNOWN_POLICY     = "UnknownPolicy";
  
  /**
    The value that will be returned upon a successful match. */
  protected int matchReturnValue = ACCEPT;
  
  /** 
    The value that will be returned upon an unsuccessful match */
  protected int noMatchReturnValue = DENY;
  
  /**
    Set the value to return upon a successful match. Valid
    string values are "ACCEPT", "DENY", and "NEUTRAL". */
  public void setMatchReturnValue(String filterReturnValue) {
    if (filterReturnValue.equalsIgnoreCase("accept")) {
      matchReturnValue = ACCEPT;
    } else if (filterReturnValue.equalsIgnoreCase("deny")) {
      matchReturnValue = DENY;
    } else if (filterReturnValue.equalsIgnoreCase("neutral")) {
      matchReturnValue = NEUTRAL;
    } else {
      LogLog.error("invalid matchReturnValue: " + filterReturnValue);
    }
  }
    
  /**
    Gets the value that will be returned upon a successful
    match. */
  public String getMatchReturnValue() {
    if (matchReturnValue == ACCEPT)
      return "accept";
    else if (matchReturnValue == DENY)
      return "deny";
    else if (matchReturnValue == NEUTRAL)
      return "neutral";
    else
      return "unknown"; // this one should never happen
  }

  /**
    Set the value to return upon a successful match. Valid
    string values are "ACCEPT", "DENY", and "NEUTRAL". */
  public void setNoMatchReturnValue(String filterReturnValue) {
    if (filterReturnValue.equalsIgnoreCase("accept")) {
      noMatchReturnValue = ACCEPT;
    } else if (filterReturnValue.equalsIgnoreCase("deny")) {
      noMatchReturnValue = DENY;
    } else if (filterReturnValue.equalsIgnoreCase("neutral")) {
      noMatchReturnValue = NEUTRAL;
    } else {
      LogLog.error("invalid noMatchReturnValue: " + filterReturnValue);
    }
  }
    
  /**
    Gets the value that will be returned upon an unsuccessful
    match. */
  public String getNoMatchReturnValue() {
    if (noMatchReturnValue == ACCEPT)
      return "accept";
    else if (noMatchReturnValue == DENY)
      return "deny";
    else if (noMatchReturnValue == NEUTRAL)
      return "neutral";
    else
      return "unknown"; // this one should never happen
  }
  
  /**
    Sets the match and nomatch return values based on a "policy"
    string.  Valid values for the policy string are defined as
    constants for this class: ACCEPT_ON_MATCH, DENY_ON_MATCH, 
    ACCEPT_ON_NOMATCH, DENY_ON_NOMATCH. */
  public void setChainPolicy(String policyStr) {
    if (policyStr.equalsIgnoreCase(ACCEPT_ON_MATCH)) {
      matchReturnValue = ACCEPT;
      noMatchReturnValue = NEUTRAL;
    } else if (policyStr.equalsIgnoreCase(DENY_ON_MATCH)) {
      matchReturnValue = DENY;
      noMatchReturnValue = NEUTRAL;
    } else if (policyStr.equalsIgnoreCase(ACCEPT_ON_NOMATCH)) {
      matchReturnValue = NEUTRAL;
      noMatchReturnValue = ACCEPT;
    } else if (policyStr.equalsIgnoreCase(DENY_ON_NOMATCH)) {
      matchReturnValue = NEUTRAL;
      noMatchReturnValue = DENY;
    } else {
      LogLog.error("invalid chainPolicy: " + policyStr);
    }
  }

  /**
    Gets the chain policy string value that matches the current
    settings of matchReturnValue and noMatchReturn value. If the
    current values do not match a known policy setting, then the
    value of UNKNOWN_PLOCY is returned.
    Valid return values for the policy string are defined as
    constants for this class: ACCEPT_ON_MATCH, DENY_ON_MATCH, 
    ACCEPT_ON_NOMATCH, DENY_ON_NOMATCH, and UNKNOWN_POLICY. */
  public String getChainPolicy() {
    if (matchReturnValue == ACCEPT && noMatchReturnValue == NEUTRAL) {
      return ACCEPT_ON_MATCH;
    } else if (matchReturnValue == DENY && noMatchReturnValue == NEUTRAL) {
      return DENY_ON_MATCH;
    } else if (matchReturnValue == NEUTRAL && noMatchReturnValue == ACCEPT) {
      return ACCEPT_ON_NOMATCH;
    } else if (matchReturnValue == NEUTRAL && noMatchReturnValue == DENY) {
      return DENY_ON_NOMATCH;
    } else {
      return UNKNOWN_POLICY;
    }
  }
 
  /**
    Implementation that calls the canMatch() and match() methods
    of subclasses. If a match test can be performed (canMatch()
    returned true), then either the configured matchReturnValue
    or noMatchReturnValue will be returned. If no match test can
    be performed (canMatch() returned false), then Filter.NEUTRAL
    is returned. */
  public int decide(LoggingEvent event) {
    if (canMatch()) {
      if (match(event)) {
        return matchReturnValue;
      } else {
        return noMatchReturnValue;
      }
    }
    else
      return NEUTRAL;
  }

  /**
    Subclasses can override this method with their own version if
    it is possible that no match test can/should be performed due
    to a misconfiguration. This method should return true if a match
    test can be performed, and false if it cannot be performed. The
    default version always returns true. */
  protected boolean canMatch() {
    return true;
  }
  
  /**
    Subclasses must implement this method to perform the specific
    match test that they require. This method should return true
    if a match is made, and false if no match is made. */
  abstract protected boolean match(LoggingEvent event);
}