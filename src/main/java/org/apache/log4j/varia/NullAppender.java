/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.log4j.varia;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

/**
  * A NullAppender merely exists, it never outputs a message to any
  * device.  
  * @author Ceki G&uuml;lc&uml;
  */
public class NullAppender extends AppenderSkeleton {

  private static NullAppender instance = new NullAppender();

  public NullAppender() {
  }

  /** 
   * There are no options to acticate.
   * */
  public void activateOptions() {
  }

  /**
   * Whenever you can, use this method to retreive an instance instead
   * of instantiating a new one with <code>new</code>.
   * @deprecated Use getNullAppender instead.  getInstance should have been static.
   * */
  public NullAppender getInstance() {
    return instance;
  }

    /**
     * Whenever you can, use this method to retreive an instance instead
     * of instantiating a new one with <code>new</code>.
     * */
  public static NullAppender getNullAppender() {
      return instance;
  }

  public void close() {
  }

  /**
   * Does not do anything. 
   * */
  public void doAppend(LoggingEvent event) {
  }

  /**
   * Does not do anything. 
   * */
  protected void append(LoggingEvent event) {
  }

  /**
    * NullAppenders do not need a layout.  
    * */
  public boolean requiresLayout() {
    return false;
  }
}
