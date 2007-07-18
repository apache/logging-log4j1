/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;


public class SAXErrorHandler implements ErrorHandler {
  Logger logger = LogManager.getLogger(SAXErrorHandler.class);
  
  public void error(SAXParseException ex) {
    logger.error(
      "Parsing error on line " + ex.getLineNumber() + " and column "
      + ex.getColumnNumber());
    logger.error(ex.getMessage(), ex.getException());
    //LogLog.error("pid="+ex.getPublicId()+" sid="+ex.getSystemId());
  }

  public void fatalError(SAXParseException ex) {
    error(ex);
  }

  public void warning(SAXParseException ex) {
    logger.warn(
      "Parsing error on line " + ex.getLineNumber() + " and column "
      + ex.getColumnNumber());
    logger.warn(ex.getMessage(), ex.getException());
  }
}
