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

/*
 * Created on 11/09/2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.log4j.chainsaw;

import org.apache.log4j.helpers.LogLog;

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
  private JTable table;

  ThrowableRenderPanel(JTable table) {
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
    this.table = table;
    showStackTraceAction.putValue(
      Action.SHORT_DESCRIPTION, "Display the full stack trace in a popup");
    btn.setAction(showStackTraceAction);
  }

	void addActionListener(ActionListener l){
		btn.addActionListener(l);
	}
	
  private void setText(String text) {
    lbl.setText(text);
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
