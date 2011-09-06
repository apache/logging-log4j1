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
package org.apache.log4j.rewrite;

import junit.framework.*;
import org.apache.log4j.*;
import org.apache.log4j.util.Compare;
import org.apache.log4j.xml.*;

import java.io.InputStream;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Hashtable;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class RewriteAppenderTest extends TestCase {
    public RewriteAppenderTest(final String name) {
        super(name);
    }

    public void setUp() {
        LogManager.getLoggerRepository().resetConfiguration();
        Hashtable context = MDC.getContext();
        if (context != null) {
            context.clear();
        }
    }

    public void tearDown() {
        LogManager.getLoggerRepository().shutdown();
    }

    public void configure(final String resourceName) throws Exception {
        InputStream is = RewriteAppenderTest.class.getResourceAsStream(resourceName);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(is);
        DOMConfigurator.configure(doc.getDocumentElement());
    }


    public void testMapPolicy() throws Exception {
        configure("map.xml");
        Logger logger = Logger.getLogger(RewriteAppenderTest.class);
        logger.info("Message 0");
        MDC.put("p1", "Hola");

        Map msg = new TreeMap();
        msg.put("p1", "Hello");
        msg.put("p2", "World");
        msg.put("x1", "Mundo");
        logger.info(msg);
        msg.put("message", "Message 1");
        logger.info(msg);
        assertTrue(Compare.compare(RewriteAppenderTest.class, "temp", "map.log"));
    }

    private static class BaseBean {
        private final Object p2;
        private final Object x1;

        public BaseBean(final Object p2,
                        final Object x1) {
             this.p2 = p2;
             this.x1 = x1;
        }

        public Object getP2() {
            return p2;
        }

        public Object getX1() {
            return x1;
        }

        public String toString() {
            return "I am bean.";
        }
    }

    private static class MessageBean extends BaseBean {
        private final Object msg;

        public MessageBean(final Object msg,
                           final Object p2,
                           final Object x1) {
            super(p2, x1);
            this.msg = msg;
        }

        public Object getMessage() {
            return msg;
        }
    }

    public void testReflectionPolicy() throws Exception {
        configure("reflection.xml");
        Logger logger = Logger.getLogger(RewriteAppenderTest.class);
        logger.info("Message 0");
        logger.info(new BaseBean("Hello", "World" ));
        MDC.put("p1", "Hola");
        MDC.put("p2", "p2");
        logger.info(new MessageBean("Welcome to The Hub", "Hello", "World" ));
        assertTrue(Compare.compare(RewriteAppenderTest.class, "temp", "reflection.log"));
    }

    public void testPropertyPolicy() throws Exception {
        configure("property.xml");
        Logger logger = Logger.getLogger(RewriteAppenderTest.class);
        logger.info("Message 0");
        MDC.put("p1", "Hola");
        logger.info("Message 1");
        assertTrue(Compare.compare(RewriteAppenderTest.class, "temp", "property.log"));
    }
}
