
package org.apache.log4j;

/**
   Defines the minimum set of priorities recognized by the system,
   that is {@link #FATAL}, {@link #ERROR}, {@link #WARN}, {@link
   #INFO} and {@link #DEBUG}.

   <p>The <code>Priority</code> class may be subclassed to define a larger
   priority set.

   @author Ceki G&uuml;lc&uuml;
 */
public class Priority {

  int level;
  String levelStr;

  public final static int OFF_INT = Integer.MAX_VALUE;
  public final static int FATAL_INT = 50000;
  public final static int ERROR_INT = 40000;
  public final static int WARN_INT  = 30000;
  public final static int INFO_INT  = 20000;
  public final static int DEBUG_INT = 10000;
  public final static int ALL_INT = Integer.MIN_VALUE;


  /**
     The <code>OFF</code> is used to turn off logging.
   */
  final static public Priority OFF = new Priority(OFF_INT, "OFF");


  /**
     The <code>FATAL</code> priority designates very severe error
     events that will presumably lead the application to abort.
   */
  final static public Priority FATAL = new Priority(FATAL_INT, "FATAL");

  /**
     The <code>ERROR</code> priority designates error events that
     might still allow the application to continue running.  */
  final static public Priority ERROR = new Priority(ERROR_INT, "ERROR");

  /**
     The <code>WARN</code> priority designates potentially harmful situations.
  */
  final static public Priority WARN  = new Priority(WARN_INT, "WARN");

  /**
     The <code>INFO</code> priority designates informational messages
     that highlight the progress of the application at coarse-grained
     level.  */
  final static public Priority INFO  = new Priority(INFO_INT, "INFO");

  /**
     The <code>DEBUG</code> priority designates fine-grained
     informational events that are most useful to debug an
     application.  */
  final static public Priority DEBUG = new Priority(DEBUG_INT, "DEBUG");

  /**
     The <code>ALL</code> is used to turn on all logging.
  */
  final static public Priority ALL = new Priority(ALL_INT, "ALL");

  
  /**
     Instantiate a priority object.
   */
  protected
  Priority(int level, String levelStr) {
    this.level = level;
    this.levelStr = levelStr;
  }


  /**
     Returns the string representation of this priority.
   */
  final
  public
  String toString() {
    return levelStr;
  }

  /**
     Returns the integer representation of this priority.
   */
  public
  final
  int toInt() {
    return level;
  }

    
  /**
     Returns <code>true</code> if this priority has a higher or equal
     priority than the priority passed as argument, <code>false</code>
     otherwise.  
     
     <p>You should think twice before overriding the default
     implementation of <code>isGreaterOrEqual</code> method.

  */
  public
  boolean isGreaterOrEqual(Priority r) {
    return level >= r.level;
  }


  /**
     Convert the string passed as argument to a priority. If the
     conversion fails, then this method returns {@link #DEBUG}. 
  */
  public
  static
  Priority toPriority(String sArg) {
    return toPriority(sArg, Priority.DEBUG);
  }

  /**
    Convert an integer passed as argument to a priority. If the
    conversion fails, then this method returns {@link #DEBUG}.

  */
  public
  static
  Priority toPriority(int val) {
    return toPriority(val, Priority.DEBUG);
  }

  /**
    Convert an integer passed as argument to a priority. If the
    conversion fails, then this method returns the specified default.
  */
  public
  static
  Priority toPriority(int val, Priority defaultPriority) {
    switch(val) {
    case ALL_INT: return ALL;
    case DEBUG_INT: return DEBUG;
    case INFO_INT: return INFO;
    case WARN_INT: return WARN;
    case ERROR_INT: return ERROR;
    case FATAL_INT: return FATAL;
    case OFF_INT: return OFF;
    default: return defaultPriority;
    }
  }

  /**
     Convert the string passed as argument to a priority. If the
     conversion fails, then this method returns the value of
     <code>defaultPriority</code>.  
  */
  public
  static
  Priority toPriority(String sArg, Priority defaultPriority) {                  
    if(sArg == null)
       return defaultPriority;
    
    String s = sArg.toUpperCase();

    if(s.equals("ALL")) return Priority.ALL; 
    if(s.equals("DEBUG")) return Priority.DEBUG; 
    if(s.equals("INFO"))  return Priority.INFO;
    if(s.equals("WARN"))  return Priority.WARN;  
    if(s.equals("ERROR")) return Priority.ERROR;
    if(s.equals("FATAL")) return Priority.FATAL;
    if(s.equals("OFF")) return Priority.OFF;
    return defaultPriority;
  }


}
