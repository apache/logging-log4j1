
package org.apache.log4j.xml.test;

import org.apache.log4j.Priority;


/**
   This class introduces a new priority level called TRACE. TRACE has
   lower priority than DEBUG.

 */
public class TPriority extends Priority {

  static final int  TRACE_INT  = 8000;

  // We assimilate TRACE to DEBUG on Syslog
  static final int SYSLOG_TRACE_INT  = 7;  

  private static String TRACE_STR  = "TRACE";

  public static final TPriority TRACE = new TPriority(TRACE_INT, "TRACE", 7);

  protected
  TPriority(int level, String strLevel, int syslogEquiv) {
    super(level, strLevel, syslogEquiv);
  }

  public
  static
  Priority toPriority(String sArg) {
    if(sArg == null)
       return TPriority.TRACE;
    
    String stringVal = sArg.toUpperCase();
    
    if(stringVal.equalsIgnoreCase(TRACE_STR)) return TPriority.TRACE; 
    return Priority.toPriority(sArg);
    
  }

  public
  static
  Priority toPriority(int i) throws  IllegalArgumentException {
    switch(i) {
    case TRACE_INT: return TPriority.TRACE;
    }
    return Priority.toPriority(i);
  }

}
  
