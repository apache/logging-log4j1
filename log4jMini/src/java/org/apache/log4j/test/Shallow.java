
package org.apache.log4j.test; 

import org.apache.log4j.*;
import org.apache.log4j.helpers.LogLog;
import java.io.IOException;
import java.util.Enumeration;
/** 
   This class is a shallow test of the various appenders and
   layouts. It also tests their reading of the configuration file.
   @author  Ceki G&uuml;lc&uuml;
*/
public class Shallow {
  
  static Category cat = Category.getInstance(Shallow.class);
  
  public 
  static 
  void main(String argv[]) {
    if(argv.length == 2) 
      init(argv[0], argv[1]);
    else 
      usage("Wrong number of arguments.");
    test();
  }

  static
  void usage(String msg) {
    System.err.println(msg);
    System.err.println( "Usage: java "+ Shallow.class.getName()+" file format");
    System.exit(1);
  }

  static 
  void init(String file, String format) {
    Category root = Category.getRoot();
    Layout layout = new PatternLayout(format);
    try {
      root.addAppender(new FileAppender(layout, file));
    } catch(Exception e) {
      LogLog.warn("Could not open file ["+file+"].");
    }
  }

  static 
  void test() {
    int i = -1; 

    Category root = Category.getRoot();
    cat.debug("Message " + ++i);
    root.debug("Message " + i);
    cat.info ("Message " + ++i);
    cat.warn ("Message " + ++i);
    cat.error("Message " + ++i);
    
    Exception e = new Exception("Just testing");
    cat.debug("Message " + ++i, e);    
    root.info("Message " + ++i, e);
    cat.warn("Message " + ++i, e);
    root.error("Message " + ++i, e);
  }
}
