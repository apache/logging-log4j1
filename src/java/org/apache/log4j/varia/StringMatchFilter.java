/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.log4j.varia;

import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

/**
   This is a very simple filter based on string matching.
   

   <p>The filter admits two options <b>StringToMatch</b> and
   <b>AcceptOnMatch</b>. If there is a match between the value of the
   StringToMatch option and the message of the {@link LoggingEvent},
   then the {@link #decide} method returns {@link Filter#ACCEPT} if
   the <b>AcceptOnMatch</b> option value is true, if it is false then
   {@link Filter#DENY} is returned. If there is no match, {@link
   Filter#NEUTRAL} is returned.

   @author Ceki G&uuml;lc&uuml;

   @since 0.9.0 */
public class StringMatchFilter extends Filter {
  
  boolean acceptOnMatch = true;
  String stringToMatch;
  
  public
  void setStringToMatch(String s) {
    stringToMatch = s;
  }
  
  public
  String getStringToMatch() {
    return stringToMatch;
  }
  
  public
  void setAcceptOnMatch(boolean acceptOnMatch) {
    this.acceptOnMatch = acceptOnMatch;
  }
  
  public
  boolean getAcceptOnMatch() {
    return acceptOnMatch;
  }

  /**
     Returns {@link Filter#NEUTRAL} is there is no string match.
   */
  public
  int decide(LoggingEvent event) {
    String msg = event.getRenderedMessage();

    if(msg == null ||  stringToMatch == null)
      return Filter.NEUTRAL;
    

    if( msg.indexOf(stringToMatch) == -1 ) {
      return Filter.NEUTRAL;
    } else { // we've got a match
      if(acceptOnMatch) {
	return Filter.ACCEPT;
      } else {
	return Filter.DENY;
      }
    }
  }
}
