/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j;

import java.util.Vector;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.helpers.DateLayout;
import org.apache.log4j.helpers.Transform;
import org.apache.log4j.helpers.LogLog;

/**
   An appender that appends logging events to a vector.
   @author Ceki  G&uuml;lc&uuml;
*/
public class VectorAppender extends AppenderSkeleton {

  public Vector vector;
  
  public
  VectorAppender() {
    vector = new Vector();
  }

  /**
     Does nothing.
  */
  public
  void activateOptions() {
  }


  /**
     This method is called by the {@link AppenderSkeleton#doAppend}
     method.

  */
  public
  void append(LoggingEvent event) {
    System.out.println("---Vector appender called with message ["+event.getRenderedMessage()+"].");
    vector.addElement(event);
   }

  public
  Vector getVector() {
    return vector;
  }

  public
  synchronized
  void close() {
    if(this.closed)
      return;
    this.closed = true;
  }


  public
  boolean requiresLayout() {
    return false;
  }
}
