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
 * 
 * The NullLoggerRepository returns instances of {@link NullLogger}.
 * @author Ceki G&uuml;c&uuml;
 */
public class NullLoggerRepository implements LoggerRepository {

  /* Always returns a {@link NullLogger}.
   * @see org.apache.ugli.LoggerRepository#getLogger(java.lang.String)
   */
  public Logger getLogger(String name) {
    return NullLogger.NULL_LOGGER;
  }

  /* Always returns a {@link NullLogger}.
   * @see org.apache.ugli.LoggerRepository#getLogger(java.lang.String, java.lang.String)
   */
  public Logger getLogger(String domainName, String subDomainName) {
    return NullLogger.NULL_LOGGER;
  }

  /* Always returns a {@link NullLogger}.
   * @see org.apache.ugli.LoggerRepository#getLogger(java.lang.Class)
   */
  public Logger getLogger(Class clazz) {
    return NullLogger.NULL_LOGGER;
  }

  /* Always returns a {@link NullLogger}.
   * @see org.apache.ugli.LoggerRepository#getLogger(java.lang.Class, java.lang.String)
   */
  public Logger getLogger(Class clazz, String subDomainName) {
    return NullLogger.NULL_LOGGER;
  }

  /* Always returns a {@link NullLogger}.
   * @see org.apache.ugli.LoggerRepository#close()
   */
  public void close() {
    // NOP
  }

}
