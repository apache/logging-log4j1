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


// Contributors: Mathias Rupprecht <mmathias.rupprecht@fja.com>
package org.apache.log4j.spi.location;

import org.apache.log4j.helpers.LogLog;


/**
   The internal representation of caller location information.

   @since 0.8.3
*/
public class LocationInfo implements java.io.Serializable {

  /**
     When location information is not available the constant
     <code>NA</code> is returned. Current value of this string
     constant is <b>?</b>.  */
  public static final String NA = "?";
  
  static final long serialVersionUID = -1325822038990805636L;
  
  private static boolean haveStackTraceElement = false;
  
  /**
   * NA_LOCATION_INFO is used in conjunction with deserialized LoggingEvents 
   * without real location info available.
   * @since 1.3
   */
  public static LocationInfo NA_LOCATION_INFO = new LocationInfo(NA, NA, NA, NA);
 
  static {
    try {
      Class.forName("java.lang.StackTraceElement");
      haveStackTraceElement = true;
    } catch ( Throwable e) {
      // we are running on a JDK prior to 1.4
    } 
  }
  
  /**
     Caller's line number.
  */
  String lineNumber;

  /**
     Caller's file name.
  */
  String fileName;

  /**
     Caller's fully qualified class name.
  */
  String className;

  /**
     Caller's method name.
  */
  String methodName;

  /**
     All available caller information, in the format
     <code>fully.qualified.classname.of.caller.methodName(Filename.java:line)</code>
    */
  transient String fullInfo;

  public LocationInfo(
    String fileName, String className, String methodName, String lineNumber) {
    this.fileName = fileName;
    this.className = className;
    this.methodName = methodName;
    this.lineNumber = lineNumber;
  }

  
  /**
     Instantiate location information based on a Throwable. We
     expect the Throwable <code>t</code>, to be in the format

       <pre>
        java.lang.Throwable
        ...
          at org.apache.log4j.PatternLayout.format(PatternLayout.java:413)
          at org.apache.log4j.FileAppender.doAppend(FileAppender.java:183)
        at org.apache.log4j.Category.callAppenders(Category.java:131)
        at org.apache.log4j.Category.log(Category.java:512)
        at callers.fully.qualified.className.methodName(FileName.java:74)
        ...
       </pre>

       <p>However, we can also deal with JIT compilers that "lose" the
       location information, especially between the parentheses.

    */
  public LocationInfo(Throwable t, String fqnOfCallingClass) {
    if (t == null) {
      return;
    }
    
    if(haveStackTraceElement) {
      StackTraceElementExtractor.extract(this, t, fqnOfCallingClass);
    } else {
      LegacyExtractor.extract(this, t, fqnOfCallingClass);  
    }
  }

  public boolean equals(Object o) {
    LogLog.info("equals called");
    if (this == o) {
      return true;
    }

    if (!(o instanceof LocationInfo)) {
      LogLog.info("inequality point 1");
      return false;
    }

    LocationInfo r = (LocationInfo) o;

    if(!getClassName().equals(r.getClassName())){
     LogLog.info("inequality point 2");
      return false;
    }
    
    if(!getFileName().equals(r.getFileName())) {
     LogLog.info("inequality point 3");
      return false;
    }

    if(!getMethodName().equals(r.getMethodName())){
     LogLog.info("inequality point 4");
      return false;
    }
    
    if(!getLineNumber().equals(r.getLineNumber())){
     LogLog.info("inequality point 5");
      return false;
    }
        
    return true;
  }

  
  /**
     Return the fully qualified class name of the caller making the
     logging request.
  */
  public String getClassName() {
    return className;
  }

  /**
     Return the file name of the caller.

     <p>This information is not always available.
  */
  public String getFileName() {
    return fileName;
  }

  /**
     Returns the line number of the caller.

     <p>This information is not always available.
  */
  public String getLineNumber() {
    return lineNumber;
  }

  /**
     Returns the method name of the caller.
  */
  public String getMethodName() {
    return methodName;
  }
  
  /**
   * fullInfo format is:
   * <code>fully.qualified.classname.of.caller.methodName(Filename.java:line)</code>
   */
  public String getFullInfo() {
    if(fullInfo == null) {
      fullInfo = getClassName()+"."+getMethodName()+"("+getFileName()+":"+
      getLineNumber()+")";
    }
    return fullInfo;
  }
  
  public String toString() {
    return "(class="+getClassName()+", file="+getFileName()+", line="+getLineNumber()+", methodName="+getMethodName();
  }
}
