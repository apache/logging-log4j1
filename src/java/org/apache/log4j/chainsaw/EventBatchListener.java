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

import java.util.EventListener;
import java.util.List;


/**
 * Implementations of this interface like to be notified
 * of arriving batches of LoggingEvents, but may only
 * be interested in those coming from a particular source
 * @author Paul Smith <psmith@apache.org>
 *
 */
public interface EventBatchListener extends EventListener {
  /**
   * Returns the string representing the identifier
   * that this instance is only interested in, or
   * null if this instance is interested in ALL events from
   * any identifier
   * @return String identifier of the interested source
   */
  public String getInterestedIdentifier();

  /**
   * Implementations receive a list of ChainsawEventBatchEntry instances only if they are interested,
   * that is, if the source of the eventBatch matches this instances interested identifier
   * @param eventBatchEntries List of ChainsawEventBatchEntry instances
   * @param identifier the identifier this list of eventBatchEntrys is associated with
   */
  public void receiveEventBatch(String identifier, List eventBatchEntrys);
}
