//  Copyright (c) 2000 Ceki Gulcu.  All Rights Reserved.
//  See the LICENCE file for the terms of distribution.

package org.apache.log4j.varia;

import org.apache.log4j.Priority;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.helpers.OptionConverter;

/**
   This is a very simple filter based on priority matching.


   <p>The filter admits two options <b>PriorityToMatch</b> and
   <b>AcceptOnMatch</b>. If there is an exact match between the value
   of the PriorityToMatch option and the priority of the {@link
   LoggingEvent}, then the {@link #decide} method returns {@link
   Filter#ACCEPT} in case the <b>AcceptOnMatch</b> option value is set
   to <code>true</code>, if it is <code>false</code> then {@link
   Filter#DENY} is returned. If there is no match, {@link
   Filter#NEUTRAL} is returned.

   <p>See configuration files <a
   href="../xml/doc-files/test11.xml">test11.xml</a> and <a
   href="../xml/doc-files/test12.xml">test12.xml</a> for an example of
   seeting up a <code>PriorityMatchFilter</code>.

   @author Ceki G&uuml;lc&uuml;

   @since 0.9.1 */
public class PriorityMatchFilter extends Filter {

  
  public static final String PRIORITY_TO_MATCH_OPTION = "PriorityToMatch";

  /**
   */
  public static final String ACCEPT_ON_MATCH_OPTION = "AcceptOnMatch";
  
  /**
     Do we return ACCEPT when a match occurs. Default is
     <code>true</code>.  */
  boolean acceptOnMatch = true;

  /**
   */
  Priority priorityToMatch;


  /**
     Return the decision of this filter.

     Returns {@link Filter#NEUTRAL} if the <b>PriorityToMatch</b>
     option is not set.  Otherwise, the returned decision is defined
     according to the following table:

     <p><table border=1>
     <tr><th rowspan="2" BGCOLOR="#AAAAFF">Did a priority match occur?</th>
     	 <th colspan="2" BGCOLOR="#CCCCCC">AcceptOnMatch setting</th>
     
     <tr><td BGCOLOR="#CCCCCC" align="center">TRUE</td>
     	 <td BGCOLOR="#CCCCCC" align="center">FALSE</td>
     
     <tr><td BGCOLOR="#AAAAFF" align="center">TRUE</td>
     	 <td>{@link Filter#ACCEPT}</td><td>{@link Filter#DENY}</td>
     <tr><td BGCOLOR="#AAAAFF" align="center">FALSE</td>
     	 <td>{@link Filter#DENY}</td><td>{@link Filter#ACCEPT}</td>
     
     	 <caption align="bottom">Filter decision in function of whether a match
     	 occured and the AcceptOnMatch settings</caption> 
    </table> */
  public
  int decide(LoggingEvent event) {
    if(this.priorityToMatch == null) {
      return Filter.NEUTRAL;
    }
    
    boolean matchOccured = false;
    if(this.priorityToMatch == event.priority) {
      matchOccured = true;
    }

    if(this.acceptOnMatch ^ matchOccured) {
      return Filter.DENY;
    } else {
      return Filter.ACCEPT;
    }
  }

  public
  String[] getOptionStrings() {
    return new String[] {PRIORITY_TO_MATCH_OPTION, ACCEPT_ON_MATCH_OPTION};
  }

  public
  void setOption(String key, String value) {    
    if(key.equalsIgnoreCase(PRIORITY_TO_MATCH_OPTION)) {
      priorityToMatch = Priority.toPriority(value, null);
    } else if (key.equalsIgnoreCase(ACCEPT_ON_MATCH_OPTION)) {
      acceptOnMatch = OptionConverter.toBoolean(value, acceptOnMatch);
    }
  }
  
}
