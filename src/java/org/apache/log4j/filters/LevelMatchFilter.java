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
  LevelMatchFilter is a very simple filter that matches a
  configured log level against the log level of a logging event.
  If they levels are the same, then the match() method returns
  true, else it returns false.
  
  <p>If a LevelMatchFilter is not configured with a level to match,
  then the canMatch() method will return false.
  
  <p>For more information about how the logging event will be
  passed to the appender for reporting, please see 
  the {@link MatchFilterBase} class.
  
  @author Ceki G&uuml;lc&uuml;
  @author Mark Womack;

  @since 1.3
*/
public class LevelMatchFilter extends MatchFilterBase {

  /**
    The level to match against. */
  Level levelToMatch;

  /**
    Sets the level to match against. */
  public void setLevelToMatch(String level) {
    levelToMatch = OptionConverter.toLevel(level, null);
  }
  
  /**
    Gets the level that will be matched against. */
  public String getLevelToMatch() {
    return levelToMatch == null ? null : levelToMatch.toString();
  }

  /**
    Overrides the implementation from the base class to return
    false if the levelToMatch has not been configured. */
  protected boolean canMatch() {
    return (levelToMatch != null);
  }
  /**
    Returns true if the levelToMatch matches the level of the
    logging event. */
  protected boolean match(LoggingEvent event) {
    return (levelToMatch.equals(event.getLevel()));
  }
}