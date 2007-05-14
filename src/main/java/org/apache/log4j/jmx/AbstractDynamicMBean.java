/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.log4j.jmx;

//import java.lang.reflect.Constructor;
import java.util.Iterator;
import javax.management.DynamicMBean;
import javax.management.AttributeList;
import javax.management.Attribute;
import javax.management.RuntimeOperationsException;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.log4j.Logger;

public abstract class AbstractDynamicMBean implements DynamicMBean,
                                                      MBeanRegistration {

  String dClassName;
  MBeanServer server;

  /**
   * Enables the to get the values of several attributes of the Dynamic MBean.
   */
  public
  AttributeList getAttributes(String[] attributeNames) {

    // Check attributeNames is not null to avoid NullPointerException later on
    if (attributeNames == null) {
      throw new RuntimeOperationsException(
			   new IllegalArgumentException("attributeNames[] cannot be null"),
			   "Cannot invoke a getter of " + dClassName);
    }

    AttributeList resultList = new AttributeList();

    // if attributeNames is empty, return an empty result list
    if (attributeNames.length == 0)
      return resultList;

    // build the result attribute list
    for (int i=0 ; i<attributeNames.length ; i++){
      try {
	Object value = getAttribute((String) attributeNames[i]);
	resultList.add(new Attribute(attributeNames[i],value));
      } catch (Exception e) {
	e.printStackTrace();
      }
    }
    return(resultList);
  }

  /**
   * Sets the values of several attributes of the Dynamic MBean, and returns the
   * list of attributes that have been set.
   */
  public AttributeList setAttributes(AttributeList attributes) {

    // Check attributes is not null to avoid NullPointerException later on
    if (attributes == null) {
      throw new RuntimeOperationsException(
                    new IllegalArgumentException("AttributeList attributes cannot be null"),
		    "Cannot invoke a setter of " + dClassName);
    }
    AttributeList resultList = new AttributeList();

    // if attributeNames is empty, nothing more to do
    if (attributes.isEmpty())
      return resultList;

    // for each attribute, try to set it and add to the result list if successfull
    for (Iterator i = attributes.iterator(); i.hasNext();) {
      Attribute attr = (Attribute) i.next();
      try {
	setAttribute(attr);
	String name = attr.getName();
	Object value = getAttribute(name);
	resultList.add(new Attribute(name,value));
      } catch(Exception e) {
	e.printStackTrace();
      }
    }
    return(resultList);
  }

  protected
  abstract
  Logger getLogger();

  public
  void postDeregister() {
    getLogger().debug("postDeregister is called.");
  }

  public
  void postRegister(java.lang.Boolean registrationDone) {
  }



  public
  void preDeregister() {
    getLogger().debug("preDeregister called.");
  }

  public
  ObjectName preRegister(MBeanServer server, ObjectName name) {
    getLogger().debug("preRegister called. Server="+server+ ", name="+name);
    this.server = server;
    return name;
  }



}
