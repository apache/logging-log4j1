/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j.customLogger;


import org.apache.log4j.*;
import org.apache.log4j.spi.OptionHandler;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.helpers.LogLog;

import examples.customLevel.XLevel;

/**
   A simple example showing Logger sub-classing. It shows the
   minimum steps necessary to implement one's {@link LoggerFactory}.
   Note that sub-classes follow the hierarchy even if its loggers
   belong to different classes.
 */
public class XLogger extends Logger implements OptionHandler {
  
  // It's usually a good idea to add a dot suffix to the fully
  // qualified class name. This makes caller localization to work
  // properly even from classes that have almost the same fully
  // qualified class name as XLogger, such as XLogegoryTest.
  private static String FQCN = XLogger.class.getName() + ".";

  // It's enough to instantiate a factory once and for all.
  private static XFactory factory = new XFactory();
  
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

  static
  public
  Logger getLogger(String name) {
    return LogManager.getLogger(name, factory);
  }

  static
  public
  Logger getLogger(Class clazz) {
    return XLogger.getLogger(clazz.getName());
  }


  public
  String getSuffix() {
    return suffix;
  }

  public
  void setSuffix(String suffix) {
    this.suffix = suffix;
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



  // Any sub-class of Logger must also have its own implementation of 
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


