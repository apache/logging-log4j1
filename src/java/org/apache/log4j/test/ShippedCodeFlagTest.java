
package org.apache.log4j.test;

import org.apache.log4j.Category;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.ConsoleAppender;
	
public class ShippedCodeFlagTest {

  static Category CAT=Category.getInstance(ShippedCodeFlagTest.class.getName());

  public static void main( String[] argv) {

    String type = null;
    if(argv.length == 1) 
      type = argv[0];
    else 
      Usage("Wrong number of arguments.");
     
    if(type.equals("basic")) {
       System.out.println("System property \""+
			  BasicConfigurator.DISABLE_OVERRIDE_KEY +
			  "\" is set to ["+
		  System.getProperty(BasicConfigurator.DISABLE_OVERRIDE_KEY)
			  +"].");
    
       BasicConfigurator.configure(new ConsoleAppender(new SimpleLayout(),
						 ConsoleAppender.SYSTEM_OUT));
     
    }
    else { 
      PropertyConfigurator.configure(type);
    }
    Category.getDefaultHierarchy().disableInfo();       
    CAT.debug("Hello world");
  }

  static
  void Usage(String msg) {
    System.err.println(msg);
    System.err.println( "Usage: java org.apache.log4j.test.ShippedCodeFlagTest " +
			"basic|configFile");
    System.exit(1);
  }

}
