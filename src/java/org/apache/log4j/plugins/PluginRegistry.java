/*
 * ============================================================================
 *                   The Apache Software License, Version 1.1
 * ============================================================================
 *
 *    Copyright (C) 1999 The Apache Software Foundation. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modifica-
 * tion, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of  source code must  retain the above copyright  notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any, must
 *    include  the following  acknowledgment:  "This product includes  software
 *    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
 *    Alternately, this  acknowledgment may  appear in the software itself,  if
 *    and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "log4j" and  "Apache Software Foundation"  must not be used to
 *    endorse  or promote  products derived  from this  software without  prior
 *    written permission. For written permission, please contact
 *    apache@apache.org.
 *
 * 5. Products  derived from this software may not  be called "Apache", nor may
 *    "Apache" appear  in their name,  without prior written permission  of the
 *    Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 * APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 * DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 * ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 * (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * This software  consists of voluntary contributions made  by many individuals
 * on  behalf of the Apache Software  Foundation.  For more  information on the
 * Apache Software Foundation, please see <http://www.apache.org/>.
 *
 */

package org.apache.log4j.plugins;

import org.apache.log4j.LogManager;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.LoggerRepositoryEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.event.EventListenerList;


/**
  This is a registry for Plugin instances. It provides methods to
  start and stop plugin objects individually and to stop all
  plugins for a repository.

  @author Mark Womack
  @author Paul Smith
  @since 1.3
*/
public class PluginRegistry {
  /** stores the map of plugins for each repository. */
  private static HashMap repositoryMap = new HashMap();

  /** the listener used to listen for repository events. */
  private static RepositoryListener listener = new RepositoryListener();
  private static final EventListenerList listenerList =
    new EventListenerList();

  /**
    Starts a Plugin with default logger repository.

    @param plugin the plugin to start.
    @return Plugin the plugin parameter or a plugin that was already
      active and was equal to the original plugin. */
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
   * Returns true if the specified name is already taken by
   * an existing Plugin registered for the default Repository.
   * @param name The name to check the repository for
   * @return true if the name is already in use, otherwise false
   */
  public static boolean pluginNameExists(String name) {
    LoggerRepository repository = LogManager.getLoggerRepository();

    return pluginNameExists(name, repository);
  }

  /**
   * Returns true if the specified name is already taken by
   * an existing Plugin registered within the scope of the specified
   * LoggerRepository.
   * @param name The name to check the repository for
   * @param repository the repository to check the name against
   * @return true if the name is already in use, otherwise false
   */
  public static boolean pluginNameExists(
    String name, LoggerRepository repository) {
    synchronized (repositoryMap) {
      Map pluginMap = (Map) repositoryMap.get(repository);

      if ((pluginMap != null) && pluginMap.containsKey(name)) {
        return true;
      }
    }

    return false;
  }

  /**
    Starts a plugin with a given logger repository.

    @param plugin the plugin to start.
    @param repository the logger repository to attach the plugin to.
    @return Plugin the plugin parameter or a plugin that was already
      active and was equal to the original plugin. */
  public static Plugin startPlugin(Plugin plugin, LoggerRepository repository) {
    // if the plugin is already active, just return it
    if (plugin.isActive()) {
      return plugin;
    }

    // put plugin into the repository's reciever map
    synchronized (repositoryMap) {
      // get plugin map for repository
      HashMap pluginMap = (HashMap) repositoryMap.get(repository);

      String name = plugin.getName();

      // make sure the plugin has reference to repository
      plugin.setLoggerRepository(repository);

      // if the plugin map does not exist, create one
      if (pluginMap == null) {
        pluginMap = new HashMap();
        repositoryMap.put(repository, pluginMap);
        repository.addLoggerRepositoryEventListener(listener);
      } else {
        Plugin existingPlugin = (Plugin) pluginMap.get(name);

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
      firePluginStarted(plugin);

      return plugin;
    }
  }

  /**
  * @param plugin
  */
  private static void firePluginStarted(Plugin plugin) {
    PluginListener[] listeners =
      (PluginListener[]) listenerList.getListeners(PluginListener.class);

    PluginEvent e = null;

    for (int i = 0; i < listeners.length; i++) {
      if (e == null) {
        e = new PluginEvent(plugin);
      }

      listeners[i].pluginStarted(e);
    }
  }

  private static void firePluginStopped(Plugin plugin) {
    PluginListener[] listeners =
      (PluginListener[]) listenerList.getListeners(PluginListener.class);

    PluginEvent e = null;

    for (int i = 0; i < listeners.length; i++) {
      if (e == null) {
        e = new PluginEvent(plugin);
      }

      listeners[i].pluginStopped(e);
    }
  }

  /**
      Returns all the plugins for a given repository.

      @param repository the logger repository to get the plugins from.
      @return List list of plugins from the repository. */
  public static List getPlugins(LoggerRepository repository) {
    synchronized (repositoryMap) {
      // get plugin map for repository
      Map pluginMap = (Map) repositoryMap.get(repository);

      if (pluginMap == null) {
        return Collections.EMPTY_LIST;
      } else {
        List pluginList = new ArrayList(pluginMap.size());
        Iterator iter = pluginMap.values().iterator();

        while (iter.hasNext()) {
          pluginList.add(iter.next());
        }

        return pluginList;
      }
    }
  }

  /**
    Returns all the plugins for a given repository that are instances
    of a certain class.

    @param repository the logger repository to get the plugins from.
    @param pluginClass the class the plugin must implement to be selected.
    @return List list of plugins from the repository. */
  public static List getPlugins(
    LoggerRepository repository, Class pluginClass) {
    synchronized (repositoryMap) {
      // get plugin map for repository
      Map pluginMap = (Map) repositoryMap.get(repository);

      if (pluginMap == null) {
        return Collections.EMPTY_LIST;
      } else {
        List pluginList = new ArrayList(pluginMap.size());
        Iterator iter = pluginMap.values().iterator();

        while (iter.hasNext()) {
          Object plugin = iter.next();

          if (pluginClass.isInstance(plugin)) {
            pluginList.add(plugin);
          }
        }

        return pluginList;
      }
    }
  }

  /**
    Stops a plugin by plugin object.

    @param plugin the plugin to stop.
    @return Plugin the plugin parameter, if stopped, or null if the
      the plugin was not found in the registry. */
  public static Plugin stopPlugin(Plugin plugin) {
    return stopPlugin(plugin.getName(), plugin.getLoggerRepository());
  }

  /**
    Stops a plugin by plugin name using default repository.

    @param pluginName name of the plugin to stop.
    @return Plugin the plugin, if stopped, or null if the
      the plugin was not found in the registry. */
  public static Plugin stopPlugin(String pluginName) {
    return stopPlugin(pluginName, LogManager.getLoggerRepository());
  }

  /**
    Stops a plugin by plugin name and repository.

    @param pluginName the name of the plugin to stop.
    @param repository the repository the plugin should be attached to.
    @return Plugin the plugin, if stopped, or null if the
      the plugin was not found in the registry. */
  public static Plugin stopPlugin(
    String pluginName, LoggerRepository repository) {
    // if a null repository, exit now
    if (repository == null) {
      return null;
    }

    synchronized (repositoryMap) {
      HashMap pluginMap = (HashMap) repositoryMap.get(repository);

      if (pluginMap == null) {
        return null;
      }

      Plugin plugin = (Plugin) pluginMap.get(pluginName);

      if (plugin == null) {
        return null;
      }

      // shutdown the plugin
      plugin.shutdown();

      // remove it from the plugin map
      pluginMap.remove(pluginName);
      firePluginStopped(plugin);

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
    Stops all plugins in the given logger repository.

    @param repository the logger repository to stop all plugins for. */
  public static void stopAllPlugins(LoggerRepository repository) {
    synchronized (repositoryMap) {
      HashMap pluginMap = (HashMap) repositoryMap.get(repository);

      if (pluginMap == null) {
        return;
      }

      // remove the listener for this repository
      repository.removeLoggerRepositoryEventListener(listener);

      Iterator iter = pluginMap.values().iterator();

      while (iter.hasNext()) {
		Plugin plugin = (Plugin) iter.next();
        plugin.shutdown();
        firePluginStopped(plugin);
      }

      // since no more plugins, remove plugin map from
      // the repository
      repositoryMap.remove(repository);
    }
  }

  /**
   * Adds a PluginListener to this registry to be notified
   * of PluginEvents
   *
   * @param l PluginListener to add to this registry
   */
  public static final void addPluginListener(PluginListener l) {
    listenerList.add(PluginListener.class, l);
  }

  /**
   * Removes a particular PluginListener from this registry
   * such that it will no longer be notified of PluginEvents
   *
   * @param l PluginListener to remove
   */
  public static final void removePluginListener(PluginListener l) {
    listenerList.remove(PluginListener.class, l);
  }

  /**
    Internal class used to handle listener events from repositories. */
  private static class RepositoryListener
    implements LoggerRepositoryEventListener {
    /**
      Stops all plugins associated with the repository being reset.

      @param repository the repository that was reset. */
    public void configurationResetEvent(LoggerRepository repository) {
      PluginRegistry.stopAllPlugins(repository);
    }

    /**
      Called when the repository configuration is changed.

      @param repository the repository that was changed. */
    public void configurationChangedEvent(LoggerRepository repository) {
      // do nothing with this event
    }

    /**
      Stops all plugins associated with the repository being shutdown.

      @param repository the repository being shutdown. */
    public void shutdownEvent(LoggerRepository repository) {
      PluginRegistry.stopAllPlugins(repository);
    }
  }
}
