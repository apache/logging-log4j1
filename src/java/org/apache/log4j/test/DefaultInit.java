
package org.apache.log4j.test;

import org.apache.log4j.Category;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.FileAppender;
	
public class DefaultInit {

  static Category cat = Category.getInstance(DefaultInit.class.getName());

  public static void main( String[] argv) {

    cat.debug("Hello world");
  }

  static
  void Usage(String msg) {
    System.err.println(msg);
    System.err.println( "Usage: java org.apache.log4j.test.DefaultInit ");
    System.exit(1);
  }

}
