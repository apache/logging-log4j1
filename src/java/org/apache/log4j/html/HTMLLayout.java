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

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


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
  private static final String DEFAULT_CONVERSION_PATTERN = "%m";

  /**
   * A conversion pattern equivalent to the TTCCCLayout. Current value is
   * <b>%r%t%p%c%x%m</b>.
   */
  private static final String TTCC_CONVERSION_PATTERN = "%r%t%p%c%x%m";
    /**
     * Customized pattern conversion rules are stored under this key in the
     * {@link LoggerRepository} object store.
     */
  private static final String PATTERN_RULE_REGISTRY = "PATTERN_RULE_REGISTRY";

  private static final String TRACE_PREFIX = "<br>&nbsp;&nbsp;&nbsp;&nbsp;";
  protected final int BUF_SIZE = 256;
  protected final int MAX_CAPACITY = 1024;
  private String pattern;
  private LoggingEventPatternConverter[] patternConverters;
  private FormattingInfo[] patternFields;
  private String timezone;
  private String title = "Log4J Log Messages";

  private boolean internalCSS = false;
  private String url2ExternalCSS = "http://logging.apache.org/log4j/docs/css/eventTable-1.0.css";
  
  // Does our PatternConverter chain handle throwable on its own? 
  private boolean chainHandlesThrowable;
  
  
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
    activateOptions();
  }

  /**
   * Set the <b>ConversionPattern </b> option. This is the string which
   * controls formatting and consists of a mix of literal content and
   * conversion specifiers.
   */
  public void setConversionPattern(String conversionPattern) {
    pattern = conversionPattern;
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
      List converters = new ArrayList();
      List fields = new ArrayList();
      Map converterRegistry = null;
      if(this.repository != null) {
          converterRegistry = (Map) this.repository.getObject(PATTERN_RULE_REGISTRY);
      }
      PatternParser.parse(pattern, converters, fields,
              converterRegistry, PatternParser.getPatternLayoutRules(), getLogger());

      patternConverters = new LoggingEventPatternConverter[converters.size()];
      patternFields = new FormattingInfo[converters.size()];

      int i = 0;
      Iterator converterIter = converters.iterator();
      Iterator fieldIter = fields.iterator();
      while(converterIter.hasNext()) {
          Object converter = converterIter.next();
          if (converter instanceof LoggingEventPatternConverter) {
              patternConverters[i] = (LoggingEventPatternConverter) converter;
              chainHandlesThrowable |= patternConverters[i].handlesThrowable();
          } else {
              patternConverters[i] = new LiteralPatternConverter("");
          }
          if (fieldIter.hasNext()) {
              patternFields[i]  = (FormattingInfo) fieldIter.next();
          } else {
              patternFields[i] = FormattingInfo.getDefault();
          }
          i++;
      }
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
   * Returns the value of the internalCSS option. See {@link setInternalCSS} 
   * method for details about the meaning of this option.
   * 
   * @return boolean Value of internalCSS option
   */
  public boolean isInternalCSS() {
    return internalCSS;
  }
  
  /**
   * Set the value of the internalCSS option. If set to true, the generated HTML
   * ouput will include an internal  cascading style sheet. Otherwise, the
   * generated HTML output will include a reference to an external CSS.
   * <p>
   * By default, <code>internalCSS</code> value is set to false, that is, 
   * by default, only a link to an external CSS file will be generated. 
   * 
   * @see #setURL2ExternalCSS
   * 
   * @param internalCSS
   */
  public void setInternalCSS(boolean internalCSS) {
    this.internalCSS = internalCSS;
  }
  
  /**
   * Return the URL to the external CSS file. See {@link #setURL2ExternalCSS} 
   * method for details about the meaning of this option.
   * 
   * @return URL to the external CSS file.
   */
  public String getURL2ExternalCSS() {
    return url2ExternalCSS;
  }
  /**
   * Set the URL for the external CSS file. By default, the external
   * CSS file is set to "http://logging.apache.org/log4j/docs/css/eventTable-1.0.css".
   */
  public void setURL2ExternalCSS(String url2ExternalCSS) {
    this.url2ExternalCSS = url2ExternalCSS;
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
    if(internalCSS) {
      getInternalCSS(sbuf);
    } else {
      sbuf.append("<LINK REL=StyleSheet HREF=\""+url2ExternalCSS+"\" TITLE=\"Basic\">");
    }
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
    sbuf.append("<table cellspacing=\"0\">");
    sbuf.append(Layout.LINE_SEP);


    sbuf.append("<tr class=\"header\">");
    sbuf.append(Layout.LINE_SEP);
    for (int i = 0; i < patternConverters.length; i++) {
        PatternConverter c = patternConverters[i];
        sbuf.append("<td class=\"");
        sbuf.append(c.getStyleClass(null).toLowerCase());
        sbuf.append("\">");
        sbuf.append(c.getName());
        sbuf.append("</td>");
        sbuf.append(Layout.LINE_SEP);
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

    StringBuffer buf = new StringBuffer();
    buf.append(Layout.LINE_SEP);
    buf.append("<tr class=\"");
    buf.append(level);
    if(odd) {
      buf.append(" odd\">");
    } else {
      buf.append(" even\">");
    }
    buf.append(Layout.LINE_SEP);
   
    for(int i = 0; i < patternConverters.length; i++) {
        PatternConverter c = patternConverters[i];
        buf.append("<td class=\"");
        buf.append(c.getStyleClass(event).toLowerCase());
        buf.append("\">");
        int fieldStart = buf.length();
        c.format(event, buf);
        patternFields[i].format(fieldStart, buf);
        buf.append("</td>");
        buf.append(Layout.LINE_SEP);
    }
    buf.append("</tr>");
    buf.append(Layout.LINE_SEP);
    output.write(buf.toString());

    // if the pattern chain handles throwables then no need to do it again here.
    if(!chainHandlesThrowable) {
      String[] s = event.getThrowableStrRep();
      if (s != null) {
        output.write("<tr><td class=\"exception\" colspan=\"6\">");
        appendThrowableAsHTML(s, output);
        output.write("</td></tr>" + Layout.LINE_SEP);
      }
    }
  }
  
  /**
   * Generate an internal CSS file.
   * @param buf The StringBuffer where the CSS file will be placed.
   */
  void getInternalCSS(StringBuffer buf) {

    buf.append("<STYLE  type=\"text/css\">");
    buf.append(Layout.LINE_SEP);
    buf.append("table { margin-left: 2em; margin-right: 2em; border-left: 2px solid #AAA; }");
    buf.append(Layout.LINE_SEP);

    buf.append("TR.even { background: #FFFFFF; }");
    buf.append(Layout.LINE_SEP);

    buf.append("TR.odd { background: #DADADA; }");
    buf.append(Layout.LINE_SEP);

    buf.append("TR.warn TD.level, TR.error TD.level, TR.fatal TD.level {font-weight: bold; color: #FF4040 }");
    buf.append(Layout.LINE_SEP);

    buf.append("TD { padding-right: 1ex; padding-left: 1ex; border-right: 2px solid #AAA; }");
    buf.append(Layout.LINE_SEP);

    buf.append("TD.time, TD.date { text-align: right; font-family: courier, monospace; font-size: smaller; }");
    buf.append(Layout.LINE_SEP);

    buf.append("TD.sn { text-align: right; width: 5ex; font-family: courier, monospace; font-size: smaller; }");
    buf.append(Layout.LINE_SEP);

    buf.append("TD.thread { text-align: left; }");
    buf.append(Layout.LINE_SEP);

    buf.append("TD.level { text-align: right; }");
    buf.append(Layout.LINE_SEP);

    buf.append("TD.logger { text-align: left; }");
    buf.append(Layout.LINE_SEP);

    buf.append("TR.header { background: #9090FF; color: #FFF; font-weight: bold; font-size: larger; }");    
    buf.append(Layout.LINE_SEP);

    buf.append("TD.exception { background: #C0C0F0; font-family: courier, monospace;}");
    buf.append(Layout.LINE_SEP);

    buf.append("</STYLE>");
    buf.append(Layout.LINE_SEP);

  }
}
