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

package org.apache.log4j.xml;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import java.io.InputStream;


/**
 * An {@link EntityResolver} specifically designed to return
 * <code>log4j.dtd</code> which is embedded within the log4j jar
 * file.
 *
 * @author Paul Austin
 * @author Ceki Gulcu (adapted for log4j)
 * */
public class Log4jEntityResolver implements EntityResolver {
  public InputSource resolveEntity(String publicId, String systemId) {
    Logger logger = LogManager.getLogger(Log4jEntityResolver.class);

    logger.debug(
      "Log4jEntityResolver.resolveEntity({}, {}) called", publicId, systemId);
    if (systemId.endsWith("log4j.dtd")) {
      //logger.warn("The 'log4j.dtd' is no longer used nor needed.");
      //logger.warn("See {}#log4j_dtd for more details.", Constants.CODES_HREF);
      Class clazz = getClass();
      InputStream in =
        clazz.getResourceAsStream("/org/apache/log4j/xml/log4j.dtd");

      if (in == null) {
        logger.error(
          "Could not find [log4j.dtd]. Used [{}] class loader in the search.",
          clazz.getClassLoader());

        return null;
      } else {
        return new InputSource(in);
      }
    } else {
      return null;
    }
  }
}
