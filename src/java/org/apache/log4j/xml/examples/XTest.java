
package org.apache.log4j.xml.examples;


import org.apache.xerces.parsers.DOMParser;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.log4j.Category;
import org.apache.log4j.Priority;
//import org.apache.log4j.helpers.CategoryFactory;
import org.apache.log4j.xml.examples.XPriority;
import org.apache.xerces.parsers.DOMParser;
import java.io.FileInputStream;
import org.xml.sax.InputSource;
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
    try {
      DOMParser domParser = new DOMParser();
      // We want validation.
      domParser.setFeature("http://xml.org/sax/features/validation", true);
      domParser.setErrorHandler(new ReportParserError());
      InputSource inputSource = new InputSource(new FileInputStream(configFile));
      // log4j.dtd is placed in org.apache.log4j/xml/log4j.dtd. The
      // DOMConfigurator class is placed in the same directory and can
      // find it.
      URL dtdURL = DOMConfigurator.class.getResource("log4j.dtd");
      if(dtdURL == null) {
	System.err.println("Could not find log4j.dtd.");
      }
      else {
	System.err.println("URL to log4j.dtd is " + dtdURL.toString());
	inputSource.setSystemId(dtdURL.toString());
      }
      domParser.parse(inputSource);      
      DOMConfigurator.configure(domParser.getDocument().getDocumentElement());
    }
    catch(Exception e) {
      System.err.println("Could not initialize XMLSample program.");
      e.printStackTrace();
      System.exit(1);		
    }
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


