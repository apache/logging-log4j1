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

import java.io.IOException;
import java.net.ServerSocket;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

/**
  Sends {@link LoggingEvent} objects to a set of remote log servers,
  usually a {@link SocketNode SocketNodes} in Log4j up to 1.2.17.

  Changed in 1.2.18+ to complain about its use and do nothing else.
  See <a href="https://logging.apache.org/log4j/1.2/">the log4j 1.2 homepage</a>
  for more information on why this class is disabled since 1.2.18..

  @author Mark Womack
  @deprecated
  @noinspection unused
*/
public class SocketHubAppender extends AppenderSkeleton {

  static final String SOCKET_HUB_UNSUPPORTED =
      "ERROR-LOG4J-NETWORKING-UNSUPPORTED: SocketHubAppender unsupported!" +
          " This is a breaking change in Log4J 1 >=1.2.18. Stop using this class!";

  /**
     The default port number of the ServerSocket will be created on. */
  static final int DEFAULT_PORT = 4560;
  
  /**
   * The MulticastDNS zone advertised by a SocketHubAppender
   */
  public static final String ZONE = "_log4j_obj_tcpaccept_appender.local.";

  public SocketHubAppender() {
    LogLog.error(SOCKET_HUB_UNSUPPORTED);
  }

  public
  SocketHubAppender(int _port) {
    LogLog.error(SOCKET_HUB_UNSUPPORTED);
  }

  public
  void activateOptions() {
  }

  synchronized
  public
  void close() {
  }

  public
  void cleanUp() {
  }

  public
  void append(LoggingEvent event) {
  }
  
  /**
     The SocketHubAppender does not use a layout. Hence, this method returns
     <code>false</code>. */
  public
  boolean requiresLayout() {
    return false;
  }
  
  public
  void setPort(int _port) {
	}

  public
  void setApplication(String lapp) {
  }

  public
  String getApplication() {
    return null;
  }
  
  public
  int getPort() {
    return 0;
  }

  public
  void setBufferSize(int _bufferSize) {
  }

  public
  int getBufferSize() {
    return 0;
  }
  
  public
  void setLocationInfo(boolean _locationInfo) {
  }
  
  public
  boolean getLocationInfo() {
    return false;
  }

  public void setAdvertiseViaMulticastDNS(boolean advertiseViaMulticastDNS) {
  }

  public boolean isAdvertiseViaMulticastDNS() {
    return false;
  }

  protected ServerSocket createServerSocket(final int socketPort) throws IOException {
    throw new IOException(SOCKET_HUB_UNSUPPORTED);
  }
}
