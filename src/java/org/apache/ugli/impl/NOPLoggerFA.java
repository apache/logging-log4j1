/*
 * Created on Dec 30, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.ugli.impl;

import org.apache.ugli.LoggerFactoryAdapter;
import org.apache.ugli.ULogger;


/**
 * NOPLoggerFA is am implementation of {@link LoggerFactoryAdapter}
 * which always returns the unique instance of NOPLogger.
 * 
 * @author Ceki Gulcu
 */
public class NOPLoggerFA implements LoggerFactoryAdapter {
  
  public ULogger getLogger(String name) {
    return NOPLogger.NOP_LOGGER;
  }
  public ULogger getLogger(String domainName, String subDomainName) {
    return NOPLogger.NOP_LOGGER;  
  }  
}
