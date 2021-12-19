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

import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.helpers.LogLog;

/**
   This appender listens on a socket on the port specified by the
   <b>Port</b> property for a "RollOver" message in Log4j up to 1.2.17.

   Changed in 1.2.18+ to complain about its use and act as a regular
   FileAppender otherwise. This may mean your files stop rolling over
   if you keep using this class! See
   <a href="https://logging.apache.org/log4j/1.2/">the log4j 1.2 homepage</a>
   for more information on why this class is disabled since 1.2.18.

   @author Ceki G&uuml;lc&uuml;
   @since version 0.9.0
   @noinspection unused
*/
public class ExternallyRolledFileAppender extends RollingFileAppender {
  static final String EXTERNAL_ROLLING_UNSUPPORTED =
      "ERROR-LOG4J-NETWORKING-UNSUPPORTED: External Rolled File Appender unsupported!" +
      " This is a breaking change in Log4J 1 >=1.2.18. Change your config to stop using it!";
  static final public String ROLL_OVER = "RollOver";
  static final public String OK = "OK";

  public
  ExternallyRolledFileAppender() {
    super();
    LogLog.error(EXTERNAL_ROLLING_UNSUPPORTED);
  }

  public
  void setPort(int port) {
  }

  public
  int getPort() {
    return 0;
  }

  /**
     Start listening on the port specified by a preceding call to
     {@link #setPort}.  */
  public
  void activateOptions() {
    super.activateOptions();
  }
}
