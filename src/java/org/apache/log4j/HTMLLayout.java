/*
 * Copyright 1999,2004 The Apache Software Foundation.
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

package org.apache.log4j;

import java.io.IOException;
import java.io.Writer;

import org.apache.log4j.helpers.Transform;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.location.LocationInfo;

/**
 * This layout outputs events in a HTML table.
 * 
 * @author Ceki G&uuml;lc&uuml;
 */
public class HTMLLayout extends Layout {

  static final String TRACE_PREFIX = "<br>&nbsp;&nbsp;&nbsp;&nbsp;";

  /**
   * A string constant used in naming the option for setting the the location
   * information flag. Current value of this string constant is <b>LocationInfo
   * </b>.
   * 
   * <p>
   * Note that all option keys are case sensitive.
   * 
   * @deprecated Options are now handled using the JavaBeans paradigm. This
   *             constant is not longer needed and will be removed in the
   *             <em>near</em> term.
   *  
   */
  public static final String LOCATION_INFO_OPTION = "LocationInfo";

  /**
   * A string constant used in naming the option for setting the the HTML
   * document title. Current value of this string constant is <b>Title </b>.
   */
  public static final String TITLE_OPTION = "Title";

  protected static final int BUF_SIZE = 256;

  protected static final int MAX_CAPACITY = 1024;

  // output buffer appended to when format() is invoked
  private StringBuffer sbuf = new StringBuffer(BUF_SIZE);

  // Print no location info by default
  boolean locationInfo = false;

  String title = "Log4J Log Messages";

  /**
   * Default constructor.
   * 
   * @since 1.3
   */
  public HTMLLayout() {
    super();
    // HTMLLayout prints and does not ignore exceptions. Hence the
    // return value <code>false</code>.
    ignoresThrowable = false;
  }

  /**
   * The <b>LocationInfo </b> option takes a boolean value. By default, it is
   * set to false which means there will be no location information output by
   * this layout. If the the option is set to true, then the file name and line
   * number of the statement at the origin of the log statement will be output.
   * 
   * <p>
   * If you are embedding this layout within an {@link
   * org.apache.log4j.net.SMTPAppender} then make sure to set the
   * <b>LocationInfo </b> option of that appender as well.
   */
  public void setLocationInfo(boolean flag) {
    locationInfo = flag;
  }

  /**
   * Returns the current value of the <b>LocationInfo </b> option.
   */
  public boolean getLocationInfo() {
    return locationInfo;
  }

  /**
   * The <b>Title </b> option takes a String value. This option sets the
   * document title of the generated HTML document.
   * 
   * <p>
   * Defaults to 'Log4J Log Messages'.
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Returns the current value of the <b>Title </b> option.
   */
  public String getTitle() {
    return title;
  }

  /**
   * Returns the content type output by this layout, i.e "text/html".
   */
  public String getContentType() {
    return "text/html";
  }

  /**
   * No options to activate.
   */
  public void activateOptions() {
  }

  public void format(java.io.Writer output, LoggingEvent event)
      throws java.io.IOException {

    output.write(Layout.LINE_SEP + "<tr>" + Layout.LINE_SEP);

    output.write("<td>");
    output.write(Long.toString(event.getTimeStamp()
        - LoggingEvent.getStartTime()));
    output.write("</td>" + Layout.LINE_SEP);

    output.write("<td title=\"" + event.getThreadName() + " thread\">");
    Transform.escapeTags(event.getThreadName(), output);
    output.write("</td>" + Layout.LINE_SEP);

    output.write("<td title=\"Level\">");

    if (event.getLevel().equals(Level.DEBUG)) {
      output.write("<font color=\"#339933\">");
      output.write(event.getLevel().toString());
      output.write("</font>");
    } else if (event.getLevel().isGreaterOrEqual(Level.WARN)) {
      output.write("<font color=\"#993300\"><strong>");
      output.write(event.getLevel().toString());
      output.write("</strong></font>");
    } else {
      output.write(event.getLevel().toString());
    }

    output.write("</td>" + Layout.LINE_SEP);

    output.write("<td title=\"" + event.getLoggerName() + " category\">");
    output.write(event.getLoggerName());
    output.write("</td>" + Layout.LINE_SEP);

    if (locationInfo) {
      LocationInfo locInfo = event.getLocationInformation();
      output.write("<td>");
      Transform.escapeTags(locInfo.getFileName(), output);
      output.write(':');
      output.write(locInfo.getLineNumber());
      output.write("</td>" + Layout.LINE_SEP);
    }

    output.write("<td title=\"Message\">");
    Transform.escapeTags(event.getRenderedMessage(), output);
    output.write("</td>" + Layout.LINE_SEP);
    output.write("</tr>" + Layout.LINE_SEP);

    if (event.getNDC() != null) {
      output
          .write("<tr><td bgcolor=\"#EEEEEE\" style=\"font-size : xx-small;\" colspan=\"6\" title=\"Nested Diagnostic Context\">");
      Transform.escapeTags(event.getNDC(), output);
      output.write("</td></tr>" + Layout.LINE_SEP);
    }

    // If we are told to ignore the throwable we will ignore it. Otherwise,
    // we will print it
    if (!ignoresThrowable) {
      String[] s = event.getThrowableStrRep();

      if (s != null) {
        output
            .write("<tr><td bgcolor=\"#993300\" style=\"color:White; font-size : xx-small;\" colspan=\"6\">");
        appendThrowableAsHTML(s, output);
        output.write("</td></tr>" + Layout.LINE_SEP);
      }
    }
  }

  void appendThrowableAsHTML(String[] s, Writer output) throws IOException {
    if (s != null) {
      int len = s.length;

      if (len == 0) { return; }

      Transform.escapeTags(s[0], output);
      output.write(Layout.LINE_SEP);

      for (int i = 1; i < len; i++) {
        output.write(TRACE_PREFIX);
        Transform.escapeTags(s[i], output);
        output.write(Layout.LINE_SEP);
      }
    }
  }

  /**
   * Returns appropriate HTML headers.
   */
  public String getHeader() {
    StringBuffer sbuf = new StringBuffer();
    sbuf
        .append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">"
            + Layout.LINE_SEP);
    sbuf.append("<html>" + Layout.LINE_SEP);
    sbuf.append("<head>" + Layout.LINE_SEP);
    sbuf.append("<title>" + title + "</title>" + Layout.LINE_SEP);
    sbuf.append("<style type=\"text/css\">" + Layout.LINE_SEP);
    sbuf.append("<!--" + Layout.LINE_SEP);
    sbuf
        .append("body, table {font-family: arial,sans-serif; font-size: x-small;}"
            + Layout.LINE_SEP);
    sbuf.append("th {background: #336699; color: #FFFFFF; text-align: left;}"
        + Layout.LINE_SEP);
    sbuf.append("-->" + Layout.LINE_SEP);
    sbuf.append("</style>" + Layout.LINE_SEP);
    sbuf.append("</head>" + Layout.LINE_SEP);
    sbuf.append("<body bgcolor=\"#FFFFFF\" topmargin=\"6\" leftmargin=\"6\">"
        + Layout.LINE_SEP);
    sbuf.append("<hr size=\"1\" noshade>" + Layout.LINE_SEP);
    sbuf.append("Log session start time " + new java.util.Date() + "<br>"
        + Layout.LINE_SEP);
    sbuf.append("<br>" + Layout.LINE_SEP);
    sbuf
        .append("<table cellspacing=\"0\" cellpadding=\"4\" border=\"1\" bordercolor=\"#224466\" width=\"100%\">"
            + Layout.LINE_SEP);
    sbuf.append("<tr>" + Layout.LINE_SEP);
    sbuf.append("<th>Time</th>" + Layout.LINE_SEP);
    sbuf.append("<th>Thread</th>" + Layout.LINE_SEP);
    sbuf.append("<th>Level</th>" + Layout.LINE_SEP);
    sbuf.append("<th>Category</th>" + Layout.LINE_SEP);

    if (locationInfo) {
      sbuf.append("<th>File:Line</th>" + Layout.LINE_SEP);
    }

    sbuf.append("<th>Message</th>" + Layout.LINE_SEP);
    sbuf.append("</tr>" + Layout.LINE_SEP);

    return sbuf.toString();
  }

  /**
   * Returns the appropriate HTML footers.
   */
  public String getFooter() {
    StringBuffer sbuf = new StringBuffer();
    sbuf.append("</table>" + Layout.LINE_SEP);
    sbuf.append("<br>" + Layout.LINE_SEP);
    sbuf.append("</body></html>");

    return sbuf.toString();
  }
}