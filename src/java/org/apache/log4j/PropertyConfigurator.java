/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.APL file.  */


// Contibutors: "Luke Blanshard" <Luke@quiq.com>
//              "Mark DONSZELMANN" <Mark.Donszelmann@cern.ch>
//              Anders Kristensen <akristensen@dynamicsoft.com>

package org.apache.log4j;

import org.apache.log4j.Category;
import org.apache.log4j.Priority;
import org.apache.log4j.DefaultCategoryFactory;
import org.apache.log4j.spi.OptionHandler;
import org.apache.log4j.spi.Configurator;
import org.apache.log4j.spi.CategoryFactory;
import org.apache.log4j.or.ObjectRenderer;
import org.apache.log4j.or.RendererMap;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.helpers.FileWatchdog;

import java.util.NoSuchElementException;
import java.util.Enumeration;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Hashtable;

/**
   Extends {@link BasicConfigurator} to provide configuration from an
   external file.  See <b>{@link #doConfigure(String, Hierarchy)}</b> for the
   expected format.

   <p>It is sometimes useful to see how log4j is reading configuration
   files. You can enable log4j internal logging by defining the
   <b>log4j.configDebug</b> variable. 

   <P>As of log4j version 0.8.5, at the initialization of the Category
   class, the file <b>log4j.properties</b> will be searched from the
   search path used to load classes. If the file can be found, then it
   will be fed to the {@link
   PropertyConfigurator#configure(java.net.URL)} method.

   <p>The <code>PropertyConfigurator</code> does not handle the
   advanced configuration features supported by the {@link
   org.apache.log4j.xml.DOMConfigurator DOMConfigurator} such as support for
   sub-classing of the Priority class, {@link org.apache.log4j.spi.Filter
   Filters}, custom {@link org.apache.log4j.spi.ErrorHandler ErrorHandlers},
   nested appenders such as the {@link org.apache.log4j.AsyncAppender
   AsyncAppender}, etc.

   <p><em>All option values admit variable substitution.</em> For
   example, if <code>java.home</code> system property is set to
   <code>/home/xyz</code> and the File option is set to the string
   <code>${java.home}/test.log</code>, then File option will be
   interpreted as the string <code>/home/xyz/test.log</code>.

   <p>The value of the substituted variable can be defined as a system
   property or in the configuration file file itself.

   <p>The syntax of variable substituion is similar to that of UNIX
   shells. The string between an opening <b>&quot;${&quot;</b> and
   closing <b>&quot;}&quot;</b> is interpreted as a key. Its value is
   searched in the system properties, and if not founf then in the
   configuration file being parsed.  The corresponding value replaces
   the ${variableName} sequence.



   @author Ceki G&uuml;lc&uuml;
   @since version 0.8.1 */
public class PropertyConfigurator extends BasicConfigurator
           implements Configurator {

  /**
     Used internally to keep track of configured appenders.
   */
  protected Hashtable registry = new Hashtable(11);
  protected CategoryFactory categoryFactory = new DefaultCategoryFactory();
  
  static final String CATEGORY_PREFIX = "log4j.category.";
  static final String ADDITIVITY_PREFIX = "log4j.additivity.";
  static final String ROOT_CATEGORY_PREFIX = "log4j.rootCategory";
  static final String APPENDER_PREFIX = "log4j.appender.";  
  static final String RENDERER_PREFIX = "log4j.renderer.";
  static final String CATEGORY_FACTORY_KEY = "log4j.categoryFactory";

  static final private String INTERNAL_ROOT_NAME = "root";
  
  /**
    Read configuration from a file. The existing configuration is not
    cleared nor reset. If you require a different call, behaviour,
    then call {@link BasicConfigurator#resetConfiguration
    resetConfiguration} method before calling
    <code>doConfigure</code>.

    <p>The configuration file consists of staments in the format
    <code>key=value</code>.

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
    log.appender.appenderName.layout=fully.qualified.name.of.layout.class
    log.appender.appenderName.layout.option1=value1
    ....
    log.appender.appenderName.layout.optionN=valueN
    </pre>
    
    <h3>Configuring categories</h3>

    <p>The syntax for configuring the root category is:
    <pre>
      log4j.rootCategory=[FATAL|ERROR|WARN|INFO|DEBUG], appenderName, appenderName, ...
    </pre>

    <p>This syntax means that one of the strings values ERROR, WARN,
    INFO or DEBUG can be supplied followed by appender names separated
    by commas.
    
    <p>If one of the optional priority values ERROR, WARN, INFO or
    DEBUG is given, the root priority is set to the corresponding
    priority.  If no priority value is specified, then the root
    priority remains untouched.

    <p>The root category can be assigned multiple appenders.
    
    <p>Each <i>appenderName</i> (seperated by commas) will be added to
    the root category. The named appender is defined using the
    appender syntax defined above.

    <p>For non-root categories the syntax is almost the same:
    <pre>
    log4j.category.category_name=[INHERITED|FATAL|ERROR|WARN|INFO|DEBUG], appenderName, appenderName, ...
    </pre>

    <p>Thus, one of the usual priority values FATAL, ERROR, WARN,
    INFO, or DEBUG can be optionally specified. For any any of these
    values the named category is assigned the corresponding
    priority. In addition however, the value INHERITED can be
    optionally specified which means that named category should
    inherit its priority from the category hierarchy.

    <p>If no priority value is supplied, then the priority of the
    named category remains untouched.

    <p>By default categories inherit their priority from the
    hierarchy. However, if you set the priority of a category and
    later decide that that category should inherit its priority, then
    you should specify INHERITED as the value for the priority value.
    
    <p>Similar to the root category syntax, each <i>appenderName</i>
    (seperated by commas) will be attached to the named category.
    
    <p>See the <a href="../../manual.html#additivity">appender
    additivity rule</a> in the user manual for the meaning of the
    <code>additivity</code> flag.

    <p>The user can override any of the {@link
    BasicConfigurator#disable} family of methods by setting the a key
    "log4j.disableOverride" to <code>true</code> or any value other
    than false. As in <pre> log4j.disableOverride=true </pre>

    <h3>ObjectRenderers</h3>
    
    You can customize the way message objects of a given type are
    converted to String before being logged. This is done by
    specifying an {@link org.apache.log4j.or.ObjectRenderer ObjectRenderer}
    for the object type would like to customize.

    <p>The syntax is:

    <pre>
    log4j.renreder.fully.qualified.name.of.rendered.class=fully.qualified.name.of.rendering.class
    </pre>

    As in,
    <pre>
    log4j.renderer.my.Fruit=my.FruitRenderer
    </pre>

    <h3>Class Factories</h3>

    In case you are using your own sub-types of the
    <code>Category</code> class and wish to use configuration files,
    then you <em>must</em> set the <code>categoryFactory</code> for
    the sub-type that you are using.
    
    <p>The syntax is:

    <pre>
    log4j.categoryFactory=fully.qualified.name.of.categoryFactory.class
    </pre>

    See {@link org.apache.log4j.examples.MyCategory} for an example.
    
    <h3>Example</h3>

    <p>An example configuration is given below. Other configuration
    file examples are given in {@link org.apache.log4j.examples.Sort}
    class documentation.

    <pre>

    # Set options for appender named "A1". 
    # Appender "A1" will be a SyslogAppender
    log4j.appender.A1=org.apache.log4j.net.SyslogAppender

    # The syslog daemon resides on www.abc.net
    log4j.appender.A1.SyslogHost=www.abc.net

    # A1's layout is a PatternLayout, using the conversion pattern 
    # <b>%r %-5p %c{2} %M.%L %x - %m\n</b>. Thus, the log output will
    # include # the relative time since the start of the application in
    # milliseconds, followed by the priority of the log request,
    # followed by the two rightmost components of the category name,
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

    # Root category set to DEBUG using the A2 appender defined above.
    log4j.rootCategory=DEBUG, A2

    # Category definions:
    # The SECURITY category inherits is priority from root. However, it's output
    # will go to A1 appender defined above. It's additivity is non-cumulative.
    log4j.category.SECURITY=INHERIT, A1
    log4j.additivity.SECURITY=false

    # Only warnings or above will be logged for the category "SECURITY.access".
    # Output will go to A1.
    log4j.category.SECURITY.access=WARN

    
    # The category "class.of.the.day" inherits its priority from the
    # category hierrarchy.  Output will go to the appender's of the root
    # category, A2 in this case.
    log4j.category.class.of.the.day=INHERIT
    </pre>

    <p>Refer to the <b>setOption</b> method in each Appender and
    Layout for class specific options.
    
    <p>Use the <code>#</code> or <code>!</code> characters at the
    beginning of a line for comments.
   
   <p> 
   @param configFileName The name of the configuration file where the
   configuration information is stored.

  */
  public
  void doConfigure(String configFileName, Hierarchy hierarchy) {
    Properties props = new Properties();
    try {
      FileInputStream istream = new FileInputStream(configFileName);
      props.load(istream);
      istream.close();
    }
    catch (IOException e) {
      LogLog.error("Could not read configuration file [" + configFileName+
			 "].", e);
      LogLog.error("Ignoring configuration file [" + configFileName+"].");
      return;
    }
    // If we reach here, then the config file is alright.
    doConfigure(props, hierarchy);
  }

  /**
   */
  static
  public 
  void configure(String configFilename) {
    new PropertyConfigurator().doConfigure(configFilename, 
					   Category.defaultHierarchy);
  }

  /**
     Read configuration options from url <code>configURL</code>.
     
     @since 0.8.2
   */
  public
  static
  void configure(java.net.URL configURL) {
    new PropertyConfigurator().doConfigure(configURL, Category.defaultHierarchy);
  }


  /**
     Read configuration options from <code>properties</code>.

     See {@link #doConfigure(String, Hierarchy)} for the expected format.
  */
  static
  public
  void configure(Properties properties) {
    new PropertyConfigurator().doConfigure(properties, Category.defaultHierarchy);
  }

  /**
     Like {@link #configureAndWatch(String, long)} except that the
     default delay as defined by {@link FileWatchdog#DEFAULT_DELAY} is
     used. 
     
     @param configFilename A file in key=value format.

  */
  static
  public
  void configureAndWatch(String configFilename) {
    configureAndWatch(configFilename, FileWatchdog.DEFAULT_DELAY);
  }


  /**
     Read the configuration file <code>configFilename</code> if it
     exists. Moreover, a thread will be created that will periodically
     check if <code>configFilename</code> has been created or
     modified. The period is determined by the <code>delay</code>
     argument. If a change or file creation is detected, then
     <code>configFilename</code> is read to configure log4j.  

      @param configFilename A file in key=value format.
      @param delay The delay in milliseconds to wait between each check.
  */
  static
  public
  void configureAndWatch(String configFilename, long delay) {
    PropertyWatchdog pdog = new PropertyWatchdog(configFilename);
    pdog.setDelay(delay);
    pdog.start();
  }


  /**
     Read configuration options from <code>properties</code>.

     See {@link #doConfigure(String, Hierarchy)} for the expected format.
  */
  public
  void doConfigure(Properties properties, Hierarchy hierarchy) {

    String value = properties.getProperty(LogLog.CONFIG_DEBUG_KEY);
    if(value != null) {
      LogLog.setInternalDebugging(OptionConverter.toBoolean(value, true));
    }

    // Check if the config file overides the shipped code flag.
    String override = properties.getProperty(
                                    BasicConfigurator.DISABLE_OVERRIDE_KEY);
    BasicConfigurator.overrideAsNeeded(override);
    
    configureRootCategory(properties, hierarchy);
    configureCategoryFactory(properties);
    parseCatsAndRenderers(properties, hierarchy);

    LogLog.debug("Finished configuring.");    
    // We don't want to hold references to appenders preventing their
    // garbage collection.
    registry.clear();
  }

  /**
     Read configuration options from url <code>configURL</code>.
   */
  public
  void doConfigure(java.net.URL configURL, Hierarchy hierarchy) {
    Properties props = new Properties();
    LogLog.debug("Reading configuration from URL " + configURL);
    try {
      props.load(configURL.openStream());
    }
    catch (java.io.IOException e) {
      LogLog.error("Could not read configuration file from URL [" + configURL 
		   + "].", e);
      LogLog.error("Ignoring configuration file [" + configURL +"].");
      return;
    }
    doConfigure(props, hierarchy);
  }


  // -------------------------------------------------------------------------------
  // Internal stuff
  // -------------------------------------------------------------------------------

  void configureCategoryFactory(Properties props) {
    String factoryClassName = OptionConverter.findAndSubst(CATEGORY_FACTORY_KEY,
							   props);    
    if(factoryClassName != null) {
      LogLog.debug("Setting category factory to ["+factoryClassName+"].");
      categoryFactory = (CategoryFactory) 
                  OptionConverter.instantiateByClassName(factoryClassName,
							 CategoryFactory.class, 
							 categoryFactory);
    }
  }

  void configureOptionHandler(OptionHandler oh, String prefix,
			      Properties props) {
    String[] options = oh.getOptionStrings();
    if(options == null) 
      return;

    String value;
    for(int i = 0; i < options.length; i++) {
      value =  OptionConverter.findAndSubst(prefix + options[i], props);
      LogLog.debug(
         "Option " + options[i] + "=[" + (value == null? "null" : value)+"].");
      // Some option handlers assume that null value are not passed to them.
      // So don't remove this check
      if(value != null) {
	oh.setOption(options[i], value);
      }
    }
    oh.activateOptions();
  }
  
    
  void configureRootCategory(Properties props, Hierarchy hierarchy) {
    String value = OptionConverter.findAndSubst(ROOT_CATEGORY_PREFIX, props);
    if(value == null) 
      LogLog.debug("Could not find root category information. Is this OK?");
    else {
      Category root = hierarchy.getRoot();
      synchronized(root) {
	parseCategory(props, root, ROOT_CATEGORY_PREFIX, INTERNAL_ROOT_NAME, 
		      value);
      }
    }        
  }


  /**
     Parse non-root elements, such non-root categories and renderers.
  */
  protected
  void parseCatsAndRenderers(Properties props, Hierarchy hierarchy) {
    Enumeration enum = props.propertyNames();
    while(enum.hasMoreElements()) {      
      String key = (String) enum.nextElement();
      if(key.startsWith(CATEGORY_PREFIX)) {
	String categoryName = key.substring(CATEGORY_PREFIX.length());	
	String value =  OptionConverter.findAndSubst(key, props);
	Category cat = hierarchy.getInstance(categoryName, categoryFactory);
	synchronized(cat) {
	  parseCategory(props, cat, key, categoryName, value);
	  parseAdditivityForCategory(props, cat, categoryName);
	}
      } else if(key.startsWith(RENDERER_PREFIX)) {
	String renderedClass = key.substring(RENDERER_PREFIX.length());	
	String renderingClass = OptionConverter.findAndSubst(key, props);
	addRenderer(renderedClass, renderingClass);
      }      
    }
  }  

  /**
     Parse the additivity option for a non-root category.
   */
  void parseAdditivityForCategory(Properties props, Category cat,
				  String categoryName) {
    String value = OptionConverter.findAndSubst(ADDITIVITY_PREFIX + categoryName, 
					     props);
    LogLog.debug("Handling "+ADDITIVITY_PREFIX + categoryName+"=["+value+"]");
    // touch additivity only if necessary	
    if((value != null) && (!value.equals(""))) {
      boolean additivity = OptionConverter.toBoolean(value, true);
      LogLog.debug("Setting additivity for \""+categoryName+"\" to "+
		   additivity); 
      cat.setAdditivity(additivity);
    }
    
  }
  
  /**
     This method must work for the root category as well.
   */
  void parseCategory(Properties props, Category cat, String optionKey,
		     String catName, String value) {

    LogLog.debug("Parsing for [" +catName +"] with value=[" + value+"].");
    // We must skip over ',' but not white space
    StringTokenizer st = new StringTokenizer(value, ",");
 
    
    // If value is not in the form ", appender.." or "", then we should set
    // the priority of the category.
    
    if(!(value.startsWith(",") || value.equals(""))) {

      // just to be on the safe side...
      if(!st.hasMoreTokens())
	return;
    
      String priorityStr = st.nextToken();
      LogLog.debug("Priority token is [" + priorityStr + "].");

      // If the priority value is inherited, set category priority value to
      // null. We also check that the user has not specified inherited for the
      // root category.
      if(priorityStr.equalsIgnoreCase(BasicConfigurator.INHERITED) &&
	                              !catName.equals(INTERNAL_ROOT_NAME)) 
	cat.setPriority(null);
      else 
	cat.setPriority(Priority.toPriority(priorityStr));
      LogLog.debug("Category " + catName + " set to " + cat.getPriority());
    }

    // Remove all existing appenders. They will be reconstructed below.
    cat.removeAllAppenders();
    
    Appender appender;    
    String appenderName;
    while(st.hasMoreTokens()) {
      appenderName = st.nextToken().trim();
      if(appenderName == null || appenderName.equals(","))
	continue;
      LogLog.debug("Parsing appender named \"" + appenderName +"\".");
      appender = parseAppender(props, appenderName);
      if(appender != null) {
	cat.addAppender(appender);
      }      
    }          
  }



  Appender parseAppender(Properties props, String appenderName) {
    Appender appender = registryGet(appenderName);
    if((appender != null)) {
      LogLog.debug("Appender \"" + appenderName + "\" was already parsed.");
      return appender;
    }
    // Appender was not previously initialized.
    String prefix = APPENDER_PREFIX + appenderName;
    String layoutPrefix = prefix + ".layout";    

    appender = (Appender) OptionConverter.instantiateByKey(props, prefix,
					      org.apache.log4j.Appender.class,
					      null);
    if(appender == null) {
      LogLog.error(
              "Could not instantiate appender named \"" + appenderName+"\".");
      return null;
    }
    appender.setName(appenderName);

    if(appender instanceof OptionHandler) {
      configureOptionHandler((OptionHandler) appender, prefix + ".", props);
      LogLog.debug("Parsed \"" + appenderName +"\" options.");
      if(appender.requiresLayout()) {
	Layout layout = (Layout) OptionConverter.instantiateByKey(props, 
								  layoutPrefix,
								  Layout.class, 
								  null);
	if(layout != null) {
	  appender.setLayout(layout);
	  LogLog.debug("Parsing layout options for \"" + appenderName +"\".");
	  configureOptionHandler(layout, layoutPrefix + ".", props);
	  LogLog.debug("End of parsing for \"" + appenderName +"\".");      
	}
      }
    }
    registryPut(appender);
    return appender;
  }

  
  void  registryPut(Appender appender) {
    registry.put(appender.getName(), appender);
  }
  
  Appender registryGet(String name) {
    return (Appender) registry.get(name);
  }
}

class PropertyWatchdog extends FileWatchdog {

  PropertyWatchdog(String filename) {
    super(filename);
  }

  /**
     Call {@link PropertyConfigurator#configure(String)} with the
     <code>filename</code> to reconfigure log4j. */
  public
  void doOnChange() {
    new PropertyConfigurator().doConfigure(filename, Category.defaultHierarchy);
  }
}
