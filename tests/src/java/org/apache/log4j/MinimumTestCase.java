/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;

import org.apache.log4j.*;
import org.apache.log4j.helpers.AbsoluteTimeDateFormat;
import org.apache.log4j.util.*;

/**
   A superficial but general test of log4j.
 */
public class MinimumTestCase extends TestCase {

  static String FILTERED = "output/filtered";

  static String EXCEPTION1 = "java.lang.Exception: Just testing";
  static String EXCEPTION2 = "\\s*at .*\\(.*:\\d{1,4}\\)";
  static String EXCEPTION3 = "\\s*at .*\\(Native Method\\)";

  //18 fevr. 2002 20:02:41,551 [main] FATAL ERR - Message 0

  static String TTCC_PAT = AbsoluteTDFFilter.ADAT_PAT+ 
              " \\[main]\\ (DEBUG|INFO|WARN|ERROR|FATAL) .* - Message \\d{1,2}";

  static String TTCC2_PAT = AbsoluteTDFFilter.ADAT_PAT+ 
              " \\[main]\\ (DEBUG|INFO|WARN|ERROR|FATAL) .* - Messages should bear numbers 0 through 23\\.";

  //18 fvr. 2002 19:49:53,456

  Logger root; 
  Logger logger;

  public MinimumTestCase(String name) {
    super(name);
  }

  public void setUp() {
    root = Logger.getRootLogger();
    root.removeAllAppenders();
  }

  public void simple() throws Exception {
    
    Layout layout = new SimpleLayout();
    Appender appender = new FileAppender(layout, "output/simple", false);
    root.addAppender(appender);    
    common();

    Transformer.transform("output/simple", FILTERED, new LineNumberFilter());
    assert(Compare.compare(FILTERED, "witness/simple"));
  }

  public void ttcc() throws Exception {
    
    Layout layout = new TTCCLayout(AbsoluteTimeDateFormat.DATE_AND_TIME_DATE_FORMAT);
    Appender appender = new FileAppender(layout, "output/ttcc", false);
    root.addAppender(appender);    
    common();

    ControlFilter cf1 = new ControlFilter(new String[]{TTCC_PAT, TTCC2_PAT, EXCEPTION1, 
						       EXCEPTION2, EXCEPTION3});

    Transformer.transform("output/ttcc", FILTERED, new Filter[] {cf1, 
						  new LineNumberFilter(), 
                                     new AbsoluteTDFFilter(AbsoluteTDFFilter.ADAT_PAT)});

    assert(Compare.compare(FILTERED, "witness/ttcc"));
  }


  void common() {
    
    int i = 0;

    // In the lines below, the category names are chosen as an aid in
    // remembering their level values. In general, the category names
    // have no bearing to level values.
    
    Logger ERR = Logger.getLogger("ERR");
    ERR.setLevel(Level.ERROR);
    Logger INF = Logger.getLogger("INF");
    INF.setLevel(Level.INFO);
    Logger INF_ERR = Logger.getLogger("INF.ERR");
    INF_ERR.setLevel(Level.ERROR);
    Logger DEB = Logger.getLogger("DEB");
    DEB.setLevel(Level.DEBUG);
    
    // Note: categories with undefined level 
    Logger INF_UNDEF = Logger.getLogger("INF.UNDEF");
    Logger INF_ERR_UNDEF = Logger.getLogger("INF.ERR.UNDEF");    
    Logger UNDEF = Logger.getLogger("UNDEF");   


    // These should all log.----------------------------
    ERR.log(Level.FATAL, "Message " + i); i++;  //0
    ERR.error( "Message " + i); i++;          

    INF.log(Level.FATAL, "Message " + i); i++; // 2
    INF.error( "Message " + i); i++;         
    INF.warn ( "Message " + i); i++; 
    INF.info ( "Message " + i); i++;

    INF_UNDEF.log(Level.FATAL, "Message " + i); i++;  //6
    INF_UNDEF.error( "Message " + i); i++;         
    INF_UNDEF.warn ( "Message " + i); i++; 
    INF_UNDEF.info ( "Message " + i); i++; 
    
    
    INF_ERR.log(Level.FATAL, "Message " + i); i++;  // 10
    INF_ERR.error( "Message " + i); i++;  

     INF_ERR_UNDEF.log(Level.FATAL, "Message " + i); i++; 
    INF_ERR_UNDEF.error( "Message " + i); i++;             

    DEB.log(Level.FATAL, "Message " + i); i++;  //14
    DEB.error( "Message " + i); i++;         
    DEB.warn ( "Message " + i); i++; 
    DEB.info ( "Message " + i); i++; 
    DEB.debug( "Message " + i); i++; 

    
    // defaultLevel=DEBUG
    UNDEF.log(Level.FATAL, "Message " + i); i++;  // 19
    UNDEF.error("Message " + i); i++;         
    UNDEF.warn ("Message " + i); i++; 
    UNDEF.info ("Message " + i); i++; 
    UNDEF.debug("Message " + i, new Exception("Just testing.")); i++;    

    // -------------------------------------------------
    // The following should not log
    ERR.warn("Message " + i);  i++; 
    ERR.info("Message " + i);  i++; 
    ERR.debug("Message " + i);  i++; 
      
    INF.debug("Message " + i);  i++; 
    INF_UNDEF.debug("Message " + i); i++; 


    INF_ERR.warn("Message " + i);  i++; 
    INF_ERR.info("Message " + i);  i++; 
    INF_ERR.debug("Message " + i); i++; 
    INF_ERR_UNDEF.warn("Message " + i);  i++; 
    INF_ERR_UNDEF.info("Message " + i);  i++; 
    INF_ERR_UNDEF.debug("Message " + i); i++; 
    // -------------------------------------------------
      
    INF.info("Messages should bear numbers 0 through 23.");
  }

  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(new MinimumTestCase("simple"));
    suite.addTest(new MinimumTestCase("ttcc"));
    return suite;
  }

}
