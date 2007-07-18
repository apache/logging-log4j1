/*
 * Copyright 1999-2006 The Apache Software Foundation.
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

package org.apache.log4j.helpers;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

/**
   <code>ErrorHandler</code> and its implementations are no longer
   utilized by Log4j.  The <code>ErrorHandler</code> interface and any
   implementations of it are only here to provide binary runtime
   compatibility with versions previous to 1.3, most specifically
   1.2.xx versions.  All methods are NOP's.

   @author Ceki G&uuml;lc&uuml;
   @since 0.9.0
   @deprecated As of 1.3
 */
public class OnlyOnceErrorHandler implements org.apache.log4j.spi.ErrorHandler {
  public void setLogger(Logger logger) {}
  public void activateOptions() {}
  public void error(String message, Exception e, int errorCode) {}
  public void error(String message, Exception e, int errorCode, LoggingEvent event) {}
  public void error(String message) {}
  public void setAppender(Appender appender) {}
  public void setBackupAppender(Appender appender) {}
}
