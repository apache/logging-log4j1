/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.APL file.  */

package org.apache.log4j.xml.examples;


import org.apache.log4j.*;
import org.apache.log4j.spi.OptionHandler;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.helpers.LogLog;

import org.apache.log4j.xml.examples.XLevel;

/**
   A simple example showing Category sub-classing. It shows the
   minimum steps necessary to implement one's {@link LoggerFactory}.
   Note that sub-classes follow the hiearchy even if its categories
   belong to different classes.

   <p>See <b><a href="doc-files/XCategory.java">source code</a></b>
   for more details. See also <a
   href="doc-files/extension1.xml">extension1.xml</a> and <a
   href="doc-files/extension2.xml">extension2.xml</a> XML
   configuration files.

   <p>
   
 */
public class XLogger extends Logger implements OptionHandler {
  
  // It's usually a good idea to add a dot suffix to the fully
  // qualified class name. This makes caller localization to work
  // properly even from classes that have almost the same fully
  // qualified class name as XLogger, such as XLogegoryTest.
  private static String FQCN = XLogger.class.getName() + ".";

  // It's enough to instantiate a factory once and for all.
  private static XFactory factory = new XFactory();
  
  public static final String SUFFIX_OPTION = "Suffix";

  String suffix = "";

  /**
     Just calls the parent constuctor.
   */
  protected XLogger(String name) {
    super(name);
  }

  /** 
     Nothing to activate.
   */
  public
  void activateOptions() {
  }

  /**
     Overrides the standard debug method by appending the value of
     suffix variable to each message.  
  */
  public 
  void debug(String message) {
    super.log(FQCN, Level.DEBUG, message + " " + suffix, null);
  }


 /**
    Retuns the option names for this component, namely the string
    {@link #SUFFIX_OPTION}.
 */
  public
  String[] getOptionStrings() {
    return (new String[] {SUFFIX_OPTION});
  }


  /**
     We introduce a new printing method in order to support {@link
     XLevel#LETHAL}.  */
  public
  void lethal(String message, Throwable t) { 
    if(repository.isDisabled(XLevel.LETHAL_INT)) 
      return;
    if(XLevel.LETHAL.isGreaterOrEqual(this.getChainedLevel()))
      forcedLog(FQCN, XLevel.LETHAL, message, t);
  }

  /**
     We introduce a new printing method in order to support {@link
     XLevel#LETHAL}.  */
  public
  void lethal(String message) { 
    if(repository.isDisabled(XLevel.LETHAL_INT)) 
      return;
    if(XLevel.LETHAL.isGreaterOrEqual(this.getChainedLevel()))
      forcedLog(FQCN, XLevel.LETHAL, message, null);
  }


 /**
     Set XLogegory specific options.

     <p>The <b>Suffix</b> option is the only recognized option. It
     takes a string value.
     */
  public
  void setOption(String option, String value) {
    if(option == null) {
      return;
    }
    if(option.equalsIgnoreCase(SUFFIX_OPTION)) {
      this.suffix = value;
      LogLog.debug("Setting suffix to"+suffix);
    }
  }
  
  public
  String getOption(String option) {
    if(option.equalsIgnoreCase(SUFFIX_OPTION)) {
      return this.suffix;
    }
    return null;
  }

  static
  public
  Logger getInstance(String name) {
    return Category.getInstance(name, factory);
  }

  /**
     We introduce a new printing method that takes the TRACE level.
  */
  public
  void trace(String message, Throwable t) { 
    if(repository.isDisabled(XLevel.TRACE_INT))
      return;   
    if(XLevel.TRACE.isGreaterOrEqual(this.getChainedLevel()))
      forcedLog(FQCN, XLevel.TRACE, message, t);
  }

  /**
     We introduce a new printing method that takes the TRACE level.
  */
  public
  void trace(String message) { 
    if(repository.isDisabled(XLevel.TRACE_INT))
      return;   
    if(XLevel.TRACE.isGreaterOrEqual(this.getChainedLevel()))
      forcedLog(FQCN, XLevel.TRACE, message, null);
  }



  // Any sub-class of Category must also have its own implementation of 
  // CategoryFactory.
  public static class XFactory implements LoggerFactory {
    
    public XFactory() {
    }

    public
    Logger  makeNewLoggerInstance(String name) {
      return new XLogger(name);
    }
  }
}


