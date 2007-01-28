/*
 * Copyright 1999-2005 The Apache Software Foundation.
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

package org.apache.log4j.jmx;


import java.lang.reflect.Constructor;
import org.apache.log4j.*;

import org.apache.log4j.spi.LoggerEventListener;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.LoggerRepositoryEx;
import org.apache.log4j.helpers.OptionConverter;

import java.util.Enumeration;
import java.util.Vector;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;

import javax.management.ObjectName;
import javax.management.MBeanInfo;
import javax.management.Attribute;

import javax.management.MBeanException;
import javax.management.AttributeNotFoundException;
import javax.management.RuntimeOperationsException;
import javax.management.ReflectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationBroadcaster;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.NotificationFilter;
import javax.management.NotificationFilterSupport;
import javax.management.ListenerNotFoundException;

public class HierarchyDynamicMBean extends AbstractDynamicMBean
                                   implements LoggerEventListener,
                                              NotificationBroadcaster {

  private static Logger log = Logger.getLogger(HierarchyDynamicMBean.class);

  private static final String ADD_APPENDER = "addAppender.";
  private static final String REMOVE_APPENDER = "removeAppender.";
  private static final String THRESHOLD = "threshold";

  private MBeanConstructorInfo[] dConstructors = new MBeanConstructorInfo[2];
  private MBeanOperationInfo[] dOperations = new MBeanOperationInfo[2];

  private Vector vAttributes = new Vector();
  private String dClassName = this.getClass().getName();
  private String dDescription =
     "This MBean acts as a management facade for org.apache.log4j.Hierarchy.";

  private NotificationBroadcasterSupport nbs = new NotificationBroadcasterSupport();
  private LoggerRepository repository;

  /**
   * Construct with a default repository.
   */
  public HierarchyDynamicMBean() {
    this(LogManager.getLoggerRepository());
  }

  /**
   * Construct with a logger repository.
   * @param repository cannot be null
   */
  public HierarchyDynamicMBean(LoggerRepository repository) {
    if (repository == null)
      throw new NullPointerException();
    this.repository = repository;
    buildDynamicMBeanInfo();
  }

  private
  void buildDynamicMBeanInfo() {
    Constructor[] constructors = this.getClass().getConstructors();
    String cdesc = "Constructs a HierarchyDynamicMBean instance";
    dConstructors[0] = new MBeanConstructorInfo(
         cdesc,
	 constructors[0]);
    dConstructors[1] = new MBeanConstructorInfo(
         cdesc,
         constructors[1]);
    
    vAttributes.add(new MBeanAttributeInfo(THRESHOLD,
					   "java.lang.String",
					   "The \"threshold\" state of the hiearchy.",
					   true,
					   true,
					   false));

    MBeanParameterInfo[] params = new MBeanParameterInfo[1];
    params[0] = new MBeanParameterInfo("name", "java.lang.String",
				       "Create a logger MBean" );
    dOperations[0] = new MBeanOperationInfo("addLoggerMBean",
				    "add a loggerMBean",
				    params ,
				    "javax.management.ObjectName",
				    MBeanOperationInfo.ACTION);
    dOperations[1] = new MBeanOperationInfo("addLoggerMBeans",
            "add a loggerMBean for all loggers",
            new MBeanParameterInfo[0],
            null,
            MBeanOperationInfo.ACTION);
    
  }

  /**
   * Adds a logger MBean, returning a new {@link ObjectName} or returning null if the logger does not exist.
   * @param name name of the logger.
   * @return
   */
  public ObjectName addLoggerMBean(String name) {
    Logger log = repository.exists(name);
    if (log != null) {
      return addLoggerMBean(log);
    } else {
      return null;
    }
  }

  /**
   * Adds a logger MBean for all loggers.
   */
  public
  void addLoggerMBeans() {
    Enumeration e = repository.getCurrentLoggers();
    while (e.hasMoreElements()) {
      Logger l = (Logger)e.nextElement();
      addLoggerMBean(l);
    }
  }

  /**
   * Adds a logger MBean by Logger, returning a new registered MBean.
   */
  public ObjectName addLoggerMBean(Logger logger) {
    String name = logger.getName();
    ObjectName objectName = null;
    try {
      LoggerDynamicMBean loggerMBean = new LoggerDynamicMBean(logger);
      objectName = new ObjectName(getObjectName().getDomain(), "logger", name);
      getServer().registerMBean(loggerMBean, objectName);

      NotificationFilterSupport nfs = new NotificationFilterSupport();
      nfs.enableType(ADD_APPENDER+logger.getName());

      log.debug("---Adding logger ["+name+"] as listener.");

      nbs.addNotificationListener(loggerMBean, nfs, null);


      vAttributes.add(new MBeanAttributeInfo("logger_"+name,
					     "javax.management.ObjectName",
					     "The "+name+" logger.",
					     true,
					     true, // this makes the object
					     // clickable
					     false));

    } catch(Exception e) {
      log.error("Could not add loggerMBean for ["+name+"].", e);
    }
    return objectName;
  }

  public
  void addNotificationListener(NotificationListener listener,
			       NotificationFilter filter,
			       java.lang.Object handback) {
    nbs.addNotificationListener(listener, filter, handback);
  }

  protected
  Logger getLogger() {
    return log;
  }

  public
  MBeanInfo getMBeanInfo() {
    //cat.debug("getMBeanInfo called.");

    MBeanAttributeInfo[] attribs = new MBeanAttributeInfo[vAttributes.size()];
    vAttributes.toArray(attribs);

    return new MBeanInfo(dClassName,
			 dDescription,
			 attribs,
			 dConstructors,
			 dOperations,
			 new MBeanNotificationInfo[0]);
  }

  public
  MBeanNotificationInfo[] getNotificationInfo(){
    return nbs.getNotificationInfo();
  }

  public
  Object invoke(String operationName,
		Object params[],
		String signature[]) throws MBeanException,
                                           ReflectionException {

    if (operationName == null) {
      throw new RuntimeOperationsException(
        new IllegalArgumentException("Operation name cannot be null"),
	"Cannot invoke a null operation in " + dClassName);
    }
    // Check for a recognized operation name and call the corresponding operation

    if( operationName.equals("addLoggerMBean")) {
      return addLoggerMBean((String)params[0]);
    } else if (operationName.equals("addLoggerMBeans")) {
      addLoggerMBeans();
      return null;
    } else {
      throw new ReflectionException(
	    new NoSuchMethodException(operationName),
	    "Cannot find the operation " + operationName + " in " + dClassName);
    }

  }


  public
  Object getAttribute(String attributeName) throws AttributeNotFoundException,
                                                    MBeanException,
                                                    ReflectionException {

    // Check attributeName is not null to avoid NullPointerException later on
    if (attributeName == null) {
      throw new RuntimeOperationsException(new IllegalArgumentException(
			"Attribute name cannot be null"),
       "Cannot invoke a getter of " + dClassName + " with null attribute name");
    }

    log.debug("Called getAttribute with ["+attributeName+"].");

    // Check for a recognized attributeName and call the corresponding getter
    if (attributeName.equals(THRESHOLD)) {
      return repository.getThreshold();
    } else if(attributeName.startsWith("logger")) {
      int k = attributeName.indexOf("%3D");
      String val = attributeName;
      if(k > 0) {
	val = attributeName.substring(0, k)+'='+ attributeName.substring(k+3);
      }
      try {
	return new ObjectName("log4j:"+val);
      } catch(Exception e) {
	log.error("Could not create ObjectName" + val);
      }
    }



    // If attributeName has not been recognized throw an AttributeNotFoundException
    throw(new AttributeNotFoundException("Cannot find " + attributeName +
					 " attribute in " + dClassName));

  }

  public void levelChangedEvent(Logger logger) {
    // TODO Auto-generated method stub    
  }

  public void appenderAddedEvent(Logger logger, Appender appender) {
    log.debug("addAppenderEvent called: logger="+logger.getName()+
	      ", appender="+appender.getName());
    Notification n = new Notification(ADD_APPENDER+logger.getName(), this, 0);
    n.setUserData(appender);
    log.debug("sending notification.");
    nbs.sendNotification(n);
  }

  public void appenderRemovedEvent(Logger logger, Appender appender) {
    log.debug("removeAppenderCalled: logger=" + logger.getName()+
	      ", appender="+appender.getName());
    Notification n = new Notification(REMOVE_APPENDER+logger.getName(), this, 0);
    n.setUserData(appender);
    log.debug("sending notification.");
    nbs.sendNotification(n);
  }

  public
  void postRegister(java.lang.Boolean registrationDone) {
    log.debug("postRegister is called.");
    ((LoggerRepositoryEx)repository).addLoggerEventListener(this);
    Logger root = repository.getRootLogger();
    addLoggerMBean(root);
  }
  
  public
  void removeNotificationListener(NotificationListener listener)
                                         throws ListenerNotFoundException {
    nbs.removeNotificationListener(listener);
  }

  public
  void setAttribute(Attribute attribute) throws AttributeNotFoundException,
                                                InvalidAttributeValueException,
                                                MBeanException,
                                                ReflectionException {

    // Check attribute is not null to avoid NullPointerException later on
    if (attribute == null) {
      throw new RuntimeOperationsException(
                  new IllegalArgumentException("Attribute cannot be null"),
	  "Cannot invoke a setter of "+dClassName+" with null attribute");
    }
    String name = attribute.getName();
    Object value = attribute.getValue();

    if (name == null) {
      throw new RuntimeOperationsException(
               new IllegalArgumentException("Attribute name cannot be null"),
	       "Cannot invoke the setter of "+dClassName+
	       " with null attribute name");
    }

    if(name.equals(THRESHOLD)) {
      Level l = OptionConverter.toLevel((String) value,
					   repository.getThreshold());
      repository.setThreshold(l);
    }
  }

}
