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

package org.apache.ugli.impl;

import org.apache.ugli.LoggerFactoryAdapter;
import org.apache.ugli.ULogger;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;


/**
 * @author ceki
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class JDK14LoggerFA implements LoggerFactoryAdapter {
  Map map;

  public JDK14LoggerFA() {
    map = new HashMap();
  }

  /* (non-Javadoc)
   * @see org.apache.ugli.LoggerFactoryAdapter#getLogger(java.lang.String)
   */
  public ULogger getLogger(String name) {
    ULogger ulogger = (ULogger) map.get(name);
    if (ulogger == null) {
      Logger logger = Logger.getLogger(name);
      ulogger = new JDK14Logger(logger);
      map.put(name, ulogger);
    }
    return ulogger;
  }

  /* (non-Javadoc)
   * @see org.apache.ugli.LoggerFactoryAdapter#getLogger(java.lang.String, java.lang.String)
   */
  public ULogger getLogger(String domainName, String subDomainName) {
    return getLogger(domainName);
  }
}
