/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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
 * A convienent abstract class for plugin subclasses that implements
 * the basic methods of the Plugin interface. Subclasses are required
 * to implement the isActive(), activateOptions(), and shutdown()
 * methods.
 * <p/>
 * <p>Developers are not required to subclass PluginSkeleton to
 * develop their own plugins (they are only required to implement the
 * Plugin interface), but it provides a convenient base class to start
 * from.
 * <p/>
 * Contributors: Nicko Cadell
 *
 * @author Mark Womack (mwomack@apache.org)
 * @author Paul Smith (psmith@apache.org)
 * @since 1.3
 */
public abstract class PluginSkeleton extends ComponentBase implements Plugin {
    /**
     * Name of this plugin.
     */
    protected String name = "";

    /**
     * Active state of plugin.
     */
    protected boolean active;

    /**
     * This is a delegate that does all the PropertyChangeListener
     * support.
     */
    private PropertyChangeSupport propertySupport =
            new PropertyChangeSupport(this);

    /**
     * Construct new instance.
     */
    protected PluginSkeleton() {
        super();
    }

    /**
     * Gets the name of the plugin.
     *
     * @return String the name of the plugin.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the plugin and notifies
     * PropertyChangeListeners of the change.
     *
     * @param newName the name of the plugin to set.
     */
    public void setName(final String newName) {
        String oldName = this.name;
        this.name = newName;
        propertySupport.firePropertyChange("name", oldName, this.name);
    }

    /**
     * Gets the logger repository for this plugin.
     *
     * @return LoggerRepository the logger repository this plugin will affect.
     */
    public LoggerRepository getLoggerRepository() {
        return repository;
    }

    /**
     * Sets the logger repository used by this plugin and notifies a
     * relevant PropertyChangeListeners registered. This
     * repository will be used by the plugin functionality.
     *
     * @param repository the logger repository that this plugin should affect.
     */
    public void setLoggerRepository(final LoggerRepository repository) {
        Object oldValue = this.repository;
        this.repository = repository;
        firePropertyChange("loggerRepository", oldValue, this.repository);
    }

    /**
     * Returns whether this plugin is Active or not.
     *
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
    public boolean isEquivalent(final Plugin testPlugin) {
        return (repository == testPlugin.getLoggerRepository())
                && ((this.name == null && testPlugin.getName() == null)
                || (this.name != null
                           && name.equals(testPlugin.getName())))
                && this.getClass().equals(testPlugin.getClass());
    }

    /**
     * Add property change listener.
     * @param listener listener.
     */
    public final void addPropertyChangeListener(
            final PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }

    /**
     * Add property change listener for one property only.
     * @param propertyName property name.
     * @param listener listener.
     */
    public final void addPropertyChangeListener(
            final String propertyName,
            final PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(propertyName, listener);
    }

    /**
     * Remove property change listener.
     * @param listener listener.
     */
    public final void removePropertyChangeListener(
            final PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }

    /**
     * Remove property change listener on a specific property.
     * @param propertyName property name.
     * @param listener listener.
     */
    public final void removePropertyChangeListener(
            final String propertyName,
            final PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(propertyName, listener);
    }

    /**
     * Fire a property change event to appropriate listeners.
     * @param evt change event.
     */
    protected final void firePropertyChange(
            final PropertyChangeEvent evt) {
        propertySupport.firePropertyChange(evt);
    }

    /**
     * Fire property change event to appropriate listeners.
     * @param propertyName property name.
     * @param oldValue old value.
     * @param newValue new value.
     */
    protected final void firePropertyChange(
            final String propertyName,
            final boolean oldValue,
            final boolean newValue) {
        propertySupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * Fire property change event to appropriate listeners.
     * @param propertyName property name.
     * @param oldValue old value.
     * @param newValue new value.
     */
    protected final void firePropertyChange(
            final String propertyName,
            final int oldValue, final int newValue) {
        propertySupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * Fire property change event to appropriate listeners.
     * @param propertyName property name.
     * @param oldValue old value.
     * @param newValue new value.
     */
    protected final void firePropertyChange(
            final String propertyName,
            final Object oldValue,
            final Object newValue) {
        propertySupport.firePropertyChange(propertyName, oldValue, newValue);
    }
}
