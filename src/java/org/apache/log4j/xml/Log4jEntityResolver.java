/*
 * Copyright 1999-2005 The Apache Software Foundation.
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
