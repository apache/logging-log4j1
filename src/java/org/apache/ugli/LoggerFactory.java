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

package org.apache.ugli;

import java.io.InputStream;
import java.util.Properties;

import org.apache.ugli.impl.NOPLoggerFA;



/**
 * The <code>LoggerFactory</code> can produce Loggers for various logging APIs, 
 * most notably for log4j, JDK 1.4 logging. Other implemenations such as 
 * {@link org.apache.ugli.impl.NOPLogger NOPLogger} and 
 * {@link org.apache.ugli.impl.SimpleLogger SimpleLogger} are also supported.
 * 
 * @author Ceki G&uuml;lc&uuml;
 */
public class LoggerFactory {

  static final String UGLI_PROPERTIES ="ugli.properties";
  static final String UGLI_FACTORY_ADAPTER_CLASS ="ugli.factoryAdapterClass";
  
  static LoggerFactoryAdapter adapter;

  static {
    // the following code assumes that the jar file containing LoggerFactory
    // has properly packaged a ugli.propertis file specifiying the adapter 
    // factory class to use

    ClassLoader cl = LoggerFactoryAdapter.class.getClassLoader();
    InputStream is = cl.getResourceAsStream(UGLI_PROPERTIES);
    if(is == null) {
      System.err.println("Could not retreive ugli.properties");
    } else {
      Properties props = null;
      try {
        props = new Properties();
        props.load(is);
        is.close();
      } catch(java.io.IOException ie) {
      }
    
      String adapterClassStr = props.getProperty(UGLI_FACTORY_ADAPTER_CLASS); 
      try {
        Class adapterClass = cl.loadClass(adapterClassStr);
        adapter = (LoggerFactoryAdapter) adapterClass.newInstance();
      } catch(ClassNotFoundException cnfe) {
        System.err.println("Could not find class ["+adapterClassStr+"]");
        cnfe.printStackTrace();
      } catch(Exception e) {
        System.err.println("Could not instantiate instance of class ["+adapterClassStr+"]");
        e.printStackTrace();
      }
    }
    if(adapter == null) {
      // TODO consider falling back on something more meaningful
      adapter = new NOPLoggerFA();
    }
  }
  
  static public ULogger getLogger(String name) {
    return adapter.getLogger(name);
  }
  
  static public ULogger getLogger(String domainName, String subDomainName) {
    return adapter.getLogger(domainName, subDomainName);
  }
  
  static public ULogger getLogger(Class clazz) {
    return adapter.getLogger(clazz.getName());
  }
  static public ULogger getLogger(Class clazz, String subDomainName) {
    return adapter.getLogger(clazz.getName(), subDomainName);
  }
  
}
