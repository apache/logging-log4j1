
package org.log4j.test;

import org.log4j.Category;
import org.log4j.BasicConfigurator;
import org.log4j.PropertyConfigurator;
import org.log4j.SimpleLayout;
import org.log4j.FileAppender;
	
public class DefaultInit {

  static Category cat = Category.getInstance(DefaultInit.class.getName());

  public static void main( String[] argv) {

    cat.debug("Hello world");
  }

  static
  void Usage(String msg) {
    System.err.println(msg);
    System.err.println( "Usage: java org.log4j.test.DefaultInit ");
    System.exit(1);
  }

}
