/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j;

import org.apache.log4j.spi.OptionHandler;
import org.apache.log4j.spi.LoggingEvent;

/**
   Extend this abstract class to create your own log layout format.
   
   @author Ceki G&uuml;lc&uuml;

*/
  
public abstract class Layout implements OptionHandler {

  // Note that the line.separator property can be looked up even by
  // applets.
  public final static String LINE_SEP = System.getProperty("line.separator");
  public final static int LINE_SEP_LEN  = LINE_SEP.length();


  /**
     Implement this method to create your own layout format.
  */
  abstract
  public
  String format(LoggingEvent event);

  /**
     If the layout handles the throwable object contained within
     {@link LoggingEvent}, then the layout should return
     <code>false</code>. Otherwise, if the layout ignores throwable
     object, then the layout should return <code>true</code>.

     @since 1.0 */
  abstract
  public
  boolean ignoresThrowable();

}
