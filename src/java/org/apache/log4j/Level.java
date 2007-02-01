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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;


/**
   Defines the minimum set of levels recognized by the system, that is
   <code>OFF</code>, <code>FATAL</code>, <code>ERROR</code>,
   <code>WARN</code>, <code>INFO</code>, <code>DEBUG</code>,
   <code>TRACE</code>, and <code>ALL</code>.

   <p>The <code>Level</code> class may be subclassed to define a larger
   level set.

   @author Ceki G&uuml;lc&uuml;
   @author Yoav Shapira
   @author Curt Arnold
 */
public class Level extends Priority implements Serializable {

  /**
   * TRACE level integer value.
   * @since 1.2.12
   */
  public static final int TRACE_INT = 5000;


  /**
   * The <code>OFF</code> has the highest possible rank and is
   * intended to turn off logging.
   */
  public static final Level OFF = new Level(OFF_INT, "OFF", 0);

  /**
   * The <code>FATAL</code> level designates very severe error
   * events that will presumably lead the application to abort.
   */
  public static final Level FATAL = new Level(FATAL_INT, "FATAL", 0);

  /**
   * The <code>ERROR</code> level designates error events that
   * might still allow the application to continue running.
   */
  public static final Level ERROR = new Level(ERROR_INT, "ERROR", 3);

  /**
   * The <code>WARN</code> level designates potentially harmful situations.
   */
  public static final Level WARN = new Level(WARN_INT, "WARN", 4);

  /**
   * The <code>INFO</code> level designates informational messages
   * that highlight the progress of the application at coarse-grained
   * level.
   */
  public static final Level INFO = new Level(INFO_INT, "INFO", 6);

  /**
   * The <code>DEBUG</code> Level designates fine-grained
   * informational events that are most useful to debug an
   * application.
   */
  public static final Level DEBUG = new Level(DEBUG_INT, "DEBUG", 7);

  /**
   * The <code>TRACE</code> Level designates finer-grained
   * informational events than the <code>DEBUG</code level.
   * @since 1.2.12
   */
  public static final Level TRACE = new Level(TRACE_INT, "TRACE", 7);

  /**
   * The <code>ALL</code> has the lowest possible rank and is intended to
   * turn on all logging.
   */
  public static final Level ALL = new Level(ALL_INT, "ALL", 7);


  /**
   * Serialization version id.
   */
  static final long serialVersionUID = 3491141966387921974L;


  /**
   * Instantiate a level object.
   *
   * @param level The integer level value
   * @param levelStr The level name
   * @param syslogEquivalent The UNIX SystLog level equivalent
   */
  protected Level(int level, String levelStr, int syslogEquivalent) {
    super(level, levelStr, syslogEquivalent);
  }

  /**
   * Convert the string passed as argument to a level. If the
   * conversion fails, then this method returns {@link #DEBUG}.
   *
   * @param sArg The level name
   * @return The matching Level object
   */
  public static Level toLevel(String sArg) {
    return toLevel(sArg, Level.DEBUG);
  }

  /**
   * Convert an integer passed as argument to a level. If the
   * conversion fails, then this method returns {@link #DEBUG}.
   *
   * @param val The level integer value
   * @return The matching Level object
   */
  public static Level toLevel(int val) {
    return toLevel(val, Level.DEBUG);
  }

  /**
   * Two Levels (formerly Priorities) are equal if their level
   * integer value fields are equal.  If the argument is not
   * a Level, this method returns False.
   *
   * @param o The other Level
   * @return boolean True if equals
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
   * The hashCode of a Level (i.e. Priority) is its level field.
   *
   * @return The integer level value
   */
  public int hashCode() {
    return level;
  }




  /**
   * Convert an integer passed as argument to a level. If the
   * conversion fails, then this method returns the specified default.
   *
   * @param val The integer value
   * @param defaultLevel The Level to return if no match is found
   * @return The matching Level
   */
  public static Level toLevel(int val, Level defaultLevel) {
    switch (val) {
    case ALL_INT:
      return ALL;
    case TRACE_INT:
      return TRACE;
    case DEBUG_INT:
      return DEBUG;
    case INFO_INT:
      return INFO;
    case WARN_INT:
      return WARN;
    case ERROR_INT:
      return ERROR;
    case FATAL_INT:
      return FATAL;
    case OFF_INT:
      return OFF;
    default:
      return defaultLevel;
    }
  }

  /**
   * Convert the string passed as argument to a level. If the
   * conversion fails, then this method returns the
   * <code>defaultLevel</code>.
   *
   * @param sArg The Level name
   * @param defaultLevel Level to return if no match is found
   * @return The matching Level
   */
  public static Level toLevel(String sArg, Level defaultLevel) {
    if (sArg == null) {
      return defaultLevel;
    }

    String s = sArg.toUpperCase();

    if (s.equals("ALL")) {
      return ALL;
    }

    if (s.equals("TRACE")) {
      return TRACE;
    }

    if (s.equals("DEBUG")) {
      return DEBUG;
    }

    if (s.equals("INFO")) {
      return INFO;
    }

    if (s.equals("WARN")) {
      return WARN;
    }

    if (s.equals("ERROR")) {
      return ERROR;
    }

    if (s.equals("FATAL")) {
      return FATAL;
    }

    if (s.equals("OFF")) {
      return OFF;
    }

    return defaultLevel;
  }

  /**
   * Custom deserialization of Level.
   * @param s serialization stream.
   * @throws IOException if IO exception.
   * @throws ClassNotFoundException if class not found.
   */
  private void readObject(final ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    level = s.readInt();
    syslogEquivalent = s.readInt();
    levelStr = s.readUTF();
  }

  /**
   * Serialize level.
   * @param s serialization stream.
   * @throws IOException if exception during serialization.
   */
  private void writeObject(final ObjectOutputStream s) throws IOException {
      s.defaultWriteObject();
      s.writeInt(level);
      s.writeInt(syslogEquivalent);
      if (levelStr == null) {
          s.writeUTF("");
      } else {
          s.writeUTF(levelStr);
      }
  }

  /**
   * Resolved deserialized level to one of the stock instances
   * if possible.
   * @return resolved object.
   * @throws ObjectStreamException if exception during resolution.
   */
  private Object readResolve() throws ObjectStreamException {
      return toLevel(level, this);
  }
}
