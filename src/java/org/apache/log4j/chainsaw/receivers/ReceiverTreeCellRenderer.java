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

package org.apache.log4j.chainsaw.receivers;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.apache.log4j.chainsaw.Generator;
import org.apache.log4j.chainsaw.icons.ChainsawIcons;
import org.apache.log4j.chainsaw.icons.LevelIconFactory;
import org.apache.log4j.plugins.Plugin;
import org.apache.log4j.spi.Thresholdable;


/**
 * A TreeCellRenderer that can format the information of Receivers
 * and their children
 *
 * @author Paul Smith <psmith@apache.org>
 */
public class ReceiverTreeCellRenderer extends DefaultTreeCellRenderer {
  private Icon rootIcon = new ImageIcon(ChainsawIcons.ANIM_NET_CONNECT);
  private JPanel panel = new JPanel();
  private JLabel levelLabel = new JLabel();

  public ReceiverTreeCellRenderer() {
    super();
    panel.setOpaque(false);
    panel.setLayout(new BorderLayout());
    panel.add(this, BorderLayout.CENTER);
    panel.add(levelLabel, BorderLayout.EAST);
  }

  public Component getTreeCellRendererComponent(
    JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
    int row, boolean focus) {
    super.getTreeCellRendererComponent(
      tree, value, sel, expanded, leaf, row, focus);

    DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
    Object o = node.getUserObject();
    setText(o.toString());

    String tooltip = "";

    setIcon(null);
    if (
      o == ((ReceiversTreeModel) tree.getModel()).getRootNode().getUserObject()) {
      setText(o.toString());
    } else if (o instanceof String) {
      setText(o.toString());
      setIcon(null);
     }else if(o instanceof Plugin){
       setText(((Plugin)o).getName());
     }else if(o instanceof Generator){
    	Generator generator = (Generator) o;
    	setText(generator.getName());
    	setIcon(ChainsawIcons.ICON_HELP);
    } 
    else {
      setText("(Unknown Type) :: " + o);
    }

    if (
      o == ((ReceiversTreeModel) tree.getModel()).getRootNode().getUserObject()) {
      setIcon(rootIcon);
    }

    levelLabel.setText(null);
    levelLabel.setIcon(null);

    if (o instanceof Thresholdable) {
      Thresholdable t = (Thresholdable) o;

      if (t.getThreshold() != null) {
        levelLabel.setIcon(
          (Icon) LevelIconFactory.getInstance().getLevelToIconMap().get(
            t.getThreshold().toString()));

        if (levelLabel.getIcon() == null) {
          levelLabel.setText(t.getThreshold().toString());
        }
      }
    }

    setToolTipText(tooltip);

    return panel;
  }
}
