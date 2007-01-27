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

import org.apache.log4j.spi.DefaultRepositorySelector;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.RepositorySelector;

import junit.framework.TestCase;


/**
 * Tests for {@link LogManager}.
 *
 * @author Curt Arnold
 */
public class LogManagerTest extends TestCase {

  private static final Object sharedGuard = new Object();

  private Hierarchy h = new Hierarchy();
  
  /**
   * Create new instance of LogManagerTest.
   * @param testName test name
   */
  public LogManagerTest(final String testName) {
    super(testName);
  }

  /**
   *  Check value of DEFAULT_CONFIGURATION_FILE.
   *  @deprecated since constant is deprecated
   */
  public void testDefaultConfigurationFile() {
     assertEquals("log4j.properties", LogManager.DEFAULT_CONFIGURATION_FILE);
  }

  /**
   *  Check value of DEFAULT_XML_CONFIGURATION_FILE.
   *  @deprecated since constant is deprecated
   */
  public void testDefaultXmlConfigurationFile() {
     assertEquals("log4j.xml", LogManager.DEFAULT_XML_CONFIGURATION_FILE);
  }
  
  /**
   *  Check value of DEFAULT_CONFIGURATION_KEY.
   *  @deprecated since constant is deprecated
   */
  public void testDefaultConfigurationKey() {
     assertEquals("log4j.configuration", LogManager.DEFAULT_CONFIGURATION_KEY);
  }
  
  /**
   *  Check value of CONFIGURATOR_CLASS_KEY.
   *  @deprecated since constant is deprecated
   */
  public void testConfiguratorClassKey() {
     assertEquals("log4j.configuratorClass", LogManager.CONFIGURATOR_CLASS_KEY);
  }
  
  /**
   *  Check value of DEFAULT_INIT_OVERRIDE_KEY.
   *  @deprecated since constant is deprecated
   */
  public void testDefaultInitOverrideKey() {
     assertEquals("log4j.defaultInitOverride", LogManager.DEFAULT_INIT_OVERRIDE_KEY);
  }
  
  public void testValidSelector() {
    RepositorySelector selector = new DefaultRepositorySelector(h);
    LogManager.setRepositorySelector(selector, sharedGuard);
    Logger log = Logger.getLogger("TestValidSelector");
    log.info("Logger obtained");
    
    try {
      LogManager.setRepositorySelector(selector, "joe");
      fail("cannot cheat guard");
    } catch (IllegalArgumentException e) {}
  }

  public void testInvalidSelector() {
    try {
      RepositorySelector selector = new RepositorySelector() {

        public LoggerRepository getLoggerRepository() {
          return null;
        }
        
      };
      LogManager.setRepositorySelector(selector, sharedGuard);
      Logger.getLogger("TestInvalidSelector");      
      fail("Invalid repository selector should have generated IllegalArgumentException");
    } catch (IllegalArgumentException iae) {
    }
  }

}
