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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

import org.apache.log4j.Logger;


/**
 *  An extension of TableColumnModel for log4j events.  Primarily this class
 *  manages the preferences for the table.
 *
 * @author <a href="mailto:rdecampo@twcny.rr.com">Ray DeCampo</a>
 */
class MyTableColumnModel extends DefaultTableColumnModel {
  /** Prefix for all properties referenced by this class. */
  public static final String PROP_PREFIX = Preferences.PROP_PREFIX + ".table";

  /** Column order property */
  public static final String ORDER_PROPERTY = PROP_PREFIX + ".columns.order";

  /** Column width property */
  public static final String COLUMN_WIDTH_PROPERTY = "width";

  /** Column visibility property */
  public static final String COLUMN_VISIBLE_PROPERTY = "visible";

  /** Logger for the class */
  private static final Logger LOG = Logger.getLogger(MyTableColumnModel.class);
  private static final Preferences PREFS = Preferences.getInstance();

  /** Map of TableColumns to PreferenceSets */
  private final Map mColPrefMap = new HashMap();
  private final MyTableModel mTableModel;

  /**
   *  Construct a MyTableColumnModel.
   *
   *  @param tableModel table model to work with
   */
  public MyTableColumnModel(MyTableModel tableModel) {
    mTableModel = tableModel;
  }

  /**
   *  Load the properties from the
   *  {@link org.apache.log4j.chainsaw.Preferences} and apply them to the
   *  model.
   */
  public void loadPrefs() {
    // Keep a separate list of columns to remove to avoid concurrent
    // modification
    final List toRemove = new ArrayList(getColumnCount());

    for (int i = 0; i < getColumnCount(); i++) {
      TableColumn col = getColumn(i);
      PreferenceSet colPrefSet =
        PREFS.getPreferenceSet(PROP_PREFIX, mTableModel.getColumnName(i));
      putColumnPreferences(col, colPrefSet);

      int width = colPrefSet.getInteger(COLUMN_WIDTH_PROPERTY, -1);

      if (width >= 0) {
        col.setPreferredWidth(width);
      }

      boolean visible = colPrefSet.getBoolean(COLUMN_VISIBLE_PROPERTY, true);

      if (!visible) {
        LOG.info("Hiding column " + mTableModel.getColumnName(i));
        toRemove.add(col);
      }
    }

    // Order the columns properly
    String[] colOrder = getColumnOrder();

    for (int i = 0; i < colOrder.length; i++) {
      int index = getColumnIndex(colOrder[i]);
      super.moveColumn(index, i);
    }

    // Remove the columns that should not be visible
    for (int i = 0; i < toRemove.size(); i++) {
      removeColumn((TableColumn) toRemove.get(i));
    }
  }

  /**
   *  Update the {@link org.apache.log4j.chainsaw.Preferences} based on the
   *  state of the model.
   */
  public void savePrefs() {
    for (int i = 0; i < getColumnCount(); i++) {
      TableColumn col = getColumn(i);
      PreferenceSet colPrefSet = getColumnPreferences(col);
      colPrefSet.setInteger(COLUMN_WIDTH_PROPERTY, col.getWidth());
      colPrefSet.setBoolean(COLUMN_VISIBLE_PROPERTY, true);
    }
  }

  /** {@inheritDoc} */
  public void addColumn(TableColumn column) {
    PreferenceSet colPrefSet = getColumnPreferences(column);

    if (colPrefSet != null) {
      colPrefSet.setBoolean(COLUMN_VISIBLE_PROPERTY, true);
    }

    super.addColumn(column);
  }

  /** {@inheritDoc} */
  public void removeColumn(TableColumn column) {
    PreferenceSet colPrefSet = getColumnPreferences(column);

    if (colPrefSet != null) {
      colPrefSet.setBoolean(COLUMN_VISIBLE_PROPERTY, false);
    }

    super.removeColumn(column);
  }

  /** {@inheritDoc} */
  public void moveColumn(int columnIndex, int newIndex) {
    super.moveColumn(columnIndex, newIndex);
    saveColumnOrder();
  }

  /**
   *  Get the {@link javax.swing.table.TableColumn}s available for the
   *  logging event table.  All columns are returned iiregardless of whether
   *  thay are or are not visible.
   *
   *  @return  the set of available columns
   */
  public Set getAvailableColumns() {
    return mColPrefMap.keySet();
  }

  /**
   *  Get the {@link org.apache.log4j.chainsaw.PrefenceSet} for a given
   *  column.
   *
   *  @param col  the column to get the preferences for
   *
   *  @return the set of preferences for the column
   */
  public PreferenceSet getColumnPreferences(TableColumn col) {
    return (PreferenceSet) mColPrefMap.get(col);
  }

  /* Store the column preferences in the map. */
  private void putColumnPreferences(TableColumn col, PreferenceSet colPrefSet) {
    mColPrefMap.put(col, colPrefSet);
  }

  /* Determine the order of the columns based on the preferences. */
  private String[] getColumnOrder() {
    String colOrder = PREFS.getProperty(ORDER_PROPERTY);

    if (colOrder == null) {
      return new String[0];
    }

    List result = new ArrayList(6);
    int last = 0;
    int comma = -1;

    while (
      (last < colOrder.length())
        && ((comma = colOrder.indexOf(',', last)) >= 0)) {
      result.add(colOrder.substring(last, comma));
      last = comma + 1;
    }

    if (last < colOrder.length()) {
      result.add(colOrder.substring(last));
    }

    return (String[]) result.toArray(new String[result.size()]);
  }

  /** Save the current column order to the preferences */
  private void saveColumnOrder() {
    StringBuffer colList = new StringBuffer(45);

    for (int i = 0; i < getColumnCount(); i++) {
      if (i > 0) {
        colList.append(',');
      }

      PreferenceSet colPrefSet = getColumnPreferences(getColumn(i));
      colList.append(colPrefSet.getName());
    }

    PREFS.setProperty(ORDER_PROPERTY, colList.toString());
  }
}
