/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.APL file.  */

package org.apache.log4j.xml.test;


import org.apache.log4j.Category;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.OptionHandler;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.CategoryFactory;

import org.apache.log4j.xml.test.TPriority;

/**
   A simple example showing Category sub-classing. It shows the
   minimum steps necessary to implement one's {@link CategoryFactory} 
   and that sub-classes can follow the hiearchy 

   See <b><a href="doc-files/XCategory.java">source
   code</a></b> for more details.
   
 */
public class TCategory extends Category implements OptionHandler {

  // It's enough to instantiate a factory once and for all.
  private static TFactory factory = new TFactory();


  static String instanceFQCN = TCategory.class.getName();
  
  public static final String SUFFIX_OPTION = "Suffix";

  String suffix;

  /**
     Just calls the parent constuctor.
   */
  public TCategory(String name) {
    super(name);
  }

  public
  void activateOptions() {
  }

  /**
     Overrides the standard debug method by appending " world" to each
     message.  */
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

  public
  String[] getOptionStrings() {
    return (new String[] {SUFFIX_OPTION});
  }

  public
  void setOption(String option, String value) {
    System.out.println(option+"="+value);
    if(option == null) {
      return;
    }
    if(option.equalsIgnoreCase(SUFFIX_OPTION)) {
      this.suffix = value;
      System.out.println("Setting suffix to"+suffix);

    }
  }

  public
  void trace(String message) { 
    // disable is a static variable defined in Category class
    if(disable <=  TPriority.TRACE_INT) return;   
    if(TPriority.TRACE.isGreaterOrEqual(this.getChainedPriority()))
      callAppenders(new LoggingEvent(instanceFQCN, this, 
				     TPriority.TRACE, message, null));
  }


  public
  void trace(String message, Exception e) { 
    // disable is a static variable defined in Category class
    if(disable <=  TPriority.TRACE_INT) return;   
    if(TPriority.TRACE.isGreaterOrEqual(this.getChainedPriority()))
      callAppenders(new LoggingEvent(instanceFQCN, this, 
				     TPriority.TRACE, message, e));
  }


  // Any sub-class of Category must also have its own implementation of 
  // CategoryFactory.

  private static class TFactory implements CategoryFactory {
    TFactory() {
    }

    public
    Category makeNewCategoryInstance(String name) {
      return new TCategory(name);
    }
  }


}


