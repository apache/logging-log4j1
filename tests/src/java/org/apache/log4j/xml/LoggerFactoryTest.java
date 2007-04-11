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
package org.apache.log4j.xml;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.joran.JoranConfigurator;
import org.apache.log4j.spi.LoggerFactory;

import junit.framework.TestCase;

public class LoggerFactoryTest extends TestCase {
  
  static boolean pass = false;
  
  public static class Factory implements LoggerFactory {

    public Logger makeNewLoggerInstance(String name) {
      pass = true;
      return new MyLogger(name);
    }
    
  }
  
  public static class MyLogger extends Logger {

    protected MyLogger(String name) {
      super(name);
    }
    
  }
  
  public void testSelectLogFactory()
  {
    JoranConfigurator jc = new JoranConfigurator();
    jc.doConfigure("input/xml/loggerFactory1.xml", LogManager.getLoggerRepository());
    Logger l = Logger.getLogger("x");
    assertEquals(MyLogger.class, l.getClass());
    assertEquals(true, pass);
    pass = false;
    
    jc.doConfigure("input/xml/loggerFactory2.xml", LogManager.getLoggerRepository());
    Logger.getLogger("xy");
    assertEquals(true, pass);
  }
  
}
