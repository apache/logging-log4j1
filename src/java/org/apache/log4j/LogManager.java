/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j;

import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.spi.RepositorySelector;
import org.apache.log4j.spi.DefaultRepositorySelector;
import org.apache.log4j.spi.RootCategory;
import org.apache.log4j.helpers.Loader;

import java.util.Enumeration;

/**
  Use the <code>LogManager</code> to retreive instances of {@link Logger}.

  @author Ceki G&uuml;lc&uuml;
*/
public class LogManager {

  static private Object guard = null;
  static private RepositorySelector repositorySelector;

  static {
    Hierarchy h = new Hierarchy(new RootCategory(Level.DEBUG));
    repositorySelector = new DefaultRepositorySelector(h);    
  }

  /**
     Sets <code>LoggerFactory</code> but only if the correct
     <em>guard</em> is passed as parameter.
     
     <p>Initally the guard is null.  If the guard is
     <code>null</code>, then invoking this method sets the logger
     factory and the guard. Following invocations will throw a {@link
     IllegalArgumentException}, unless the previously set
     <code>guard</code> is passed as the second parameter.

     <p>This allows a high-level component to set the logger factory to
     be used. Thus, fixing the log4j environment.
     
     <p>For example, when tomcat starts it will be able to install its
     own logger factory. However, if and when Tomcat is embedded
     within JBoss, then JBoss will install its loggger factory and
     Tomcat will use the factory set by its container, JBoss.
  */
  static
  public
  void setRepositorySelector(RepositorySelector selector, Object guard) 
                                                 throws IllegalArgumentException {
    if((LogManager.guard != null) && (LogManager.guard != guard)) {
      throw new IllegalArgumentException(
           "Attempted to reset the LoggerFactory without possessing the guard.");
    }

    if(selector == null) {
      throw new IllegalArgumentException("RepositorySelector must be non-null.");
    }

    LogManager.guard = guard;
    LogManager.repositorySelector = selector;

  }

  static
  public
  LoggerRepository getLoggerRepository() {
    return repositorySelector.getLoggerRepository();
  }

  /**
     Retrieve the appropriate root logger.
   */
  public
  static 
  Logger getRootLogger() {
     // Delegate the actual manufacturing of the logger to the logger repository.
    return repositorySelector.getLoggerRepository().getRootLogger();
  }

  /**
     Retrieve the appropriate {@link Logger} instance.  
  */
  public
  static 
  Logger getLogger(String name) {
     // Delegate the actual manufacturing of the logger to the logger factory.
    return repositorySelector.getLoggerRepository().getLogger(name);
  }

  /**
     Retrieve the appropriate {@link Logger} instance.  
  */
  public
  static 
  Logger getLogger(String name, LoggerFactory factory) {
     // Delegate the actual manufacturing of the logger to the logger factory.
    return repositorySelector.getLoggerRepository().getLogger(name, factory);
  }  

  public
  static
  Logger exists(String name) {
    return repositorySelector.getLoggerRepository().exists(name);
  }

  public
  static
  Enumeration getCurrentLoggers() {
    return repositorySelector.getLoggerRepository().getCurrentLoggers();
  }

  public
  static
  void shutdown() {
    repositorySelector.getLoggerRepository().shutdown();
  }

  public
  static
  void resetConfiguration() {
    repositorySelector.getLoggerRepository().resetConfiguration();
  }
}

