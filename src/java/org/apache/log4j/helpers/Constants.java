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

package org.apache.log4j.helpers;


/**
 * Constants used by netwrok-based appenders and others.
 */
public interface Constants {
  static final String APPLICATION_KEY = "application";
  static final String HOSTNAME_KEY = "hostname";
  static final String LOG4J_ID_KEY = "log4jid";
  public static final String TIMESTAMP_RULE_FORMAT = "yyyy/MM/dd HH:mm:ss";

  /*
   * The default property file name for automatic configuration.
   */
  static final String DEFAULT_CONFIGURATION_FILE = "log4j.properties";
  /*
   * The default XML configuration file name for automatic configuration.
   */
  static final String DEFAULT_XML_CONFIGURATION_FILE = "log4j.xml";
  static final String DEFAULT_CONFIGURATION_KEY = "log4j.configuration";
  static final String CONFIGURATOR_CLASS_KEY = "log4j.configuratorClass";
  
  static String JNDI_CONTEXT_NAME = "java:comp/env/log4j/context-name";
}
