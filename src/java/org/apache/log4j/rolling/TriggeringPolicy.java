/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

/**
 * A <code>TriggeringPolicy</code> determines the time where rollover
 * should occur.  
 *
 * @author Ceki G&uuml;lc&uuml;
 * */
package org.apache.log4j.rolling;

public interface TriggeringPolicy {
  
  /** 
   * Should rolllover be triggered at this time?
   * */
  public boolean isTriggeringEvent(long size);

  public boolean isTriggeringEvent();

  public boolean isSizeSensitive();

}
