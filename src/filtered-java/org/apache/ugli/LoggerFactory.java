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

// WARNING
// WARNING Modifications MUST be made to the original file found at
// WARNING $LOG4J_HOME/src/filtered-java/org/apache/ugli/LoggerFactory.java
// WARNING

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

  //WARNING Modify the original in
  //        $LOG4J_HOME/src/filtered-java/org/apache/ugli/
  
  static {
    String adapterClassStr = "org.apache.ugli.impl.@IMPL@LoggerFA";
    System.out.println("UGLI built for "+adapterClassStr);
    try {
      adapter = new org.apache.ugli.impl.@IMPL@LoggerFA();
    } catch (Exception e) {
      // unless there was a problem with the build or the JVM we will never
      // get exceptions
      System.err.println(
        "Could not instantiate instance of class [" + adapterClassStr + "]");
      e.printStackTrace();
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
