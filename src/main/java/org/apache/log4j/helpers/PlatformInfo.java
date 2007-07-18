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

package org.apache.log4j.helpers;

import java.util.Properties;


/**
 * This class provides information about the runtime platform.
 *
 * @author Ceki Gulcu
 * @since 1.3
 * */
public class PlatformInfo {
  private static final int UNINITIALIZED = -1;

  // Check if we are running in IBM's visual age.
  private static int inVisualAge = UNINITIALIZED;
  private static int onAS400 = UNINITIALIZED;
  private static int hasStackTraceElement = UNINITIALIZED;

  public static boolean isInVisualAge() {
    if (inVisualAge == UNINITIALIZED) {
      try {
        Class dummy = Class.forName("com.ibm.uvm.tools.DebugSupport");
        inVisualAge = 1;
      } catch (Throwable e) {
        inVisualAge = 0;
      }
    }
    return (inVisualAge == 1);
  }

  /**
   * Are we running on AS400?
   */
  public static boolean isOnAS400() {
    if (onAS400 == UNINITIALIZED) {
      try {
        Properties p = System.getProperties();
        String osname = p.getProperty("os.name");
        if ((osname != null) && (osname.equals("OS/400"))) {
          onAS400 = 1;
        } else {
          onAS400 = 0;
        }
      } catch (Throwable e) {
        // This should not happen, but if it does, assume we are not on
        // AS400.
        onAS400 = 0;
      }
    }
    return (onAS400 == 1);
  }

  public static boolean hasStackTraceElement() {
    if (hasStackTraceElement == UNINITIALIZED) {
      try {
        Class.forName("java.lang.StackTraceElement");
        hasStackTraceElement = 1;
      } catch (Throwable e) {
        // we are running on a JDK prior to 1.4
        hasStackTraceElement = 0;
      }
    }
    return (hasStackTraceElement == 1);
  }
  
  public static boolean isJDK14OrLater() {
    return hasStackTraceElement();
  }
}
