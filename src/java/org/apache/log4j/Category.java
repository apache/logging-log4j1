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
//              Colin Sampaleanu <colinml1@exis.com> 

package org.apache.log4j;

import org.apache.log4j.spi.RootCategory;
import org.apache.log4j.spi.AppenderAttachable;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.NullEnumeration;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.helpers.AppenderAttachableImpl;
import org.apache.log4j.helpers.Loader;
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
  This is the central class in the log4j package. One of the
  distintive features of log4j are hierarchical categories and their
  evaluation.

  <p>See the <a href="../../../../manual.html">user manual</a> for an
  introduction on this class. 

  @author Ceki G&uuml;lc&uuml;
  @author Anders Kristensen */
public class Category extends Logger implements AppenderAttachable {

  private static final String FQCN = Category.class.getName();

  private static LoggerFactory factory = new DefaultCategoryFactory();
    
  /**
     This constructor created a new <code>Category</code> instance and
     sets its name.

     <p>It is intended to be used by sub-classes only. You should not
     create categories directly.

     @param name The name of the category.  
  */
  protected 
  Category(String name) {
    super(name);
  }

  /** 
    Log a message object with the {@link Level#DEBUG DEBUG} level.

    <p>This method first checks if this category is <code>DEBUG</code>
    enabled by comparing the level of this category with the {@link
    Level#DEBUG DEBUG} level. If this category is
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
    if(hierarchy.enableInt >  Level.DEBUG_INT) 
      return;    
    if(Level.DEBUG.isGreaterOrEqual(this.getChainedLevel())) {
      forcedLog(FQCN, Level.DEBUG, message, null);
    }
  }
  

  /**  
   Log a message object with the <code>DEBUG</code> level including
   the stack trace of the {@link Throwable} <code>t</code> passed as
   parameter.
   
   <p>See {@link #debug(Object)} form for more detailed information.
   
   @param message the message object to log.
   @param t the exception to log, including its stack trace.  */  
  public
  void debug(Object message, Throwable t) {
    if(hierarchy.enableInt >  Level.DEBUG_INT) 
      return;
    if(Level.DEBUG.isGreaterOrEqual(this.getChainedLevel()))
      forcedLog(FQCN, Level.DEBUG, message, t);    
  }

  //public
  //void dump() {
  //  System.out.println("Category " + name + " dump -----");
  //  for(Category c = this; c != null; c=c.parent)
  //	System.out.println("("+c.name+", "+c.level+") ->");
  //  System.out.println("---------------------------");
  //
  //}
  
  /** 
    Log a message object with the {@link Level#ERROR ERROR} Level.

    <p>This method first checks if this category is <code>ERROR</code>
    enabled by comparing the level of this category with {@link
    Level#ERROR ERROR} Level. If this category is
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
    if(hierarchy.enableInt >  Level.ERROR_INT) 
      return;
    if(Level.ERROR.isGreaterOrEqual(this.getChainedLevel()))
      forcedLog(FQCN, Level.ERROR, message, null);
  }

  /**
     Returns all the currently defined categories in the default
     hierarchy as an {@link java.util.Enumeration Enumeration}.

     <p>The root category is <em>not</em> included in the returned
     {@link Enumeration}.     

     @deprecated FIXME FIXME FIXME FIXME FIXME FIXME FIXME 
  */
  public
  static
  Enumeration getCurrentCategories() {
    return defaultHierarchy.getCurrentLoggers();
  }


 /**
     Retrieve a category with named as the <code>name</code>
     parameter. If the named category already exists, then the
     existing instance will be reutrned. Otherwise, a new instance is
     created. 

     By default, categories do not have a set level but inherit
     it from the hierarchy. This is one of the central features of
     log4j.

     @param name The name of the category to retrieve. 


 */
  public
  static
  Category getInstance(String name) {
    return (Category) defaultHierarchy.getLogger(name, factory);
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
  Category getInstance(String name, LoggerFactory factory) {
    return (Category) defaultHierarchy.getLogger(name, factory);
  }	


  public
  static
  Category getRoot() {
    return (Category) defaultHierarchy.getRootLogger();
  }
}
