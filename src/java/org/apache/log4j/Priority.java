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
   Defines the minimum set of priorities recognized by the system,
   that is {@link #FATAL}, {@link #ERROR}, {@link #WARN}, {@link
   #INFO} and {@link #DEBUG}.

   <p>The <code>Priority</code> class may be subclassed to define a larger
   priority set.

   @author Ceki G&uuml;lc&uuml;
 */
public class Priority extends Level {

  //final static public Priority FATAL = new Priority(FATAL_INT, "FATAL", 0);
  //final static public Priority ERROR = new Priority(ERROR_INT, "ERROR", 3);
  //final static public Priority WARN  = new Priority(WARN_INT, "WARN",  4);
  //final static public Priority INFO  = new Priority(INFO_INT, "INFO",  6);
  //final static public Priority DEBUG = new Priority(DEBUG_INT, "DEBUG", 7);
    
  /**
     Instantiate a level object.
   */
  protected
  Priority(int level, String levelStr, int syslogEquivalent) {
    super(level, levelStr, syslogEquivalent);
  }

}
