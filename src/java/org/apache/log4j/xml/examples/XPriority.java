
package org.apache.log4j.xml.examples;

import org.apache.log4j.Priority;


/**
   This class introduces a new priority level called TRACE. TRACE has
   lower priority than DEBUG.

 */
public class XPriority extends Priority {

  static final int  TRACE_INT  = 800;
  static final int  FATAL_INT  = 1;


  // We assimilate FATAL to EMERG on Syslog  
  static final int SYSLOG_FATAL_INT  = 0;  
  // We assimilate TRACE to DEBUG on Syslog
  static final int SYSLOG_TRACE_INT  = 7;

  public static final XPriority TRACE = new XPriority(TRACE_INT, "TRACE", 7);
  public static final XPriority FATAL = new XPriority(FATAL_INT, "FATAL", 
						      FATAL_INT);

  protected
  XPriority(int level, String strLevel, int syslogEquiv) {
    super(level, strLevel, syslogEquiv);
  }

  public
  static
  Priority toPriority(String sArg) {
    if(sArg == null)
       return XPriority.TRACE;
    
    String stringVal = sArg.toUpperCase();
    
    if(stringVal.equals("TRACE")) return XPriority.TRACE; 
    if(stringVal.equals("FATAL")) return XPriority.FATAL;
    return Priority.toPriority(sArg);
    
  }

  public
  static
  Priority toPriority(int i) throws  IllegalArgumentException {
    switch(i) {
    case TRACE_INT: return XPriority.TRACE;
    case FATAL_INT: return XPriority.FATAL;
    }
    return Priority.toPriority(i);
  }

}
  
