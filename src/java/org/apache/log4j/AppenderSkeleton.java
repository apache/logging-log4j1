/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.APL file.  */

package org.apache.log4j;

import org.apache.log4j.Layout;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.OptionHandler;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.helpers.OnlyOnceErrorHandler;
import org.apache.log4j.helpers.LogLog;


/** 
   Abstract super-class of the other appenders in the package.
   
   This class provides threashold filtering 

   @since 0.8.1
   @author Ceki G&uuml;lc&uuml;
 */
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
      It is assumed and enforced that errorHandler is never null.
  */
  protected ErrorHandler errorHandler = new OnlyOnceErrorHandler();

  /** The first filter in the filter chain. Set to <code>null</code>
      initially. */
  protected Filter headFilter;
  /** The last filter in the filter chain. */
  protected Filter tailFilter;

  /**
     Is this appender closed? 
   */
  protected boolean closed = false;


  /**
     The variable <code>emittedWarning</code> lets us remember whether
     we already emitted a warning to <code>System.err.</code>  */
  //  protected boolean emittedWarning = false;
  

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
     Add a filter to end of the filter list.

     @since 0.9.0
   */
  public
  void addFilter(Filter newFilter) {
    if(headFilter == null) {
      headFilter = tailFilter = newFilter;
    } else {
      tailFilter.next = newFilter;
      tailFilter = newFilter;    
    }
  }


  /**
     Subclasses of <code>AppenderSkeleton</code> should imlement this
     method to perform actual logging. See also {@link #doAppend
     AppenderSkeleton.doAppend} method.

     @since 0.9.0
  */
  abstract
  protected
  void append(LoggingEvent event);


  /**
     Clear the filters chain.
     
     @since 0.9.0 */
  public
  void clearFilters() {
    headFilter = tailFilter = null;
  }

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

    LogLog.debug("Finalizing appender named ["+name+"].");
    close();
  }


  /** 
      Return the currently set {@link ErrorHandler} for this
      Appender.  

      @since 0.9.0 */
  public
  ErrorHandler getErrorHandler() {
    return this.errorHandler;
  }


  /** 
      Return the first filter in the filter chain for this
      Appender. The return value may be <code>null</code> if no is
      filter is set.
      
  */
  public
  final
  Filter getFirstFilter() {
    return headFilter;
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
     Check whether the message priority is below the appender's
     threshold. Ig there is no threshold set, then the return value is
     always <code>true</code>.

  */
  public
  boolean isAsSevereAsThreshold(Priority priority) {
    return ((threshold == null) || priority.isGreaterOrEqual(threshold));
  }


  /**
     This method performs threshold checks and invokes filters before
     delegating actual logging to the sub-classes specific {@link
     AppenderSkeleton#append} method.

   */
  public
  synchronized 
  void doAppend(LoggingEvent event) {
    if(closed) {
      LogLog.error("Attempted to append to closed appender named ["+name+"].");
    }

    if(!isAsSevereAsThreshold(event.priority)) {
      return;
    }

    Filter f = this.headFilter;
    
    FILTER_LOOP:
    while(f != null) {
      switch(f.decide(event)) {
      case Filter.DENY: return;
      case Filter.ACCEPT: break FILTER_LOOP;
      case Filter.NEUTRAL: f = f.next;
      }
    }
    
    this.append(event);    
  }

  /** 
      Set the {@link ErrorHandler} for this Appender.
      @since 0.9.0
  */
  public
  void setErrorHandler(ErrorHandler eh) {
    if(eh == null) {
      // We do not throw exception here since the cause is probably a
      // bad config file.
      LogLog.error(
              "It is forbidden to set a null ErrorHandler. Ignoring directive.");
    } else {
      this.errorHandler = eh;
    }
  }

  /**
     Set the layout for this appender. Note that some appenders have
     their own (fixed) layouts or do not use one. For example, the
     {@link org.apache.log4j.net.SocketAppender} ignores the layout set
     here. 
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
     
     @since 0.8.3 */
  public
  void setThreshold(Priority threshold) {
    this.threshold = threshold;
  }
}
