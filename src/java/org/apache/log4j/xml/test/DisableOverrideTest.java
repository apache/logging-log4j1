
package org.apache.log4j.xml.test;

import org.apache.log4j.Category;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.FileAppender;

//import org.xml.sax.InputSource;
//import org.apache.log4j.xml.examples.ReportParserError;
//import org.apache.xerces.parsers.DOMParser;

public class DisableOverrideTest {

  static Category CAT = Category.getInstance(DisableOverrideTest.class.getName());

  public static void main( String[] argv) {

    String configFile = null;
    if(argv.length == 1) 
      configFile = argv[0];
    else 
      Usage("Wrong number of arguments.");
     
    //try {
      //DOMParser domParser = new DOMParser();
      //domParser.setFeature("http://xml.org/sax/features/validation", true);
      //domParser.setFeature("http://apache.org/xml/features/validation/dynamic",
      //		   true);      
      //domParser.setErrorHandler(new ReportParserError());      
      //domParser.parse(new InputSource(configFile));
      //DOMConfigurator.configure(domParser.getDocument().getDocumentElement());
    //}
    //catch(Exception e) {
    //System.err.println("Could not initialize test program.");
    //e.printStackTrace();
    // System.exit(1);           
    //}
    DOMConfigurator.configure(configFile);
    DOMConfigurator.disableInfo();       
    CAT.debug("Hello world");
  }

  static
  void Usage(String msg) {
    System.err.println(msg);
    System.err.println("Usage: java "+ DisableOverrideTest.class.getName() +
		       "configFile");
    System.exit(1);
  }

}
