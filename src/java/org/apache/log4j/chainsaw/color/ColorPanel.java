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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.Box;
import javax.swing.BoxLayout;
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
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;

import org.apache.log4j.chainsaw.SmallButton;
import org.apache.log4j.chainsaw.filter.FilterModel;
import org.apache.log4j.chainsaw.icons.ChainsawIcons;
import org.apache.log4j.chainsaw.rule.ColorRule;
import org.apache.log4j.chainsaw.rule.EqualsRule;
import org.apache.log4j.chainsaw.rule.ExpressionRule;
import org.apache.log4j.chainsaw.rule.ExpressionRuleContext;
import org.apache.log4j.chainsaw.rule.OrRule;
import org.apache.log4j.chainsaw.rule.Rule;


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
  private ColorRuleModel tableModel; 
  private JScrollPane tableScrollPane; 
  private JTextField expression;
  private JTable table;
  private JColorChooser chooser;
  private JButton addUpdateButton; 
  private final String ADD_TEXT = "Add";
  private final String UPDATE_TEXT = "Update";
  private Color defaultBackground;
  private Color defaultForeground;
  private ActionListener closeListener;
  private ExpressionEditor editor;
  private JLabel statusBar;
  
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
  
  public ColorPanel(
    final RuleColorizer colorizer, final FilterModel filterModel) {
    super(new BorderLayout());
    
    this.colorizer = colorizer;
    this.filterModel = filterModel;
    
    chooser = new JColorChooser();
    JLabel l = new JLabel();
    chooser.setPreviewPanel(l);

    tableModel = new ColorRuleModel();
    table = new JTable(tableModel);
    
    table.setToolTipText("Click to edit");
    table.setRowHeight(20);
    table.setSurrendersFocusOnKeystroke(true);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    editor = new ExpressionEditor();
    table.getColumnModel().getColumn(0).setCellEditor(editor);
    table.setPreferredScrollableViewportSize(new Dimension(400,200));
    tableScrollPane = new JScrollPane(table);

    //apply a set of defaults for now, (eventually color rules will be loaded from disk)
    tableModel.add(
      new ColorRuleHolder(
        "level == FATAL || level == ERROR",
        new ColorRule(OrRule.getRule(EqualsRule.getRule("level", "FATAL"), EqualsRule.getRule("level", "ERROR"))
          , new Color(147, 22, 0),
          Color.white)));
    tableModel.add(
      new ColorRuleHolder(
        "level == WARN",
        new ColorRule(
          EqualsRule.getRule("level", "WARN"), Color.yellow.brighter())));
    applyRules("default", tableModel);

    ruleSetsPanel = buildRuleSetsPanel();
    ruleSetsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    JPanel chooserPanel = buildColorChooserPanel();

    JTextField field = new JTextField();
    defaultBackground = field.getBackground();
    defaultForeground = field.getForeground();

    rulesPanel = buildRulesPanel();
    rulesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

    JPanel rightPanel = new JPanel(new BorderLayout());

    JPanel rightOuterPanel = new JPanel();
    rightOuterPanel.setLayout(new BoxLayout(rightOuterPanel, BoxLayout.X_AXIS));
    rightOuterPanel.add(Box.createHorizontalStrut(10));

    JPanel southPanel = new JPanel();
    southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
    southPanel.add(chooserPanel);
    southPanel.add(Box.createVerticalStrut(10));
    JPanel closePanel = buildClosePanel();
    southPanel.add(closePanel);
    statusBar = new JLabel("Ruleset support not yet implemented");
    JPanel statusPanel = new JPanel();
    statusPanel.add(statusBar);
    southPanel.add(statusPanel);

    rightPanel.add(rulesPanel, BorderLayout.CENTER);
    rightPanel.add(southPanel, BorderLayout.SOUTH);    
    rightOuterPanel.add(rightPanel);
    
    add(ruleSetsPanel, BorderLayout.WEST);
    add(rightOuterPanel, BorderLayout.CENTER);
  }

  public void setCloseActionListener(ActionListener listener) {
    closeListener = listener;
  }

  public void hidePanel() {
    if (closeListener != null) {
      closeListener.actionPerformed(null);
    }
  }

  void applyRules(String ruleSet, ColorRuleModel tableModel) {
    colorizer.clear();
    Iterator iter = tableModel.getColorRuleHolders().iterator();
    List list = new ArrayList();
    while (iter.hasNext()) {
        ColorRuleHolder holder = (ColorRuleHolder)iter.next();
        list.add(holder.colorRule);
    }
    colorizer.addRules(list);
  }

  JPanel buildClosePanel() {  
      JPanel panel = new JPanel();
      panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
      panel.add(Box.createHorizontalGlue());
      JButton applyButton = new JButton("Apply");

      applyButton.addActionListener(new AbstractAction() {
        public void actionPerformed(ActionEvent evt) {
            editor.stopCellEditing();
            applyRules("default", tableModel);
            hidePanel();
        }
      });
      
      panel.add(applyButton);
      return panel;   
  }
  JPanel buildColorChooserPanel() {
      JPanel panel = new JPanel(new BorderLayout());
      
         chooser.getSelectionModel().addChangeListener(
           new ChangeListener() {
             public void stateChanged(ChangeEvent evt) {
               int row = table.getSelectedRow();
               if (row > -1) {
                    ColorRuleHolder holder = (ColorRuleHolder)tableModel.get(row);                 
                    holder.setBackgroundColor(chooser.getColor());
                    tableModel.fireTableRowsUpdated(row, row);
                    table.repaint();
               }
             }
           });

         panel.add(chooser, BorderLayout.CENTER);
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
              if (!editor.isValid()) {
                  int editingRow = editor.getEditingRow();
                  table.editCellAt(editingRow, 0);
                  if (table.getEditorComponent() != null) {
                    table.getEditorComponent().requestFocus();
                  }
                  return;
              }
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
              ColorRuleHolder holder = tableModel.get(index);
              editor.stopCellEditing();
              tableModel.remove(index);
              index = index - 1;              
              tableModel.insert(index, holder);
              table.editCellAt(index, 0);
              table.getSelectionModel().setSelectionInterval(index, index);
              if (table.getEditorComponent() != null) {
                table.getEditorComponent().requestFocus();
              }
            }
          }
        });

      downButton.addActionListener(
        new AbstractAction() {
          public void actionPerformed(ActionEvent evt) {
            int index = table.getSelectionModel().getMaxSelectionIndex();

            if ((index > -1) && (index < (tableModel.getRowCount() - 1))) {
              ColorRuleHolder holder = tableModel.get(index);
              editor.stopCellEditing();
              tableModel.remove(index);
              index = index + 1;
              tableModel.insert(index, holder);
              table.editCellAt(index, 0);
              table.getSelectionModel().setSelectionInterval(index, index);
              if (table.getEditorComponent() != null) {
                table.getEditorComponent().requestFocus();
              }
            }
          }
        });

      table.setDefaultRenderer(Object.class, new ColorRenderer());

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
      newButton.addActionListener(new AbstractAction() {
          public void actionPerformed(ActionEvent evt) {
              ColorRuleHolder newRule = new ColorRuleHolder();
              int currentRow = table.getSelectedRow();
              editor.stopCellEditing();
              if (currentRow < 0) {
                  tableModel.add(newRule);
                  currentRow = table.getRowCount() - 1;
              } else {
                tableModel.insert(currentRow, newRule);
              }
              table.getSelectionModel().setSelectionInterval(currentRow, currentRow);
              table.editCellAt(currentRow, 0);
              if (table.getEditorComponent() != null) {
                table.getEditorComponent().requestFocus();
              }
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
            if ((!editor.isValid()) && (editor.getEditingRow() == index)) {
              editor.cancelCellEditing();
            }
            editor.stopCellEditing();
            if (index > -1 && index < table.getRowCount()) {
              ColorRuleHolder holder = tableModel.get(index);

              tableModel.remove(index);

              if (index > 0) {
                index = index - 1;
              }

              if (tableModel.getRowCount() > 0) {
                table.getSelectionModel().setSelectionInterval(index, index);
                table.editCellAt(index, 0);
                if (table.getEditorComponent() != null) {
                  table.getEditorComponent().requestFocus();
                }
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
  
  class ColorRenderer extends DefaultTableCellRenderer {
    public Component getTableCellRendererComponent(JTable table, Object value,
    boolean isSelected, boolean hasFocus, int row, int column) {
      ColorRuleHolder h = ((ColorRuleModel)table.getModel()).get(row);
      Component c =
        super.getTableCellRendererComponent(
          table, value, isSelected, hasFocus, row, column);
      c.setBackground(h.getBackgroundColor());
      c.setForeground(h.getForegroundColor());

      return c;    }
  }

  class ColorRuleModel extends AbstractTableModel {
      ArrayList list = new ArrayList();
    
      public int getColumnCount() {
          return 1;
      }
      
      public List getColorRuleHolders() {
          return list;
      }
    
      public ColorRuleHolder get(int rowIndex) {
          return (ColorRuleHolder)list.get(rowIndex);
      }
    
      public void remove(int rowIndex) {
          list.remove(rowIndex);
          fireTableDataChanged();
      }
    
      public void insert(int index, ColorRuleHolder holder) {
          list.add(index, holder);
          fireTableDataChanged();
      }
    
      public void add(ColorRuleHolder holder) {
          list.add(holder);
          fireTableDataChanged();
      }
    
      public int indexOf(ColorRuleHolder holder) {
          return list.indexOf(holder);
      }

      public int getRowCount() {
          return list.size();
      }

      public boolean isCellEditable(int rowIndex, int columnIndex) {
          return true;
      }

      public Class getColumnClass(int c) {
          return Object.class;
      }
              
      public Object getValueAt(int rowIndex, int columnIndex) {
          if (columnIndex == 0) {
              return ((ColorRuleHolder)list.get(rowIndex)).getRuleText();
          }
          return null;
      }

      public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
          if (columnIndex == 0) {
              if (rowIndex < list.size()) {
                ((ColorRuleHolder)list.get(rowIndex)).setRule(aValue.toString());
                statusBar.setText("");
              }
          }
          fireTableDataChanged();
          table.repaint();
      }

      public String getColumnName(int columnIndex) {
          return null;
      }
  }

  class ColorRuleHolder {
    String ruleText;
    ColorRule colorRule;

    ColorRuleHolder() {
        ruleText="";
    }
        
    ColorRuleHolder(String ruleText, ColorRule colorRule) {
      this.ruleText = ruleText;
      this.colorRule = colorRule;
    }
    
    ColorRuleHolder(String ruleText) {
        this.ruleText = ruleText;
        this.colorRule = new ColorRule(ExpressionRule.getRule(ruleText), defaultBackground, defaultForeground);
    }
    
    public Color getBackgroundColor() {
        if (colorRule != null) {
            return colorRule.getBackgroundColor();
        } else {
            return defaultBackground;
        }
    }
    
    public void setBackgroundColor(Color color) {
        Rule originalRule = null;
        if (colorRule != null) {
            originalRule = colorRule.getRule();
        } 
        colorRule = new ColorRule(originalRule, color); 
    }

    public void setForegroundColor(Color color) {
        Rule originalRule = null;
        Color background = null;
        if (colorRule != null) {
            originalRule = colorRule.getRule();
            background = colorRule.getBackgroundColor();
        } 

        colorRule = new ColorRule(originalRule, background, color); 
    }

    public Color getForegroundColor() {
        if (colorRule != null) {
            return colorRule.getForegroundColor();
        } else {
            return defaultForeground;
        }
    }
    
    public String toString() {
      return ruleText + ".." + colorRule;
    }
  
    public String getRuleText() {
        return ruleText;
    }
    
    public void setRule(String ruleText) {
        try {
          Color background = null;
          Color foreground = null;
          if (colorRule != null) {
            background = colorRule.getBackgroundColor();
            foreground = colorRule.getForegroundColor();              
          }
          colorRule = new ColorRule(ExpressionRule.getRule(ruleText), background, foreground);
          this.ruleText = ruleText;
        } catch (IllegalArgumentException iae) {
            statusBar.setText("Invalid expression: " + ruleText);
            throw iae;
        }
    } 

    public int hashCode() {
      int result = 37;

      if (ruleText != null) {
        result = result + (37 * ruleText.hashCode());
      }

      return result;
    }

    //note: in order to perform 'contains' checks, we are only evaluating the string for equality,
    //not the colorrule.  This allows us to 'update' an existing rule with a new color.
    public boolean equals(Object o) {
      if (o instanceof ColorRuleHolder) {
        ColorRuleHolder h = (ColorRuleHolder) o;

        return (((ruleText == null) && (h.ruleText == null))
        || ((ruleText != null) && ruleText.equals(h.ruleText)));
      }

      return false;
    }
  }
  
  class ColorEditor extends AbstractCellEditor
                           implements TableCellEditor,
                                      ActionListener {
      JComboBox box;
      
      SmallButton button = new SmallButton();
      Color currentColor;
      JColorChooser colorChooser;
      JDialog dialog;
      JTextField textField = new JTextField();
      JPanel panel = new JPanel(new BorderLayout());

      public ColorEditor() {
          textField.addKeyListener(new ExpressionRuleContext(filterModel, textField));
          //Set up the dialog that the button brings up.
          colorChooser = new JColorChooser();
          panel.add(textField, BorderLayout.CENTER);
          panel.add(button, BorderLayout.EAST);
          dialog = JColorChooser.createDialog(textField,
                                          "Pick a Color",
                                          true,  //modal
                                          colorChooser,
                                          this,  //OK button handler
                                          null); //no CANCEL button handler
          button.setAction(new AbstractAction("...") {
              public void actionPerformed(ActionEvent e) {
                      ColorRuleHolder holder = (ColorRuleHolder)tableModel.get(table.getSelectedRow());
                      currentColor = holder.getBackgroundColor();
                      colorChooser.setColor(currentColor);
                      dialog.setVisible(true);

                      fireEditingStopped(); //Make the renderer reappear.
              }
          });

      }

      public void actionPerformed(ActionEvent e) {
              int row = table.getSelectedRow();
              if (row > -1) {
                   ColorRuleHolder holder = (ColorRuleHolder)tableModel.get(row);                 
                   holder.setBackgroundColor(colorChooser.getColor());
                   tableModel.fireTableRowsUpdated(row, row);
                   table.repaint();
              }
      }

      //Implement the one CellEditor method that AbstractCellEditor doesn't.
      public Object getCellEditorValue() {
          return textField.getText();
      }

      //Implement the one method defined by TableCellEditor.
      public Component getTableCellEditorComponent(JTable table,
                                                   Object value,
                                                   boolean isSelected,
                                                   int row,
                                                   int column) {
         ColorRuleHolder holder = (ColorRuleHolder)tableModel.get(row);
         textField.setText(value.toString());
         textField.setBackground(holder.getBackgroundColor());            

          currentColor = holder.getBackgroundColor();
          return panel;
      }
  }
  
  class ExpressionEditor extends AbstractCellEditor
  implements TableCellEditor {
      JTextField textField = new JTextField();
      ColorRuleHolder currentHolder = null;
      boolean isValid = true;
      int editingRow = -1;
      public ExpressionEditor() {
          textField.addKeyListener(new ExpressionRuleContext(filterModel, textField));
          chooser.getSelectionModel().addChangeListener(
            new ChangeListener() {
              public void stateChanged(ChangeEvent evt) {
                int row = table.getSelectedRow();
                if (row > -1) {
                     currentHolder = (ColorRuleHolder)tableModel.get(row);                 
                     textField.setBackground(chooser.getColor());
                     currentHolder.setBackgroundColor(chooser.getColor());
                }
              }
            });
      }
      
    public int getEditingRow() {
        return editingRow;
    }
    
    public boolean stopCellEditing() {
     try {
         if (currentHolder != null) {
            currentHolder.setRule(textField.getText());
         }
         isValid = true;
     } catch (IllegalArgumentException iae) {
         isValid = false;
         return false;
     }
     return super.stopCellEditing();   
    }
      
    public void cancelCellEditing() {
       isValid = true;
       editingRow = -1;
       super.cancelCellEditing();
    }

    public String getExpression() {
        return textField.getText();
    } 
    
    public boolean isValid() {
        return isValid;
    }
    
    public ColorRuleHolder getCurrentHolder() {
        return currentHolder;
    }

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (value != null) {
            editingRow = row;
            currentHolder = (ColorRuleHolder)tableModel.get(row);            
            textField.setText(currentHolder.getRuleText());
            textField.setBackground(currentHolder.getBackgroundColor());
            textField.setForeground(currentHolder.getForegroundColor());
            textField.setToolTipText("Press ctrl-space or right click for context menu");
        }
        return textField;
	}

	public Object getCellEditorValue() {
		return textField.getText();
	}
  }
}

