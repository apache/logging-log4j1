package org.apache.log4j.test; 

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Category;
import org.apache.log4j.Priority;
import java.io.IOException;

public class ROFile {

  static Category cat = Category.getInstance(ROFile.class.getName());
  
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
    System.err.println( "Usage: java " + ROFile.class.getName() +
			"configFile");
    System.exit(1);
  }

  static
  void init(String configFile) {
    PropertyConfigurator.configure(configFile);
  }

  static
  void test() {
    int i = -1;
    cat.debug("Message " + ++i);
  }
}
