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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.StringTokenizer;
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
  private static final DateFormat DATE_FORMATTER =
    new SimpleDateFormat(ChainsawConstants.DATETIME_FORMAT);
  private Vector countListeners = new Vector();
  private boolean currentSortAscending;
  private int currentSortColumn;
  private DisplayFilter displayFilter;
  private final FilterChangeExecutor filterExecutor =
    new FilterChangeExecutor();
  private boolean sortEnabled = false;
  protected final Object syncLock = new Object();

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
    this.displayFilter = displayFilter;
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
    System.out.println(
      "request to sort col=" + col + ", which is "
      + ChainsawColumns.getColumnsNames().get(col));
    SwingUtilities.invokeLater(new SortExecutor(table, col, row, ascending));
  }

  /**
   * Escape &lt;, &gt; &amp; and &quot; as their entities. It is very
   * dumb about &amp; handling.
   * @param aStr the String to escape.
   * @return the escaped String
   */
  String escape(String string) {
    if (string == null) {
      return null;
    }

    final StringBuffer buf = new StringBuffer();

    for (int i = 0; i < string.length(); i++) {
      char c = string.charAt(i);

      switch (c) {
      case '<':
        buf.append("&lt;");

        break;

      case '>':
        buf.append("&gt;");

        break;

      case '\"':
        buf.append("&quot;");

        break;

      case '&':
        buf.append("&amp;");

        break;

      default:
        buf.append(c);

        break;
      }
    }

    return buf.toString();
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

  public Vector getAllEvents() {
    Vector v = new Vector();
    synchronized (syncLock) {
      Iterator iter = unfilteredList.iterator();

      while (iter.hasNext()) {
        v.add(getEvent((Vector)iter.next()));
      }
    }

    return v;
  }

public LoggingEvent getEvent(Vector v) {
	Integer ID = new Integer(v.get(ChainsawColumns.getColumnsNames().indexOf(ChainsawConstants.ID_COL_NAME)).toString());
    ListIterator iter = ChainsawColumns.getColumnsNames().listIterator();
    String column = null;
    int index = -1;

    //iterate through all column names and set the value from the unfiltered event 
    long timeStamp = 0L;
    Logger logger = null;
    String level = null;
    String threadName = "";
    Object message = null;
    String ndc = "";
    Hashtable mdc = null;
    String[] exception = null;
    String className = "";
    String methodName = "";
    String fileName = "";
    String lineNumber = "";
    Hashtable properties = null;
    boolean hadIDProperty = false;

    String value = null;

    while (iter.hasNext()) {
      column = (String) iter.next();
      index = ChainsawColumns.getColumnsNames().indexOf(column);
      value = v.get(index).toString();

      if (column.equalsIgnoreCase(ChainsawConstants.LOGGER_COL_NAME)) {
        logger = Logger.getLogger(value);
      }

      if (column.equalsIgnoreCase(ChainsawConstants.TIMESTAMP_COL_NAME)) {
        try {
          timeStamp = DATE_FORMATTER.parse(value).getTime();
        } catch (ParseException pe) {
          pe.printStackTrace();

          //ignore...leave as 0L
        }
      }

      if (column.equalsIgnoreCase(ChainsawConstants.LEVEL_COL_NAME)) {
        level = value;
      }

      if (column.equalsIgnoreCase(ChainsawConstants.THREAD_COL_NAME)) {
        threadName = value;
      }

      if (column.equalsIgnoreCase(ChainsawConstants.NDC_COL_NAME)) {
        ndc = value;
      }

      if (column.equalsIgnoreCase(ChainsawConstants.MESSAGE_COL_NAME)) {
        message = value;
      }

      if (column.equalsIgnoreCase(ChainsawConstants.MDC_COL_NAME)) {
        mdc = new Hashtable();

        StringTokenizer t = new StringTokenizer(value, ",");

        while (t.hasMoreElements()) {
          StringTokenizer t2 = new StringTokenizer(t.nextToken(), "=");
          mdc.put(t2.nextToken(), t2.nextToken());
        }
      }

      if (column.equalsIgnoreCase(ChainsawConstants.THROWABLE_COL_NAME)) {
        exception = new String[] { value };
      }

      if (column.equalsIgnoreCase(ChainsawConstants.CLASS_COL_NAME)) {
        className = value;
      }

      if (column.equalsIgnoreCase(ChainsawConstants.METHOD_COL_NAME)) {
        methodName = value;
      }

      if (column.equalsIgnoreCase(ChainsawConstants.FILE_COL_NAME)) {
        fileName = value;
      }

      if (column.equalsIgnoreCase(ChainsawConstants.LINE_COL_NAME)) {
        lineNumber = value;
      }

      if (column.equalsIgnoreCase(ChainsawConstants.PROPERTIES_COL_NAME)) {
        properties = new Hashtable();

        StringTokenizer t = new StringTokenizer(value, ",");

        while (t.hasMoreElements()) {
          StringTokenizer t2 = new StringTokenizer(t.nextToken(), "=");
          String propertyName = t2.nextToken();

          if (propertyName.equalsIgnoreCase(ChainsawConstants.LOG4J_ID_KEY)) {
            hadIDProperty = true;
          }

          properties.put(propertyName, t2.nextToken());
        }
      }
    }

    //if log4jid property did not exist, set it (will be used during reconstruction)
    if (!hadIDProperty) {
      properties.put(ChainsawConstants.LOG4J_ID_KEY, ID.toString());
    }

    Level levelImpl = Level.toLevel(level);

    return new LoggingEvent(
      logger.getName(), logger, timeStamp, levelImpl, threadName, message, ndc,
      mdc, exception,
      new LocationInfo(fileName, className, methodName, lineNumber), properties);
  }

  public int getRowIndex(Vector v) {
    synchronized (syncLock) {
      return filteredList.indexOf(v);
    }
  }

  public int getColumnCount() {
    return ChainsawColumns.getColumnsNames().size();
  }

  public String getColumnName(int column) {
    return ChainsawColumns.getColumnsNames().get(column).toString();
  }

  /* (non-Javadoc)
   * @see org.apache.log4j.chainsaw.EventContainer#getDetailText(int)
   */
  public String getDetailText(int row) {
    boolean pastFirst = false;
    StringBuffer detail = new StringBuffer(128);
    detail.append("<html><body><table cellspacing=0 cellpadding=0>");

    List columnNames = ChainsawColumns.getColumnsNames();

    Vector v;

    synchronized (syncLock) {
      v = (Vector) filteredList.get(row);
    }

    if (v == null) {
      return "";
    }

    ListIterator iter = displayFilter.getDetailColumns().listIterator();
    String column = null;
    int index = -1;

    while (iter.hasNext()) {
      column = (String) iter.next();
      index = columnNames.indexOf(column);

      if (index > -1) {
        if (pastFirst) {
          detail.append("</td></tr>");
        }

        detail.append("<tr><td valign=\"top\"><b>");
        detail.append(column);
        detail.append(": </b></td><td>");
        detail.append(escape(v.get(index).toString()));
        pastFirst = true;
      }
    }

    detail.append("</table></body></html>");

    return detail.toString();
  }

  public Vector getRow(int row) {
    return (Vector) filteredList.get(row);
  }

  public int getRowCount() {
    synchronized (syncLock) {
      return filteredList.size();
    }
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
    Vector row = (Vector) filteredList.get(rowIndex);

    if (row == null) {
      LogLog.error("Invalid rowindex=" + rowIndex);
      throw new NullPointerException("Invalid rowIndex=" + rowIndex);
    }

    return row.get(columnIndex);
  }

  public boolean isAddRow(Vector row, boolean valueIsAdjusting) {
    boolean rowAdded = false;

    synchronized (syncLock) {
      //set the last field to the 'unfilteredevents size + 1 - an ID based on reception order
      int propertiesIndex =
        ChainsawColumns.getColumnsNames().indexOf(
          ChainsawConstants.PROPERTIES_COL_NAME);
      String props = (String) row.get(propertiesIndex);
      Integer thisInt = null;

      if (props.indexOf(ChainsawConstants.LOG4J_ID_KEY) > -1) {
        StringBuffer newProps = new StringBuffer();
        StringTokenizer t = new StringTokenizer(props, ",");

        while (t.hasMoreElements()) {
          StringTokenizer t2 = new StringTokenizer(t.nextToken(), "=");
          String propertyName = t2.nextToken();

          if (propertyName.equalsIgnoreCase(ChainsawConstants.LOG4J_ID_KEY)) {
            thisInt = new Integer(t2.nextToken());
          } else {
            if (newProps.length() > 0) {
              newProps.append(",");
            }

            newProps.append(propertyName);
            newProps.append("=");
            newProps.append(t2.nextToken());
          }
        }

        //strip off the ID property - not needed now that it's reloaded
        row.set(propertiesIndex, newProps.toString());
      }

      if (thisInt == null) {
        thisInt = new Integer(++uniqueRow);
      }

      row.add(thisInt);

      //prevent duplicate rows
      if (unfilteredList.contains(row)) {
        return false;
      }

      unfilteredList.add(row);

      if ((displayFilter == null) || (displayFilter.isDisplayed(row))) {
        filteredList.add(row);

        rowAdded = true;
      }
    }

    if (!valueIsAdjusting) {
      notifyCountListeners();
    }

    int newRow = filteredList.indexOf(row);

    if(!isCyclic()) {
      fireTableRowsInserted(newRow, newRow);
    } else {
      if(unfilteredList.size()==((CyclicBufferList)unfilteredList).getMaxSize()){
        fireTableDataChanged();
      } else {
        fireTableRowsInserted(newRow, newRow);
      }
    }
    

    return rowAdded;
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
        Vector v = null;

        if ((currentRow > -1) && (currentRow < filteredList.size())) {
          v = (Vector) filteredList.get(currentRow);
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

        Vector v2 = null;
        Iterator iter = unfilteredList.iterator();

        while (iter.hasNext()) {
          v2 = (Vector) iter.next();

          if (displayFilter != null) {
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
    if(!isCyclic()){
      throw new IllegalStateException("You cannot call getMaxSize() when the model is not cyclic");
    }
    return ((CyclicBufferList)unfilteredList).getMaxSize();
  }


}
