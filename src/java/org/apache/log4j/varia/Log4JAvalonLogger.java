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

package org.apache.log4j.varia;

import org.apache.avalon.framework.logger.Logger;

import org.apache.log4j.Level;


/**
 * The default Log4J wrapper class for Avalon Logger.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision$ $Date$
 */
public final class Log4JAvalonLogger implements Logger {

  /**
   * Constant for name of class to use when recording caller
   * of log method. Appending a dot at the end is recommended practice.
   */
  private static final String FQCN = Log4JAvalonLogger.class.getName() + ".";

  //underlying implementation
  private final org.apache.log4j.Logger m_logger;

  /**
   * Create a logger that delegates to specified category.
   *
   * @param logImpl the category to delegate to
   */
  public Log4JAvalonLogger(final org.apache.log4j.Logger logImpl) {
    m_logger = logImpl;
  }

  /**
   * Log a debug message.
   *
   * @param message the message
   */
  public final void debug(final String message) {
    m_logger.log(FQCN, Level.DEBUG, message, null );
  }

  /**
   * Log a debug message.
   *
   * @param message the message
   * @param throwable the throwable
   */
  public final void debug(final String message, final Throwable throwable) {
    m_logger.log( FQCN, Level.DEBUG, message, throwable);
  }

  /**
   * Determine if messages of priority "debug" will be logged.
   *
   * @return true if "debug" messages will be logged
   */
  public final boolean isDebugEnabled() {
    return m_logger.isDebugEnabled();
  }

  /**
   * Log a info message.
   *
   * @param message the message
   */
  public final void info(final String message) {
    m_logger.log( FQCN, Level.INFO, message, null );
  }

  /**
   * Log a info message.
   *
   * @param message the message
   * @param throwable the throwable
   */
  public final void info(final String message, final Throwable throwable) {
    m_logger.log( FQCN, Level.INFO, message, throwable );
  }

  /**
   * Determine if messages of priority "info" will be logged.
   *
   * @return true if "info" messages will be logged
   */
  public final boolean isInfoEnabled() {
    return m_logger.isInfoEnabled();
  }

  /**
   * Log a warn message.
   *
   * @param message the message
   */
  public final void warn(final String message) {
    m_logger.log( FQCN, Level.WARN, message, null );
  }

  /**
   * Log a warn message.
   *
   * @param message the message
   * @param throwable the throwable
   */
  public final void warn(final String message, final Throwable throwable) {
    m_logger.log( FQCN, Level.WARN, message, throwable );
  }

  /**
   * Determine if messages of priority "warn" will be logged.
   *
   * @return true if "warn" messages will be logged
   */
  public final boolean isWarnEnabled() {
    return m_logger.isEnabledFor(Level.WARN);
  }

  /**
   * Log a error message.
   *
   * @param message the message
   */
  public final void error(final String message) {
    m_logger.log( FQCN, Level.ERROR, message, null );
  }

  /**
   * Log a error message.
   *
   * @param message the message
   * @param throwable the throwable
   */
  public final void error(final String message, final Throwable throwable) {
    m_logger.log( FQCN, Level.ERROR, message, throwable );
  }

  /**
   * Determine if messages of priority "error" will be logged.
   *
   * @return true if "error" messages will be logged
   */
  public final boolean isErrorEnabled() {
    return m_logger.isEnabledFor(Level.ERROR);
  }

  /**
   * Log a fatalError message.
   *
   * @param message the message
   */
  public final void fatalError(final String message) {
    m_logger.log( FQCN, Level.FATAL, message, null );
  }

  /**
   * Log a fatalError message.
   *
   * @param message the message
   * @param throwable the throwable
   */
  public final void fatalError(
    final String message, final Throwable throwable) {
    m_logger.log( FQCN, Level.FATAL, message, throwable );
  }

  /**
   * Determine if messages of priority "fatalError" will be logged.
   *
   * @return true if "fatalError" messages will be logged
   */
  public final boolean isFatalErrorEnabled() {
    return m_logger.isEnabledFor(Level.FATAL);
  }

  /**
   * Create a new child logger.
   * The name of the child logger is [current-loggers-name].[passed-in-name]
   * Throws <code>IllegalArgumentException</code> if name has an empty element name
   *
   * @param name the subname of this logger
   * @return the new logger
   */
  public final Logger getChildLogger(final String name) {
    return new Log4JAvalonLogger(
      org.apache.log4j.Logger.getLogger(m_logger.getName() + "." + name));
  }
}
