/**
  Copyright (c) 2000, Ceki Gulcu

  Permission is hereby granted, free of charge, to any person
  obtaining a copy of this software and associated documentation files
  (the "Software"), to deal in the Software without restriction,
  including without limitation the rights to use, copy, modify, merge,
  publish, distribute, sublicense, and/or sell copies of the Software,
  and to permit persons to whom the Software is furnished to do so,
  subject to the following conditions:

  The above copyright notice and this permission notice shall be
  included in all copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
  BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
  ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
  CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  SOFTWARE.
*/

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
