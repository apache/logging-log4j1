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

package org.apache.log4j.plugins;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;


/**
  Defines the base class for Receiver plugins.

  <p>Just as Appenders send logging events outside of the log4j
  environment (to files, to smtp, to sockets, etc), Receivers bring
  logging events inside the log4j environment.

  <p>Receivers are meant to support the receiving of
  remote logging events from another process. For example,
  SocketAppender "appends" a logging event to a socket, configured
  for a specific host and port number.  On the receiving side of
  the socket can be a SocketReceiver object.  The SocketReceiver
  object receives the logging event, and then "posts" it to the
  log4j environment (LoggerRepository) on the receiving machine, to
  be handled by the configured appenders, etc.  The various
  settings in this environment (Logger levels, Appender filters &
  thresholds) are applied to the received logging event.

  <p>Receivers can also be used to "import" log messages from other
  logging packages into the log4j environment.

  <p>Receivers can be configured to post events to a given
  LoggerRepository.

  <p>Subclasses of Receiver must implement the isActive(),
  activateOptions(), and shutdown() methods. The doPost() method
  is provided to standardize the "import" of remote events into
  the repository.

  @author Mark Womack
  @author Ceki G&uuml;lc&uuml;
  @since 1.3
*/
public abstract class Receiver extends PluginSkeleton {
  /**
    Posts the logging event to a logger in the configured logger
    repository.

    @param event the log event to post to the local log4j environment. */
  public void doPost(LoggingEvent event) {
    // get the "local" logger for this event from the
    // configured repository.
    Logger localLogger =
      getLoggerRepository().getLogger(event.getLoggerName());

    // if the logger level is greater or equal to the level
    // of the event, use the logger to append the event.
    if (event.getLevel().isGreaterOrEqual(localLogger.getEffectiveLevel())) {
      // call the loggers appenders to process the event
      localLogger.callAppenders(event);
    }
  }
}
