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

  @author Ceki G&uuml;lc&uuml; */
public class Logger extends Category {
  protected Logger(String name) {
    super(name);
  }

  /**
    Log a message object with the {@link Level#FINE FINE} level which
    is just an alias for the {@link Level#DEBUG DEBUG} level.

    <p>This method first checks if this category is <code>DEBUG</code>
    enabled by comparing the level of this category with the {@link
    Level#DEBUG DEBUG} level. If this category is
    <code>DEBUG</code> enabled, then it converts the message object
    (passed as parameter) to a string by invoking the appropriate
    {@link org.apache.log4j.or.ObjectRenderer}. It then proceeds to call all the
    registered appenders in this category and also higher in the
    hierarchy depending on the value of the additivity flag.

    <p><b>WARNING</b> Note that passing a {@link Throwable} to this
    method will print the name of the <code>Throwable</code> but no
    stack trace. To print a stack trace use the {@link #debug(Object,
    Throwable)} form instead.

    @param message the message object to log. */

  //public
  //void fine(Object message) {
  //  if(repository.isDisabled(Level.DEBUG_INT))
  //    return;
  //  if(Level.DEBUG.isGreaterOrEqual(this.getChainedLevel())) {
  //    forcedLog(FQCN, Level.DEBUG, message, null);
  //  }
  //}

  /**
   Log a message object with the <code>FINE</code> level including
   the stack trace of the {@link Throwable} <code>t</code> passed as
   parameter.

   <p>See {@link #fine(Object)} form for more detailed information.

   @param message the message object to log.
   @param t the exception to log, including its stack trace.  */

  //public
  //void fine(Object message, Throwable t) {
  //  if(repository.isDisabled(Level.DEBUG_INT))
  //    return;
  //  if(Level.DEBUG.isGreaterOrEqual(this.getChainedLevel()))
  //    forcedLog(FQCN, Level.FINE, message, t);
  //}

  /**
     Retrieve a logger by name.
  */
  public static Logger getLogger(String name) {
    return LogManager.getLogger(name);
  }

  /**
     Same as calling <code>getLogger(clazz.getName())</code>.
   */
  public static Logger getLogger(Class clazz) {
    return LogManager.getLogger(clazz.getName());
  }

  /**
     Retrieve the root logger.
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
