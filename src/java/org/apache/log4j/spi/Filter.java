/**
  Copyright (c) 2000, Ceki Gulcu

  Permission is hereby granted, free of charge, to any person
  obtaining a copy of this software and associated documentation files
  (the "Software"), to deal in the Software without restriction,
  including without limitation the rights to use, copy, modify, merge,
  publish, distribute, sublicense, and/or sell copies of the Software,
  and to permit persons to whom the Software is furnished to do so,
  subject to the following conditions:

  The above copyright notice and this permission notice shall be
  included in all copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
  BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
  ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
  CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  SOFTWARE.
*/

package org.apache.log4j.spi;

import org.apache.log4j.spi.LoggingEvent;


/**
   Users should extend this class to implement customized logging
   event filtering. Note that {@link org.apache.log4j.Category} and {@link
   org.apache.log4j.AppenderSkeleton}, the parent class of all standard
   appenders, have built-in filtering rules. It is suggested that you
   first use and understand the built-in rules before rushing to write
   your own custom filters.

   <p>This abstract class assumes and also imposes that filters be
   organized in a linear chain. The {@link #decide
   decide(LoggingEvent)} method of each filter is called sequentially,
   in the order of their addition to the chain.

   <p>The {@link #decide decide(LoggingEvent)} method must return one
   of the integer constants {@link #DENY}, {@link #NEUTRAL} or {@link
   #ACCEPT}.

   <p>If the value {@link #DENY} is returned, then the log event is
   dropped immediately without consulting with the remaining
   filters. 

   <p>If the value {@link #NEUTRAL} is returned, then the next filter
   in the chain is consulted. If there are no more filters in the
   chain, then the log event is logged. Thus, in the presence of no
   filters, the default behaviour is to log all logging events.

   <p>If the value {@link #ACCEPT} is returned, then the log
   event is logged without consulting the remaining filters. 

   <p>The philosophy of log4j filters is largely inspired from the
   Linux ipchains. 

   <p>Note that filtering is only supported by the {@link
   org.apache.log4j.xml.DOMConfigurator DOMConfigurator}. The {@link
   org.apache.log4j.PropertyConfigurator PropertyConfigurator} does not
   support filters.

   @author Ceki G&uuml;lc&uuml;
   @since 0.9.0 */
public abstract class Filter implements OptionHandler {

  /**
     Points to the next filter in the filter chain.
   */
  public Filter next;

  /**
     The log event must be dropped immediately without consulting
     with the remaining filters, if any, in the chain.  */
  public static final int DENY    = -1;
  
  /**
     This filter is neutral with respect to the log event. The
     remaining filters, if any, should be consulted for a final decision.
  */
  public static final int NEUTRAL = 0;

  /**
     The log event must be logged immediately without consulting with
     the remaining filters, if any, in the chain.  */
  public static final int ACCEPT  = 1;


  /**
     Usually filters options become active when set. We provide a
     default do-nothing implementation for convenience.
  */
  public
  void activateOptions() {
  }



  /**     
     <p>If the decision is <code>DENY</code>, then the event will be
     dropped. If the decision is <code>NEUTRAL</code>, then the next
     filter, if any, will be invoked. If the decision is ACCEPT then
     the event will be logged without consulting with other filters in
     the chain.

     @param event The LoggingEvent to decide upon.
     @param decision The decision of the filter.  */
  abstract
  public
  int decide(LoggingEvent event);

}
