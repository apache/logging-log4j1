/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.APL file.  */

package org.apache.log4j.examples;

import org.apache.log4j.*;
import org.apache.log4j.spi.CategoryFactory;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.log4j.xml.examples.XPriority;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.helpers.LogLog;

/**
   A simple example showing category subclassing. 

   <p>See <b><a href="doc-files/MyCategory.java">source code</a></b>
   for more details.

   <p>See {@link MyCategoryTest} for a usage example.
   
 */
public class MyCategory extends Category {

  // It's usually a good idea to add a dot suffix to the fully
  // qualified class name. This makes caller localization to work
  // properly even from classes that have the almostthe same fully
  // qualified class name as MyCategory, e.g. MyCategoryTest.
  static String FQCN = MyCategory.class.getName() + ".";

  // It's enough to instantiate a factory once and for all.
  private static MyCategoryFactory myFactory = new MyCategoryFactory();

  /**
     Just calls the parent constuctor.
   */
  public MyCategory(String name) {
    super(name);
  }

  /**
     Overrides the standard debug method by appending " world" at the
     end of each message.  */
  public 
  void debug(Object message) {
    super.debug(message + " world.");    
  }
  
  /**
     This makes caller localization to work properly.
   */
  protected
  String getFQCN() {
    return MyCategory.FQCN;
  }


  /**
     This method overrides {@link Category#getInstance} by supplying
     its own factory type as a parameter.
  */
  public 
  static
  Category getInstance(String name) {
    return Category.getInstance(name, myFactory); 
  }

  public
  void trace(Object message) {
    super.log(XPriority.TRACE, message); 
  }
}


