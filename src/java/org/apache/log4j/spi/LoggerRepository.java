/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j.spi;

import org.apache.log4j.*;
import java.util.Enumeration;

/**
   A <code>LoggerRepository</code> is used to create and retrieve
   <code>Loggers</code>. The relation between loggers in a repository
   depends on the repository but typically loggers are arranged in a
   named hierarchy.

   <p>In addition to the creational methods, a
   <code>LoggerRepository</code> can be queried for existing loggers,
   can act as a point of registry for events related to loggers.

   @author Ceki G&uuml;lc&uuml;
   @since 1.2 */
public interface LoggerRepository {

  /**
     Add a {@link HierarchyEventListener} event to the repository.
     @deprecated As of v1.3, use {@link #addLoggerRepositoryEventListener}
     and {@link addLoggerEventListener} methods instead.
  */
  public void addHierarchyEventListener(HierarchyEventListener listener);

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

  public abstract Logger exists(String name);

  public abstract void shutdown();

  public Enumeration getCurrentLoggers();

  /**
     @deprecated Please use {@link #getCurrentLoggers} instead.  */
  public Enumeration getCurrentCategories();

  public abstract void resetConfiguration();

  /**
    @deprecated As of 1.3, please use fireAddAppenderEvent(Logger,Appender).*/
  public abstract void fireAddAppenderEvent(Category logger,
      Appender appender);

  /**
    Requests that a appender added event be sent to any registered
    {@link LoggerEventListener}.
    @param logger The logger to which the appender was added. 
    @param appender The appender added to the logger.
    @since 1.3*/
  public abstract void fireAddAppenderEvent(Logger logger,
    Appender appender);

  /**
    Requests that a appender removed event be sent to any registered
    {@link LoggerEventListener}.
    @param logger The logger from which the appender was removed. 
    @param appender The appender removed from the logger.
    @since 1.3*/
  public abstract void fireRemoveAppenderEvent(Logger logger,
    Appender appender);

  /**
    Requests that an all appenders removed event be sent to any registered
    {@link LoggerEventListener}. 
    @param logger The logger from which all appenders were removed.
    @since 1.3*/
  public abstract void fireRemoveAllAppendersEvent(Logger logger);

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
   
}
