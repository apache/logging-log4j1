/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.APL file.  */

// Contibutors: Alex Blewitt <Alex.Blewitt@ioshq.com>
//              Markus Oestreicher <oes@zurich.ibm.com>
//              Frank Hoering <fhr@zurich.ibm.com>
//              Nelson Minar <nelson@media.mit.edu>
//              Jim Cakalic <jim_cakalic@na.biomerieux.com>
//              Avy Sharell <asharell@club-internet.fr>
//              Ciaran Treanor <ciaran@xelector.com>
//              Jeff Turner <jeff@socialchange.net.au>
//              Horwitz, Michael <MHorwitz@siemens.co.za>

package org.apache.log4j;

import org.apache.log4j.Priority;
import org.apache.log4j.spi.RootCategory;
import org.apache.log4j.spi.AppenderAttachable;
import org.apache.log4j.spi.CategoryFactory;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.NullEnumeration;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.helpers.AppenderAttachableImpl;
import org.apache.log4j.or.RendererMap;
import org.apache.log4j.or.ObjectRenderer;

import java.util.Enumeration;
import java.util.Vector;
import java.util.MissingResourceException;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.net.URL;
import java.net.MalformedURLException;


/**
  This is the central class in the log4j package. One of the distintive
  features of log4j are hierarchal categories and their evaluation.

  <p>See the <a href="../../../manual.html">user manual</a> for an
  introduction on this class.

  @author Ceki G&uuml;lc&uuml;
  @author Anders Kristensen 
  */
public class Category implements AppenderAttachable {

  // DISABLE_OFF should be set to a value lower than all possible
  // priorities.
  static final int DISABLE_OFF = -1;
  static final int DISABLE_OVERRIDE = -21;
  
  private static String DEFAULT_FQN = "org.apache.log4j.Category";

  protected static String instanceFQN;

  // Don't disable any priority level by default.
  protected static int disable = Category.DISABLE_OFF;

  static boolean emittedNoAppenderWarning = false;
  static boolean emittedNoResourceBundleWarning = false;  

  // default is a language reserved term, we exceptionally prefix with
  // an undescore.
  static 
  public 
  final Hierarchy defaultHierarchy = new Hierarchy(new 
						   RootCategory(Priority.DEBUG));

  
  protected ResourceBundle resourceBundle;
  
  // Categories need to know what Hierarchy they are in
  protected Hierarchy myContext;

  /**
     This string constant is set to <b>log4j.properties</b> the name
     of the file that will be searched by default in classpath. If the
     file can be found, then it is fed to the {@link
     PropertyConfigurator}.

     See also {@link #DEFAULT_CONFIGURATION_KEY} for a more general
     alternative.

     @since 0.8.5 */
     static public final String DEFAULT_CONFIGURATION_FILE = "log4j.properties";
     
  /**
     This String constant is set to <b>log4j.configuration</b>. 

     <p>It corresponds to name of the system property which, if set,
     specifies the name of the resource containing the properties file
     or {@link URL} with which log4j should configure itself. See
     {@link OptionConverter#selectAndConfigure} for more detailed
     information on the processing of this options.

     <p>Setting the <b>log4j.configuration</b> system property
     overrides the default search for the file
     <b>log4j.properties</b>.

     <p>Note that all property keys are case sensitive.  

     @since 1.0 */
     static final public String DEFAULT_CONFIGURATION_KEY="log4j.configuration";

  /**
      Setting the system property <b>log4j.defaultInitOverride</b> to
      "true" or any other value than "false" will skip default
      configuration process.

     <p>The current value of the DEFAULT_INIT_OVERRIDE_KEY string
     constant is <b>log4j.defaultInitOverride</b>.

     <p>Note that all property keys are case sensitive.  

     @since 0.8.5 */
  public static final String DEFAULT_INIT_OVERRIDE_KEY = 
                                                 "log4j.defaultInitOverride";

  /** Search for the properties file log4j.properties in the CLASSPATH.  */
  static {
    String override = null;
    try {
      override=System.getProperty(DEFAULT_INIT_OVERRIDE_KEY, override);
    } catch(SecurityException e) {
      LogLog.debug("Could not read system property \""+
		   	   DEFAULT_INIT_OVERRIDE_KEY +"\".", e);
    }
    // if there is no default init override, them get the resource
    // specified by the user or the default config file.
    if(override == null || "false".equalsIgnoreCase(override)) {
      String resource = System.getProperty(DEFAULT_CONFIGURATION_KEY, 
					   DEFAULT_CONFIGURATION_FILE);
      URL url = null;
      try {
	url = new URL(resource);
      } catch (MalformedURLException ex) {
	// so, resource is not a URL:
	// attempt to get the resource in the most generic way:
	url = Category.class.getResource(resource);
	if(url == null) {
	  // if that doen't work, then try again in a slightly
	  // different way
	  ClassLoader loader = Category.class.getClassLoader();
	  if(loader != null) {
	    url = loader.getResource(resource);	  
	  }
	}	
      }	
      
      // If we have a non-null url, then delegate the rest of the
      // configuration to the OptionConverter.selectAndConfigure
      // method.
      if(url != null) {
	OptionConverter.selectAndConfigure(url, defaultHierarchy);
      } else {
	LogLog.debug("Could not find resource: ["+resource+"].");
      }
    }  
  } 

  /**
     The name of this category.
  */
  protected String   name;  

  /**
     The assigned priority of this category.  The
     <code>priority</code> variable need not be assined a value in
     which case it is inherited form the hierarchy.  */
  protected Priority priority;

  /**
     The parent of this category. All categories have at least one
     ancestor which is the root category. */
  protected Category parent;

  AppenderAttachableImpl aai;

  /** Additivity is set to true by default, that is children inherit
      the appenders of their ancestors by default. If this variable is
      set to <code>false</code> then the appenders found in the
      ancestors of this category are not used. However, the children
      of this category will inherit its appenders, unless the children
      have their additivity flag set to <code>false</code> too. See
      the user manual for more details. */
  protected boolean additive = true;
  
  /**
     This constructor created a new <code>Category</code> instance and
     sets its name.

     <p>It is intended to be used by sub-classes only. You should not
     create categories directly.

     @param name The name of the category.  */
  protected 
  Category(String name) {
    this.name = name;
    this.instanceFQN = DEFAULT_FQN;
  }

  /**
     Add <code>newAppender</code> to the list of appenders of this
     Category instance.

     <p>If <code>newAppender</code> is already in the list of
     appenders, then it won't be added again.
  */
  synchronized  
  public 
  void addAppender(Appender newAppender) {
    if(aai == null) {
      aai = new AppenderAttachableImpl();
    }
    aai.addAppender(newAppender);
  }

  /**
     If <code>assertion</code> parameter is <code>false</code>, then
     logs <code>msg</code> as an {@link #error(Object) error} statement.

     @param assertion 
     @param msg The message to print if <code>assertion</code> is
     false.

     @since 0.8.1 */
  public
  void assert(boolean assertion, String msg) {
    if(!assertion)
      this.error(msg);
  }
  

  /**
     Call the appenders in the hierrachy starting at
     <code>this</code>.  If no appenders could be found, emit a
     warning.

     <p>This method always calls all the appenders inherited form the
     hierarchy circumventing any evaluation of whether to log or not
     to log the particular log request.
     
     @param LoggingEvent the event to log.  */
  public
  void callAppenders(LoggingEvent event) {
    int writes = 0;

    for(Category c = this; c != null; c=c.parent) {
      // Protected against simultaneous call to addAppender, removeAppender,...
      synchronized(c) {
	if(c.aai != null) {
	  writes += c.aai.appendLoopOnAppenders(event);
	}
	if(!c.additive) {
	  break;
	}
      }
    }
    // No appenders in hierarchy, warn user only once.
    if(!Category.emittedNoAppenderWarning && writes == 0) {
      LogLog.error("No appenders could be found for category (" +
		    this.getName() + ").");
      LogLog.error("Please initialize the log4j system properly.");
      emittedNoAppenderWarning = true;
    }
  }

  /**
     Close all attached appenders implementing the AppenderAttachable
     interface.  
     @since 1.0
  */
  synchronized
  void closeNestedAppenders() {
    Enumeration enum = this.getAllAppenders();
    if(enum != null) {
      while(enum.hasMoreElements()) {
	Appender a = (Appender) enum.nextElement();
	if(a instanceof AppenderAttachable) {
	  a.close();
	}
      }
    }
  }

  /** 
    Log a message object with the {@link Priority#DEBUG DEBUG} priority.

    <p>This method first checks if this category is <code>DEBUG</code>
    enabled by comparing the priority of this category with the {@link
    Priority#DEBUG DEBUG} priority. If this category is
    <code>DEBUG</code> enabled, then it converts the message object
    (passed as parameter) to a string by invoking the appropriate
    {@link ObjectRenderer}. It then proceeds to call all the
    registered appenders in this category and also higher in the
    hierarchy depending on the value of the additivity flag.

    <p><b>WARNING</b> Note that passing a {@link Throwable} to this
    method will print the name of the <code>Throwable</code> but no
    stack trace. To print a stack trace use the {@link #debug(Object,
    Throwable)} form instead.
    
    @param message the message object to log. */
  public
  void debug(Object message) {
    if(disable >=  Priority.DEBUG_INT) 
      return;    
    if(Priority.DEBUG.isGreaterOrEqual(this.getChainedPriority())) {
      forcedLog(instanceFQN, Priority.DEBUG, message, null);
    }
  }
  
  /** 
   Log a message object with the <code>DEBUG</code> priority including
   the stack trace of the {@link Throwable} <code>t</code> passed as
   parameter.
   
   <p>See {@link #debug(Object)} form for more detailed information.
   
   @param message the message object to log.
   @param t the exception to log, including its stack trace.  */    
  public
  void debug(Object message, Throwable t) {
    if(disable >=  Priority.DEBUG_INT) 
      return;   
    if(Priority.DEBUG.isGreaterOrEqual(this.getChainedPriority()))
      forcedLog(instanceFQN, Priority.DEBUG, message, t);
  }

  //public
  //void dump() {
  //  System.out.println("Category " + name + " dump -----");
  //  for(Category c = this; c != null; c=c.parent)
  //	System.out.println("("+c.name+", "+c.priority+") ->");
  //  System.out.println("---------------------------");
  //
  //}
  
  /** 
    Log a message object with the {@link Priority#ERROR ERROR} priority.

    <p>This method first checks if this category is <code>ERROR</code>
    enabled by comparing the priority of this category with {@link
    Priority#ERROR ERROR} priority. If this category is
    <code>ERROR</code> enabled, then it converts the message object
    passed as parameter to a string by invoking the appropriate {@link
    ObjectRenderer}. It proceeds to call all the registered appenders
    in this category and also higher in the hierarchy depending on
    the value of the additivity flag.

    <p><b>WARNING</b> Note that passing a {@link Throwable} to this
    method will print the name of the <code>Throwable</code> but no
    stack trace. To print a stack trace use the {@link #error(Object,
    Throwable)} form instead.
    
    @param message the message object to log */
  public
  void error(Object message) {
    if(disable >=  Priority.ERROR_INT) return;
    if(this.isEnabledFor(Priority.ERROR))
      forcedLog(instanceFQN, Priority.ERROR, message, null);
  }

  /** 
   Log a message object with the <code>ERROR</code> priority including
   the stack trace of the {@link Throwable} <code>t</code> passed as
   parameter.
   
   <p>See {@link #error(Object)} form for more detailed information.
   
   @param message the message object to log.
   @param t the exception to log, including its stack trace.  */  
  public
  void error(Object message, Throwable t) {
    if(disable >=  Priority.ERROR_INT) return;
    if(this.isEnabledFor(Priority.ERROR))
      forcedLog(instanceFQN, Priority.ERROR, message, t);
    
  }


  /**
     If the named category exists (in the default hierarchy) then it
     returns a reference to the category, otherwise it returns
     <code>null</code>.
     
     <p>Contributed by Ciaran Treanor -  ciaran@xelector.com
     @version version 0.8.5 */
  public
  static
  Category exists(String name) {    
    return defaultHierarchy.exists(name);
  }

  /** 
    Log a message object with the {@link Priority#FATAL FATAL} priority.

    <p>This method first checks if this category is <code>FATAL</code>
    enabled by comparing the priority of this category with {@link
    Priority#FATAL FATAL} priority. If the category is <code>FATAL</code>
    enabled, then it converts the message object passed as parameter
    to a string by invoking the appropriate {@link ObjectRenderer}. It
    proceeds to call all the registered appenders in this category and
    also higher in the hierarchy depending on the value of the
    additivity flag.

    <p><b>WARNING</b> Note that passing a {@link Throwable} to this
    method will print the name of the Throwable but no stack trace. To
    print a stack trace use the {@link #fatal(Object, Throwable)} form
    instead. 
    
    @param message the message object to log */
  public
  void fatal(Object message) {
    if(disable >=  Priority.FATAL_INT) return;    
    if(Priority.FATAL.isGreaterOrEqual(this.getChainedPriority()))
      forcedLog(instanceFQN, Priority.FATAL, message, null);
  }
  
  /** 
   Log a message object with the <code>FATAL</code> priority including
   the stack trace of the {@link Throwable} <code>t</code> passed as
   parameter.
   
   <p>See {@link #fatal(Object)} for more detailed information.
   
   @param message the message object to log.
   @param t the exception to log, including its stack trace.  */
  public
  void fatal(Object message, Throwable t) {
    if(disable >=  Priority.FATAL_INT) return;   
    if(Priority.FATAL.isGreaterOrEqual(this.getChainedPriority()))
      forcedLog(instanceFQN, Priority.FATAL, message, t);
  }


  /**
     This method renders the <code>message</code> passed as a
     oaraneter and logs immediately without further checks.  */
  protected
  void forcedLog(String fqn, Priority priority, Object message, Throwable t) {
    String s;
    if(message instanceof String) {
      s = (String) message;
    } else {
      s = myContext.rendererMap.findAndRender(message);
    }
    callAppenders(new LoggingEvent(fqn, this, priority, s, t));
  }


  /**
     Get the additivity flag for this Category instance.  
  */
  public
  boolean getAdditivity() {
    return additive;
  }

  /**
     Get the appenders contained in this category as an
     Enumeration. If there are no appenders in this category
     <code>null</code> is returned.
     
     @return Enumeration An enumeration of the appenders in this category.  
  */
  public
  Enumeration getAllAppenders() {
    if(aai == null)
      return NullEnumeration.getInstance();
    else 
      return aai.getAllAppenders();
  }

  /**
     Look for the appender named as <code>name</code>.

     <p>Return the appender with that name if in the list. Return null
     otherwise.  
  */
  public
  Appender getAppender(String name) {
     if(aai == null || name == null)
      return null;

     return aai.getAppender(name);
  }
  
  /**
     Starting from this category, search the category hierarchy for a
     non-null priority and return it. Otherwise, return the priority of the
     root category.
     
     <p>The Category class is designed so that this method executes as
     quickly as possible.
   */
  public 
  Priority getChainedPriority() {
    for(Category c = this; c != null; c=c.parent) {
      if(c.priority != null) 
	return c.priority;
    }
    return null; // If reached will cause an NullPointerException.
  }

  /**
     Returns all the currently defined categories in the default
     hierarchy as an {@link java.util.Enumeration Enumeration}.

     <p>The root category is <em>not</em> included in the returned
     {@link Enumeration}.     
  */
  public
  static
  Enumeration getCurrentCategories() {
    // The accumlation in v is necessary because not all elements in
    // HierarchyMaintainer.ht are Category objects as there might be some
    // ProvisionNodes as well.       
    Vector v = new Vector(defaultHierarchy.ht.size());
    
    Enumeration elems = defaultHierarchy.ht.elements();
    while(elems.hasMoreElements()) {
      Object o = elems.nextElement();
      if(o instanceof Category) {
	v.addElement(o);
      }
    }
    return v.elements();
  }


  /**
     Return the default Hierarchy instance.

     @since 1.0
   */
  public 
  static 
  Hierarchy getDefaultHierarchy() {
    return defaultHierarchy;
  }

  
 /**
     Retrieve a category with named as the <code>name</code>
     parameter. If the named category already exists, then the
     existing instance will be reutrned. Otherwise, a new instance is
     created. 

     By default, categories do not have a set priority but inherit
     it from the hierarchy. This is one of the central features of
     log4j.

     @param name The name of the category to retrieve.  */
  public
  static
  Category getInstance(String name) {
    return defaultHierarchy.getInstance(name);
  }	

 /**
    Shorthand for <code>getInstance(clazz.getName())</code>.

    @param clazz The name of <code>clazz</code> will be used as the
    name of the category to retrieve.  See {@link
    #getInstance(String)} for more detailed information.

    @since 1.0 */
  public
  static
  Category getInstance(Class clazz) {
    return getInstance(clazz.getName());
  }	


  /**
     Like {@link #getInstance(String)} except that the type of category
     instantiated depends on the type returned by the {@link
     CategoryFactory#makeNewCategoryInstance} method of the
     <code>factory</code> parameter.
     
     <p>This method is intended to be used by sub-classes.
     
     @param name The name of the category to retrieve.

     @param factory A {@link CategoryFactory} implementation that will
     actually create a new Instance.

     @since 0.8.5 */
  public
  static
  Category getInstance(String name, CategoryFactory factory) {
    return defaultHierarchy.getInstance(name, factory);
  }	

  
  /**
     Return the category name.  */
  public
  final
  String getName() {
    return name;
  }
    
  /**
     Returns the assigned {@link Priority}, if any, for this Category.  
     
     @return Priority - the assigned Priority, can be <code>null</code>.
  */
  final
  public
  Priority getPriority() {
    return this.priority;
  }

  /**
     Return the root of the default category hierrachy.

     <p>The root category is always instantiated and available. It's
     name is "root".

     <p>Nevertheless, calling {@link #getInstance
     Category.getInstance("root")} does not retrieve the root category 
     but a category just under root named "root".
     
   */
  final
  public
  static
  Category getRoot() {
    return defaultHierarchy.getRoot();
  }

  /**
     Return the {@link ResourceBundle} for this category.

     <p>This method walks the hierarchy to find the appropriate
     resource bundle.

     <p>In case there is no bundle in the hierarchy then
     <code>null</code> is returned.

     @since 0.9.0 */
  public
  ResourceBundle getResourceBundle() {
    for(Category c = this; c != null; c=c.parent) {
      if(c.resourceBundle != null) 
	return c.resourceBundle;
    }
    // It might be the case that there is no resource bundle 
    return null;
  }

  protected
  String getResourceBundleString(String key) {
    ResourceBundle rb = getResourceBundle();
    // This is one of the rare cases where we can use logging in order
    // to report errors from within log4j.
    if(rb == null) {
      if(!emittedNoResourceBundleWarning) {
	error("No resource bundle has been set for category "+name);
	emittedNoResourceBundleWarning = true;
      }
      return null;
    }
    else {
      try {
	return rb.getString(key);
      }
      catch(MissingResourceException mre) {
	error("No resource is associated with key \""+key+"\".");
	return null;
      }
    }
  }
  
  /** 
    Log a message object with the {@link Priority#INFO INFO} priority.

    <p>This method first checks if this category is <code>INFO</code>
    enabled by comparing the priority of this category with {@link
    Priority#INFO INFO} priority. If the category is <code>INFO</code>
    enabled, then it converts the message object passed as parameter
    to a string by invoking the appropriate {@link ObjectRenderer}. It
    proceeds to call all the registered appenders in this category and
    also higher in the hierarchy depending on the value of the
    additivity flag.

    <p><b>WARNING</b> Note that passing a {@link Throwable} to this
    method will print the name of the Throwable but no stack trace. To
    print a stack trace use the {@link #info(Object, Throwable)} form
    instead. 
    
    @param message the message object to log */
  public
  void info(Object message) {
    if(disable >=  Priority.INFO_INT) return;    
    if(Priority.INFO.isGreaterOrEqual(this.getChainedPriority()))
      forcedLog(instanceFQN, Priority.INFO, message, null);
  }
  
  /** 
   Log a message object with the <code>INFO</code> priority including
   the stack trace of the {@link Throwable} <code>t</code> passed as
   parameter.
   
   <p>See {@link #info(Object)} for more detailed information.
   
   @param message the message object to log.
   @param t the exception to log, including its stack trace.  */
  public
  void info(Object message, Throwable t) {
    if(disable >=  Priority.INFO_INT) return;   
    if(Priority.INFO.isGreaterOrEqual(this.getChainedPriority()))
      forcedLog(instanceFQN, Priority.INFO, message, t);
  }

  /**
     Check whether this category is enabled for the <code>DEBUG</code>
     priority.
     
     <p> This function is intended to lessen the computational cost of
     disabled log debug statements.

     <p> For some <code>cat</code> Category object, when you write,
     <pre>
       cat.debug("This is entry number: " + i );
     </pre>

     <p>You incur the cost constructing the message, concatenatiion in
     this case, regardless of whether the message is logged or not.

     <p>If you are worried about speed, then you should write
     <pre>
       if(cat.isDebugEnabled()) {
         cat.debug("This is entry number: " + i );
       }
     </pre>

     <p>This way you will not incur the cost of parameter construction
     if debugging is disabled for <code>cat</code>. On the other hand,
     if the <code>cat</code> is debug enabled, you will incur the cost
     of evaluating whether the category is debug enabled twice. Once
     in <code>isDebugEnabled</code> and once in the
     <code>debug</code>.  This is an insignificant overhead since
     evaluating a category takes about 1% of the time it takes to
     actually log.

     @return boolean - <code>true</code> if this category is debug
     enabled, <code>false</code> otherwise.
     
   */
  public
  boolean isDebugEnabled() {
    if(disable >=  Priority.DEBUG_INT)
      return false;   
    return Priority.DEBUG.isGreaterOrEqual(this.getChainedPriority());
  }
  
  /**
     Check whether this category is enabled for a given {@link
     Priority} passed as parameter.

     See also {@link #isDebugEnabled}.
       
     @return boolean True if this category is enabled for <code>priority</code>.
  */
  public
  boolean isEnabledFor(Priority priority) {
    if(disable >= priority.level) {
      return false;
    }
    return priority.isGreaterOrEqual(this.getChainedPriority());
  }

  /**
    Check whether this category is enabled for the info priority.
    See also {@link #isDebugEnabled}.

    @return boolean - <code>true</code> if this category is enabled
    for priority info, <code>false</code> otherwise.
  */
  public
  boolean isInfoEnabled() {
    if(disable >= Priority.INFO_INT)
      return false;   
    return Priority.INFO.isGreaterOrEqual(this.getChainedPriority());
  }


  /**
     Log a localized message. The user supplied parameter
     <code>key</code> is replaced by its localized version from the
     resource bundle.
     
     @see #setResourceBundle

     @since 0.8.4 */
  public
  void l7dlog(Priority priority, String key, Throwable t) {
    if(disable >= priority.level) {
      return;
    }
    if(priority.isGreaterOrEqual(this.getChainedPriority())) {
      String msg = getResourceBundleString(key);
      // if message corresponding to 'key' could not be found in the
      // resource bundle, then default to 'key'.
      if(msg == null) {
	msg = key;
      }
      callAppenders(new LoggingEvent(instanceFQN, this, priority, msg, t));
    }
  }
  /**
     Log a localized and parameterized message. First, the user
     supplied <code>key</code> is searched in the resource
     bundle. Next, the resulting pattern is formatted using 
     {@link MessageFormat#format(String,Object[])} method with the user
     supplied object array <code>params</code>.
     
     @since 0.8.4
  */
  public
  void l7dlog(Priority priority, String key,  Object[] params, Throwable t) {
    if(disable >= priority.level) {
      return;
    }    
    if(priority.isGreaterOrEqual(this.getChainedPriority())) {
      String pattern = getResourceBundleString(key);
      String msg;
      if(pattern == null) 
	msg = key;
      else 
	msg = java.text.MessageFormat.format(pattern, params);
      callAppenders(new LoggingEvent(instanceFQN, this, priority, msg, t));
    }
  }
  
  /**
     This generic form is intended to be used by wrappers.
   */
  public
  void log(Priority priority, Object message, Throwable t) {
    if(disable >= priority.level) {
      return;
    }
    if(priority.isGreaterOrEqual(this.getChainedPriority())) 
      forcedLog(instanceFQN, priority, message, t);
  }
  
 /**
    This generic form is intended to be used by wrappers.
 */
  public
  void log(Priority priority, Object message) {
    if(disable >= priority.level) {
      return;
    }
    if(priority.isGreaterOrEqual(this.getChainedPriority()))
      forcedLog(instanceFQN, priority, message, null);
  }

  /**
     
     This is the most generic printing method. It is intended to be
     invoked by wrapper classes.
          
     @param callerFQN The wrapper class' fully qualified class name.
     @param priority The priority of the logging request.
     @param message The message of the logging request.
     @param t The throwable of the logging request, may be null.
  */
  public
  void log(String callerFQN, Priority priority, Object message, Throwable t) {
    if(disable >= priority.level) {
      return;
    }
    if(priority.isGreaterOrEqual(this.getChainedPriority())) {
      forcedLog(callerFQN, priority, message, t);
    }
  }


  /**
     Remove all previously added appenders from this Category
     instance.

     <p>This is useful when re-reading configuration information.
  */
  synchronized
  public
  void removeAllAppenders() {
    if(aai != null) {
      aai.removeAllAppenders();
      aai = null;
    }
  }

  /**
     Remove the appender passed as parameter form the list of appenders.

     @since 0.8.2
  */
  synchronized
  public
  void removeAppender(Appender appender) {
    if(appender == null || aai == null) 
      return;
    aai.removeAppender(appender);
  }

  /**
     Remove the appender with the name passed as parameter form the
     list of appenders.

     @since 0.8.2 */
  synchronized
  public
  void removeAppender(String name) {
    if(name == null || aai == null) return;
    aai.removeAppender(name);
  }
  
  /**
     Set the additivity flag for this Category instance.
     @since 0.8.1
   */
  public
  void setAdditivity(boolean additive) {
    this.additive = additive;
  }

  /**
     Only the Hiearchy class can set the hiearchy of a
     category. Default package access is MANDATORY here.  */
  final
  void setHierarchy(Hierarchy hierarchy) {
    this.myContext = hierarchy;
  }
  
  /**
     Set the priority of this Category.

     <p>Null values are admitted.
  */
  public
  void setPriority(Priority priority) {
    this.priority = priority;
  }


  /**
     Set the resource bundle to be used with localized logging
     methods {@link #l7dlog(Priority,String,Throwable)} and {@link
     #l7dlog(Priority,String,Object[],Throwable)}.

     @since 0.8.4
   */
  public
  void setResourceBundle(ResourceBundle bundle) {
    resourceBundle = bundle;
  }

  /**
     Calling this method will <em>safely</em> close and remove all
     appenders in all the categories including root contained in the
     default hierachy.
     
     <p>Some appenders such as {@link org.apache.log4j.net.SocketAppender}
     and {@link AsyncAppender} need to be closed before the
     application exists. Otherwise, pending logging events might be
     lost.

     <p>The <code>shutdown</code> method is careful to close nested
     appenders before closing regular appenders. This is allows
     configurations where a regular appender is attached to a category
     and again to a nested appender.  

     @since 1.0
  */
  public
  static
  void shutdown() {
    defaultHierarchy.shutdown();
  }

  
  /** 
    Log a message object with the {@link Priority#WARN WARN} priority.

    <p>This method first checks if this category is <code>WARN</code>
    enabled by comparing the priority of this category with {@link
    Priority#WARN WARN} priority. If the category is <code>WARN</code>
    enabled, then it converts the message object passed as parameter
    to a string by invoking the appropriate {@link ObjectRenderer}. It
    proceeds to call all the registered appenders in this category and
    also higher in the hieararchy depending on the value of the
    additivity flag.

    <p><b>WARNING</b> Note that passing a {@link Throwable} to this
    method will print the name of the Throwable but no stack trace. To
    print a stack trace use the {@link #warn(Object, Throwable)} form
    instead.  <p>
    
    @param message the message object to log.  */
  public
  void warn(Object message) {
    if(this.isEnabledFor(Priority.WARN))
      forcedLog(instanceFQN, Priority.WARN, message, null);
  }
  
  /** 
   Log a message with the <code>WARN</code> priority including the
   stack trace of the {@link Throwable} <code>t</code> passed as
   parameter.
   
   <p>See {@link #warn(Object)} for more detailed information.
   
   @param message the message object to log.
   @param t the exception to log, including its stack trace.  */
  public
  void warn(Object message, Throwable t) {
    if(this.isEnabledFor(Priority.WARN))
      forcedLog(instanceFQN, Priority.WARN, message, t);
  }
}
