//  Copyright (c) 2000 Ceki Gulcu.  All Rights Reserved.
//  See the LICENCE file for the terms of distribution.

package org.apache.log4j.varia;

import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.helpers.OptionConverter;

/**
   This is a very simple filter based on string matching.
   

   <p>The filter admits two options <b>StringToMatch</b> and
   <b>AcceptOnMatch</b>. If there is a match between the value of the
   StringToMatch option and the message of the {@link LoggingEvent},
   then the {@link #decide} method returns {@link Filter#ACCEPT} if
   the <b>AcceptOnMatch</b> option value is true, if it is false then
   {@link Filter#DENY} is returned. If there is no match, {@link
   Filter#NEUTRAL} is returned.

   <p>See configuration files <a
   href="../xml/doc-files/test6.xml">test6.xml</a>, <a
   href="../xml/doc-files/test7.xml">test7.xml</a>, <a
   href="../xml/doc-files/test8.xml">test8.xml</a>, <a
   href="../xml/doc-files/test9.xml">test9.xml</a>, and <a
   href="../xml/doc-files/test10.xml">test10.xml</a> for examples of
   seeting up a <code>StringMatchFilter</code>.


   @author Ceki G&uuml;lc&uuml;

   @since 0.9.0 */
public class StringMatchFilter extends Filter {

  
  public static final String STRING_TO_MATCH_OPTION = "StringToMatch";

  /**
   */
  public static final String ACCEPT_ON_MATCH_OPTION = "AcceptOnMatch";
  
  boolean acceptOnMatch = true;
  String stringToMatch;


  /**
     Returns {@link Filter#NEUTRAL} is there is no string match.
   */
  public
  int decide(LoggingEvent event) {
    if(event.message == null ||  stringToMatch == null)
      return Filter.NEUTRAL;
    
    // we've got a string message
    if(event.message instanceof String) {      
      String msg = (String) event.message;
      // match
      if( msg.indexOf(stringToMatch) == -1 ) {
	return Filter.NEUTRAL;
      } else { // we've got a match
	if(acceptOnMatch) {
	  return Filter.ACCEPT;
	} else {
	  return Filter.DENY;
	}
      }
    } else { // we've got an non-string message
      return Filter.NEUTRAL;      
    }
    

  }

  public
  String[] getOptionStrings() {
    return new String[] {STRING_TO_MATCH_OPTION, ACCEPT_ON_MATCH_OPTION};
  }

  public
  void setOption(String key, String value) { 
    
    if(key.equalsIgnoreCase(STRING_TO_MATCH_OPTION)) {
      stringToMatch = value;
    } else if (key.equalsIgnoreCase(ACCEPT_ON_MATCH_OPTION)) {
      acceptOnMatch = OptionConverter.toBoolean(value, acceptOnMatch);
    }
  }
  
}
