/*
 * Created on Nov 22, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.log4j.joran.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.log4j.spi.ErrorItem;
import org.xml.sax.InputSource;


/**
 * @author ceki
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class XMLUtil {
  public static final int  CANT_SAY = 0;  
  public static final int  WELL_FORMED = 1; 
  public static final int  ILL_FORMED = 2;
  public static final int  UNRECOVERABLE_ERROR = 3;
  
  public static int checkIfWellFormed(InputStream is, List errorList) {
    int result;
    
    if(is.markSupported()) {
      System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
      try {
        is.mark(64*1024*1024);
        InputSource inputSource = new InputSource(is);
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setValidating(false);
        SAXParser saxParser = spf.newSAXParser();

        int oldSize = errorList.size();
        saxParser.parse(inputSource, new WellfomednessChecker(errorList));
        int newSize = errorList.size();
        
        if(newSize > oldSize) {
          result = ILL_FORMED;
        } else {
          result = WELL_FORMED;
        }
      } catch(Exception ex) {
        errorList.add(new ErrorItem("Problem while hecking well-formedness", ex));
        result = ILL_FORMED;
      } finally {
        try {
          is.reset();
        } catch(IOException e) {
          result = UNRECOVERABLE_ERROR;
        }
      }
    } else {
      System.out.println("XXXXXXXXXXXXX markSupported NOT supported");
      return CANT_SAY;
    }
    
    return result;
  }
  
}
