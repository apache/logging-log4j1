/*
 * Copyright 1999,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.log4j.chainsaw;

import java.util.EventObject;


/**
 * An event representing when a Key has arrived inside a Chainsaw model that has
 * not been seen previously.
 *
 * @author Paul Smith <psmith@apache.org>
 *
 */
public class NewKeyEvent extends EventObject {
  private int newModelIndex = -1;
  private Object key;
  private Object value;

  /**
   * @param source
   */
  public NewKeyEvent(
    Object source, int newModelIndex, Object key, Object value) {
    super(source);
    this.newModelIndex = newModelIndex;
    this.key = key;
    this.value = value;
  }

  /**
   * @return
   */
  public Object getKey() {
    return key;
  }

  /**
   * @return
   */
  public int getNewModelIndex() {
    return newModelIndex;
  }

  /**
   * @return
   */
  public Object getValue() {
    return value;
  }
}
