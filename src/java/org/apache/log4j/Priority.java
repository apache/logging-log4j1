/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.APL file.
 */

package org.apache.log4j;

/**
   Defines the minimum set of priorities recognized by the system,
   that is {@link #FATAL}, {@link #ERROR}, {@link #WARN}, {@link
   #INFO} and {@link #DEBUG}.

   <p>The <code>Priority</code> class may be subclassed to define a larger
   priority set.
   
 */
public class Priority {

  int level;
  String levelStr;
  int syslogEquivalent;

  final static int FATAL_INT = 50000;
  final static int ERROR_INT = 40000;
  final static int WARN_INT  = 30000;
  final static int INFO_INT  = 20000;
  final static int DEBUG_INT = 10000;

  /**
     The <code>FATAL</code> priority designates very severe error
     events that will presumably lead the application to abort.

   */
  final static public Priority FATAL = new Priority(FATAL_INT, "FATAL", 0);

  /**
     The <code>ERROR</code> priority designates error events that
     might still allow the application to continue running.  */
  final static public Priority ERROR = new Priority(ERROR_INT, "ERROR", 3);

  /**
     The <code>WARN</code> priority designates potentially harmful situations.
  */
  final static public Priority WARN  = new Priority(WARN_INT, "WARN",  4);

  /**
     The <code>INFO</code> priority designates informational messages
     that higlight the progress of the application at coarse-grained
     level.  */
  final static public Priority INFO  = new Priority(INFO_INT, "INFO",  5);

  /**
     The <code>DEBUG</code> priority designates fine-grained
     informational events that are most useful to debug an
     application.  */
  final static public Priority DEBUG = new Priority(DEBUG_INT, "DEBUG", 6);

  
  /**
     Instantiate a priority object.
   */
  protected
  Priority(int level, String levelStr, int syslogEquivalent) {
    this.level = level;
    this.levelStr = levelStr;
    this.syslogEquivalent = syslogEquivalent;
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
     Returns the string representation of this priority.
   */
  final
  public
  String toString() {
    return levelStr;
  }

  /**
     Returns the integer representation of this priority.
   */
  public
  final
  int toInt() {
    return level;
  }

    
  /**
     Returns <code>true</code> if this priority has a higher or equal
     priority than the priority passed as argument, <code>false</code>
     otherwise.  
     
     <p>You should think twice before overriding the default
     implementation of <code>isGreaterOrEqual</code> method.

  */
  public
  boolean isGreaterOrEqual(Priority r) {
    return level >= r.level;
  }

  /**
     Return all possible priorities as an array of Priority objects in
     descending order.  */
  public
  static
  Priority[] getAllPossiblePriorities() {
    return new Priority[] {Priority.FATAL, Priority.ERROR, Priority.WARN, 
			     Priority.INFO, Priority.DEBUG};
  }


  /**
     Convert the string passed as argument to a priority. If the
     conversion fails, then this method returns {@link #DEBUG}. 
  */
  public
  static
  Priority toPriority(String sArg) {
    return toPriority(sArg, Priority.DEBUG);
  }

  /**
    Convert an integerq passed as argument to a priority. If the
    conversion fails, then this method returns {@link #DEBUG}.      

  */
  public
  static
  Priority toPriority(int val) {
    switch(val) {
    case DEBUG_INT: return DEBUG;
    case INFO_INT: return INFO;
    case WARN_INT: return WARN;
    case ERROR_INT: return ERROR;
    case FATAL_INT: return FATAL;
    default: return DEBUG;
    }
  }

  /**
     Convert the string passed as argument to a priority. If the
     conversion fails, then this method returns the value of
     <code>defaultPriority</code>.  
  */
  public
  static
  Priority toPriority(String sArg, Priority defaultPriority) {                  
    if(sArg == null)
       return defaultPriority;
    
    String s = sArg.toUpperCase();

    if(s.equals("DEBUG")) return Priority.DEBUG; 
    if(s.equals("INFO"))  return Priority.INFO;
    if(s.equals("WARN"))  return Priority.WARN;  
    if(s.equals("ERROR")) return Priority.ERROR;
    if(s.equals("FATAL")) return Priority.FATAL;
    return defaultPriority;
  }

}
