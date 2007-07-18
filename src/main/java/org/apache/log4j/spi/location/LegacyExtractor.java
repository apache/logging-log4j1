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

import org.apache.log4j.Layout;
import org.apache.log4j.helpers.PlatformInfo;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.log4j.spi.LocationInfo;


/**
 * Extract location information from a throwable. The techniques used here
 * work on all JDK platforms including those prior to JDK 1.4.
 * 
 * @since 1.3
 * @author Ceki G&uuml;lc&uuml;
 */
public class LegacyExtractor {


  private static StringWriter sw = new StringWriter();
  private static PrintWriter pw = new PrintWriter(sw);

  private LegacyExtractor() {
  }

  static public void extract(LocationInfo li, Throwable t, String fqnOfInvokingClass) {
    // on AS400, package path separator in stack trace is not dot '.', 
    // but slash '/'
    if (PlatformInfo.isOnAS400()) {
      fqnOfInvokingClass = fqnOfInvokingClass.replace('.', '/');
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

    // Given the current structure of log4j, the line 
    // containing 'fqnOfCallingClass', usually "org.apache.log4j.Logger." 
    // should be printed just before the caller.
    // This method of searching may not be fastest but it's safer
    // than counting the stack depth which is not guaranteed to be
    // constant across JVM implementations.
    ibegin = s.lastIndexOf(fqnOfInvokingClass);

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
    // need to skip the inital 'at'. The same applied to AS400.
    if ((!PlatformInfo.isInVisualAge()) && (!PlatformInfo.isOnAS400())) {
      // back up to first blank character
      ibegin = s.lastIndexOf("at ", iend);

      if (ibegin == -1) {
        return;
      }

      // Add 3 to skip "at ";
      ibegin += 3;
    }

    // everything between is the requested stack item
    li.fullInfo = s.substring(ibegin, iend);
    setFileName(li, li.fullInfo );
    setClassName(li, li.fullInfo );
    setMethodName(li, li.fullInfo );
    setLineNumber(li, li.fullInfo );
  }

  /**
   * Make a best-effort attemt at setting the fike name of the caller. 
   * This information may not always be available.
  */
  static void setFileName(LocationInfo li, String fullInfo) {
    if (fullInfo == null) {
      li.fileName = LocationInfo.NA;
    } else {
      int iend = fullInfo.lastIndexOf(':');

      if (iend == -1) {
        li.fileName = LocationInfo.NA;
      } else {
        int ibegin = fullInfo.lastIndexOf('(', iend - 1);
        li.fileName = fullInfo.substring(ibegin + 1, iend);
      }
    }
  }

  /**
   * Make a best-effort attemt at setting the class name of the caller. 
   * This information may not always be available.
  */
  static void setClassName(LocationInfo li, String fullInfo) {
    if (fullInfo == null) {
      li.className = LocationInfo.NA;
      return;
    }

    // Starting the search from '(' is safer because there is
    // potentially a dot between the parentheses.
    int iend = fullInfo.lastIndexOf('(');

    if (iend == -1) {
      li.className = LocationInfo.NA;
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

      if (PlatformInfo.isInVisualAge()) {
        ibegin = fullInfo.lastIndexOf(' ', iend) + 1;
      }

      if (iend == -1) {
        li.className = LocationInfo.NA;
      } else {
        li.className = fullInfo.substring(ibegin, iend);
      }
    }
  }

  /**
   * Make a best-effort attemt at setting the line number of the caller. 
   * This information may not always be available.
  */
  static void setLineNumber(LocationInfo li, String fullInfo) {
    if (fullInfo == null) {
      li.lineNumber = LocationInfo.NA;
    } else {
      int iend = fullInfo.lastIndexOf(')');
      int ibegin = fullInfo.lastIndexOf(':', iend - 1);

      if (ibegin == -1) {
        li.lineNumber = LocationInfo.NA;
      } else {
        li.lineNumber = fullInfo.substring(ibegin + 1, iend);
      }
    }
  }

  /**
   * Make a best-effort attemt at setting the method name of the caller. 
   * This information may not always be available.
  */
  static void setMethodName(LocationInfo li, String fullInfo) {
    if (fullInfo == null) {
      li.methodName = LocationInfo.NA;
    } else {
      int iend = fullInfo.lastIndexOf('(');
      int ibegin = fullInfo.lastIndexOf('.', iend);

      if (ibegin == -1) {
        li.methodName = LocationInfo.NA;
      } else {
        li.methodName = fullInfo.substring(ibegin + 1, iend);
      }
    }
  }
}
