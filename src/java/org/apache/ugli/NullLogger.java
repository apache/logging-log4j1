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
package org.apache.ugli;


/**
 * A no operation (NOP) implementation of {@link Logger}.
 * 
 * @author Ceki G&uuml;lc&uuml;
 */
public class NullLogger implements Logger {

  /**
   * The unique instance of NullLogger.
   */
  public final static NullLogger NULL_LOGGER = new NullLogger();
  
  /**
   * There is no point in people creating multiple instances of NullLogger. 
   * Hence, the private access modifier. 
   */
  private NullLogger() {
  }
  
  /* Always returns false.
   * 
   * @see org.apache.ugli.Logger#isDebugEnabled()
   */
  public boolean isDebugEnabled() {
    return false;
  }

  /* A NOP implementation.
   * @see org.apache.ugli.Logger#debug(java.lang.Object)
   */
  public void debug(Object msg) {
    // NOP
  }

  /* A NOP implementation.
   * @see org.apache.ugli.Logger#debug(java.lang.Object, java.lang.Object)
   */
  public void debug(Object parameterizedMsg, Object param1) {
    // NOP
  }

  /* A NOP implementation.
   * @see org.apache.ugli.Logger#debug(java.lang.Object, java.lang.Object, java.lang.Object)
   */
  public void debug(Object parameterizedMsg, Object param1, Object param2) {
    // NOP
  }

  /* A NOP implementation.
   * @see org.apache.ugli.Logger#debug(java.lang.Object, java.lang.Throwable)
   */
  public void debug(Object msg, Throwable t) {
    // NOP
  }

  /* Always returns false.
   * @see org.apache.ugli.Logger#isInfoEnabled()
   */
  public boolean isInfoEnabled() {
    // NOP
    return false;
  }

  /* A NOP implementation.
   * @see org.apache.ugli.Logger#info(java.lang.Object)
   */
  public void info(Object msg) {
    // NOP
  }

  /* A NOP implementation.
   * @see org.apache.ugli.Logger#info(java.lang.Object, java.lang.Object)
   */
  public void info(Object parameterizedMsg, Object param1) {
    // NOP
  }

  /* A NOP implementation.
   * @see org.apache.ugli.Logger#info(java.lang.Object, java.lang.Object, java.lang.Object)
   */
  public void info(Object parameterizedMsg, Object param1, Object param2) {
    // NOP
  }

  /* A NOP implementation.
   * @see org.apache.ugli.Logger#info(java.lang.Object, java.lang.Throwable)
   */
  public void info(Object msg, Throwable t) {
    // NOP
  }

  /* Always returns false.
   * @see org.apache.ugli.Logger#isWarnEnabled()
   */
  public boolean isWarnEnabled() {
    return false;
  }

  /* A NOP implementation.
   * @see org.apache.ugli.Logger#warn(java.lang.Object)
   */
  public void warn(Object msg) {
    // NOP
  }

  /* A NOP implementation.
   * @see org.apache.ugli.Logger#warn(java.lang.Object, java.lang.Object)
   */
  public void warn(Object parameterizedMsg, Object param1) {
    // NOP
  }

  /* A NOP implementation.
   * @see org.apache.ugli.Logger#warn(java.lang.Object, java.lang.Object, java.lang.Object)
   */
  public void warn(Object parameterizedMsg, Object param1, Object param2) {
    // NOP
  }

  /* A NOP implementation.
   * @see org.apache.ugli.Logger#warn(java.lang.Object, java.lang.Throwable)
   */
  public void warn(Object msg, Throwable t) {
    // NOP
  }

  /* Always returns false.
   * @see org.apache.ugli.Logger#isErrorEnabled()
   */
  public boolean isErrorEnabled() {
    return false;
  }

  /* A NOP implementation.
   * @see org.apache.ugli.Logger#error(java.lang.Object)
   */
  public void error(Object msg) {
    // NOP
  }

  /* A NOP implementation.
   * @see org.apache.ugli.Logger#error(java.lang.Object, java.lang.Object)
   */
  public void error(Object parameterizedMsg, Object param1) {
    // NOP
  }

  /* A NOP implementation.
   * @see org.apache.ugli.Logger#error(java.lang.Object, java.lang.Object, java.lang.Object)
   */
  public void error(Object parameterizedMsg, Object param1, Object param2) {
    // NOP
  }

  /* A NOP implementation.
   * @see org.apache.ugli.Logger#error(java.lang.Object, java.lang.Throwable)
   */
  public void error(Object msg, Throwable t) {
    // NOP
  }

}
