/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.APL file.  */

package org.apache.log4j;

import org.apache.log4j.spi.LoggingEvent;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.Writer;


public class HTMLLayout extends Layout {

  protected final int BUF_SIZE = 256;
  protected final int MAX_CAPACITY = 1024;

  // output buffer appended to when format() is invoked
  private StringBuffer sbuf = new StringBuffer(BUF_SIZE);

  public
  void activateOptions() {
  }

  public 
  String format(LoggingEvent event) {
    
    if(sbuf.capacity() > MAX_CAPACITY) {
      sbuf = new StringBuffer(BUF_SIZE);
    } else {
      sbuf.setLength(0);
    }
    
    sbuf.append("\r\n\r\n<tr>");


    sbuf.append("<td>");
    sbuf.append(event.priority);
    sbuf.append("</td>\r\n");

    sbuf.append("<td>");
    sbuf.append(event.categoryName);
    sbuf.append("</td>\r\n");

    sbuf.append("<td>");
    sbuf.append(event.threadName);
    sbuf.append("</td>\r\n");

    sbuf.append("<td>");
    sbuf.append(event.message);
    sbuf.append("</td>\r\n");


    sbuf.append("</tr>");

    if(event.throwable != null) {
      sbuf.append("\r\n<tr><td colspan=\"5\">");
      sbuf.append(getThrowableAsHTML(event.throwable));
      sbuf.append("</td></tr>");
    }


    return sbuf.toString();
  }


 /**
     Returns the content type output by this layout, i.e "text/html".
  */
  public
  String getContentType() {
    return "text/html";
  }

  

  /**
     Returns appropriate HTML headers.
  */
  public
  String getHeader() {
    return "<html><body>\r\n<table border=\"1\">\r\n<tr>\r\n"+
     "<th>Priority</th><th>Category</th><th>Thread</th><th>Message</th></tr>";
  }

  /**
     Returns the appropriate HTML footers.
  */
  public
  String getFooter() {
    return "</table></body></html>";
  }
  

  public
  String[] getOptionStrings() {
    return new String[0];
  }

  String getThrowableAsHTML(Throwable throwable) {
    if(throwable == null) 
      return null;
 
    StringWriter sw = new StringWriter();
    HTMLPrintWriter hpw = new HTMLPrintWriter(sw);

    throwable.printStackTrace(hpw);
    return sw.toString();
  }


  public
  boolean ignoresThrowable() {
    return false;
  }

  public
  void setOption(String key, String value) {
  }

  static class HTMLPrintWriter extends PrintWriter {
    
    static String TRACE_PREFIX = "<br>&nbsp;&nbsp;&nbsp;&nbsp;";

    public
    HTMLPrintWriter(Writer writer) {
      super(writer);
    }

    public
    void println(char[] c) {
      write(TRACE_PREFIX);
      this.write(c);
    }

  
    public
    void println(String s) {
      write(TRACE_PREFIX);
      this.write(s);
    }    
  }
}
