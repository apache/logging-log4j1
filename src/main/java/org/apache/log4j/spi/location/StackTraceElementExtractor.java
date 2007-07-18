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

package org.apache.log4j.spi.location;
import org.apache.log4j.spi.LocationInfo;

import java.lang.reflect.Method;


/**
 * A faster extractor based on StackTraceElements introduced in JDK 1.4.
 *
 * The present code uses reflection. Thus, it should compile on all platforms.
 *
 * @author Martin Schulz
 * @author Ceki G&uuml;lc&uuml;
 *
 */
public class StackTraceElementExtractor {
  protected static boolean haveStackTraceElement = false;
  private static Method getStackTrace = null;
  private static Method getClassName = null;
  private static Method getFileName = null;
  private static Method getMethodName = null;
  private static Method getLineNumber = null;
  private static Object[] nullArgs = new Object[] {  };

  static {
    try {
      Class cStackTraceElement = Class.forName("java.lang.StackTraceElement");
      Class[] nullClassArray = new Class[] {  };
      getStackTrace =
        Throwable.class.getDeclaredMethod("getStackTrace", nullClassArray);
      getClassName =
        cStackTraceElement.getDeclaredMethod("getClassName", nullClassArray);
      getFileName =
        cStackTraceElement.getDeclaredMethod("getFileName", nullClassArray);
      getMethodName =
        cStackTraceElement.getDeclaredMethod("getMethodName", nullClassArray);
      getLineNumber =
        cStackTraceElement.getDeclaredMethod("getLineNumber", nullClassArray);
      haveStackTraceElement = true;
    } catch (Throwable e) {
      // we should never get here
    } 
  }

  static public void extract(LocationInfo li, Throwable t, String fqnOfInvokingClass) {
    if (t == null) {
      return;
    }
    
    Object location = null;
    try {
      Object[] stes = (Object[]) getStackTrace.invoke(t, nullArgs);

      boolean match = false;
      for (int i = 0; i < stes.length; i++) {
        if (((String) getClassName.invoke(stes[i], nullArgs)).equals(
            fqnOfInvokingClass)) {
          match = true;
        } else if(match) {
          location = stes[i];
          break;
        }        
      }
    } catch (Throwable e) {
      // Extraction failed, not much we could do now. We can't event log this 
      // failure because if there is one failure, there may be many others which 
      // are likely to follow. As location extraction is done on a best-effort 
      // basis, silence is preferable to overwhelming the user...
    }
    
    // If we failed to extract the location line, then default to LocationInfo.NA
    if(location == null) {
      li.className = LocationInfo.NA;
      li.fileName = LocationInfo.NA;
      li.lineNumber = LocationInfo.NA;
      li.methodName = LocationInfo.NA;
    } else {  // otherwise, get the real info    
      setClassName(li, location);
      setFileName(li, location);
      setMethodName(li, location);
      setLineNumber(li, location);
    }
  }

  /**
     Return the fully qualified class name of the caller making the
     logging request.
  */
  static void setClassName(LocationInfo li, Object location) {
    try {
      li.className = (String) getClassName.invoke(location, nullArgs);
    } catch (Throwable e) {
      li.className = LocationInfo.NA;
    } 
  }

  static void setFileName(LocationInfo li, Object location) {
    try {
      li.fileName = (String) getFileName.invoke(location, nullArgs);
    } catch (Throwable e) {
      li.fileName = LocationInfo.NA;
    }
  }

  static void setLineNumber(LocationInfo li, Object location) {
    Integer ln = null;
    try {
      ln = (Integer) getLineNumber.invoke(location, nullArgs);
      if (ln.intValue() >= 0) {
        li.lineNumber = ln.toString();
      }
    } catch (Throwable e) {
      li.lineNumber = LocationInfo.NA;
    }
  }

  static void setMethodName(LocationInfo li, Object location) {
    try {
      li.methodName = (String) getMethodName.invoke(location, nullArgs);
    } catch (Throwable e) {
      li.methodName = LocationInfo.NA;
    }
  }
}
