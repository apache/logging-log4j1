/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j;

import java.util.Hashtable;
import java.util.Enumeration;
import org.apache.log4j.spi.LoggerRepository;

/**
  This is a registry for Plugin instances. It provides methods to 
  start and stop plugin objects individually and to stop all
  plugins for a repository.
  
  @author Mark Womack
  @since 1.3
*/
public class PluginRegistry {
  private static Hashtable repositoryMap = new Hashtable();
  
  /**
    Starts a Plugin with default logger repository. */
  public static Plugin startPlugin(Plugin plugin) {
    return startPlugin(plugin, LogManager.getLoggerRepository());
  }

  /**
    Starts a plugin with a given logger repository. */
  public static Plugin startPlugin(Plugin plugin,
  LoggerRepository repository) {
    
    // make sure the plugin has reference to repository
    plugin.setLoggerRepository(repository);
    
    // put plugin into the repository's reciever map
    synchronized(repositoryMap) {
      // get plugin map for repository
      Hashtable pluginMap = (Hashtable)repositoryMap.get(repository);
      
      // if the plugin map does not exist, create one
      if (pluginMap == null) {
        pluginMap = new Hashtable();
        repositoryMap.put(repository, pluginMap);
      }
      
      // existing plugin exists with the
      String name = plugin.getName();
      if (name == null) {
        name = "";
      }
      Plugin existingPlugin = (Plugin)pluginMap.get(name);
      if (existingPlugin != null) {
        boolean isEqual = existingPlugin.equals(plugin);
        
        // if the plugins are equivalent and the existing one
        // is still active, just return the existing one now
        if (isEqual && existingPlugin.isActive()) {
          return existingPlugin;
        } else {
          existingPlugin.shutdown();
        }
      }
      
      // put the new plugin into the map
      pluginMap.put(name, plugin);
      
      // start the new plugin
      plugin.activateOptions();
      
      return plugin;
    }
  }
  
  /**
    Stops a plugin in the default logger repository. */
  public static Plugin stopPlugin(Plugin plugin) {
    return stopPlugin(plugin.getName(), 
      LogManager.getLoggerRepository());
  }

  /**
    Stops a plugin in the default logger repository. */  
  public static Plugin stopPlugin(String pluginName) {
    return stopPlugin(pluginName,
      LogManager.getLoggerRepository());
  }
  
  /**
    Stops a plugin in the given logger repository. */
  public static Plugin stopPlugin(Plugin plugin, 
  LoggerRepository repository) {
    return stopPlugin(plugin.getName(), repository);
  }
  
  /**
    Stops a plugin in the given logger repository. */
  public static Plugin stopPlugin(String pluginName, 
  LoggerRepository repository) {
    String name = pluginName;
    if (pluginName == null) {
      pluginName = "";
    }
    synchronized(repositoryMap) {
      Hashtable pluginMap = (Hashtable)repositoryMap.get(repository);
      if (pluginMap == null)
        return null;
        
      Plugin plugin = (Plugin)pluginMap.get(pluginName);
      if (plugin == null)
        return null;
      
      // shutdown the plugin
      plugin.shutdown();
      
      // remove it from the plugin map
      pluginMap.remove(plugin);
      
      // if no more plugins, remove the plugin map from
      // repository map
      if (pluginMap.isEmpty())
        repositoryMap.remove(repository);
      
      // return it for future use
      return plugin;
    }
  }
  
  /**
    Stops all plugins in the default logger repository. */
  public static void stopAllPlugins() {
    stopAllPlugins(LogManager.getLoggerRepository());
  }
  
  /**
    Stops all plugins in the given logger repository. */
  public static void stopAllPlugins(LoggerRepository repository) {
    synchronized(repositoryMap) {
      Hashtable pluginMap = (Hashtable)repositoryMap.get(repository);
      if (pluginMap == null)
        return;
        
      Enumeration enum = pluginMap.elements();
      while(enum.hasMoreElements()) {
        Plugin plugin = (Plugin)enum.nextElement();
        plugin.shutdown();
      }
      
      // since no more plugins, remove plugin map from
      // the repository
      repositoryMap.remove(repository);
    }
  }
}