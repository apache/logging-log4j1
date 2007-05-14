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
import com.sun.jdmk.comm.HtmlAdaptorServer;

import org.apache.log4j.Logger;


public class Agent {

  static Logger log = Logger.getLogger(Agent.class);

  public Agent() {
  }


  public
  void start() {

    MBeanServer server = MBeanServerFactory.createMBeanServer();
    HtmlAdaptorServer html = new HtmlAdaptorServer();

    try {
      log.info("Registering HtmlAdaptorServer instance.");
      server.registerMBean(html, new ObjectName("Adaptor:name=html,port=8082"));
      log.info("Registering HierarchyDynamicMBean instance.");
      HierarchyDynamicMBean hdm = new HierarchyDynamicMBean();
      server.registerMBean(hdm, new ObjectName("log4j:hiearchy=default"));

    } catch(Exception e) {
      log.error("Problem while regitering MBeans instances.", e);
      return;
    }
    html.start();
  }
}
