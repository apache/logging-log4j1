/*
 * Copyright 1999,2006 The Apache Software Foundation.
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

  /**
   * @deprecated This variable is for internal use only. It will
   * become package protected in future versions.
   * */
  static public final String DEFAULT_CONFIGURATION_FILE = "log4j.properties";
  
  /**
   * @deprecated This variable is for internal use only. It will
   * become package protected in future versions.
   * */
  static final String DEFAULT_XML_CONFIGURATION_FILE = "log4j.xml";  
   
  /**
   * @deprecated This variable is for internal use only. It will
   * become private in future versions.
   * */
  static final public String DEFAULT_CONFIGURATION_KEY="log4j.configuration";

  /**
   * @deprecated This variable is for internal use only. It will
   * become private in future versions.
   * */
  static final public String CONFIGURATOR_CLASS_KEY="log4j.configuratorClass";

  /**
  * @deprecated This variable is for internal use only. It will
  * become private in future versions.
  */
  public static final String DEFAULT_INIT_OVERRIDE_KEY = 
                                                 "log4j.defaultInitOverride";

    /**
     * Concurrency guard.
     */
    private static Object guard = null;

    /**
     * The repository selector.
     */
    private static RepositorySelector repositorySelector;

    /**
     * The debug flag, false by default.
     * True will cause debug printing to System.out.
     * Modify via system property log4j.debug.
     */
    private static boolean debug = false;

    /**
     * The default LoggerRepository instance created by LogManager. This instance
     * is provided for the convenience of the {@link RepositorySelector} instance.
     * The selector, if it choses, may ignore this default repository.
     */
    public static final LoggerRepository defaultLoggerRepository;
  

    /**
     * The following static initializer gets invoked immediately after a call to 
     * Logger.getLogger() is made. Here is a description of the static initializer:
     *
     * - Create defaultLoggerRepository,
     * - Configure(defaultLoggerRepository) depending on system properties,
     *   during the configuration of defaultLoggerRepository a temporary repository 
     *   selector is used.
     */
    static {
        // Check debug
        String debugProp = System.getProperty("log4j.debug");
        if(Boolean.valueOf(debugProp).booleanValue()) {
            debug = true;
        }

        if(debug) {
            System.out.println("**Start of LogManager static initializer");
        }

        Hierarchy hierarchy =  new Hierarchy(new RootLogger(Level.DEBUG));
        defaultLoggerRepository = hierarchy;
        hierarchy.setName(Constants.DEFAULT_REPOSITORY_NAME);
        
        // temporary repository
        repositorySelector = new DefaultRepositorySelector(defaultLoggerRepository);
        
        //  Attempt to perform automatic configuration of the default repository
        String configuratorClassName =
            OptionConverter.getSystemProperty(Constants.CONFIGURATOR_CLASS_KEY, null);
        String configurationOptionStr = 
            OptionConverter.getSystemProperty(Constants.DEFAULT_CONFIGURATION_KEY, null);
        
        if (configurationOptionStr == null) {
            if (Loader.getResource(Constants.DEFAULT_XML_CONFIGURATION_FILE) != null) {
                configurationOptionStr = Constants.DEFAULT_XML_CONFIGURATION_FILE;
            } else if (
                       Loader.getResource(Constants.DEFAULT_CONFIGURATION_FILE) != null) {
                configurationOptionStr = Constants.DEFAULT_CONFIGURATION_FILE;
            }
        }
        
        if(debug) {
            System.out.println("*** configurationOptionStr=" + configurationOptionStr);
        }
      
        IntializationUtil.initialConfiguration(
                                               defaultLoggerRepository, configurationOptionStr, configuratorClassName);
        
        String repositorySelectorStr = 
            OptionConverter.getSystemProperty("log4j.repositorySelector", null);
        
        if (repositorySelectorStr == null) {
            // NOTHING TO DO, the default repository has been configured already
        } else if (repositorySelectorStr.equalsIgnoreCase("JNDI")) {
            if(debug) {
                System.out.println("*** Will use ContextJNDISelector **");
            }

            repositorySelector = new ContextJNDISelector();
            guard = new Object();
        } else {
            Object r =
                OptionConverter.instantiateByClassName(
                                                       repositorySelectorStr, RepositorySelector.class, null);
            
            if (r instanceof RepositorySelector) {
                if(debug) {
                    System.out.println(
                                       "*** Using [" + repositorySelectorStr
                                       + "] instance as repository selector.");
                }
                repositorySelector = (RepositorySelector) r;
                guard = new Object();
            } else {
                if(debug) {
                    System.out.println(
                                       "*** Could not insantiate [" + repositorySelectorStr
                                       + "] as repository selector.");
                    System.out.println("*** Using default repository selector");
                }

                repositorySelector = new DefaultRepositorySelector(defaultLoggerRepository);
            }
        }

        if(debug) {
            System.out.println("** End of LogManager static initializer");
        }
    }

    /**
       Sets <code>RepositorySelector</code> but only if the correct
       <em>guard</em> is passed as parameter.
       
       <p>Initally the guard is null, unless the JVM is started with the
       log4j.repositorySelector system property
       (-Dlog4j.repositorySelector=[JNDI | <fully qualified class name>]).
       If the guard is
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
}
