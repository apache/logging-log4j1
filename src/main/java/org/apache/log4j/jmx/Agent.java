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

import org.apache.log4j.Logger;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import java.lang.reflect.InvocationTargetException;
import java.io.InterruptedIOException;


/**
 * Manages an instance of com.sun.jdmk.comm.HtmlAdapterServer in Log4j up to 1.2.17.
 *
 * Changed in 1.2.18+ to complain about its use and do nothing else.
 * See <a href="https://logging.apache.org/log4j/1.2/">the log4j 1.2 homepage</a>
 * for more information on why this class is disabled since 1.2.18.
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
     * @deprecated
     */
  public Agent() {
  }

    /**
     * Creates a new instance of com.sun.jdmk.comm.HtmlAdapterServer
     * using reflection.
     *
     * @since 1.2.16
     * @return new instance.
     * @deprecated
     */
  private static Object createServer() {
    throw new RuntimeException("JMX / HtmlAdapterServer no longer supported");
  }

    /**
     * Invokes HtmlAdapterServer.start() using reflection.
     *
     * @since 1.2.16
     * @param server instance of com.sun.jdmk.comm.HtmlAdapterServer.
     * @deprecated
     */
  private static void startServer(final Object server) {
    throw new RuntimeException("JMX / HtmlAdapterServer no longer supported");
  }


    /**
     * Starts instance of HtmlAdapterServer.
     * @deprecated
     */
  public void start() {
    throw new RuntimeException("JMX / HtmlAdapterServer no longer supported");
  }
}
