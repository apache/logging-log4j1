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

import org.apache.log4j.spi.OptionHandler;


/**
 * A <code>RollingPolicy</code> specifies the actions taken
 * on a logging file rollover.
 *
 * @author Ceki G&uuml;lc&uuml;
 * @author Curt Arnold
 * @since 1.3
 * */
public interface RollingPolicy extends OptionHandler {
  /**
   * Initialize the policy and return any initial actions for rolling file appender..
   *
   * @param file current value of RollingFileAppender.getFile().
   * @param append current value of RollingFileAppender.getAppend().
   * @return Description of the initialization, may be null to indicate
   * no initialization needed.
   * @throws SecurityException if denied access to log files.
   */
  public RolloverDescription initialize(
    final String file, final boolean append) throws SecurityException;

  /**
   * Prepare for a rollover.  This method is called prior to
   * closing the active log file, performs any necessary
   * preliminary actions and describes actions needed
   * after close of current log file.
   *
   * @param activeFile file name for current active log file.
   * @return Description of pending rollover, may be null to indicate no rollover
   * at this time.
   * @throws SecurityException if denied access to log files.
   */
  public RolloverDescription rollover(final String activeFile) throws SecurityException;
}
