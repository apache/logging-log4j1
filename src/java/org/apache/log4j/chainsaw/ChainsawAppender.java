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

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;


/**
 * ChainsawAppender receives LoggingEvents from the local
 * Log4J environment, and appends them into a model that
 * can be used inside a Swing GUI
 * @author Paul Smith
 * @version 1.0
 */
public class ChainsawAppender
    extends AppenderSkeleton
    implements EventDetailSink, TableModel {

  private static MyTableModel sSharedModel;
  private final MyTableModel wrappedTableModel = getDefaultModel();

  private static ChainsawAppender sSharedAppender = null;

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
  private static synchronized MyTableModel getDefaultModel()
  {
    if (sSharedModel == null) {
      sSharedModel = new MyTableModel();
    }
    return sSharedModel;
  }

  /**
   * Return the singleton instance of the ChainsawAppender, it should only
   * be initialised once.
   * @return
   */
  static ChainsawAppender getInstance()
  {
    return sSharedAppender;
  }

  /**
   * Returns the internally wrapped Model
   *
   * NOTE: it is strongly recommended at this time not to rely on this method
   * until further refactoring is completed.
   * @return MyTableModel
   */
  MyTableModel getWrappedModel()
  {
    return wrappedTableModel;
  }

  public boolean requiresLayout() {
    return false;
  }

  /**
   * Implements the EventDetailSink interface by forwarding the EventDetails
   * object onto an internal Model
   */
  public void addEvent(EventDetails aDetails)
  {
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
   * Close does nothing
   */
  public void close() {
    // TODO: perhaps it should clear the internal TableModel
  }

  // ==========================================================================
  // All methods here are from TableModel, and simply forward on to the
  // internal wrappedTableModel instance
  // ==========================================================================

  public int getRowCount() {
    return wrappedTableModel.getRowCount();
  }

  public int getColumnCount() {
    return wrappedTableModel.getColumnCount();
  }

  public String getColumnName(int aColumnIndex) {
    return wrappedTableModel.getColumnName(aColumnIndex);
  }

  public Class getColumnClass(int columnIndex) {
    return wrappedTableModel.getColumnClass(columnIndex);
  }

  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return wrappedTableModel.isCellEditable(rowIndex, columnIndex);
  }

    public Object getValueAt(int rowIndex, int columnIndex) {
    return wrappedTableModel.getValueAt(rowIndex, columnIndex);
  }

  public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    wrappedTableModel.setValueAt(aValue, rowIndex, columnIndex);
  }

  public void addTableModelListener(TableModelListener l) {
    wrappedTableModel.addTableModelListener(l);
  }

  public void removeTableModelListener(TableModelListener l) {
    wrappedTableModel.removeTableModelListener(l);
  }
}
