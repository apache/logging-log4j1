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

import org.apache.log4j.chainsaw.icons.ChainsawIcons;
import org.apache.log4j.chainsaw.prefs.LoadSettingsEvent;
import org.apache.log4j.chainsaw.prefs.SaveSettingsEvent;
import org.apache.log4j.chainsaw.prefs.SettingsListener;
import org.apache.log4j.helpers.ISO8601DateFormat;
import org.apache.log4j.spi.LoggingEvent;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;

import java.text.DateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;


/**
 * A specific TableCellRenderer that colourizes a particular cell based on
 * some ColourFilters that have been stored according to the value for the row
 *
 * @author Claude Duguay
 * @author Scott Deboy <sdeboy@apache.org>
 * @author Paul Smith <psmith@apache.org>
 *
 */
public class TableColorizingRenderer extends DefaultTableCellRenderer
  implements SettingsListener {
  private static final DateFormat DATE_FORMATTER =
    new ISO8601DateFormat(Calendar.getInstance().getTimeZone());
  private Map iconMap = new HashMap();
  private ColorFilter colorFilter;
  private JTable table;
  private Color background = new Color(255, 255, 254);
  private final Color COLOR_ODD = new Color(230, 230, 230);
  private final JLabel idComponent = new JLabel();
  private final JLabel levelComponent = new JLabel();
  private String levelDisplay = ChainsawConstants.LEVEL_DISPLAY_ICONS;

  /**
   * Creates a new TableColorizingRenderer object.
   */
  public TableColorizingRenderer() {
    idComponent.setBorder(BorderFactory.createRaisedBevelBorder());
    idComponent.setBackground(Color.gray);
    idComponent.setHorizontalAlignment(JLabel.CENTER);
    idComponent.setOpaque(true);

    levelComponent.setOpaque(true);
    levelComponent.setHorizontalAlignment(JLabel.CENTER);

    //    Image warningImage = ((IconUIResource)UIManager.getIcon("OptionPane.warningIcon")).getImage().getScaledInstance(16,16, Image.SCALE_FAST);
    String[] iconFileNames =
      new String[] { "Warn.gif", "Inform.gif", "Error.gif" };
    String[] iconLabels = new String[] { "WARN", "INFO", "ERROR" };

    for (int i = 0; i < iconLabels.length; i++) {
      final ImageIcon icon =
        new ImageIcon(
          UIManager.getLookAndFeel().getClass().getResource(
            "icons/" + iconFileNames[i]));
      double scalex = .5;
      double scaley = .5;
      final int newWidth = (int) (scalex * icon.getIconWidth());
      final int newHeight = (int) (scaley * icon.getIconHeight());
      Image iconImage =
        icon.getImage().getScaledInstance(
          newWidth, newHeight, Image.SCALE_SMOOTH);
      iconMap.put(iconLabels[i], new ImageIcon(iconImage));
    }

    /**
     * finally, add the debug icon...
     */
    iconMap.put("DEBUG", ChainsawIcons.ICON_DEBUG);

    levelComponent.setText("");
  }

  /**
  *TODO
  *
  * @param colorFilter TODO
  */
  public void setColorFilter(ColorFilter colorFilter) {
    this.colorFilter = colorFilter;
  }

  public void loadSettings(LoadSettingsEvent event) {
    levelDisplay = event.getSetting(ChainsawConstants.LEVEL_DISPLAY);
  }

  public void saveSettings(SaveSettingsEvent event) {
  }

  /**
   *TODO
   *
   * @param table TODO
   * @param value TODO
   * @param isSelected TODO
   * @param hasFocus TODO
   * @param row TODO
   * @param col TODO
   *
   * @return TODO
   */
  public Component getTableCellRendererComponent(
    JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
    int col) {
    value = formatField(value);

    Color color = null;

    Component c =
      super.getTableCellRendererComponent(
        table, value, isSelected, hasFocus, row, col);
    int colIndex = table.getColumnModel().getColumn(col).getModelIndex() + 1;

    switch (colIndex) {
    case ChainsawColumns.INDEX_ID_COL_NAME:
      idComponent.setText(value.toString());
      idComponent.setForeground(c.getForeground());
      idComponent.setBackground(c.getBackground());
      c = idComponent;

      break;

    case ChainsawColumns.INDEX_LEVEL_COL_NAME:

      Icon icon = (Icon) iconMap.get(value.toString());

      if (
        ((levelDisplay != null)
          && levelDisplay.equals(ChainsawConstants.LEVEL_DISPLAY_ICONS))
          && (icon != null)) {
        levelComponent.setIcon(icon);
        levelComponent.setText("");
        levelComponent.setToolTipText(value.toString());
      } else {
        levelComponent.setIcon(null);
        levelComponent.setText(value.toString());
      }

      levelComponent.setBackground(c.getBackground());
      levelComponent.setForeground(c.getForeground());
      c = levelComponent;

      break;

    default:
      break;
    }

    if (isSelected) {
      return c;
    }

    this.table = table;

    Vector colNames = null;

    if ((color == null) && (colorFilter != null)) {
      TableModel model = table.getModel();
      LoggingEvent event = null;
      if (model instanceof EventContainer) {
        EventContainer model2 = (EventContainer) model;
        event = model2.getRow(row);

        if (event == null) {
          //ignore...probably changed displayed cols
          return c;
        }
      } else {
        throw new UnsupportedOperationException(
          getClass() + " can only support an EventContainer TableModel");
      }

      color = colorFilter.getColor(ChainsawColumns.getColumnsNames(), event);
    }

    if ((color != null)) {
      c.setBackground(color);
    } else if (!isSelected) {
      /**
       * Colourize based on row striping
       */
      if ((row % 2) != 0) {
        c.setBackground(COLOR_ODD);
      } else {
        c.setBackground(background);
      }
    }

    return c;
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
      return DATE_FORMATTER.format((Date) o);
    }
  }
}
