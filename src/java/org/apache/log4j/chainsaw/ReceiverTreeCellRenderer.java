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
import java.awt.Component;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.apache.log4j.chainsaw.icons.ChainsawIcons;
import org.apache.log4j.chainsaw.icons.LevelIconFactory;
import org.apache.log4j.net.AddressBased;
import org.apache.log4j.net.NetworkBased;
import org.apache.log4j.net.PortBased;
import org.apache.log4j.plugins.Pauseable;
import org.apache.log4j.spi.Thresholdable;


/**
 * A TreeCellRenderer that can format the information of Receivers
 * and their children
 *
 * @author Paul Smith <psmith@apache.org>
 */
public class ReceiverTreeCellRenderer extends DefaultTreeCellRenderer {
  private Icon activeReceiverIcon =
    new ImageIcon(ChainsawIcons.ICON_ACTIVE_RECEIVER);
  private Icon inactiveReceiverIcon =
    new ImageIcon(ChainsawIcons.ICON_INACTIVE_RECEIVER);
  private Icon rootIcon = new ImageIcon(ChainsawIcons.ANIM_NET_CONNECT);
  private JPanel panel = new JPanel();
  private JLabel levelLabel = new JLabel();

  public ReceiverTreeCellRenderer() {
    super();
    panel.setOpaque(false);
    panel.setLayout(new BorderLayout());
    panel.add(this, BorderLayout.CENTER);
    panel.add(levelLabel, BorderLayout.WEST);
  }

  public Component getTreeCellRendererComponent(
    JTree tree, Object value, boolean selected, boolean expanded, boolean leaf,
    int row, boolean hasFocus) {
    super.getTreeCellRendererComponent(
      tree, value, selected, expanded, leaf, row, hasFocus);

    DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
    Object o = node.getUserObject();
    setText(o.toString());

    String tooltip = "";

    /**
     * Deal with Text
     */
    if ((o instanceof NetworkBased)) {
      NetworkBased networkBased = (NetworkBased) o;

      StringBuffer buf = new StringBuffer(networkBased.getName());

      if (o instanceof AddressBased) {
        buf.append("::").append(((AddressBased) o).getAddress());
      }

      if (o instanceof PortBased) {
        PortBased portBased = (PortBased) o;

        buf.append("::").append(portBased.getPort());
      }

      buf.append("\n ").append("(").append(
        networkBased.isActive() ? "active" : "inactive");

      if (o instanceof Thresholdable) {
        Thresholdable t = (Thresholdable) o;

        if (t.getThreshold() != null) {
          buf.append(",").append(t.getThreshold());
        }
      }

      buf.append(")");
      setText(buf.toString());
    } else if (
      o == ((ReceiversTreeModel) tree.getModel()).getRootNode().getUserObject()) {
      setText(o.toString());
    } else if (o instanceof String) {
      setText(o.toString());
      setIcon(null);
    }else if(o instanceof Generator){
    	Generator generator = (Generator) o;
    	setText(generator.getName());
    	setIcon(ChainsawIcons.ICON_HELP);
    } 
    else {
      setText("(Unknown Type) :: " + o);
    }

    /**
     * Now deal with Icon
     */
    if (o instanceof NetworkBased) {
      NetworkBased networkBased = (NetworkBased) o;

      if (networkBased.isActive()) {
        if ((o instanceof Pauseable) && !((Pauseable) o).isPaused()) {
          setIcon(activeReceiverIcon);
          tooltip += "This item is active, and can be paused";
        } else if ((o instanceof Pauseable) && ((Pauseable) o).isPaused()) {
          setIcon(inactiveReceiverIcon);
          tooltip += "This item is paused, and can be resumed";
        } else {
          setIcon(null);
          tooltip += " This item cannot be Paused/Resumed";
        }
      } else {
      }
    } else if (
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
