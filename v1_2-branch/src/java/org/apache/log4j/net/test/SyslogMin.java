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

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Priority;
import org.apache.log4j.NDC;


public class SyslogMin {

  static Logger CAT = Logger.getLogger(SyslogMin.class);

  public
  static
  void main(String argv[]) {

      if(argv.length == 1) {
	ProgramInit(argv[0]);
      }
      else {
	Usage("Wrong number of arguments.");
      }
      test("someHost");
  }


  static
  void Usage(String msg) {
    System.err.println(msg);
    System.err.println( "Usage: java " + SyslogMin.class + " configFile");
    System.exit(1);
  }


  static
  void ProgramInit(String configFile) {
    int port = 0;
    PropertyConfigurator.configure(configFile);
  }

  static
  void test(String host) {
    NDC.push(host);
    int i  = 0;
    CAT.debug( "Message " + i++);
    CAT.info( "Message " + i++);
    CAT.warn( "Message " + i++);
    CAT.error( "Message " + i++);
    CAT.log(Priority.FATAL, "Message " + i++);
    CAT.debug("Message " + i++,  new Exception("Just testing."));
  }
}
