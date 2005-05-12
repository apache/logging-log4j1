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
package org.apache.log4j.varia;

import junit.framework.TestCase;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;

import java.net.InetAddress;
import java.net.Socket;


/**
   Test case for varia/ExternallyRolledFileAppender.
   @author Curt Arnold
   @since 1.3
 */
public class ExternallyRolledFileAppenderTest extends TestCase {
    private static final Logger logger = Logger.getLogger(ExternallyRolledFileAppenderTest.class);
    private static final Logger root = Logger.getRootLogger();
    private static final int portNo = 6700;

    public ExternallyRolledFileAppenderTest(final String name) {
        super(name);
    }

    public void setUp() {
    }

    public void tearDown() {
        LogManager.shutdown();

        //      try {
        //        Thread.sleep(100);
        //      } catch(InterruptedException ex) {
        //      }
    }

    /**
     * Test basic rolling functionality.
     */
    public void test1() throws Exception {
        PropertyConfigurator.configure("input/rolling/obsoleteERFA1.properties");

        new File("output/obsoleteERFA-test1.log").delete();
        new File("output/obsoleteERFA-test1.log.1").delete();

        // Write exactly 10 bytes with each log
        for (int i = 0; i < 25; i++) {
            if (i < 10) {
                logger.debug("Hello---" + i);
            } else if (i < 100) {
                logger.debug("Hello--" + i);
            }

            if ((i % 10) == 9) {
                roll();
            }
        }

        assertTrue(new File("output/obsoleteERFA-test1.log").exists());
        assertTrue(new File("output/obsoleteERFA-test1.log.1").exists());
    }

    /**
     * Test externally rolled configured from application.
     *
     * Currently commented out since attempt to listen on socket
     * results in an exception indicating that there is already a listener.
     *
     * @deprecated Class under test is deprecated.
     */
    public void test2() throws Exception {
        PatternLayout layout = new PatternLayout("%m\n");
        org.apache.log4j.varia.ExternallyRolledFileAppender rfa = new org.apache.log4j.varia.ExternallyRolledFileAppender();
        rfa.setName("ROLLING");
        rfa.setLayout(layout);
        rfa.setAppend(false);
        rfa.setMaximumFileSize(100);
        rfa.setFile("output/obsoleteERFA-test2.log");
        rfa.setPort(portNo);
        rfa.activateOptions();
        root.addAppender(rfa);

        new File("output/obsoleteERFA-test2.log").delete();
        new File("output/obsoleteERFA-test2.log.1").delete();

        // Write exactly 10 bytes with each log
        for (int i = 0; i < 25; i++) {
            if (i < 10) {
                logger.debug("Hello---" + i);
            } else if (i < 100) {
                logger.debug("Hello--" + i);
            }

            if ((i % 10) == 9) {
                roll();
            }
        }

        assertTrue(new File("output/obsoleteERFA-test2.log").exists());
        assertTrue(new File("output/obsoleteERFA-test2.log.1").exists());
    }

    /**
     * Sends a roll request to the appender.
     *
     * @deprecated Class under test is deprecated.
     * @throws java.io.IOException
     */
    private void roll() throws java.io.IOException {
        Socket socket = new Socket(InetAddress.getLocalHost(), portNo);
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        dos.writeUTF(ExternallyRolledFileAppender.ROLL_OVER);

        String rc = dis.readUTF();
        assertEquals(ExternallyRolledFileAppender.OK, rc);
    }
}
