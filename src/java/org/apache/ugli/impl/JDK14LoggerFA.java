/*
 * Created on Dec 30, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.ugli.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Logger;

import org.apache.ugli.LoggerFactoryAdapter;
import org.apache.ugli.ULogger;


/**
 * @author ceki
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class JDK14LoggerFA implements LoggerFactoryAdapter {

  Map map;
  
  JDK14LoggerFA() {
    map = new HashMap();
  }

  /* (non-Javadoc)
   * @see org.apache.ugli.LoggerFactoryAdapter#getLogger(java.lang.String)
   */
  public ULogger getLogger(String name) {
    ULogger ulogger = (ULogger) map.get(name);
    if(ulogger == null) {
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
