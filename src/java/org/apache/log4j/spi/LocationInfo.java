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
package org.apache.log4j.spi;

import org.apache.log4j.Layout;
import org.apache.log4j.helpers.LogLog;

import sun.security.action.GetLongAction;

import java.io.PrintWriter;
import java.io.StringWriter;


/**
   The internal representation of caller location information.

   @since 0.8.3
*/
public class LocationInfo implements java.io.Serializable {
  private static StringWriter sw = new StringWriter();
  private static PrintWriter pw = new PrintWriter(sw);

  /**
     When location information is not available the constant
     <code>NA</code> is returned. Current value of this string
     constant is <b>?</b>.  */
  public static final String NA = "?";
  static final long serialVersionUID = -1325822038990805636L;

  /**
   * NA_LOCATION_INFO is used in conjunction with deserialized LoggingEvents 
   * without real location info available.
   * @since 1.3
   */
  public static LocationInfo NA_LOCATION_INFO = new LocationInfo(NA, NA, NA, NA);
  
  // Check if we are running in IBM's visual age.
  static boolean inVisualAge = false;

  static {
    try {
      Class dummy = Class.forName("com.ibm.uvm.tools.DebugSupport");
      inVisualAge = true;
      LogLog.debug("Detected IBM VisualAge environment.");
    } catch (Throwable e) {
      ; // nothing to do
    }
  }

  /**
     Caller's line number.
  */
  transient String lineNumber;

  /**
     Caller's file name.
  */
  transient String fileName;

  /**
     Caller's fully qualified class name.
  */
  transient String className;

  /**
     Caller's method name.
  */
  transient String methodName;

  /**
     All available caller information, in the format
     <code>fully.qualified.classname.of.caller.methodName(Filename.java:line)</code>
    */
  public String fullInfo;

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

    String s;

    // Protect against multiple access to sw.
    synchronized (sw) {
      t.printStackTrace(pw);
      s = sw.toString();
      sw.getBuffer().setLength(0);
    }
    
    //System.out.println("s is ["+s+"].");
    int ibegin;

    //System.out.println("s is ["+s+"].");
    int iend;

    // Given the current structure of the package, the line
    // containing "org.apache.log4j.Category." should be printed just
    // before the caller.
    // This method of searching may not be fastest but it's safer
    // than counting the stack depth which is not guaranteed to be
    // constant across JVM implementations.
    ibegin = s.lastIndexOf(fqnOfCallingClass);

    if (ibegin == -1) {
      return;
    }

    ibegin = s.indexOf(Layout.LINE_SEP, ibegin);

    if (ibegin == -1) {
      return;
    }

    ibegin += Layout.LINE_SEP_LEN;

    // determine end of line
    iend = s.indexOf(Layout.LINE_SEP, ibegin);

    if (iend == -1) {
      return;
    }

    // VA has a different stack trace format which doesn't
    // need to skip the inital 'at'
    if (!inVisualAge) {
      // back up to first blank character
      ibegin = s.lastIndexOf("at ", iend);

      if (ibegin == -1) {
        return;
      }

      // Add 3 to skip "at ";
      ibegin += 3;
    }

    // everything between is the requested stack item
    this.fullInfo = s.substring(ibegin, iend);
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
    if (
      (className == null) && (fullInfo == null)) {
      return NA;
    }

    if (className == null) {
      // Starting the search from '(' is safer because there is
      // potentially a dot between the parentheses.
      int iend = fullInfo.lastIndexOf('(');

      if (iend == -1) {
        className = NA;
      } else {
        iend = fullInfo.lastIndexOf('.', iend);

        // This is because a stack trace in VisualAge looks like:
        //java.lang.RuntimeException
        //  java.lang.Throwable()
        //  java.lang.Exception()
        //  java.lang.RuntimeException()
        //  void test.test.B.print()
        //  void test.test.A.printIndirect()
        //  void test.test.Run.main(java.lang.String [])
        int ibegin = 0;

        if (inVisualAge) {
          ibegin = fullInfo.lastIndexOf(' ', iend) + 1;
        }

        if (iend == -1) {
          className = NA;
        } else {
          className = this.fullInfo.substring(ibegin, iend);
        }
      }
    }

    return className;
  }

  /**
     Return the file name of the caller.

     <p>This information is not always available.
  */
  public String getFileName() {
    if ((fileName == null) && (fullInfo == null)) {
      return NA;
    }

    if (fileName == null) {
      int iend = fullInfo.lastIndexOf(':');

      if (iend == -1) {
        fileName = NA;
      } else {
        int ibegin = fullInfo.lastIndexOf('(', iend - 1);
        fileName = this.fullInfo.substring(ibegin + 1, iend);
      }
    }

    return fileName;
  }

  /**
     Returns the line number of the caller.

     <p>This information is not always available.
  */
  public String getLineNumber() {
    if (
      (lineNumber == null) && (fullInfo == null)) {
      return NA;
    }

    if (lineNumber == null) {
      int iend = fullInfo.lastIndexOf(')');
      int ibegin = fullInfo.lastIndexOf(':', iend - 1);

      if (ibegin == -1) {
        lineNumber = NA;
      } else {
        lineNumber = this.fullInfo.substring(ibegin + 1, iend);
      }
    }

    return lineNumber;
  }

  /**
     Returns the method name of the caller.
  */
  public String getMethodName() {
    if (
      (methodName == null) && (fullInfo == null)) {
      return NA;
    }

    if (methodName == null) {
      int iend = fullInfo.lastIndexOf('(');
      int ibegin = fullInfo.lastIndexOf('.', iend);

      if (ibegin == -1) {
        methodName = NA;
      } else {
        methodName = this.fullInfo.substring(ibegin + 1, iend);
      }
    }

    return methodName;
  }
  
  public String toString() {
    return "(class="+getClassName()+", file="+getFileName()+", line="+getLineNumber()+", methodName="+getMethodName();
  }
}
