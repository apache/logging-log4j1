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

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import org.apache.log4j.chainsaw.icons.ChainsawIcons;


/**
 * @author Paul Smith <psmith@apache.org>
 *
 */
public class ChainsawColumns {
  private static final List columnNames = new ArrayList();

  static {
    columnNames.add(ChainsawConstants.LOGGER_COL_NAME);
    columnNames.add(ChainsawConstants.TIMESTAMP_COL_NAME);
    columnNames.add(ChainsawConstants.LEVEL_COL_NAME);
    columnNames.add(ChainsawConstants.THREAD_COL_NAME);
    columnNames.add(ChainsawConstants.MESSAGE_COL_NAME);
    columnNames.add(ChainsawConstants.NDC_COL_NAME);
    columnNames.add(ChainsawConstants.THROWABLE_COL_NAME);
    columnNames.add(ChainsawConstants.CLASS_COL_NAME);
    columnNames.add(ChainsawConstants.METHOD_COL_NAME);
    columnNames.add(ChainsawConstants.FILE_COL_NAME);
    columnNames.add(ChainsawConstants.LINE_COL_NAME);

    //NOTE:  ID must ALWAYS be last field because the model adds this value itself as an identifier to the end of the consructed vector
    columnNames.add(ChainsawConstants.ID_COL_NAME);
  }

  public static final int INDEX_LOGGER_COL_NAME = 1;
  public static final int INDEX_TIMESTAMP_COL_NAME = 2;
  public static final int INDEX_LEVEL_COL_NAME = 3;
  public static final int INDEX_THREAD_COL_NAME = 4;
  public static final int INDEX_MESSAGE_COL_NAME = 5;
  public static final int INDEX_NDC_COL_NAME = 6;
  public static final int INDEX_THROWABLE_COL_NAME = 7;
  public static final int INDEX_CLASS_COL_NAME = 8;
  public static final int INDEX_METHOD_COL_NAME = 9;
  public static final int INDEX_FILE_COL_NAME = 10;
  public static final int INDEX_LINE_COL_NAME = 11;
  public static final int INDEX_ID_COL_NAME = 12;

 public static final Cursor CURSOR_FOCUS_ON;
 static{
 	CURSOR_FOCUS_ON = Toolkit.getDefaultToolkit().createCustomCursor(new ImageIcon(ChainsawIcons.WINDOW_ICON).getImage(), new Point(3,3), "FocusOn");
 }

  private ChainsawColumns() {
  }

  public static List getColumnsNames() {
    return columnNames;
  }

  /**
   * Given the index which matches one of the static constants in this class, returns the resolved
   * Column name as a string label.
   * @param columnIndex (not this is a 1 based collection)
   * @return
   */
  public static String getColumnName(int columnIndex) {
    return getColumnsNames().get(columnIndex - 1).toString();
  }
}
