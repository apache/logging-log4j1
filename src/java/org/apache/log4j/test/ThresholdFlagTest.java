
package org.apache.log4j.test;

import org.apache.log4j.*;
	
public class ThresholdFlagTest {

  static Logger logger = Logger.getLogger(ThresholdFlagTest.class);

  public static void main( String[] argv) {

    String type = null;
    if(argv.length == 1) 
      type = argv[0];
    else 
      Usage("Wrong number of arguments.");
     
    PropertyConfigurator.configure(type);

    logger.debug("m1");
    logger.info("m2");
    logger.warn("m3");
    logger.error("m4");
    logger.fatal("m5");
  }

  static
  void Usage(String msg) {
    System.err.println(msg);
    System.err.println( "Usage: java org.apache.log4j.test.ThresholdFlagTest configFile");
    System.exit(1);
  }

}
