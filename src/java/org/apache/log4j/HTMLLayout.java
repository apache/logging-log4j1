/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.APL file.  */

package org.apache.log4j;

import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.helpers.OptionConverter;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.Writer;

/**
   This layout outputs events in a HTML table.

   @author Ceki G&uuml;lc&uuml;   
 */
public class HTMLLayout extends Layout {

  protected final int BUF_SIZE = 256;
  protected final int MAX_CAPACITY = 1024;

  static String TRACE_PREFIX = "<br>&nbsp;&nbsp;&nbsp;&nbsp;";

  // output buffer appended to when format() is invoked
  private StringBuffer sbuf = new StringBuffer(BUF_SIZE);

  /**
     A string constant used in naming the option for setting the the
     location information flag.  Current value of this string
     constant is <b>LocationInfo</b>.  

     <p>Note that all option keys are case sensitive.
     
     @deprecated Options are now handled using the JavaBeans paradigm.
     This constant is not longer needed and will be removed in the
     <em>near</em> term.

  */
  public static final String LOCATION_INFO_OPTION = "LocationInfo";

  /**
     A string constant used in naming the option for setting the the
     HTML document title.  Current value of this string
     constant is <b>Title</b>.  
  */
  public static final String TITLE_OPTION = "Title";

  // Print no location info by default
  boolean locationInfo = false;

  String title = "Log4J Logging Statements";

  /**
     Returns a String consisting of one element {@link
     #LOCATION_INFO_OPTION}. 
     
     @deprecated We now use JavaBeans introspection to configure
     components. Options strings are no longer needed.
 */
  public
  String[] getOptionStrings() {
    return new String[] {LOCATION_INFO_OPTION, TITLE_OPTION};
  }

  /**
     Set HTMLLayout specific options.

     <p>The <b>LocationInfo</b> option takes a boolean value. By
     default, it is set to false which means there will be no location
     information output by this layout. If the the option is set to
     true, then the file name and line number of the statement
     at the origin of the log statement will be output. 

     <p>If you are embedding this layout within an {@link
     org.apache.log4j.net.SMTPAppender} then make sure to set the
     <b>LocationInfo</b> option of that appender as well.
     
     @deprecated Use the setter method for the option directly instead
     of the generic <code>setOption</code> method. 

   */
  public
  void setOption(String key, String value) {
    if(value == null) return;

    if (key.equals(LOCATION_INFO_OPTION)) {
      locationInfo = OptionConverter.toBoolean(value, locationInfo);
    }
    else if (key.equals(TITLE_OPTION)) {
      title = value;
    }
  }
  
  /**
     The <b>LocationInfo</b> option takes a boolean value. By
     default, it is set to false which means there will be no location
     information output by this layout. If the the option is set to
     true, then the file name and line number of the statement
     at the origin of the log statement will be output. 

     <p>If you are embedding this layout within an {@link
     org.apache.log4j.net.SMTPAppender} then make sure to set the
     <b>LocationInfo</b> option of that appender as well.
   */
  public
  void setLocationInfo(boolean flag) {
    locationInfo = flag;
  }
  
  /**
     Returns the current value of the <b>LocationInfo</b> option.
   */
  public
  boolean getLocationInfo() {
    return locationInfo;
  }

  /**
    The <b>Title</b> option takes a String value. This option sets the
    document title of the generated HTML document.
    
    <p>Defaults to 'Log4J Logging Statements'.
  */
  public
  void setTitle(String title) {
    this.title = title;
  }

  /**
     Returns the current value of the <b>Title</b> option.
  */
  public
  String getTitle() {
    return title;
  }
  
 /**
     Returns the content type output by this layout, i.e "text/html".
  */
  public
  String getContentType() {
    return "text/html";
  }

  /**
     No options to activate.
  */
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
    sbuf.append(event.timeStamp - event.getStartTime());
    sbuf.append("</td>\r\n");

    sbuf.append("<td>");
    sbuf.append(event.getThreadName());
    sbuf.append("</td>\r\n");


    sbuf.append("<td>");
    if(event.priority.isGreaterOrEqual(Priority.WARN)) {
      sbuf.append("<font color=\"#FF0000\">");
      sbuf.append(event.priority);      
      sbuf.append("</font>");
    } else {
      sbuf.append(event.priority);      
    }
    sbuf.append("</td>\r\n");

    sbuf.append("<td>");
    sbuf.append(event.categoryName);
    sbuf.append("</td>\r\n");

    sbuf.append("<td>");
    sbuf.append(event.getNDC());
    sbuf.append("</td>\r\n");

    if(locationInfo) {
      LocationInfo locInfo = event.getLocationInformation();
      sbuf.append("<td>");
      sbuf.append(locInfo.getFileName());
      sbuf.append(':');
      sbuf.append(locInfo.getLineNumber());
      sbuf.append("</td>\r\n");
    }


    sbuf.append("<td>");
    sbuf.append(event.getRenderedMessage());
    sbuf.append("</td>\r\n");


    sbuf.append("</tr>");

    String[] s = event.getThrowableStrRep(); 
    if(s != null) {
      sbuf.append("\r\n<tr><td colspan=\"7\">");
      appendThrowableAsHTML(s, sbuf);
      sbuf.append("</td></tr>");
    }
    return sbuf.toString();
  }

  void appendThrowableAsHTML(String[] s, StringBuffer sbuf) {
    if(s != null) {
      int len = s.length;
      if(len == 0) 
	return;
      sbuf.append(s[0]);
      sbuf.append(Layout.LINE_SEP);
      for(int i = 1; i < len; i++) {
	sbuf.append(TRACE_PREFIX);
	sbuf.append(s[i]);
	sbuf.append(Layout.LINE_SEP);
      }
    }
  }

  /**
     Returns appropriate HTML headers.
  */
  public
  String getHeader() {
    StringBuffer sbuf = new StringBuffer();
    sbuf.append("<html>\r\n");
    sbuf.append("<head>\r\n");
    sbuf.append("<title>" + title + "</title>\r\n");
    sbuf.append("<body bgcolor=\"#FFFFFF\">\r\n");
    sbuf.append("<table border=\"1\" cellpadding=\"2\">\r\n<tr>\r\n");
    sbuf.append("<th>Time</th><th>Thread</th><th>Priority</th><th>Category</th>");
    sbuf.append("<th>NDC</th>");
    if(locationInfo) {
      sbuf.append("<th>File:Line</th>");
    }
    sbuf.append("<th>Message</th></tr>");
    return sbuf.toString();
  }

  /**
     Returns the appropriate HTML footers.
  */
  public
  String getFooter() {
    return "</table></body></html>";
  }
  
  


  /**
     The HTML layout handles the throwable contained in logging
     events. Hence, this method return <code>false</code>.  */
  public
  boolean ignoresThrowable() {
    return false;
  }

}
