
package org.apache.log4j.xml.examples;

import org.apache.log4j.xml.DOMConfigurator;
import org.apache.log4j.Category;
import org.apache.log4j.Priority;
import org.apache.xerces.parsers.DOMParser;
import java.io.FileInputStream;
import org.xml.sax.InputSource;
import java.net.*;

/**

   This <a href="doc-files/XMLSample.java">example code</a> shows how to
   read an XML based configuration file using a DOM parser.

   <p>Sample XML files <a href="doc-files/sample1.xml">sample1.xml</a>
   and <a href="doc-files/sample2.xml">sample2.xml</a> are provided.

   
   <p>Note that the log4j.dtd is not in the local directory.
   It is found by the class loader.
   
   @author Ceki G&uuml;lc&uuml;

*/
public class XMLSample {
  
  static Category cat = Category.getInstance(XMLSample.class.getName());

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
    System.err.println( "Usage: java " + XMLSample.class.getName() +
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
      DOMConfigurator.configure(domParser.getDocument().getDocumentElement() );
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
    Category root = Category.getRoot();    
    cat.debug("Message " + ++i);
    cat.warn ("Message " + ++i);
    cat.error("Message " + ++i);        
    Exception e = new Exception("Just testing");
    cat.debug("Message " + ++i, e);
  }
}
