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
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;


/**
 * GUI panel used to manipulate the PreferenceModel for a Log Panel
 *
 * @author Paul Smith
 */
public class LogPanelPreferencePanel extends JPanel {
  private final LogPanelPreferenceModel model;
  final JLabel titleLabel = new JLabel("Selected Pref Panel");

  public LogPanelPreferencePanel(LogPanelPreferenceModel model) {
    this.model = model;
    initComponents();
  }

  /**
   * Setsup and layouts the components
   */
  private void initComponents() {
    //		setBorder(BorderFactory.createLineBorder(Color.red));
    setLayout(new BorderLayout(5, 5));
    setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

    final JTree prefTree = new JTree(createTreeModel());
    DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
    renderer.setLeafIcon(null);
    prefTree.setCellRenderer(renderer);

    final JScrollPane treeScroll = new JScrollPane(prefTree);

    treeScroll.setPreferredSize(new Dimension(200, 240));

    JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

    JPanel selectedPrefPanel = new JPanel(new BorderLayout(0, 3));

    titleLabel.setFont(titleLabel.getFont().deriveFont(16.0f));
    titleLabel.setBorder(BorderFactory.createEtchedBorder());
    titleLabel.setBackground(Color.white);
    titleLabel.setOpaque(true);

    selectedPrefPanel.add(titleLabel, BorderLayout.NORTH);

    mainPanel.add(treeScroll, BorderLayout.WEST);
    mainPanel.add(selectedPrefPanel, BorderLayout.CENTER);

    add(mainPanel, BorderLayout.CENTER);

    JButton okButton = new JButton("OK");
    JButton cancelButton = new JButton("Cancel");
    Box buttonBox = Box.createHorizontalBox();
    buttonBox.add(Box.createHorizontalGlue());
    buttonBox.add(okButton);
    buttonBox.add(Box.createHorizontalStrut(5));
    buttonBox.add(cancelButton);

    add(buttonBox, BorderLayout.SOUTH);
  }

  private TreeModel createTreeModel() {
    final DefaultMutableTreeNode rootNode =
      new DefaultMutableTreeNode("Preferences");
    DefaultTreeModel model = new DefaultTreeModel(rootNode);

    DefaultMutableTreeNode formatting =
      new DefaultMutableTreeNode("Formatting");
    rootNode.add(formatting);

    return model;
  }

  public static void main(String[] args) {
    JFrame f = new JFrame("Preferences Panel Test Bed");
    LogPanelPreferenceModel model = new LogPanelPreferenceModel();
    f.getContentPane().add(new LogPanelPreferencePanel(model));

    f.setSize(640, 480);
    f.show();
  }
}
