/*
 * ============================================================================
 *                   The Apache Software License, Version 1.1
 * ============================================================================
 *
 *    Copyright (C) 1999 The Apache Software Foundation. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modifica-
 * tion, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of  source code must  retain the above copyright  notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any, must
 *    include  the following  acknowledgment:  "This product includes  software
 *    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
 *    Alternately, this  acknowledgment may  appear in the software itself,  if
 *    and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "log4j" and  "Apache Software Foundation"  must not be used to
 *    endorse  or promote  products derived  from this  software without  prior
 *    written permission. For written permission, please contact
 *    apache@apache.org.
 *
 * 5. Products  derived from this software may not  be called "Apache", nor may
 *    "Apache" appear  in their name,  without prior written permission  of the
 *    Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 * APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 * DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 * ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 * (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * This software  consists of voluntary contributions made  by many individuals
 * on  behalf of the Apache Software  Foundation.  For more  information on the
 * Apache Software Foundation, please see <http://www.apache.org/>.
 *
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
public class Level extends Priority {
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

  /**
     Instantiate a Level object.
   */
  protected Level(int level, String levelStr, int syslogEquivalent) {
    super(level, levelStr, syslogEquivalent);
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
