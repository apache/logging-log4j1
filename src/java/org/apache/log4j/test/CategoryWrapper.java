
package org.apache.log4j.test;

import org.apache.log4j.*;

/**
   This is just to test that wrapper can work. This implementation is
   actually quite bad and should be avoided.

*/

public class CategoryWrapper {


  Category c;
  static String FQCN = CategoryWrapper.class.getName();

  CategoryWrapper(String name) {
    c = Category.getInstance(name);
  }

  public 
  static 
  void main(String argv[]) {    
    Layout layout = new PatternLayout("%p [%t] %C %F - %m\n");
    Appender out = new FileAppender(layout, System.out);
    CategoryWrapper w1 = new CategoryWrapper("c1");
    w1.addAppender(out);
    w1.print("hello");
  }


  public 
  void addAppender(Appender appender) {
    c.addAppender(appender);
  }

  public 
  void print(String msg) {

    // You have to supply the fully qualified named of the wrapper
    // class to the specially tailored category.log method for
    // PatternLayout's %C conversion pattern to work.

    // We have to add the ".print" string because the invocation of
    // wrapper.print method is made from the wrapper itself (main
    // method). This is highly unusual. The fqcn of the wrapper is
    // normally sufficient.

    c.log(FQCN+".print", Priority.DEBUG, msg, null);
  }
  
  
  static
  void Usage(String msg) {
    System.err.println(msg);
    System.err.println("Usage: java "+CategoryWrapper.class.getName()
		       + " fileName");
    System.exit(1);
  }
}
