/*
 * Copyright 1999,2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Created on Nov 22, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.log4j.joran.util;

import org.apache.log4j.spi.ErrorItem;
import org.apache.log4j.xml.Log4jEntityResolver;

import org.xml.sax.InputSource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


/**
 * @author ceki
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class XMLUtil {
  public static final int CANT_SAY = 0;
  public static final int WELL_FORMED = 1;
  public static final int ILL_FORMED = 2;
  public static final int UNRECOVERABLE_ERROR = 3;

  public static int checkIfWellFormed(String filename, List errorList) {
    int returnCode;

    FileInputStream fis = null;
    try {
      fis = new FileInputStream(filename);
      returnCode = checkIfWellFormed(new InputSource(fis), errorList);
    } catch (IOException ioe) {
      String errMsg = "Could not open [" + filename + "].";
      errorList.add(new ErrorItem(errMsg, ioe));
      returnCode = UNRECOVERABLE_ERROR;
    } finally {
      if (fis != null) {
        try {
          fis.close();
        } catch (java.io.IOException e) {
        }
      }
    }
    return returnCode;
  }
  
  public static int checkIfWellFormed(URL url, List errorList) {
    int returnCode;
    InputStream in = null;
    
    try {
      in  = url.openStream();
      returnCode = checkIfWellFormed(new InputSource(in), errorList);
    } catch (IOException ioe) {
      String errMsg = "Could not open [" + url + "].";
      errorList.add(new ErrorItem(errMsg, ioe));
      returnCode = UNRECOVERABLE_ERROR;
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (java.io.IOException e) {
        }
      }
    }
    return returnCode;
  }

  private static int checkIfWellFormed(InputSource inputSource, List errorList) {
    int result;
    try {
      SAXParserFactory spf = SAXParserFactory.newInstance();
      spf.setValidating(false);
      SAXParser saxParser = spf.newSAXParser();

      WellfomednessChecker wc = new WellfomednessChecker(errorList);
      wc.setEntityResolver(new Log4jEntityResolver());
      
      saxParser.parse(inputSource, wc);
      result = WELL_FORMED;      
    } catch(org.xml.sax.SAXParseException se) {
      result = ILL_FORMED;
    } catch (Exception ex) {
      errorList.add(
        new ErrorItem("Problem while checking well-formedness", ex));
      result = UNRECOVERABLE_ERROR;
    }

    return result;
  }
}
