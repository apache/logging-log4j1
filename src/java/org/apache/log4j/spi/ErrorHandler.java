/*
 * ============================================================================
 *                   The Apache Software License, Version 1.1
 * ============================================================================
 *
 *    Copyright (C) 1999 The Apache Software Foundation. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modifica-
 * tion, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of  source code must  retain the above copyright  notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any, must
 *    include  the following  acknowledgment:  "This product includes  software
 *    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
 *    Alternately, this  acknowledgment may  appear in the software itself,  if
 *    and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "log4j" and  "Apache Software Foundation"  must not be used to
 *    endorse  or promote  products derived  from this  software without  prior
 *    written permission. For written permission, please contact
 *    apache@apache.org.
 *
 * 5. Products  derived from this software may not  be called "Apache", nor may
 *    "Apache" appear  in their name,  without prior written permission  of the
 *    Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 * APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 * DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 * ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 * (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * This software  consists of voluntary contributions made  by many individuals
 * on  behalf of the Apache Software  Foundation.  For more  information on the
 * Apache Software Foundation, please see <http://www.apache.org/>.
 *
 */

package org.apache.log4j.spi;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;


/**
   Appenders may delegate their error handling to
   <code>ErrorHandlers</code>.

   <p>Error handling is a particularly tedious to get right because by
   definition errors are hard to predict and to reproduce.


   <p>Please take the time to contact the author in case you discover
   that errors are not properly handled. You are most welcome to
   suggest new error handling policies or criticize existing policies.


   @author Ceki G&uuml;lc&uuml;

*/
public interface ErrorHandler extends OptionHandler {
  /**
     Add a reference to a logger to which the failing appender might
     be attached to. The failing appender will be searched and
     replaced only in the loggers you add through this method.

     @param logger One of the loggers that will be searched for the failing
     appender in view of replacement.

     @since 1.2 */
  void setLogger(Logger logger);

  /**
     Equivalent to the {@link #error(String, Exception, int,
     LoggingEvent event)} with the the event parameteter set to
     <code>null</code>.

  */
  void error(String message, Exception e, int errorCode);

  /**
     This method is normally used to just print the error message
     passed as a parameter.
  */
  void error(String message);

  /**
     This method is invoked to handle the error.

     @param message The message assoicated with the error.
     @param e The Exption that was thrown when the error occured.
     @param errorCode The error code associated with the error.
     @param event The logging event that the failing appender is asked
            to log.

     @since 1.2 */
  void error(String message, Exception e, int errorCode, LoggingEvent event);

  /**
     Set the appender for which errors are handled. This method is
     usually called when the error handler is configured.

     @since 1.2 */
  void setAppender(Appender appender);

  /**
     Set the appender to falkback upon in case of failure.

     @since 1.2 */
  void setBackupAppender(Appender appender);
}
