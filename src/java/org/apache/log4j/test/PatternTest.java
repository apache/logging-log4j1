
package org.apache.log4j.test;

import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Category;
import org.apache.log4j.Priority;
/**
   This class is a test of the PatternLayout class.

   @author Ceki G&uuml;lc&uuml;
*/
public class PatternTest {
  static Category CAT = Category.getInstance(PatternTest.class);


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
    Category root = Category.getRoot();
    
    CAT.debug("Message " + ++i);
    root.debug("Message " + i);        

    CAT.info ("Message " + ++i);
    root.info("Message " + i);        

    CAT.warn ("Message " + ++i);
    root.warn("Message " + i);        

    CAT.error("Message " + ++i);
    root.error("Message " + i);
    
    CAT.log(Priority.FATAL, "Message " + ++i);
    root.log(Priority.FATAL, "Message " + i);    
    
    Exception e = new Exception("Just testing");
    CAT.debug("Message " + ++i, e);
    root.debug("Message " + i, e);
    
    CAT.info("Message " + ++i, e);
    root.info("Message " + i, e);    

    CAT.warn("Message " + ++i , e);
    root.warn("Message " + i , e);    

    CAT.error("Message " + ++i, e);
    root.error("Message " + i, e);    

    CAT.log(Priority.FATAL, "Message " + ++i, e);
    root.log(Priority.FATAL, "Message " + i, e);    
    
    LogManager.shutdown();
  }
}
