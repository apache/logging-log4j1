/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j.filters;

import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

/**
  LevelMatchFilter is a very simple filter based on level matching, 
  which can be used to reject logging events with levels outside a 
  certain range. If they levels are within the range, then the 
  match() method returns true, else it returns false.
    
  <p>If <code>LevelMin</code> is not defined, then there is no
  minimum acceptable level (ie a level is never rejected for
  being too "low"/unimportant).  If <code>LevelMax</code> is not
  defined, then there is no maximum acceptable level (ie a
  level is never rejected for being too "high"/important).
  
  <p>Refer to the {@link
  org.apache.log4j.AppenderSkeleton#setThreshold setThreshold} method
  available to <code>all</code> appenders extending {@link
  org.apache.log4j.AppenderSkeleton} for a more convenient way to
  filter out events by level.

  <p>For more information about how the logging event will be
  passed to the appender for reporting, please see 
  the {@link MatchFilterBase} class.
  
  @author Simon Kitching
  @author based on code by Ceki G&uuml;lc&uuml; 
  @author Mark Womack;

  @since 1.3
*/
public class LevelRangeMatchFilter extends MatchFilterBase {

  /**
    Minimum level to match against. */
  Level levelMin;
  
  /**
    Maximum level to match against. */
  Level levelMax;

  /**
    Set the <code>LevelMax</code> option. */
  public void setLevelMax(Level levelMax) {
    this.levelMax = levelMax;
  }

  /**
    Get the value of the <code>LevelMax</code> option. */
  public Level getLevelMax() {
    return levelMax;
  }

  /**
    Set the <code>LevelMin</code> option. */
  public void setLevelMin(Level levelMin) {
    this.levelMin = levelMin;
  }

  /**
    Get the value of the <code>LevelMin</code> option. */
  public Level getLevelMin() {
    return levelMin;
  }
  
  /**
    Returns true if the the level of the logging event is in
    the configured range of <code>LevelMin</code> and
    <code>LevelMax</code>. */
  protected boolean match(LoggingEvent event) {
    if(this.levelMin != null) {
      if (event.getLevel().isGreaterOrEqual(levelMin) == false) {
        // level of event is less than minimum
        return false;
      }
    }

    if(this.levelMax != null) {
      if (event.getLevel().toInt() > levelMax.toInt()) {
        // level of event is greater than maximum
        // Alas, there is no Level.isGreater method. and using
        // a combo of isGreaterOrEqual && !Equal seems worse than
        // checking the int values of the level objects..
        return false;
      }
    }

    // return true match
    return true;
  }
}