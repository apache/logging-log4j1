/*
 * Created on Dec 30, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.ugli.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.ugli.LoggerFactoryAdapter;
import org.apache.ugli.ULogger;


/**
 * An implementation of {@link LoggerFactoryAdapter} which always returns
 * {@link SimpleLogger} instances.
 * 
 * @author Ceki G&uuml;lc&uuml;
 */
public class SimpleLoggerFA implements LoggerFactoryAdapter {

  Map map;
  
  SimpleLoggerFA() {
    map = new HashMap();
  }


  /**
   * Return an appropriate {@link SimpleLogger} instance by name. At this time,
   * 
   */
  /**
   * Return an appropriate {@link SimpleLogger} instance.
   * */
  public ULogger getLogger(String name) {
    ULogger ulogger = (ULogger) map.get(name);
    if(ulogger == null) {
      ulogger = new SimpleLogger(name);
      map.put(name, ulogger);
    }
    return ulogger;
  }

  /*
   *  (non-Javadoc)
   * @see org.apache.ugli.LoggerFactoryAdapter#getLogger(java.lang.String, java.lang.String)
   */
  public ULogger getLogger(String domainName, String subDomainName) {
    return getLogger(domainName);
  }
  
  
}
