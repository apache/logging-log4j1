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

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableColumn;


/**
 * A sort of Preferences frame for the current app/panel that allows the
 * user to choose which Columns are worth displaying.  This Frame can then be
 * moved, minimized away to be used later, rather than being a Dialog.
 *
 * @author Scott Deboy <sdeboy@apache.org>
 *
 */
public class ColumnSelector extends JFrame implements TableColumnModelListener {
  private final JSortTable sortTable;
  private final DisplayFilter displayFilter;
  private Vector checkBoxes=new Vector();
  private List columnNames = ChainsawColumns.getColumnsNames();

/*
 * Build a vector of JCheckBoxes.  
 * No need to use a map to look up the column name because 
 * the checkbox text is the column name.
 * 
 */
  public ColumnSelector(String ident,
    final Vector columnNames, final JSortTable sortTable,
    final DisplayFilter displayFilter) {
  	super("Select displayed columns for " + ident);
    this.sortTable = sortTable;
    this.displayFilter = displayFilter;
    setLocation(150, 150);

    JPanel columnPanel = new JPanel(new GridLayout(0, 1));
    columnPanel.setBorder(
      BorderFactory.createTitledBorder("Select display columns"));
    columnPanel.setPreferredSize(new Dimension(150, 340));

    int modelIndex = 0;

	//build displayed columns
    for (
      Iterator iter2 = columnNames.iterator();
        iter2.hasNext();) {
      String column = iter2.next().toString();

      //default checkbox to displayed/undisplayed based on 
      //whether or not the column is displayed at construction time.
      //a convertcolumnindextoview return value of -1 is not displayed.
      int viewIndex = sortTable.convertColumnIndexToView(modelIndex);
      final JCheckBox box = new JCheckBox(column, viewIndex > -1);

      checkBoxes.add(box);

	  /* In the checkbox action, first get the current sorted column index
	   * and then iterate through the ChainsawConstants column list and examine 
	   * whether or not the column is displayed in the table.
	   * 
	   */
      box.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Vector v = new Vector();

			//get the index of this column in the columnmodel
			int modelIndex = columnNames.indexOf(box.getText());

			//get the index of this column in the table
            int viewIndex = sortTable.convertColumnIndexToView(modelIndex);

			//if the column is not in the table and the checkbox is now selected,
			//add the column to the table
                if ((viewIndex == -1) && box.isSelected())  {
                  TableColumn tableColumn = new TableColumn(modelIndex);
                  sortTable.addColumn(tableColumn);
                } else {
                	//if the column is in the table and the checkbox is now 
                	//NOT selected, remove the column from the table
                	if ((viewIndex > -1) && !box.isSelected()) {
		            	sortTable.removeColumn(sortTable.getColumn(box.getText()));
	                }
                }
          }
        });
      columnPanel.add(box);
    }

    getContentPane().add(columnPanel);
    pack();
  }

  public void columnAdded(TableColumnModelEvent e) {
    updateState();
  }

  public void columnRemoved(TableColumnModelEvent e) {
    updateState();
  }

  public void columnMoved(TableColumnModelEvent e) {
  }

  public void columnMarginChanged(ChangeEvent e) {
  }

  public void columnSelectionChanged(ListSelectionEvent e) {
  }

  private void updateState() {
	//update the selected state of each checkbox - we don't kmnow which column 
	//was added or removed, so go through all of them
	int modelIndex = 0;
    for (
      Iterator iter2 = columnNames.iterator();
        iter2.hasNext();) {
      Object column = iter2.next();
      int viewIndex = sortTable.convertColumnIndexToView(modelIndex);
      ((JCheckBox)checkBoxes.get(modelIndex)).setSelected(viewIndex > -1);
      modelIndex++;
    }
  }
}
