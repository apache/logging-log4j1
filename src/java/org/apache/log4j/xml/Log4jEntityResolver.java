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
 * @auhtor Ceki G&uuml;lc&uuml;
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
      }
      
      // Return a copy of log4j.dtd. This solves problems with not
      // being able to remove the log4j.jar file on Windows systems.
      try {
	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	byte[] buf = new byte[1024];
	int i;
	while((i = in.read(buf)) != -1) {
	  bos.write(buf, 0, i);
	}
	in.close();
	return new InputSource(new ByteArrayInputStream(bos.toByteArray()));
      } catch(IOException e) {
	LogLog.error("Could not read log4j.dtd.", e);
	return null;
      }
    } else {
      return null;
    }
  }
}
