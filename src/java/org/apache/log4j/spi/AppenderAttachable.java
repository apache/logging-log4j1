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

package org.log4j.spi;

import org.log4j.Appender;
import java.util.Enumeration;

/**
   Interface for attaching appenders to objects.

   @author Ceki G&uuml;lc&uuml;
   @since 0.9.1 */
public interface AppenderAttachable {
  
  /**
     Add an appender.
   */
  public
  void addAppender(Appender newAppender);

  /**
     Get all previously added appenders as an Enumeration.  */
  public
  Enumeration getAllAppenders();

  /**
     Get  an appender by name.
   */
  public
  Appender getAppender(String name);

  /**
     Remove all previously added appenders.
  */
  void removeAllAppenders();


  /**
     Remove the appender passed as parameter form the list of appenders.
  */
   void removeAppender(Appender appender);


 /**
    Remove the appender with the name passed as parameter form the
    list of appenders.  
  */
 void
 removeAppender(String name);   
}

