/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j.spi;

/**
  Interface used to listen for {@link LoggerRepository} related
  events such as startup, reset, and shutdown.  Clients register
  an instance of the interface and the instance is called back
  when the various events occur.
  
  {@link LoggerRepository} provides methods for adding and removing
  LoggerEventListener instances.

  @author Ceki G&uuml;lc&uuml;
  @author Mark Womack
  @since 1.3
*/
public interface LoggerRepositoryEventListener {
  
  /**
    Called when the repository configuration is reset. */
  public void configurationResetEvent(LoggerRepository repository);

  /**
    Called when the repository configuration is changed. */
  public void configurationChangedEvent(LoggerRepository repository);

  /**
    Called when the repository is shutdown. When this method is
    invoked, the repository is still valid (ie it has not been
    shutdown, but will be after this method returns). */
  public void shutdownEvent(LoggerRepository repository);
}