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

import java.awt.*;

import javax.swing.*;
import javax.swing.table.*;


/**
 * A Table Column header renederer that displays a nice Up/Down arrow
 * depending on whether this column is the current sort column or not,
 * and which way the sort is functioning
 * @author Claude Duguay
*/
public class SortHeaderRenderer extends DefaultTableCellRenderer {
  public static final Icon NONSORTED = new SortArrowIcon(SortArrowIcon.NONE);
  public static final Icon ASCENDING = new SortArrowIcon(SortArrowIcon.ASCENDING);
  public static final Icon DECENDING = new SortArrowIcon(SortArrowIcon.DECENDING);

  public SortHeaderRenderer() {
    setHorizontalTextPosition(LEFT);
    setHorizontalAlignment(CENTER);
  }

  public Component getTableCellRendererComponent(
    JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
    int col) {
    int index = -1;
    boolean ascending = true;

    if (table instanceof JSortTable) {
      JSortTable sortTable = (JSortTable) table;
      index = sortTable.getSortedColumnIndex();
      ascending = sortTable.isSortedColumnAscending();
    }

    if (table != null) {
      JTableHeader header = table.getTableHeader();

      if (header != null) {
        setForeground(header.getForeground());
        setBackground(header.getBackground());
        Font font = header.getFont();
		if (col == index) {
			
			setFont(new Font(font.getName(), Font.BOLD, font.getSize()));
		}
		else {
		  setFont(font);
		}
      }
    }

    Icon icon = ascending ? ASCENDING : DECENDING;
    setIcon((col == index) ? icon : NONSORTED);
    setText((value == null) ? "" : value.toString());
    setBorder(UIManager.getBorder("TableHeader.cellBorder"));

    return this;
  }
}
