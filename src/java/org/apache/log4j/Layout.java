/*
 * ============================================================================
 *                   The Apache Software License, Version 1.1
 * ============================================================================
 *
 *    Copyright (C) 1999 The Apache Software Foundation. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modifica-
 * tion, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of  source code must  retain the above copyright  notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any, must
 *    include  the following  acknowledgment:  "This product includes  software
 *    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
 *    Alternately, this  acknowledgment may  appear in the software itself,  if
 *    and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "log4j" and  "Apache Software Foundation"  must not be used to
 *    endorse  or promote  products derived  from this  software without  prior
 *    written permission. For written permission, please contact
 *    apache@apache.org.
 *
 * 5. Products  derived from this software may not  be called "Apache", nor may
 *    "Apache" appear  in their name,  without prior written permission  of the
 *    Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 * APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 * DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 * ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 * (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * This software  consists of voluntary contributions made  by many individuals
 * on  behalf of the Apache Software  Foundation.  For more  information on the
 * Apache Software Foundation, please see <http://www.apache.org/>.
 *
 */

package org.apache.log4j;

import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.OptionHandler;


/**
   Extend this abstract class to create your own log layout format.

   @author Ceki G&uuml;lc&uuml;

*/
public abstract class Layout implements OptionHandler {
  // Note that the line.separator property can be looked up even by
  // applets.
  public static final String LINE_SEP = System.getProperty("line.separator");
  public static final int LINE_SEP_LEN = LINE_SEP.length();

  /**
     Implement this method to create your own layout format.
  */
  public abstract String format(LoggingEvent event);

  /**
     Returns the content type output by this layout. The base class
     returns "text/plain".
  */
  public String getContentType() {
    return "text/plain";
  }

  /**
     Returns the header for the layout format. The base class returns
     <code>null</code>.  */
  public String getHeader() {
    return null;
  }

  /**
     Returns the footer for the layout format. The base class returns
     <code>null</code>.  */
  public String getFooter() {
    return null;
  }

  /**
     If the layout handles the throwable object contained within
     {@link LoggingEvent}, then the layout should return
     <code>false</code>. Otherwise, if the layout ignores throwable
     object, then the layout should return <code>true</code>.

     <p>The {@link SimpleLayout}, {@link TTCCLayout}, {@link
     PatternLayout} all return <code>true</code>. The {@link
     org.apache.log4j.xml.XMLLayout} returns <code>false</code>.

     @since 0.8.4 */
  public abstract boolean ignoresThrowable();
}
