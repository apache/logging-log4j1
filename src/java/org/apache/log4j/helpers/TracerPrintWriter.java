// WARNING
// WARNING Do not use Log statements in this file.
// WARNING

//      Copyright 1996-2000, International Business Machines 
//      Corporation. All Rights Reserved.

// Contributors: Nocolai <XNH@crisplant.com>

package org.apache.log4j.helpers;

import java.io.PrintWriter;

/**

   A PrintWriter used to print stack traces of exceptions.

   <p>It's output target is always a QuietWriter.
   
 */
public class TracerPrintWriter extends PrintWriter {

  protected QuietWriter qWriter;

  public
  TracerPrintWriter(QuietWriter qWriter) {
    super(qWriter);
    this.qWriter = qWriter;
  }

  final
  public
  void setQuietWriter(QuietWriter qWriter) {
    this.qWriter = qWriter;
  }
        
  public
  void println(Object o) {
    this.qWriter.write(o.toString());
    this.qWriter.write(org.apache.log4j.Layout.LINE_SEP);
  }

  // JDK 1.1.x apprenly uses this form of println while in
  // printStackTrace()
  public
  void println(char[] s) {
    this.println(new String(s));
  }

  public
  void println(String s) {
    this.qWriter.write(s);
    this.qWriter.write(org.apache.log4j.Layout.LINE_SEP);
  }
}

