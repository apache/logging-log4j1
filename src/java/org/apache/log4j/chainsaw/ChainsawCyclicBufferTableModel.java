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

import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.SwingUtilities;
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
  implements EventContainer {
  private boolean cyclic;
  private final int INITIAL_CAPACITY = 1024;
  final List unfilteredList;
  final List filteredList;
  private Vector countListeners = new Vector();
  private boolean currentSortAscending;
  private int currentSortColumn;

  /**
   * @deprecated should use new filtering stuff
   */
  private DisplayFilter displayFilter;
  private final FilterChangeExecutor filterExecutor =
    new FilterChangeExecutor();
  private boolean sortEnabled = false;
  protected final Object syncLock = new Object();
  private LoggerNameModel loggerNameModelDelegate =
    new LoggerNameModelSupport();

  //because we may be using a cyclic buffer, if an ID is not provided in the property, 
  //use and increment this row counter as the ID for each received row
  int uniqueRow;

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
    countListeners.add(listener);
  }

  public void filterChanged() {
    SwingUtilities.invokeLater(filterExecutor);
  }

  public boolean isSortable(int col) {
    return true;
  }

  public void notifyCountListeners() {
    for (int i = 0; i < countListeners.size(); i++) {
      ((EventCountListener) countListeners.get(i)).eventCountChanged(
        filteredList.size(), unfilteredList.size());
    }
  }

  public void setDisplayFilter(DisplayFilter displayFilter) {
    LogLog.warn(
      "Currently ignoring Display filter change while in Vector->LoggingEvent model changeover");

    //    this.displayFilter = displayFilter;
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

  public int find(int startRow, String text) {
    if (text == null) {
      text = "";
    } else {
      text = text.toLowerCase();
    }

    int currentRow = -1;
    String thisVal = null;

    synchronized (syncLock) {
      Iterator iter = filteredList.iterator();

      while (iter.hasNext()) {
        currentRow++;

        Vector v2 = (Vector) iter.next();

        if (currentRow < startRow) {
          continue;
        }

        Iterator iter2 = v2.iterator();

        while (iter2.hasNext()) {
          thisVal = iter2.next().toString();

          boolean result =
            ((thisVal != null) && (thisVal.toLowerCase().indexOf(text) > -1));

          if (result) {
            return currentRow;
          }
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

  //TODO DO we need this method, I can find no references anywhere, but
  // left as is since it's such a large method....
  //  public LoggingEvent getEvent(Vector v) {
  //    Integer ID =
  //      new Integer(
  //        v.get(
  //          ChainsawColumns.getColumnsNames().indexOf(
  //            ChainsawConstants.ID_COL_NAME)).toString());
  //    ListIterator iter = ChainsawColumns.getColumnsNames().listIterator();
  //    String column = null;
  //    int index = -1;
  //
  //    //iterate through all column names and set the value from the unfiltered event 
  //    long timeStamp = 0L;
  //    Logger logger = null;
  //    String level = null;
  //    String threadName = "";
  //    Object message = null;
  //    String ndc = "";
  //    Hashtable mdc = null;
  //    String[] exception = null;
  //    String className = "";
  //    String methodName = "";
  //    String fileName = "";
  //    String lineNumber = "";
  //    Hashtable properties = null;
  //    boolean hadIDProperty = false;
  //
  //    String value = null;
  //
  //    while (iter.hasNext()) {
  //      column = (String) iter.next();
  //      index = ChainsawColumns.getColumnsNames().indexOf(column);
  //      value = v.get(index).toString();
  //
  //      if (column.equalsIgnoreCase(ChainsawConstants.LOGGER_COL_NAME)) {
  //        logger = Logger.getLogger(value);
  //      }
  //
  //      if (column.equalsIgnoreCase(ChainsawConstants.TIMESTAMP_COL_NAME)) {
  //        try {
  //          timeStamp = DATE_FORMATTER.parse(value).getTime();
  //        } catch (ParseException pe) {
  //          pe.printStackTrace();
  //
  //          //ignore...leave as 0L
  //        }
  //      }
  //
  //      if (column.equalsIgnoreCase(ChainsawConstants.LEVEL_COL_NAME)) {
  //        level = value;
  //      }
  //
  //      if (column.equalsIgnoreCase(ChainsawConstants.THREAD_COL_NAME)) {
  //        threadName = value;
  //      }
  //
  //      if (column.equalsIgnoreCase(ChainsawConstants.NDC_COL_NAME)) {
  //        ndc = value;
  //      }
  //
  //      if (column.equalsIgnoreCase(ChainsawConstants.MESSAGE_COL_NAME)) {
  //        message = value;
  //      }
  //
  //      if (column.equalsIgnoreCase(ChainsawConstants.MDC_COL_NAME)) {
  //        mdc = new Hashtable();
  //
  //        StringTokenizer t = new StringTokenizer(value, ",");
  //
  //        while (t.hasMoreElements()) {
  //          StringTokenizer t2 = new StringTokenizer(t.nextToken(), "=");
  //          mdc.put(t2.nextToken(), t2.nextToken());
  //        }
  //      }
  //
  //      if (column.equalsIgnoreCase(ChainsawConstants.THROWABLE_COL_NAME)) {
  //        exception = new String[] { value };
  //      }
  //
  //      if (column.equalsIgnoreCase(ChainsawConstants.CLASS_COL_NAME)) {
  //        className = value;
  //      }
  //
  //      if (column.equalsIgnoreCase(ChainsawConstants.METHOD_COL_NAME)) {
  //        methodName = value;
  //      }
  //
  //      if (column.equalsIgnoreCase(ChainsawConstants.FILE_COL_NAME)) {
  //        fileName = value;
  //      }
  //
  //      if (column.equalsIgnoreCase(ChainsawConstants.LINE_COL_NAME)) {
  //        lineNumber = value;
  //      }
  //
  //      if (column.equalsIgnoreCase(ChainsawConstants.PROPERTIES_COL_NAME)) {
  //        properties = new Hashtable();
  //
  //        StringTokenizer t = new StringTokenizer(value, ",");
  //
  //        while (t.hasMoreElements()) {
  //          StringTokenizer t2 = new StringTokenizer(t.nextToken(), "=");
  //          String propertyName = t2.nextToken();
  //
  //          if (propertyName.equalsIgnoreCase(ChainsawConstants.LOG4J_ID_KEY)) {
  //            hadIDProperty = true;
  //          }
  //
  //          properties.put(propertyName, t2.nextToken());
  //        }
  //      }
  //    }
  //
  //    //if log4jid property did not exist, set it (will be used during reconstruction)
  //    if (!hadIDProperty) {
  //      properties.put(ChainsawConstants.LOG4J_ID_KEY, ID.toString());
  //    }
  //
  //    Level levelImpl = Level.toLevel(level);
  //
  //    return new LoggingEvent(
  //      logger.getName(), logger, timeStamp, levelImpl, threadName, message, ndc,
  //      mdc, exception,
  //      new LocationInfo(fileName, className, methodName, lineNumber), properties);
  //  }
  public int getRowIndex(LoggingEvent e) {
    synchronized (syncLock) {
      return filteredList.indexOf(e);
    }
  }

  public int getColumnCount() {
    return ChainsawColumns.getColumnsNames().size();
  }

  public String getColumnName(int column) {
    return ChainsawColumns.getColumnsNames().get(column).toString();
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
      return "";
    }
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

      //    TODO hook up the new display filter stuff once converted
      rowAdded = true;
      filteredList.add(e);

      //      if ((displayFilter == null) || (displayFilter.isDisplayed(e))) {
      //        filteredList.add(e);
      //
      //        rowAdded = true;
      //      }
    }

    if (!valueIsAdjusting) {
      notifyCountListeners();
    }

    int newRow = filteredList.size() - 1;

    if (!isCyclic()) {
      fireTableRowsInserted(newRow, newRow);
    } else {
      if (
        unfilteredList.size() == ((CyclicBufferList) unfilteredList)
          .getMaxSize()) {
        fireTableDataChanged();
      } else {
        fireTableRowsInserted(newRow, newRow);
      }
    }

    return rowAdded;
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
        //          TODO change when filtering refactor done.
        if (displayFilter != null) {
          filteredList.clear();

          Vector v2 = null;
          Iterator iter = unfilteredList.iterator();

          while (iter.hasNext()) {
            v2 = (Vector) iter.next();

            if (displayFilter.isDisplayed(v2)) {
              filteredList.add(v2);
            }
          }
        }

        if (sortEnabled) {
          sort();
        }
      }

      fireTableDataChanged();
      notifyCountListeners();
    }
  }
}
