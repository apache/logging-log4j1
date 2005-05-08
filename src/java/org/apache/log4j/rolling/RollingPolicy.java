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

package org.apache.log4j.rolling;


import org.apache.log4j.spi.OptionHandler;

/**
 * A <code>RollingPolicy</code> is responsible for performing the
 * rolling over of the active log file. The <code>RollingPolicy</code>
 * is also responsible for providing the <em>active log file</em>,
 * that is the live file where logging output will be directed.
 * 
 * @author Ceki G&uuml;lc&uuml;
 * @since 1.3
 * */
public interface RollingPolicy {
  
  /**
   * Rolls over log files according to implementation policy.  
   * <p>
   * <p>This method is invoked by {@link RollingFileAppender}, usually 
   * at the behest of its {@link TriggeringPolicy}.
   * 
   * @throws RolloverFailure Thrown if the rollover operation fails for any
   * reason.
   */
  public void rollover() throws RolloverFailure;

  /**
   * Get the new name of the active log file.
   * */  
  public String getActiveFileName();
}
