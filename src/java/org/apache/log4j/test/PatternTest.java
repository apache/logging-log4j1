
package org.apache.log4j.test;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Level;
/**
   This class is a test of the PatternLayout class.

   @author Ceki G&uuml;lc&uuml;
*/
public class PatternTest {
  final static Logger logger = Logger.getLogger(PatternTest.class);
  

  public 
  static 
  void main(String argv[]) {

    if(argv.length == 1) 
      init(argv[0]);
    else 
      Usage("Wrong number of arguments.");

    test();
  }

  static
  void Usage(String msg) {
    System.err.println(msg);
    System.err.println( "Usage: java " + PatternTest.class.getName() +
			" configFile");
    System.exit(1);
  }
  static
  void init(String configFile) {
    PropertyConfigurator.configure(configFile);
  }

  static
  void test() {
    int i = -1;
    Logger root = Logger.getRootLogger();
    
    logger.debug("Message " + ++i);
    root.debug("Message " + i);        

    logger.info ("Message " + ++i);
    root.info("Message " + i);        

    logger.warn ("Message " + ++i);
    root.warn("Message " + i);        

    logger.error("Message " + ++i);
    root.error("Message " + i);
    
    logger.log(Level.FATAL, "Message " + ++i);
    root.log(Level.FATAL, "Message " + i);    
    
    Exception e = new Exception("Just testing");
    logger.debug("Message " + ++i, e);
    root.debug("Message " + i, e);
    
    logger.info("Message " + ++i, e);
    root.info("Message " + i, e);    

    logger.warn("Message " + ++i , e);
    root.warn("Message " + i , e);    

    logger.error("Message " + ++i, e);
    root.error("Message " + i, e);    

    logger.log(Level.FATAL, "Message " + ++i, e);
    root.log(Level.FATAL, "Message " + i, e);    
    
    LogManager.shutdown();
  }
}
