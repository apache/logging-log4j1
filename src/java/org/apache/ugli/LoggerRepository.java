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
 * A LoggerRepository is an application specific object that returns 
 * {@link Logger} instances.
 * 
 * @author Ceki G&uuml;lc&uuml;
 */
public interface LoggerRepository {

  
  public Logger getLogger(String name);
  public Logger getLogger(String domainName, String subDomainName);
  public Logger getLogger(Class clazz);
  public Logger getLogger(Class clazz, String subDomainName);
  
  
  public void close();
}
