
package org.log4j.xml.test;

import org.log4j.xml.DOMConfigurator;
import org.log4j.Category;
import org.log4j.Priority;
import org.log4j.xml.examples.ReportParserError;
import org.apache.xerces.parsers.DOMParser;
import java.io.FileInputStream;
import org.xml.sax.InputSource;

/**
   @author Ceki G&uuml;lc&uuml;
*/
public class SubClassTest {

  static TCategory cat = (TCategory) 
                        TCategory.getInstance(SubClassTest.class.getName());


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
    System.err.println( "Usage: java " + SubClassTest.class.getName() +
			" configFile");
    System.exit(1);
  }
  
  static
  void init(String configFile) {
    try {
      DOMParser domParser = new DOMParser();
      domParser.setFeature("http://xml.org/sax/features/validation", true);
      domParser.setErrorHandler(new ReportParserError());      
      domParser.parse(new InputSource(configFile));
      DOMConfigurator.configure(domParser.getDocument().getDocumentElement() );
    }
    catch(Exception e) {
      System.err.println("Could not initialize test program.");
      e.printStackTrace();
      System.exit(1);		
    }
  }

  static
  void test() {
    int i = -1;
    Category root = Category.getRoot();
    
    cat.trace("Message " + ++i);
    cat.debug("Message " + ++i);
    root.debug("Message " + i);
    cat.info ("Message " + ++i);
    cat.warn ("Message " + ++i);
    cat.error("Message " + ++i);    
    cat.log(Priority.FATAL, "Message " + ++i);    
    Exception e = new Exception("Just testing");
    cat.trace("Message " + ++i, e);
    cat.debug("Message " + ++i, e);
    cat.error("Message " + ++i, e); 
  }
}
