
package org.log4j.test; 

import org.log4j.PropertyConfigurator;
import org.log4j.Category;
import org.log4j.NDC;

public class ConfigurationFileParsing {
  
  public 
  static 
  void main(String argv[]) {

    if(argv.length == 1) {
      NDC.push("testing");
      PropertyConfigurator.configure(argv[0]);
      Category root = Category.getRoot();
      root.debug("Message 1");
      root.debug("Message 2");      
      NDC.pop();
    }
    else {
      Usage("Wrong number of arguments.");
    }
    
  }

  static
  void Usage(String msg) {
    System.err.println(msg);
    System.err.println("Usage: java "+ConfigurationFileParsing.class.getName()
		       + " fileName");
    System.exit(1);
  }

  
}
