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


import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.config.ConfiguratorBase;
import org.apache.log4j.spi.Configurator;
import org.apache.log4j.spi.ConfiguratorEx;
import org.apache.log4j.plugins.PluginSkeleton;
import org.apache.log4j.helpers.OptionConverter;

import java.net.URL;
import java.io.InputStream;
import java.util.List;

/**
  Implements the required interface for Watchdog objects.

  <p>This class provides an implementation of the Watchdog interface that
  forms the base class used for specific Watchdog classes implemented in the
  log4j framework.  Using this class for all Watchdog implementations is not
  required.  Developers may choose to implement their own version of the
  Watchdog interface (which extends the base Plugin interface) if they so 
  choose.

  <p>This implementation provides two helper methods, reconfigureByURL and
  reconfigureByInputStream.  Either of these methods can be called from the
  implemented reconfigure method, as required by the specific type of data
  source being monitored.  The reconfigureByURL method is meant for sources
  that can be described by a URL (ie file, url).  The rconfigureByInputStream
  method is meant for sources where onl a stream of data is available
  (ie socket).

  <p>Subclasses of this implementation are required to implement their own
  version of the abstract reconfigure method.

  @author Mark Womack <mwomack@apache.org>
  @since 1.3
*/
public abstract class WatchdogSkeleton
extends PluginSkeleton implements Watchdog {

  protected String configuratorClassName;

  /**
   * Sets the Configurator class used for reconfiguration.
   *
   * @param configuratorClassName Fully qualified class name for Configurator
   * class.
   */
   public void setConfigurator(String configuratorClassName) {
     this.configuratorClassName = configuratorClassName;
   }

  /**
   * Returns the configurator class used for reconfiguration.
   *
   * @return Fully qualified class name for Configurator class.
   */
   public String getConfigurator() {
     return configuratorClassName;
   }

  /**
   * Called to reconfigure the log4j environment when the monitored data
   * source has been updated.  Must be implemented by subclasses.
   */
  public abstract boolean reconfigure();

  /**
   * Helper method to get an instance of the configurator class.
   *
   * @return An instance of the Configurator class to use
   * for reconfiguration.
   */
  protected Configurator getConfiguratorInstance() {
    // create an instance of the configurator class
    Configurator configurator;

    // if we were configured with a configurator class name, use it
    if (configuratorClassName != null) {
      configurator = (Configurator) OptionConverter.instantiateByClassName(
        configuratorClassName, Configurator.class, null);
    }
    // otherwise, default to PropertyConfigurator
    else {
      configurator = new PropertyConfigurator();
    }

    return configurator;
  }

  /**
   * Helper method to reconfigure using a URL.
   * The input parameter, configurationURL, should be a URL pointing to
   * the configuration data in a format expected by the configurator.
   *
   * @param srcURL The url that contains the data to be used for
   *   reconfiguration.
   * @return True if the reconfiguration was without error
   */
  protected boolean reconfigureByURL(URL srcURL) {
    if (this.getLogger().isDebugEnabled()) {
      this.getLogger().debug("watchdog \"{}\" reconfiguring from url: {}",
        this.getName(), srcURL);
    }

    // create an instance of the configurator class
    Configurator configurator = getConfiguratorInstance();
    if (configurator == null) {
      getLogger().error(
          "watchdog \"{}\" could not create configurator, ignoring new configuration settings",
          this.getName());
      return false;
    }

    configurator.doConfigure(srcURL, this.getLoggerRepository());
    return configure(configurator);
  }

  /**
   * Helper method to reconfigure using an InputStream.
   *
   * @param stream The stream of data to be used for reconfiguration.
   * @return True if the reconfiguration was without error
   */
  protected boolean reconfigureByStream(InputStream stream) {
    if (this.getLogger().isDebugEnabled()) {
      this.getLogger().debug("watchdog \"{}\" reconfiguring by stream",
        this.getName());
    }

    // create an instance of the configurator class
    Configurator configurator = getConfiguratorInstance();

    if (configurator instanceof ConfiguratorEx) {
      ConfiguratorEx configuratorEx = (ConfiguratorEx)configurator;
      configuratorEx.doConfigure(stream, this.getLoggerRepository());
      return configure(configurator);
    } else {
      getLogger().error(
        "watchdog \"{}\" could not create configurator, configurator class is not of type ConfiguratorEx",
        this.getName());
      return false;
    }

  }
  
  private boolean configure(Configurator configurator) {
    if (configurator instanceof ConfiguratorBase) {
      ConfiguratorBase baseConfigurator = (ConfiguratorBase)configurator;
      List errorList = baseConfigurator.getErrorList();
      if (errorList.size() != 0) {
        getLogger().error("errors reported during reconfiguration: ");
        for (int x = 0; x < errorList.size(); x++) {
          getLogger().debug("error " + x + ": " + errorList.get(x));
        }
        return false;
      }
    }
    return true;
  }
}
