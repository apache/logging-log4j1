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

package org.apache.log4j.xml;

import java.io.InputStream;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import org.apache.log4j.helpers.LogLog;

/**
 * An {@link EntityResolver} specifically designed to return
 * <code>java 1.4's logging dtd, logger.dtd</code> which is embedded within the log4j jar
 * file.  Based on EntityResolver. 
 *
 * @author Paul Austin
 * @author Scott Deboy <sdeboy@apache.org>
 * 
 */
public class UtilLoggingEntityResolver implements EntityResolver {

  public InputSource resolveEntity (String publicId, String systemId) {
    System.err.println("publicID: ["+publicId+"]");
    System.err.println("systemId: ["+systemId+"]");
    if (systemId.endsWith("logger.dtd")) {
      Class clazz = getClass();
      InputStream in = clazz.getResourceAsStream("/org/apache/log4j/xml/logger.dtd");
      if (in == null) {
	LogLog.error("Could not find [logger.dtd]. Used [" + clazz.getClassLoader() 
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
