/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.log4j.helpers;

/** VersionHelper20 is the Java 2 compatible VersionHelper for classloading
 *  @since 1.2
 *  @see org.apache.log4j.helpers.VersionHelper
 *  @author Christopher Taylor
 */
public class VersionHelper20 extends VersionHelper {
  public Class loadClass (String klass_name) throws ClassNotFoundException {
    return Thread.currentThread().getContextClassLoader().loadClass(klass_name);
  }
}

