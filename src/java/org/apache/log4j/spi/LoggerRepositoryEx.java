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

package org.apache.log4j.spi;

import org.apache.log4j.Appender;
import org.apache.log4j.Category;
import org.apache.log4j.Logger;
import org.apache.log4j.plugins.PluginRegistry;
import org.apache.log4j.scheduler.Scheduler;

import java.util.List;
import java.util.Map;


/**
   A <code>LoggerRepository</code> is used to create and retrieve
   <code>Loggers</code>. The relation between loggers in a repository
   depends on the repository but typically loggers are arranged in a
   named hierarchy.

   <p>In addition to the creational methods, a
   <code>LoggerRepository</code> can be queried for existing loggers,
   can act as a point of registry for events related to loggers.

   @author Ceki G&uuml;lc&uuml;
   @author Mark Womack
   @author Curt Arnold
   @since 1.3 */
public interface LoggerRepositoryEx extends LoggerRepository {
  /**
    Add a {@link LoggerRepositoryEventListener} to the repository. The
    listener will be called when repository events occur.
     @param listener event listener, may not be null.
    @since 1.3*/
  void addLoggerRepositoryEventListener(
    LoggerRepositoryEventListener listener);

  /**
    Remove a {@link LoggerRepositoryEventListener} from the repository.
   @param listener listener.
    @since 1.3*/
  void removeLoggerRepositoryEventListener(
    LoggerRepositoryEventListener listener);

  /**
    Add a {@link LoggerEventListener} to the repository. The  listener
    will be called when repository events occur.
   @param listener listener, may not be null.
    @since 1.3*/
  void addLoggerEventListener(LoggerEventListener listener);

  /**
    Remove a {@link LoggerEventListener} from the repository.
   @param listener listener, may not be null.
    @since 1.3*/
  void removeLoggerEventListener(LoggerEventListener listener);

  /**
   * Get the name of this logger repository.
   * @return name, may not be null.
   * @since 1.3
   */
  String getName();

  /**
   * A logger repository is a named entity.
   * @param repoName new name, may not be null.
   * @since 1.3
   */
  void setName(String repoName);

  /**
   * Is the current configuration of the repository in its original (pristine)
   * state?
   * @return true if repository is in original state.
   *
   * @since 1.3
   */
  boolean isPristine();

  /**
   *  Set the pristine flag.
   * @param state state
   *  @see #isPristine
   *  @since 1.3
   */
  void setPristine(boolean state);

  /**
    Requests that a appender removed event be sent to any registered
    {@link LoggerEventListener}.
    @param logger The logger from which the appender was removed.
    @param appender The appender removed from the logger.
    @since 1.3*/
  void fireRemoveAppenderEvent(Category logger, Appender appender);

  /**
    Requests that a level changed event be sent to any registered
    {@link LoggerEventListener}.
    @param logger The logger which changed levels.
    @since 1.3*/
  void fireLevelChangedEvent(Logger logger);

  /**
    Requests that a configuration changed event be sent to any registered
    {@link LoggerRepositoryEventListener}.
    @since 1.3*/
  void fireConfigurationChangedEvent();

  /**
   * Return the PluginRegisty for this LoggerRepository.
   * @return plug in registry.
   * @since 1.3
   */
  PluginRegistry getPluginRegistry();

  /**
   * Return the {@link Scheduler} for this LoggerRepository.
   * @return scheduler.
   * @since 1.3
   */
  Scheduler getScheduler();

  /**
   * Get the properties specific for this repository.
   * @return property map.
   * @since 1.3
   */
  Map getProperties();

  /**
   * Get the property of this repository.
   * @param key property key.
   * @return key value or null if not set.
   * @since 1.3
   */
  String getProperty(String key);

  /**
   * Set a property of this repository.
   * @param key key, may not be null.
   * @param value new value, if null, property will be removed.
   * @since 1.3
   */
  void setProperty(String key, String value);

  /**
   * Errors which cannot be logged, go to the error list.
   *
   * @return List
   */
  List getErrorList();

  /**
   * Errors which cannot be logged, go to the error list.
   *
   * @param errorItem an ErrorItem to add to the error list
   */
  void addErrorItem(ErrorItem errorItem);

  /**
   * A LoggerRepository can also act as a store for various objects used
   * by log4j components.
   *
   * @param key key, may not be null.
   * @return The object stored under 'key'.
   * @since 1.3
   */
  Object getObject(String key);

  /**
   * Store an object under 'key'. If no object can be found, null is returned.
   *
   * @param key key, may not be null.
   * @param value value, may be null.
   */
  void putObject(String key, Object value);

  /**
   * Sets the logger factory used by {@link LoggerRepository#getLogger(String)}.
   * @param loggerFactory factory to use, may not be null
   * @since 1.3
   */
  void setLoggerFactory(LoggerFactory loggerFactory);

  /**
   * Returns the logger factory used by
   * {@link LoggerRepository#getLogger(String)}.
   *
   * @return non-null factory
   * @since 1.3
   */
  LoggerFactory getLoggerFactory();

}
