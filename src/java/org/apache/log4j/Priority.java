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
 * @deprecated 
 */
public class Priority extends Level {
  
  private Priority(int level, String levelStr, int syslogEquivalent) {
    super(level, levelStr, syslogEquivalent);
  }

  
  /**
     The <code>FATAL</code> level designates very severe error
     events that will presumably lead the application to abort.
   */
  //public static final Priority FATAL = new Level(FATAL_INT, "FATAL", 0);

  /**
     The <code>ERROR</code> level designates error events that
     might still allow the application to continue running.  */
 // public static final Priority ERROR = new Level(ERROR_INT, "ERROR", 3);

  /**
     The <code>WARN</code> level designates potentially harmful situations.
  */
 // public static final Priority WARN = new Level(WARN_INT, "WARN", 4);

  /**
     The <code>INFO</code> level designates informational messages
     that highlight the progress of the application at coarse-grained
     level.  */
 // public static final Priority INFO = new Level(INFO_INT, "INFO", 6);

  /**
     The <code>DEBUG</code> priority designates fine-grained
     informational events that are most useful to debug an
     application.  */
  //public static final Priority DEBUG = new Level(DEBUG_INT, "DEBUG", 7);


  /**
     Convert the string passed as argument to a priority. If the
     conversion fails, then this method returns {@link #DEBUG}.

     @deprecated Please use the {@link Level#toLevel(String)} method instead.}


  */
//  public static Level toPriority(String sArg) {
//    return Level.toLevel(sArg);
//  }

  /**
    Convert an integer passed as argument to a priority. If the
    conversion fails, then this method returns {@link #DEBUG}.

  */
//  public static Priority toPriority(int val) {
//    return toPriority(val, Priority.DEBUG);
//  }

  /**
    Convert an integer passed as argument to a priority. If the
    conversion fails, then this method returns the specified default.
  */
//  public static Priority toPriority(int val, Priority defaultPriority) {
//    return Level.toLevel(val, (Level) defaultPriority);
//  }

  /**
     Convert the string passed as argument to a priority. If the
     conversion fails, then this method returns the value of
     <code>defaultPriority</code>.
  */
//  public static Priority toPriority(String sArg, Priority defaultPriority) {
//    return Level.toLevel(sArg, (Level) defaultPriority);
//  }
}
