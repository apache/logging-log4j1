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

package org.apache.log4j.varia.test; 

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
   This test program reads a config file and attempts to log to the
   appenders specified as many times as specified by the second
   loopLength parameter.
   
   @author Ceki G&uuml;lc&uuml; */
public class Loop {

  static Logger cat = Logger.getLogger(Loop.class);
  static int loopLength;

  public 
  static 
  void main(String argv[]) {

    if(argv.length == 2) 
      init(argv[0], argv[1]);
    else 
      usage("Wrong number of arguments.");
    test();
  }


  static
  void usage(String msg) {
    System.err.println(msg);
    System.err.println( "Usage: java " + Loop.class.getName() +
			"configFile loopLength");
    System.exit(1);
  }

  
  static
  void init(String configFile, String loopStr) {
    PropertyConfigurator.configure(configFile);
    try {
      loopLength   = Integer.parseInt(loopStr);      
    }
    catch(java.lang.NumberFormatException e) {
      e.printStackTrace();
      usage("Could not interpret loopLength ["+ loopStr +"].");
    }
  }

  static
  void test() {
    for(int i=0; i < loopLength; i++) {
      Thread.yield();
      cat.debug("MSG "+i);
    }
  }
}
