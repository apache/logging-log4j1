/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.APL file.
 */

package org.apache.log4j.helpers;



import java.io.Writer;
import java.io.FilterWriter;
import java.io.IOException;
import org.apache.log4j.spi.ErrorHandler;

/**
   SyslogQuietWriter extends QuietWriter by prepending the syslog
   priority code before each printed String.

   @since 0.7.3
*/
public class SyslogQuietWriter extends QuietWriter {

  int syslogFacility;
  int priority;

  public
  SyslogQuietWriter(Writer writer, int syslogFacility, ErrorHandler eh) {
    super(writer, eh);
    this.syslogFacility = syslogFacility;
  }

  public
  void setPriority(int priority) {
    this.priority = priority;
  }

  public
  void setSyslogFacility(int syslogFacility) {
    this.syslogFacility = syslogFacility;
  }
  
  public
  void write(String string) {
    super.write("<"+(syslogFacility | priority)+">" + string);
  }
}
