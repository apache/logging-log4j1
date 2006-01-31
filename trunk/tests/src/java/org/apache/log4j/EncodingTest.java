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
package org.apache.log4j;

import junit.framework.TestCase;

import org.apache.log4j.util.BinaryCompare;


/**
 * Tests support for encoding specification.
 * @author Curt Arnold
 * @since 1.3
 */
public class EncodingTest extends TestCase {
    /**
    *   Construct an instance of EncodingTest.
    * @param name test name
    */
    public EncodingTest(final String name) {
        super(name);
    }

    /**
     * Resets configuration after each test.
     */
    public void tearDown() {
        Logger.getRootLogger().getLoggerRepository().resetConfiguration();
    }

    /**
     * Test us-ascii encoding.
     * @throws Exception if test failure
     */
    public void testASCII() throws Exception {
        Logger root = Logger.getRootLogger();
        configure(root, "output/ascii.log", "US-ASCII");
        common(root);
        assertTrue(BinaryCompare.compare("output/ascii.log",
                "witness/encoding/ascii.log"));
    }

    /**
     * Test iso-8859-1 encoding.
     * @throws Exception if test failure
     */
    public void testLatin1() throws Exception {
        Logger root = Logger.getRootLogger();
        configure(root, "output/latin1.log", "iso-8859-1");
        common(root);
        assertTrue(BinaryCompare.compare("output/latin1.log",
                "witness/encoding/latin1.log"));
    }

    /**
     * Test utf-8 encoding.
     * @throws Exception if test failure.
     */
    public void testUtf8() throws Exception {
        Logger root = Logger.getRootLogger();
        configure(root, "output/UTF-8.log", "UTF-8");
        common(root);
        assertTrue(BinaryCompare.compare("output/UTF-8.log",
                "witness/encoding/UTF-8.log"));
    }

    /**
     * Test utf-16 encoding.
     * @throws Exception if test failure.
     */
    public void testUtf16() throws Exception {
        Logger root = Logger.getRootLogger();
        configure(root, "output/UTF-16.log", "UTF-16");
        common(root);
        assertTrue(BinaryCompare.compare("output/UTF-16.log",
                "witness/encoding/UTF-16.log"));
    }

    /**
     * Test utf-16be encoding.
     * @throws Exception if test failure.
     */
    public void testUtf16BE() throws Exception {
        Logger root = Logger.getRootLogger();
        configure(root, "output/UTF-16BE.log", "UTF-16BE");
        common(root);
        assertTrue(BinaryCompare.compare("output/UTF-16BE.log",
                "witness/encoding/UTF-16BE.log"));
    }

    /**
     * Test utf16-le encoding.
     * @throws Exception if test failure.
     */
    public void testUtf16LE() throws Exception {
        Logger root = Logger.getRootLogger();
        configure(root, "output/UTF-16LE.log", "UTF-16LE");
        common(root);
        assertTrue(BinaryCompare.compare("output/UTF-16LE.log",
                "witness/encoding/UTF-16LE.log"));
    }

    /**
     * Configure logging.
     * @param logger logger
     * @param filename logging file name
     * @param encoding encoding
     */
    private void configure(final Logger logger, final String filename,
        final String encoding) {
        PatternLayout layout = new PatternLayout();
        layout.setConversionPattern("%p - %m\\n");
        layout.activateOptions();

        FileAppender appender = new FileAppender();
        appender.setFile(filename);
        appender.setEncoding(encoding);
        appender.setAppend(false);
        appender.setLayout(layout);
        appender.activateOptions();
        logger.addAppender(appender);
        logger.setLevel(Level.INFO);
    }

    /**
     * Common logging requests.
     * @param logger logger
     */
    private void common(final Logger logger) {
        logger.info("Hello, World");

        // pi can be encoded in iso-8859-1
        logger.info("\u00b9");

        //  one each from Latin, Arabic, Armenian, Bengali, CJK and Cyrillic
        logger.info("A\u0605\u0530\u0986\u4E03\u0400");
    }
}
