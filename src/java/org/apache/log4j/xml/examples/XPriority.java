
package org.apache.log4j.xml.examples;

import org.apache.log4j.Priority;


/**
   This class introduces a new priority level called TRACE. TRACE has
   lower priority than DEBUG.

 */
public class XPriority extends Priority {

  static final int  TRACE_INT   = Priority.DEBUG_INT - 1;
  static final int  LETHAL_INT  = Priority.FATAL_INT + 1;


  private static String TRACE_STR  = "TRACE";
  private static String LETHAL_STR  = "LETHAL";


  public static final XPriority TRACE = new XPriority(TRACE_INT, TRACE_STR, 7);
  public static final XPriority LETHAL = new XPriority(LETHAL_INT, LETHAL_STR, 
						       0);


  protected
  XPriority(int level, String strLevel, int syslogEquiv) {
    super(level, strLevel, syslogEquiv);
  }

  /**
     Convert the string passed as argument to a priority. If the
     conversion fails, then this method returns {@link #TRACE}. 
  */
  public
  static
  Priority toPriority(String sArg) {
    return toPriority(sArg, XPriority.TRACE);
  }


  public
  static
  Priority toPriority(String sArg, Priority defaultValue) {

    if(sArg == null) {
      return defaultValue;
    }
    String stringVal = sArg.toUpperCase();
    
    if(stringVal.equals(TRACE_STR)) {
      return XPriority.TRACE;
    } else if(stringVal.equals(LETHAL_STR)) {
      return XPriority.LETHAL;
    }
      
    return Priority.toPriority(sArg, defaultValue);    
  }


  public
  static
  Priority toPriority(int i) throws  IllegalArgumentException {
    switch(i) {
    case TRACE_INT: return XPriority.TRACE;
    case LETHAL_INT: return XPriority.LETHAL;
    }
    return Priority.toPriority(i);
  }

}
  
