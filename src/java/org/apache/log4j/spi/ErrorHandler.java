/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j.spi;

import org.apache.log4j.Appender;

/**
   Appenders may delegate their error handling to ErrorHandlers.

   <p>Error handling is a particularly tedious to get right because by
   definition errors are hard to predict and to reproduce. 


   <p>Please take the time to contact the author in case you discover
   that errors are not properly handled. You are most welcome to
   suggest new error handling policies or criticize existing policies.


   @author Ceki G&uuml;lc&uuml;

   @since 0.9.0 */
public interface ErrorHandler extends OptionHandler {


  /**
     This method should handle the error. Information about the error
     condition is passed a parameter.
     
     @param message The message assoicated with the error.
     @param e The Exption that was thrown when the error occured.
     @param errorCode The error code associated with the error.
  */
  void error(String message, Exception e, int errorCode);


  /**
     This method prints the error message passed as a parameter.
  */
  void error(String message);

}
