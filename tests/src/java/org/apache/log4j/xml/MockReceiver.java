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
package org.apache.log4j.xml;

import org.apache.log4j.plugins.PluginSkeleton;

/**
 * Mock receiver used by PluginConfiguratorTest.
 */
public final class MockReceiver extends PluginSkeleton {
    /**
     * Is active.
     */
    private boolean active = false;
    /**
     * Host name.
     */
    private String host;
    /**
     * Port.
     */
    private int port = 0;

    /**
     * Create new instance.
     */
    public MockReceiver() {
        super();
    }

    /**
     * Shutdown.
     */
    public void shutdown() {
        active = false;
    }

    /**
     * Is plugin active.
     * @return true if active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Activate options.
     */
    public void activateOptions() {
        active = true;
    }

    /**
      Get the remote host to connect to for logging events.
      @return host
     */
    public String getHost() {
      return host;
    }

    /**
     * Configures the Host property, this will require activateOptions
     * to be called for this to take effect.
     * @param remoteHost address of remote host.
     */
    public void setHost(final String remoteHost) {
      this.host = remoteHost;
    }
    /**
      Set the remote host to connect to for logging events.
     Equivalent to setHost.
     @param remoteHost address of remote host.
     */
    public void setPort(final String remoteHost) {
      host = remoteHost;
    }

    /**
      Get the remote port to connect to for logging events.
     @return port
     */
    public int getPort() {
      return port;
    }

    /**
      Set the remote port to connect to for logging events.
      @param p port
     */
    public void setPort(final int p) {
      this.port = p;
    }

}
