/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */

// Contributors:  Kitching Simon <Simon.Kitching@orange.ch>

package org.apache.log4j;
  
/**
   <font color="#AA4444">Refrain from using this class directly, use
   the {@link Level} class instead</font>.

   @author Ceki G&uuml;lc&uuml; */
public class Priority {

  int level;
  String levelStr;
  int syslogEquivalent;

  public final static int OFF_INT = Integer.MAX_VALUE;
  public final static int FATAL_INT = 50000;
  public final static int ERROR_INT = 40000;
  public final static int WARN_INT  = 30000;
  public final static int INFO_INT  = 20000;
  public final static int DEBUG_INT = 10000;
    //public final static int FINE_INT = DEBUG_INT;
  public final static int ALL_INT = Integer.MIN_VALUE;

  /**
   * @deprecated Use {@link Level#FATAL} instead.
   */
  final static public Priority FATAL = new Level(FATAL_INT, "FATAL", 0);

  /**
   * @deprecated Use {@link Level#ERROR} instead.
   */
  final static public Priority ERROR = new Level(ERROR_INT, "ERROR", 3);

  /**
   * @deprecated Use {@link Level#WARN} instead.
   */
  final static public Priority WARN  = new Level(WARN_INT, "WARN",  4);

  /**
   * @deprecated Use {@link Level#INFO} instead.
   */
  final static public Priority INFO  = new Level(INFO_INT, "INFO",  6);

  /**
   * @deprecated Use {@link Level#DEBUG} instead.
   */
  final static public Priority DEBUG = new Level(DEBUG_INT, "DEBUG", 7);

  
  /**
     Instantiate a level object.
   */
  protected
  Priority(int level, String levelStr, int syslogEquivalent) {
    this.level = level;
    this.levelStr = levelStr;
    this.syslogEquivalent = syslogEquivalent;
  }

  /**
     Two priorities are equal if their level fields are equal.
     @since 1.2
   */
  public
  boolean equals(Object o) {
    if(o instanceof Priority) {
      Priority r = (Priority) o;
      return (this.level == r.level);
    } else {
      return false;
    }
  }

  /**
     Return the syslog equivalent of this priority as an integer.
   */
  public
  final
  int getSyslogEquivalent() {
    return syslogEquivalent;
  }


   
  /**
     Returns <code>true</code> if this level has a higher or equal
     level than the level passed as argument, <code>false</code>
     otherwise.  
     
     <p>You should think twice before overriding the default
     implementation of <code>isGreaterOrEqual</code> method.

  */
  public
  boolean isGreaterOrEqual(Priority r) {
    return level >= r.level;
  }

  /**
     Return all possible priorities as an array of Level objects in
     descending order.

     @deprecated This method will be removed with no replacement.
  */
  public
  static
  Priority[] getAllPossiblePriorities() {
    return new Priority[] {Priority.FATAL, Priority.ERROR, Level.WARN, 
			   Priority.INFO, Priority.DEBUG};
  }


  /**
     Returns the string representation of this priority.
   */
  final
  public
  String toString() {
    return levelStr;
  }

  /**
     Returns the integer representation of this level.
   */
  public
  final
  int toInt() {
    return level;
  }

  /**
   * @deprecated Please use the {@link Level#toLevel(String)} method instead.
  */
  public
  static
  Priority toPriority(String sArg) {
    return Level.toLevel(sArg);
  }

  /**
   * @deprecated Please use the {@link Level#toLevel(int)} method instead.   
   */
  public
  static
  Priority toPriority(int val) {
    return toPriority(val, Priority.DEBUG);
  }

  /**
   * @deprecated Please use the {@link Level#toLevel(int, Level)} method instead.   
  */
  public
  static
  Priority toPriority(int val, Priority defaultPriority) {
    return Level.toLevel(val, (Level) defaultPriority);
  }

  /**
   * @deprecated Please use the {@link Level#toLevel(String, Level)} method instead.   
   */
  public
  static
  Priority toPriority(String sArg, Priority defaultPriority) {                  
    return Level.toLevel(sArg, (Level) defaultPriority);
  }
}
