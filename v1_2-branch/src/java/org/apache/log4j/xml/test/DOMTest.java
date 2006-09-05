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

package org.apache.log4j.xml.test;

import org.apache.log4j.xml.DOMConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;
import org.apache.log4j.Level;
//import org.apache.log4j.xml.examples.ReportParserError;
//import org.apache.xerces.parsers.DOMParser;
//import java.io.FileInputStream;
//import org.xml.sax.InputSource;

/**
   @author Ceki G&uuml;lc&uuml;
*/
public class DOMTest {
  static Logger cat = Logger.getLogger(DOMTest.class.getName());


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
    System.err.println( "Usage: java " + DOMTest.class.getName() +
			" configFile");
    System.exit(1);
  }
  
  static
  void init(String configFile) {
    DOMConfigurator.configure(configFile);
  }

  static
  void test() {
    int i = -1;
    Logger root = Logger.getRootLogger();
    
    cat.debug("Message " + ++i);
    root.debug("Message " + i);        

    cat.info ("Message " + ++i);
    root.info("Message " + i);        

    cat.warn ("Message " + ++i);
    root.warn("Message " + i);        

    cat.error("Message " + ++i);
    root.error("Message " + i);
    
    cat.log(Level.FATAL, "Message " + ++i);
    root.log(Level.FATAL, "Message " + i);    
    
    Exception e = new Exception("Just testing");
    cat.debug("Message " + ++i, e);
    root.debug("Message " + i, e);
    
    cat.error("Message " + ++i, e);
    root.error("Message " + i, e);    

    LogManager.shutdown();
  }
}
