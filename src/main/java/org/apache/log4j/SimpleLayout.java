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

import org.apache.log4j.spi.LoggingEvent;

import java.io.Writer;


/**
   SimpleLayout consists of the level of the log statement,
   followed by " - " and then the log message itself. For example,

   <pre>
           DEBUG - Hello world
   </pre>

   <p>
   @author Ceki G&uuml;lc&uuml;
   @since version 0.7.0
   @deprecated since 1.3

   <p>{@link PatternLayout} offers a much more powerful alternative.
*/
public class SimpleLayout extends Layout {
  /**
   *   Result of previous format request.  Not reliable if not
   *       layout is not externally synchronized.
   *   @deprecated since 1.3
   */
  StringBuffer sbuf = new StringBuffer(128);

  public SimpleLayout() {
  }

  public void activateOptions() {
  }

  /**
	 Writes the log statement in a format consisting of the
	 <code>level</code>, followed by " - " and then the
	 <code>message</code>. For example, <pre> INFO - "A message"
	 </pre>

	 <p>The <code>category</code> parameter is ignored.
	 <p>
	 @param event The LoggingEvent to format and write
	*/
  public String format(final LoggingEvent event) {
    StringBuffer sb = new StringBuffer(event.getLevel().toString());
    sb.append(" - ");
    sb.append(event.getRenderedMessage());
    sb.append(LINE_SEP);
    String s = sb.toString();
    sbuf.setLength(0);
    sbuf.append(s);
    return s;
   }
}
