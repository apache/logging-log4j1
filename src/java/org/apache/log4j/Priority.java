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
package org.apache.log4j;


/**
   <font color="#AA4444">Refrain from using this class directly, use
   the {@link Level} class instead.</font>

   @author Ceki G&uuml;lc&uuml; */
public class Priority {
  public static final int OFF_INT = Integer.MAX_VALUE;
  public static final int FATAL_INT = 50000;
  public static final int ERROR_INT = 40000;
  public static final int WARN_INT = 30000;
  public static final int INFO_INT = 20000;
  public static final int DEBUG_INT = 10000;

  //public final static int FINE_INT = DEBUG_INT;
  public static final int ALL_INT = Integer.MIN_VALUE;

  /**
     The <code>FATAL</code> level designates very severe error
     events that will presumably lead the application to abort.
   */
  public static final Priority FATAL = new Level(FATAL_INT, "FATAL", 0);

  /**
     The <code>ERROR</code> level designates error events that
     might still allow the application to continue running.  */
  public static final Priority ERROR = new Level(ERROR_INT, "ERROR", 3);

  /**
     The <code>WARN</code> level designates potentially harmful situations.
  */
  public static final Priority WARN = new Level(WARN_INT, "WARN", 4);

  /**
     The <code>INFO</code> level designates informational messages
     that highlight the progress of the application at coarse-grained
     level.  */
  public static final Priority INFO = new Level(INFO_INT, "INFO", 6);

  /**
     The <code>DEBUG</code> priority designates fine-grained
     informational events that are most useful to debug an
     application.  */
  public static final Priority DEBUG = new Level(DEBUG_INT, "DEBUG", 7);
  int level;
  String levelStr;
  int syslogEquivalent;

  /**
     Instantiate a level object.
   */
  protected Priority(int level, String levelStr, int syslogEquivalent) {
    this.level = level;
    this.levelStr = levelStr;
    this.syslogEquivalent = syslogEquivalent;
  }

  /**
   * Two priorities are equal if their level fields are equal.
   * @since 1.2
   */
  public boolean equals(Object o) {
    if (o instanceof Priority) {
      Priority r = (Priority) o;

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
  public boolean isGreaterOrEqual(Priority r) {
    return level >= r.level;
  }

  /**
     Return all possible priorities as an array of Level objects in
     descending order.

     @deprecated This method will be removed with no replacement.
  */
  public static Priority[] getAllPossiblePriorities() {
    return new Priority[] {
      Priority.FATAL, Priority.ERROR, Level.WARN, Priority.INFO, Priority.DEBUG
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
     Convert the string passed as argument to a priority. If the
     conversion fails, then this method returns {@link #DEBUG}.

     @deprecated Please use the {@link Level#toLevel(String)} method instead.}


  */
  public static Priority toPriority(String sArg) {
    return Level.toLevel(sArg);
  }

  /**
    Convert an integer passed as argument to a priority. If the
    conversion fails, then this method returns {@link #DEBUG}.

  */
  public static Priority toPriority(int val) {
    return toPriority(val, Priority.DEBUG);
  }

  /**
    Convert an integer passed as argument to a priority. If the
    conversion fails, then this method returns the specified default.
  */
  public static Priority toPriority(int val, Priority defaultPriority) {
    return Level.toLevel(val, (Level) defaultPriority);
  }

  /**
     Convert the string passed as argument to a priority. If the
     conversion fails, then this method returns the value of
     <code>defaultPriority</code>.
  */
  public static Priority toPriority(String sArg, Priority defaultPriority) {
    return Level.toLevel(sArg, (Level) defaultPriority);
  }
}
