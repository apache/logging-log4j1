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

import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.OptionHandler;

import java.beans.PropertyChangeListener;


/**
 * Defines the required interface for all Plugin objects.
 * <p/>
 * <p>A plugin implements some specific functionality to extend
 * the log4j framework.  Each plugin is associated with a specific
 * LoggerRepository, which it then uses/acts upon.  The functionality
 * of the plugin is up to the developer.
 * <p/>
 * <p>Examples of plugins are Receiver and Watchdog. Receiver plugins
 * allow for remote logging events to be received and processed by
 * a repository as if the event was sent locally. Watchdog plugins
 * allow for a repository to be reconfigured when some "watched"
 * configuration data changes.
 *
 * @author Mark Womack (mwomack@apache.org)
 * @author Nicko Cadell
 * @author Paul Smith (psmith@apache.org)
 * @since 1.3
 */
public interface Plugin extends OptionHandler {
    /**
     * Gets the name of the plugin.
     *
     * @return String the name of the plugin.
     */
    String getName();

    /**
     * Sets the name of the plugin.
     *
     * @param name the name of the plugin.
     */
    void setName(String name);

    /**
     * Gets the logger repository for this plugin.
     *
     * @return the logger repository to which this plugin is attached.
     */
    LoggerRepository getLoggerRepository();

    /**
     * Sets the logger repository used by this plugin. This
     * repository will be used by the plugin functionality.
     *
     * @param repository the logger repository to attach this plugin to.
     */
    void setLoggerRepository(LoggerRepository repository);

    /**
     * Adds a PropertyChangeListener to this instance which is
     * notified only by changes of the property with name propertyName.
     *
     * @param propertyName
     *    the name of the property in standard JavaBean syntax
     *    (e.g. for setName(), property="name")
     * @param l listener
     */
    void addPropertyChangeListener(
            String propertyName, PropertyChangeListener l);

    /**
     * Adds a PropertyChangeListener that will be notified of all property
     * changes.
     *
     * @param l The listener to add.
     */
    void addPropertyChangeListener(PropertyChangeListener l);

    /**
     * Removes a specific PropertyChangeListener from this instances
     * registry that has been mapped to be notified of all property
     * changes.
     *
     * @param l The listener to remove.
     */
    void removePropertyChangeListener(PropertyChangeListener l);

    /**
     * Removes a specific PropertyChangeListener from this instance's
     * registry which has been previously registered to be notified
     * of only a specific property change.
     *
     * @param propertyName property name, may not be null.
     * @param l listener to be removed.
     */
    void removePropertyChangeListener(
            String propertyName, PropertyChangeListener l);

    /**
     * True if the plugin is active and running.
     *
     * @return boolean true if the plugin is currently active.
     */
    boolean isActive();

    /**
     * Returns true if the testPlugin is considered to be "equivalent" to the
     * this plugin.  The equivalency test is at the discretion of the plugin
     * implementation.  The PluginRegistry will use this method when starting
     * new plugins to see if a given plugin is considered equivalent to an
     * already running plugin with the same name.  If they are considered to
     * be equivalent, the currently running plugin will be left in place, and
     * the new plugin will not be started.
     * <p/>
     * It is possible to override the equals() method, however this has
     * more meaning than is required for this simple test and would also
     * require the overriding of the hashCode() method as well.  All of this
     * is more work than is needed, so this simple method is used instead.
     *
     * @param testPlugin The plugin to test equivalency against.
     * @return Returns true if testPlugin is considered to be equivelent.
     */
    boolean isEquivalent(Plugin testPlugin);

    /**
     * Call when the plugin should be stopped.
     */
    void shutdown();
}
