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

package org.apache.log4j.spi;

import org.apache.log4j.helpers.Constants;

public class DefaultRepositorySelector implements RepositorySelector {
  LoggerRepository defaultRepository;

  public DefaultRepositorySelector(final LoggerRepository repository) {
    this.defaultRepository = repository;
  }

  public LoggerRepository  getLoggerRepository() {
    return defaultRepository;
  }
  
  public LoggerRepository getLoggerRepository(final String name) {
    if(Constants.DEFAULT_REPOSITORY_NAME.equals(name)) {
      return defaultRepository;
    } else {
      return null;
    }
  }
  
  /**
   * Does nothing, always returns null.
   * 
   * @return Always null
   */
  public LoggerRepository detachRepository(final String name) {
    // do nothing, as the default repository cannot be removed
    return null;
  }
}
