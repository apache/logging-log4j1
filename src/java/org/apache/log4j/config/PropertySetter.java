/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.APL file.
 */

package org.apache.log4j.config;

import java.beans.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import org.apache.log4j.*;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.OptionHandler;

/**
   General purpose Object property setter. Clients repeatedly invokes
   {@link #setProperty setProperty(name,value)} in order to invoke setters
   on the Object specified in the constructor. This class relies on the
   JavaBeans {@link Introspector} to analyze the given Object Class using
   reflection.
   
   <p>Usage:
   <pre>
     PropertySetter ps = new PropertySetter(anObject);
     ps.set("name", "Joe");
     ps.set("age", "32");
     ps.set("isMale", "true");
   </pre>
   will cause the invocations anObject.setName("Joe"), anObject.setAge(32),
   and setMale(true) if such methods exist with those signatures.
   Otherwise an {@link IntrospectionException} are thrown.
  
   @author Anders Kristensen
   @since 1.1
 */
public class PropertySetter {
  protected Object obj;
  protected PropertyDescriptor[] props;
  
  /**
    Create a new PropertySetter for the specified Object. This is done
    in prepartion for invoking {@link #setProperty} one or more times.
    
    @param obj  the object for which to set properties
   */
  public
  PropertySetter(Object obj) {
    this.obj = obj;
  }
  
  /**
     Uses JavaBeans {@link Introspector} to computer setters of object to be
     configured.
   */
  protected
  void introspect() {
    try {
      BeanInfo bi = Introspector.getBeanInfo(obj.getClass());
      props = bi.getPropertyDescriptors();
    } catch (IntrospectionException ex) {
      LogLog.error("Failed to introspect "+obj+": " + ex.getMessage());
      props = new PropertyDescriptor[0];
    }
  }
  
  public
  static
  void setProperties(Object obj, Properties properties, String prefix) {
    new PropertySetter(obj).setProperties(properties, prefix);
  }
  

  /**
     Set the properites for the object that match the
     <code>prefix</code> passed as parameter.

     
   */
  public
  void setProperties(Properties properties, String prefix) {
    int len = prefix.length();
    
    for (Enumeration e = properties.keys(); e.hasMoreElements(); ) {
      String key = (String) e.nextElement();
      
      // handle only properties that start with the desired frefix.
      if (key.startsWith(prefix)) {

	
	// ignore key if it contains dots after the prefix
        if (key.indexOf('.', len + 1) > 0) {
	  //System.err.println("----------Ignoring---["+key
	  //	     +"], prefix=["+prefix+"].");
	  continue;
	}
        
	String value = OptionConverter.findAndSubst(key, properties);
        key = key.substring(len);
        if ("layout".equals(key) && obj instanceof Appender) {
          continue;
        }
        
        setProperty(key, value);
      }
    }
    activate();
  }
  
  /**
     Set a property on this PropertySetter's Object. If successful, this
     method will invoke a setter method on the underlying Object. The
     setter is the one for the specified property name and the value is
     determined partly from the setter argument type and partly from the
     value specified in the call to this method.
     
     <p>If the setter expects a String no conversion is necessary.
     If it expects an int, then an attempt is made to convert 'value'
     to an int using new Integer(value). If the setter expects a boolean,
     the conversion is by new Boolean(value).
     
     @param name    name of the property
     @param value   String value of the property
     @throws PropertySetterException
            if no setter exists for the named property,
            or if the setter takes an unkown argument type, i.e. one
            other than String, int, and boolean,
            or if the setter method isn't public
   */
  public
  void setProperty(String name, String value) {
    if (value == null) return;
    
    name = Introspector.decapitalize(name);
    PropertyDescriptor prop = getPropertyDescriptor(name);
    
    if (prop == null) {
      LogLog.warn("No such property: " + name);
    } else {
      try {
        setProperty(prop, name, value);
      } catch (PropertySetterException ex) {
        LogLog.warn("Failed to set property " + name +
                    " to value \"" + value + "\": " + ex.getMessage());
      }
    }
  }
  
  /*
  public
  void setProperty(String name, String value) throws PropertySetterException {
    if (props == null) introspect();
    name = Introspector.decapitalize(name);
    PropertyDescriptor prop = getPropertyDescriptor(name);
    
    if (prop != null) {
      setProperty(prop, name, value);
    } else {
      throw new PropertySetterException(
          "No such property", obj, name, value);
    }
  }
  */
  
  public
  void setProperty(PropertyDescriptor prop, String name, String value)
    throws PropertySetterException
  {
    Method setter = prop.getWriteMethod();
    if (setter == null) {
      throw new PropertySetterException("No setter for property");
    }
    Class[] paramTypes = setter.getParameterTypes();
    if (paramTypes.length != 1) {
      throw new PropertySetterException("#params for setter != 1");
    }
    
    Object arg;
    try {
      arg = getArg(value, paramTypes[0]);
    } catch (Throwable t) {
      throw new PropertySetterException(t);
    }
    if (arg == null) {
      throw new PropertySetterException(
          "Unknown property type: "+ paramTypes[0]);
    }
    LogLog.debug("Setting property " + name + ": " + arg);
    try {
      setter.invoke(obj, new Object[]  { arg });
    } catch (Exception ex) {
      throw new PropertySetterException(ex);
    }
  }
  
  protected
  Object getArg(String val, Class type) {
    if (String.class.isAssignableFrom(type)) {
      return val;
    } else if (Integer.TYPE.isAssignableFrom(type)) {
      return new Integer(val.trim());
    } else if (Long.TYPE.isAssignableFrom(type)) {
      return new Long(val.trim());
    } else if (Boolean.TYPE.isAssignableFrom(type)) {
      val = val.trim();
      if ("true".equalsIgnoreCase(val)) {
        return Boolean.TRUE;
      } else if ("false".equalsIgnoreCase(val)) {
        return Boolean.FALSE;
      }
    } else if (Priority.class.isAssignableFrom(type)) {
      return Priority.toPriority(val.trim());
    }
    return null;
  }
  
  protected
  PropertyDescriptor getPropertyDescriptor(String name) {
    if (props == null) introspect();
    
    for (int i = 0; i < props.length; i++) {
      if (name.equals(props[i].getName())) return props[i];
    }
    return null;
  }
  
  public
  void activate() {
    if (obj instanceof OptionHandler) {
        ((OptionHandler) obj).activateOptions();
    }
  }
}
