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
    idComponent.setHorizontalAlignment(JLabel.CENTER);
    idComponent.setOpaque(true);

    levelComponent.setOpaque(true);
    levelComponent.setHorizontalAlignment(JLabel.CENTER);

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
        int startPos = logger.length();

        for (int i = 0; i < loggerPrecision; i++) {
          startPos = logger.lastIndexOf(".", startPos - 1);
        }

        if (startPos < 0) {
          break;
        } else {
          ((JLabel) c).setText(logger.substring(startPos + 1));
        }
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
