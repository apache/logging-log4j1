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

package org.slf4j;
import org.apache.log4j.Logger;



/**
 *   Concrete implementation of SLF4J LoggerFactory contract
 *   that returns log4j Loggers.
 * 
 *    @author Curt Arnold
 */
public class LoggerFactory {
  private LoggerFactory() {
  }

  
  static public ULogger getLogger(final String name) {
    return Logger.getLogger(name);
  }
  
  static public ULogger getLogger(final String domainName, final String subDomainName) {
    return Logger.getLogger(domainName);
  }
  
  static public ULogger getLogger(final Class clazz) {
    return Logger.getLogger(clazz.getName());
  }
  static public ULogger getLogger(final Class clazz, final String subDomainName) {
    return Logger.getLogger(clazz.getName());
  }
  
}
