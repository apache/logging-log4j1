
package org.log4j;

import org.log4j.spi.LoggingEvent;


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
      sbuf.append(event.getThrowableInformation());
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


  public
  boolean ignoresThrowable() {
    return false;
  }

  public
  void setOption(String key, String value) {
  }
}
