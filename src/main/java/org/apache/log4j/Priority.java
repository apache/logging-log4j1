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
package org.apache.log4j;


/**
 * <font color="#AA4444">Refrain from using this class directly, use the 
 * {@link Level} class instead</font>. 
 * 
 * @author Ceki G&uuml;lc&uuml; 
 */
public class Priority {
    /**
     * The integer value of this Level instance.
     */
    transient int level;
    /**
     * The label of this Level instance.
     */
    transient String levelStr;
    /**
     * The UNIX SysLog equivalent value of this Level instance.
     */
    transient int syslogEquivalent;

    /**
     * OFF level integer value.
     */
    public static final int OFF_INT = Integer.MAX_VALUE;

    /**
     * FATAL level integer value.
     */
    public static final int FATAL_INT = 50000;

    /**
     * ERROR level integer value.
     */
    public static final int ERROR_INT = 40000;

    /**
     * WARN level integer value.
     */
    public static final int WARN_INT = 30000;

    /**
     * INFO level integer value.
     */
    public static final int INFO_INT = 20000;

    /**
     * DEBUG level integer value.
     */
    public static final int DEBUG_INT = 10000;


    /**
     * ALL level integer value.
     */
    public static final int ALL_INT = Integer.MIN_VALUE;

  

  /**
     The <code>FATAL</code> level designates very severe error
     events that will presumably lead the application to abort.
   @deprecated
   */
  public static final Priority FATAL = new Level(FATAL_INT, "FATAL", 0);

  /**
     The <code>ERROR</code> level designates error events that
     might still allow the application to continue running.
   @deprecated
   */
  public static final Priority ERROR = new Level(ERROR_INT, "ERROR", 3);

  /**
     The <code>WARN</code> level designates potentially harmful situations.
   @deprecated
  */
  public static final Priority WARN = new Level(WARN_INT, "WARN", 4);

  /**
     The <code>INFO</code> level designates informational messages
     that highlight the progress of the application at coarse-grained
     level.
   @deprecated
   */
  public static final Priority INFO = new Level(INFO_INT, "INFO", 6);

  /**
     The <code>DEBUG</code> priority designates fine-grained
     informational events that are most useful to debug an
     application.
   @deprecated
   */
  public static final Priority DEBUG = new Level(DEBUG_INT, "DEBUG", 7);

    /**
      * Default constructor for deserialization.
      */
    protected Priority() {
        level = Level.DEBUG_INT;
        levelStr = "DEBUG";
        syslogEquivalent = 7;
    }

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
     * Returns a hash code based on the level.
     */
    public
    int hashCode() {
      return level;
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
   @deprecated

  */
  public static Priority toPriority(int val) {
    return toPriority(val, Priority.DEBUG);
  }

  /**
    Convert an integer passed as argument to a priority. If the
    conversion fails, then this method returns the specified default.
   @deprecated
  */
  public static Priority toPriority(int val, Priority defaultPriority) {
    return Level.toLevel(val, (Level) defaultPriority);
  }

  /**
     Convert the string passed as argument to a priority. If the
     conversion fails, then this method returns the value of
     <code>defaultPriority</code>.
   @deprecated
  */
  public static Priority toPriority(String sArg, Priority defaultPriority) {
    return Level.toLevel(sArg, (Level) defaultPriority);
  }
}
