/*
 * Created on May 10, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.log4j.db;

import java.util.Set;

import org.apache.log4j.spi.LoggingEvent;

/**
 * @author Ceki G&uuml;lc&uuml;
 *
 */
public class DBHelper {
  
  public static short PROPERTIES_EXIST = 0x01;
  public static short EXCEPTION_EXISTS = 0x02;
  
  static short computeReferenceMask(LoggingEvent event) {
    short mask = 0;
    Set propertiesKeys = event.getPropertyKeySet();
    if(propertiesKeys.size() > 0) {
      mask = PROPERTIES_EXIST;
    }
    String[] strRep = event.getThrowableStrRep();
    if(strRep != null) {
      mask |= EXCEPTION_EXISTS;
    }
    return mask;
  }
  

}
