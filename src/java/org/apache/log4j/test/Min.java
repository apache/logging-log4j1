/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.APL file.  */

package org.apache.log4j.test; 

import org.apache.log4j.Category;
import org.apache.log4j.Layout;
import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.net.SyslogAppender;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.TTCCLayout;
import org.apache.log4j.Level;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.helpers.AbsoluteTimeDateFormat;
import java.io.IOException;

/**
   This class tests the functionality of the Log class and the
   different layouts.

   @author  Ceki G&uuml;lc&uuml;
*/
public class Min {

  public 
  static 
  void main(String argv[]) {

      if(argv.length == 1) {
	ProgramInit(argv[0]);
      }
      else {
	Usage("Wrong number of arguments.");
      }
      test1();
  }


  static
  void Usage(String msg) {
    System.err.println(msg);
    System.err.println( "Usage: java org.apache.log4j.test.Min " +
			"simple|ttcc");
    System.exit(1);
  }


  /**
    Program wide initialization method.
    */

  static
  void ProgramInit(String layoutType) {

    Appender appender = null;	
    Layout layout = null;
        
    if(layoutType.equals("simple")) 
      layout = new SimpleLayout();
    else if(layoutType.equals("ttcc")) {
      layout = new TTCCLayout(AbsoluteTimeDateFormat.DATE_AND_TIME_DATE_FORMAT);
    }
    else 
      Usage("Wrong layoutType [" + layoutType +"].");


    appender = new ConsoleAppender(layout, "System.out");
    BasicConfigurator.configure(appender);
  }

  
  static
  void test1() {

    int i = 0;

    // In the lines below, the category names are chosen as an aid in
    // remembering their level values. In general, the category
    // names should have no bearing to level values.
    
    Category ERR = Category.getInstance("ERR");
    ERR.setPriority(Level.ERROR);
    Category INF = Category.getInstance("INF");
    INF.setPriority(Level.INFO);
    Category INF_ERR = Category.getInstance("INF.ERR");
    INF_ERR.setPriority(Level.ERROR);
    Category DEB = Category.getInstance("DEB");
    DEB.setPriority(Level.DEBUG);
    
    // Note: categories with undefined level 
    Category INF_UNDEF = Category.getInstance("INF.UNDEF");
    Category INF_ERR_UNDEF = Category.getInstance("INF.ERR.UNDEF");    
    Category UNDEF = Category.getInstance("UNDEF");   


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
}
