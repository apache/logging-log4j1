/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.log4j.varia;

import  org.apache.log4j.spi.ErrorHandler;
import  org.apache.log4j.spi.LoggingEvent;
import  org.apache.log4j.Appender;
import  org.apache.log4j.Logger;
import java.util.Vector;

/**

   The <code>FallbackErrorHandler</code> implements the ErrorHandler
   interface such that a secondary appender may be specified.  This
   secondary appender takes over if the primary appender fails for
   whatever reason.

   <p>The error message is printed on <code>System.err</code>, and
   logged in the new secondary appender.

*/
public class FallbackErrorHandler implements ErrorHandler {


  Appender backup;
  Appender primary;
  Vector loggers;

  public FallbackErrorHandler() {
  }
  

  /**
     <em>Adds</em> the logger passed as parameter to the list of
     loggers that we need to search for in case of appender failure.
  */
  public 
  void setLogger(String loggerName) {
    if(loggers == null) {
      loggers = new Vector();
    }
    if(loggerName.equalsIgnoreCase
    loggers.add(logger);
  }


  /**
     No options to activate.
  */
  public 
  void activateOptions() {
  }


  /**
     Prints the message and the stack trace of the exception on
     <code>System.err</code>.  */
  public
  void error(String message, Exception e, int errorCode) { 
    error(message, e, errorCode, null);
  }

  /**
     Prints the message and the stack trace of the exception on
     <code>System.err</code>.
   */
  public
  void error(String message, Exception e, int errorCode, LoggingEvent event) {
    for(int i = 0; i < loggers.size(); i++) {
      Logger l = (Logger) loggers.get(i);
      if(l.isAttached(primary)) {
	 l.removeAppender(primary);
	 l.addAppender(backup);
      }
    }
  }


  /**
     Print a the error message passed as parameter on
     <code>System.err</code>.  
  */
  public 
  void error(String message) {
    //if(firstTime) {
    //LogLog.error(message);
    //firstTime = false;
    //}
  }
  
  /**
     The appender to which this error handler is attached.
   */
  public
  void setAppender(Appender primary) {
    this.primary = primary;
  }

  /**
     Set the backup appender.
   */
  public
  void setBackupAppender(Appender backup) {
    this.backup = backup;
  }
  
}
