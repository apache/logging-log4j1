/*
 * Copyright 1999-2006 The Apache Software Foundation.
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
import org.apache.log4j.Level;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.NDC;


public class SyslogMin {

  final static Logger LOG = Logger.getLogger(SyslogMin.class.getName());

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
    LOG.debug( "Message " + i++);
    LOG.info( "Message " + i++);
    LOG.warn( "Message " + i++);
    LOG.error( "Message " + i++);
    LOG.log(Level.FATAL, "Message " + i++);
    LOG.debug("Message " + i++,  new Exception("Just testing."));
  }
}
