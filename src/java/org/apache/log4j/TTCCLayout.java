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


// Contributors: Christopher Williams
//               Mathias Bogaert
package org.apache.log4j;

import org.apache.log4j.helpers.DateLayout;
import org.apache.log4j.spi.LoggingEvent;


/**
 TTCC layout format consists of time, thread, category and nested
 diagnostic context information, hence the name.

 <p>Each of the four fields can be individually enabled or
 disabled. The time format depends on the <code>DateFormat</code>
 used.

 <p>Here is an example TTCCLayout output with the
 {@link org.apache.log4j.helpers.RelativeTimeDateFormat}.

 <pre>
176 [main] INFO  org.apache.log4j.examples.Sort - Populating an array of 2 elements in reverse order.
225 [main] INFO  org.apache.log4j.examples.SortAlgo - Entered the sort method.
262 [main] DEBUG org.apache.log4j.examples.SortAlgo.OUTER i=1 - Outer loop.
276 [main] DEBUG org.apache.log4j.examples.SortAlgo.SWAP i=1 j=0 - Swapping intArray[0] = 1 and intArray[1] = 0
290 [main] DEBUG org.apache.log4j.examples.SortAlgo.OUTER i=0 - Outer loop.
304 [main] INFO  org.apache.log4j.examples.SortAlgo.DUMP - Dump of interger array:
317 [main] INFO  org.apache.log4j.examples.SortAlgo.DUMP - Element [0] = 0
331 [main] INFO  org.apache.log4j.examples.SortAlgo.DUMP - Element [1] = 1
343 [main] INFO  org.apache.log4j.examples.Sort - The next log statement should be an error message.
346 [main] ERROR org.apache.log4j.examples.SortAlgo.DUMP - Tried to dump an uninitialized array.
        at org.apache.log4j.examples.SortAlgo.dump(SortAlgo.java:58)
        at org.apache.log4j.examples.Sort.main(Sort.java:64)
467 [main] INFO  org.apache.log4j.examples.Sort - Exiting main method.
</pre>

  <p>The first field is the number of milliseconds elapsed since the
  start of the program. The second field is the thread outputting the
  log statement. The third field is the level, the fourth field is
  the category to which the statement belongs.

  <p>The fifth field (just before the '-') is the nested diagnostic
  context.  Note the nested diagnostic context may be empty as in the
  first two statements. The text after the '-' is the message of the
  statement.

  <p><b>WARNING</b> Do not use the same TTCCLayout instance from
  within different appenders. The TTCCLayout is not thread safe when
  used in his way. However, it is perfectly safe to use a TTCCLayout
  instance from just one appender.

  <p>{@link PatternLayout} offers a much more flexible alternative.

  @author Ceki G&uuml;lc&uuml;
  @author <A HREF="mailto:heinz.richter@ecmwf.int">Heinz Richter</a>

*/
public class TTCCLayout extends DateLayout {
  // Internal representation of options
  private boolean threadPrinting = true;
  private boolean categoryPrefixing = true;
  private boolean contextPrinting = true;
  protected final StringBuffer buf = new StringBuffer(256);

  /**
     Instantiate a TTCCLayout object with {@link
     org.apache.log4j.helpers.RelativeTimeDateFormat} as the date
     formatter in the local time zone.

     @since 0.7.5 */
  public TTCCLayout() {
    this.setDateFormat(RELATIVE_TIME_DATE_FORMAT, null);
  }

  /**
     Instantiate a TTCCLayout object using the local time zone. The
     DateFormat used will depend on the <code>dateFormatType</code>.

     <p>This constructor just calls the {@link
     DateLayout#setDateFormat} method.

     */
  public TTCCLayout(String dateFormatType) {
    this.setDateFormat(dateFormatType);
  }

  /**
     The <b>ThreadPrinting</b> option specifies whether the name of the
     current thread is part of log output or not. This is true by default.
   */
  public void setThreadPrinting(boolean threadPrinting) {
    this.threadPrinting = threadPrinting;
  }

  /**
     Returns value of the <b>ThreadPrinting</b> option.
   */
  public boolean getThreadPrinting() {
    return threadPrinting;
  }

  /**
     The <b>CategoryPrefixing</b> option specifies whether {@link Category}
     name is part of log output or not. This is true by default.
   */
  public void setCategoryPrefixing(boolean categoryPrefixing) {
    this.categoryPrefixing = categoryPrefixing;
  }

  /**
     Returns value of the <b>CategoryPrefixing</b> option.
   */
  public boolean getCategoryPrefixing() {
    return categoryPrefixing;
  }

  /**
     The <b>ContextPrinting</b> option specifies log output will include
     the nested context information belonging to the current thread.
     This is true by default.
   */
  public void setContextPrinting(boolean contextPrinting) {
    this.contextPrinting = contextPrinting;
  }

  /**
     Returns value of the <b>ContextPrinting</b> option.
   */
  public boolean getContextPrinting() {
    return contextPrinting;
  }

  /**
   In addition to the level of the statement and message, the
   returned byte array includes time, thread, category and {@link NDC}
   information.

   <p>Time, thread, category and diagnostic context are printed
   depending on options.
  */
  public String format(LoggingEvent event) {
    // Reset buf
    buf.setLength(0);

    dateFormat(buf, event);

    if (this.threadPrinting) {
      buf.append('[');
      buf.append(event.getThreadName());
      buf.append("] ");
    }

    buf.append(event.getLevel().toString());
    buf.append(' ');

    if (this.categoryPrefixing) {
      buf.append(event.getLoggerName());
      buf.append(' ');
    }

    if (this.contextPrinting) {
      String ndc = event.getNDC();

      if (ndc != null) {
        buf.append(ndc);
        buf.append(' ');
      }
    }

    buf.append("- ");
    buf.append(event.getRenderedMessage());
    buf.append(LINE_SEP);

    return buf.toString();
  }

  /**
      The TTCCLayout does not handle the throwable contained within
      {@link LoggingEvent LoggingEvents}. Thus, it returns
      <code>true</code>.

      @since version 0.8.4 */
  public boolean ignoresThrowable() {
    return true;
  }
}
