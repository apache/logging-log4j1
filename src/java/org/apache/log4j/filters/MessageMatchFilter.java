/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j.filters;

import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.helpers.OptionConverter;

/**
  MessageMatchFilter is a very simple filter that matches a 
  configured value against the message value of a logging event.
  
  <p>The filter admits two options <b>MessageToMatch</b> and
  <b>ExactMatch</b>.
  
  <p>As the name indicates, the value of <b>MessageToMatch</b> property
  determines the string value to match. If <b>ExactMatch</b> is set
  to true, a match will occur only when <b>MessageToMatch</b> exactly
  matches the message value of the logging event.  Otherwise, if the
  <b>ExactMatch</b> property is set to <code>false</code>, a match
  will occur when <b>MessageToMatch</b> is contained anywhere within the
  message value. The <b>ExactMatch</b> property is set to
  <code>false</code> by default.
  
  <p>Note that by default <b>MessageToMatch</b> is set to
  <code>null</code> and will only match a null message.

  <p>For more information about how the logging event will be
  passed to the appender for reporting, please see 
  the {@link MatchFilterBase} class.

  @author Mark Womack;

  @since 1.3
*/
public class MessageMatchFilter extends MatchFilterBase {

  /**
    The message match against. */
  String messageToMatch;

  /**
    Do we look for an exact match or just a "contains" match? */
  boolean exactMatch = false;

  /**
    Sets the string to match against the logging event message. */
  public void setMessageToMatch(String _message) {
    messageToMatch = _message;
  }
  
  public String getMessageToMatch() {
    return messageToMatch;
  }

  /**
    Set to true if configured value must exactly match the message
    value of the LoggingEvent. Set to false if the configured
    value must only be contained in the message value of the
    LoggingEvent. Default is false. */
  public void setExactMatch(boolean exact) {
    exactMatch = exact;
  }
  
  public boolean getExactMatch() {
    return exactMatch;
  }
  
  /**
    If <b>ExactMatch</b> is set to true, returns true only when
    <b>MessageToMatch</b> exactly matches the message value of the 
    logging event. If the <b>ExactMatch</b> property
    is set to <code>false</code>, returns true when 
    <b>MessageToMatch</b> is contained anywhere within the message
    value. Otherwise, false is returned. */
  protected boolean match(LoggingEvent event) {
    String msg = event.getRenderedMessage();
    if (msg == null) {
      return (messageToMatch == null);
    } else {
      if (messageToMatch != null) {
        if (exactMatch) {
          return messageToMatch.equals(msg);
        } else {
          return (msg.indexOf(messageToMatch) != -1);
        }
      }
    }
    
    return false;
  }
}