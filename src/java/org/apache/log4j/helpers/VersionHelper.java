/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.APL file.
 */

package org.apache.log4j.helpers;

import java.util.StringTokenizer;

/**
   VersionHelper fixes the classloading trouble when using Log4J in a
   multi-classloader environment like Jakarta Tomcat

   @since 1.2
   @author Christopher Taylor */
abstract public class VersionHelper {
	
  public static final String VERSION_PROPERTY = "java.version";	
  private static VersionHelper helper;

	
  /** VersionHelpers for specific JVM versions override this method,
   *  For example, VersionHelper11 just calls into
   *  <code>Class.forName()</code>, while VersionHelper20 calls into
   *  <code>Thread.currentThread().getContextClassLoader().loadClass()</code>
   * @see java.lang.Thread#getContextClassLoader */
  

  abstract public Class loadClass (String klass_name) throws ClassNotFoundException;
	
  /** All classes in Log4J that need to dynamically load other classes
   * must use
   * <code>org.apache.log4j.helpers.VersionHelper.getInstance().loadClass(<i>class
   * name</i>)</code> and <b>not</b> use <code>Class.forName</code>.
   * In a Java 2 environment, classes from a parent classloader will
   * ignore any classes available in child class loaders.
   * Consequently, any appenders, layout managers, or other supporting
   * Log4J classes that are not bundled with the main
   * <code>log4j.jar</code> file will be ignored in a
   * multi-classloader environment like Tomcat unless a call is made
   * to
   * <code>Thread.currentThread().getContextClassLoader().loadClass</code>.
   * As this method is only in Java 2, special version checking code
   * must be inserted guaranteeing that it won't be executed on Java 1
   * platforms.  The VersionHelper superclass handles the version
   * checking within this method by parsing the System property
   * <code>java.version</code>.
   * @see java.lang.Thread#getContextClassLoader */
  public static VersionHelper getInstance () {
    if (helper == null) {
      /* If the helper is null, we'll inspect the System property "java.version" and
	 figure out which version of the VM we're running on.
	 Version strings are: [major version].[minor version].[bug fix revision]
	 So JDK 1.2: 1.2.0
	 JDK 1.1: 1.1.0 
      */
      String prop = System.getProperty(VERSION_PROPERTY);
      StringTokenizer st = new StringTokenizer(prop,".");
      st.nextToken(); // Ignore the initial 1
      String version = st.nextToken();
      try {
	/* Here we'll parse the number and decide which version helper to use */
	switch (Integer.parseInt(version)) {
	case 0:
	case 1: helper = new VersionHelper11(); break;
	default: helper = new VersionHelper20(); break;
	}
      } catch (NumberFormatException oops) {
	helper = new VersionHelper11();
      }  
    }
    return helper;
  }
}
