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

package org.apache.log4j.watchdog;

import org.apache.log4j.plugins.Plugin;

/**
  Defines the required interface for all Watchdog objects.

  <p>A watchdog is an entity that monitors a source of configuration data.
  If the source indicates that the configuration data has been changed, then
  the new configuration data is read and the log4j environment is
  reconfigured using the new data.

  <p>Examples of watchdogs are FileWatchdog and SocketWatchdog.  FileWatchdog
  monitors a configuration file for updates, using the updated file to
  reconfigure log4j.  SocketWatchdog monitors a socket port, using the data
  stream from the socket to reconfigure log4j.
  
  <p>Watchdogs are implemented as instances of the Plugin interface and can
  be started and stopped like any other Plugin object.
  
  <p>Watchdogs are not specific to any Configurator class.  Any Configurator
  can be used with any Watchdog.  When reconfiguring, the Watchdog should
  create a new instance of the defined Configurator class and call the
  appropriate Configurator method to reconfigure the log4j environment.

  @author Mark Womack <mwomack@apache.org>
  @since 1.3
*/
public interface Watchdog extends Plugin {

  /**
   * Sets the Configurator class used for reconfiguration.
   *
   * @param configuratorClassName Fully qualified class name for
   *   Configurator class.
   */
  public void setConfigurator(String configuratorClassName);
  
  /**
   * Returns the configurator class used for reconfiguration.
   *
   * @return Fully qualified class name for Configurator class.
   */
  public String getConfigurator();

  /**
   * Called to reconfigure the log4j environment when the monitored data
   * source has been updated.
   *
   * @return True if reconfiguration was without errors
   */
  public boolean reconfigure();
  
}
