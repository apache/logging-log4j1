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

package org.apache.log4j.net.test;

import org.apache.log4j.*;

public class SMTPMin {

  static Logger cat = Logger.getLogger(SMTPMin.class);

  public
  static
  void main(String argv[]) {
    if(argv.length == 1)
      init(argv[0]);
    else
      usage("Wrong number of arguments.");

    NDC.push("some context");
    test();
  }

  static
  void usage(String msg) {
    System.err.println(msg);
    System.err.println("Usage: java " + SMTPMin.class.getName()
		       + " configFile");
    System.exit(1);
  }

  static
  void init(String configFile) {
    PropertyConfigurator.configure(configFile);
  }


  static
  void test() {
    int i  = 0;
    cat.debug( "Message " + i++);
    cat.debug("Message " + i++,  new Exception("Just testing."));
    cat.info( "Message " + i++);
    cat.warn( "Message " + i++);
    cat.error( "Message " + i++);
    cat.log(Priority.FATAL, "Message " + i++);
    LogManager.shutdown();
    Thread.currentThread().getThreadGroup().list();
  }

}
