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
     all methods {@link Hierarchy#disable}, {@link
     Hierarchy#disableAll}, {@link Hierarchy#disableDebug} and {@link
     Hierarchy#disableInfo}. Thus, enabling normal evaluation of logging
     requests, i.e. according to the <a
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
     Hierarchy#disable} method with the corresponding priority.

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
      Category.defaultHierarchy.setDisableOverride(override);
    } else { // check for log4j.disable only in absence of log4j.disableOverride
      String disableStr = OptionConverter.getSystemProperty(DISABLE_KEY, null);
      if(disableStr != null) {
	Category.defaultHierarchy.disable(disableStr);
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
     See {@link Hierarchy#disable(String)}.

     @deprecated Use <code>Category.getDefaultHierarchy().disable()</code> instead.  */
  public
  static
  void disable(String priorityStr) {
    Category.getDefaultHierarchy().disable(priorityStr);
  }

  /**
     See {@link Hierarchy#disable(Priority)}.

     @deprecated Use <code>Category.getDefaultHierarchy().disable(p)</code> instead.  */
  public
  static
  void disable(Priority p) {
  
  }
  

  /**
     See {@link Hierarchy#disableAll()}.

     @deprecated Use <code>Category.getDefaultHierarchy().disableAll()</code> instead.  */  
  public
  static
  void disableAll() {
      Category.getDefaultHierarchy().disable(Priority.FATAL);
  }

 /**
     See {@link Hierarchy#disableDebug()}.

     @deprecated Use <code>Category.getDefaultHierarchy().disableDebug()</code> instead.  */ 
  public
  static
  void disableDebug() {
    Category.getDefaultHierarchy().disable(Priority.DEBUG);
  }  

 /**
     See {@link Hierarchy#disableInfo()}.

     @deprecated Use <code>Category.getDefaultHierarchy().disableInfo()</code> instead.  */ 
  public
  static
  void disableInfo() {
    Category.getDefaultHierarchy().disable(Priority.INFO);
  } 
  

 /**
     See {@link Hierarchy#enableAll()}.

     @deprecated Use <code>Category.getDefaultHierarchy().enableAll()</code> instead.  */ 
  public
  static
  void enableAll() {
    Category.getDefaultHierarchy().disable(Priority.INFO);
  }

  /**
     Add a {@link FileAppender} that uses {@link PatternLayout} using
     the {@link PatternLayout#TTCC_CONVERSION_PATTERN} and prints to
     <code>System.out</code> to the root category.  */
  static
  public
  void configure() {
    Category root = Category.getRoot();
    root.addAppender(new ConsoleAppender(
           new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN)));
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

  /**
     Reset the default hierarchy to its defaut. It is equivalent to
     calling
     <code>Category.getDefaultHierarchy().resetConfiguration()</code>.
 
     See {@link Hierarchy#resetConfiguration()} for more details.  */
  public
  static
  void resetConfiguration() {
    Category.defaultHierarchy.resetConfiguration();
  }

  /**
     @deprecated Use <code>hierarchy.resetConfiguration()</code> instead.
  */
  public
  static
  void resetConfiguration(Hierarchy hierarchy) {
    hierarchy.resetConfiguration();
  }
}
