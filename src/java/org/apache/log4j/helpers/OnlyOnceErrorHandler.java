/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.APL file.
 */

package org.apache.log4j.helpers;

import  org.apache.log4j.spi.ErrorHandler;

/**

   The <code>OnlyOnceErrorHandler</code> implements log4j's default
   error handling policy which consists of emitting a message for the
   first error in an appender and ignoring all following errors.

   <p>The error message is printed on <code>System.err</code>. 

   <p>This policy aims at a protecting an otherwise working
   application from being flooded with error messages when logging
   fails.

   @author Ceki G&uuml;lc&uuml;
   @since 0.9.0 */
public class OnlyOnceErrorHandler implements ErrorHandler {


  final String WARN_PREFIX = "log4j warning: ";
  final String ERROR_PREFIX = "log4j error: ";

  boolean firstTime = true;

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
    if(firstTime) {
      LogLog.error(message, e);
      firstTime = false;
    }
  }

  /**
     Returns <code>null</code> as <code>OnlyOnceErrorHandler</code>
     has no options.  */
  public
  String[] getOptionStrings() {
    return null;
  }

  /**
     No options to set.
  */
  public
  void setOption(String key, String value) {
  }

  /**
     Print a the error message passed as parameter on
     <code>System.err</code>.  
  */
  public
  void error(String message) {
    if(firstTime) {
      LogLog.error(message);
      firstTime = false;
    }
  }
}
