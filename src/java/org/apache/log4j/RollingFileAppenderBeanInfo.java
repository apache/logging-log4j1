/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.APL file.  */

 

package org.apache.log4j;

import java.beans.*;
import org.apache.log4j.helpers.LogLog;
import java.lang.reflect.Method;

public class RollingFileAppenderBeanInfo extends SimpleBeanInfo {

  private PropertyDescriptor[] props;

  public
  RollingFileAppenderBeanInfo() {
    Class clazz = RollingFileAppender.class;
    try {
      // the magic is here
      BeanInfo bi = Introspector.getBeanInfo(clazz,
					     Introspector.IGNORE_ALL_BEANINFO);

      props = bi.getPropertyDescriptors();
      if(props != null) {
	for(int i = 0; i < props.length; i++) {
	  if(props[i].getName().equals("maxFileSize")) {
	    Method m = clazz.getMethod("setMaxFileSize", 
				       new Class[] {String.class});
	    props[i] = new PropertyDescriptor("maxFileSize", null, m);
	  }
	}
      }
      // flush the bean info because getPropertyDescriptors() will now return
      // different properties
      Introspector.flushFromCaches(RollingFileAppender.class);
    } catch(IntrospectionException e) {
      LogLog.error("Could not inspect RollingFileAppender.", e);
    } catch(NoSuchMethodException e) {
      LogLog.error("Could find setter method for RollingFileAppender.", e);
    }
  }

  public
  PropertyDescriptor[] getPropertyDescriptors() {
    return props;
  }
}
