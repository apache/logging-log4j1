/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.log4j.helpers;

/**
   VersionHelper fixes the classloading trouble when using Log4J in a
   multi-classloader environment like Jakarta Tomcat

   @since 1.2
   @author Christopher Taylor */
abstract public class VersionHelper {
	
  static public final String VERSION_PROPERTY = "java.version";	
  static private VersionHelper helper;
  
  /** VersionHelpers for specific JVM versions override this method,
   *  For example, VersionHelper11 just calls into
   *  <code>Class.forName()</code>, while VersionHelper20 calls into
   *  <code>Thread.currentThread().getContextClassLoader().loadClass()</code>
   * @see java.lang.Thread#getContextClassLoader */
  

  abstract 
  public 
  Class loadClass (String klass_name) throws ClassNotFoundException;
	
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
      /* Inspect the System property "java.version" and figure out which
	 version of the VM we're running on.  Version strings are: [major
	 version].[minor version].[bug fix revision] So JDK 1.2: 1.2.0 JDK
	 1.1: 1.1.0 */
      String prop = System.getProperty(VERSION_PROPERTY);
      boolean java1 = true;

      if(prop != null) {
	int i = prop.indexOf('.');
	if(i != -1) {	
	  if(prop.charAt(i+1) != '1')
	    java1 = false;
	} 
      }
      if(java1) {
	helper = new VersionHelper11();
      } else {
	helper = new VersionHelper20();
      }
    }
    return helper;
  }
  
  
}
