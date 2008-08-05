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

import javax.management.ObjectName;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;

import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;


/**
 * Manages an instance of com.sun.jdmk.comm.HtmlAdapterServer which
 * was provided for demonstration purposes in the
 * Java Management Extensions Reference Implementation 1.2.1.
 * This class is provided to maintain compatibility with earlier
 * versions of log4j and use in new code is discouraged.
 *
 * @deprecated
 */
public class Agent {

    /**
     * Diagnostic logger.
     * @deprecated
     */
  static Logger log = Logger.getLogger(Agent.class);

    /**
     * Create new instance.
     */
  public Agent() {
  }

    /**
     * Creates a new instance of com.sun.jdmk.comm.HtmlAdapterServer
     * using reflection.
     *
     * @since 1.2.16
     * @return new instance.
     */
  private static Object createServer() {
      Object newInstance = null;
      try {
        newInstance = Class.forName(
                "com.sun.jdmk.comm.HtmlAdapterServer").newInstance();
      } catch (ClassNotFoundException ex) {
          throw new NoClassDefFoundError(ex.toString());
      } catch (IllegalAccessException ex) {
          throw new IllegalAccessError(ex.toString());
      } catch (InstantiationException ex) {
          throw new InstantiationError(ex.toString());
      }
      return newInstance;
  }

    /**
     * Invokes HtmlAdapterServer.start() using reflection.
     *
     * @since 1.2.16
     * @param server instance of com.sun.jdmk.comm.HtmlAdapterServer.
     */
  private static void startServer(final Object server) {
      try {
          server.getClass().getMethod("start", new Class[0]).
                  invoke(server, new Object[0]);
      } catch(NoSuchMethodException ex) {
          throw new NoSuchMethodError(ex.toString());
      } catch(IllegalAccessException ex) {
          throw new IllegalAccessError(ex.toString());
      } catch(InvocationTargetException ex) {
          throw (RuntimeException) ex.getTargetException();
      }
  }


    /**
     * Starts instance of HtmlAdapterServer.
      */
  public void start() {

    MBeanServer server = MBeanServerFactory.createMBeanServer();
    Object html = createServer();

    try {
      log.info("Registering HtmlAdaptorServer instance.");
      server.registerMBean(html, new ObjectName("Adaptor:name=html,port=8082"));
      log.info("Registering HierarchyDynamicMBean instance.");
      HierarchyDynamicMBean hdm = new HierarchyDynamicMBean();
      server.registerMBean(hdm, new ObjectName("log4j:hiearchy=default"));

    } catch(Exception e) {
      log.error("Problem while registering MBeans instances.", e);
      return;
    }
    startServer(html);
  }
}
