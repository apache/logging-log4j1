/*
 * Copyright 1999,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


// Contributors:  Kitching Simon <Simon.Kitching@orange.ch>
//                Nicholas Wolff
package org.apache.log4j;


/**
   Defines the minimum set of levels recognized by the system, that is
   <code>OFF</code>, <code>FATAL</code>, <code>ERROR</code>,
   <code>WARN</code>, <code>INFO</code, <code>DEBUG</code> and
   <code>ALL</code>.

   <p>The <code>Level</code> class may be subclassed to define a larger
   level set.

   @author Ceki G&uuml;lc&uuml;

 */
public class Level {
  public static final int OFF_INT = Integer.MAX_VALUE;
  public static final int FATAL_INT = 50000;
  public static final int ERROR_INT = 40000;
  public static final int WARN_INT = 30000;
  public static final int INFO_INT = 20000;
  public static final int DEBUG_INT = 10000;

  //public final static int FINE_INT = DEBUG_INT;
  public static final int ALL_INT = Integer.MIN_VALUE;
  
  /**
     The <code>OFF</code> has the highest possible rank and is
     intended to turn off logging.  */
  public static final Level OFF = new Level(OFF_INT, "OFF", 0);

  /**
     The <code>FATAL</code> level designates very severe error
     events that will presumably lead the application to abort.
   */
  public static final Level FATAL = new Level(FATAL_INT, "FATAL", 0);

  /**
     The <code>ERROR</code> level designates error events that
     might still allow the application to continue running.  */
  public static final Level ERROR = new Level(ERROR_INT, "ERROR", 3);

  /**
     The <code>WARN</code> level designates potentially harmful situations.
  */
  public static final Level WARN = new Level(WARN_INT, "WARN", 4);

  /**
     The <code>INFO</code> level designates informational messages
     that highlight the progress of the application at coarse-grained
     level.  */
  public static final Level INFO = new Level(INFO_INT, "INFO", 6);

  /**
     The <code>DEBUG</code> Level designates fine-grained
     informational events that are most useful to debug an
     application.  */
  public static final Level DEBUG = new Level(DEBUG_INT, "DEBUG", 7);

  /**
     The <code>ALL</code> has the lowest possible rank and is intended to
     turn on all logging.  */
  public static final Level ALL = new Level(ALL_INT, "ALL", 7);

  
  int level;
  String levelStr;
  int syslogEquivalent;

  /**
    Instantiate a level object.
  */ 
  protected Level(int level, String levelStr, int syslogEquivalent) {
   this.level = level;
   this.levelStr = levelStr;
   this.syslogEquivalent = syslogEquivalent;
  }

  /**
     Convert the string passed as argument to a level. If the
     conversion fails, then this method returns {@link #DEBUG}.
  */
  public static Level toLevel(String sArg) {
    return toLevel(sArg, Level.DEBUG);
  }

  /**
    Convert an integer passed as argument to a level. If the
    conversion fails, then this method returns {@link #DEBUG}.

  */
  public static Level toLevel(int val) {
    return toLevel(val, Level.DEBUG);
  }
  /**
   * Two priorities are equal if their level fields are equal.
   * @since 1.2
   */
  public boolean equals(Object o) {
    if (o instanceof Level) {
      Level r = (Level) o;

      return (this.level == r.level);
    } else {
      return false;
    }
  }

  /**
   * The hashCode        of a Level (i.e. Priority) is its level field.
   */
  public int hashCode() {
    return level;
  }

  /**
     Return the syslog equivalent of this priority as an integer.
   */
  public final int getSyslogEquivalent() {
    return syslogEquivalent;
  }

  /**
     Returns <code>true</code> if this level has a higher or equal
     level than the level passed as argument, <code>false</code>
     otherwise.

     <p>You should think twice before overriding the default
     implementation of <code>isGreaterOrEqual</code> method.

  */
  public boolean isGreaterOrEqual(Level r) {
    return level >= r.level;
  }

  /**
  Return all possible priorities as an array of Level objects in
  descending order.

  @deprecated This method will be removed with no replacement.
*/
  public static Level[] getAllPossiblePriorities() {
    return new Level[] {
     Level.FATAL, Level.ERROR, Level.WARN, Level.INFO, Level.DEBUG
 };
}


  /**
  Returns the string representation of this priority.
  */
  public final String toString() {
    return levelStr;
  }

  /**
    Returns the integer representation of this level.
  */
  public final int toInt() {
    return level;
  }
  
  /**
    Convert an integer passed as argument to a level. If the
    conversion fails, then this method returns the specified default.
  */
  public static Level toLevel(int val, Level defaultLevel) {
    switch (val) {
    case ALL_INT:
      return ALL;

    case DEBUG_INT:
      return Level.DEBUG;

    case INFO_INT:
      return Level.INFO;

    case WARN_INT:
      return Level.WARN;

    case ERROR_INT:
      return Level.ERROR;

    case FATAL_INT:
      return Level.FATAL;

    case OFF_INT:
      return OFF;

    default:
      return defaultLevel;
    }
  }

  /**
     Convert the string passed as argument to a level. If the
     conversion fails, then this method returns the value of
     <code>defaultLevel</code>.
  */
  public static Level toLevel(String sArg, Level defaultLevel) {
    if (sArg == null) {
      return defaultLevel;
    }

    String s = sArg.toUpperCase();

    if (s.equals("ALL")) {
      return Level.ALL;
    }

    if (s.equals("DEBUG")) {
      return Level.DEBUG;
    }

    //if(s.equals("FINE")) return Level.FINE; 
    if (s.equals("INFO")) {
      return Level.INFO;
    }

    if (s.equals("WARN")) {
      return Level.WARN;
    }

    if (s.equals("ERROR")) {
      return Level.ERROR;
    }

    if (s.equals("FATAL")) {
      return Level.FATAL;
    }

    if (s.equals("OFF")) {
      return Level.OFF;
    }

    return defaultLevel;
  }
}
