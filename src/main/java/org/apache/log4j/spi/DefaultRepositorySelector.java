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

package org.apache.log4j.spi;

/**
 * Trivial implementation of RepositorySelectorEx which takes
 * a fixed repository.
 */
public class DefaultRepositorySelector implements RepositorySelectorEx {
  
  private LoggerRepository defaultRepository;

  /**
   * Constructs a new instance.
   * @param repository cannot be null
   */
  public DefaultRepositorySelector(final LoggerRepository repository) {
    if (repository == null)
      throw new NullPointerException();
    this.defaultRepository = repository;
  }

  public LoggerRepository getLoggerRepository() {
    return defaultRepository;
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
