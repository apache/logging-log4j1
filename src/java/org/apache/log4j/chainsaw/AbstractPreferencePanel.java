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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.log4j.chainsaw.icons.ChainsawIcons;
/**
 * Some basic plumbing for Preference related dialogs.
 * 
 * Sub classes have the following responsibilities:
 * 
 * <ul>
 * <li>Must call this this classes initComponents() method from
 * within the sub-classes constructor, this method laysout the
 * panel and calls the abstract createTreeModel method to initialise the
 * tree of Panels.
 * <li>Must override createTreeModel() method to return a TreeModel whose
 * TreeNodes getUserObject() method will return a JComponent that is displayed
 * as the selected editable Preference panel.  This JComponent's .toString() method is
 * used as the title of the preference panel
 * </ul>
 * 
 * @author Paul Smith <psmith@apache.org>
 *
 */
public abstract class AbstractPreferencePanel extends JPanel
{

  private final JLabel titleLabel = new JLabel("Selected Pref Panel");
  private final JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
  private final JPanel selectedPrefPanel = new JPanel(new BorderLayout(0, 3));
  private final JButton okButton = new JButton("OK");
  private final JButton cancelButton = new JButton("Cancel");
  private ActionListener okCancelListener;
  private Component currentlyDisplayedPanel = null;
  private final JTree prefTree = new JTree();
  /**
   * Setup and layout for the components
   */
  protected void initComponents()
  {
    //		setBorder(BorderFactory.createLineBorder(Color.red));
    setLayout(new BorderLayout(5, 5));
    setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
  
    prefTree.setModel(createTreeModel());
    
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
   * @return
   */
  protected abstract TreeModel createTreeModel();

  /**
   * Ensures a specific panel is displayed in the spot where
   * preferences can be selected.
   *
  * @param panel
  */
  protected void setDisplayedPrefPanel(JComponent panel)
  {
    if (currentlyDisplayedPanel != null) {
      selectedPrefPanel.remove(currentlyDisplayedPanel);
    }
  
    selectedPrefPanel.add(panel, BorderLayout.CENTER);
    currentlyDisplayedPanel = panel;
    String title = panel.toString();
    titleLabel.setText(title);
    selectedPrefPanel.revalidate();
    selectedPrefPanel.repaint();
  }

  public void notifyOfLookAndFeelChange() {
    SwingUtilities.updateComponentTreeUI(this);
    
    Enumeration enumeration = ((DefaultMutableTreeNode)prefTree.getModel().getRoot()).breadthFirstEnumeration();
    while (enumeration.hasMoreElements()) {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumeration.nextElement();
      if (node.getUserObject() instanceof Component) {
        Component c = (Component) node.getUserObject();
        SwingUtilities.updateComponentTreeUI(c);
      }
    }
  }
                                          
  public void setOkCancelActionListener(ActionListener l)
  {
    this.okCancelListener = l;
  }

  public void hidePanel()
  {
    if (okCancelListener != null) {
      okCancelListener.actionPerformed(null);
    }
  }

  /**
   * @return Returns the cancelButton.
   */
  protected final JButton getCancelButton()
  {
    return cancelButton;
  }
  /**
   * @return Returns the okButton.
   */
  protected final JButton getOkButton()
  {
    return okButton;
  }
}
