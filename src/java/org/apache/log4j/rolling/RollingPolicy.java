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

import java.io.IOException;

import java.util.List;


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
   * Prepare for a rollover.  This method is called prior to
   * closing the active log file and performs any necessary
   * preliminary actions.  The rolling file appender will then
   * close the active log file, perform the synchronous actions
   * and dispatch the asynchronous files before returning
   * control to its caller.
   *
   * @param activeFile buffer containing name of the active log file on entry,
   * and name of future active file on exit.
   * @param synchronousActions list to which instances of Runnable
   * are appended for actions to be performed after closing the active file.
   * @param asynchronousActions list to which instances of Runnable
   * are appended for actions to be performed asynchronously
   * after closing the active file and executing the synchronous actions
   * @return true if rollover should proceed.  If false rollover will
   * silently be skipped.
   * @throws IOException on failure to prepare for rollover.  Rollover
   * will be suppressed if an exception is thrown.
   */
  public boolean rollover(
    StringBuffer activeFile, List synchronousActions, List asynchronousActions)
    throws IOException;
}
