/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

// Contibutors: Alex Blewitt <Alex.Blewitt@ioshq.com>
//              Markus Oestreicher <oes@zurich.ibm.com>
//              Frank Hoering <fhr@zurich.ibm.com>
//              Nelson Minar <nelson@media.mit.edu>
//              Jim Cakalic <jim_cakalic@na.biomerieux.com>
//              Avy Sharell <asharell@club-internet.fr>
//              Ciaran Treanor <ciaran@xelector.com>
//              Jeff Turner <jeff@socialchange.net.au>
//              Michael Horwitz <MHorwitz@siemens.co.za>
//              Calvin Chan <calvin.chan@hic.gov.au>
//              Aaron Greenhouse <aarong@cs.cmu.edu>
//              Beat Meier <bmeier@infovia.com.ar>

package org.apache.log4j;

import org.apache.log4j.Priority;
import org.apache.log4j.spi.RootCategory;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.helpers.LogLog;

import java.util.Enumeration;
import java.util.Vector;
import java.util.MissingResourceException;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.net.URL;
import java.net.MalformedURLException;


/**
  This is the central class in the log4j package. One of the
  distintive features of log4j are hierarchical categories and their
  evaluation.

  <p>See the <a href="../../../../manual.html">user manual</a> for an
  introduction on this class. 

  @author Ceki G&uuml;lc&uuml;
  @author Anders Kristensen */
public class Category {

  /**
     The hierarchy where categories are attached to by default.
  */
  static 
  public 
  final Hierarchy defaultHierarchy = new Hierarchy(new 
						   RootCategory(Priority.DEBUG));

  /**
     The name of this category.
  */
  protected String   name;  

  /**
     The assigned priority of this category.  The
     <code>priority</code> variable need not be assined a value in
     which case it is inherited form the hierarchy.  */
  volatile protected Priority priority;

  /**
     The parent of this category. All categories have at least one
     ancestor which is the root category. */
  volatile protected Category parent;

  // Categories need to know what Hierarchy they are in
  protected Hierarchy hierarchy;
  
  /** Array of appenders. */
  protected Vector  appenderList;

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

     @param name The name of the category.  
  */
  protected 
  Category(String name) {
    this.name = name;
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
    // Null values for newAppender parameter are strictly forbidden.
    if(newAppender == null)
      return;
    
    if(appenderList == null) {
      appenderList = new Vector(1);
    }
    if(!appenderList.contains(newAppender))
      appenderList.addElement(newAppender);
  }

  public
  int appendLoopOnAppenders(LoggingEvent event) {
    int size = 0;
    Appender appender;

    if(appenderList != null) {
      size = appenderList.size();
      for(int i = 0; i < size; i++) {
	appender = (Appender) appenderList.elementAt(i);
	appender.doAppend(event);
      }
    }    
    return size;
  }

  /**
     Call the appenders in the hierrachy starting at
     <code>this</code>.  If no appenders could be found, emit a
     warning.

     <p>This method calls all the appenders inherited from the
     hierarchy circumventing any evaluation of whether to log or not
     to log the particular log request.
     
     @param LoggingEvent the event to log.  */
  public
  void callAppenders(LoggingEvent event) {
    int writes = 0;

    for(Category c = this; c != null; c=c.parent) {
      // Protected against simultaneous call to addAppender, removeAppender,...
      synchronized(c) {
	if(c.appenderList != null) {
	  writes += c.appendLoopOnAppenders(event);
	}
	if(!c.additive) {
	  break;
	}
      }
    }
    // No appenders in hierarchy, warn user only once.
    if(!hierarchy.emittedNoAppenderWarning && writes == 0) {
      LogLog.error("No appenders could be found for category (" +
		    this.getName() + ").");
      LogLog.error("Please initialize the log4j system properly.");
      hierarchy.emittedNoAppenderWarning = true;
    }
  }

  /** 
    Log a message object with the {@link Priority#DEBUG DEBUG} priority.

    <p>This method first checks if this category is <code>DEBUG</code>
    enabled by comparing the priority of this category with the {@link
    Priority#DEBUG DEBUG} priority. If this category is
    <code>DEBUG</code> enabled, then it invokes all the registered
    appenders in this category and also higher in the hierarchy
    depending on the value of the additivity flag.

    <p><b>WARNING</b> Note that passing a {@link Throwable} to this
    method will print the name of the <code>Throwable</code> but no
    stack trace. To print a stack trace use the {@link #debug(Object,
    Throwable)} form instead.
    
    @param message the message object to log. */
  public
  void debug(Object message) {
    if(hierarchy.enableInt >  Priority.DEBUG_INT) 
      return;    
    if(Priority.DEBUG.isGreaterOrEqual(this.getChainedPriority())) {
      forcedLog(Priority.DEBUG, message, null);
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
    if(hierarchy.enableInt >  Priority.DEBUG_INT) 
      return;
    if(Priority.DEBUG.isGreaterOrEqual(this.getChainedPriority()))
      forcedLog(Priority.DEBUG, message, t);    
  }

  /** 
    Log a message object with the {@link Priority#ERROR ERROR} priority.

    <p>This method first checks if this category is <code>ERROR</code>
    enabled by comparing the priority of this category with {@link
    Priority#ERROR ERROR} priority.  If this category is
    <code>DEBUG</code> enabled, then it invokes all the registered
    appenders in this category and also higher in the hierarchy
    depending on the value of the additivity flag.

    <p><b>WARNING</b> Note that passing a {@link Throwable} to this
    method will print the name of the <code>Throwable</code> but no
    stack trace. To print a stack trace use the {@link #error(Object,
    Throwable)} form instead.
    
    @param message the message object to log */
  public
  void error(Object message) {
    if(hierarchy.enableInt >  Priority.ERROR_INT) 
      return;
    if(Priority.ERROR.isGreaterOrEqual(this.getChainedPriority()))
      forcedLog(Priority.ERROR, message, null);
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
    if(hierarchy.enableInt >  Priority.ERROR_INT) 
      return;
    if(Priority.ERROR.isGreaterOrEqual(this.getChainedPriority()))
      forcedLog(Priority.ERROR, message, t);
    
  }

  /** 
    Log a message object with the {@link Priority#FATAL FATAL} priority.

    <p>This method first checks if this category is <code>FATAL</code>
    enabled by comparing the priority of this category with {@link
    Priority#FATAL FATAL} priority.  If this category is
    <code>DEBUG</code> enabled, then it invokes all the registered
    appenders in this category and also higher in the hierarchy
    depending on the value of the additivity flag.

    <p><b>WARNING</b> Note that passing a {@link Throwable} to this
    method will print the name of the Throwable but no stack trace. To
    print a stack trace use the {@link #fatal(Object, Throwable)} form
    instead. 
    
    @param message the message object to log */
  public
  void fatal(Object message) {
    if(hierarchy.enableInt >  Priority.FATAL_INT) 
      return;    
    if(Priority.FATAL.isGreaterOrEqual(this.getChainedPriority()))
      forcedLog(Priority.FATAL, message, null);
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
    if(hierarchy.enableInt >  Priority.FATAL_INT) 
      return;   
    if(Priority.FATAL.isGreaterOrEqual(this.getChainedPriority()))
      forcedLog(Priority.FATAL, message, t);
  }


  /**
     This method creates a new logging event and logs the event
     without further checks.  */
  protected
  void forcedLog(Priority priority, Object message, Throwable t) {
    callAppenders(new LoggingEvent(this, priority, message, t));
  }


  /**
     Get the additivity flag for this Category instance.  
  */
  public
  boolean getAdditivity() {
    return additive;
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
     Return the the {@link Hierarchy} where this <code>Category</code> instance is
     attached.

     @since 1.1 */
  public  
  Hierarchy getHierarchy() {
    return hierarchy;
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
    Log a message object with the {@link Priority#INFO INFO} priority.

    <p>This method first checks if this category is <code>INFO</code>
    enabled by comparing the priority of this category with {@link
    Priority#INFO INFO} priority.  If this category is
    <code>DEBUG</code> enabled, then it invokes all the registered
    appenders in this category and also higher in the hierarchy
    depending on the value of the additivity flag.

    <p><b>WARNING</b> Note that passing a {@link Throwable} to this
    method will print the name of the Throwable but no stack trace. To
    print a stack trace use the {@link #info(Object, Throwable)} form
    instead. 
    
    @param message the message object to log */
  public
  void info(Object message) {
    if(hierarchy.enableInt >  Priority.INFO_INT) 
      return;    
    if(Priority.INFO.isGreaterOrEqual(this.getChainedPriority()))
      forcedLog(Priority.INFO, message, null);
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
    if(hierarchy.enableInt >  Priority.INFO_INT) 
      return;   
    if(Priority.INFO.isGreaterOrEqual(this.getChainedPriority()))
      forcedLog(Priority.INFO, message, t);
  }

  /**
    *  Check whether this category is enabled for the <code>DEBUG</code>
    *  priority.
    *  
    *  <p> This function is intended to lessen the computational cost of
    *  disabled log debug statements.
    * 
    *  <p> For some <code>cat</code> Category object, when you write,
    *  <pre>
    *      cat.debug("This is entry number: " + i );
    *  </pre>
    *  
    *  <p>You incur the cost constructing the message, concatenatiion in
    *  this case, regardless of whether the message is logged or not.
    * 
    *  <p>If you are worried about speed, then you should write
    *  <pre>
    * 	 if(cat.isDebugEnabled()) { 
    * 	   cat.debug("This is entry number: " + i );
    * 	 }
    *  </pre>
    * 
    *  <p>This way you will not incur the cost of parameter
    *  construction if debugging is disabled for <code>cat</code>. On
    *  the other hand, if the <code>cat</code> is debug enabled, you
    *  will incur the cost of evaluating whether the category is debug
    *  enabled twice. Once in <code>isDebugEnabled</code> and once in
    *  the <code>debug</code>.  This is an insignificant overhead
    *  since evaluating a category takes about 1%% of the time it
    *  takes to actually log.
    * 
    *  @return boolean - <code>true</code> if this category is debug
    *  enabled, <code>false</code> otherwise.
    *   */
  public
  boolean isDebugEnabled() {
    if(hierarchy.enableInt >  Priority.DEBUG_INT)
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
    if(hierarchy.enableInt >  priority.level) 
      return false;
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
    if(hierarchy.enableInt > Priority.INFO_INT)
      return false;   
    return Priority.INFO.isGreaterOrEqual(this.getChainedPriority());
  }


  
  /**
     Remove all previously added appenders from this Category
     instance.

     <p>This is useful when re-reading configuration information.
  */
  synchronized
  public
  void removeAllAppenders() {
    if(appenderList != null) {
      int len = appenderList.size();      
      for(int i = 0; i < len; i++) {
	Appender a = (Appender) appenderList.elementAt(i);
	a.close();
      }
      appenderList.removeAllElements();
      appenderList = null;      
    }
  }

  /**
     Remove the appender passed as parameter form the list of appenders.

     @since 0.8.2
  */
  synchronized
  public
  void removeAppender(Appender appender) {
    if(appender == null || appenderList == null) 
      return;
    appenderList.removeElement(appender);    
  }

  /**
     Remove the appender with the name passed as parameter form the
     list of appenders.

     @since 0.8.2 */
  synchronized
  public
  void removeAppender(String name) {
    if(name == null || appenderList == null) return;
    int size = appenderList.size();
    for(int i = 0; i < size; i++) {
      if(name.equals(((Appender)appenderList.elementAt(i)).getName())) {
	appenderList.removeElementAt(i);
	break;
      }
    }
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
    this.hierarchy = hierarchy;
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
    Log a message object with the {@link Priority#WARN WARN} priority.

    <p>This method first checks if this category is <code>WARN</code>
    enabled by comparing the priority of this category with {@link
    Priority#WARN WARN} priority.  If this category is
    <code>DEBUG</code> enabled, then it invokes all the registered
    appenders in this category and also higher in the hierarchy
    depending on the value of the additivity flag.

    <p><b>WARNING</b> Note that passing a {@link Throwable} to this
    method will print the name of the Throwable but no stack trace. To
    print a stack trace use the {@link #warn(Object, Throwable)} form
    instead.  <p>
    
    @param message the message object to log.  */
  public
  void warn(Object message) {
    if(hierarchy.enableInt >  Priority.WARN_INT) 
      return;   

    if(Priority.WARN.isGreaterOrEqual(this.getChainedPriority()))
      forcedLog(Priority.WARN, message, null);    
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
    if(hierarchy.enableInt >  Priority.WARN_INT) 
      return;   
    if(Priority.WARN.isGreaterOrEqual(this.getChainedPriority()))
      forcedLog(Priority.WARN, message, t);
  }
}
