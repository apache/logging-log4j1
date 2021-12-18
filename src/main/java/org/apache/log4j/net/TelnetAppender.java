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

package org.apache.log4j.net;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

import java.io.IOException;

/**
   The TelnetAppender is a log4j appender that specializes in
   writing to a read-only socket in Log4j up to 1.2.17.

   Changed in 1.2.18+ to complain about its use and do nothing else.
   See <a href="https://logging.apache.org/log4j/1.2/">the log4j 1.2 homepage</a>
   for more information on why JMS is disabled since 1.2.18.

   @author <a HREF="mailto:jay@v-wave.com">Jay Funnell</a>
   @deprecated
   @noinspection unused
*/

public class TelnetAppender extends AppenderSkeleton {

  static final String TELNET_APPENDER_UNSUPPORTED =
      "ERROR-LOG4J-NETWORKING-UNSUPPORTED: TelnetAppender unsupported!" +
      " This is a breaking change in Log4J 1 >=1.2.18. Stop using this class!";

  public TelnetAppender() {
    LogLog.error(TELNET_APPENDER_UNSUPPORTED);
  }

  public TelnetAppender(boolean isActive) {
    super(isActive);
    LogLog.error(TELNET_APPENDER_UNSUPPORTED);
  }

  /**
      In 1.2.17 this appender required a layout to format the text to the
      attached client(s), keep the same for compatibility. */
  public boolean requiresLayout() {
    return true;
  }

  public void activateOptions() {
    LogLog.error(TELNET_APPENDER_UNSUPPORTED);
  }

  public
  int getPort() {
    return 0;
  }

  public
  void setPort(int port) {
  }

  public void close() {
  }

  protected void append(LoggingEvent event) {
    errorHandler.error(TELNET_APPENDER_UNSUPPORTED);
  }

  /** @noinspection InnerClassMayBeStatic, FinalizeNotProtected */
  protected class SocketHandler extends Thread {

    public void finalize() {
        close();
    }
      
    public void close() {
    }

    public synchronized void send(final String message) {
      errorHandler.error(TELNET_APPENDER_UNSUPPORTED);
    }

    public void run() {
      errorHandler.error(TELNET_APPENDER_UNSUPPORTED);
    }

    public SocketHandler(int port) throws IOException {
      LogLog.error(TELNET_APPENDER_UNSUPPORTED);
      throw new IOException(TELNET_APPENDER_UNSUPPORTED);
    }
  }
}
