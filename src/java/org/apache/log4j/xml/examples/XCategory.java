/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.APL file.  */

package org.apache.log4j.xml.examples;


import org.apache.log4j.Category;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.OptionHandler;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.CategoryFactory;
import org.apache.log4j.helpers.LogLog;

import org.apache.log4j.xml.examples.XPriority;

/**
   A simple example showing Category sub-classing. It shows the
   minimum steps necessary to implement one's {@link CategoryFactory}.
   Note that sub-classes follow the hiearchy even if its categories
   belong to different classes.

   See <b><a href="doc-files/XCategory.java">source code</a></b> for
   more details. See also <a
   href="doc-files/extension1.xml">extension1.xml</a> and <a
   href="doc-files/extension2.xml">extension2.xml</a> XML configuration
   files.

   
 */
public class XCategory extends Category implements OptionHandler {

  // It's enough to instantiate a factory once and for all.
  private static XFactory factory = new XFactory();


  static String instanceFQCN = XCategory.class.getName();
  
  public static final String SUFFIX_OPTION = "Suffix";

  String suffix = "";

  /**
     Just calls the parent constuctor.
   */
  public XCategory(String name) {
    super(name);
  }

  public
  void activateOptions() {
  }

  /**
     Overrides the standard debug method by appending the value of
     suffix variable to each message.  */
  public 
  void debug(String message) {
    log(instanceFQCN, Priority.DEBUG, message + suffix, null);
  }

  /**
     This method overrides {@link Category#getInstance} by supplying
     its own factory type as a parameter.

   */
  public 
  static
  Category getInstance(String name) {
    return Category.getInstance(name, factory); 
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
     Set XCategory specific options.

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

  /**
     We introduce a new printing method that takes the TRACE priority.
  */
  public
  void trace(String message) { 
    // disable is defined in Category
    if(disable <=  XPriority.TRACE_INT) return;   
    if(XPriority.TRACE.isGreaterOrEqual(this.getChainedPriority()))
      callAppenders(new LoggingEvent(instanceFQCN, this, XPriority.TRACE, 
				     message, null));
  }


  // Any sub-class of Category must also have its own implementation of 
  // CategoryFactory.

  private static class XFactory implements CategoryFactory {
    XFactory() {
    }

    public
    Category makeNewCategoryInstance(String name) {
      return new XCategory(name);
    }
  }


}


