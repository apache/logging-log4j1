/*
 * Created on Dec 30, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.ugli;



/**
 * LoggerFactoryAdapter interface is used internally by {@link LoggerFactory}.
 * 
 * <p>Only developers wishing to write new UGLI adapters need to worry about
 * this interface.
 * 
 * @author Ceki G&uuml;lc&uuml;
 *
 */
public interface LoggerFactoryAdapter {
  
  public ULogger getLogger(String name);
  public ULogger getLogger(String domainName, String subDomainName);  
}
