
package org.apache.log4j.xml.examples;

import org.apache.log4j.xml.DOMConfigurator;
import org.apache.log4j.Category;
import org.apache.log4j.Priority;
import java.net.*;

/**
   A simple example showing Category sub-classing. It shows the
   minimum steps necessary to implement one's {@link
   org.apache.log4j.spi.CategoryFactory} and that sub-classes can follow the
   hiearchy

   See <b><a href="doc-files/XCategory.java">source
   code</a></b> for more details.
   
 */
public class XTest {

  /**
     This program will just print 
     <pre>
       DEBUG [main] some.cat - Hello world.
     </pre>
     and exit.

   */

  public 
  static 
  void main(String argv[]) {

    if(argv.length == 1) 
      init(argv[0]);
    else 
      Usage("Wrong number of arguments.");
    sample();
  }

  static
  void Usage(String msg) {
    System.err.println(msg);
    System.err.println( "Usage: java " + XTest.class.getName() +
			"configFile");
    System.exit(1);
  }
  
  static
  void init(String configFile) {
    DOMConfigurator.configure(configFile);
  }

  static
  void sample() {
    int i = -1;
    XCategory cat = (XCategory) XCategory.getInstance("some.cat");    

    Category root = Category.getRoot();    
    cat.trace("Message " + ++i);
    cat.debug("Message " + ++i);
    cat.warn ("Message " + ++i);
    cat.error("Message " + ++i);        
    cat.fatal("Message " + ++i);        
    Exception e = new Exception("Just testing");
    cat.debug("Message " + ++i, e);
  }

}


