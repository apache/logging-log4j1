/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.APL file.  */

package org.apache.log4j.spi;

/**
   
   Implementions of this interface allow certain appenders to decide
   when to perform an appender specific action.

  <p>See {@link org.apache.log4j.net.SMTPAppender} for an example of
  an appender that depends on a
  <code>TriggeringEventEvaluators</code>.

  @author Ceki G&uuml;lc&uuml;
  @since version 1.0
   
 */
public interface TriggeringEventEvaluator {
  
  /**
     Is this the triggering event?
   */
  public boolean isTriggeringEvent(LoggingEvent event);
}
