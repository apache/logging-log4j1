//      Copyright 1996-1999, International Business Machines 
//      Corporation. All Rights Reserved.

//      Copyright 2000, Ceki Gulcu. All Rights Reserved.

//      See the LICENCE file for the terms of distribution.

// Contributors: Christopher Williams 
//               Mathias Bogaert
       
package org.apache.log4j;

import org.apache.log4j.Level;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.DateLayout;
import org.apache.log4j.helpers.RelativeTimeDateFormat;
import org.apache.log4j.helpers.AbsoluteTimeDateFormat;
import org.apache.log4j.helpers.DateTimeDateFormat;
import org.apache.log4j.helpers.ISO8601DateFormat;
import org.apache.log4j.spi.LoggingEvent;

/**
 TTCC layout format consists of time, thread, category and nested
 diagnostic context information, hence the name.
 
 <p>Each of the four fields can be individually enabled or
 disabled. The time format depends on the <code>DateFormat</code>
 used.

 <p>Here is an example TTCCLayout output with the {@link RelativeTimeDateFormat}.

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
  private boolean threadPrinting    = true;    
  private boolean categoryPrefixing = true;
  private boolean contextPrinting   = true;

  
  protected final StringBuffer buf = new StringBuffer(256);   


  /**
     Instantiate a TTCCLayout object with {@link
     RelativeTimeDateFormat} as the date formatter in the local time
     zone.

     @since 0.7.5
  */
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
  public
  void setThreadPrinting(boolean threadPrinting) {
    this.threadPrinting = threadPrinting;
  }
  
  /**
     Returns value of the <b>ThreadPrinting</b> option.
   */
  public
  boolean getThreadPrinting() {
    return threadPrinting;
  }
  
  /**
     The <b>CategoryPrefixing</b> option specifies whether {@link Category}
     name is part of log output or not. This is true by default.
   */
  public
  void setCategoryPrefixing(boolean categoryPrefixing) {
    this.categoryPrefixing = categoryPrefixing;
  }
  
  /**
     Returns value of the <b>CategoryPrefixing</b> option.
   */
  public
  boolean getCategoryPrefixing() {
    return categoryPrefixing;
  }
  
  /**
     The <b>ContextPrinting</b> option specifies log output will include
     the nested context information belonging to the current thread.
     This is true by default.
   */
  public
  void setContextPrinting(boolean contextPrinting) {
    this.contextPrinting = contextPrinting;
  }
  
  /**
     Returns value of the <b>ContextPrinting</b> option.
   */
  public
  boolean getContextPrinting() {
    return contextPrinting;
  }

  /**
   In addition to the level of the statement and message, the
   returned byte array includes time, thread, category and {@link NDC}
   information.
   
   <p>Time, thread, category and diagnostic context are printed
   depending on options.
   
    @param category
    @param level
    @param message

  */
  public
  String format(LoggingEvent event) {

    // Reset buf
    buf.setLength(0);

    dateFormat(buf, event);
    
    if(this.threadPrinting) {
      buf.append('[');
      buf.append(event.getThreadName());
      buf.append("] ");
    }
    buf.append(event.level.toString());
    buf.append(' ');

    if(this.categoryPrefixing) {
      buf.append(event.categoryName);
      buf.append(' ');
    }

    if(this.contextPrinting) {
       String ndc = event.getNDC();
       
      if(ndc != null) {
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
  public
  boolean ignoresThrowable() {
    return true;
  }
} 
