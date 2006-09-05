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

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Logger;

public class ROFile {

  static Logger cat = Logger.getLogger(ROFile.class);

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
    System.err.println( "Usage: java " + ROFile.class.getName() +
			"configFile");
    System.exit(1);
  }

  static
  void init(String configFile) {
    PropertyConfigurator.configure(configFile);
  }

  static
  void test() {
    int i = -1;
    cat.debug("Message " + ++i);
  }
}
