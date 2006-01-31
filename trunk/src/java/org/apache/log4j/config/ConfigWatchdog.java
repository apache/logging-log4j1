/*
 * Copyright 1999-2006 The Apache Software Foundation.
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

package org.apache.log4j.config;

import org.apache.log4j.spi.LoggerRepository;

/**
  Defines the basic interface that all ConfigWatchdogs must support.
  
  <p>ConfigWatchdogs "watch" a log4j configuration source, and when
  new, changed configuration data is available, the watchdog will
  initiate a reconfiguration of the log4j settings using the new
  configuration data.
    
  <p>All watchdogs can have a name and configurator class.
  The configurator class will be used when reconfiguring using the 
  new data.  All watchdogs can be started and stopped.
  
  <p>Several different ConfigWatchdog classes are available in the
  org.apache.log4j.config package, such as FileWatchdog (watches a
  configuration file), HttpWatchdog (watches a configuration file 
  at a url location), and SocketWatchdog (watches a socket for 
  incoming configuration data).
  
  <p>If these are not sufficient, developers are encouraged
  to implement specific versions of ConfigWatchdogs for their 
  particular needs. This can be done by subclassing the WatchdogBase
  or URLWatchdogBase abstract classes, or to write their own class
  that implements the ConfigWatchdog interface.  Please see the
  above mentioned classes for more information.  

  @author Mark Womack
  
  @since 1.3
*/
public interface ConfigWatchdog {
  
  /**
    Returns true if this watchdog is currently running. */
  public boolean isRunning();
  
  /**
     Set the name of this watchdog. The name is used by other
     components to identify this watchdog. */
  public void setName(String name);

  /**
     Get the name of this watchdog. The name uniquely identifies 
     the watchdog.  */
  public String getName();

  /**
    Sets the configurator class name used for reconfiguration. */
  public void setConfiguratorClassName(String className);
  
  /**
    Gets the configurator class name used for reconfiguration. */
  public String getConfiguratorClassName();

  /**
    Sets the configurator class used for reconfiguration. */
  public void setConfiguratorClass(Class clazz);
  
  /**
    Gets the configurator class used for reconfiguration. */
  public Class getConfiguratorClass();

  /**
    Set the logger repository this watchdog will reconfigure
    when new configuration data is detected.  If not set,
    should default to the return value of 
    LogManager.getLoggerRepository(). */
  public void setLoggerRepository(LoggerRepository repository);
  
  /**
    Get the logger repository this watchdog will reconfigure
    when new configuration data is detected.  Default is the
    return value of LogManager.getLoggerRepository(). */
  public LoggerRepository getLoggerRepository();
  
  /**
    Starts this watchdog watching. After calling this method the
    watchdog will be active. */
  public void startWatching();
  
  /**
    Stops this watchdog. After calling this method the
    watchdog will become inactive, but it is not guaranteed
    to be immediately inactive. If threads are involved in the 
    implementation, it may take time for them to be interupted and
    exited. */
  public void stopWatching();
}
