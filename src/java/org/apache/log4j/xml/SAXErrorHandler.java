/*
 * Copyright 1999-2005 The Apache Software Foundation.
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

package org.apache.log4j.xml;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;
import org.apache.log4j.helpers.LogLog;

public class SAXErrorHandler implements ErrorHandler {

  public
  void error(SAXParseException ex) {
    LogLog.error("Parsing error on line "+ex.getLineNumber()+" and column "
		 +ex.getColumnNumber());
    LogLog.error(ex.getMessage(), ex.getException());
    //LogLog.error("pid="+ex.getPublicId()+" sid="+ex.getSystemId());
  }
  
  public
  void fatalError(SAXParseException ex) {
    error(ex);
  }
   
  public
  void warning(SAXParseException ex) {
    LogLog.warn("Parsing error on line "+ex.getLineNumber()+" and column "
		+ex.getColumnNumber());
    LogLog.warn(ex.getMessage(), ex.getException());
  }


}
