/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */
package org.apache.log4j.plugins;

import org.apache.log4j.spi.LoggerRepository;


/**
  A convienent abstract class for plugin subclasses that implements
  the basic methods of the Plugin interface. Subclasses are required
  to implement the isActive(), activateOptions(), and shutdown()
  methods.

  <p>Developers are not required to subclass PluginSkeleton to
  develop their own plugins (they are only required to implement the
  Plugin interface), but it provides a convienent base class to start
  from.

  Contributors: Nicko Cadell

  @author Mark Womack
  @since 1.3
*/
public abstract class PluginSkeleton implements Plugin {
  /** Name of this plugin. */
  protected String name = "";
  
  /** Repository this plugin is attached to. */
  protected LoggerRepository repository;

  /**
    Gets the name of the plugin. 
    
    @return String the name of the plugin. */
  public String getName() {
    return name;
  }

  /**
    Sets the name of the plugin. 
    
    @param name the name of the plugin to set. */
  public void setName(String name) {
    this.name = name;
  }

  /**
    Gets the logger repository for this plugin. 
    
    @return LoggerRepository the logger repository this plugin will affect. */
  public LoggerRepository getLoggerRepository() {
    return repository;
  }

  /**
    Sets the logger repository used by this plugin. This
    repository will be used by the plugin functionality. 
    
    @param repository the logger repository that this plugin should affect. */
  public void setLoggerRepository(LoggerRepository repository) {
    this.repository = repository;
  }
}
