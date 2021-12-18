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

import java.net.Socket;

import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.LoggingEvent;

// Contributors:  Moses Hohman <mmhohman@rainbow.uchicago.edu>

/**
   Read {@link LoggingEvent} objects sent from a remote client using
   Sockets (TCP) in Log4j up to 1.2.17.

   Changed in 1.2.18+ to complain about its use and do nothing else.
   See <a href="https://logging.apache.org/log4j/1.2/">the log4j 1.2 homepage</a>
   for more information on why JMS is disabled since 1.2.18.

   @author  Ceki G&uuml;lc&uuml;

   @since 0.8.4
   @deprecated
   @noinspection unused
*/
public class SocketNode implements Runnable {

  static final String SOCKET_NODE_UNSUPPORTED =
      "ERROR-LOG4J-NETWORKING-UNSUPPORTED: SocketNode unsupported!" +
      " This is a breaking change in Log4J 1 >=1.2.18. Stop using this class!";

  public SocketNode(Socket socket, LoggerRepository hierarchy) {
    LogLog.error(SOCKET_NODE_UNSUPPORTED);
  }

  public void run() {
    LogLog.error(SOCKET_NODE_UNSUPPORTED);
  }
}
