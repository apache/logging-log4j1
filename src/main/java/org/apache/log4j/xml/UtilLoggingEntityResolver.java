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
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import java.io.InputStream;


/**
 * An {@link EntityResolver} specifically designed to return
 * <code>java 1.4's logging dtd, logger.dtd</code>
 * which is embedded within the log4j jar file.  Based on EntityResolver.
 *
 * @since 1.3
 *
 * @author Paul Austin
 * @author Scott Deboy (sdeboy@apache.org)
 */
public final class UtilLoggingEntityResolver implements EntityResolver {

    /**
     * Create new instance.
     */
    public UtilLoggingEntityResolver() {
        super();
    }


    /** {@inheritDoc} */
  public InputSource resolveEntity(final String publicId,
                                   final String systemId) {
    if (systemId.endsWith("logger.dtd")) {
      Class clazz = getClass();
      InputStream in =
        clazz.getResourceAsStream("/org/apache/log4j/xml/logger.dtd");
      if (in == null) {
        LogManager.getLogger(UtilLoggingEntityResolver.class).error(
          "Could not find [logger.dtd]. Used [" + clazz.getClassLoader()
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
