package org.apache.log4j;

import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.OptionHandler;
import org.apache.log4j.helpers.LogLog;


/** 
   Abstract superclass of the other appenders in the package.
   
   This class provides the code for common functionality, such as
   support for threshold filtering and support for general filters.

   @since 0.8.1
   @author Ceki G&uuml;lc&uuml; */
public abstract class AppenderSkeleton implements Appender, OptionHandler {

  /** The layout variable does not need to be set if the appender
      implementation has its own layout. */
  protected Layout layout;

  /** Appenders are named. */
  protected String name;

  /**
     There is no priority threshold filtering by default.  */
  protected Priority threshold;

  /**
     Is this appender closed? 
   */
  protected boolean closed = false;

  boolean firstTime = true;

  /**
     A string constant used in naming the option for setting the
     threshold for the appender. See also {@link #setThreshold
     setThreshold} method. Current value of this string constant is
     <b>Threshold</b>.

     <p>Note that all option keys are case sensitive.
     
  */
  public static final String THRESHOLD_OPTION = "Threshold";
  

  /**
     Derived appenders should override this method if option structure
     requires it.  */
  public
  void activateOptions() {
  }


  /**
     Subclasses of <code>AppenderSkeleton</code> should implement this
     method to perform actual logging. See also {@link #doAppend
     AppenderSkeleton.doAppend} method.

     @since 0.9.0
  */
  abstract
  protected
  void append(LoggingEvent event);


  /**
     Finalize this appender by calling the imlenentation's
     <code>close</code> method.

     @since 0.8.4
  */
  public
  void finalize() {
    // An appender might be closed then garbage collected. There is no
    // point in closing twice.
    if(this.closed) 
      return;
    close();
  }

  /**
     Prints the message and the stack trace of the exception on
     <code>System.err</code>.  */
  public
  void error(String message, Exception e) { 
    if(firstTime) {
      LogLog.error(message, e);
      firstTime = false;
    }
  }

  /**
     Print a the error message passed as parameter on
     <code>System.err</code>.  
  */
  public
  void error(String message) {
    error(message, null);
  }

  /**
     Returns the layout of this appender. The value may be null.
  */
  public
  Layout getLayout() {
    return layout;
  }


  /**
     Returns the name of this FileAppender.
   */
  public
  final
  String getName() {
    return this.name;
  }

  /**
     Returns the string array {{@link #THRESHOLD_OPTION}}.

     <p>Configurable Appenders must override this method to return the
     additional options they accept.  */
  public
  String[] getOptionStrings() {
    return new String[] {THRESHOLD_OPTION};
  }

  /**
     Returns this appenders threshold priority. See the {@link
     #setThreshold} method for the meaning of this option.
     
     @since 1.1 */
  public
  Priority getThreshold() {
    return threshold;
  }


  /**
     Check whether the message priority is below the appender's
     threshold. If there is no threshold set, then the return value is
     always <code>true</code>.

  */
  public
  boolean isAsSevereAsThreshold(Priority priority) {
    return ((threshold == null) || priority.isGreaterOrEqual(threshold));
  }


  /**
     This method performs threshold checks and invokes filters before
     delegating actual logging to the subclasses specific {@link
     AppenderSkeleton#append} method.

   */
  public
  synchronized 
  void doAppend(LoggingEvent event) {
    if(closed) {
      LogLog.warn("Appender ["+name+"] is closed.");
    }

    if(!isAsSevereAsThreshold(event.priority)) {
      return;
    }
    
    this.append(event);    
  }

  /**
     Set the layout for this appender. Note that some appenders have
     their own (fixed) layouts or do not use one. 
  */
  public
  void setLayout(Layout layout) {
    this.layout = layout;
  }

  
  /**
     Set the name of this Appender.
   */
  public
  void setName(String name) {
    this.name = name;
  }

  /**
     Configurable Appenders should override this method if they admit
     additional options.

     All classes derived from {@link AppenderSkeleton} admit the
     <b>Threshold</b> option, that is the value of the string constant
     {@link #THRESHOLD_OPTION}.

     See {@link #setThreshold} method for the meaning of this option.
  */
  public
  void setOption(String key, String value) {
    if(key.equalsIgnoreCase(THRESHOLD_OPTION)) {
      threshold = Priority.toPriority(value);
    }
  }

  /**
     Set the threshold priority. All log events with lower priority
     than the threshold priority are ignored by the appender.
     
     <p>In configuration files this option is specified by setting the
     value of the <b>Threshold</b> option to a priority
     string, such as "DEBUG", "INFO" and so on.
     
     @since 0.8.3 */
  public
  void setThreshold(Priority threshold) {
    this.threshold = threshold;
  }  
}
