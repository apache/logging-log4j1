/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */
package org.apache.log4j.plugins;

import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.OptionHandler;


/**
  Defines the required interface for all Plugin objects.

  <p>A plugin implements some specific functionality to extend
  the log4j framework.  Each plugin is associated with a specific
  LoggerRepository, which it then uses/acts upon.  The functionality
  of the plugin is up to the developer.

  <p>Examples of plugins are Receiver and Watchdog. Receiver plugins
  allow for remote logging events to be received and processed by
  a repository as if the event was sent locally. Watchdog plugins
  allow for a repository to be reconfigured when some "watched"
  configuration data changes.

  @author Mark Womack
  @author Nicko Cadell
  @since 1.3
*/
public interface Plugin extends OptionHandler {
  /**
    Gets the name of the plugin.

    @return String the name of the plugin. */
  public String getName();

  /**
    Sets the name of the plugin.

    @param name the name of the plugin. */
  public void setName(String name);

  /**
    Gets the logger repository for this plugin.

    @return LoggerRepository the logger repository this plugin is
      attached to. */
  public LoggerRepository getLoggerRepository();

  /**
    Sets the logger repository used by this plugin. This
    repository will be used by the plugin functionality.

    @param repository the logger repository to attach this plugin to. */
  public void setLoggerRepository(LoggerRepository repository);

  /**
    True if the plugin is active and running.

    @return boolean true if the plugin is currently active. */
  public boolean isActive();

  /**
    Call when the plugin should be stopped. */
  public void shutdown();
}
