/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */


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
