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

import org.apache.log4j.chainsaw.prefs.SettingsManager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


/**
 * A JFrame that represents configuration information on how
 * LoggingEvents are displayed, or colourized.  This works much like a
 * dialog would without getting in the way of giving up control over the other
 * frame.
 * @author Scott Deboy <sdeboy@apache.org>
 */
public class ColorDisplaySelector extends JFrame implements ChangeListener,
  ListSelectionListener {
  private static final String NONE_COL_NAME = "None";
  private static final String LEVEL_COL_NAME = "Level";
  private static final String COLOR_FILTERS_EXTENSION = ".colorfilters";
  private static final String DISPLAY_FILTERS_EXTENSION = ".displayfilters";
  private final JList list = new JList();
  private String selectedValue;
  private String selectedColumn;
  private Color selectedColor = null;
  private JColorChooser colorChooser = new JColorChooser();
  private ColorFilter colorFilter;
  private DisplayFilter displayFilter;
  private Map columnColorMap;
  private Map columnDisplayMap;
  private Map buttonMap = new HashMap();
  private String applyColorUpdateColumn;
  private String applyDisplayUpdateColumn;
  private List levelList;
  private Map levelMap = new HashMap();
  private Map globalLevelMap = new HashMap();
  private Map selectedColorMap = new HashMap();
  private Map selectedDisplayMap = new HashMap();
  private Map displayDisplayMap;
  private Map colorDisplayMap;
  private Map columnEntryMap;
  private String title;

  /**
   * Constructor that builds all the UI elements, and binds all the
   * actions to relevant buttons/menus.
   */
  public ColorDisplaySelector(
    final String title, Map columnEntryMap, final ColorFilter colorFilter,
    final DisplayFilter displayFilter, final Map colorDisplayMap,
    final Map displayDisplayMap, final List columnNames,
    final List filterableColumns, final List levelList) {
    super(title + " - Select color and display filters");

    this.title = title;
    this.colorFilter = colorFilter;
    this.displayFilter = displayFilter;
    this.columnEntryMap = columnEntryMap;
    this.colorDisplayMap = colorDisplayMap;
    this.displayDisplayMap = displayDisplayMap;
    this.levelList = levelList;
    loadFilters();

    colorChooser.setPreviewPanel(new JPanel());
    list.setCellRenderer(new ColoredCellRenderer());
    setLocation(50, 50);

    JPanel p = new JPanel(new BorderLayout());

    //get all of the entrysets - a map entry with the key being the column
    //and the value being a hashset containing all the values for that column
    JPanel combinedColumnPanel = new JPanel();
    combinedColumnPanel.setLayout(
      new BoxLayout(combinedColumnPanel, BoxLayout.X_AXIS));

    JPanel columnPanel = new JPanel();
    columnPanel.setLayout(new BoxLayout(columnPanel, BoxLayout.Y_AXIS));

    columnPanel.setBorder(BorderFactory.createTitledBorder("View"));

    columnPanel.add(new JLabel("Column:"));

    JPanel selectedColorPanel = new JPanel();
    selectedColorPanel.setLayout(
      new BoxLayout(selectedColorPanel, BoxLayout.Y_AXIS));
    selectedColorPanel.add(new JLabel("Color:"));

    //    selectedColorPanel.setBorder(
    //      BorderFactory.createTitledBorder("Select a color filter:"));
    JPanel selectedDisplayPanel = new JPanel();
    selectedDisplayPanel.setLayout(
      new BoxLayout(selectedDisplayPanel, BoxLayout.Y_AXIS));
    selectedDisplayPanel.add(new JLabel("Display:"));

    //    selectedDisplayPanel.setBorder(
    //      BorderFactory.createTitledBorder("Select a display filter:"));
    ButtonGroup columnGroup = new ButtonGroup();
    ButtonGroup selectedColorGroup = new ButtonGroup();
    ButtonGroup selectedDisplayGroup = new ButtonGroup();

    Iterator iter = filterableColumns.iterator();

    while (iter.hasNext()) {
      final String key = (String) iter.next();

      //get all of the regular expressions for the color filter tied to this column
      final Collection colorFilterCollection =
        colorFilter.getRegExpByColumn(key);

      //get all of the values associated with this column
      final Set s = (Set) columnEntryMap.get(key);

      //add the entries from colorfiltercollection (in case custom ones are supported)
      s.addAll(colorFilterCollection);

      //build a local map of keys to color entries for each column
      HashMap colors = colorFilter.getEntriesByColumn(key);
      Object c = columnColorMap.get(key);

      if ((c != null) && c instanceof HashMap) {
        colors.putAll((HashMap) c);
      }

      columnColorMap.put(key, colors);

      //get all of the regular expressions for the display filter tied to this column
      final Collection displayFilterCollection =
        displayFilter.getValuesByColumn(key);

      //add the entries from colorfiltercollection (in case custom ones are supported)
      s.addAll(displayFilterCollection);

      //build a local map of keys to display entries for each column
      Vector displays = displayFilter.getEntriesByColumn(key);
      Object d = columnDisplayMap.get(key);

      if ((d != null) && d instanceof Vector) {
        displays.addAll((Vector) d);
      }

      columnDisplayMap.put(key, displays);

      int displayStyle = Font.PLAIN;

      if (displays.size() > 0) {
        displayStyle = displayStyle + Font.BOLD;
      }

      int colorStyle = Font.PLAIN;

      if (colors.size() > 0) {
        colorStyle = colorStyle + Font.BOLD;
      }

      JRadioButton colorButton = new JRadioButton((String) key);
      colorButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            applyColorUpdateForColumn((String) key);
            applyColorFilters((String) key);

            JMenuItem m = (JMenuItem) colorDisplayMap.get((String) key);

            if (m != null) {
              m.setSelected(true);
            }
          }
        });

      colorButton.setFont(colorButton.getFont().deriveFont(colorStyle));

      selectedColorGroup.add(colorButton);
      selectedColorPanel.add(colorButton);
      selectedColorMap.put(key, colorButton);

      JRadioButton displayButton = new JRadioButton((String) key);
      displayButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            applyDisplayUpdateForColumn((String) key);
            applyDisplayFilters((String) key);

            JMenuItem m = (JMenuItem) displayDisplayMap.get((String) key);

            if (m != null) {
              m.setSelected(true);
            }
          }
        });
      displayButton.setFont(displayButton.getFont().deriveFont(displayStyle));

      selectedDisplayGroup.add(displayButton);
      selectedDisplayPanel.add(displayButton);
      selectedDisplayMap.put(key, displayButton);

      boolean selected = (buttonMap.size() == 0);

      //'none' isn't valid - just add as a spacer
      if ((key.equalsIgnoreCase(NONE_COL_NAME))) {
        JRadioButton fake = new JRadioButton(key);
        fake.setEnabled(false);
        columnPanel.add(fake);
      } else {
        JRadioButton button = new JRadioButton((String) key, selected);
        button.setFont(button.getFont().deriveFont(Font.PLAIN));
        buttonMap.put(key, button);

        if (selected) {
          selectedColumn = key;
        }

        button.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              if ((s != null) && (s.size() > 0)) {
                ArrayList l = new ArrayList();
                l.addAll(s);
                Collections.sort(l);
                list.setListData(l.toArray());
              }

              selectedValue = null;
              selectedColumn = key;

              if ((s.size() > 0) && (list.getSelectedIndex() < 0)) {
                list.setSelectedIndex(0);
              }

              syncDisplayCheckBoxes();
              syncGlobalDisplayCheckBoxes();
            }
          });

        columnGroup.add(button);
        columnPanel.add(button);
      }
    }

    JPanel colorDisplayPanel = new JPanel();
    colorDisplayPanel.setBorder(
      BorderFactory.createTitledBorder("Activate filters:"));

    colorDisplayPanel.setLayout(
      new BoxLayout(colorDisplayPanel, BoxLayout.X_AXIS));
    colorDisplayPanel.add(selectedColorPanel);
    colorDisplayPanel.add(selectedDisplayPanel);

    combinedColumnPanel.add(colorDisplayPanel);
    combinedColumnPanel.add(columnPanel);

    //build panel which displays the available levels
    JPanel levelPanel = new JPanel();
    levelPanel.setBorder(
      BorderFactory.createTitledBorder("Selected value's display levels:"));

    JPanel innerLevelPanel = new JPanel();
    innerLevelPanel.setLayout(
      new BoxLayout(innerLevelPanel, BoxLayout.Y_AXIS));

    Iterator levelIter = levelList.iterator();

    while (levelIter.hasNext()) {
      final String level = (String) levelIter.next();
      final JCheckBox box = new JCheckBox(level);
      levelMap.put(level, box);
      box.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (selectedColumn != null) {
              if (box.isSelected()) {
                if ((selectedValue != null) && (selectedColumn != null)) {
                  System.out.println(
                    "adding display filter for level: " + level + " column "
                    + selectedColumn + " value: " + selectedValue);
                  ;
                  addDisplayFilter(
                    new DisplayFilterEntry(
                      selectedColumn, selectedValue, level));
                }
              } else {
                if ((selectedValue != null) && (selectedColumn != null)) {
                  System.out.println(
                    "removing display filter for level: " + level + " column "
                    + selectedColumn + " value: " + selectedValue);

                  removeDisplayFilter(
                    new DisplayFilterEntry(
                      selectedColumn, selectedValue, level));
                }
              }

              syncDisplayCheckBoxes();
              syncGlobalDisplayCheckBoxes();
              list.revalidate();
              list.repaint();
            }
          }
        });
      innerLevelPanel.add(box);
    }

    levelPanel.add(innerLevelPanel);

    JPanel globalLevelPanel = new JPanel();
    globalLevelPanel.setBorder(
      BorderFactory.createTitledBorder("Column-wide display levels:"));

    JPanel innerGlobalLevelPanel = new JPanel();
    innerGlobalLevelPanel.setLayout(
      new BoxLayout(innerGlobalLevelPanel, BoxLayout.Y_AXIS));

    Iterator iter2 = levelList.iterator();

    while (iter2.hasNext()) {
      final String level = (String) iter2.next();
      final JCheckBox box = new JCheckBox(level);
      globalLevelMap.put(level, box);
      innerGlobalLevelPanel.add(box);
      box.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (selectedColumn != null) {
              if (selectedColumn.equalsIgnoreCase(LEVEL_COL_NAME)) {
                if (box.isSelected()) {
                  addDisplayFilter(
                    new DisplayFilterEntry(
                      selectedColumn, level, level));
                } else {
                  removeDisplayFilter(
                    new DisplayFilterEntry(
                      selectedColumn, level, level));
                }
              } else {
                if (box.isSelected()) {
                  addGlobalDisplayFilter(level);
                } else {
                  removeGlobalDisplayFilter(level);
                }
              }

              syncDisplayCheckBoxes();
              syncGlobalDisplayCheckBoxes();
              list.revalidate();
              list.repaint();
            }
          }
        });
    }

    globalLevelPanel.add(innerGlobalLevelPanel);

    list.getSelectionModel().addListSelectionListener(this);
    colorChooser.getSelectionModel().addChangeListener(this);

    JScrollPane scrollPane = new JScrollPane(list);
    scrollPane.setHorizontalScrollBarPolicy(
      JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    scrollPane.setVerticalScrollBarPolicy(
      JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

    JPanel scrollPanePanel = new JPanel(new GridLayout());
    scrollPanePanel.setBorder(
      BorderFactory.createTitledBorder("Column values:"));
    scrollPanePanel.add(scrollPane);

    JPanel stackedColorDisplayPanel = new JPanel(new BorderLayout());
    JPanel leftRightLevelPanel = new JPanel(new GridLayout(1, 2));

    JPanel colorChooserPanel = new JPanel();
    colorChooserPanel.setBorder(
      BorderFactory.createTitledBorder("Choose colors:"));
    colorChooserPanel.add(colorChooser);
    leftRightLevelPanel.add(levelPanel);
    leftRightLevelPanel.add(globalLevelPanel);
    stackedColorDisplayPanel.add(leftRightLevelPanel, BorderLayout.NORTH);
    stackedColorDisplayPanel.add(colorChooserPanel, BorderLayout.CENTER);

    p.add(combinedColumnPanel, BorderLayout.WEST);
    p.add(scrollPanePanel, BorderLayout.CENTER);
    p.add(stackedColorDisplayPanel, BorderLayout.EAST);
    getContentPane().add(p);
    pack();
    updateAllButtonStyles();
  }

    public void save() {
      ObjectOutputStream o = null;

      try {
        o = new ObjectOutputStream(
            new BufferedOutputStream(
              new FileOutputStream(
                new File(
                  SettingsManager.getInstance().getSettingsDirectory()
                  + File.separator + title
                  + ChainsawConstants.FILTERS_EXTENSION))));
        o.writeObject(columnColorMap);
        o.writeObject(columnDisplayMap);
        o.flush();
      } catch (FileNotFoundException fnfe) {
      } catch (IOException ioe) {
        ioe.printStackTrace();
      }

      try {
        if (o != null) {
          o.close();
        }
      } catch (IOException ioe) {
      }
    }

  /**
   * Regurgitates the Filters from an offline store.
   */
  private void loadFilters() {
    ObjectInputStream s = null;

    try {
      s = new ObjectInputStream(
          new BufferedInputStream(
            new FileInputStream(
              new File(
                SettingsManager.getInstance().getSettingsDirectory()
                + File.separator + title + ChainsawConstants.FILTERS_EXTENSION))));
      columnColorMap = (HashMap) s.readObject();
      columnDisplayMap = (HashMap) s.readObject();
    } catch (IOException ioe) {
    } catch (ClassNotFoundException cnfe) {
    } finally {
      if (s != null) {
        try {
          s.close();
        } catch (IOException ioe) {
        }
      }
    }

    if (columnColorMap == null) {
      columnColorMap = new HashMap();
    }

    if (columnDisplayMap == null) {
      columnDisplayMap = new HashMap();
    }
  }

  /**
   * Updates the List with the relevant values for the selected Column.
   * @param selectedColumn
   */
  private void updateList(String selectedColumn) {
    Set s = (Set) columnEntryMap.get(selectedColumn);

    if ((s != null) && (s.size() > 0)) {
      ArrayList l = new ArrayList();
      l.addAll(s);
      Collections.sort(l);
      list.setListData(l.toArray());
    }

    if ((s.size() > 0) && (list.getSelectedIndex() < 0)) {
      list.setSelectedIndex(0);
    }

    list.revalidate();
    list.repaint();
  }

  /**
   * When shown, ensures that the currently selected Column is correctly
   * displayed and any other related information on the Frame stays in sync
   */
  public void show() {
    super.show();

    if (selectedColumn != null) {
      selectColumn(selectedColumn);
      syncDisplayCheckBoxes();
    }
  }

  /**
   * When someone changes the selected Column, the appropriate List panel
   * needs to get updated with the relevant information for that selection
   * @param column
   */
  public void selectColumn(String column) {
    selectedColumn = column;

    JRadioButton b = (JRadioButton) buttonMap.get(column);
    b.setSelected(true);
    updateList(column);
  }

  public void applyColorFilters(String column) {
    colorFilter.clear();

    if (column.equalsIgnoreCase(NONE_COL_NAME)) {
      return;
    }

    HashMap m = (HashMap) columnColorMap.get(column);
    Collection c = m.entrySet();
    Iterator iter = c.iterator();

    while (iter.hasNext()) {
      Map.Entry entry = (Map.Entry) iter.next();
      colorFilter.addFilter(
        column, (String) entry.getKey(), (Color) entry.getValue());
    }
  }

  /**
   * Removes all the...mmm?
   */
  public void clearColors() {
    Set s = columnColorMap.keySet();
    Iterator iter = s.iterator();

    while (iter.hasNext()) {
      HashMap m = (HashMap) columnColorMap.get((String) iter.next());
      m.clear();
    }
  }

  private void addColorFilter(String column, String regExp, Color color) {
    HashMap map = (HashMap) columnColorMap.get(column);
    map.put(regExp, color);

    if (
      (applyColorUpdateColumn != null)
        && applyColorUpdateColumn.equals(column)) {
      applyColorFilters(column);
    }
  }

  private Color getColor(String column, String regExp) {
    Color color = null;

    if (column != null) {
      HashMap map = (HashMap) columnColorMap.get(column);
      Object o = map.get(regExp);

      if (o instanceof Color) {
        color = (Color) o;
      }
    }

    return color;
  }

  private boolean filterExists(String column, String value) {
    if (column != null) {
      Vector v = (Vector) columnDisplayMap.get(column);

      if (v != null) {
        Iterator iter = v.iterator();

        while (iter.hasNext()) {
          DisplayFilterEntry entry = (DisplayFilterEntry) iter.next();

          if (
            entry.getColumnName().equals(column)
              && (entry.getColumnValue().equals(value))) {
            return true;
          }
        }
      }
    }

    return false;
  }

  public void valueChanged(ListSelectionEvent e) {
    if (e.getValueIsAdjusting()) {
      return;
    }

    int selected = list.getSelectedIndex();

    if (selected > -1) {
      selectedValue = (String) list.getModel().getElementAt(selected);
      selectedColor = null;
    }

    syncDisplayCheckBoxes();
  }

  public void applyColorUpdateForColumn(String column) {
    applyColorUpdateColumn = column;

    JRadioButton button = (JRadioButton) selectedColorMap.get(column);
    button.setSelected(true);
  }

  private void updateAllButtonStyles() {
    Set displaySet = displayDisplayMap.entrySet();
    Iterator iter = displaySet.iterator();

    while (iter.hasNext()) {
      Map.Entry displayEntry = (Map.Entry) iter.next();
      String colName = (String) displayEntry.getKey();
      JMenuItem d = (JMenuItem) displayEntry.getValue();
      int displayStyle = Font.PLAIN;
      Vector displayFilterVector = (Vector) columnDisplayMap.get(colName);

      if ((displayFilterVector != null) && (displayFilterVector.size() > 0)) {
        displayStyle = displayStyle + Font.BOLD;
      }

      if (d != null) {
        d.setFont(d.getFont().deriveFont(displayStyle));
      } else {
        //d was null - button not found
      }
    }

    Set colorSet = colorDisplayMap.entrySet();
    Iterator iter2 = colorSet.iterator();

    while (iter2.hasNext()) {
      Map.Entry colorEntry = (Map.Entry) iter2.next();
      String colName = (String) colorEntry.getKey();
      JMenuItem c = (JMenuItem) colorEntry.getValue();
      int colorStyle = Font.PLAIN;
      HashMap colorFilterMap = (HashMap) columnColorMap.get(colName);

      if ((colorFilterMap != null) && (colorFilterMap.size() > 0)) {
        colorStyle = colorStyle + Font.BOLD;
      }

      if (c != null) {
        c.setFont(c.getFont().deriveFont(colorStyle));
      } else {
        //c was null - button not found
      }
    }
  }

  private void updateButtonStyle() {
    if (selectedColumn != null) {
      JRadioButton displayButton =
        (JRadioButton) selectedDisplayMap.get(selectedColumn);
      int displayStyle = Font.PLAIN;
      Vector displayFilterVector =
        (Vector) columnDisplayMap.get(selectedColumn);

      if (displayFilterVector.size() > 0) {
        displayStyle = displayStyle + Font.BOLD;
      }

      displayButton.setFont(displayButton.getFont().deriveFont(displayStyle));

      JMenuItem d = (JMenuItem) displayDisplayMap.get(selectedColumn);

      if (d != null) {
        d.setFont(displayButton.getFont());
      } else {
        //d was null - button not found
      }

      JRadioButton colorButton =
        (JRadioButton) selectedColorMap.get(selectedColumn);
      HashMap colorFilterMap = (HashMap) columnColorMap.get(selectedColumn);
      int colorStyle = Font.PLAIN;

      if (colorFilterMap.size() > 0) {
        colorStyle = colorStyle + Font.BOLD;
      }

      colorButton.setFont(colorButton.getFont().deriveFont(colorStyle));

      JMenuItem c = (JMenuItem) colorDisplayMap.get(selectedColumn);

      if (c != null) {
        c.setFont(colorButton.getFont());
      } else {
        //c was null - button not found
      }
    }
  }

  public void stateChanged(ChangeEvent e) {
    if (selectedColumn != null) {
      selectedColor = colorChooser.getColor();

      if ((selectedValue != null) && (selectedColor != null)) {
        addColorFilter(selectedColumn, selectedValue, selectedColor);
      }

      list.revalidate();
      list.repaint();
      updateButtonStyle();
    }
  }

  public void applyDisplayFilters(String column) {
    displayFilter.clear();

    if (column.equalsIgnoreCase(NONE_COL_NAME)) {
      return;
    }

    Vector v = (Vector) columnDisplayMap.get(column);
    Vector filters = new Vector();

    if (v != null) {
      Iterator iter = v.iterator();

      while (iter.hasNext()) {
        DisplayFilterEntry entry = (DisplayFilterEntry) iter.next();
        filters.add(entry);
      }
    }

    displayFilter.addFilters(filters);
  }

  public void clearDisplay() {
    Set s = columnDisplayMap.keySet();
    Iterator iter = s.iterator();

    while (iter.hasNext()) {
      Vector v = (Vector) columnDisplayMap.get((String) iter.next());

      if (v != null) {
        v.clear();
      }
    }

    syncDisplayCheckBoxes();
    syncGlobalDisplayCheckBoxes();
    list.revalidate();
    list.repaint();
  }

  //column name is the name of the column, the regular expression is the level value
  private void addDisplayFilter(DisplayFilterEntry entry) {
    Vector v = (Vector) columnDisplayMap.get(entry.getColumnName());

    if (v != null) {
      if (!v.contains(entry)) {
        v.add(entry);
      }
    } else {
      v = new Vector();
      v.add(entry);
      columnDisplayMap.put(entry.getColumnName(), v);
    }

    if (
      (applyDisplayUpdateColumn != null)
        && applyDisplayUpdateColumn.equals(entry.getColumnName())) {
      applyDisplayFilters(entry.getColumnName());
    }
  }

  //column name is the name of the column, the regular expression is the level value
  private void removeDisplayFilter(DisplayFilterEntry entry) {
    Vector v = (Vector) columnDisplayMap.get(entry.getColumnName());

    if (v != null) {
      if (v.contains(entry)) {
        v.remove(entry);
      }
    }

    if (
      (applyDisplayUpdateColumn != null)
        && applyDisplayUpdateColumn.equals(entry.getColumnName())) {
      applyDisplayFilters(entry.getColumnName());
    }
  }

  private void addGlobalDisplayFilter(String level) {
    if (selectedColumn != null) {
      Set set = (Set) columnEntryMap.get(selectedColumn);
      Vector v = (Vector) columnDisplayMap.get(selectedColumn);
      Iterator iter = set.iterator();

      while (iter.hasNext()) {
        String thisValue = (String) iter.next();
        DisplayFilterEntry entry =
          new DisplayFilterEntry(
            selectedColumn, thisValue, level);

        if (!v.contains(entry)) {
          addDisplayFilter(entry);
        }
      }
    }
  }

  private void removeGlobalDisplayFilter(String level) {
    if (selectedColumn != null) {
      Set set = (Set) columnEntryMap.get(selectedColumn);
      Vector v = (Vector) columnDisplayMap.get(selectedColumn);
      Iterator iter = set.iterator();

      while (iter.hasNext()) {
        String thisValue = (String) iter.next();
        DisplayFilterEntry entry =
          new DisplayFilterEntry(
            selectedColumn, thisValue, level);

        if (v.contains(entry)) {
          removeDisplayFilter(entry);
        }
      }
    }
  }

  private void syncDisplayCheckBoxes() {
    //if level is selected, disable level map checkbox entries
    //otherwise, enable
    if (selectedColumn != null) {
      Set levelSet = levelMap.entrySet();
      Iterator levelIter = levelSet.iterator();
      boolean enabled = !selectedColumn.equalsIgnoreCase(LEVEL_COL_NAME);

      while (levelIter.hasNext()) {
        Map.Entry m = (Map.Entry) levelIter.next();
        JCheckBox b = (JCheckBox) m.getValue();
        b.setEnabled(enabled);
      }
    }

    int selected = list.getSelectedIndex();

    if (selected > -1) {
      selectedValue = (String) list.getModel().getElementAt(selected);

      if (selectedColumn != null) {
        Vector v = (Vector) columnDisplayMap.get(selectedColumn);

        if (v != null) {
          //examine each level...if the level and value matched an entry, use display value
          Collection c = levelMap.entrySet();
          Iterator iterx = c.iterator();

          while (iterx.hasNext()) {
            boolean selectedFlag = false;
            Map.Entry m = (Map.Entry) iterx.next();
            JCheckBox box = (JCheckBox) m.getValue();
            String level = (String) m.getKey();

            if (selectedColumn.equalsIgnoreCase(LEVEL_COL_NAME)) {
              selectedFlag = false;
            } else {
              //v is list of display entries for this column
              Iterator iter = v.iterator();
              boolean displayFlag = false;

              while (iter.hasNext()) {
                DisplayFilterEntry entry = (DisplayFilterEntry) iter.next();

                if (entry.matches(selectedColumn, selectedValue, level)) {
                  selectedFlag = true;
                }
              }
            }

            box.setSelected(selectedFlag);
          }
        }
      }
    }
  }

  private void syncGlobalDisplayCheckBoxes() {
    //list of all levels as keys mapped to jcheckboxes as values
    if (selectedColumn != null) {
      Collection levels = globalLevelMap.entrySet();

      //list of all displayfilter entries
      Vector displayFilterVector =
        (Vector) columnDisplayMap.get(selectedColumn);
      updateButtonStyle();

      //list of all values for this column
      HashSet valueSet = (HashSet) columnEntryMap.get(selectedColumn);

      //examine all levels
      //examine all values for the column
      //find any displayfilterentries which apply to this column and value
      //if all of the displayfilterentries for the value contain the level,
      //set the checkbox to selected
      HashMap levelMap = new HashMap();
      Iterator levelIterator = levels.iterator();

      while (levelIterator.hasNext()) {
        Map.Entry levelEntry = (Map.Entry) levelIterator.next();
        JCheckBox box = (JCheckBox) (levelEntry.getValue());
        String currentLevel = (String) levelEntry.getKey();
        boolean selected = false;

        if (selectedColumn.equalsIgnoreCase(LEVEL_COL_NAME)) {
          DisplayFilterEntry entry =
            new DisplayFilterEntry(
              selectedColumn, currentLevel, currentLevel);

          if (((Vector) columnDisplayMap.get(selectedColumn)).contains(entry)) {
            selected = true;
          }
        } else {
          Iterator valueIterator = valueSet.iterator();
          int foundCount = 0;

          while (valueIterator.hasNext()) {
            String value = (String) valueIterator.next();
            Iterator displayFilterIterator = displayFilterVector.iterator();

            while (displayFilterIterator.hasNext()) {
              DisplayFilterEntry thisEntry =
                (DisplayFilterEntry) displayFilterIterator.next();

              if (thisEntry.getColumnValue().equals(value)) {
                if (thisEntry.getLevel().equals(currentLevel)) {
                  foundCount++;
                }
              }
            }
          }

          selected = (foundCount == valueSet.size());
        }

        box.setSelected(selected);
      }
    }
  }

  public void applyDisplayUpdateForColumn(String column) {
    applyDisplayUpdateColumn = column;

    JRadioButton button = (JRadioButton) selectedDisplayMap.get(column);
    button.setSelected(true);
  }

  class ColoredCellRenderer extends JLabel implements ListCellRenderer {
    public ColoredCellRenderer() {
      setOpaque(true);
    }

    public Component getListCellRendererComponent(
      JList list, Object value, int index, boolean isSelected,
      boolean cellHasFocus) {
      String text = null;

      if (value != null) {
        text = value.toString();
      }

      Color color = null;
      Border border = null;

      if (getColor(selectedColumn, (String) value) != null) {
        color = getColor(selectedColumn, (String) value);
      } else {
        color = Color.white;
      }

      if (isSelected) {
        border = BorderFactory.createEtchedBorder();
      }

      int style = Font.PLAIN;

      if (filterExists(selectedColumn, (String) value)) {
        style = Font.BOLD;
      }

      setIcon(new SelectedIcon(isSelected));
      setFont(getFont().deriveFont(style));
      setText(text);
      setBorder(border);
      setBackground(color);

      return this;
    }
  }

  class SelectedIcon implements Icon {
    private boolean isSelected;
    private int width = 9;
    private int height = 18;
    private int[] xPoints = new int[4];
    private int[] yPoints = new int[4];

    public SelectedIcon(boolean isSelected) {
      this.isSelected = isSelected;
      xPoints[0] = 0;
      yPoints[0] = -1;
      xPoints[1] = 0;
      yPoints[1] = height;
      xPoints[2] = width;
      yPoints[2] = height / 2;
      xPoints[3] = width;
      yPoints[3] = (height / 2) - 1;
    }

    public int getIconHeight() {
      return height;
    }

    public int getIconWidth() {
      return width;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
      if (isSelected) {
        int length = xPoints.length;
        int[] newXPoints = new int[length];
        int[] newYPoints = new int[length];

        for (int i = 0; i < length; i++) {
          newXPoints[i] = xPoints[i] + x;
          newYPoints[i] = yPoints[i] + y;
        }

        g.setColor(Color.black);

        g.fillPolygon(newXPoints, newYPoints, length);
      }
    }
  }
}
