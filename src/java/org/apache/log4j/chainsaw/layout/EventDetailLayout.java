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

package org.apache.log4j.chainsaw.layout;

import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;

import java.io.IOException;
import java.io.Writer;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;


/**
 * This layout is used for formatting HTML text for use inside
 * the Chainsaw Event Detail Panel, and the tooltip used
 * when mouse-over on a particular log event row.
 *
 * It relies an an internal PatternLayout to accomplish this, but ensures HTML characters
 * from any LoggingEvent are escaped first.
 *
 * @author Paul Smith <psmith@apache.org>
 */
public class EventDetailLayout extends Layout {
  private PatternLayout patternLayout = new PatternLayout();

  public EventDetailLayout() {
  }

  public void setConversionPattern(String conversionPattern) {
    patternLayout.setConversionPattern(conversionPattern);
    patternLayout.activateOptions();
  }

  public String getConversionPattern() {
    return patternLayout.getConversionPattern();
  }

  /* (non-Javadoc)
   * @see org.apache.log4j.Layout#getFooter()
   */
  public String getFooter() {
    return "";
  }

  /* (non-Javadoc)
   * @see org.apache.log4j.Layout#getHeader()
   */
  public String getHeader() {
    return "";
  }

  //  /* (non-Javadoc)
  //   * @see org.apache.log4j.Layout#format(java.io.Writer, org.apache.log4j.spi.LoggingEvent)
  //   */
  //  public void format(Writer output, LoggingEvent event)
  //    throws IOException {
  //    boolean pastFirst = false;
  //    output.write("<html><body><table cellspacing=0 cellpadding=0>");
  //
  //    List columnNames = ChainsawColumns.getColumnsNames();
  //
  //    Vector v = ChainsawAppenderHandler.convert(event);
  //
  //    /**
  //     * we need to add the ID property from the event
  //     */
  //    v.add(event.getProperty(ChainsawConstants.LOG4J_ID_KEY));
  //    
  //    //             ListIterator iter = displayFilter.getDetailColumns().listIterator();
  //    Iterator iter = columnNames.iterator();
  //    String column = null;
  //    int index = -1;
  //
  //    while (iter.hasNext()) {
  //      column = (String) iter.next();
  //      index = columnNames.indexOf(column);
  //
  //      if (index > -1) {
  //        if (pastFirst) {
  //          output.write("</td></tr>");
  //        }
  //
  //        output.write("<tr><td valign=\"top\"><b>");
  //        output.write(column);
  //        output.write(": </b></td><td>");
  //
  //
  //        if (index<v.size()) {
  //			Object o = v.get(index);
  //
  //			if (o != null) {
  //				output.write(escape(o.toString()));
  //			} else {
  //				output.write("{null}");
  //			}
  //			
  //		}else {
  ////            output.write("Invalid column " + column + " (index=" + index + ")");      
  //        }
  //
  //        pastFirst = true;
  //      }
  //    }
  //
  //    output.write("</table></body></html>");
  //  }

  /**
    * Escape &lt;, &gt; &amp; and &quot; as their entities. It is very
    * dumb about &amp; handling.
    * @param aStr the String to escape.
    * @return the escaped String
    */
  private static String escape(String string) {
    if (string == null) {
      return "";
    }

    final StringBuffer buf = new StringBuffer();

    for (int i = 0; i < string.length(); i++) {
      char c = string.charAt(i);

      switch (c) {
      case '<':
        buf.append("&lt;");

        break;

      case '>':
        buf.append("&gt;");

        break;

      case '\"':
        buf.append("&quot;");

        break;

      case '&':
        buf.append("&amp;");

        break;

      default:
        buf.append(c);

        break;
      }
    }

    return buf.toString();
  }

  /**
   * Takes a source event and copies it into a new LoggingEvent object
   * and ensuring all the internal elements of the event are HTML safe
   * @param event
   * @return new LoggingEvent
   */
  private static LoggingEvent copyForHTML(LoggingEvent event) {
    String fqnCategory = escape(event.getFQNOfLoggerClass());
    Logger logger = Logger.getLogger(event.getLoggerName());
    String threadName = event.getThreadName();
    Object msg = event.getMessage();
    String ndc = event.getNDC();
    Hashtable mdc = formatMDC(event);
    String[] throwableStringRep = event.getThrowableStrRep();
    LocationInfo li = null;
    if (event.locationInformationExists()) {
        li = formatLocationInfo(event);
    }
    Hashtable properties = formatProperties(event);
    LoggingEvent copy =
      new LoggingEvent(
        fqnCategory, logger, event.getTimeStamp(), event.getLevel(), threadName, msg,
        ndc, mdc, throwableStringRep, li, properties);

    return copy;
  }

  /**
  * @param event
  * @return
  */
  private static Hashtable formatMDC(LoggingEvent event) {
    Set keySet = event.getMDCKeySet();
    Hashtable hashTable = new Hashtable();

    for (Iterator iter = keySet.iterator(); iter.hasNext();) {
      Object key = (Object) iter.next();
      Object value = event.getMDC(key.toString());
      hashTable.put(escape(key.toString()), escape(value.toString()));
    }

    return hashTable;
  }

  /**
   * @param event
   * @return
   */
  private static LocationInfo formatLocationInfo(LoggingEvent event) {
    LocationInfo info = event.getLocationInformation();
    LocationInfo newInfo =
      new LocationInfo(
        escape(info.getFileName()), escape(info.getClassName()),
        escape(info.getMethodName()), escape(info.getLineNumber()));

    return newInfo;
  }

  /**
   * @param event
   * @return
   */
  private static Hashtable formatProperties(LoggingEvent event) {
    Set keySet = event.getPropertyKeySet();
    Hashtable hashTable = new Hashtable();

    for (Iterator iter = keySet.iterator(); iter.hasNext();) {
      Object key = (Object) iter.next();
      Object value = event.getProperty(key.toString());
      hashTable.put(escape(key.toString()), escape(value.toString()));
    }

    return hashTable;
  }

  /* (non-Javadoc)
     * @see org.apache.log4j.Layout#ignoresThrowable()
     */
  public boolean ignoresThrowable() {
    return false;
  }

  /* (non-Javadoc)
   * @see org.apache.log4j.spi.OptionHandler#activateOptions()
   */
  public void activateOptions() {
  }

  /* (non-Javadoc)
   * @see org.apache.log4j.Layout#format(java.io.Writer, org.apache.log4j.spi.LoggingEvent)
   */
  public void format(Writer output, LoggingEvent event)
    throws IOException {
    patternLayout.format(output, copyForHTML(event));
  }
}
