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
import org.apache.log4j.chainsaw.rule.ColorRule;
import org.apache.log4j.chainsaw.rule.EqualsRule;
import org.apache.log4j.chainsaw.rule.ExpressionRule;
import org.apache.log4j.chainsaw.rule.ExpressionRuleContext;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Panel which updates a RuleColorizer, allowing the user to build expression-based
 * color rules.
 * 
 * @author Scott Deboy <sdeboy@apache.org> 
 */
public class ColorPanel extends JPanel {
  private static final String ADD_TEXT = "Add-->";
  private static final String UPDATE_TEXT = "Update-->";
  private final RuleColorizer colorizer;
  final DefaultListModel listModel;
  boolean addMode = true;

  public ColorPanel(
    final RuleColorizer colorizer, final FilterModel filterModel) {
    super();
    setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    this.colorizer = colorizer;

    final JColorChooser chooser = new JColorChooser();

    listModel = new DefaultListModel();

    final JList list = new JList(listModel);
    final JScrollPane scrollPane = new JScrollPane(list);
    scrollPane.setPreferredSize(new Dimension(400, 400));

    //apply a set of defaults for now, (eventually color rules will be loaded from disk)
    listModel.addElement(
      new ColorRuleHolder(
        "level == FATAL",
        new ColorRule(
          EqualsRule.getRule("level", "FATAL"), new Color(147, 22, 0),
          Color.white)));
    listModel.addElement(
      new ColorRuleHolder(
        "level == ERROR",
        new ColorRule(
          EqualsRule.getRule("level", "ERROR"), new Color(147, 22, 0),
          Color.white)));
    listModel.addElement(
      new ColorRuleHolder(
        "level == WARN",
        new ColorRule(
          EqualsRule.getRule("level", "WARN"), Color.yellow.brighter())));
    applyRules();

    JPanel leftPanel = new JPanel(new BorderLayout());
    JPanel leftCenterPanel = new JPanel();
    leftCenterPanel.setLayout(
      new BoxLayout(leftCenterPanel, BoxLayout.Y_AXIS));

    JPanel expressionClearPanel = new JPanel();
    expressionClearPanel.setLayout(
      new BoxLayout(expressionClearPanel, BoxLayout.Y_AXIS));

    JPanel expressionPanel = new JPanel();
    expressionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

    leftPanel.add(new JLabel("Rule:"), BorderLayout.NORTH);

    final JTextField expression = new JTextField(30);
    final Color defaultExpressionBackground = expression.getBackground();
    final Color defaultExpressionForeground = expression.getForeground();

    expression.addKeyListener(
      new ExpressionRuleContext(filterModel, expression));
    expressionPanel.add(expression);

    JPanel addUpdatePanel = new JPanel();
    addUpdatePanel.setLayout(new BoxLayout(addUpdatePanel, BoxLayout.X_AXIS));

    addUpdatePanel.add(new JLabel(" "));

    final JButton addUpdateButton = new JButton(ADD_TEXT);
    addUpdatePanel.add(addUpdateButton);

    expressionPanel.add(addUpdatePanel);

    expressionClearPanel.add(expressionPanel);

    JPanel clearPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

    JButton clearButton = new JButton("Clear");
    clearPanel.add(clearButton);
    clearButton.addActionListener(
      new AbstractAction() {
        public void actionPerformed(ActionEvent evt) {
          addMode = true;
          expression.setText("");
          addUpdateButton.setText(ADD_TEXT);
          expression.setBackground(defaultExpressionBackground);
          expression.setForeground(defaultExpressionForeground);
          list.getSelectionModel().clearSelection();
        }
      });

    expressionClearPanel.add(clearPanel);

    leftCenterPanel.add(expressionClearPanel);

    JPanel chooserPanel = new JPanel();
    chooserPanel.add(chooser);

    chooser.getSelectionModel().addChangeListener(
      new ChangeListener() {
        public void stateChanged(ChangeEvent evt) {
          expression.setBackground(chooser.getColor());
        }
      });

    leftCenterPanel.add(chooserPanel);
    leftPanel.add(leftCenterPanel);
    add(leftPanel);

    JPanel updownPanel = new JPanel();
    updownPanel.setLayout(new GridLayout(6, 1));

    JPanel removePanel = new JPanel();
    final JButton removeButton = new JButton("Remove");

    removeButton.addActionListener(
      new AbstractAction() {
        public void actionPerformed(ActionEvent evt) {
          int index = list.getSelectionModel().getMaxSelectionIndex();

          if (index > -1) {
            ColorRuleHolder holder = (ColorRuleHolder) listModel.get(index);

            listModel.remove(index);

            if (index > 0) {
              index = index - 1;
            }

            addUpdateButton.setText(ADD_TEXT);
            addMode = true;

            if (listModel.getSize() > 0) {
              list.getSelectionModel().setSelectionInterval(index, index);
            }
          }
        }
      });

    removePanel.add(removeButton);

    updownPanel.add(new JLabel(" "));
    updownPanel.add(new JLabel(" "));

    final JButton upButton = new JButton("Move Up");
    final JButton downButton = new JButton("Move Down");
    JPanel upPanel = new JPanel();

    updownPanel.add(removePanel);

    upPanel.add(upButton);

    JPanel downPanel = new JPanel();
    downPanel.add(downButton);

    updownPanel.add(upPanel);

    JPanel updownLabelPanel = new JPanel();
    updownLabelPanel.add(new JLabel("Move rule"));
    updownPanel.add(updownLabelPanel);
    updownPanel.add(downPanel);

    add(updownPanel);

    upButton.addActionListener(
      new AbstractAction() {
        public void actionPerformed(ActionEvent evt) {
          int index = list.getSelectionModel().getMaxSelectionIndex();

          if (index > 0) {
            ColorRuleHolder holder = (ColorRuleHolder) listModel.get(index);
            listModel.remove(index);
            index = index - 1;
            listModel.add(index, holder);
            list.getSelectionModel().setSelectionInterval(index, index);
          }
        }
      });

    downButton.addActionListener(
      new AbstractAction() {
        public void actionPerformed(ActionEvent evt) {
          int index = list.getSelectionModel().getMaxSelectionIndex();

          if ((index > -1) && (index < (listModel.getSize() - 1))) {
            ColorRuleHolder holder = (ColorRuleHolder) listModel.get(index);
            listModel.remove(index);
            index = index + 1;
            listModel.add(index, holder);
            list.getSelectionModel().setSelectionInterval(index, index);
          }
        }
      });

    list.setCellRenderer(new ColorRenderer());
    list.getSelectionModel().addListSelectionListener(
      new ListSelectionListener() {
        public void valueChanged(ListSelectionEvent e) {
          if (!e.getValueIsAdjusting()) {
            int index = list.getSelectionModel().getMaxSelectionIndex();

            if (index > -1) {
              addMode = false;
              addUpdateButton.setText(UPDATE_TEXT);

              ColorRuleHolder holder = (ColorRuleHolder) listModel.get(index);
              expression.setText(holder.ruleText);
              expression.setBackground(holder.colorRule.getBackgroundColor());
              expression.setForeground(holder.colorRule.getForegroundColor());
              chooser.setColor(holder.colorRule.getBackgroundColor());
            }

            if (index < 0) {
              removeButton.setEnabled(false);
              downButton.setEnabled(false);
              upButton.setEnabled(false);
            } else if ((index == 0) && (listModel.getSize() == 1)) {
              removeButton.setEnabled(true);
              downButton.setEnabled(false);
              upButton.setEnabled(false);
            } else if ((index == 0) && (listModel.getSize() > 1)) {
              removeButton.setEnabled(true);
              downButton.setEnabled(true);
              upButton.setEnabled(false);
            } else if (index == (listModel.getSize() - 1)) {
              removeButton.setEnabled(true);
              downButton.setEnabled(false);
              upButton.setEnabled(true);
            } else {
              removeButton.setEnabled(true);
              downButton.setEnabled(true);
              upButton.setEnabled(true);
            }
          }
        }
      });

    addUpdateButton.addActionListener(
      new AbstractAction() {
        public void actionPerformed(ActionEvent evt) {
          ColorRuleHolder holder =
            new ColorRuleHolder(
              expression.getText(),
              new ColorRule(
                ExpressionRule.getRule(expression.getText()),
                chooser.getColor()));

          if (addMode) {
            listModel.addElement(holder);
          } else {
            int index = list.getSelectionModel().getMaxSelectionIndex();
            listModel.remove(index);
            listModel.add(index, holder);
          }

          int index = listModel.indexOf(holder);
          list.getSelectionModel().setSelectionInterval(index, index);
        }
      });

    JPanel rightPanel = new JPanel(new BorderLayout());

    rightPanel.add(new JLabel("Rules:"), BorderLayout.NORTH);
    rightPanel.add(scrollPane, BorderLayout.CENTER);

    JPanel applyPanel = new JPanel();
    JButton apply = new JButton("Apply");
    applyPanel.add(apply);
    rightPanel.add(applyPanel, BorderLayout.SOUTH);

    apply.addActionListener(
      new AbstractAction() {
        public void actionPerformed(ActionEvent evt) {
          applyRules();
        }
      });

    add(rightPanel);
  }

  void applyRules() {
    colorizer.clear();

    List newList = new ArrayList();

    for (int i = 0, j = listModel.size(); i < j; i++) {
      newList.add(((ColorRuleHolder) listModel.get(i)).colorRule);
    }

    colorizer.addRules(newList);
  }

  class ColorRenderer extends DefaultListCellRenderer {
    public Component getListCellRendererComponent(
      JList list, Object value, int index, boolean isSelected,
      boolean cellHasFocus) {
      ColorRuleHolder h = (ColorRuleHolder) value;
      Component c =
        super.getListCellRendererComponent(
          list, value, index, isSelected, cellHasFocus);
      ((JLabel) c).setIcon(new SelectedIcon(isSelected));
      c.setBackground(h.colorRule.getBackgroundColor());
      c.setForeground(h.colorRule.getForegroundColor());

      return c;
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


class ColorRuleHolder {
  String ruleText;
  ColorRule colorRule;

  ColorRuleHolder(String ruleText, ColorRule colorRule) {
    this.ruleText = ruleText;
    this.colorRule = colorRule;
  }

  public String toString() {
    return ruleText;
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
