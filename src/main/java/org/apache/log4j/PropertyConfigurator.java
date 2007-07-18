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

// Contibutors: "Luke Blanshard" <Luke@quiq.com>
//              "Mark DONSZELMANN" <Mark.Donszelmann@cern.ch>
//               Anders Kristensen <akristensen@dynamicsoft.com>
package org.apache.log4j;

import org.apache.log4j.config.ConfiguratorBase;
import org.apache.log4j.config.PropertySetter;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.helpers.Constants;
import org.apache.log4j.or.RendererMap;

//import org.apache.log4j.config.PropertySetterException;
import org.apache.log4j.spi.*;
import org.apache.log4j.watchdog.FileWatchdog;
import org.apache.log4j.plugins.PluginRegistry;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import java.net.URL;
import java.net.MalformedURLException;


/**
   Allows the configuration of log4j from an external file.  See
   <b>{@link #doConfigure(String, LoggerRepository)}</b> for the
   expected format.

   <p>It is sometimes useful to see how log4j is reading configuration
   files. You can enable log4j internal logging by defining the
   <b>log4j.debug</b> variable.

   <P>As of log4j version 0.8.5, at class initialization time class,
   the file <b>log4j.properties</b> will be searched from the search
   path used to load classes. If the file can be found, then it will
   be fed to the {@link PropertyConfigurator#configure(java.net.URL)}
   method.

   <p>The <code>PropertyConfigurator</code> does not handle the
   advanced configuration features supported by the {@link
   org.apache.log4j.xml.DOMConfigurator DOMConfigurator} such as
   support for {@link org.apache.log4j.spi.Filter Filters}, nested
   appenders such as the {@link org.apache.log4j.AsyncAppender
   AsyncAppender}, etc.

   <p>All option <em>values</em> admit variable substitution. The
   syntax of variable substitution is similar to that of Unix
   shells. The string between an opening <b>&quot;${&quot;</b> and
   closing <b>&quot;}&quot;</b> is interpreted as a key. The value of
   the substituted variable can be defined as a system property or in
   the configuration file itself. The value of the key is first
   searched in the system properties, and if not found there, it is
   then searched in the configuration file being parsed.  The
   corresponding value replaces the ${variableName} sequence. For
   example, if <code>java.home</code> system property is set to
   <code>/home/xyz</code>, then every occurrence of the sequence
   <code>${java.home}</code> will be interpreted as
   <code>/home/xyz</code>.


   @author Ceki G&uuml;lc&uuml;
   @author Anders Kristensen
   @since 0.8.1 */
public class PropertyConfigurator extends ConfiguratorBase
    implements ConfiguratorEx {
  static final String CATEGORY_PREFIX = "log4j.category.";
  static final String LOGGER_PREFIX = "log4j.logger.";
  static final String FACTORY_PREFIX = "log4j.factory";
  static final String ADDITIVITY_PREFIX = "log4j.additivity.";
  static final String ROOT_CATEGORY_PREFIX = "log4j.rootCategory";
  static final String ROOT_LOGGER_PREFIX = "log4j.rootLogger";
  static final String APPENDER_PREFIX = "log4j.appender.";
  static final String RENDERER_PREFIX = "log4j.renderer.";
  static final String THRESHOLD_PREFIX = "log4j.threshold";

  /**
   * Makes log4j reset the configuration by setting it to true.
   */ 
  static final String RESET_KEY = "log4j.reset";
  
  /** Key for specifying the {@link org.apache.log4j.spi.LoggerFactory
      LoggerFactory}.  Currently set to "<code>log4j.loggerFactory</code>".  */
  public static final String LOGGER_FACTORY_KEY = "log4j.loggerFactory";
  private static final String INTERNAL_ROOT_NAME = "root";

  private static Object watchdogLock = new Object();
  private static FileWatchdog fileWatchdog = null;

  /**
     Used internally to keep track of configured appenders.
   */
  protected Hashtable registry = new Hashtable(11);
  protected LoggerFactory loggerFactory = new DefaultLoggerFactory();
  protected List errorList = new Vector();

  /**
    Read configuration from a file. <b>The existing configuration is
    not cleared nor reset.</b> If you require a different behavior,
    then call {@link  LogManager#resetConfiguration
    resetConfiguration} method before calling
    <code>doConfigure</code>.

    <p>The configuration file consists of statements in the format
    <code>key=value</code>. The syntax of different configuration
    elements are discussed below.

    <h3>Repository-wide threshold</h3>

    <p>The repository-wide threshold filters logging requests by level
    regardless of logger. The syntax is:

    <pre>
    log4j.threshold=[level]
    </pre>

    <p>The level value can consist of the string values OFF, FATAL,
    ERROR, WARN, INFO, DEBUG, ALL or a <em>custom level</em> value. A
    custom level value can be specified in the form
    level#classname. By default the repository-wide threshold is set
    to the lowest possible value, namely the level <code>ALL</code>.
    </p>


    <h3>Appender configuration</h3>

    <p>Appender configuration syntax is:
    <pre>
    # For appender named <i>appenderName</i>, set its class.
    # Note: The appender name can contain dots.
    log4j.appender.appenderName=fully.qualified.name.of.appender.class

    # Set appender specific options.
    log4j.appender.appenderName.option1=value1
    ...
    log4j.appender.appenderName.optionN=valueN
    </pre>

    For each named appender you can configure its {@link Layout}. The
    syntax for configuring an appender's layout is:
    <pre>
    log4j.appender.appenderName.layout=fully.qualified.name.of.layout.class
    log4j.appender.appenderName.layout.option1=value1
    ....
    log4j.appender.appenderName.layout.optionN=valueN
    </pre>

    <h3>Configuring loggers</h3>

    <p>The syntax for configuring the root logger is:
    <pre>
      log4j.rootLogger=[level], appenderName, appenderName, ...
    </pre>

    <p>This syntax means that an optional <em>level</em> can be
    supplied followed by appender names separated by commas.

    <p>The level value can consist of the string values OFF, FATAL,
    ERROR, WARN, INFO, DEBUG, ALL or a <em>custom level</em> value. A
    custom level value can be specified in the form
    <code>level#classname</code>.

    <p>If a level value is specified, then the root level is set
    to the corresponding level.  If no level value is specified,
    then the root level remains untouched.

    <p>The root logger can be assigned multiple appenders.

    <p>Each <i>appenderName</i> (separated by commas) will be added to
    the root logger. The named appender is defined using the
    appender syntax defined above.

    <p>For non-root categories the syntax is almost the same:
    <pre>
    log4j.logger.logger_name=[level|INHERITED|NULL], appenderName, appenderName, ...
    </pre>

    <p>The meaning of the optional level value is discussed above
    in relation to the root logger. In addition however, the value
    INHERITED can be specified meaning that the named logger should
    inherit its level from the logger hierarchy.

    <p>If no level value is supplied, then the level of the
    named logger remains untouched.

    <p>By default categories inherit their level from the
    hierarchy. However, if you set the level of a logger and later
    decide that that logger should inherit its level, then you should
    specify INHERITED as the value for the level value. NULL is a
    synonym for INHERITED.

    <p>Similar to the root logger syntax, each <i>appenderName</i>
    (separated by commas) will be attached to the named logger.

    <p>See the <a href="../../../../manual.html#additivity">appender
    additivity rule</a> in the user manual for the meaning of the
    <code>additivity</code> flag.

    <h3>ObjectRenderers</h3>

    You can customize the way message objects of a given type are
    converted to String before being logged. This is done by
    specifying an {@link org.apache.log4j.or.ObjectRenderer ObjectRenderer}
    for the object type would like to customize.

    <p>The syntax is:

    <pre>
    log4j.renderer.fully.qualified.name.of.rendered.class=fully.qualified.name.of.rendering.class
    </pre>

    As in,
    <pre>
    log4j.renderer.my.Fruit=my.FruitRenderer
    </pre>

    <h3>Logger Factories</h3>

    The usage of custom logger factories is discouraged and no longer
    documented.

    <h3>Example</h3>

    <p>An example configuration is given below. Other configuration
    file examples are given in the <code>examples</code> folder.

    <pre>

    # Set options for appender named "A1".
    # Appender "A1" will be a SyslogAppender
    log4j.appender.A1=org.apache.log4j.net.SyslogAppender

    # The syslog daemon resides on www.abc.net
    log4j.appender.A1.SyslogHost=www.abc.net

    # A1's layout is a PatternLayout, using the conversion pattern
    # <b>%r %-5p %c{2} %M.%L %x - %m\n</b>. Thus, the log output will
    # include # the relative time since the start of the application in
    # milliseconds, followed by the level of the log request,
    # followed by the two rightmost components of the logger name,
    # followed by the callers method name, followed by the line number,
    # the nested disgnostic context and finally the message itself.
    # Refer to the documentation of {@link PatternLayout} for further information
    # on the syntax of the ConversionPattern key.
    log4j.appender.A1.layout=org.apache.log4j.PatternLayout
    log4j.appender.A1.layout.ConversionPattern=%-4r %-5p %c{2} %M.%L %x - %m\n

    # Set options for appender named "A2"
    # A2 should be a RollingFileAppender, with maximum file size of 10 MB
    # using at most one backup file. A2's layout is TTCC, using the
    # ISO8061 date format with context printing enabled.
    log4j.appender.A2=org.apache.log4j.RollingFileAppender
    log4j.appender.A2.MaxFileSize=10MB
    log4j.appender.A2.MaxBackupIndex=1
    log4j.appender.A2.layout=org.apache.log4j.TTCCLayout
    log4j.appender.A2.layout.ContextPrinting=enabled
    log4j.appender.A2.layout.DateFormat=ISO8601

    # Root logger set to DEBUG using the A2 appender defined above.
    log4j.rootLogger=DEBUG, A2

    # Logger definitions:
    # The SECURITY logger inherits is level from root. However, it's output
    # will go to A1 appender defined above. It's additivity is non-cumulative.
    log4j.logger.SECURITY=INHERITED, A1
    log4j.additivity.SECURITY=false

    # Only warnings or above will be logged for the logger "SECURITY.access".
    # Output will go to A1.
    log4j.logger.SECURITY.access=WARN


    # The logger "class.of.the.day" inherits its level from the
    # logger hierarchy.  Output will go to the appender's of the root
    # logger, A2 in this case.
    log4j.logger.class.of.the.day=INHERITED
    </pre>

    <p>Refer to the <b>setOption</b> method in each Appender and
    Layout for class specific options.

    <p>Use the <code>#</code> or <code>!</code> characters at the
    beginning of a line for comments.

   <p>
   @param configFileName The name of the configuration file where the
   configuration information is stored.

  */
  public void doConfigure(String configFileName, LoggerRepository repo) {
    Properties props = new Properties();

      FileInputStream istream = null;
      try {
      istream = new FileInputStream(configFileName);
      props.load(istream);
    } catch (Exception e) {
      String errMsg =
        "Could not read configuration file [" + configFileName + "].";
      addError(new ErrorItem(errMsg, e));
      getLogger(repo).error(errMsg, e);
      return;
    } finally {
        if(istream != null) {
            try {
                istream.close();
            } catch(IOException ignored) {
            }
        }
    }

    // If we reach here, then the config file is alright.
    doConfigure(props, repo);
  }

  /**
   */
  public static void configure(String configFilename) {
    new PropertyConfigurator().doConfigure(
      configFilename, LogManager.getLoggerRepository());
  }

  /**
     Read configuration options from url <code>configURL</code>.

     @since 0.8.2
   */
  public static void configure(java.net.URL configURL) {
    new PropertyConfigurator().doConfigure(
      configURL, LogManager.getLoggerRepository());
  }

  /**
     Read configuration options from <code>properties</code>.

     See {@link #doConfigure(String, LoggerRepository)} for the expected format.
  */
  public static void configure(Properties properties) {
    new PropertyConfigurator().doConfigure(
      properties, LogManager.getLoggerRepository());
  }

  /**
    Like {@link #configureAndWatch(String, long)} except that the
    default delay of 60 seconds is used.

    @deprecated Use org.apache.log4j.watchdog.FileWatchdog directly.

    @param configFilename A log4j configuration file in XML format.

  */
  static public void configureAndWatch(String configFilename) {
    configureAndWatch(configFilename, 60000);
  }

  /**
    Read the configuration file <code>configFilename</code> if it
    exists. Moreover, a thread will be created that will periodically
    check if <code>configFilename</code> has been created or
    modified. The period is determined by the <code>delay</code>
    argument. If a change or file creation is detected, then
    <code>configFilename</code> is read to configure log4j.

    @deprecated Use org.apache.log4j.watchdog.FileWatchdog directly.

    @param configFilename A log4j configuration file in XML format.
    @param delay The delay in milliseconds to wait between each check.
  */
  static public void configureAndWatch(String configFilename, long delay) {
    synchronized(watchdogLock) {
      PluginRegistry pluginRegistry =
        ((LoggerRepositoryEx)LogManager.getLoggerRepository()).getPluginRegistry();

      // stop existing watchdog
      if (fileWatchdog != null) {
        pluginRegistry.stopPlugin(fileWatchdog.getName());
        fileWatchdog = null;
      }

      // create the watchdog
      fileWatchdog = new FileWatchdog();
      fileWatchdog.setName("PropertyConfigurator.FileWatchdog");
      fileWatchdog.setConfigurator(PropertyConfigurator.class.getName());
      fileWatchdog.setFile(configFilename);
      fileWatchdog.setInterval(delay);
      fileWatchdog.setInitialConfigure(true);

      // register and start the watchdog
      pluginRegistry.addPlugin(fileWatchdog);
      fileWatchdog.activateOptions();
    }
  }

  /**
     Read configuration options from <code>properties</code>.

     See {@link #doConfigure(String, LoggerRepository)} for the expected format.
  */
  public void doConfigure(Properties properties, LoggerRepository repository) {
    String debug = properties.getProperty(DEBUG_KEY);
    String reset = properties.getProperty(RESET_KEY);

    try {
      // we start by attaching a temporary list appender
      attachListAppender(repository);

      if (OptionConverter.toBoolean(reset, false)) {
        repository.resetConfiguration();
      }
      
      boolean attachedConsoleAppender = false;
      if ((debug != null) && OptionConverter.toBoolean(debug, true)) {
        attachTemporaryConsoleAppender(repository);
        attachedConsoleAppender = true;
      }
      
      // As soon as we start configuration process, the pristine flag is set to 
      // false.
      if(repository instanceof LoggerRepositoryEx) {
        ((LoggerRepositoryEx) repository).setPristine(false);
      }


      String thresholdStr =
        OptionConverter.findAndSubst(THRESHOLD_PREFIX, properties);

      if (thresholdStr != null) {
        repository.setThreshold(
            OptionConverter.toLevel(thresholdStr, Level.ALL));
        getLogger(repository).debug(
          "Hierarchy threshold set to [" + repository.getThreshold() + "].");
      }

      configureRootCategory(properties, repository);
      configureLoggerFactory(properties, repository);
      parseCatsAndRenderers(properties, repository);

      getLogger(repository).debug("Finished configuring.");

      if (attachedConsoleAppender) {
        detachTemporaryConsoleAppender(repository, errorList);
      }

      // We don't want to hold references to appenders preventing their
      // garbage collection.
      clearRegistry();
    } finally {
      detachListAppender(repository);
    }
  }

    /**
     * Clears the registry so that we don't hold references to Appender
     * objects, which would prevent their garbage collection.
     */
    protected void clearRegistry() {
      registry.clear();
    }

  /**
     Read configuration options from url <code>configURL</code>.
   */
  public void doConfigure(java.net.URL configURL, LoggerRepository repository) {
    Properties props = new Properties();
    getLogger(repository).debug(
      "Reading configuration from URL {}", configURL);

    InputStream in = null;
    try {
      in = configURL.openStream();
      props.load(in);
    } catch (Exception e) {
      String errMsg =
        "Could not read configuration file from URL [" + configURL + "].";
      addError(new ErrorItem(errMsg, e));
      getLogger(repository).error(errMsg, e);
      return;
    } finally {
        if (in != null) {
            try {
                in.close();
            } catch(IOException ignored) {
            }
        }
    }

    doConfigure(props, repository);
  }

  /**
   * Read configuration options from input stream <code>configStream</code>.
   * @since 1.3
   * @param configStream
   * @param repository
   */
  public void doConfigure(InputStream configStream,
                          LoggerRepository repository) {
    Properties props = new Properties();
    getLogger(repository).debug(
      "Reading configuration from input stream");

    try {
      props.load(configStream);
    } catch (java.io.IOException e) {
      String errMsg =
        "Could not read configuration file from input stream.";
      addError(new ErrorItem(errMsg, e));
      getLogger(repository).error(errMsg, e);
      return;
    }

    doConfigure(props, repository);
  }

  // --------------------------------------------------------------------------
  // Internal stuff
  // --------------------------------------------------------------------------

  /**
     Check the provided <code>Properties</code> object for a
     {@link org.apache.log4j.spi.LoggerFactory LoggerFactory}
     entry specified by {@link #LOGGER_FACTORY_KEY}.  If such an entry
     exists, an attempt is made to create an instance using the default
     constructor.  This instance is used for subsequent Category creations
     within this configurator.

     @see #parseCatsAndRenderers
   */
  protected void configureLoggerFactory(Properties props, LoggerRepository repository) {
    String factoryClassName =
      OptionConverter.findAndSubst(LOGGER_FACTORY_KEY, props);

    if (factoryClassName != null) {
      loggerFactory =
        (LoggerFactory) OptionConverter.instantiateByClassName(
          factoryClassName, LoggerFactory.class, loggerFactory);
      PropertySetter setter = new PropertySetter(loggerFactory);
      setter.setLoggerRepository(repository);
      setter.setProperties(props, FACTORY_PREFIX + ".");
    }
  }

  void configureRootCategory(Properties props, LoggerRepository repository) {
    String effectiveFrefix = ROOT_LOGGER_PREFIX;
    String value = OptionConverter.findAndSubst(ROOT_LOGGER_PREFIX, props);

    if (value == null) {
      value = OptionConverter.findAndSubst(ROOT_CATEGORY_PREFIX, props);
      effectiveFrefix = ROOT_CATEGORY_PREFIX;
    }

    if (value == null) {
      getLogger(repository).debug(
        "Could not find root logger information. Is this OK?");
    } else {
      Logger root = repository.getRootLogger();

      synchronized (root) {
        parseCategory(
          repository, props, root, effectiveFrefix, INTERNAL_ROOT_NAME, value);
      }
    }
  }

  /**
     Parse non-root elements, such non-root categories and renderers.
  */
  protected void parseCatsAndRenderers(
    Properties props, LoggerRepository repository) {
    Enumeration enumeration = props.propertyNames();

    while (enumeration.hasMoreElements()) {
      String key = (String) enumeration.nextElement();

      if (key.startsWith(CATEGORY_PREFIX) || key.startsWith(LOGGER_PREFIX)) {
        String loggerName = null;

        if (key.startsWith(CATEGORY_PREFIX)) {
          loggerName = key.substring(CATEGORY_PREFIX.length());
        } else if (key.startsWith(LOGGER_PREFIX)) {
          loggerName = key.substring(LOGGER_PREFIX.length());
        }

        String value = OptionConverter.findAndSubst(key, props);
        Logger logger = repository.getLogger(loggerName, loggerFactory);

        synchronized (logger) {
          parseCategory(repository, props, logger, key, loggerName, value);
          parseAdditivityForLogger(repository, props, logger, loggerName);
        }
      } else if (key.startsWith(RENDERER_PREFIX)) {
        String renderedClass = key.substring(RENDERER_PREFIX.length());
        String renderingClass = OptionConverter.findAndSubst(key, props);

        if (repository instanceof RendererSupport) {
          RendererSupport rs = (RendererSupport) repository;
          RendererMap rm = rs.getRendererMap();
          rm.addRenderer(renderedClass, renderingClass);
        }
      }
    }
  }

  /**
     Parse the additivity option for a non-root category.
   */
  void parseAdditivityForLogger(
    LoggerRepository repository, Properties props, Logger cat,
    String loggerName) {
    String value =
      OptionConverter.findAndSubst(ADDITIVITY_PREFIX + loggerName, props);
    getLogger(repository).debug(
      "Handling " + ADDITIVITY_PREFIX + loggerName + "=[" + value + "]");

    // touch additivity only if necessary
    if ((value != null) && (!value.equals(""))) {
      boolean additivity = OptionConverter.toBoolean(value, true);
      getLogger(repository).debug(
        "Setting additivity for \"" + loggerName + "\" to " + additivity);
      cat.setAdditivity(additivity);
    }
  }

  /**
     This method must work for the root category as well.
   */
  void parseCategory(
    LoggerRepository repository, Properties props, Logger logger,
    String optionKey, String loggerName, String value) {
    getLogger(repository).debug(
      "Parsing for [{}] with value=[{}].", loggerName, value);

    // We must skip over ',' but not white space
    StringTokenizer st = new StringTokenizer(value, ",");

    // If value is not in the form ", appender.." or "", then we should set
    // the level of the loggeregory.
    if (!(value.startsWith(",") || value.equals(""))) {
      // just to be on the safe side...
      if (!st.hasMoreTokens()) {
        return;
      }

      String levelStr = st.nextToken().trim();
      getLogger(repository).debug("Level token is [{}].", levelStr);

      // If the level value is inherited, set category level value to
      // null. We also check that the user has not specified inherited for the
      // root category.
      if (
        Configurator.INHERITED.equalsIgnoreCase(levelStr)
          || Configurator.NULL.equalsIgnoreCase(levelStr)) {
        if (loggerName.equals(INTERNAL_ROOT_NAME)) {
          getLogger(repository).warn("The root logger cannot be set to null.");
        } else {
          logger.setLevel(null);
        }
      } else {
        logger.setLevel(OptionConverter.toLevel(levelStr, Level.DEBUG));
      }

      getLogger(repository).debug(
        "Category {} set to {}.", loggerName, logger.getLevel());
    }

    // Begin by removing all existing appenders.
    logger.removeAllAppenders();

    Appender appender;
    String appenderName;

    while (st.hasMoreTokens()) {
      appenderName = st.nextToken().trim();

      if ((appenderName == null) || appenderName.equals(",")) {
        continue;
      }

      getLogger(repository).debug(
        "Parsing appender named \"{}\".", appenderName);
      appender = parseAppender(repository, props, appenderName);

      if (appender != null) {
        logger.addAppender(appender);
      }
    }
  }

  Appender parseAppender(
    LoggerRepository repository, Properties props, String appenderName) {
    Appender appender = registryGet(appenderName);

    if ((appender != null)) {
      getLogger(repository).debug(
        "Appender \"{}\" was already parsed.", appenderName);

      return appender;
    }

    // Appender was not previously initialized.
    String prefix = APPENDER_PREFIX + appenderName;
    String layoutPrefix = prefix + ".layout";

    appender =
      (Appender) OptionConverter.instantiateByKey(
        props, prefix, org.apache.log4j.Appender.class, null);

    if (appender == null) {
      String errMsg =
        "Could not instantiate appender named \"" + appenderName + "\".";
      addError(new ErrorItem(errMsg));
      getLogger(repository).error(errMsg);
      return null;
    }

    appender.setName(appenderName);
    appender.setLoggerRepository(repository);
    if (appender instanceof OptionHandler) {
      String layoutClassName =
        OptionConverter.findAndSubst(layoutPrefix, props);

      // if there are layout related directives, we process these now
      if (layoutClassName != null) {
        // Trim layoutClassName to avoid trailing spaces that cause problems.
        Layout layout =
          (Layout) OptionConverter.instantiateByClassName(
            layoutClassName.trim(), Layout.class, null);

        if (layout != null) {
          layout.setLoggerRepository(repository);
          appender.setLayout(layout);
          getLogger(repository).debug(
            "Parsing layout options for \"" + appenderName + "\".");

          PropertySetter layoutPS = new PropertySetter(layout);
          layoutPS.setLoggerRepository(repository);
          layoutPS.setProperties(props, layoutPrefix + ".");

          activateOptions(layout);
          getLogger(repository).debug(
            "End of parsing for \"" + appenderName + "\".");
        }
      }

      PropertySetter appenderPS = new PropertySetter(appender);
      appenderPS.setLoggerRepository(repository);
      appenderPS.setProperties(props, prefix + ".");
      activateOptions(appender);
      getLogger(repository).debug("Parsed \"" + appenderName + "\" options.");
    }

    registryPut(appender);

    return appender;
  }

  void activateOptions(Object obj) {
    if (obj instanceof OptionHandler) {
      ((OptionHandler) obj).activateOptions();
    }
  }

  void registryPut(Appender appender) {
    registry.put(appender.getName(), appender);
  }

  Appender registryGet(String name) {
    return (Appender) registry.get(name);
  }

  public List getErrorList() {
    return errorList;
  }
}
