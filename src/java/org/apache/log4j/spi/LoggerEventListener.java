/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j.spi;

import org.apache.log4j.Logger;
import org.apache.log4j.Appender;

/**
  Interface used to listen for {@link Logger} related events such as
  add/remove appender or changing levels.  Clients register an instance of
  the interface and the instance is called back when the various events occur.
  
  {@link LoggerRepository} provides methods for adding and removing
  LoggerEventListener instances.
  
  When implementing the methods of this interface, it is useful to remember
  that the Logger can access the repository using its getRepository()
  method.
  
  @author Ceki G&uuml;lc&uuml;
  @author Mark Womack
  @since 1.3
*/
public interface LoggerEventListener {
  
  /**
    Called when an appender is added to the logger.
    
    @param logger The logger to which the appender was added.
    @param appender The appender added to the logger. */
  public void appenderAddedEvent(Logger logger, Appender appender);
  
  /**
    Called when an appender is removed from the logger.
    
    @param logger The logger from which the appender was removed.
    @param appender The appender removed from the logger. */
  public void appenderRemovedEvent(Logger logger, Appender appender);

  /**
    Called when all appenders are removed from the logger.
    
    @param logger The logger from which the appenders were removed. */
  public void allAppendersRemovedEvent(Logger logger);
  
  /**
    Called when level changed on the logger.
    
    @param logger The logger that changed levels. */
  public void levelChangedEvent(Logger logger);
}