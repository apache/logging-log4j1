/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */
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
