/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.APL file.  */

package org.apache.log4j.net;

import org.apache.log4j.helpers.QuietWriter;
import org.apache.log4j.helpers.TracerPrintWriter;

/**
   SyslogTracerPrintWriter overrides the println function in
   TracerPrintWriter by replacing the TAB character which appear as
   '^I' on syslog files with spaces. It also does not print the "\n".
*/
class SyslogTracerPrintWriter extends TracerPrintWriter {

  static String TAB = "    ";
  
  SyslogTracerPrintWriter(QuietWriter qWriter) {
    super(qWriter);
  }

  /**
     Make the first Exception line print properly by omitting the \n ath the 
     end.
  */
  public
   void println(Object o) {
    this.qWriter.write(o.toString());
  }

  // Note: the Char[] form is handled by the TracerPrinterWriter super
  // class

  /**
     Remove the first character from the string (usually a TAB) and do
     not print "\n"
  */   
  public
  void println(String s) {
      // remove '^I' and replace it with 4 spaces
      this.qWriter.write(TAB+s.substring(1));
  }
}
