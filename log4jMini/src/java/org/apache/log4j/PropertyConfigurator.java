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
import org.apache.log4j.spi.OptionHandler;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;

import java.util.NoSuchElementException;
import java.util.Enumeration;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Hashtable;

/**
   Allows the log4j configuration from an external file.  See
   <b>{@link #doConfigure(String, Hierarchy)}</b> for the expected
   format.

   <p>It is sometimes useful to see how log4j is reading configuration
   files. You can enable log4j internal logging by defining the
   <b>log4j.debug</b> variable. 

   <P>At the initialization of the Category class, the file
   <b>log4j.properties</b> will be searched from the search path used
   to load classes. If the file can be found, then it will be fed to
   the {@link PropertyConfigurator#configure(java.net.URL)} method.


   @author Ceki G&uuml;lc&uuml;
   @since log4jME 1.0
*/
public class PropertyConfigurator {

  /**
     Used internally to keep track of configured appenders.
   */
  protected Hashtable registry = new Hashtable(11);
  
  static final String CATEGORY_PREFIX = "log4j.category.";
  static final String ADDITIVITY_PREFIX = "log4j.additivity.";
  static final String ROOT_CATEGORY_PREFIX = "log4j.rootCategory";
  static final String APPENDER_PREFIX = "log4j.appender.";  
  static final String CATEGORY_FACTORY_KEY = "log4j.categoryFactory";

  static final private String INHERITED = "inherited";
  static final private String INTERNAL_ROOT_NAME = "root";
  
  /**
    Read configuration from a file. The existing configuration is not
    cleared nor reset. 

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

    <h3>Example</h3>

    <p>An example configuration is given below. Other configuration
    file examples are given in {@link org.apache.log4j.examples.Sort}
    class documentation.

    <pre>

    # Set options for appender named "A1". 
    # Appender "A1" will be a FileAppender
    log4j.appender.A1=org.apache.log4j.FileAppender

    # It will send its output to System.out
    log4j.appender.A1.File=System.out

    # A1's layout is a PatternLayout, using the conversion pattern 
    # <b>%-4r %-5p %c{2} - %m%n</b>. Thus, the log output will
    # include the relative time since the start of the application in
    # milliseconds, followed by the priority of the log request,
    # followed by the two rightmost components of the category name
    # and finally the message itself.
    # Refer to the documentation of {@link PatternLayout} for further information
    # on the syntax of the ConversionPattern key.    
    log4j.appender.A1.layout=org.apache.log4j.PatternLayout
    log4j.appender.A1.layout.ConversionPattern=%-4r %-5p %c{2} - %m%n

    # Set options for appender named "A2"
    # A2 should be a <code>FileAppender</code> printing to the
    # file <code>temp</code>.
    log4j.appender.A2=org.apache.log4j.FileAppender
    log4j.appender.A2.File=temp

    log4j.appender.A2.layout=org.apache.log4j.PatternLayout
    log4j.appender.A2.layout.ConversionPattern=%-4r %-5p %c - %m%n

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
      LogLog.error("Could not read config file [" + configFileName+
			 "].", e);
      LogLog.error("Ignoring config file [" + configFileName+"].");
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
     Read configuration options from <code>properties</code>.

     See {@link #doConfigure(String, Hierarchy)} for the expected format.
  */
  public
  void doConfigure(Properties properties, Hierarchy hierarchy) {

    String value = properties.getProperty(LogLog.DEBUG_KEY);
    if(value != null) {
      LogLog.setInternalDebugging(OptionConverter.toBoolean(value, true));
    }
    
    configureRootCategory(properties, hierarchy);
    parseCats(properties, hierarchy);

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
    LogLog.debug("Reading URL " + configURL);
    try {
      props.load(configURL.openStream());
    }
    catch (java.io.IOException e) {
      LogLog.error("Could not read config file [" + configURL 
		   + "].", e);
      LogLog.error("Ignoring config file [" + configURL +"].");
      return;
    }
    doConfigure(props, hierarchy);
  }


  // -------------------------------------------------------------------------------
  // Internal stuff
  // -------------------------------------------------------------------------------


  void configureOptionHandler(OptionHandler oh, String prefix,
			      Properties props) {
    String[] options = oh.getOptionStrings();
    if(options == null) 
      return;

    String value;
    for(int i = 0; i < options.length; i++) {
      value =  props.getProperty(prefix + options[i]);
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
    String value = props.getProperty(ROOT_CATEGORY_PREFIX);
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
  void parseCats(Properties props, Hierarchy hierarchy) {
    Enumeration enum = props.propertyNames();
    while(enum.hasMoreElements()) {      
      String key = (String) enum.nextElement();
      if(key.startsWith(CATEGORY_PREFIX)) {
	String categoryName = key.substring(CATEGORY_PREFIX.length());	
	String value =  props.getProperty(key);
	Category cat = hierarchy.getInstance(categoryName);
	synchronized(cat) {
	  parseCategory(props, cat, key, categoryName, value);
	  parseAdditivityForCategory(props, cat, categoryName);
	}
      }      
    }
  }  

  /**
     Parse the additivity option for a non-root category.
   */
  void parseAdditivityForCategory(Properties props, Category cat,
				  String categoryName) {
    String value = props.getProperty(ADDITIVITY_PREFIX + categoryName);
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
      if(priorityStr.equalsIgnoreCase(INHERITED) && !catName.equals(INTERNAL_ROOT_NAME)) 
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

