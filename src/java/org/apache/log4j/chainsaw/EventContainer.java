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

package org.apache.log4j.chainsaw;

import org.apache.log4j.rule.Rule;
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
 * @author Stephen Pain
 *
 */
public interface EventContainer extends SortTableModel, LoggerNameModel {
  /**
   * Adds an EventCountListener, to be notified when the # of events changes
   * @param listener
   */
  void addEventCountListener(EventCountListener listener);

  void addPropertyChangeListener(PropertyChangeListener l);

  void addPropertyChangeListener(
    String propertyName, PropertyChangeListener l);

  /**
   * Adds a NewKeyListener to be notified when unique Key (Property keys)
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

  List getMatchingEvents(Rule rule);

  /**
   * Returns true if this model is Cyclic (bounded) or not.
   * @return true/false
   */
  boolean isCyclic();

  /**
   * Configures this model to use Cyclic or non-cyclic models.
   * This method should fire a property Change event if
   * it involves an actual change in the underlying model.
   *
   * This method does nothing if there is no change in proprty.
   * @param cyclic
   */
  void setCyclic(boolean cyclic);

  /**
   * If this container is in Cyclic mode, returns the Size of the cyclic buffer,
   * otherwise this method throws an IllegalStateException, when in unlimited
   * mode, this method has no meaning.
   *
   * @throws IllegalStateException if this containers isCyclic() method returns false.
   * @return int size of the cyclic buffer
   */
  int getMaxSize();

  /**
   * Locates a row number, starting from startRow, matching the rule provided
   *
   * @param rule
   * @param startRow
   * @param searchForward
   */
  int find(Rule rule, int startRow, boolean searchForward);

  /**
   * Returns a copied list of all the event in the model.
   */
  List getAllEvents();

  /**
   * Returns a copied list containing the events in the model with filter applied
   */
  List getFilteredEvents();
  
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
   * Return the last added row.
   */
  int getLastAdded();

  /**
   * Adds a row to the model.
   * @param row
   * @param valueIsAdjusting
   * @return flag representing whether or not the row is being displayed (not filtered)
   */
  boolean isAddRow(LoggingEvent e, boolean valueIsAdjusting);

  /**
   * Fire appropriate table update events for the range.
   */
  void fireTableEvent(int begin, int end, int count);

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
}
