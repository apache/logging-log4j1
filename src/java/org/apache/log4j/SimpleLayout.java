//      Copyright 1996-1999, International Business Machines 
//      Corporation. All Rights Reserved.


package org.apache.log4j;

import org.apache.log4j.spi.LoggingEvent;

/**
   SimpleLayout consists of the priority of the log statement,
   followed by " - " and then the log message itself. For example,

   <pre>
           DEBUG - Hello world
   </pre>

   <p>
   @author Ceki G&uuml;lc&uuml;
   @since version 0.7.0

   <p>{@link PatternLayout} offers a much more powerful alternative.
*/
public class SimpleLayout extends Layout {

  StringBuffer sbuf = new StringBuffer(128);

  public SimpleLayout() {
  }

  public
  void activateOptions() {
  }
  
  /**
     Return the a log statement in a format consisting of the
     <code>priority</code>, folloed by " - " and then the
     <code>message</code>. For example, <pre> INFO - "A message"
     </pre>

     <p>The <code>category</code> parameter is ignored.
     <p>
     @return A byte array in SimpleLayout format.
    */
  public
  String format(LoggingEvent event) {

    sbuf.setLength(0);
    sbuf.append(event.priority.toString());
    sbuf.append(" - ");
    sbuf.append(event.message);
    sbuf.append(LINE_SEP);
    return sbuf.toString();
  }

  public
  String[] getOptionStrings() {
    return new String[0];
  }

/**
     The SimpleLayout does not handle the throwable contained within
     {@link LoggingEvent LoggingEvents}. Thus, it returns
     <code>true</code>.

     @since version 0.8.4 */
  public
  boolean ignoresThrowable() {
    return true;
  }  

  public
  void setOption(String option, String value) {
  }
}
