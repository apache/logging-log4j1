/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */

// Contributors:  Kitching Simon <Simon.Kitching@orange.ch>
//                Nicolas Wolff

package org.apache.log4j;

/**
   Defines the minimum set of priorities recognized by the system,
   that is <code>FATAL</code>, <code>ERROR</code>, <code>WARN</code>,
   <code>INFO</code> and <code>DEBUG</code>.

   <p>The <code>Level</code> class may be subclassed to define a larger
   priority set.

   @author Ceki G&uuml;lc&uuml;

 */
public class Level extends Priority {


  /**
     The <code>OFF</code> has the highest possible rank and is
     intended to turn off logging.  */
  final static public Level OFF = new Level(OFF_INT, "OFF", 0);

  /**
     The <code>ALL</code> has the lowest possible rank and is intended to
     turn on all logging.  */
  final static public Level ALL = new Level(ALL_INT, "ALL", 7);


  /**
     Instantiate a priority object.
   */
  protected
  Level(int level, String levelStr, int syslogEquivalent) {
    super(level, levelStr, syslogEquivalent);
  }


  /**
     Convert the string passed as argument to a level. If the
     conversion fails, then this method returns {@link #DEBUG}. 
  */
  public
  static
  Level toLevel(String sArg) {
    return (Level) toLevel(sArg, Priority.DEBUG);
  }

  /**
    Convert an integer passed as argument to a level. If the
    conversion fails, then this method returns {@link #DEBUG}.

  */
  public
  static
  Level toLevel(int val) {
    return (Level) toLevel(val, Priority.DEBUG);
  }

  /**
    Convert an integer passed as argument to a level. If the
    conversion fails, then this method returns the specified default.
  */
  public
  static
  Priority toLevel(int val, Priority defaultLevel) {
    switch(val) {
    case ALL_INT: return ALL;
    case DEBUG_INT: return (Level) Priority.DEBUG;
    case INFO_INT: return (Level) Priority.INFO;
    case WARN_INT: return (Level) Priority.WARN;
    case ERROR_INT: return (Level) Priority.ERROR;
    case FATAL_INT: return (Level) Priority.FATAL;
    case OFF_INT: return OFF;
    default: return defaultLevel;
    }
  }

  /**
     Convert the string passed as argument to a level. If the
     conversion fails, then this method returns the value of
     <code>defaultLevel</code>.  
  */
  public
  static
  Priority toLevel(String sArg, Priority defaultLevel) {                  
    if(sArg == null)
       return defaultLevel;
    
    String s = sArg.toUpperCase();

    if(s.equals("ALL")) return Level.ALL; 
    if(s.equals("DEBUG")) return (Level) Priority.DEBUG; 
    //if(s.equals("FINE")) return Level.FINE; 
    if(s.equals("INFO"))  return (Level) Priority.INFO;
    if(s.equals("WARN"))  return (Level) Priority.WARN;  
    if(s.equals("ERROR")) return (Level) Priority.ERROR;
    if(s.equals("FATAL")) return (Level) Priority.FATAL;
    if(s.equals("OFF")) return Level.OFF;
    return defaultLevel;
  }
}
