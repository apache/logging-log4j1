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

import org.apache.log4j.chainsaw.color.Colorizer;
import org.apache.log4j.chainsaw.icons.LevelIconFactory;
import org.apache.log4j.helpers.ISO8601DateFormat;
import org.apache.log4j.spi.LoggingEvent;

import java.awt.Color;
import java.awt.Component;

import java.text.DateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;


/**
 * A specific TableCellRenderer that colourizes a particular cell based on
 * some ColourFilters that have been stored according to the value for the row
 *
 * @author Claude Duguay
 * @author Scott Deboy <sdeboy@apache.org>
 * @author Paul Smith <psmith@apache.org>
 *
 */
public class TableColorizingRenderer extends DefaultTableCellRenderer {
  private static final DateFormat DATE_FORMATTER =
    new ISO8601DateFormat(Calendar.getInstance().getTimeZone());
  private static final Map iconMap =
    LevelIconFactory.getInstance().getLevelToIconMap();
  private Colorizer colorizer;
  private final JLabel idComponent = new JLabel();
  private final JLabel levelComponent = new JLabel();
  private boolean levelUseIcons = true;
  private DateFormat dateFormatInUse = DATE_FORMATTER;
  private int loggerPrecision = 0;
  private boolean toolTipsVisible;

  /**
   * Creates a new TableColorizingRenderer object.
   */
  public TableColorizingRenderer(Colorizer colorizer) {
    this.colorizer = colorizer;
    idComponent.setBorder(BorderFactory.createRaisedBevelBorder());
    idComponent.setBackground(Color.gray);
    idComponent.setHorizontalAlignment(SwingConstants.CENTER);
    idComponent.setOpaque(true);

    levelComponent.setOpaque(true);
    levelComponent.setHorizontalAlignment(SwingConstants.CENTER);

    levelComponent.setText("");
  }
  
  public void setToolTipsVisible(boolean toolTipsVisible) {
      this.toolTipsVisible = toolTipsVisible;
  }

  public Component getTableCellRendererComponent(
    final JTable table, Object value, boolean isSelected, boolean hasFocus,
    int row, int col) {
    value = formatField(value);

    Color background = null;
    Color foreground = null;

    Component c = super.getTableCellRendererComponent(table, value, 
        isSelected, hasFocus, row, col);
    int colIndex = table.getColumnModel().getColumn(col).getModelIndex() + 1;

    switch (colIndex) {
    case ChainsawColumns.INDEX_ID_COL_NAME:
      idComponent.setText(value.toString());
      idComponent.setForeground(c.getForeground());
      idComponent.setBackground(c.getBackground());
      c = idComponent;
      break;

    case ChainsawColumns.INDEX_THROWABLE_COL_NAME:
      if (value instanceof String[]) {
        ((JLabel) c).setText(((String[]) value)[0]);
      }
      break;

    case ChainsawColumns.INDEX_LOGGER_COL_NAME:
      if (loggerPrecision == 0) {
        break;
      } else {
        String logger = value.toString();
        int startPos = -1;

        for (int i = 0; i < loggerPrecision; i++) {
          startPos = logger.indexOf(".", startPos + 1);
          if (startPos < 0) {
              break;
          }
        }

        ((JLabel) c).setText(logger.substring(startPos + 1));
      }
      break;

    case ChainsawColumns.INDEX_LEVEL_COL_NAME:
      if (levelUseIcons) {
        levelComponent.setIcon((Icon) iconMap.get(value.toString()));

        if (levelComponent.getIcon() != null) {
          levelComponent.setText("");
        }

        if (toolTipsVisible) {
            levelComponent.setToolTipText(((JLabel)c).getToolTipText());
        } else {
            levelComponent.setToolTipText(value.toString());
        } 
      } else {
        levelComponent.setIcon(null);
        levelComponent.setText(value.toString());
      }
      levelComponent.setForeground(c.getForeground());
      levelComponent.setBackground(c.getBackground());

      c = levelComponent;
      break;

    default:
      break;
    }

    if (isSelected) {
      return c;
    }

    if (getColorizer() != null) {
      EventContainer container = (EventContainer) table.getModel();
      LoggingEvent event = container.getRow(row);

      if (event == null) {
        //ignore...probably changed displayed cols
        return c;
      }
      background = getColorizer().getBackgroundColor(event);
      foreground = getColorizer().getForegroundColor(event);
    }

    /**
     * Colourize based on row striping
     */
    if (background == null) {
      if ((row % 2) != 0) {
        background = ChainsawConstants.COLOR_ODD_ROW;
      } else {
        background = ChainsawConstants.COLOR_EVEN_ROW;
      }
    }

    if (foreground == null) {
      foreground = Color.black;
    }
    
    c.setBackground(background);
    c.setForeground(foreground);
    
    return c;
  }

  /**
   * Changes the Date Formatting object to be used for rendering dates.
   * @param formatter
   */
  void setDateFormatter(DateFormat formatter) {
    this.dateFormatInUse = formatter;
  }

  /**
   * Changes the Logger precision.
   * @param precision
   */
  void setLoggerPrecision(String loggerPrecisionText) {
    try {
      loggerPrecision = Integer.parseInt(loggerPrecisionText);
    } catch (NumberFormatException nfe) {
        loggerPrecision = 0;
    }
  }

  /**
   *Format date field
   *
   * @param o object
   *
   * @return formatted object
   */
  private Object formatField(Object o) {
    if (!(o instanceof Date)) {
      return o;
    } else {
      return dateFormatInUse.format((Date) o);
    }
  }

  /**
   * @param colorizer
   */
  public void setColorizer(Colorizer colorizer) {
    this.colorizer = colorizer;
  }

  /**
   * @return
   */
  public Colorizer getColorizer() {
    return colorizer;
  }

  /**
   * Returns true if this renderer will use Icons to render the Level
   * column, otherwise false.
   * @return
   */
  public boolean isLevelUseIcons() {
    return levelUseIcons;
  }

  /**
   * Sets the property which determines whether to use Icons or text
   * for the Level column
   * @param levelUseIcons
   */
  public void setLevelUseIcons(boolean levelUseIcons) {
    this.levelUseIcons = levelUseIcons;
  }
}
