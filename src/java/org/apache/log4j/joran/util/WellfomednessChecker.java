/*
 * Created on Nov 22, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.log4j.joran.util;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.spi.ErrorItem;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
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
    ErrorItem errorItem = new ErrorItem("Parsing error", spe);
    errorItem.setLineNumber(spe.getLineNumber());
    errorItem.setColNumber(spe.getColumnNumber());
    errorList.add(errorItem);
  }

  public void fatalError(SAXParseException spe) throws SAXException {
    ErrorItem errorItem = new ErrorItem("Parsing fatal error", spe);
    errorItem.setLineNumber(spe.getLineNumber());
    errorItem.setColNumber(spe.getColumnNumber());
    errorList.add(errorItem);

  }

  public void warning(SAXParseException spe) throws SAXException {
    ErrorItem errorItem = new ErrorItem("Parsing warning", spe);
    errorItem.setLineNumber(spe.getLineNumber());
    errorItem.setColNumber(spe.getColumnNumber());
    errorList.add(errorItem);
  }
  
  public EntityResolver getEntityResolver() {
    return entityResolver;
  }
  
  public void setEntityResolver(EntityResolver entityResolver) {
    this.entityResolver = entityResolver;
  }

  public InputSource resolveEntity(String publicId, String systemId) throws SAXException {
    if(entityResolver == null) {
      // the default implementation is to return null
      return null;
    } else {
      try {
        return entityResolver.resolveEntity(publicId, systemId);
      } catch(IOException ioe) {
        // fall back to the default "implementation"
        return null;
      }
    }
  }
  
}
