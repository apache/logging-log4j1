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

package org.apache.log4j.plugins;

import org.apache.log4j.spi.ComponentBase;
import org.apache.log4j.spi.LoggerRepository;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;


/**
  A convienent abstract class for plugin subclasses that implements
  the basic methods of the Plugin interface. Subclasses are required
  to implement the isActive(), activateOptions(), and shutdown()
  methods.

  <p>Developers are not required to subclass PluginSkeleton to
  develop their own plugins (they are only required to implement the
  Plugin interface), but it provides a convenient base class to start
  from.

  Contributors: Nicko Cadell

  @author Mark Womack <mwomack@apache.org>
  @author Paul Smith <psmith@apache.org>
  @since 1.3
*/
public abstract class PluginSkeleton extends ComponentBase implements Plugin {
  /** Name of this plugin. */
  protected String name = "";

  protected boolean active;

  /**
   * This is a delegate that does all the PropertyChangeListener
   * support.
   */
  private PropertyChangeSupport propertySupport =
    new PropertyChangeSupport(this);

  /**
    Gets the name of the plugin.

    @return String the name of the plugin. */
  public String getName() {
    return name;
  }

  /**
    Sets the name of the plugin and notifies PropertyChangeListeners of the change

    @param name the name of the plugin to set. */
  public void setName(String name) {
    String oldName = this.name;
    this.name = name;
    propertySupport.firePropertyChange("name", oldName, this.name);
  }

  /**
    Gets the logger repository for this plugin.

    @return LoggerRepository the logger repository this plugin will affect. */
  public LoggerRepository getLoggerRepository() {
    return repository;
  }

  /**
    Sets the logger repository used by this plugin and notifies an relevant PropertyChangeListeners registered. This
    repository will be used by the plugin functionality.

    @param repository the logger repository that this plugin should affect. */
  public void setLoggerRepository(LoggerRepository repository) {
    Object oldValue = this.repository;
    this.repository = repository;
    firePropertyChange("loggerRepository", oldValue, this.repository);
  }

  /**
   * Returns whether this plugin is Active or not
   * @return true/false
   */
  public synchronized boolean isActive() {
    return active;
  }

  /**
   * Returns true if the plugin has the same name and logger repository as the
   * testPlugin passed in.
   * 
   * @param testPlugin The plugin to test equivalency against.
   * @return Returns true if testPlugin is considered to be equivalent.
   */
  public boolean isEquivalent(Plugin testPlugin) {
    return (repository == testPlugin.getLoggerRepository()) &&
      ((this.name == null && testPlugin.getName() == null) ||
       (this.name != null && name.equals(testPlugin.getName()))) && this.getClass().equals(testPlugin.getClass());
  }

  /**
   * @param listener
   */
  public final void addPropertyChangeListener(PropertyChangeListener listener) {
    propertySupport.addPropertyChangeListener(listener);
  }

  /**
   * @param propertyName
   * @param listener
   */
  public final void addPropertyChangeListener(
    String propertyName, PropertyChangeListener listener) {
    propertySupport.addPropertyChangeListener(propertyName, listener);
  }

  /**
   * @param listener
   */
  public final void removePropertyChangeListener(
    PropertyChangeListener listener) {
    propertySupport.removePropertyChangeListener(listener);
  }

  /**
   * @param propertyName
   * @param listener
   */
  public final void removePropertyChangeListener(
    String propertyName, PropertyChangeListener listener) {
    propertySupport.removePropertyChangeListener(propertyName, listener);
  }

  /**
   * @param evt
   */
  protected final void firePropertyChange(PropertyChangeEvent evt) {
    propertySupport.firePropertyChange(evt);
  }

  /**
   * @param propertyName
   * @param oldValue
   * @param newValue
   */
  protected final void firePropertyChange(
    String propertyName, boolean oldValue, boolean newValue) {
    propertySupport.firePropertyChange(propertyName, oldValue, newValue);
  }

  /**
   * @param propertyName
   * @param oldValue
   * @param newValue
   */
  protected final void firePropertyChange(
    String propertyName, int oldValue, int newValue) {
    propertySupport.firePropertyChange(propertyName, oldValue, newValue);
  }

  /**
   * @param propertyName
   * @param oldValue
   * @param newValue
   */
  protected final void firePropertyChange(
    String propertyName, Object oldValue, Object newValue) {
    propertySupport.firePropertyChange(propertyName, oldValue, newValue);
  }
}
