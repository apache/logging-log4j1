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

package org.apache.log4j.xml.examples;

import org.apache.log4j.helpers.LogLog;

/**

   This class is needed for validating a log4j.dtd derived XML file.

   @author Joe Kesselman

   @since 0.8.3
   
 */
public class ReportParserError implements org.xml.sax.ErrorHandler {
  
  void report(String msg, org.xml.sax.SAXParseException e) {
    LogLog.error(msg+e.getMessage()+ "\n\tat line="+ e.getLineNumber()+
		 " col="+e.getColumnNumber()+ " of "+
		 "SystemId=\""+e.getSystemId()+
		 "\" PublicID = \""+e.getPublicId()+'\"');
  }
   
  public void warning(org.xml.sax.SAXParseException e) {
    report("WARNING: ", e);
  }
   
  public void error(org.xml.sax.SAXParseException e) {
    report("ERROR: ", e);
  }
   
  public void fatalError(org.xml.sax.SAXParseException e) {
    report("FATAL: ", e);
  }
}
