/*
 * Copyright 1999,2005 The Apache Software Foundation.
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
  public boolean isDebugEnabled();
//
  
  /**
   * Log a message object with the DEBUG level. 
   * @param msg - the message object to be logged
   */
  public void debug(Object msg);
  
  
  /**
   * Log a parameterized message object at the DEBUG level. 
   * 
   * <p>This form is useful in avoiding the superflous object creation
   * problem when invoking this method while it is disabled.
   * </p>
   * @param parameterizedMsg - the parameterized message object
   * @param param1 - the parameter 
   */
  public void debug(Object parameterizedMsg, Object param1);
  
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
  public void debug(String parameterizedMsg, Object param1, Object param2);
  public void debug(Object msg, Throwable t);


  public boolean isInfoEnabled();
  public void info(Object msg);
  public void info(Object parameterizedMsg, Object param1);
  public void info(String parameterizedMsg, Object param1, Object param2);
  public void info(Object msg, Throwable t);


  public boolean isWarnEnabled();
  public void warn(Object msg);
  public void warn(Object parameterizedMsg, Object param1);
  public void warn(String parameterizedMsg, Object param1, Object param2);
  public void warn(Object msg, Throwable t);


  public boolean isErrorEnabled();
  public void error(Object msg);
  public void error(Object parameterizedMsg, Object param1);
  public void error(String parameterizedMsg, Object param1, Object param2);
  public void error(Object msg, Throwable t);

}
