/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

/**
 * A <code>CopyingPolicy</code> is responsible for copying a log
 * file after the occurence of a rollover triggering event.
 *
 * @author Ceki G&uuml;lc&uuml;
 * */
package org.apache.log4j.rolling;

public interface CopyingPolicy {
  
  /** 
   * Copy the file passed as parameter to an appropriate location.
   * */
  public void copy(File oldLogFile);
}
