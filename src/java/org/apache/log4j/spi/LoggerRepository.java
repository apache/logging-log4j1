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

package org.apache.log4j.spi;

import org.apache.log4j.*;
import org.apache.log4j.plugins.PluginRegistry;

import java.util.Enumeration;
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
   @since 1.2 */
public interface LoggerRepository {
  /**
    Add a {@link LoggerRepositoryEventListener} to the repository. The
    listener will be called when repository events occur.
    @since 1.3*/
  public void addLoggerRepositoryEventListener(
    LoggerRepositoryEventListener listener);

  /**
    Remove a {@link LoggerRepositoryEventListener} from the repository.
    @since 1.3*/
  public void removeLoggerRepositoryEventListener(
    LoggerRepositoryEventListener listener);

  /**
    Add a {@link LoggerEventListener} to the repository. The  listener
    will be called when repository events occur.
    @since 1.3*/
  public void addLoggerEventListener(LoggerEventListener listener);

  
  /**
    Remove a {@link LoggerEventListener} from the repository.
    @since 1.3*/
  public void removeLoggerEventListener(LoggerEventListener listener);

  /**
     Is the repository disabled for a given level? The answer depends
     on the repository threshold and the <code>level</code>
     parameter. See also {@link #setThreshold} method.  */
  boolean isDisabled(int level);


  /**
   * Get the name of this logger repository.
   * @since 1.3
   */
  public String getName();

  
  /**
   * A logger repository is a named entity.
   * @since 1.3
   */
  public void setName(String repoName);

  /**
     Set the repository-wide threshold. All logging requests below the
     threshold are immediately dropped. By default, the threshold is
     set to <code>Level.ALL</code> which has the lowest possible rank.  */
  public void setThreshold(Level level);
  
  /**
      Another form of {@link #setThreshold(Level)} accepting a string
      parameter instead of a <code>Level</code>. */
  public void setThreshold(String val);

  public void emitNoAppenderWarning(Category cat);
  
  /**
     Get the repository-wide threshold. See {@link
     #setThreshold(Level)} for an explanation. */
  public Level getThreshold();

  public Logger getLogger(String name);

  public Logger getLogger(String name, LoggerFactory factory);

  public Logger getRootLogger();

  /**
   * Is the current configuration of the reposiroty, the original (pristine)
   * configuration?
   * 
   * @since 1.3
   */
  public boolean isPristine();
  
  /**
   *  Set the pristine flag. 
   *  @see #isPristine 
   *  @since 1.3
   */
  public void setPristine(boolean state);
  
  public abstract Logger exists(String name);

  public abstract void shutdown();

  public Enumeration getCurrentLoggers();

  /**
     @deprecated Please use {@link #getCurrentLoggers} instead.  */
  public Enumeration getCurrentCategories();

  public abstract void resetConfiguration();

  /**
    Requests that a appender added event be sent to any registered
    {@link LoggerEventListener}.
    @param logger The logger to which the appender was added.
    @param appender The appender added to the logger.
    @since 1.3*/
  public abstract void fireAddAppenderEvent(Logger logger, Appender appender);

  /**
    Requests that a appender removed event be sent to any registered
    {@link LoggerEventListener}.
    @param logger The logger from which the appender was removed.
    @param appender The appender removed from the logger.
    @since 1.3*/
  public abstract void fireRemoveAppenderEvent(
    Logger logger, Appender appender);

  /**
    Requests that a level changed event be sent to any registered
    {@link LoggerEventListener}.
    @param logger The logger which changed levels.
    @since 1.3*/
  public abstract void fireLevelChangedEvent(Logger logger);

  /**
    Requests that a configuration changed event be sent to any registered
    {@link LoggerRepositoryEventListener}.
    @param logger The logger which changed levels.
    @since 1.3*/
  public abstract void fireConfigurationChangedEvent();
  
  /**
   * Return the PluginRegisty for this LoggerRepository.
   * @since 1.3
   */
  public PluginRegistry getPluginRegistry();

  /** 
   * Get the properties specific for this repository.
   * @since 1.3
   */
  public Map getProperties();

  /** 
   * Get the property of this repository.
   * @since 1.3
   */
  public String getProperty(String key);

  /** 
   * Set a property of this repository.
   * @since 1.3
   */
  public void setProperty(String key, String value);
  
}
