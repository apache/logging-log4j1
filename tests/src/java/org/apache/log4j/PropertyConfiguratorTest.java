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
package org.apache.log4j;

import junit.framework.TestCase;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

/**
 * Test property configurator.
 *
 */
public class PropertyConfiguratorTest extends TestCase {
    public PropertyConfiguratorTest(final String testName) {
        super(testName);
    }

    /**
     * Test for bug 40944.
     * Did not catch IllegalArgumentException on Properties.load
     * and close input stream.
     * @throws IOException if IOException creating properties file.
     */
    public void testBadUnicodeEscape() throws IOException {
        String fileName = "output/badescape.properties";
        FileWriter writer = new FileWriter(fileName);
        writer.write("log4j.rootLogger=\\uXX41");
        writer.close();
        PropertyConfigurator.configure(fileName);
        File file = new File(fileName);
        assertTrue(file.delete()) ;
        assertFalse(file.exists());
    }

    /**
     * Test for bug 40944.
     * configure(URL) never closed opened stream.
     * @throws IOException if IOException creating properties file.
     */
        public void testURL() throws IOException {
        File file = new File("output/unclosed.properties");
        FileWriter writer = new FileWriter(file);
        writer.write("log4j.rootLogger=debug");
        writer.close();
        URL url = file.toURL();
        PropertyConfigurator.configure(url);
        assertTrue(file.delete());
        assertFalse(file.exists());
    }

    /**
     * Test for bug 40944.
     * configure(URL) did not catch IllegalArgumentException and
     * did not close stream.
     * @throws IOException if IOException creating properties file.
     */
        public void testURLBadEscape() throws IOException {
        File file = new File("output/urlbadescape.properties");
        FileWriter writer = new FileWriter(file);
        writer.write("log4j.rootLogger=\\uXX41");
        writer.close();
        URL url = file.toURL();
        PropertyConfigurator.configure(url);
        assertTrue(file.delete());
        assertFalse(file.exists());
    }

    /**
     * Test processing of log4j.reset property, see bug 17531.
     *
     */
    public void testReset() {
        VectorAppender appender = new VectorAppender();
        appender.setName("A1");
        Logger.getRootLogger().addAppender(appender);
        Properties props = new Properties();
        props.put("log4j.reset", "true");
        PropertyConfigurator.configure(props);
        assertNull(Logger.getRootLogger().getAppender("A1"));
        LogManager.resetConfiguration();
    }

}
