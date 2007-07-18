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

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.log4j.spi.ComponentBase;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.OptionHandler;


/**
   Extend this abstract class to create your own log layout format.

   @author Ceki G&uuml;lc&uuml;
   @author Chris Nokes

*/
public abstract class Layout extends ComponentBase implements OptionHandler {
  // Note that the line.separator property can be looked up even by
  // applets.
  public static final String LINE_SEP = System.getProperty("line.separator");
  public static final int LINE_SEP_LEN = LINE_SEP.length();
  
  String header;
  String footer;

  protected boolean ignoresThrowable = true;
  
  /**
   * Implement this method to create your own layout format.
   * */
  public abstract String format(LoggingEvent event);

  /**
     Returns the content type output by this layout. The base class
     returns "text/plain".
  */
  public String getContentType() {
    return "text/plain";
  }

  /**
   * Returns the header for the layout format. There is no default header.
   * */
  public String getHeader() {
    return header;
  }

  /**
   * Returns the footer for the layout format. There is no default footer.
   */
  public String getFooter() {
    return footer;
  }

  
  /**
   * If the layout handles the throwable object contained within 
   * {@link LoggingEvent}, then the layout should return <code>false</code>. 
   * Otherwise, if the layout ignores throwable object, then the layout should 
   * return <code>true</code>.
   * 
   * <p>By default, {@link SimpleLayout}, {@link TTCCLayout}, {@link 
   * PatternLayout} all return <code>true</code>. The {@link 
   * org.apache.log4j.xml.XMLLayout} returns <code>false</code>.
   * 
   * <p>As of log4j version 1.3, ignoresThrowable is a settable property. Thus,
   * you can override a layout's default setting.
   * 
   * @since 0.8.4 
   * */
  public boolean ignoresThrowable() {
    return ignoresThrowable;
  }

  /**
   * 
   * @since 1.3
   * @param ignoresThrowable
  */  
  public void setIgnoresThrowable(boolean ignoresThrowable) {
    this.ignoresThrowable = ignoresThrowable;
  }
  
  
  /**
   * Set the footer. Note that some layout have their own footers and may choose
   * to ignote the footer set here.
   * 
   * @param footer the footer
   * @since 1.3
   */
  public void setFooter(String footer) {
    this.footer = footer;
  }

  /**
   * Set the header. Note that some layout have their own headers and may choose
   * to ignote the header set here.
   *
   * @param header the header
   * @since 1.3
   */
  public void setHeader(String header) {
    this.header = header;
  }
  
}
