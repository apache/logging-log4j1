/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j.helpers;

import java.util.Properties;

public class OptionConverter {

  public
  static
  boolean toBoolean(String value, boolean defaultVal) {
    if(value == null)
      return defaultVal;
    String trimmedVal = value.trim();
    if("true".equalsIgnoreCase(trimmedVal)) 
  	return true;
    if("false".equalsIgnoreCase(trimmedVal))
      return false;
    return defaultVal;
  }


  public
  static
  Object instantiateByKey(Properties props, String key, Class superClass,
				Object defaultValue) {

    // Get the value of the property in string form
    String className = props.getProperty(key);
    if(className == null) {
      LogLog.error("Could not find value for " + key);
      return defaultValue;
    }
    // Trim className to avoid trailing spaces that cause problems.
    return OptionConverter.instantiateByClassName(className.trim(), superClass,
						  defaultValue);
  }


    /**
     Instantiate an object given a class name. Check that the
     <code>className</code> is a subclass of <code>superClass</code>.

   */
  public
  static
  Object instantiateByClassName(String className, Class superClass,
				Object defaultValue) {
    if(className != null) {
      try {
	Class classObj = Class.forName(className);
	if(!superClass.isAssignableFrom(classObj)) 
	  LogLog.error("A \""+className+"\" object is not assignable to a \""+
		       superClass.getName() + "\" object.");
	return classObj.newInstance();
      }
      catch (Exception e) {
	LogLog.error("Could not instantiate class [" + className + "].", e);
      }
    }
    return defaultValue;    
  }



}
