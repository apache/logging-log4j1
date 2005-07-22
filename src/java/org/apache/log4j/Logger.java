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

package org.apache.log4j;

import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.helpers.MessageFormatter;


/**
 * This is the central class in the log4j package. Most logging
 * operations, except configuration, are done through this class.
 * <p>
 * 
 * @author Ceki G&uuml;lc&uuml;
 * @since log4j 1.2
*/
public class Logger extends Category {
  /**
   * The fully qualified name of the Logger class. See also the {@link #getFQCN}
   * method.
   */
  private static final String FQCN = Logger.class.getName();

  /**
   * This constructor creates a new <code>Logger</code> instance and sets
   * its name.
   *
   * <p>
   * It is intended to be used by sub-classes only. You should not create
   * loggers directly.
   * </p>
   *
   * @param name The name of the logger.
   */
  protected Logger(String name) {
    super(name);
  }

  /**
    * Retrieve a logger by name. If the named logger already exists, then the
    * existing instance will be reutrned. Otherwise, a new instance is created.
    *
    * <p>By default, loggers do not have a set level but inherit it from their
    * ancestors. This is one of the central features of log4j.
    * </p>
    *
    * @param name The name of the logger to retrieve.
  */
  public static Logger getLogger(String name) {
    return LogManager.getLogger(name);
  }

  /**
   * Shorthand for <code>{@link #getLogger(Class) getLogger(clazz.getName())}</code>.
   *
   * @param clazz The name of <code>clazz</code> will be used as the name of
   *        the logger to retrieve.  See {@link #getLogger(String)} for
   *        more detailed information.
   */
  public static Logger getLogger(Class clazz) {
    return LogManager.getLogger(clazz.getName());
  }

  /**
   * Return the root of logger for the current hierarchy.
   *
   * <p>The root logger is always instantiated and available. It's name is
   * "root".
   * </p>
   *
   * <p>Nevertheless, note that calling <code>Logger.getLogger("root")</code>
   * does not retrieve the root logger but a logger just under root named
   * "root".
   * </p>
   */
  public static Logger getRootLogger() {
    return LogManager.getRootLogger();
  }

  /**
     Like {@link #getLogger(String)} except that the type of logger
     instantiated depends on the type returned by the {@link
     LoggerFactory#makeNewLoggerInstance} method of the
     <code>factory</code> parameter.

     <p>This method is intended to be used by sub-classes.

     @param name The name of the logger to retrieve.

     @param factory A {@link LoggerFactory} implementation that will
     actually create a new Instance.

     @since 0.8.5 */
  public static Logger getLogger(String name, LoggerFactory factory) {
    return LogManager.getLogger(name, factory);
  }
  
  /**
   * Log a message object with the {@link Level#TRACE TRACE} level.
   *
   * @param message the message object to log.
   * @see #debug(Object) for an explanation of the logic applied.
   * @since 1.2.12
   */
  public void trace(Object message) {
    if (repository.isDisabled(Level.TRACE_INT)) {
      return;
    }

    if (Level.TRACE.isGreaterOrEqual(this.getEffectiveLevel())) {
      forcedLog(FQCN, Level.TRACE, message, null);
    }
  }

  /**
   * Log a message object with the <code>TRACE</code> level including the
   * stack trace of the {@link Throwable}<code>t</code> passed as parameter.
   *
   * <p>
   * See {@link #debug(Object)} form for more detailed information.
   * </p>
   *
   * @param message the message object to log.
   * @param t the exception to log, including its stack trace.
   * @since 1.2.12
   */
  public void trace(Object message, Throwable t) {
    if (repository.isDisabled(Level.TRACE_INT)) {
      return;
    }

    if (Level.TRACE.isGreaterOrEqual(this.getEffectiveLevel())) {
      forcedLog(FQCN, Level.TRACE, message, t);
    }
  }

  /**
   * Log a message with the <code>TRACE</code> level with message formatting
   * done according to the value of <code>messagePattern</code> and 
   * <code>arg</code> parameters.
   * <p>
   * This form avoids superflous parameter construction. Whenever possible,
   * you should use this form instead of constructing the message parameter 
   * using string concatenation.
   * 
   * @param messagePattern The message pattern which will be parsed and formatted
   * @param arg The argument to replace the formatting element, i,e, 
   * the '{}' pair within <code>messagePattern</code>.
   * @since 1.3
   */
  public void trace(Object messagePattern, Object arg) {
    if (repository.isDisabled(Level.TRACE_INT)) {
      return;
    }

    if (Level.TRACE.isGreaterOrEqual(this.getEffectiveLevel())) {
      if (messagePattern instanceof String){
        String msgStr = (String) messagePattern;
        msgStr = MessageFormatter.format(msgStr, arg);
        forcedLog(FQCN, Level.TRACE, msgStr, null);
      } else {
        // To be failsafe, we handle the case where 'messagePattern' is not
        // a String. Unless the user makes a mistake, this should never happen.
        forcedLog(FQCN, Level.TRACE, messagePattern, null);
      }
    }
  }
  /**
   * Log a message with the <code>TRACE</code> level with message formatting
   * done according to the messagePattern and the arguments arg1 and arg2.
   * <p>
   * This form avoids superflous parameter construction. Whenever possible,
   * you should use this form instead of constructing the message parameter 
   * using string concatenation.
   * 
   * @param messagePattern The message pattern which will be parsed and formatted
   * @param arg1 The first argument to replace the first formatting element
   * @param arg2 The second argument to replace the second formatting element
   * @since 1.3
   */
  public void trace(String messagePattern, Object arg1, Object arg2) {
    if (repository.isDisabled(Level.TRACE_INT)) {
      return;
    }
    if (Level.TRACE.isGreaterOrEqual(this.getEffectiveLevel())) {
      messagePattern = MessageFormatter.format(messagePattern, arg1, arg2);
      forcedLog(FQCN, Level.TRACE, messagePattern, null);
    }
  }
  
  /**
   * Log a message with the <code>FATAL</code> level with message formatting
   * done according to the messagePattern and the arguments arg1 and arg2.
   * <p>
   * This form avoids superflous parameter construction. Whenever possible,
   * you should use this form instead of constructing the message parameter 
   * using string concatenation.
   *
   * @param messagePattern The message pattern which will be parsed and formatted
   * @param arg1 The first argument to replace the first formatting element
   * @param arg2 The second argument to replace the second formatting element
   * @since 1.3
   */
  public void fatal(String messagePattern, Object arg1, Object arg2) {
    if (repository.isDisabled(Level.FATAL_INT)) {
      return;
    }
    if (Level.FATAL.isGreaterOrEqual(this.getEffectiveLevel())) {
      messagePattern = MessageFormatter.format(messagePattern, arg1, arg2);
      forcedLog(FQCN, Level.FATAL, messagePattern, null);
    }
  }
  
}

// End of class: Logger.java
