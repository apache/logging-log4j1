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
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import javax.swing.SwingUtilities;
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
  private boolean cyclic;
  private final int INITIAL_CAPACITY = 1024;
  final List unfilteredList;
  final List filteredList;

  //  private Vector countListeners = new Vector();
  private boolean currentSortAscending;
  private int currentSortColumn;
  private EventListenerList eventListenerList = new EventListenerList();
  private List columnNames = new ArrayList(ChainsawColumns.getColumnsNames());
  private final FilterChangeExecutor filterExecutor =
    new FilterChangeExecutor();
  private boolean sortEnabled = false;
  protected final Object syncLock = new Object();
  private LoggerNameModel loggerNameModelDelegate =
    new LoggerNameModelSupport();

  //because we may be using a cyclic buffer, if an ID is not provided in the property, 
  //use and increment this row counter as the ID for each received row
  int uniqueRow;
  private Set uniqueMDCKeys = new HashSet();
  private Rule displayRule;

  public ChainsawCyclicBufferTableModel(boolean isCyclic, int bufferSize) {
    this.cyclic = isCyclic;

    if (isCyclic) {
      unfilteredList = new CyclicBufferList(bufferSize);
      filteredList = new CyclicBufferList(bufferSize);
    } else {
      unfilteredList = new ArrayList(INITIAL_CAPACITY);
      filteredList = new ArrayList(INITIAL_CAPACITY);
    }
  }

  /**
   * @param l
   */
  public void removeLoggerNameListener(LoggerNameListener l) {
    loggerNameModelDelegate.removeLoggerNameListener(l);
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  public int hashCode() {
    return loggerNameModelDelegate.hashCode();
  }

  /**
   * @param loggerName
   * @return
   */
  public boolean addLoggerName(String loggerName) {
    return loggerNameModelDelegate.addLoggerName(loggerName);
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  public String toString() {
    return loggerNameModelDelegate.toString();
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

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  public boolean equals(Object obj) {
    return loggerNameModelDelegate.equals(obj);
  }

  public void addEventCountListener(EventCountListener listener) {
    eventListenerList.add(EventCountListener.class, listener);
  }

  public void filterChanged() {
    SwingUtilities.invokeLater(filterExecutor);
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

  /**
  *
  */
  private void reFilter() {
    new Thread(filterExecutor).start();
  }

  /* (non-Javadoc)
     * @see org.apache.log4j.chainsaw.EventContainer#sort()
     */
  public void sort() {
    if (sortEnabled) {
      synchronized (syncLock) {
        Collections.sort(
          filteredList,
          new ColumnComparator(currentSortColumn, currentSortAscending));
      }
    }
  }

  public void sortColumn(
    JSortTable table, int col, int row, boolean ascending) {
    LogLog.debug(
      "request to sort col=" + col + ", which is "
      + ChainsawColumns.getColumnsNames().get(col));
    SwingUtilities.invokeLater(new SortExecutor(table, col, row, ascending));
  }

  /* (non-Javadoc)
   * @see org.apache.log4j.chainsaw.EventContainer#clear()
   */
  public void clearModel() {
    synchronized (syncLock) {
      unfilteredList.clear();
      filteredList.clear();
      uniqueRow = 0;
    }

    fireTableDataChanged();
    notifyCountListeners();
  }

  /**
   * @deprecated - should this be replaced with a Refinement filter?
   *
   * If not it should be replaced with something inside LogPanel, a Finder class, it should not be in the Model.
   */
  public int find(int startRow, String text) {
    if (text == null) {
      text = "";
    } else {
      text = text.toLowerCase();
    }

    int currentRow = -1;
    String thisVal = null;

    synchronized (syncLock) {
      ListIterator iter = filteredList.listIterator();

      while (iter.hasNext()) {
        currentRow++;

        LoggingEvent event = (LoggingEvent) iter.next();

        if (currentRow < startRow) {
          continue;
        }

        if (event.getMessage().toString().toLowerCase().indexOf(text) > 0) {
          return currentRow;
        }
      }
    }

    return -1;
  }

  public List getAllEvents() {
    List list = new ArrayList(unfilteredList.size());

    synchronized (syncLock) {
      list.addAll(unfilteredList);
    }

    return list;
  }

  public int getRowIndex(LoggingEvent e) {
    synchronized (syncLock) {
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
    return (LoggingEvent) filteredList.get(row);
  }

  public int getRowCount() {
    synchronized (syncLock) {
      return filteredList.size();
    }
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
    LoggingEvent event = (LoggingEvent) filteredList.get(rowIndex);
    LocationInfo info = event.getLocationInformation();

    if (event == null) {
      LogLog.error("Invalid rowindex=" + rowIndex);
      throw new NullPointerException("Invalid rowIndex=" + rowIndex);
    }

    switch (columnIndex + 1) {
    case ChainsawColumns.INDEX_ID_COL_NAME:

      Object id = event.getProperty(ChainsawConstants.LOG4J_ID_KEY);

      if (id != null) {
        return id;
      }

      return new Integer(rowIndex);

    case ChainsawColumns.INDEX_LEVEL_COL_NAME:
      return event.getLevel();

    case ChainsawColumns.INDEX_LOGGER_COL_NAME:
      return event.getLoggerName();

    case ChainsawColumns.INDEX_TIMESTAMP_COL_NAME:
      return new Date(event.timeStamp);

    case ChainsawColumns.INDEX_MESSAGE_COL_NAME:
      return event.getRenderedMessage();

    case ChainsawColumns.INDEX_MDC_COL_NAME:
      return event.getMDCKeySet();

    case ChainsawColumns.INDEX_NDC_COL_NAME:
      return event.getNDC();

    case ChainsawColumns.INDEX_PROPERTIES_COL_NAME:
      return event.getPropertyKeySet();

    case ChainsawColumns.INDEX_THREAD_COL_NAME:
      return event.getThreadName();

    case ChainsawColumns.INDEX_THROWABLE_COL_NAME:
      return event.getThrowableStrRep();

    case ChainsawColumns.INDEX_CLASS_COL_NAME:
      return (info != null) ? info.getClassName() : "";

    case ChainsawColumns.INDEX_FILE_COL_NAME:
      return (info != null) ? info.getFileName() : "";

    case ChainsawColumns.INDEX_LINE_COL_NAME:
      return (info != null) ? info.getLineNumber() : "";

    case ChainsawColumns.INDEX_METHOD_COL_NAME:
      return (info != null) ? info.getMethodName() : "";

    default:

      if (columnIndex <= columnNames.size()) {
        return event.getMDC(columnNames.get(columnIndex).toString());
      }
    }

    return "";
  }

  public boolean isAddRow(LoggingEvent e, boolean valueIsAdjusting) {
    boolean rowAdded = false;

    synchronized (syncLock) {
      Object id = e.getProperty(ChainsawConstants.LOG4J_ID_KEY);

      if (id == null) {
        id = new Integer(++uniqueRow);
        e.setProperty(ChainsawConstants.LOG4J_ID_KEY, id.toString());
      }

      //prevent duplicate rows
      if (unfilteredList.contains(e)) {
        return false;
      }

      unfilteredList.add(e);

      rowAdded = true;

      if ((displayRule == null) || (displayRule.evaluate(e))) {
        filteredList.add(e);

        rowAdded = true;
      }
    }

    if (!valueIsAdjusting) {
      notifyCountListeners();
    }

    int newRow = filteredList.size() - 1;

    /**
     * Is this a new MDC key we haven't seen before?
     */
    boolean newColumn = uniqueMDCKeys.addAll(e.getMDCKeySet());

    /**
     * If so, we should add them as columns and notify listeners.
     */
    for (Iterator iter = e.getMDCKeySet().iterator(); iter.hasNext();) {
      Object key = (Object) iter.next();

      if (!columnNames.contains(key)) {
        columnNames.add(key);
        LogLog.debug("Adding col '" + key + "', columNames=" + columnNames);
        fireNewKeyColumnAdded(
          new NewKeyEvent(
            this, columnNames.indexOf(key), key, e.getMDC(key.toString())));
      }
    }

    if (!isCyclic() && !newColumn) {
      fireTableRowsInserted(newRow, newRow);
    } else {
      if (
        newColumn
          || (unfilteredList.size() == ((CyclicBufferList) unfilteredList)
          .getMaxSize())) {
        fireTableDataChanged();
      } else {
        fireTableRowsInserted(newRow, newRow);
      }
    }

    return rowAdded;
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
  protected int getMaxSize() {
    if (!isCyclic()) {
      throw new IllegalStateException(
        "You cannot call getMaxSize() when the model is not cyclic");
    }

    return ((CyclicBufferList) unfilteredList).getMaxSize();
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
   * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
   */
  public void propertyChange(PropertyChangeEvent evt) {
    if (evt.getSource() instanceof Rule) {
      reFilter();
    }
  }

  class SortExecutor implements Runnable {
    private JSortTable table;
    private int col;
    private int currentRow;
    private boolean ascending;

    /**
     * Re-apply current sort column in the same order, and re-select the row.
     */
    public SortExecutor(
      JSortTable table, int col, int currentRow, boolean ascending) {
      this.table = table;
      this.col = col;
      this.currentRow = currentRow;
      this.ascending = ascending;
    }

    public void run() {
      synchronized (syncLock) {
        LoggingEvent v = null;

        if ((currentRow > -1) && (currentRow < filteredList.size())) {
          v = (LoggingEvent) filteredList.get(currentRow);
        }

        sortEnabled = true;
        currentSortColumn = col;
        currentSortAscending = ascending;

        if (col > -1) {
          sort();
        }

        if (v == null) {
          table.scrollToRow(
            -1, table.columnAtPoint(table.getVisibleRect().getLocation()));
        } else {
          table.scrollToRow(
            filteredList.indexOf(v),
            table.columnAtPoint(table.getVisibleRect().getLocation()));
        }
      }
    }
  }

  class FilterChangeExecutor implements Runnable {
    /**
     * Update filtered rows.
     */
    FilterChangeExecutor() {
    }

    public void run() {
      synchronized (syncLock) {
        filteredList.clear();

        if (displayRule != null) {
          LoggingEvent event = null;
          Iterator iter = unfilteredList.iterator();

          while (iter.hasNext()) {
            event = (LoggingEvent) iter.next();

            if (displayRule.evaluate(event)) {
              filteredList.add(event);
            }
          }
        } else {
          filteredList.addAll(unfilteredList);
        }

        if (sortEnabled) {
          sort();
        }
      }

      SwingUtilities.invokeLater(
        new Runnable() {
          public void run() {
            fireTableDataChanged();
            notifyCountListeners();
          }
        });
    }
  }
}
