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

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.table.TableColumn;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;


/**
 *  The PreferencesDialog presenta graphical means for editing
 *  {@link org.apache.log4j.chainsaw.Preferences}.
 *
 *  @author <a href="mailto:rdecampo@twcny.rr.com">Raymond DeCampo</a>
 */
class PreferencesDialog extends JDialog {

  /**
   * The one and only Preferences reference
   */
  private static final Preferences PREFS = Preferences.getInstance();

  /**
   * The ColumnModel to use to save/load stuff from
   */
  private final MyTableColumnModel mColModel;

  /**
   * TextField to enter the max # of files available on MRU list
   */
  private final JTextField mMaxFilesField = new JTextField(6);

  /**
   * CheckBox used to indicate whether the filters should be saved
   */
  private final JCheckBox mSaveFilters = new JCheckBox("Save filters?");

  /** Map relating TableColumns to JCheckBoxes */
  private final Map mColMap = new HashMap(10);

  /**
   *  Construct a PrefencesDialog with the given owner and column model.
   *
   *  @param owner        frame to own the dialog
   *  @param columnModel  column model to manipulate
   */
  public PreferencesDialog(JFrame owner, MyTableColumnModel columnModel) {
    super(owner, "Chainsaw Preferences", true);
    mColModel = columnModel;

    final Box main = Box.createVerticalBox();
    getContentPane().add(main, BorderLayout.CENTER);

    final JPanel filePanel = new JPanel();
    filePanel.setLayout(new GridLayout(1, 2));
    filePanel.add(new JLabel("Maximum # of recent files: ", JLabel.RIGHT));
    filePanel.add(mMaxFilesField);

    // Make sure we only get digits
    mMaxFilesField.setDocument(
      new PlainDocument() {
        public void insertString(int offs, String str, AttributeSet a)
          throws BadLocationException {
          if (str == null) {
            return;
          }

          StringBuffer realStr = new StringBuffer(str.length());

          for (int i = 0; i < str.length(); i++) {
            if (Character.isDigit(str.charAt(i))) {
              realStr.append(str.charAt(i));
            }
          }

          // No more than four characters
          if ((getLength() + realStr.length()) > 4) {
            int truncLen = Math.max(0, 4 - getLength());
            realStr.setLength(truncLen);
          }

          super.insertString(offs, realStr.toString(), a);
        }
      });
    mMaxFilesField.setText(String.valueOf(PREFS.getMaxFiles()));
    filePanel.setAlignmentX(1.0f);
    main.add(filePanel);
    main.add(Box.createVerticalStrut(3));

    final PreferenceSet controlPrefs =
      PREFS.getPreferenceSet(
        Preferences.PROP_PREFIX, ControlPanel.PREF_SET_NAME);
    mSaveFilters.setSelected(
      controlPrefs.getBoolean(ControlPanel.SAVE_PROPERTY, true));
    mSaveFilters.setHorizontalAlignment(JCheckBox.LEFT);

    final JPanel filterPanel = new JPanel();
    filterPanel.setAlignmentX(1.0f);
    filterPanel.add(mSaveFilters);
    main.add(filterPanel);
    main.add(Box.createVerticalStrut(6));

    final JPanel colPanel = new JPanel();
    colPanel.setLayout(new GridLayout(0, 3));

    final Iterator colIter = getAvailableColumns();

    while (colIter.hasNext()) {
      final TableColumn col = (TableColumn) colIter.next();
      final String colName = String.valueOf(col.getHeaderValue());
      final PreferenceSet colPrefs = mColModel.getColumnPreferences(col);
      final boolean visible =
        colPrefs.getBoolean(MyTableColumnModel.COLUMN_VISIBLE_PROPERTY, true);
      JCheckBox check = new JCheckBox(colName, visible);
      mColMap.put(col, check);
      colPanel.add(check);
    }

    colPanel.setBorder(BorderFactory.createTitledBorder("Display columns:"));
    main.add(colPanel);
    main.add(Box.createVerticalStrut(3));

    final JPanel buttonPanel = new JPanel();
    buttonPanel.setAlignmentX(0.0f);
    buttonPanel.add(new JButton(new OKAction()));
    buttonPanel.add(new JButton(new CancelAction()));
    getContentPane().add(buttonPanel, BorderLayout.SOUTH);

    pack();
    setLocationRelativeTo(owner);
  }

  /**
   * Get the columns from the model in alphabetical order
   * @return Iterator of available columns
   */
  private Iterator getAvailableColumns() {
    SortedSet cols =
      new TreeSet(
        new Comparator() {
          public int compare(Object x, Object y) {
            final TableColumn xCol = (TableColumn) x;
            final TableColumn yCol = (TableColumn) y;

            return String.valueOf(xCol.getHeaderValue()).compareTo(
              String.valueOf(yCol.getHeaderValue()));
          }
        });
    cols.addAll(mColModel.getAvailableColumns());

    return cols.iterator();
  }

  /**
   * OK button handler
   */
  private class OKAction extends AbstractAction {

    /**
     * Constructor
     */
    public OKAction() {
      putValue(NAME, "OK");
    }

    /**
     * ActionPerformed handler when they press Ok
     * @param ae ActionEvent when the button is pressed
     */
    public void actionPerformed(ActionEvent ae) {
      // File preferences
      try {
        final int maxFiles = Integer.parseInt(mMaxFilesField.getText());
        PREFS.setMaxFiles(maxFiles);
      } catch (final NumberFormatException nfe) {
        // This really ought not happen given the document
        // unless nothing was entered
      }

      // Filter preferences
      final PreferenceSet controlPrefs =
        PREFS.getPreferenceSet(
          Preferences.PROP_PREFIX, ControlPanel.PREF_SET_NAME);
      controlPrefs.setBoolean(
        ControlPanel.SAVE_PROPERTY, mSaveFilters.isSelected());

      // Column preferences
      Iterator colIter = getAvailableColumns();

      while (colIter.hasNext()) {
        final TableColumn col = (TableColumn) colIter.next();
        final JCheckBox check = (JCheckBox) mColMap.get(col);
        final PreferenceSet colPrefs = mColModel.getColumnPreferences(col);
        final boolean visible =
          colPrefs.getBoolean(
            MyTableColumnModel.COLUMN_VISIBLE_PROPERTY, true);

        if (check.isSelected() && !visible) {
          mColModel.addColumn(col);
        } else if (!check.isSelected() && visible) {
          mColModel.removeColumn(col);
        }
      }

      hide();
    }
  }

  /**
   * Cancel button handler
   */
  private class CancelAction extends AbstractAction {

    /**
     * Constructor for the action
     */
    public CancelAction() {
      putValue(NAME, "Cancel");
    }

    /**
     * ActionEvent handler
     * @param ae ActionEvent when they press Cancel
     */
    public void actionPerformed(ActionEvent ae) {
      hide();
    }
  }
}
