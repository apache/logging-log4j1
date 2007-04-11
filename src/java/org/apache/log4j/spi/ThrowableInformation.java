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

package org.apache.log4j.spi;

import org.apache.log4j.helpers.PlatformInfo;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * ThrowableInformation is log4j's internal representation of throwables. It
 * essentially consists of a string array, called 'rep', where the first
 * element, that is rep[0], represents the string representation of the
 * throwable (i.e. the value you get when you do throwable.toString()) and
 * subsequent elements correspond the stack trace with the top most entry of the
 * stack corresponding to the second entry of the 'rep' array that is rep[1].
 *
 * @author Ceki G&uuml;lc&uuml;
 *
 */
public class ThrowableInformation implements java.io.Serializable {
  static final long serialVersionUID = -4748765566864322735L;

  private transient Throwable throwable = null;
  private String[] rep;

  public ThrowableInformation(final Throwable throwable) {
    this.throwable = throwable;
    ArrayList lines = new ArrayList();
    extractStringRep(throwable, lines);
    rep = new String[lines.size()];
    lines.toArray(rep);
  }

  public ThrowableInformation(String[] rep) {
    this.rep = rep;
  }

    /**
     * Gets throwable.
     * @return throwable, may be null.
     * @deprecated
     */
  public Throwable getThrowable() {
     return throwable;
  }

    /**
     * Extract string representation of throwable.
     * @param t throwable, may not be null.
     * @param lines list to receive stack trace, may not be null.
     */
  private static void extractStringRep(final Throwable t, final List lines) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      t.printStackTrace(pw);
      pw.flush();
      LineNumberReader reader = new LineNumberReader(
              new StringReader(sw.toString()));
      try {
        String line = reader.readLine();
        while(line != null) {
          lines.add(line);
          line = reader.readLine();
        }
      } catch(IOException ex) {
          lines.add(ex.toString());
      }

    // Check if the Throwable t has a nested Throwable. If so, invoke
    // extractStringRep recursively.
    // Note that the Throwable.getCause was added in JDK 1.4. The printStackTrace
    // method was modified in JDK 1.4 to handle the nested throwable returned
    // by Throwable.getCause.
    try {
      Class tC = t.getClass();
      Method[] mA = tC.getMethods();
      Method nextThrowableMethod = null;
      for (int i = 0; i < mA.length; i++) {
        if (("getCause".equals(mA[i].getName()) && !PlatformInfo.isJDK14OrLater())
            || "getRootCause".equals(mA[i].getName())
            || "getNextException".equals(mA[i].getName())
            || "getException".equals(mA[i].getName())) {
          // check param types
          Class[] params = mA[i].getParameterTypes();
          if ((params == null) || (params.length == 0)) {
            // just found the getter for the nested throwable
            nextThrowableMethod = mA[i];
            break; // no need to search further
          }
        }
      }

      if (nextThrowableMethod != null) {
        // get the nested throwable and log it
        Throwable nextT =
          (Throwable) nextThrowableMethod.invoke(t, new Object[0]);
        if (nextT != null) {
          lines.add("Root cause follows.");
          extractStringRep(nextT, lines);
        }
      }
    } catch (Exception e) {
      // do nothing
    }
  }

  /**
   * Retun a clone of the string representation of the exceptopn (throwable)
   * that this object represents.
   */
  public String[] getThrowableStrRep() {
    return (String[]) rep.clone();
  }

  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (!(o instanceof ThrowableInformation)) {
      return false;
    }

    ThrowableInformation r = (ThrowableInformation) o;

    if (rep == null) {
      return (r.rep == null);
    }

    // at this point we know that both rep and r.rep are non-null.
    if (rep.length != r.rep.length) {
      return false;
    }

    int len = rep.length;
    for (int i = 0; i < len; i++) {
      if (!rep[i].equals(r.rep[i])) {
        return false;
      }
    }

    return true;
  }
}
