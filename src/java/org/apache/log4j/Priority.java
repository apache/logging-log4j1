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


  //final static public Level DEBUG = Level.DEBUG;
  final static public Level INFO = Level.INFO;
  final static public Level WARN = Level.WARN;
    
  /**
     Instantiate a level object.
   */
  protected
  Priority(int level, String levelStr, int syslogEquivalent) {
    super(level, levelStr, syslogEquivalent);
  }

}
