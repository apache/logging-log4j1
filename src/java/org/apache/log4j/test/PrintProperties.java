/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.APL file.
 */

package org.apache.log4j.test;

import java.util.*;
import org.apache.log4j.*;
import org.apache.log4j.spi.OptionHandler;

/**
   Prints the configuration of the log4j default hierarchy
   (which needs to be auto-initialized) as a propoperties file
   on System.out.
   
   @author  Anders Kristensen
 */
public class PrintProperties {
  protected int numAppenders = 0;
  protected Hashtable appenderNames  = new Hashtable();
  protected Hashtable layoutNames  = new Hashtable();
  
  public
  static
  void main(String[] args) {
    new PrintProperties().print();
  }
  
  public
  void print() {
    printOptions(Category.getRoot());
    
    Enumeration cats = Category.getCurrentCategories();
    while (cats.hasMoreElements()) {
      printOptions((Category) cats.nextElement());
    }
  }
  
  public
  void printOptions(Category cat) {
    Enumeration appenders = cat.getAllAppenders();
    String appenderString = "";
    
    while (appenders.hasMoreElements()) {
      Appender app = (Appender) appenders.nextElement();
      String name;
      
      if ((name = (String) appenderNames.get(app)) == null) {
        name = "A" + numAppenders++;
        appenderNames.put(app, name);
        
        if (app instanceof OptionHandler) {
          printOptions((OptionHandler) app, "log4j.appender."+name);
        }
        Layout layout = ((AppenderSkeleton) app).getLayout();
        if (layout != null) {
          printOptions(layout, "log4j.appender."+name+".layout");
        }
      }
      appenderString += ", " + name;
    }
    String catKey = (cat == Category.getRoot())
        ? "log4j.rootCategory"
        : "log4j.category." + cat.getName();
    System.out.println(catKey + "=" + cat.getPriority() + appenderString);
  }
  
  public
  void printOptions(OptionHandler oh, String fullname) {
    String[] options = oh.getOptionStrings();
    
    System.out.println(fullname + "=" + oh.getClass().getName());
    
    for (int i = 0; i < options.length; i++) {
      String val = oh.getOption(options[i]);
      if (val != null) {
        System.out.println(fullname + "." + options[i] +
                   "=" + oh.getOption(options[i]));
      }
    }
  }
}