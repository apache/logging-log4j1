/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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


/**
 * A proxy for org.slf4j.ULogger.  In slf4j implementing builds, this
 *     interface will extend org.slf4j.ULogger and add no additional methods.
 *
 * @author Ceki G&uuml;lc&uuml;
 * @author Curt Arnold
 */
 public interface ULogger {


  /**
   * Is the logger instance enabled for the DEBUG level?
   * @return true if debug is enabled.
   */
  boolean isDebugEnabled();

  /**
   * Log a message object with the DEBUG level.
   * @param msg - the message object to be logged
   */
  void debug(Object msg);


  /**
   * Log a parameterized message object at the DEBUG level.
   *
   * <p>This form is useful in avoiding the superflous object creation
   * problem when invoking this method while it is disabled.
   * </p>
   * @param parameterizedMsg - the parameterized message object
   * @param param1 - the parameter
   */
  void debug(Object parameterizedMsg, Object param1);

  /**
   * Log a parameterized message object at the DEBUG level.
   *
   * <p>This form is useful in avoiding the superflous object creation
   * problem when invoking this method while it is disabled.
   * </p>
   * @param parameterizedMsg - the parameterized message object
   * @param param1 - the first parameter
   * @param param2 - the second parameter
   */
  void debug(String parameterizedMsg, Object param1, Object param2);
    /**
     * Log a message object with the <code>DEBUG</code> level including the
     * stack trace of the {@link Throwable}<code>t</code> passed as parameter.
     *
     *
     * @param msg the message object to log.
     * @param t the exception to log, including its stack trace.
     */
  void debug(Object msg, Throwable t);


    /**
     * Is the logger instance enabled for the INFO level?
     * @return true if debug is enabled.
     */
  boolean isInfoEnabled();
    /**
     * Log a message object with the INFO level.
     * @param msg - the message object to be logged
     */
  void info(Object msg);
    /**
     * Log a parameterized message object at the INFO level.
     *
     * <p>This form is useful in avoiding the superflous object creation
     * problem when invoking this method while it is disabled.
     * </p>
     * @param parameterizedMsg - the parameterized message object
     * @param param1 - the parameter
     */
  void info(Object parameterizedMsg, Object param1);
    /**
     * Log a parameterized message object at the INFO level.
     *
     * <p>This form is useful in avoiding the superflous object creation
     * problem when invoking this method while it is disabled.
     * </p>
     * @param parameterizedMsg - the parameterized message object
     * @param param1 - the first parameter
     * @param param2 - the second parameter
     */
  void info(String parameterizedMsg, Object param1, Object param2);
    /**
     * Log a message object with the <code>INFO</code> level including the
     * stack trace of the {@link Throwable}<code>t</code> passed as parameter.
     *
     *
     * @param msg the message object to log.
     * @param t the exception to log, including its stack trace.
     */
  void info(Object msg, Throwable t);


    /**
     * Is the logger instance enabled for the WARN level?
     * @return true if debug is enabled.
     */
  boolean isWarnEnabled();
    /**
     * Log a message object with the WARN level.
     * @param msg - the message object to be logged
     */
  void warn(Object msg);
    /**
     * Log a parameterized message object at the WARN level.
     *
     * <p>This form is useful in avoiding the superflous object creation
     * problem when invoking this method while it is disabled.
     * </p>
     * @param parameterizedMsg - the parameterized message object
     * @param param1 - the parameter
     */
  void warn(Object parameterizedMsg, Object param1);
    /**
     * Log a parameterized message object at the WARN level.
     *
     * <p>This form is useful in avoiding the superflous object creation
     * problem when invoking this method while it is disabled.
     * </p>
     * @param parameterizedMsg - the parameterized message object
     * @param param1 - the first parameter
     * @param param2 - the second parameter
     */
  void warn(String parameterizedMsg, Object param1, Object param2);
    /**
     * Log a message object with the <code>WARN</code> level including the
     * stack trace of the {@link Throwable}<code>t</code> passed as parameter.
     *
     *
     * @param msg the message object to log.
     * @param t the exception to log, including its stack trace.
     */
  void warn(Object msg, Throwable t);


    /**
     * Is the logger instance enabled for the ERROR level?
     * @return true if debug is enabled.
     */
  boolean isErrorEnabled();
    /**
     * Log a message object with the ERROR level.
     * @param msg - the message object to be logged
     */
  void error(Object msg);
    /**
     * Log a parameterized message object at the ERROR level.
     *
     * <p>This form is useful in avoiding the superflous object creation
     * problem when invoking this method while it is disabled.
     * </p>
     * @param parameterizedMsg - the parameterized message object
     * @param param1 - the parameter
     */
  void error(Object parameterizedMsg, Object param1);
    /**
     * Log a parameterized message object at the ERROR level.
     *
     * <p>This form is useful in avoiding the superflous object creation
     * problem when invoking this method while it is disabled.
     * </p>
     * @param parameterizedMsg - the parameterized message object
     * @param param1 - the first parameter
     * @param param2 - the second parameter
     */
  void error(String parameterizedMsg, Object param1, Object param2);

    /**
     * Log a message object with the <code>ERROR</code> level including the
     * stack trace of the {@link Throwable}<code>t</code> passed as parameter.
     *
     *
     * @param msg the message object to log.
     * @param t the exception to log, including its stack trace.
     */
    void error(Object msg, Throwable t);

}
