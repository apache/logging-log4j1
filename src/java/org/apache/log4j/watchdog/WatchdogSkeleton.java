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

import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.spi.Configurator;
import org.apache.log4j.plugins.Plugin;
import org.apache.log4j.plugins.PluginSkeleton;
import org.apache.log4j.helpers.OptionConverter;

import java.io.InputStream;
import java.net.URL;

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
  public abstract void reconfigure();
  
  /**
   * Helper method to get an instance of the configurator class.
   *
   * @return An instance of the Configurator class to use
   * for reconfiguration.
   */
  protected Configurator getConfiguratorInstance() {
    // create an instance of the configurator class
    Configurator configurator = null;
    
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
   */
  protected void reconfigureByURL(URL srcURL) {
    if (this.getLogger().isDebugEnabled()) {
      this.getLogger().debug("watchdog \"{}\" reconfiguring from url: {}",
        this.getName(), srcURL);
    }
    
    // create an instance of the configurator class
    Configurator configurator = getConfiguratorInstance();
    
    // if able to create configurator, then reconfigure using input stream
    if (configurator != null) {
      configurator.doConfigure(srcURL, this.getLoggerRepository());
    }
	  else {
	    getLogger().error(
        "watchdog \"{}\" could not create configurator, ignoring new configuration settings",
        this.getName());
	  }
  }
  
  /**
   * Helper method to reconfigure using an InputStream.
   * The input parameter, configurationStream, should be a stream
   * of configuration data in a format expected by the configurator.
   *
   * @param srcStream The input stream that contains the data to be used
   *   reconfiguration.
   */
   protected void reconfigureByInputStream(InputStream srcStream) {
    if (this.getLogger().isDebugEnabled()) {
      this.getLogger().debug("watchdog \"{}\" reconfiguring from InputStream");
    }
    
    // create an instance of the configurator class
    Configurator configurator = getConfiguratorInstance();
    
    // if able to create configurator, then reconfigure using input stream
    if (configurator != null) {
      try {
        configurator.doConfigure(srcStream, this.getLoggerRepository());
	    } catch (Exception e) {
	  	  getLogger().error(
        "watchdog " + this.getName() + " error working with configurator," +
        " ignoring new configuration settings", e);
	    }
	  }
	  else {
	  	  getLogger().error(
          "watchdog \"{}\" could not create configurator, ignoring new configuration settings",
          this.getName());
	  }
  }  
}
