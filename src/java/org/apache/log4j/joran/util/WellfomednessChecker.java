/*
 * Created on Nov 22, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.log4j.joran.util;


import java.util.List;
import org.apache.log4j.helpers.Constants;
import org.apache.log4j.spi.ErrorItem;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * As the name indicated this ContentHander is used to check the
 * well-formedness of an XML document.
 * 
 * @author Ceki Gulcu
 *
 */
public class WellfomednessChecker extends DefaultHandler {
  
  List errorList;
  EntityResolver entityResolver;
  
  WellfomednessChecker(List errorList) {
    this.errorList = errorList;
  }
  
  public void error(SAXParseException spe) throws SAXException {
    errorReport(spe);
  }

  public void fatalError(SAXParseException spe) throws SAXException {
    errorReport(spe);
  }

  public void warning(SAXParseException spe) throws SAXException {
    errorReport(spe);
  }
  
  private void errorReport(SAXParseException spe) throws SAXException {
    int line = spe.getLineNumber();
    ErrorItem errorItem = new ErrorItem("Parsing warning", spe);
    errorItem.setLineNumber(line);
    errorItem.setColNumber(spe.getColumnNumber());
    errorList.add(errorItem);
    if(line == 2) {
      ErrorItem e1 = new ErrorItem("The 'log4j.dtd' is no longer used nor needed.");
      errorList.add(e1);
      ErrorItem e2 = new ErrorItem("See "+Constants.CODES_HREF+"#log4j_dtd for more details.");
      errorList.add(e2);
    }
  }
  

  
}
