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

package org.slf4j.impl;

import org.apache.log4j.LogManager;
import org.slf4j.LoggerFactoryAdapter;
import org.slf4j.ULogger;

/**
 * This factory adapter relies on log4's {@link LogManager} to do its job.
 * 
 * @author Ceki G&uuml;lc&uuml;
 *
 */
public class Log4jLoggerFA implements LoggerFactoryAdapter {

  public Log4jLoggerFA() {
  }

  /**
   * Get a {@link ULogger} by name, by delegating all work to log4's 
   * {@link LogManager}.
   * 
   * <p>The implementation is trivial because log4j 
   * {@link org.apache.log4j.Logger loggers} directly implement the 
   * {@link ULogger} interface.
   */
  public ULogger getLogger(String name) {
    return LogManager.getLogger(name);  
  }

  /**
   * Get a {@link ULogger} by domain and subdomain name, by delegating all work 
   * to log4's {@link LogManager}.
   * 
   * <p>The implementation is trivial because log4j 
   * {@link org.apache.log4j.Logger loggers} directly implement the 
   * {@link ULogger} interface.
   */
  public ULogger getLogger(String domainName, String subDomainName) {
    return LogManager.getLogger(domainName);  
  }
}

