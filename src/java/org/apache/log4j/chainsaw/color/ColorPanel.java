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

package org.apache.log4j.chainsaw.color;

import org.apache.log4j.chainsaw.filter.FilterModel;
import org.apache.log4j.chainsaw.icons.ChainsawIcons;
import org.apache.log4j.chainsaw.rule.ColorRule;
import org.apache.log4j.chainsaw.rule.ExpressionRule;
import org.apache.log4j.chainsaw.rule.ExpressionRuleContext;
import org.apache.log4j.chainsaw.rule.Rule;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;


/**
 * Panel which updates a RuleColorizer, allowing the user to build expression-based
 * color rules.
 *
 * @author Scott Deboy <sdeboy@apache.org>
 */
public class ColorPanel extends JPanel {
  private RuleColorizer colorizer;
  private JPanel ruleSetsPanel;
  private JPanel rulesPanel;
  private JPanel ruleSettingsPanel;
  private FilterModel filterModel;
  private DefaultTableModel tableModel;
  private JScrollPane tableScrollPane;
  private JTable table;
  private ActionListener closeListener;
  private JLabel statusBar;

  public ColorPanel(
    final RuleColorizer colorizer, final FilterModel filterModel) {
    super(new BorderLayout());

    this.colorizer = colorizer;
    this.filterModel = filterModel;

    //apply a set of defaults for now, (eventually color rules will be loaded from disk)
    Vector data1 = new Vector();
    data1.add("level == FATAL || level == ERROR");
    data1.add(new Color(147, 22, 0));
    data1.add(Color.white);

    Vector data2 = new Vector();
    data2.add("level == WARN");
    data2.add(Color.yellow.brighter());
    data2.add(Color.black);

    Vector data = new Vector();
    data.add(data1);
    data.add(data2);

    table = buildTable(data);
    statusBar = new JLabel("Ruleset support not yet implemented");

    applyRules("default");

    table.setToolTipText("Click to edit");
    table.setRowHeight(20);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setColumnSelectionAllowed(false);

    table.setPreferredScrollableViewportSize(new Dimension(400, 200));
    tableScrollPane = new JScrollPane(table);

    ruleSetsPanel = buildRuleSetsPanel();
    ruleSetsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

    rulesPanel = buildRulesPanel();
    rulesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

    JPanel rightPanel = new JPanel(new BorderLayout());

    JPanel rightOuterPanel = new JPanel();
    rightOuterPanel.setLayout(
      new BoxLayout(rightOuterPanel, BoxLayout.X_AXIS));
    rightOuterPanel.add(Box.createHorizontalStrut(10));

    JPanel southPanel = new JPanel();
    southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
    southPanel.add(Box.createVerticalStrut(10));

    JPanel closePanel = buildClosePanel();
    southPanel.add(closePanel);

    JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    statusPanel.add(statusBar);
    southPanel.add(statusPanel);

    rightPanel.add(rulesPanel, BorderLayout.CENTER);
    rightPanel.add(southPanel, BorderLayout.SOUTH);
    rightOuterPanel.add(rightPanel);

    add(ruleSetsPanel, BorderLayout.WEST);
    add(rightOuterPanel, BorderLayout.CENTER);
  }

  public static void main(String[] args) {
    FilterModel filterModel = new FilterModel();
    RuleColorizer colorizer = new RuleColorizer();

    ColorPanel p = new ColorPanel(colorizer, filterModel);
    final JFrame f = new JFrame();

    p.setCloseActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          System.exit(0);
        }
      });

    f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    f.getContentPane().add(p);
    f.pack();
    f.setVisible(true);
  }

  private Vector getDefaultColors() {
    Vector vec = new Vector();

    vec.add(Color.white);
    vec.add(Color.black);

    vec.add(new Color(0, 153, 0));
    vec.add(new Color(0, 204, 51));
    vec.add(new Color(153, 255, 153));
    vec.add(new Color(51, 255, 0));
    vec.add(new Color(204, 255, 204));
    vec.add(new Color(0, 153, 153));
    vec.add(new Color(0, 204, 204));
    vec.add(new Color(153, 102, 0));
    vec.add(new Color(102, 102, 0));
    vec.add(new Color(153, 153, 0));
    vec.add(new Color(204, 204, 0));
    vec.add(new Color(255, 255, 0));
    vec.add(new Color(255, 255, 204));
    vec.add(new Color(255, 153, 51));
    vec.add(new Color(204, 0, 51));
    vec.add(new Color(255, 51, 51));
    vec.add(new Color(255, 153, 153));
    vec.add(new Color(255, 204, 204));
    vec.add(new Color(204, 0, 204));
    vec.add(new Color(255, 51, 255));
    vec.add(new Color(153, 51, 255));
    vec.add(new Color(0, 0, 153));
    vec.add(new Color(0, 0, 255));
    vec.add(new Color(51, 153, 255));
    vec.add(new Color(153, 153, 255));
    vec.add(new Color(204, 204, 255));
    vec.add(new Color(102, 255, 255));
    vec.add(new Color(102, 102, 102));
    vec.add(new Color(153, 153, 153));
    vec.add(new Color(204, 204, 204));

    return vec;
  }

  private JTable buildTable(Vector data) {
    Vector backgroundColors = getDefaultColors();
    Vector foregroundColors = getDefaultColors();
    backgroundColors.add("Browse...");
    foregroundColors.add("Browse...");

    JComboBox background = new JComboBox(backgroundColors);
    background.setMaximumRowCount(15);
    background.setRenderer(new ColorListCellRenderer());

    JComboBox foreground = new JComboBox(foregroundColors);
    foreground.setMaximumRowCount(15);
    foreground.setRenderer(new ColorListCellRenderer());

    Vector cols = new Vector();
    cols.add("Expression");
    cols.add("Background");
    cols.add("Foreground");

    tableModel = new DefaultTableModel(data, cols);

    JTable table = new JTable(tableModel);

    DefaultCellEditor backgroundEditor = new DefaultCellEditor(background);
    DefaultCellEditor foregroundEditor = new DefaultCellEditor(foreground);
    JTextField textField = new JTextField();
    textField.addKeyListener(
      new ExpressionRuleContext(filterModel, textField));
    table.getColumnModel().getColumn(0).setCellEditor(
      new DefaultCellEditor(textField));
    table.getColumnModel().getColumn(1).setCellEditor(backgroundEditor);
    table.getColumnModel().getColumn(2).setCellEditor(foregroundEditor);

    background.addItemListener(new ColorItemListener(background));
    foreground.addItemListener(new ColorItemListener(foreground));

    table.getColumnModel().getColumn(0).setCellRenderer(
      new ExpressionTableCellRenderer());
    table.getColumnModel().getColumn(1).setCellRenderer(
      new ColorTableCellRenderer());
    table.getColumnModel().getColumn(2).setCellRenderer(
      new ColorTableCellRenderer());

    return table;
  }

  public void setCloseActionListener(ActionListener listener) {
    closeListener = listener;
  }

  public void hidePanel() {
    if (closeListener != null) {
      closeListener.actionPerformed(null);
    }
  }

  void applyRules(String ruleSet) {
    table.getColumnModel().getColumn(0).getCellEditor().stopCellEditing();
    colorizer.clear();

    List list = new ArrayList();
    Vector vector = tableModel.getDataVector();
    StringBuffer result = new StringBuffer();

    for (int i = 0; i < vector.size(); i++) {
      Vector v = (Vector) vector.elementAt(i);

      try {
        Rule expressionRule = ExpressionRule.getRule((String) v.elementAt(0));
        Color background = getBackground();
        Color foreground = getForeground();

        if (v.elementAt(1) instanceof Color) {
          background = (Color) v.elementAt(1);
        }

        if (v.elementAt(2) instanceof Color) {
          foreground = (Color) v.elementAt(2);
        }

        ColorRule r = new ColorRule(expressionRule, background, foreground);
        list.add(r);
      } catch (IllegalArgumentException iae) {
        if (!result.toString().equals("")) {
          result.append("<br>");
        }

        result.append(iae.getMessage());
      }
    }

    if (result.toString().equals("")) {
      ((JLabel) table.getColumnModel().getColumn(0).getCellRenderer())
      .setToolTipText("Click to edit");
      statusBar.setText("");
    } else {
      statusBar.setText("Errors - see expression tooltip");
      ((JLabel) table.getColumnModel().getColumn(0).getCellRenderer())
      .setToolTipText("<html>" + result.toString() + "</html>");
    }

    colorizer.addRules(list);
  }

  JPanel buildClosePanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
    panel.add(Box.createHorizontalGlue());

    JButton applyButton = new JButton("Apply");

    applyButton.addActionListener(
      new AbstractAction() {
        public void actionPerformed(ActionEvent evt) {
          applyRules("default");
        }
      });

    panel.add(applyButton);

    JButton closeButton = new JButton("Close");

    closeButton.addActionListener(
      new AbstractAction() {
        public void actionPerformed(ActionEvent evt) {
          hidePanel();
        }
      });
    panel.add(Box.createHorizontalStrut(10));
    panel.add(closeButton);

    return panel;
  }

  JPanel buildUpDownPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    JPanel innerPanel = new JPanel();
    innerPanel.setLayout(new GridLayout(5, 1));

    final JButton upButton = new JButton(ChainsawIcons.ICON_UP);
    upButton.setToolTipText("Move selected rule up");

    final JButton downButton = new JButton(ChainsawIcons.ICON_DOWN);
    downButton.setToolTipText("Move selected rule down");
    upButton.setEnabled(false);
    downButton.setEnabled(false);

    table.getSelectionModel().addListSelectionListener(
      new ListSelectionListener() {
        public void valueChanged(ListSelectionEvent e) {
          if (!e.getValueIsAdjusting()) {
            int index = table.getSelectionModel().getMaxSelectionIndex();

            if (index < 0) {
              downButton.setEnabled(false);
              upButton.setEnabled(false);
            } else if ((index == 0) && (tableModel.getRowCount() == 1)) {
              downButton.setEnabled(false);
              upButton.setEnabled(false);
            } else if ((index == 0) && (tableModel.getRowCount() > 1)) {
              downButton.setEnabled(true);
              upButton.setEnabled(false);
            } else if (index == (tableModel.getRowCount() - 1)) {
              downButton.setEnabled(false);
              upButton.setEnabled(true);
            } else {
              downButton.setEnabled(true);
              upButton.setEnabled(true);
            }
          }
        }
      });

    JPanel upPanel = new JPanel();

    upPanel.add(upButton);

    JPanel downPanel = new JPanel();
    downPanel.add(downButton);

    innerPanel.add(new JLabel(""));
    innerPanel.add(upPanel);
    innerPanel.add(new JLabel(""));
    innerPanel.add(downPanel);
    panel.add(innerPanel, BorderLayout.CENTER);

    upButton.addActionListener(
      new AbstractAction() {
        public void actionPerformed(ActionEvent evt) {
          int index = table.getSelectionModel().getMaxSelectionIndex();

          if (index > 0) {
            Vector v = tableModel.getDataVector();
            Vector row = (Vector) v.elementAt(index);
            tableModel.removeRow(index);
            index = index - 1;
            tableModel.insertRow(index, row);
            table.getSelectionModel().setSelectionInterval(index, index);
          }
        }
      });

    downButton.addActionListener(
      new AbstractAction() {
        public void actionPerformed(ActionEvent evt) {
          int index = table.getSelectionModel().getMaxSelectionIndex();

          if ((index > -1) && (index < (tableModel.getRowCount() - 1))) {
            Vector v = tableModel.getDataVector();
            Vector row = (Vector) v.elementAt(index);

            tableModel.removeRow(index);
            index = index + 1;
            tableModel.insertRow(index, row);
            table.getSelectionModel().setSelectionInterval(index, index);
          }
        }
      });

    return panel;
  }

  JPanel buildRuleSetsPanel() {
    JPanel panel = new JPanel(new BorderLayout());

    DefaultListModel listModel = new DefaultListModel();
    listModel.addElement("Default");

    JLabel ruleSetLabel = new JLabel("RuleSets:");
    panel.add(ruleSetLabel, BorderLayout.NORTH);

    final JList list = new JList(listModel);
    JScrollPane scrollPane = new JScrollPane(list);
    list.setEnabled(false);

    panel.add(scrollPane, BorderLayout.CENTER);

    JPanel buttonPanel = new JPanel(new GridLayout(0, 2));

    JPanel newPanel = new JPanel();
    JButton newButton = new JButton("New");
    newButton.setEnabled(false);
    newPanel.add(newButton);

    JPanel deletePanel = new JPanel();
    JButton deleteButton = new JButton("Delete");
    deleteButton.setEnabled(false);
    deletePanel.add(deleteButton);

    buttonPanel.add(newPanel);
    buttonPanel.add(deletePanel);

    panel.add(buttonPanel, BorderLayout.SOUTH);

    return panel;
  }

  JPanel buildRulesPanel() {
    JPanel listPanel = new JPanel(new BorderLayout());
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    JLabel ruleSetLabel = new JLabel("RuleSet Name:");
    panel.add(ruleSetLabel);

    JTextField ruleSetTextField = new JTextField(20);
    ruleSetTextField.setText("Default");
    ruleSetTextField.setAlignmentX(Component.LEFT_ALIGNMENT);
    ruleSetTextField.setEnabled(false);

    panel.add(ruleSetTextField);

    panel.add(Box.createVerticalStrut(10));

    JLabel rulesLabel = new JLabel("Rules:");

    panel.add(rulesLabel);

    JPanel buttonPanel = new JPanel(new GridLayout(0, 2));
    buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

    JPanel newPanel = new JPanel();
    JButton newButton = new JButton("New");
    newButton.addActionListener(
      new AbstractAction() {
        public void actionPerformed(ActionEvent evt) {
          int currentRow = table.getSelectedRow();
          Vector v = new Vector();
          v.add("");
          v.add(Color.white);
          v.add(Color.black);

          if (currentRow < 0) {
            tableModel.addRow(v);
            currentRow = table.getRowCount() - 1;
          } else {
            tableModel.insertRow(currentRow, v);
          }

          table.getSelectionModel().setSelectionInterval(
            currentRow, currentRow);
        }
      });

    newPanel.add(newButton);

    JPanel deletePanel = new JPanel();
    final JButton deleteButton = new JButton("Delete");
    deleteButton.setEnabled(false);

    deleteButton.addActionListener(
      new AbstractAction() {
        public void actionPerformed(ActionEvent evt) {
          int index = table.getSelectionModel().getMaxSelectionIndex();

          if ((index > -1) && (index < table.getRowCount())) {
            Vector v = tableModel.getDataVector();
            Vector row = (Vector) v.elementAt(index);

            tableModel.removeRow(index);

            if (index > 0) {
              index = index - 1;
            }

            if (tableModel.getRowCount() > 0) {
              table.getSelectionModel().setSelectionInterval(index, index);
            }
          }
        }
      });

    table.getSelectionModel().addListSelectionListener(
      new ListSelectionListener() {
        public void valueChanged(ListSelectionEvent e) {
          if (!e.getValueIsAdjusting()) {
            int index = table.getSelectionModel().getMaxSelectionIndex();

            if (index < 0) {
              deleteButton.setEnabled(false);
            } else {
              deleteButton.setEnabled(true);
            }
          }
        }
      });

    deletePanel.add(deleteButton);

    buttonPanel.add(newPanel);
    buttonPanel.add(deletePanel);

    listPanel.add(panel, BorderLayout.NORTH);

    JPanel tablePanel = new JPanel(new BorderLayout());
    tablePanel.add(tableScrollPane, BorderLayout.CENTER);
    tablePanel.add(buildUpDownPanel(), BorderLayout.EAST);
    listPanel.add(tablePanel, BorderLayout.CENTER);
    listPanel.add(buttonPanel, BorderLayout.SOUTH);

    return listPanel;
  }

  class ColorListCellRenderer extends JLabel implements ListCellRenderer {
    ColorListCellRenderer() {
      setOpaque(true);
    }

    public Component getListCellRendererComponent(
      JList list, Object value, int index, boolean isSelected,
      boolean cellHasFocus) {
      setText(" ");

      if (isSelected && (index > -1)) {
        setBorder(BorderFactory.createLineBorder(Color.black, 2));
      } else {
        setBorder(BorderFactory.createEmptyBorder());
      }

      if (value instanceof Color) {
        setBackground((Color) value);
      } else {
        setBackground(Color.white);

        if (value != null) {
          setText(value.toString());
        }
      }

      return this;
    }
  }

  class ColorItemListener implements ItemListener {
    JComboBox box;
    JDialog dialog;
    JColorChooser colorChooser;

    ColorItemListener(final JComboBox box) {
      this.box = box;
      colorChooser = new JColorChooser();
      dialog =
        JColorChooser.createDialog(
          box, "Pick a Color", true, //modal
          colorChooser,
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              box.insertItemAt(colorChooser.getColor(), 0);
              box.setSelectedIndex(0);
            }
          }, //OK button handler
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              box.setSelectedIndex(0);
            }
          }); //CANCEL button handler
      dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    }

    public void itemStateChanged(ItemEvent e) {
      if (e.getStateChange() == ItemEvent.SELECTED) {
        if (box.getSelectedItem() instanceof Color) {
          box.setBackground((Color) box.getSelectedItem());
          repaint();
        } else {
          box.setBackground(Color.white);
          dialog.setVisible(true);
        }
      }
    }
  }

  class ColorTableCellRenderer extends JPanel implements TableCellRenderer {
    Border selectedBorder;
    Border unselectedBorder;

    public Component getTableCellRendererComponent(
      JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
      int column) {
      if (value instanceof Color) {
        setBackground((Color) value);
      }

      if (isSelected) {
        if (selectedBorder == null) {
          selectedBorder =
            BorderFactory.createMatteBorder(
              2, 5, 2, 5, table.getSelectionBackground());
        }
        setBorder(selectedBorder);
      } else {
        if (unselectedBorder == null) {
          unselectedBorder =
            BorderFactory.createMatteBorder(2, 5, 2, 5, table.getBackground());
        }
        setBorder(unselectedBorder);
      }

      return this;
    }
  }

  class ExpressionTableCellRenderer extends JLabel implements TableCellRenderer {
    Border selectedBorder;
    Border unselectedBorder;

    ExpressionTableCellRenderer() {
      setOpaque(true);
    }

    public Component getTableCellRendererComponent(
      JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
      int column) {
      Vector v = tableModel.getDataVector();
      Vector r = (Vector) v.elementAt(row);
      setText(value.toString());

      if (r.elementAt(1) instanceof Color) {
        setBackground((Color) r.elementAt(1));
      }

      if (r.elementAt(2) instanceof Color) {
        setForeground((Color) r.elementAt(2));
      }

      if (isSelected) {
        if (selectedBorder == null) {
          selectedBorder =
            BorderFactory.createMatteBorder(
              2, 5, 2, 5, table.getSelectionBackground());
        }
        setBorder(selectedBorder);
      } else {
        if (unselectedBorder == null) {
          unselectedBorder =
            BorderFactory.createMatteBorder(2, 5, 2, 5, table.getBackground());
        }
        setBorder(unselectedBorder);
      }

      return this;
    }
  }
}
