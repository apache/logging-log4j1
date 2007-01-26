/*
 * Copyright 1999,2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.log4j.config;

import org.apache.log4j.Level;
import org.apache.log4j.spi.ComponentBase;

import java.beans.*;

import java.lang.reflect.*;


/**
   Used for inferring configuration information for a log4j's component.

   @author  Anders Kristensen
 */
public class PropertyGetter extends ComponentBase {
  protected static final Object[] NULL_ARG = new Object[] {  };
  protected Object obj;
  protected PropertyDescriptor[] props;

  /**
    Create a new PropertyGetter for the specified Object. This is done
    in prepartion for invoking {@link
    #getProperties(PropertyGetter.PropertyCallback, String)} one or
    more times.

    @param obj the object for which to set properties */
  public PropertyGetter(Object obj) throws IntrospectionException {
    BeanInfo bi = Introspector.getBeanInfo(obj.getClass());
    props = bi.getPropertyDescriptors();
    this.obj = obj;
  }

  public static void getProperties(
    Object obj, PropertyCallback callback, String prefix) {
    try {
      new PropertyGetter(obj).getProperties(callback, prefix);
    } catch (IntrospectionException ex) {
      //LogLog.error("Failed to introspect object " + obj, ex);
    }
  }

  public void getProperties(PropertyCallback callback, String prefix) {
    for (int i = 0; i < props.length; i++) {
      Method getter = props[i].getReadMethod();
      if (getter == null) {
        continue;
      }
      String name = props[i].getName();
      if (!isHandledType(getter.getReturnType())) {
        getLogger().warn("Ignoring " + name +" " + getter.getReturnType());
        continue;
      }
      try {
        Object result = getter.invoke(obj, NULL_ARG);

        getLogger().debug("PROP " + name +": " + result);
        if (result != null) {
          callback.foundProperty(obj, prefix, name, result);
        }
      } catch (Exception e) {
        getLogger().warn("Failed to get value of property " + name, e);
      }
    }
  }

  protected boolean isHandledType(Class type) {
    return String.class.isAssignableFrom(type)
    || Integer.TYPE.isAssignableFrom(type) || Long.TYPE.isAssignableFrom(type)
    || Boolean.TYPE.isAssignableFrom(type)
    || Level.class.isAssignableFrom(type);
  }

  public interface PropertyCallback {
    void foundProperty(Object obj, String prefix, String name, Object value);
  }
}
