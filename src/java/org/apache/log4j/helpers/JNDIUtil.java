/*
 * Created on Mar 28, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.log4j.helpers;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * @author ceki
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class JNDIUtil {
  
  
  static public Context getInitialContext() throws NamingException {  
    return new InitialContext();
  }
  
  static public String lookup(Context ctx, String name) {
    if(ctx == null) {
      return null;
    }
    try {
      return (String) ctx.lookup(name);
    } catch (NamingException e) {
      //LogLog.warn("Failed to get "+name);
      return null;
    }
  }
}
