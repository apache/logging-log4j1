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

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;


/**
 * A Sortable JTable implementation that allows a user to click on a
 * specific Column and have the row information sorted by that column.
 *
 * @author Claude Duguay
 * @author Scott Deboy <sdeboy@apache.org>
 * 
 */
public class JSortTable extends JTable implements MouseListener {
  protected int sortedColumnIndex = -1;
  protected boolean sortedColumnAscending = true;
  private String sortedColumn;

  public JSortTable() {
    super();
    initSortHeader();
  }
  public JSortTable(SortTableModel model) {
    super(model);
    initSortHeader();
  }

  public JSortTable(SortTableModel model, TableColumnModel colModel) {
    super(model, colModel);
    initSortHeader();
  }

  public JSortTable(
    SortTableModel model, TableColumnModel colModel,
    ListSelectionModel selModel) {
    super(model, colModel, selModel);
    initSortHeader();
  }

  protected void initSortHeader() {
    JTableHeader header = getTableHeader();
    header.setDefaultRenderer(new SortHeaderRenderer());
    header.addMouseListener(this);
  }

  public int getSortedColumnIndex() {
    return sortedColumnIndex;
  }

  public void updateSortedColumn() {
  	if (sortedColumn != null) {
  		try {
	  		sortedColumnIndex = columnModel.getColumnIndex(sortedColumn);
  		} catch (IllegalArgumentException ie) {//nothing...column is not in the model
  			setSortedColumnIndex(-1);
	  	}
	  }
  }
  
  public void setSortedColumnIndex(int index) {
    sortedColumnIndex = index;

    SortTableModel model = (SortTableModel) getModel();
    model.sortColumn(sortedColumnIndex, sortedColumnAscending);
  }

  public void scrollToRow(final int row, final int col) {
    SwingUtilities.invokeLater(
      new Runnable() {
        public void run() {
          if ((row > -1) && (row < getRowCount())) {
            try {
              setRowSelectionInterval(row, row);
              scrollRectToVisible(getCellRect(row, col, true));
            } catch (IllegalArgumentException iae) {
            }
             //ignore..out of bounds
          }
        }
      });
  }

  public void scrollToBottom(final int col) {
    SwingUtilities.invokeLater(
      new Runnable() {
        public void run() {
          int row = getRowCount() - 1;

          try {
            setRowSelectionInterval(row, row);
            scrollRectToVisible(getCellRect(row, col + 1, true));
          } catch (IllegalArgumentException iae) {
          }
           //ignore..out of bounds
        }
        ;
      });
  }

  public boolean isSortedColumnAscending() {
    return sortedColumnAscending;
  }

  public void mouseClicked(MouseEvent event) {
  	
  	if(event.getClickCount()<2 || event.isPopupTrigger()){
  		return;
  	}else if(event.getClickCount()>1 && event.getButton() == MouseEvent.BUTTON2_MASK){
  		return;
  	}
  	
    TableColumnModel colModel = getColumnModel();
    int index = colModel.getColumnIndexAtX(event.getX());
    int modelIndex = colModel.getColumn(index).getModelIndex();
    SortTableModel model = (SortTableModel) getModel();

    if (model.isSortable(modelIndex)) {
      // toggle ascension, if already sorted
      if (sortedColumnIndex == index) {
        sortedColumnAscending = !sortedColumnAscending;
      }

      sortedColumnIndex = index;
      sortedColumn = colModel.getColumn(index).getHeaderValue().toString();
      model.sortColumn(modelIndex, sortedColumnAscending);
    }
  }

  public void mousePressed(MouseEvent event) {
  }

  public void mouseReleased(MouseEvent event) {
  }

  public void mouseEntered(MouseEvent event) {
  }

  public void mouseExited(MouseEvent event) {
  }
}
