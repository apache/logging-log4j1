/*
 */
package org.apache.log4j.chainsaw;

import javax.swing.text.JTextComponent;

import org.apache.log4j.spi.LoggingEvent;

/**
 * When a particular event row has been selected in a Chainsaw
 * LogPanel, then the formatting of the detail
 * of the event is delegated to the registered EventDetailFormatter
 * instance.
 * 
 * 
 * @author Paul Smith
 */
public interface EventDetailFormatter {

  /**
   * This formatting instance is given an opportunity to 
   * take the LoggingEvent, format it, and configure the JTextComponent
   * with the formatted details.
   * 
   * This method is guaranteed to be invoked by Swing's Event Dispatching
   * thread.
   * 
   * Implementations <b>MUST</b> be thread-safe.
   * 
   * Implementations should ensure that the TextComponent is configured
   * correctly regarding Content types etc, and should make no guarantees
   * as to the current configuration of the JTextComponent.  In particular
   * implementations should not make any assumptions about the JTextComponent
   * between calls, Chainsaw makes no guarantee that the same component will
   * be passed in each time.  This allows other JTextComponents to be 
   * used as requested by the user etc.
   * 
   * @param component the component that should receive the formatted 
   * details of the event
   * 
   * @param event the event object whose information should be formatted
   * and displayed in the JTextComponent
   */
  public void format(JTextComponent component, LoggingEvent event);
}
