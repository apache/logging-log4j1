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

package org.apache.log4j;

import org.apache.log4j.helpers.Constants;
import org.apache.log4j.helpers.IntializationUtil;
import org.apache.log4j.helpers.Loader;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.scheduler.Scheduler;
import org.apache.log4j.selector.ContextJNDISelector;
import org.apache.log4j.spi.DefaultRepositorySelector;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.RepositorySelector;
import org.apache.log4j.spi.RootLogger;

import java.util.Enumeration;


/**
 * Use the <code>LogManager</code> class to retreive {@link Logger}
 * instances or to operate on the current {@link
 * LoggerRepository}. When the <code>LogManager</code> class is loaded
 * into memory the default initalzation procedure is inititated. The
 * default intialization procedure</a> is described in the <a
 * href="../../../../manual.html#defaultInit">short log4j manual</a>.
 *
 * @author Ceki G&uuml;lc&uuml; */
public class LogManager {
  private static Object guard = null;
  private static RepositorySelector repositorySelector;
  private static Scheduler schedulerInstance = null;
  
  static {
    //System.out.println("**Start of LogManager static initializer");
    Hierarchy defaultHierarchy = new Hierarchy(new RootLogger(Level.DEBUG));
    defaultHierarchy.setName("default");

    String repositorySelectorStr =
      OptionConverter.getSystemProperty("log4j.repositorySelectorClass", null);

    if (repositorySelectorStr == null) {
      // By default we use a DefaultRepositorySelector which always returns
      // the defaultHierarchy.
      repositorySelector = new DefaultRepositorySelector();
    } else if (repositorySelectorStr.equalsIgnoreCase("JNDI")) {
      System.out.println("*** Will use ContextJNDISelector **");
      repositorySelector = new ContextJNDISelector();
    } else {
      Object r =
        OptionConverter.instantiateByClassName(
          repositorySelectorStr, RepositorySelector.class, null);

      if (r instanceof RepositorySelector) {
        System.out.println(
          "*** Using [" + repositorySelectorStr
          + "] instance as repository selector.");
        repositorySelector = (RepositorySelector) r;
      } else {
        System.out.println(
          "*** Could not insantiate [" + repositorySelectorStr
          + "] as repository selector.");
        System.out.println("*** Using default repository selector");
        repositorySelector = new DefaultRepositorySelector();
      }
    }

    // at this stage 'repositorySelector' should point to a valid selector
    repositorySelector.setDefaultRepository(defaultHierarchy);

    // configure log4j internal logging for the default hierarchy
    IntializationUtil.log4jInternalConfiguration(defaultHierarchy);

    //  Attempt to perform automatic configuration of the default hierarchy
    String configuratorClassName =
      OptionConverter.getSystemProperty(
        Constants.CONFIGURATOR_CLASS_KEY, null);
    String configurationOptionStr =
      OptionConverter.getSystemProperty(
        Constants.DEFAULT_CONFIGURATION_KEY, null);

    if (configurationOptionStr == null) {
      if (Loader.getResource(Constants.DEFAULT_XML_CONFIGURATION_FILE) != null) {
        configurationOptionStr = Constants.DEFAULT_XML_CONFIGURATION_FILE;
      } else if (
        Loader.getResource(Constants.DEFAULT_CONFIGURATION_FILE) != null) {
        configurationOptionStr = Constants.DEFAULT_CONFIGURATION_FILE;
      }
    }

    //System.out.println("*** configurationOptionStr=" + configurationOptionStr);

    IntializationUtil.initialConfiguration(
      defaultHierarchy, configurationOptionStr, configuratorClassName);
    

    //System.out.println("** End of LogManager static initializer");
  }

  /**
     Sets <code>RepositorySelector</code> but only if the correct
     <em>guard</em> is passed as parameter.

     <p>Initally the guard is null.  If the guard is
     <code>null</code>, then invoking this method sets the logger
     repository and the guard. Following invocations will throw a {@link
     IllegalArgumentException}, unless the previously set
     <code>guard</code> is passed as the second parameter.

     <p>This allows a high-level component to set the {@link
     RepositorySelector} used by the <code>LogManager</code>.

     <p>For example, when tomcat starts it will be able to install its
     own repository selector. However, if and when Tomcat is embedded
     within JBoss, then JBoss will install its own repository selector
     and Tomcat will use the repository selector set by its container,
     JBoss.  
     */
  public static void setRepositorySelector(
    RepositorySelector selector, Object guard) throws IllegalArgumentException {
    if ((LogManager.guard != null) && (LogManager.guard != guard)) {
      throw new IllegalArgumentException(
        "Attempted to reset the LoggerFactory without possessing the guard.");
    }
    if (selector == null) {
      throw new IllegalArgumentException(
        "RepositorySelector must be non-null.");
    }

    LogManager.guard = guard;
    LogManager.repositorySelector = selector;
  }


  /**
   * Return the repository selector currently in use.
   * 
   * @since 1.3
   * @return {@link RepositorySelector} currently in use.
   */
  public static RepositorySelector getRepositorySelector() {
    return  LogManager.repositorySelector;
  } 
  
  public static LoggerRepository getLoggerRepository() {
    return repositorySelector.getLoggerRepository();
  }

  /**
     Retrieve the appropriate root logger.
   */
  public static Logger getRootLogger() {
    // Delegate the actual manufacturing of the logger to the logger repository.
    return repositorySelector.getLoggerRepository().getRootLogger();
  }

  /**
     Retrieve the appropriate {@link Logger} instance.
  */
  public static Logger getLogger(String name) {
    // Delegate the actual manufacturing of the logger to the logger repository.
    return repositorySelector.getLoggerRepository().getLogger(name);
  }

  /**
      Retrieve the appropriate {@link Logger} instance.
   */
  public static Logger getLogger(Class clazz) {
    // Delegate the actual manufacturing of the logger to the logger repository.
    return repositorySelector.getLoggerRepository().getLogger(clazz.getName());
  }

  /**
     Retrieve the appropriate {@link Logger} instance.
  */
  public static Logger getLogger(String name, LoggerFactory factory) {
    // Delegate the actual manufacturing of the logger to the logger repository.
    return repositorySelector.getLoggerRepository().getLogger(name, factory);
  }

  public static Logger exists(String name) {
    return repositorySelector.getLoggerRepository().exists(name);
  }

  public static Enumeration getCurrentLoggers() {
    return repositorySelector.getLoggerRepository().getCurrentLoggers();
  }

  public static void shutdown() {
    repositorySelector.getLoggerRepository().shutdown();
  }

  public static void resetConfiguration() {
    repositorySelector.getLoggerRepository().resetConfiguration();
  }
  
  /**
   * 
   * Return a singleton {@link Scheduler} instance to be shared by multiple
   * receivers and watchdogs. 
   * 
   * @since 1.3
   */
  public static Scheduler getSchedulerInstance() {
    if(schedulerInstance == null) {
      schedulerInstance = new Scheduler();
      schedulerInstance.start();
    }
    return schedulerInstance;
  }
}
