/*
 * ============================================================================
 *                   The Apache Software License, Version 1.1
 * ============================================================================
 *
 *    Copyright (C) 1999 The Apache Software Foundation. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modifica-
 * tion, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of  source code must  retain the above copyright  notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any, must
 *    include  the following  acknowledgment:  "This product includes  software
 *    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
 *    Alternately, this  acknowledgment may  appear in the software itself,  if
 *    and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "log4j" and  "Apache Software Foundation"  must not be used to
 *    endorse  or promote  products derived  from this  software without  prior
 *    written permission. For written permission, please contact
 *    apache@apache.org.
 *
 * 5. Products  derived from this software may not  be called "Apache", nor may
 *    "Apache" appear  in their name,  without prior written permission  of the
 *    Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 * APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 * DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 * ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 * (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * This software  consists of voluntary contributions made  by many individuals
 * on  behalf of the Apache Software  Foundation.  For more  information on the
 * Apache Software Foundation, please see <http://www.apache.org/>.
 *
 */

package org.apache.log4j.helpers;

import java.lang.IllegalAccessException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.net.URL;


//import java.awt.Image;
//import java.awt.Toolkit;

/**
   Load resources (or images) from various sources.

  @author Ceki G&uuml;lc&uuml;
 */
public class Loader {
  static final String TSTR =
    "Caught Exception while in Loader.getResource. This may be innocuous.";

  // We conservatively assume that we are running under Java 1.x
  private static boolean java1 = true;
  private static boolean ignoreTCL = false;

  static {
    String prop = OptionConverter.getSystemProperty("java.version", null);

    if (prop != null) {
      int i = prop.indexOf('.');

      if (i != -1) {
        if (prop.charAt(i + 1) != '1') {
          java1 = false;
        }
      }
    }

    String ignoreTCLProp =
      OptionConverter.getSystemProperty("log4j.ignoreTCL", null);

    if (ignoreTCLProp != null) {
      ignoreTCL = OptionConverter.toBoolean(ignoreTCLProp, true);
    }
  }

  /**
     This method will search for <code>resource</code> in different
     places. The rearch order is as follows:

     <ol>

     <p><li>Search for <code>resource</code> using the thread context
     class loader under Java2. If that fails, search for
     <code>resource</code> using the class loader that loaded this
     class (<code>Loader</code>). Under JDK 1.1, only the the class
     loader that loaded this class (<code>Loader</code>) is used.

     <p><li>Try one last time with
     <code>ClassLoader.getSystemResource(resource)</code>, that is is
     using the system class loader in JDK 1.2 and virtual machine's
     built-in class loader in JDK 1.1.

     </ol>
  */
  public static URL getResource(String resource) {
    ClassLoader classLoader = null;
    URL url = null;

    try {
      if (!java1) {
        classLoader = getTCL();

        if (classLoader != null) {
          LogLog.debug(
            "Trying to find [" + resource + "] using context classloader "
            + classLoader + ".");
          url = classLoader.getResource(resource);

          if (url != null) {
            return url;
          }
        }
      }

      // We could not find resource. Ler us now try with the
      // classloader that loaded this class.
      classLoader = Loader.class.getClassLoader();

      if (classLoader != null) {
        LogLog.debug(
          "Trying to find [" + resource + "] using " + classLoader
          + " class loader.");
        url = classLoader.getResource(resource);

        if (url != null) {
          return url;
        }
      }
    } catch (Throwable t) {
      LogLog.warn(TSTR, t);
    }

    // Last ditch attempt: get the resource from the class path. It
    // may be the case that clazz was loaded by the Extentsion class
    // loader which the parent of the system class loader. Hence the
    // code below.
    LogLog.debug(
      "Trying to find [" + resource
      + "] using ClassLoader.getSystemResource().");

    return ClassLoader.getSystemResource(resource);
  }

  /**
     Are we running under JDK 1.x?
  */
  public static boolean isJava1() {
    return java1;
  }

  /**
    * Get the Thread Context Loader which is a JDK 1.2 feature. If we
    * are running under JDK 1.1 or anything else goes wrong the method
    * returns <code>null<code>.
    *
    *  */
  private static ClassLoader getTCL()
    throws IllegalAccessException, InvocationTargetException {
    // Are we running on a JDK 1.2 or later system?
    Method method = null;

    try {
      method = Thread.class.getMethod("getContextClassLoader", null);
    } catch (NoSuchMethodException e) {
      // We are running on JDK 1.1
      return null;
    }

    return (ClassLoader) method.invoke(Thread.currentThread(), null);
  }

  /**
   * If running under JDK 1.2 load the specified class using the
   *  <code>Thread</code> <code>contextClassLoader</code> if that
   *  fails try Class.forname. Under JDK 1.1 only Class.forName is
   *  used.
   *
   */
  public static Class loadClass(String clazz) throws ClassNotFoundException {
    // Just call Class.forName(clazz) if we are running under JDK 1.1
    // or if we are instructed to ignore the TCL.
    if (java1 || ignoreTCL) {
      return Class.forName(clazz);
    } else {
      try {
        return getTCL().loadClass(clazz);
      } catch (Throwable e) {
        // we reached here because tcl was null or because of a
        // security exception, or because clazz could not be loaded...
        // In any case we now try one more time
        return Class.forName(clazz);
      }
    }
  }
}
