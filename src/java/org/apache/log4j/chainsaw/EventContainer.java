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

package org.apache.log4j.chainsaw;

import org.apache.log4j.chainsaw.rule.Rule;
import org.apache.log4j.spi.LoggingEvent;

import java.beans.PropertyChangeListener;
import java.util.List;


/**
 * To allow pluggable TableModel implementations for Chainsaw, this interface has been factored out.
 *
 * This interface is still subject to change.
 *
 * @author Paul Smith <psmith@apache.org>
 * @author Scott Deboy <sdeboy@apache.org>
 *
 */
public interface EventContainer extends SortTableModel, FilterChangedListener,
  LoggerNameModel {
  /**
   * Adds an EventCountListener, to be notified when the # of events changes
   * @param listener
   */
  void addEventCountListener(EventCountListener listener);

  void addPropertyChangeListener(PropertyChangeListener l);
  void addPropertyChangeListener(String propertyName, PropertyChangeListener l);

	/**
	 * Adds a NewKeyListener to be notified when unique Key (MDC/Property keys)
	 * arrive into this EventContainer
	 * @param l
	 */
  void addNewKeyListener(NewKeyListener l);

	/**
	 * Removes a listener from being notified of NewKey events.
	 * @param l
	 */
  void removeNewKeyListener(NewKeyListener l);

  /**
   * Clears the model completely
   *
   */
  void clearModel();

  /**
   * Returns true if this model is Cyclic (bounded) or not.
   * @return true/false
   */
  public boolean isCyclic();
  
  /**
   * Configures this model to use Cyclic or non-cyclic models.
   * This method should fire a property Change event if
   * it involves an actual change in the underlying model.
   *
   * This method does nothing if there is no change in proprty.
   * @param cyclic
   */
  public void setCyclic(boolean cyclic);
  
  /**
   * If this container is in Cyclic mode, returns the Size of the cyclic buffer, 
   * otherwise this method throws an IllegalStateException, when in unlimited
   * mode, this method has no meaning.
   * 
   * @throws IllegalStateException if this containers isCyclic() method returns false.
   * @return int size of the cyclic buffer
   */
  public int getMaxSize();

  /**
   * Locates a row number, starting from startRow, containing the text
   * within any column.
   * @param startRow
   * @param text
   */
  int find(int startRow, String text);

  /**
   * Returns a copied list of all the event in the model.
   */
  List getAllEvents();
  
  /**
   * Returns the total number of events currently in the model (all, not just filtered)
   * @return
   */
  int size();

  /**
   * Returns the vector representing the row.
   */
  LoggingEvent getRow(int row);

  /**
   * Adds a row to the model.
   * @param row
   * @param valueIsAdjusting
   * @return flag representing whether or not the row is being displayed (not filtered)
   */
  boolean isAddRow(LoggingEvent e, boolean valueIsAdjusting);

  /**
   * Allow a forced notification of the EventCountListeners
   *
   */
  void notifyCountListeners();

  /**
   * Sets the DisplayFilter in operation
   * @param displayFilter
   */
  void setDisplayRule(Rule displayRule);

  /**
   * Returns the index of the row
   * @param row
   */
  int getRowIndex(LoggingEvent e);

  /**
   * Causes the EventContainer to sort according to it's configured attributes
   */
  void sort();
}
