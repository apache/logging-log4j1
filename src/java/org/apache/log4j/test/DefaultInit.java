
package org.apache.log4j.test;

import org.apache.log4j.Category;
import org.apache.log4j.Logger;

public class DefaultInit {

  static Category cat = Logger.getLogger(DefaultInit.class);

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
