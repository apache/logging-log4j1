/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j.xml;

import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import org.apache.log4j.helpers.LogLog;

/**
 * An {@link EntityResolver} specifically designed to return
 * <code>log4j.dtd</code> which is embedded within the log4j jar
 * file. 
 *
 * @author Paul Austin
 * */
public class Log4jEntityResolver implements EntityResolver {

  public InputSource resolveEntity (String publicId, String systemId) {
    if (systemId.endsWith("log4j.dtd")) {
      Class clazz = getClass();
      InputStream in = clazz.getResourceAsStream("/org/apache/log4j/xml/log4j.dtd");
      if (in == null) {
	LogLog.error("Could not find [log4j.dtd]. Used [" + clazz.getClassLoader() 
		     + "] class loader in the search.");
	return null;
      } else {
	return new InputSource(in);
      }
    } else {
      return null;
    }
  }
}
