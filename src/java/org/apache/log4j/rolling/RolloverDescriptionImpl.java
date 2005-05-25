/*
 * Copyright 1999,2005 The Apache Software Foundation.
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

package org.apache.log4j.rolling;

import org.apache.log4j.rolling.helper.Action;


/**
 * Description of actions needed to complete rollover.
 *
 * @author Curt Arnold
 * @since 1.3
 *
 */
public final class RolloverDescriptionImpl implements RolloverDescription {
  /**
   * Active log file name after rollover.
   */
  private final String activeFileName;

  /**
   * Should active file be opened for appending.
   */
  private final boolean append;

  /**
   * Action to be completed after close of current active log file
   * before returning control to caller.
   */
  private final Action synchronous;

  /**
   * Action to be completed after close of current active log file
   * and before next rollover attempt, may be executed asynchronously.
   */
  private final Action asynchronous;

  /**
   * Create new instance.
   * @param activeFileName active log file name after rollover, may not be null.
   * @param append true if active log file after rollover should be opened for appending.
   * @param synchronous action to be completed after close of current active log file, may be null.
   * @param asynchronous action to be completed after close of current active log file and
   * before next rollover attempt.
   */
  public RolloverDescriptionImpl(
    final String activeFileName, final boolean append, final Action synchronous,
    final Action asynchronous) {
    if (activeFileName == null) {
      throw new NullPointerException("activeFileName");
    }

    this.append = append;
    this.activeFileName = activeFileName;
    this.synchronous = synchronous;
    this.asynchronous = asynchronous;
  }

  /**
   * Active log file name after rollover.
   * @return active log file name after rollover.
   */
  public String getActiveFileName() {
    return activeFileName;
  }

  /**
   * {@inheritDoc}
   */
  public boolean getAppend() {
    return append;
  }

  /**
   * Action to be completed after close of current active log file
   * before returning control to caller.
   *
   * @return action, may be null.
   */
  public Action getSynchronous() {
    return synchronous;
  }

  /**
   * Action to be completed after close of current active log file
   * and before next rollover attempt, may be executed asynchronously.
   *
   * @return action, may be null.
   */
  public Action getAsynchronous() {
    return asynchronous;
  }
}
