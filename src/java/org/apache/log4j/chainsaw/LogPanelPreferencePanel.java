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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;


/**
 * GUI panel used to manipulate the PreferenceModel for a Log Panel
 *
 * @author Paul Smith
 */
public class LogPanelPreferencePanel extends JPanel {
  private final LogPanelPreferenceModel committedPreferenceModel;
  private final JLabel titleLabel = new JLabel("Selected Pref Panel");
  private final JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
  private final JPanel selectedPrefPanel = new JPanel(new BorderLayout(0, 3));
  private final LogPanelPreferenceModel uncommitedPreferenceModel =
    new LogPanelPreferenceModel();
  private ActionListener okCancelListener;

  public LogPanelPreferencePanel(LogPanelPreferenceModel model) {
    this.committedPreferenceModel = model;
    initComponents();
  }

  /**
   * Setup and layout for the components
   */
  private void initComponents() {
    //		setBorder(BorderFactory.createLineBorder(Color.red));
    setLayout(new BorderLayout(5, 5));
    setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

    final JTree prefTree = new JTree(createTreeModel());
    prefTree.setRootVisible(false);

    DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
    renderer.setLeafIcon(ChainsawIcons.ICON_PREFERENCES);
    prefTree.setCellRenderer(renderer);

    final JScrollPane treeScroll = new JScrollPane(prefTree);

    treeScroll.setPreferredSize(new Dimension(200, 240));

    titleLabel.setFont(titleLabel.getFont().deriveFont(16.0f));
    titleLabel.setBorder(BorderFactory.createEtchedBorder());
    titleLabel.setBackground(Color.white);
    titleLabel.setOpaque(true);

    selectedPrefPanel.add(titleLabel, BorderLayout.NORTH);

    mainPanel.add(treeScroll, BorderLayout.WEST);
    mainPanel.add(selectedPrefPanel, BorderLayout.CENTER);

    add(mainPanel, BorderLayout.CENTER);

    JButton okButton = new JButton("OK");

    okButton.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          committedPreferenceModel.apply(uncommitedPreferenceModel);
          hidePanel();
        }
      });

    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          hidePanel();
        }
      });

    Box buttonBox = Box.createHorizontalBox();
    buttonBox.add(Box.createHorizontalGlue());
    buttonBox.add(okButton);
    buttonBox.add(Box.createHorizontalStrut(5));
    buttonBox.add(cancelButton);

    add(buttonBox, BorderLayout.SOUTH);

    DefaultTreeSelectionModel treeSelectionModel =
      new DefaultTreeSelectionModel();
    treeSelectionModel.setSelectionMode(
      TreeSelectionModel.SINGLE_TREE_SELECTION);
    prefTree.setSelectionModel(treeSelectionModel);
    prefTree.addTreeSelectionListener(
      new TreeSelectionListener() {
        public void valueChanged(TreeSelectionEvent e) {
          TreePath path = e.getNewLeadSelectionPath();
          DefaultMutableTreeNode node =
            (DefaultMutableTreeNode) path.getLastPathComponent();
          setDisplayedPrefPanel((JComponent) node.getUserObject());
        }
      });

    // ensure the first pref panel is selected and displayed
    DefaultMutableTreeNode root =
      (DefaultMutableTreeNode) prefTree.getModel().getRoot();
    DefaultMutableTreeNode firstNode =
      (DefaultMutableTreeNode) root.getFirstChild();
    prefTree.setSelectionPath(new TreePath(firstNode.getPath()));
  }

  /**
   * Ensures a specific panel is displayed in the spot where
   * preferences can be selected.
   *
  * @param panel
  */
  protected void setDisplayedPrefPanel(JComponent panel) {
    selectedPrefPanel.add(panel, BorderLayout.CENTER);
    selectedPrefPanel.invalidate();
    selectedPrefPanel.validate();
    titleLabel.setText(panel.toString());
  }

  public void setOkCancelActionListener(ActionListener l) {
    this.okCancelListener = l;
  }

  public void hidePanel() {
    if (okCancelListener != null) {
      okCancelListener.actionPerformed(null);
    }
  }

  /**
   * Ensures this panels DISPLAYED model is in sync with
   * the model initially passed to the constructor.
   *
   */
  public void updateModel() {
    this.uncommitedPreferenceModel.apply(committedPreferenceModel);
  }

  private TreeModel createTreeModel() {
    final DefaultMutableTreeNode rootNode =
      new DefaultMutableTreeNode("Preferences");
    DefaultTreeModel model = new DefaultTreeModel(rootNode);

    DefaultMutableTreeNode formatting =
      new DefaultMutableTreeNode(new FormattingPanel());
    rootNode.add(formatting);

    return model;
  }

  /**
   * @return
   */
  private LogPanelPreferenceModel getModel() {
    return uncommitedPreferenceModel;
  }

  public static void main(String[] args) {
    JFrame f = new JFrame("Preferences Panel Test Bed");
    LogPanelPreferenceModel model = new LogPanelPreferenceModel();
    f.getContentPane().add(new LogPanelPreferencePanel(model));

    f.setSize(640, 480);
    f.show();
  }

  /**
   * All of the Preferences panels used in this class extend from
   * this, it is used to provide standard L&F required by all.
   * @author Paul Smith
   *
   */
  private static class BasicPrefPanel extends JPanel {
    private BasicPrefPanel() {
      //    	setBorder(BorderFactory.createLineBorder(Color.red));
    }
  }

  /**
   * Provides preference gui's for all the Formatting options
   * available for the columns etc.
   */
  private class FormattingPanel extends BasicPrefPanel {
    private FormattingPanel() {
      super();
      this.initComponents();
    }

    private void initComponents() {
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

      JPanel dateFormatPanel = new JPanel();
      dateFormatPanel.setBorder(
        BorderFactory.createTitledBorder(
          BorderFactory.createEtchedBorder(), "Timestamp"));
      dateFormatPanel.setLayout(
        new BoxLayout(dateFormatPanel, BoxLayout.Y_AXIS));

      ButtonGroup bgDateFormat = new ButtonGroup();
      final JRadioButton rdISO =
        new JRadioButton("ISO 8601 format (yyyy-MM-dd HH:mm:ss)");
      rdISO.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            getModel().setUseISO8601Format(rdISO.isSelected());
          }
        });
      rdISO.setSelected(getModel().isUseISO8601Format());
      getModel().addPropertyChangeListener(
        "useISO8601Format",
        new PropertyChangeListener() {
          public void propertyChange(PropertyChangeEvent evt) {
            rdISO.setSelected(getModel().isUseISO8601Format());
          }
        });
      dateFormatPanel.add(rdISO);

      add(dateFormatPanel);
      add(Box.createVerticalGlue());
    }

    public String toString() {
      return "Formatting";
    }
  }
}
