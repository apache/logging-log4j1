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

package org.apache.log4j.html;

import org.apache.log4j.Layout;
import org.apache.log4j.helpers.Transform;
import org.apache.log4j.pattern.*;
import org.apache.log4j.spi.LoggingEvent;

import java.io.*;


/**
 * 
 * HTMLLayout outputs events in an HTML table. The content of the table columns
 * are specified using a conversion pattern. See 
 * {@link org.apache.log4j.PatternLayout} for documentation on the available
 * patterns. 
 * 
 * @author Ceki G&uuml;lc&uuml;
 * @author Steve Mactaggart
 * @version 1.3
 */
public class HTMLLayout extends Layout {
  /**
   * Default pattern string for log output. Currently set to the string
   * <b>"%m" </b> which just prints the application supplied message.
   */
  public static final String DEFAULT_CONVERSION_PATTERN = "%m";

  /**
   * A conversion pattern equivalent to the TTCCCLayout. Current value is
   * <b>%r%t%p%c%x%m</b>.
   */
  public static final String TTCC_CONVERSION_PATTERN = "%r%t%p%c%x%m";
  static String TRACE_PREFIX = "<br>&nbsp;&nbsp;&nbsp;&nbsp;";
  protected final int BUF_SIZE = 256;
  protected final int MAX_CAPACITY = 1024;
  private String pattern;
  private PatternConverter head;
  private String timezone;
  private String title = "Log4J Log Messages";

  // counter keeping track of the rows output
  private long counter = 0;
  
  /**
   * Constructs a PatternLayout using the DEFAULT_LAYOUT_PATTERN.
   *
   * The default pattern just produces the application supplied message.
   */
  public HTMLLayout() {
    this(DEFAULT_CONVERSION_PATTERN);
  }

  /**
   * Constructs a PatternLayout using the supplied conversion pattern.
   */
  public HTMLLayout(String pattern) {
    this.pattern = pattern;
    head =
      createPatternParser(
        (pattern == null) ? DEFAULT_CONVERSION_PATTERN : pattern).parse();
  }

  /**
   * Set the <b>ConversionPattern </b> option. This is the string which
   * controls formatting and consists of a mix of literal content and
   * conversion specifiers.
   */
  public void setConversionPattern(String conversionPattern) {
    pattern = conversionPattern;
    head = createPatternParser(conversionPattern).parse();
  }

  /**
   * Returns the value of the <b>ConversionPattern </b> option.
   */
  public String getConversionPattern() {
    return pattern;
  }

  /**
   * Does not do anything as options become effective
   */
  public void activateOptions() {
    // nothing to do.
  }

  /**
   * Returns PatternParser used to parse the conversion string. Subclasses may
   * override this to return a subclass of PatternParser which recognize
   * custom conversion characters.
   *
   * @since 0.9.0
   */
  protected PatternParser createPatternParser(String pattern) {
    return new PatternParser(pattern);
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

  void appendThrowableAsHTML(String[] s, Writer sbuf) throws IOException {
    if (s != null) {
      int len = s.length;
      if (len == 0) {
        return;
      }
      Transform.escapeTags(s[0], sbuf);
      sbuf.write(Layout.LINE_SEP);
      for (int i = 1; i < len; i++) {
        sbuf.write(TRACE_PREFIX);
        Transform.escapeTags(s[i], sbuf);
        sbuf.write(Layout.LINE_SEP);
      }
    }
  }

  /**
   * Returns appropriate HTML headers.
   */
  public String getHeader() {
    StringBuffer sbuf = new StringBuffer();
    sbuf.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"");
    sbuf.append(" \"http://www.w3.org/TR/html4/loose.dtd\">");
    sbuf.append(Layout.LINE_SEP);
    sbuf.append("<html>");
    sbuf.append(Layout.LINE_SEP);
    sbuf.append("<head>");
    sbuf.append(Layout.LINE_SEP);
    sbuf.append("<title>" + title + "</title>");
    sbuf.append(Layout.LINE_SEP);
    sbuf.append("<LINK REL=StyleSheet HREF=\"http://logging.apache.org/log4j/docs/css/default0.css\" TITLE=\"Basic\"/>");
    sbuf.append(Layout.LINE_SEP);
    sbuf.append("</head>");
    sbuf.append(Layout.LINE_SEP);
    sbuf.append("<body>");
    sbuf.append(Layout.LINE_SEP);
    
    sbuf.append("<hr size=\"1\" noshade>");
    sbuf.append(Layout.LINE_SEP);
    
    sbuf.append("Log session start time " + new java.util.Date() + "<br>");
    sbuf.append(Layout.LINE_SEP);
    sbuf.append("<br>");
    sbuf.append(Layout.LINE_SEP);
    sbuf.append("<table cellspacing=\"0\" width=\"80%\" >");
    sbuf.append(Layout.LINE_SEP);


    sbuf.append("<tr class=\"header\">");
    sbuf.append(Layout.LINE_SEP);
    PatternConverter c = head;
    while (c != null) {
      sbuf.append("<td class=\"");
      sbuf.append(c.getName().toLowerCase());
      sbuf.append("\">");
      sbuf.append(c.getName());
      sbuf.append("</td>");
      sbuf.append(Layout.LINE_SEP);

      c = c.next;
    }
    sbuf.append("</tr>");
    sbuf.append(Layout.LINE_SEP);

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

  /**
   * The HTML layout handles the throwable contained in logging events. Hence,
   * this method return <code>false</code>.
   */
  public boolean ignoresThrowable() {
    return false;
  }

  /**
   * @see org.apache.log4j.Layout#format(java.io.Writer, org.apache.log4j.spi.LoggingEvent)
   */
  public void format(Writer output, LoggingEvent event)
    throws IOException {
    
    boolean odd = true;
    if(((counter++) & 1) == 0) {
      odd = false;
    }
    
    String level = event.getLevel().toString().toLowerCase(); 
    
    output.write(Layout.LINE_SEP);
    output.write("<tr class=\"");
    output.write(level);
    if(odd) {
      output.write(" odd\">");
    } else {
      output.write(" even\">");
    }
    output.write(Layout.LINE_SEP);
   

    PatternConverter c = head;
    while (c != null) {
      output.write("<td class=\"");
      output.write(c.getName().toLowerCase());
      output.write("\">");      
      c.format(output, event);
      output.write("</td>");
      output.write(Layout.LINE_SEP);
      c = c.next;
    }
    output.write("</tr>");
    output.write(Layout.LINE_SEP);
    
    String[] s = event.getThrowableStrRep();
    if (s != null) {
      output.write("<tr><td class=\"exception\" colspan=\"6\">");
      appendThrowableAsHTML(s, output);
      output.write("</td></tr>" + Layout.LINE_SEP);
    }
  }
}
