/* Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.log4j.net;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;

import org.apache.log4j.*;
import org.apache.log4j.helpers.AbsoluteTimeDateFormat;
import org.apache.log4j.util.*;

import org.apache.log4j.xml.DOMConfigurator;
import org.apache.log4j.PropertyConfigurator;

import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;
import org.apache.log4j.NDC;
import org.apache.log4j.xml.XLevel;
import org.apache.log4j.Priority;
import java.io.IOException;
import java.util.Enumeration;

/**
   @author  Ceki G&uuml;lc&uuml;
*/
public class SocketServerTestCase extends TestCase {
  
  static Logger logger = Logger.getLogger(SocketAppenderTestCase.class);

  static public final int PORT = 12345;
  
  static Logger rootLogger = Logger.getRootLogger();

  public SocketServerTestCase(String name) {
    super(name);
  }

  public void setUp() {
    System.out.println("-----------------Setting up test case.");
    SocketAppender socketAppender = new SocketAppender("localhost", PORT);
    rootLogger.addAppender(socketAppender);
  }
  
  public void tearDown() {
    System.out.println("---------------Tearing down test case.");
    rootLogger.removeAllAppenders();
  }
  
  public void test1() {
    common();
  }

  static 
  void common() {
    int i = -1; 
    NDC.push("NDC"); 
    Logger root = Logger.getRootLogger();

    logger.log(XLevel.TRACE, "Message " + ++i);
    logger.debug("Message " + ++i);
    root.debug("Message " + ++i);
    logger.info("Message " + ++i);
    logger.warn("Message " + ++i);
    logger.log(XLevel.LETHAL, "Message " + ++i); //5
    
    Exception e = new Exception("Just testing");
    logger.debug("Message " + ++i, e);
    root.error("Message " + ++i, e);
  }



  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(new SocketServerTestCase("test1"));
    return suite;
  }
}
