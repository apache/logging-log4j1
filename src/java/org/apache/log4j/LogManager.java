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
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.helpers.LogLog;

import java.net.URL;
import java.net.MalformedURLException;


import java.util.Enumeration;

/**
  Use the <code>LogManager</code> to retreive instances of {@link Logger}.

  @author Ceki G&uuml;lc&uuml;
*/
public class LogManager {

  /**
     This string constant is set to <b>log4j.properties</b> the name
     of the file that will be searched by default in classpath. If the
     file can be found, then it is fed to the {@link
     PropertyConfigurator}.

     See also {@link #DEFAULT_CONFIGURATION_KEY} for a more general
     alternative.

     <p>See also the full description of <a
     href="../../../../manual.html#defaultInit">default
     intialization</a> procedure.

     @since 0.8.5 */
     static public final String DEFAULT_CONFIGURATION_FILE = "log4j.properties";
     
  /**
     This string constant is set to <b>log4j.configuration</b>. 

     <p>It corresponds to name of a system property that, if set,
     specifies the name of the resource containing the properties file
     or {@link URL} with which log4j should configure itself. See
     {@link OptionConverter#selectAndConfigure} for more detailed
     information on the processing of this option.

     <p>Setting the <b>log4j.configuration</b> system property
     overrides the default search for the file <b>log4j.properties</b>.

     <p>Note that all property keys are case sensitive.  

     <p>See also the full description of <a
     href="../../../../manual.html#defaultInit">default
     intialization</a> procedure.

     @since 1.0 */
     static final public String DEFAULT_CONFIGURATION_KEY="log4j.configuration";

 /**
     This string constant is set to <b>log4j.configuratorClass</b>. 

     <p>It corresponds to name of a system property that, if set,
     specifies the class name to use to automatically configure
     log4j. See {@link OptionConverter#selectAndConfigure} for more
     detailed information on the processing of this option.

     <p>Setting the <b>log4j.configuration</b> system property
     overrides the default search for the file <b>log4j.properties</b>.

     <p>Note that all property keys are case sensitive.  

     <p>See also the full description of <a
     href="../../../../manual.html#defaultInit">default
     intialization</a> procedure.
   
     @since 1.2 */
     static final public String CONFIGURATOR_CLASS_KEY="log4j.configuratorClass";

  /**
      Setting the system property <b>log4j.defaultInitOverride</b> to
      "true" or any other value than "false" will skip default
      configuration process.

     <p>The current value of the DEFAULT_INIT_OVERRIDE_KEY string
     constant is <b>log4j.defaultInitOverride</b>.

     <p>See also the full description of <a
     href="../../../../manual.html#defaultInit">default
     intialization</a> procedure.

     <p>Note that all property keys are case sensitive.  

     @since 0.8.5 */
  public static final String DEFAULT_INIT_OVERRIDE_KEY = 
                                                 "log4j.defaultInitOverride";


  static private Object guard = null;
  static private RepositorySelector repositorySelector;

  static {
    // By default we use a DefaultRepositorySelector which always returns 'h'.
    Hierarchy h = new Hierarchy(new RootCategory(Level.DEBUG));
    repositorySelector = new DefaultRepositorySelector(h);

    /** Search for the properties file log4j.properties in the CLASSPATH.  */
    String override =OptionConverter.getSystemProperty(DEFAULT_INIT_OVERRIDE_KEY,
						       null);

    // if there is no default init override, them get the resource
    // specified by the user or the default config file.
    if(override == null || "false".equalsIgnoreCase(override)) {
      String resource = OptionConverter.getSystemProperty(
						  DEFAULT_CONFIGURATION_KEY, 
						  DEFAULT_CONFIGURATION_FILE);

      String configuratorClassName = OptionConverter.getSystemProperty(
                                                   CONFIGURATOR_CLASS_KEY, 
						   null);

      URL url = null;
      try {
	// so, resource is not a URL:
	// attempt to get the resource from the class path
	url = new URL(resource);
      } catch (MalformedURLException ex) {
	url = Loader.getResource(resource); 
      }	
      
      // If we have a non-null url, then delegate the rest of the
      // configuration to the OptionConverter.selectAndConfigure
      // method.
      if(url != null) {
	LogLog.debug("Using URL ["+url+"] for automatic log4j configuration.");        
	OptionConverter.selectAndConfigure(url, configuratorClassName, 
					   LogManager.getLoggerRepository());
      } else {
	LogLog.debug("Could not find resource: ["+resource+"].");
      }
    }  
  } 

  /**
     Sets <code>LoggerFactory</code> but only if the correct
     <em>guard</em> is passed as parameter.
     
     <p>Initally the guard is null.  If the guard is
     <code>null</code>, then invoking this method sets the logger
     factory and the guard. Following invocations will throw a {@link
     IllegalArgumentException}, unless the previously set
     <code>guard</code> is passed as the second parameter.

     <p>This allows a high-level component to set the {@link
     RepositorySelector} used by the <code>LogManager</code>.
     
     <p>For example, when tomcat starts it will be able to install its
     own repository selector. However, if and when Tomcat is embedded
     within JBoss, then JBoss will install its own repository selector
     and Tomcat will use the repository selector set by its container,
     JBoss.  */
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
     // Delegate the actual manufacturing of the logger to the logger repository.
    return repositorySelector.getLoggerRepository().getLogger(name);
  }

 /**
     Retrieve the appropriate {@link Logger} instance.  
  */
  public
  static 
  Logger getLogger(Class clazz) {
     // Delegate the actual manufacturing of the logger to the logger repository.
    return repositorySelector.getLoggerRepository().getLogger(clazz.getName());
  }


  /**
     Retrieve the appropriate {@link Logger} instance.  
  */
  public
  static 
  Logger getLogger(String name, LoggerFactory factory) {
     // Delegate the actual manufacturing of the logger to the logger repository.
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

