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

/*
 * Created on 11/09/2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.log4j.chainsaw;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;


/**
 * An "editor" that doesn't allow editing, but allows the user to press a "..." for more detail about this
 * Column.
 *
 * @author Paul Smith <psmith@apache.org>
 *
 */
class ThrowableRenderPanel extends AbstractCellEditor
  implements TableCellEditor {
  private final SmallButton btn = new SmallButton();
  private final JLabel lbl = new JLabel("");
  private final JPanel panel = new JPanel();
  private Color background = new Color(255, 255, 254);
  private final Color COLOR_ODD = new Color(230, 230, 230);
  private final Action showStackTraceAction;

  ThrowableRenderPanel() {
    panel.setLayout(new BorderLayout());
    panel.add(lbl, BorderLayout.CENTER);
    panel.add(btn, BorderLayout.EAST);
    lbl.setOpaque(false);
//    btn.setOpaque(false);
    showStackTraceAction =
      new AbstractAction("...") {
          public void actionPerformed(ActionEvent e) {
          }
        };
    showStackTraceAction.putValue(
      Action.SHORT_DESCRIPTION, "Display the full stack trace in a popup");
    btn.setAction(showStackTraceAction);
  }

	void addActionListener(ActionListener l){
		btn.addActionListener(l);
	}
	
  /* (non-Javadoc)
   * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
   */
  public Component getTableCellEditorComponent(
    JTable table, Object value, boolean isSelected, int row, int column) {
    if(value!=null){
	    lbl.setText(((String[]) value)[0]);
    }else {
    	lbl.setText("");
    }

    if (isSelected) {
      panel.setBackground(table.getSelectionBackground());
      panel.setForeground(table.getSelectionForeground());
    } else if ((row % 2) != 0) {
      panel.setBackground(COLOR_ODD);
	  panel.setForeground(table.getSelectionForeground());
    } else {
      panel.setBackground(background);
	  panel.setForeground(table.getSelectionForeground());
    }

    return panel;
  }

  /* (non-Javadoc)
   * @see javax.swing.CellEditor#getCellEditorValue()
   */
  public Object getCellEditorValue() {
    return lbl.getText();
  }
}
