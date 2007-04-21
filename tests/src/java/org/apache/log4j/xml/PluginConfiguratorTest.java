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


import junit.framework.TestCase;
import org.apache.log4j.LogManager;
import org.apache.log4j.spi.LoggerRepositoryEx;
import org.apache.log4j.plugins.*;

import java.util.List;

/**
 *
 * Tests for PluginConfigurator.
 */
public final class PluginConfiguratorTest extends TestCase {

    /**
     * Create new instance.
     * @param testName test name.
     */
    public PluginConfiguratorTest(final String testName) {
        super(testName);
    }

    /**
     * Test parsing a file containing a plugin element.
     */
    public void testParse() {
        PluginConfigurator.configure("input/xml/plugins1.xml");
        PluginRegistry plugins =
                ((LoggerRepositoryEx) LogManager.getLoggerRepository()).
                        getPluginRegistry();
        assertTrue(plugins.pluginNameExists("mock1"));
        List receivers = plugins.getPlugins(
                org.apache.log4j.xml.MockReceiver.class);
        assertEquals(1, receivers.size());
        MockReceiver mock1 = (MockReceiver) receivers.get(0);
        assertEquals("mock1", mock1.getName());
        assertEquals("127.0.0.1", mock1.getHost());
        assertEquals(4560, mock1.getPort());
        assertTrue(mock1.isActive());
        plugins.stopAllPlugins();
        assertFalse(mock1.isActive());
    }
}
