
package org.apache.log4j.test; 

import org.apache.log4j.*;
import org.apache.log4j.helpers.LogLog;
import java.io.IOException;

/**
   This class tests the functionality of the Category class and the
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
    System.err.println( "Usage: java org.apache.log4j.test.Min format");
    System.exit(1);
  }


  /**
    Program wide initialization method.
    */

  static
  void ProgramInit(String format) {
    try {
      Layout layout = new PatternLayout(format);        
      Appender appender = new FileAppender(layout, System.out);
      Category.getRoot().addAppender(appender);
    } catch(Exception e) {
      LogLog.error("Could not initialize log4j.");
    }
  }

  
  static
  void test1() {

    int i = 0;

    // In the lines below, the category names are chosen as an aid in
    // remembering their priority values. In general, the category
    // names should have no bearing to priority values.
    
    Category ERR = Category.getInstance("ERR");
    ERR.setPriority(Priority.ERROR);
    Category INF = Category.getInstance("INF");
    INF.setPriority(Priority.INFO);
    Category INF_ERR = Category.getInstance("INF.ERR");
    INF_ERR.setPriority(Priority.ERROR);
    Category DEB = Category.getInstance("DEB");
    DEB.setPriority(Priority.DEBUG);
    
    // Note: categories with undefined priority 
    Category INF_UNDEF = Category.getInstance("INF.UNDEF");
    Category INF_ERR_UNDEF = Category.getInstance("INF.ERR.UNDEF");    
    Category UNDEF = Category.getInstance("UNDEF");   


    // These should all log.----------------------------
    ERR.fatal("Message " + i); i++;  //0
    ERR.error("Message " + i); i++;          
    ERR.info("Message " + i); 
    ERR.debug("Message " + i); 

    INF.fatal("Message " + i); i++; // 2
    INF.error( "Message " + i); i++;         
    INF.warn ( "Message " + i); i++; 
    INF.info ( "Message " + i); i++;

    INF_UNDEF.fatal( "Message " + i); i++;  //6
    INF_UNDEF.error( "Message " + i); i++;         
    INF_UNDEF.warn ( "Message " + i); i++; 
    INF_UNDEF.info ( "Message " + i); i++; 
    INF_UNDEF.debug ( "Message " + i);
    
    
    INF_ERR.fatal( "Message " + i); i++;  // 10
    INF_ERR.error( "Message " + i); i++;  

    INF_ERR_UNDEF.fatal( "Message " + i); i++; 
    INF_ERR_UNDEF.error( "Message " + i); i++;             

    DEB.fatal( "Message " + i); i++;  //14
    DEB.error( "Message " + i); i++;         
    DEB.warn ( "Message " + i); i++; 
    DEB.info ( "Message " + i); i++; 
    DEB.debug( "Message " + i); i++; 

    
    // defaultPriority=DEBUG
    UNDEF.error("Message " + i); i++;         
    UNDEF.warn ("Message " + i); i++; 
    UNDEF.info ("Message " + i); i++; 
    UNDEF.debug("Message " + i, new Exception("Just testing")); i++;    

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
      
    INF.info("Messages should bear numbers 0 through 22.");
  }     
}
