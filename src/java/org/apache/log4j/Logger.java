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


/**
  This is the central class in the log4j package. Most logging
  operations, except configuration, are done through this class.

  @since log4j 1.2

  @author Ceki G&uuml;lc&uuml; 
*/
public class Logger extends Category {

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
}

// End of class: Logger.java
