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

package org.apache.log4j.test;

import org.apache.log4j.*;

/**
   This is just to test that wrapper can work. This implementation is
   actually quite bad and should be avoided.

*/

public class CategoryWrapper {


  Logger c;
  static String FQCN = CategoryWrapper.class.getName();

  CategoryWrapper(String name) {
    c = Logger.getLogger(name);
  }

  public 
  static 
  void main(String argv[]) {    
    Layout layout = new PatternLayout("%p [%t] %C %F - %m\n");
    Appender out = new ConsoleAppender(layout, ConsoleAppender.SYSTEM_OUT);
    CategoryWrapper w1 = new CategoryWrapper("c1");
    w1.addAppender(out);
    w1.print("hello");
  }


  public 
  void addAppender(Appender appender) {
    c.addAppender(appender);
  }

  public 
  void print(String msg) {

    // You have to supply the fully qualified named of the wrapper
    // class to the specially tailored logger.log method for
    // PatternLayout's %C conversion pattern to work.

    // We have to add the ".print" string because the invocation of
    // wrapper.print method is made from the wrapper itself (main
    // method). This is highly unusual. The fqcn of the wrapper is
    // normally sufficient.

    c.log(FQCN+".print", Priority.DEBUG, msg, null);
  }
  
  
  static
  void Usage(String msg) {
    System.err.println(msg);
    System.err.println("Usage: java "+CategoryWrapper.class.getName()
		       + " fileName");
    System.exit(1);
  }
}
