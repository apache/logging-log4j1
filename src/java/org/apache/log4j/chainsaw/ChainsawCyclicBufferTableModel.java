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

import org.apache.log4j.helpers.Constants;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.rule.Rule;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.ProgressMonitor;
import javax.swing.event.EventListenerList;
import javax.swing.table.AbstractTableModel;


/**
 * A CyclicBuffer implementation of the EventContainer.
 *
 * NOTE:  This implementation prevents duplicate rows from being added to the model.
 *
 * Ignoring duplicates was added to support receivers which may attempt to deliver the same
 * event more than once but can be safely ignored (for example, the database receiver
 * when set to retrieve in a loop).
 *
 * @author Paul Smith <psmith@apache.org>
 * @author Scott Deboy <sdeboy@apache.org>
 *
 */
class ChainsawCyclicBufferTableModel extends AbstractTableModel
  implements EventContainer, PropertyChangeListener {
  private static final int DEFAULT_CAPACITY = 5000;
  private boolean cyclic = true;
  private int cyclicBufferSize = DEFAULT_CAPACITY;
  List unfilteredList;
  List filteredList;
  Set idSet = new HashSet(cyclicBufferSize);
  private boolean currentSortAscending;
  private int currentSortColumn;
  private EventListenerList eventListenerList = new EventListenerList();
  private List columnNames = new ArrayList(ChainsawColumns.getColumnsNames());
  private boolean sortEnabled = false;
  private boolean reachedCapacity = false;

  //  protected final Object syncLock = new Object();
  private LoggerNameModel loggerNameModelDelegate =
    new LoggerNameModelSupport();

  //because we may be using a cyclic buffer, if an ID is not provided in the property, 
  //use and increment this row counter as the ID for each received row
  int uniqueRow;
  private Set uniquePropertyKeys = new HashSet();
  private Rule displayRule;
  private PropertyChangeSupport propertySupport =
    new PropertyChangeSupport(this);

  public ChainsawCyclicBufferTableModel(int cyclicBufferSize) {
    propertySupport.addPropertyChangeListener("cyclic", new ModelChanger());
    this.cyclicBufferSize = cyclicBufferSize;

    unfilteredList = new CyclicBufferList(cyclicBufferSize);
    filteredList = new CyclicBufferList(cyclicBufferSize);
  }

  /* (non-Javadoc)
   * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
   */
  public void propertyChange(PropertyChangeEvent evt) {
    if (evt.getSource() instanceof Rule) {
      reFilter();
    }
  }

  public List getMatchingEvents(Rule rule) {
    List list = new ArrayList();

    synchronized (unfilteredList) {
      Iterator iter = unfilteredList.iterator();

      while (iter.hasNext()) {
        LoggingEvent event = (LoggingEvent) iter.next();

        if (rule.evaluate(event)) {
          list.add(event);
        }
      }
    }

    return list;
  }

  private void reFilter() {
    synchronized (unfilteredList) {
      try {
        filteredList.clear();

        Iterator iter = unfilteredList.iterator();

        while (iter.hasNext()) {
          LoggingEvent e = (LoggingEvent) iter.next();

          if ((displayRule == null) || (displayRule.evaluate(e))) {
            filteredList.add(e);
          }
        }
      } finally {
        fireTableDataChanged();
        notifyCountListeners();
      }
    }
  }

  public int find(Rule rule, int startLocation, boolean searchForward) {
    synchronized (filteredList) {
      if (searchForward) {
        for (int i = startLocation; i < filteredList.size(); i++) {
          if (rule.evaluate((LoggingEvent) filteredList.get(i))) {
            return i;
          }
        }
      } else {
        for (int i = startLocation; i > -1; i--) {
          if (rule.evaluate((LoggingEvent) filteredList.get(i))) {
            return i;
          }
        }
      }
    }

    return -1;
  }

  /**
   * @param l
   */
  public void removeLoggerNameListener(LoggerNameListener l) {
    loggerNameModelDelegate.removeLoggerNameListener(l);
  }

  /**
   * @param loggerName
   * @return
   */
  public boolean addLoggerName(String loggerName) {
    return loggerNameModelDelegate.addLoggerName(loggerName);
  }

  /**
   * @param l
   */
  public void addLoggerNameListener(LoggerNameListener l) {
    loggerNameModelDelegate.addLoggerNameListener(l);
  }

  /**
   * @return
   */
  public Collection getLoggerNames() {
    return loggerNameModelDelegate.getLoggerNames();
  }

  public void addEventCountListener(EventCountListener listener) {
    eventListenerList.add(EventCountListener.class, listener);
  }

  public boolean isSortable(int col) {
    return true;
  }

  public void notifyCountListeners() {
    EventCountListener[] listeners =
      (EventCountListener[]) eventListenerList.getListeners(
        EventCountListener.class);

    for (int i = 0; i < listeners.length; i++) {
      listeners[i].eventCountChanged(
        filteredList.size(), unfilteredList.size());
    }
  }

  /**
   * Changes the underlying display rule in use.  If there was
   * a previous Rule defined, this Model removes itself as a listener
   * from the old rule, and adds itself to the new rule (if the new Rule is not Null).
   *
   * In any case, the model ensures the Filtered list is made up to date in a separate thread.
   */
  public void setDisplayRule(Rule displayRule) {
    if (this.displayRule != null) {
      this.displayRule.removePropertyChangeListener(this);
    }

    this.displayRule = displayRule;

    if (this.displayRule != null) {
      this.displayRule.addPropertyChangeListener(this);
    }

    reFilter();
  }

  /* (non-Javadoc)
     * @see org.apache.log4j.chainsaw.EventContainer#sort()
     */
  public void sort() {
    if (sortEnabled) {
      synchronized (filteredList) {
        Collections.sort(
          filteredList,
          new ColumnComparator(
            getColumnName(currentSortColumn), currentSortColumn,
            currentSortAscending));
      }

      fireTableRowsUpdated(0, Math.max(filteredList.size() - 1, 0));
    }
  }

  public boolean isSortEnabled() {
    return sortEnabled;
  }

  public void sortColumn(int col, boolean ascending) {
    LogLog.debug("request to sort col=" + col);
    currentSortAscending = ascending;
    currentSortColumn = col;
    sortEnabled = true;
    sort();
  }

  /* (non-Javadoc)
   * @see org.apache.log4j.chainsaw.EventContainer#clear()
   */
  public void clearModel() {
    reachedCapacity = false;

    synchronized (unfilteredList) {
      unfilteredList.clear();
      filteredList.clear();
      idSet.clear();
      uniqueRow = 0;
    }

    fireTableDataChanged();
    notifyCountListeners();
  }

  public List getAllEvents() {
    List list = new ArrayList(unfilteredList.size());

    synchronized (unfilteredList) {
      list.addAll(unfilteredList);
    }

    return list;
  }

  public int getRowIndex(LoggingEvent e) {
    synchronized (filteredList) {
      return filteredList.indexOf(e);
    }
  }

  public int getColumnCount() {
    return columnNames.size();
  }

  public String getColumnName(int column) {
    return columnNames.get(column).toString();
  }

  public LoggingEvent getRow(int row) {
    synchronized (filteredList) {
      if (row < filteredList.size()) {
        return (LoggingEvent) filteredList.get(row);
      }
    }

    return null;
  }

  public int getRowCount() {
    synchronized (filteredList) {
      return filteredList.size();
    }
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
    LoggingEvent event = null;

    synchronized (filteredList) {
      if (rowIndex < filteredList.size()) {
        event = (LoggingEvent) filteredList.get(rowIndex);
      }
    }

    if (event == null) {
      return null;
    }

    LocationInfo info = null;

    if (event.locationInformationExists()) {
      info = event.getLocationInformation();
    }

    if (event == null) {
      LogLog.error("Invalid rowindex=" + rowIndex);
      throw new NullPointerException("Invalid rowIndex=" + rowIndex);
    }

    switch (columnIndex + 1) {
    case ChainsawColumns.INDEX_ID_COL_NAME:

      Object id = event.getProperty(Constants.LOG4J_ID_KEY);

      if (id != null) {
        return id;
      }

      return new Integer(rowIndex);

    case ChainsawColumns.INDEX_LEVEL_COL_NAME:
      return event.getLevel();

    case ChainsawColumns.INDEX_LOGGER_COL_NAME:
      return event.getLoggerName();

    case ChainsawColumns.INDEX_TIMESTAMP_COL_NAME:
      return new Date(event.getTimeStamp());

    case ChainsawColumns.INDEX_MESSAGE_COL_NAME:
      return event.getRenderedMessage();

    case ChainsawColumns.INDEX_NDC_COL_NAME:
      return event.getNDC();

    case ChainsawColumns.INDEX_THREAD_COL_NAME:
      return event.getThreadName();

    case ChainsawColumns.INDEX_THROWABLE_COL_NAME:
      return event.getThrowableStrRep();

    case ChainsawColumns.INDEX_CLASS_COL_NAME:
      return ((info == null)
      || ((info != null) && info.getClassName().equals("?"))) ? ""
                                                              : info
      .getClassName();

    case ChainsawColumns.INDEX_FILE_COL_NAME:
      return ((info == null)
      || ((info != null) && info.getFileName().equals("?"))) ? ""
                                                             : info
      .getFileName();

    case ChainsawColumns.INDEX_LINE_COL_NAME:
      return ((info == null)
      || ((info != null) && info.getLineNumber().equals("?"))) ? ""
                                                               : info
      .getLineNumber();

    case ChainsawColumns.INDEX_METHOD_COL_NAME:
      return ((info == null)
      || ((info != null) && info.getMethodName().equals("?"))) ? ""
                                                               : info
      .getMethodName();

    default:

      if (columnIndex <= columnNames.size()) {
        return event.getProperty(columnNames.get(columnIndex).toString());
      }
    }

    return "";
  }

  public boolean isAddRow(LoggingEvent e, boolean valueIsAdjusting) {
    boolean rowAdded = false;

    Object id = e.getProperty(Constants.LOG4J_ID_KEY);

    if (id == null) {
      id = new Integer(++uniqueRow);
      e.setProperty(Constants.LOG4J_ID_KEY, id.toString());
    }

    //prevent duplicate rows
    if (idSet.contains(id)) {
      return false;
    }

    idSet.add(id);
    unfilteredList.add(e);

    if ((displayRule == null) || (displayRule.evaluate(e))) {
      synchronized (filteredList) {
        filteredList.add(e);
        rowAdded = true;
      }
    }

    /**
     * Is this a new Property key we haven't seen before?  Remember that now MDC has been merged
     * into the Properties collection
     */
    boolean newColumn = uniquePropertyKeys.addAll(e.getPropertyKeySet());

    if (newColumn) {
      /**
       * If so, we should add them as columns and notify listeners.
       */
      for (Iterator iter = e.getPropertyKeySet().iterator(); iter.hasNext();) {
        Object key = iter.next();

        //add all keys except the 'log4jid' key
        if (!columnNames.contains(key) && !(Constants.LOG4J_ID_KEY.equalsIgnoreCase(key.toString()))) {
          columnNames.add(key);
          LogLog.debug("Adding col '" + key + "', columNames=" + columnNames);
          fireNewKeyColumnAdded(
            new NewKeyEvent(
              this, columnNames.indexOf(key), key, e.getProperty(key.toString())));
        }
      }
    }

    if (!valueIsAdjusting) {
      int lastAdded = getLastAdded();
      fireTableEvent(lastAdded, lastAdded, 1);
    }

    return rowAdded;
  }

  public int getLastAdded() {
    int last = 0;

    if (cyclic) {
      last = ((CyclicBufferList) filteredList).getLast();
    } else {
      last = filteredList.size();
    }

    return last;
  }

  public void fireTableEvent(int begin, int end, int count) {
    if (cyclic) {
      if (!reachedCapacity) {
        //if we didn't loop and it's the 1st time, insert
        if ((begin + count) < cyclicBufferSize) {
          fireTableRowsInserted(begin, end);
        } else {
          //we did loop - insert and then update rows
          fireTableRowsInserted(begin, cyclicBufferSize);
          fireTableRowsUpdated(0, cyclicBufferSize);
          reachedCapacity = true;
        }
      } else {
        fireTableRowsUpdated(0, cyclicBufferSize);
      }
    } else {
      fireTableRowsInserted(begin, end);
    }
  }

  /**
  * @param key
  */
  private void fireNewKeyColumnAdded(NewKeyEvent e) {
    NewKeyListener[] listeners =
      (NewKeyListener[]) eventListenerList.getListeners(NewKeyListener.class);

    for (int i = 0; i < listeners.length; i++) {
      NewKeyListener listener = listeners[i];
      listener.newKeyAdded(e);
    }
  }

  /**
     * Returns true if this model is Cyclic (bounded) or not
     * @return true/false
     */
  public boolean isCyclic() {
    return cyclic;
  }

  /**
   * @return
   */
  public int getMaxSize() {
    return cyclicBufferSize;
  }

  /* (non-Javadoc)
   * @see org.apache.log4j.chainsaw.EventContainer#addNewKeyListener(org.apache.log4j.chainsaw.NewKeyListener)
   */
  public void addNewKeyListener(NewKeyListener l) {
    eventListenerList.add(NewKeyListener.class, l);
  }

  /* (non-Javadoc)
   * @see org.apache.log4j.chainsaw.EventContainer#removeNewKeyListener(org.apache.log4j.chainsaw.NewKeyListener)
   */
  public void removeNewKeyListener(NewKeyListener l) {
    eventListenerList.remove(NewKeyListener.class, l);
  }

  /* (non-Javadoc)
   * @see javax.swing.table.TableModel#isCellEditable(int, int)
   */
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    switch (columnIndex + 1) {
    case ChainsawColumns.INDEX_THROWABLE_COL_NAME:
      return true;
    }

    return super.isCellEditable(rowIndex, columnIndex);
  }

  /* (non-Javadoc)
   * @see org.apache.log4j.chainsaw.EventContainer#setCyclic(boolean)
   */
  public void setCyclic(final boolean cyclic) {
    if (this.cyclic == cyclic) {
      return;
    }

    final boolean old = this.cyclic;
    this.cyclic = cyclic;
    propertySupport.firePropertyChange("cyclic", old, cyclic);
  }

  /* (non-Javadoc)
   * @see org.apache.log4j.chainsaw.EventContainer#addPropertyChangeListener(java.beans.PropertyChangeListener)
   */
  public void addPropertyChangeListener(PropertyChangeListener l) {
    propertySupport.addPropertyChangeListener(l);
  }

  /* (non-Javadoc)
   * @see org.apache.log4j.chainsaw.EventContainer#addPropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
   */
  public void addPropertyChangeListener(
    String propertyName, PropertyChangeListener l) {
    propertySupport.addPropertyChangeListener(propertyName, l);
  }

  /* (non-Javadoc)
   * @see org.apache.log4j.chainsaw.EventContainer#size()
   */
  public int size() {
    return unfilteredList.size();
  }

  private class ModelChanger implements PropertyChangeListener {
    /* (non-Javadoc)
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent arg0) {
      Thread thread =
        new Thread(
          new Runnable() {
            public void run() {
              ProgressMonitor monitor = null;

              int index = 0;

              try {
                synchronized (unfilteredList) {
                  monitor =
                    new ProgressMonitor(
                      null, "Switching models...",
                      "Transferring between data structures, please wait...", 0,
                      unfilteredList.size() + 1);
                  monitor.setMillisToDecideToPopup(250);
                  monitor.setMillisToPopup(100);
                  LogLog.debug(
                    "Changing Model, isCyclic is now " + isCyclic());

                  List newUnfilteredList = null;
                  List newFilteredList = null;
                  HashSet newIDSet = null;

                  newIDSet = new HashSet(cyclicBufferSize);

                  if (isCyclic()) {
                    newUnfilteredList = new CyclicBufferList(cyclicBufferSize);
                    newFilteredList = new CyclicBufferList(cyclicBufferSize);
                  } else {
                    newUnfilteredList = new ArrayList(cyclicBufferSize);
                    newFilteredList = new ArrayList(cyclicBufferSize);
                  }

                  int increment = 0;

                  for (Iterator iter = unfilteredList.iterator();
                      iter.hasNext();) {
                    LoggingEvent e = (LoggingEvent) iter.next();
                    newUnfilteredList.add(e);

                    Object o =
                      e.getProperty(
                        e.getProperty(Constants.LOG4J_ID_KEY));

                    if (o != null) {
                      newIDSet.add(o);
                    } else {
                      newIDSet.add(new Integer(increment++));
                    }

                    monitor.setProgress(index++);
                  }

                  unfilteredList = newUnfilteredList;
                  filteredList = newFilteredList;
                  idSet = newIDSet;
                }

                monitor.setNote("Refiltering...");
                reFilter();

                monitor.setProgress(index++);
              } finally {
                monitor.close();
              }

              LogLog.debug("Model Change completed");
            }
          });
      thread.setPriority(Thread.MIN_PRIORITY + 1);
      thread.start();
    }
  }
}
