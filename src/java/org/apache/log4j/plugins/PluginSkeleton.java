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

  @author Mark Womack
  @author Paul Smith <psmith@apache.org>
  @since 1.3
*/
public abstract class PluginSkeleton implements Plugin {
  /** Name of this plugin. */
  protected String name = "";

  /** Repository this plugin is attached to. */
  protected LoggerRepository repository;
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
