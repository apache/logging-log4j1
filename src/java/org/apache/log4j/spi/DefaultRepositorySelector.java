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

public class DefaultRepositorySelector implements RepositorySelector {
  LoggerRepository repository;

  public DefaultRepositorySelector() {
  }

  public LoggerRepository getLoggerRepository() {
    return repository;
  }
  
  public void setDefaultRepository(LoggerRepository dr) {
    if(repository == null) {
      repository = dr;
    } else {
       throw new IllegalStateException("default repository already set.");
    }
  }
  
  public void remove(String contextName) {
    // do nothing as the default reposiory cannot be removed
  }
}
