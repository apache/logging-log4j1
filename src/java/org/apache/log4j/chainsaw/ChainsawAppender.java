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

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.LoggingEvent;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;


/**
 * ChainsawAppender receives LoggingEvents from the local
 * Log4J environment, and appends them into a model that
 * can be used inside a Swing GUI
 * @author Paul Smith
 * @version 1.0
 */
public class ChainsawAppender extends AppenderSkeleton
  implements EventDetailSink, TableModel {
  /**
   * Shared model used by the shared Appender
   */
  private static MyTableModel sSharedModel;

  /**
   * The in-JVM singleton instance of the ChainsawAppender.
   *
   * If somehow Log4j initialises more than one, then the first one to
   * initialise wins!
   */
  private static ChainsawAppender sSharedAppender = null;

  /**
   * The model that is used by this Appender, we ensure
   * here that we only use a single Model as the current
   * release is effetively an in-JVM singleton
   */
  private final MyTableModel wrappedTableModel = getDefaultModel();

  /**
   * The classname of the viewer to create to view the events.
   */
  private String viewerClassname;

  /**
   * Constructor, initialises the singleton instance of the appender
   */
  public ChainsawAppender() {
    synchronized (ChainsawAppender.class) {
      if (sSharedAppender == null) {
        sSharedAppender = this;
      }
    }
  }

  /**
   * Returns the singleton MyTableModel instance that has been configured.
   *
   * This will be eventually replaced to allow each ChainsawAppender
   * to have it's own model, but for now it's important that all log events
   * received inside Chainsaw go to a single model.
   * @return MyTableModel
   */
  private static synchronized MyTableModel getDefaultModel() {
    if (sSharedModel == null) {
      sSharedModel = new MyTableModel();
    }

    return sSharedModel;
  }

  /**
   * Return the singleton instance of the ChainsawAppender, it should only
   * be initialised once.
   * @return the One and only instance of the ChainsawAppender that is
   * allowed to be referenced by the GUI
   */
  static ChainsawAppender getInstance() {
    return sSharedAppender;
  }

  /**
   * Returns the internally wrapped Model
   *
   * NOTE: it is strongly recommended at this time not to rely on this method
   * until further refactoring is completed.
   * @return MyTableModel the MyTableModel that can be used by external
   * components
   */
  MyTableModel getWrappedModel() {
    return wrappedTableModel;
  }

  /**
   * This appender does not require layout and so return false
   * @return false and only false
   */
  public boolean requiresLayout() {
    return false;
  }

  /**
   * Implements the EventDetailSink interface by forwarding the EventDetails
   * object onto an internal Model
   * @param aDetails the EventDetails to add to the model
   */
  public void addEvent(EventDetails aDetails) {
    synchronized (wrappedTableModel) {
      wrappedTableModel.addEvent(aDetails);
    }
  }

  /**
   * Appends the event into the internal wrapped TableModel
   * @param aEvent the LoggingEvent to append
   */
  protected void append(LoggingEvent aEvent) {
    synchronized (wrappedTableModel) {
      wrappedTableModel.addEvent(new EventDetails(aEvent));
    }
  }

  /**
   * Instantiates and activates an instance of a ChainsawViewer
   * to view the contents of this appender.
   */
  public void activateOptions() {
    if (viewerClassname == null) {
      viewerClassname = DefaultViewer.class.getName();
    }

    ChainsawViewer viewer =
      (ChainsawViewer) OptionConverter.instantiateByClassName(
        viewerClassname, ChainsawViewer.class, null);

    if (viewer != null) {
      viewer.activateViewer(this);
    }
  }

  /**
   * Close does nothing
   */
  public void close() {
    /** @todo  perhaps it should clear the internal TableModel */
  }

  /**
   * Sets the viewer class to use to view the events.  The class must
   * implement the ChainsawViewer interface.
   *
   * @param classname The class name of the viewer class.
   */
  public void setViewerClass(String classname) {
    viewerClassname = classname;
  }

  /**
   * Gets the viewer class to use to view the events.
   *
   * @return The class name of the viewer class.
   */
  public String getViewerClass() {
    return viewerClassname;
  }

  // ==========================================================================
  // All methods here are from TableModel, and simply forward on to the
  // internal wrappedTableModel instance
  // ==========================================================================

  /**
   * Implementation of TableModel interface
   * @return int rowCount
   */
  public int getRowCount() {
    return wrappedTableModel.getRowCount();
  }

  /**
   * Implementation of TableModel interface
   * @return int column Count
   */
  public int getColumnCount() {
    return wrappedTableModel.getColumnCount();
  }

  /**
   * Implementation of TableModel interface
   * @param aColumnIndex the Column index to query the name for
   * @return String column name
   */
  public String getColumnName(int aColumnIndex) {
    return wrappedTableModel.getColumnName(aColumnIndex);
  }

  /**
   * Implementation of TableModel interface
   * @param columnIndex column Index to query the Class of
   * @return Class class of Column
   */
  public Class getColumnClass(int columnIndex) {
    return wrappedTableModel.getColumnClass(columnIndex);
  }

  /**
   * Implementation of TableModel interface
   * @param rowIndex row Index to query
   * @param columnIndex column Index to query
   * @return boolean is Cell Editable?
   */
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return wrappedTableModel.isCellEditable(rowIndex, columnIndex);
  }

  /**
   * Implementation of TableModel interface
   * @param rowIndex the row index to retrieve value from
   * @param columnIndex to the column index to retrieve value from
   * @return Object value at a particular row/column point
   */
  public Object getValueAt(int rowIndex, int columnIndex) {
    return wrappedTableModel.getValueAt(rowIndex, columnIndex);
  }

  /**
   * Implementation of TableModel interface
   * @param aValue the value to set
   * @param rowIndex the row
   * @param columnIndex the column
   */
  public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    wrappedTableModel.setValueAt(aValue, rowIndex, columnIndex);
  }

  /**
   * Implementation of TableModel interface
   * @param l a TableModelListener to add
   */
  public void addTableModelListener(TableModelListener l) {
    wrappedTableModel.addTableModelListener(l);
  }

  /**
   * Implementation of TableModel interface
   * @param l listener to remove from the currently registered listeners
   */
  public void removeTableModelListener(TableModelListener l) {
    wrappedTableModel.removeTableModelListener(l);
  }
}
