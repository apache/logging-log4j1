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
package org.apache.log4j;

import java.lang.ref.Reference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import junit.framework.TestCase;

/**
 * Test for MDC
 * 
 *  @author Maarten Bosteels
 */
public class MDCTestCase extends TestCase {

  public void setUp() {
    MDC.clear();
  }

  public void tearDown() {
    MDC.clear();
  }

  public void testPut() throws Exception {
    MDC.put("key", "some value");
    assertEquals("some value", MDC.get("key"));
    assertEquals(1, MDC.getContext().size());
  }
  
  public void testRemoveLastKey() throws Exception {
    MDC.put("key", "some value");

    MDC.remove("key");
    checkThreadLocalsForLeaks();
  }

  private void checkThreadLocalsForLeaks() throws Exception {

      // this code is heavily based on code in org.apache.catalina.loader.WebappClassLoader

      // Make the fields in the Thread class that store ThreadLocals accessible    
      Field threadLocalsField = Thread.class.getDeclaredField("threadLocals");
      threadLocalsField.setAccessible(true);
      Field inheritableThreadLocalsField = Thread.class.getDeclaredField("inheritableThreadLocals");
      inheritableThreadLocalsField.setAccessible(true);
      // Make the underlying array of ThreadLoad.ThreadLocalMap.Entry objects accessible
      Class tlmClass = Class.forName("java.lang.ThreadLocal$ThreadLocalMap");
      Field tableField = tlmClass.getDeclaredField("table");
      tableField.setAccessible(true);

      Thread thread = Thread.currentThread();

      Object threadLocalMap;
      threadLocalMap = threadLocalsField.get(thread);
      // Check the first map
      checkThreadLocalMapForLeaks(threadLocalMap, tableField);
      // Check the second map
      threadLocalMap = inheritableThreadLocalsField.get(thread);
      checkThreadLocalMapForLeaks(threadLocalMap, tableField);

  }

  private void checkThreadLocalMapForLeaks(Object map, Field internalTableField) 
          throws IllegalAccessException, NoSuchFieldException {
    if (map != null) {
      Object[] table = (Object[]) internalTableField.get(map);
      if (table != null) {
        for (int j =0; j < table.length; j++) {
          if (table[j] != null) {

            // Check the key
            Object key = ((Reference) table[j]).get();
            String keyClassName = key.getClass().getName();

            if (key.getClass() == org.apache.log4j.helpers.ThreadLocalMap.class) {
              fail("Found a ThreadLocal with key of type [" + keyClassName + "]");
            }
          }
        }
      }
    }
  }
}
