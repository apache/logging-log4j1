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

import org.apache.ugli.impl.NOPLoggerFA;

import java.io.InputStream;

import java.util.Properties;


/**
 * The <code>LoggerFactory</code> can produce Loggers for various logging APIs,
 * most notably for log4j, JDK 1.4 logging. Other implemenations such as
 * {@link org.apache.ugli.impl.NOPLogger NOPLogger} and
 * {@link org.apache.ugli.impl.SimpleLogger SimpleLogger} are also supported.
 *
 * @author Ceki G&uuml;lc&uuml;
 */
public class LoggerFactory {
  static LoggerFactoryAdapter adapter;

  static {
    String adapterClassStr = "@ADAPTER_CLASS@";
    System.out.println("Built for "+adapterClassStr);
    try {
      adapter = new @ADAPTER_CLASS@();
    } catch (ClassNotFoundException cnfe) {
      System.err.println("Could not find class [" + adapterClassStr + "]");
      cnfe.printStackTrace();
    } catch (Exception e) {
      System.err.println(
        "Could not instantiate instance of class [" + adapterClassStr + "]");
      e.printStackTrace();
    }

    if (adapter == null) {
      // TODO consider falling back on something more meaningful
      adapter = new NOPLoggerFA();
    }
  }

  public static ULogger getLogger(String name) {
    return adapter.getLogger(name);
  }

  public static ULogger getLogger(String domainName, String subDomainName) {
    return adapter.getLogger(domainName, subDomainName);
  }

  public static ULogger getLogger(Class clazz) {
    return adapter.getLogger(clazz.getName());
  }

  public static ULogger getLogger(Class clazz, String subDomainName) {
    return adapter.getLogger(clazz.getName(), subDomainName);
  }
}
