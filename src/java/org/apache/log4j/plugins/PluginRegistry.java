/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j.plugins;

import java.util.HashMap;
import java.util.Iterator;
import org.apache.log4j.LogManager;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.LoggerRepositoryEventListener;

/**
  This is a registry for Plugin instances. It provides methods to 
  start and stop plugin objects individually and to stop all
  plugins for a repository.
  
  @author Mark Womack
  @since 1.3
*/
public class PluginRegistry {
  private static HashMap repositoryMap = new HashMap();
  private static RepositoryListener listener = new RepositoryListener();
  
  /**
    Starts a Plugin with default logger repository. */
  public static Plugin startPlugin(Plugin plugin) {
    // if repository already set in plugin, use it
    LoggerRepository repository = plugin.getLoggerRepository();
    
    // else use the default one
    if (repository == null) {
      repository = LogManager.getLoggerRepository();
    }
    
    return startPlugin(plugin, repository);
  }

  /**
    Starts a plugin with a given logger repository. */
  public static Plugin startPlugin(Plugin plugin,
  LoggerRepository repository) {
    
    // if the plugin is already active, just return it
    if (plugin.isActive())
      return plugin;
      
    // put plugin into the repository's reciever map
    synchronized(repositoryMap) {
      // get plugin map for repository
      HashMap pluginMap = (HashMap)repositoryMap.get(repository);
      
      String name = plugin.getName();

      // make sure the plugin has reference to repository
      plugin.setLoggerRepository(repository);

      // if the plugin map does not exist, create one
      if (pluginMap == null) {
        pluginMap = new HashMap();
        repositoryMap.put(repository, pluginMap);
        repository.addLoggerRepositoryEventListener(listener);
      }
      else {
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
      }
      
      // put the new plugin into the map
      pluginMap.put(name, plugin);
      
      // start the new plugin
      plugin.activateOptions();
      
      return plugin;
    }
  }
  
  /**
    Stops a plugin by plugin object. */
  public static Plugin stopPlugin(Plugin plugin) {
    return stopPlugin(plugin.getName(), plugin.getLoggerRepository());
  }
  
  /**
    Stops a plugin by plugin name using default repository. */
  public static Plugin stopPlugin(String pluginName) {
    return stopPlugin(pluginName, LogManager.getLoggerRepository());
  }

  /**
    Stops a plugin by plugin name and repository. */
  public static Plugin stopPlugin(String pluginName,
  LoggerRepository repository) {
    // if a null repository, exit now
    if (repository == null) {
      return null;
    }

    synchronized(repositoryMap) {
      HashMap pluginMap = (HashMap)repositoryMap.get(repository);
      if (pluginMap == null)
        return null;
        
      Plugin plugin = (Plugin)pluginMap.get(pluginName);
      if (plugin == null)
        return null;
      
      // shutdown the plugin
      plugin.shutdown();
      
      // remove it from the plugin map
      pluginMap.remove(pluginName);
      
      // if no more plugins, remove the plugin map from
      // repository map
      if (pluginMap.isEmpty()) {
        repository.removeLoggerRepositoryEventListener(listener);
        repositoryMap.remove(repository);
      }
      
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
      HashMap pluginMap = (HashMap)repositoryMap.get(repository);
      if (pluginMap == null)
        return;
        
      // remove the listener for this repository
      repository.removeLoggerRepositoryEventListener(listener);

      Iterator iter = pluginMap.values().iterator();
      while(iter.hasNext()) {
        ((Plugin)iter.next()).shutdown();
      }
      
      // since no more plugins, remove plugin map from
      // the repository
      repositoryMap.remove(repository);
    }
  }
  
  /**
    Internal class used to handle listener events from repositories. */
  private static class RepositoryListener
  implements LoggerRepositoryEventListener {
    /**
      Stops all plugins associated with the repository being reset. */
    public void configurationResetEvent(LoggerRepository repository) {
      PluginRegistry.stopAllPlugins(repository);
    }
  
    /**
      Called when the repository configuration is changed. */
    public void configurationChangedEvent(LoggerRepository repository) {
      // do nothing with this event
    }
  
    /**
      Stops all plugins associated with the repository being shutdown. */
    public void shutdownEvent(LoggerRepository repository) {
      PluginRegistry.stopAllPlugins(repository);
    }
  }
}