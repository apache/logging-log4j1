/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.APL file.  */

// Contibutors: "Luke Blanshard" <Luke@quiq.com>
//              "Mark DONSZELMANN" <Mark.Donszelmann@cern.ch>
//              "Muly Oved" <mulyoved@hotmail.com>

package org.apache.log4j;

import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.or.ObjectRenderer;
import org.apache.log4j.or.RendererMap;
import java.util.Enumeration;

/**
   Use this class to quickly configure the package.

   <p>For file based configuration see {@link
   PropertyConfigurator}. For XML based configuration see {@link
   org.apache.log4j.xml.DOMConfigurator DOMConfigurator}.

   @since 0.8.1
   @author Ceki G&uuml;lc&uuml; */
public class BasicConfigurator {

  /**
     <p><code>DISABLE_OVERRIDE_KEY</code> is the name of the constant
     holding the string value <b>log4j.disableOverride</b>.

     <p>Setting the system property <b>log4j.disableOverride</b> to
     "true" or any other value than "false" overrides the effects of
     all methods {@link #disable}, {@link #disableAll}, {@link
     #disableDebug} and {@link #disableInfo}. Thus, enabling normal
     evaluation of logging requests, i.e. according to the <a
     href="../../manual.html#selectionRule">Basic Selection Rule</a>.

     <p>If both <code>log4j.disableOverride</code> and a
     <code>log4j.disable</code> options are present, then
     <code>log4j.disableOverride</code> as the name indicates
     overrides any <code>log4j.disable</code> options.
     
     @since 0.8.5 */ 
     public static final String DISABLE_OVERRIDE_KEY = "log4j.disableOverride";

  /**
     <p><code>DISABLE_KEY</code> is the name of the constant
     holding the string value <b>log4j.disable</b>.

     <p>Setting the system property <b>log4j.disable</b> to DEBUG,
     INFO, WARN, ERROR or FATAL is equivalent to calling the {@link
     #disable} method with the corresponding priority.

     <p>If both <code>log4j.disableOverride</code> and a
     <code>log4j.disable</code> options are present, then
     <code>log4j.disableOverride</code> as the name indicates
     overrides any <code>log4j.disable</code> options.
     
     @since 1.1 */
     public static final String DISABLE_KEY = "log4j.disable";


  /**
     Special priority value signifying inherited behaviour. The
     current value of this string constant is <b>inherited</b>.

  */
  public static final String INHERITED = "inherited";


  // Check if value of(DISABLE_OVERRIDE_KEY) system property is set.
  // If it is set to "true" or any value other than "false", then set
  // static variable Category.disable to Category.DISABLE_OVERRIDE.
  static {    
    String override = OptionConverter.getSystemProperty(DISABLE_OVERRIDE_KEY, null);
    if(override != null) {
      if(OptionConverter.toBoolean(override, true)) {
	LogLog.debug("Overriding disable. Non-null system property " + 
		     DISABLE_OVERRIDE_KEY + "=[" + override +"].");
	Category.disable = Category.DISABLE_OVERRIDE;
      }
    } else { // check for log4j.disable only in absence of log4j.disableOverride
      String disableStr = OptionConverter.getSystemProperty(DISABLE_KEY, null);
      if(disableStr != null) {
	disableAsNeeded(disableStr);
      }
    }
  }


  protected BasicConfigurator() {
  }

  /**
     Used by subclasses to add a renderer to the hierarchy passed as parameter.
   */
  protected
  void addRenderer(Hierarchy hierarchy, String renderedClassName, 
		   String renderingClassName) {
    LogLog.debug("Rendering class: ["+renderingClassName+"], Rendered class: ["+
		 renderedClassName+"].");
    ObjectRenderer renderer = (ObjectRenderer) 
             OptionConverter.instantiateByClassName(renderingClassName, 
						    ObjectRenderer.class,
						    null);
    if(renderer == null) {
      LogLog.error("Could not instantiate renderer ["+renderingClassName+"].");
      return;
    } else {
      try {
	Class renderedClass = Class.forName(renderedClassName);
	hierarchy.rendererMap.put(renderedClass, renderer);
      } catch(ClassNotFoundException e) {
	LogLog.error("Could not find class ["+renderedClassName+"].", e);
      }
    }
  }

  
  /**
     Add a {@link FileAppender} that uses {@link PatternLayout} using
     the {@link PatternLayout#TTCC_CONVERSION_PATTERN} and prints to
     <code>System.out</code> to the root category.  */
  static
  public
  void configure() {
    Category root = Category.getRoot();
    root.addAppender(new FileAppender(
	new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN), System.out));
  }

  /**
     Add <code>appender</code> to the root category.
     @param appender The appender to add to the root category.
  */
  static
  public
  void configure(Appender appender) {
    Category root = Category.getRoot();
    root.addAppender(appender);
  }


  static
  protected
  void disableAsNeeded(String disableStr) {
    if((disableStr != null) && (Category.disable != Category.DISABLE_OVERRIDE)) {
      Priority p = Priority.toPriority(disableStr, null);
      if(p != null) {
	disable(p);
      } else {
	LogLog.warn("Could not convert ["+disableStr+"] to Priority.");
      }
    }
  }


  /**
     Disable all logging requests of priority <em>equal to or
     below</em> the priority parameter <code>p</code>, regardless of
     the request category. Logging requests of higher priority then
     the priority of <code>p</code> remain unaffected.

     <p>Nevertheless, if the {@link #DISABLE_OVERRIDE_KEY} system
     property is set to "true" or any value other than "false", then
     logging requests are evaluated as usual, i.e. according to the <a
     href="../../manual.html#selectionRule">Basic Selection Rule</a>.

     <p>The "disable" family of methods are there for speed. They
     allow printing methods such as debug, info, etc. to return
     immediately after an interger comparison without walking the
     category hierarchy. In most modern computers an integer
     comparison is measured in nanoseconds where as a category walk is
     measured in units of microseconds.

     <p>Other configurators define alternate ways of overriding the
     disable override flag. See {@link PropertyConfigurator} and
     {@link org.apache.log4j.xml.DOMConfigurator}.


     @since 0.8.5 */
  static 
  public
  void disable(Priority p) {
    if(Category.disable != Category.DISABLE_OVERRIDE) {
      Category.disable = p.level;
    }
  }
  
  /**
     Disable all logging requests regardless of category and priority.
     This method is equivalent to calling {@link #disable} with the
     argument {@link Priority#FATAL}, the highest possible priority.

     @since 0.8.5 */
  static 
  public
  void disableAll() {
    disable(Priority.FATAL);
  }


  /**
     Disable all logging requests of priority DEBUG regardless of
     category.  Invoking this method is equivalent to calling {@link
     #disable} with the argument {@link Priority#DEBUG}.

     @since 0.8.5 */
  static 
  public
  void disableDebug() {
    disable(Priority.DEBUG);
  }


  /**
     Disable all logging requests of priority INFO and below
     regardless of category. Note that DEBUG messages are also
     disabled.  

     <p>Invoking this method is equivalent to calling {@link #disable}
     with the argument {@link Priority#INFO}.

     @since 0.8.5 */
  static 
  public
  void disableInfo() {
    disable(Priority.INFO);
  }  

  /**
     Undoes the effect of calling any of {@link #disable}, {@link
     #disableAll}, {@link #disableDebug} and {@link #disableInfo}
     methods. More precisely, invoking this method sets the Category
     class internal variable called <code>disable</code> to its
     default "off" value.

     @since 0.8.5 */
  static 
  public
  void enableAll() {
    Category.disable = Category.DISABLE_OFF;
  }
  
  /**

     This method is equivalent to the {@link #disableInfo} method.
     
     @deprecated
     @since 0.8.0 */
  public
  static
  void flagAsShippedCode() {
    disableInfo();
  }


  /**
     Override the shipped code flag if the <code>override</code>
     parameter is not null.

     <p>If <code>override</code> is null then there is nothing to do.
     Otherwise, set Category.shippedCode to false if override has a
     value other than "false".     
  */
  protected
  static
  void overrideAsNeeded(String override) {
    // If override is defined, any value other than false will be
    // interpreted as true.    
    if(override != null) {
      LogLog.debug("Handling non-null disable override directive: \""+
		   override +"\".");
      if(OptionConverter.toBoolean(override, true)) {
	LogLog.debug("Overriding all disable methods.");
	Category.disable = Category.DISABLE_OVERRIDE;
      }
    }
  }

  /**
     Reset the configuration to its default.  This removes all
     appenders from all categories, sets the priority of all non-root
     categories to <code>null</code>, their additivity flad to
     <code>true</code> and sets the priority of the root category to
     {@link Priority#DEBUG DEBUG}.  Moreover, message disabling is set
     its default "off" value.

     <p>This method should be used sparingly and with care as it will
     block all logging until it is completed.</p>

     @since version 0.8.5 */
  public
  static
  void resetConfiguration() {

    Category.defaultHierarchy.getRoot().setPriority(Priority.DEBUG);
    Category.defaultHierarchy.root.setResourceBundle(null);
    Category.disable =  Category.DISABLE_OFF;
    
    // the synchronization is needed to prevent JDK 1.2.x hashtable
    // surprises
    synchronized(Category.defaultHierarchy.ht) {    
      Category.defaultHierarchy.shutdown(); // nested locks are OK    
    
      Enumeration cats = Category.getCurrentCategories();
      while(cats.hasMoreElements()) {
	Category c = (Category) cats.nextElement();
	c.setPriority(null);
	c.setAdditivity(true);
	c.setResourceBundle(null);
      }
    }
    Category.defaultHierarchy.rendererMap.clear();
  }
}
