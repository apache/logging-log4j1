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

/*
 */
package org.apache.log4j.chainsaw;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.log4j.chainsaw.icons.ChainsawIcons;
import org.apache.log4j.helpers.LogLog;


/**
 * A panel that encapsulates the Logger Name tree, with associated actions
 *
 * @author Paul Smith <psmith@apache.org>
 */
final class LoggerNameTreePanel extends JPanel {
  private static final int WARN_DEPTH = 4;
  private final JTree logTree;
  private final JScrollPane scrollTree;
  private final JToolBar toolbar = new JToolBar();
  private final JButton expandButton = new SmallButton();
  private final JButton collapseButton = new SmallButton();
  private final JButton closeButton = new SmallButton();
  private final JButton editLoggerButton = new SmallButton();
  private final Action expandAction;
  private final Action collapseAction;
  private final Action closeAction;
  private final Action editLoggerAction;

  /**
   * @param logTreeModel
   */
  LoggerNameTreePanel(LogPanelLoggerTreeModel logTreeModel) {
    super();
    setLayout(new BorderLayout());
    setBorder(BorderFactory.createEtchedBorder());

    logTree =
      new JTree(logTreeModel) {
          public String getToolTipText(MouseEvent ev) {
            if (ev == null) {
              return null;
            }

            TreePath path = logTree.getPathForLocation(ev.getX(), ev.getY());

            if (path != null) {
              Object[] objects = path.getPath();
              StringBuffer buf = new StringBuffer();

              for (int i = 1; i < objects.length; i++) {
                buf.append(objects[i].toString());

                if (i < (objects.length - 1)) {
                  buf.append(".");
                }
              }

              //				TODO output the Level filter details
              return buf.toString();
            }

            return null;
          }
        };

    ToolTipManager.sharedInstance().registerComponent(logTree);
    logTree.setCellRenderer(new LoggerNameTreeCellRenderer());

    //	============================================
    logTreeModel.addTreeModelListener(
      new TreeModelListener() {
        public void treeNodesChanged(TreeModelEvent e) {
          ensureRootExpanded();
        }

        public void treeNodesInserted(TreeModelEvent e) {
          ensureRootExpanded();
        }

        public void treeNodesRemoved(TreeModelEvent e) {
          ensureRootExpanded();
        }

        public void treeStructureChanged(TreeModelEvent e) {
          ensureRootExpanded();
        }
      });

    logTree.setEditable(false);

    //	TODO decide if Multi-selection is useful, and how it would work	
    TreeSelectionModel selectionModel = new DefaultTreeSelectionModel();
    selectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    logTree.setSelectionModel(selectionModel);

    logTree.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
    scrollTree = new JScrollPane(logTree);
    scrollTree.setMinimumSize(new Dimension(150, 400));
    toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.X_AXIS));

    expandAction = createExpandAction();
    editLoggerAction = createEditLoggerAction();
    closeAction = createCloseAction();
    collapseAction = createCollapseAction();
    setupListeners();
    configureToolbarPanel();

    add(toolbar, BorderLayout.NORTH);
    add(scrollTree, BorderLayout.CENTER);
  }

  private void ensureRootExpanded() {
    logTree.expandRow(0);
  }

  /**
  * @return
  */
  private Action createCollapseAction() {
    Action action =
      new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          collapseCurrentlySelectedNode();
        }
      };

    action.putValue(Action.NAME, "Collapse");
    action.putValue(
      Action.SHORT_DESCRIPTION,
      "Collapses all the children of the currently selected node");
    action.setEnabled(false);

    return action;
  }

  /**
     * An action that closes (hides) this panel
    * @return
    */
  private Action createCloseAction() {
    Action action =
      new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          LoggerNameTreePanel.this.setVisible(false);
        }
      };

    action.putValue(Action.NAME, "Close");
    action.putValue(Action.SHORT_DESCRIPTION, "Closes the Logger panel");
    action.putValue(Action.SMALL_ICON, new CloseIcon(8, 1, 1));

    return action;
  }

  /**
    * Configures varoius listeners etc for the components within
    * this Class.
    */
  private void setupListeners() {
    /**
       * Enable the actions depending on state of the tree selection
       */
    logTree.addTreeSelectionListener(
      new TreeSelectionListener() {
        public void valueChanged(TreeSelectionEvent e) {
          TreePath path = e.getNewLeadSelectionPath();
          expandAction.setEnabled(path != null);
//          editLoggerAction.setEnabled(path != null);
          collapseAction.setEnabled(path != null);
        }
      });

    /**
     * Now add a MouseListener that fires the expansion
     * action if CTRL + DBL CLICK is done.
     */
    logTree.addMouseListener(
      new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
          super.mouseClicked(e);

          if (
            (e.getClickCount() > 1)
              && ((e.getModifiers() & InputEvent.CTRL_MASK) > 0)
              && ((e.getModifiers() & InputEvent.BUTTON1_MASK) > 0)) {
            expandCurrentlySelectedNode();
          } else if (e.getClickCount() > 1) {
            LogLog.debug("Ignoring dbl click event " + e);
          }
        }
      });
  }

  private Action createEditLoggerAction() {
    Action action =
      new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          // TODO Auto-generated method stub
        }
      };

//    TODO enable this when it's ready.
    action.putValue("enabled", Boolean.FALSE);
    
    action.putValue(Action.NAME, "Edit filters/colors");
    action.putValue(
      Action.SHORT_DESCRIPTION,
      "Allows you to specify filters and coloring for this Logger");
    action.putValue(
      Action.SMALL_ICON, new ImageIcon(ChainsawIcons.ICON_EDIT_RECEIVER));
    action.setEnabled(false);

    return action;
  }

  /**
   * Creates an action that is used to expand the selected node
   * and all children
   * @return an Action
   */
  private Action createExpandAction() {
    Action action =
      new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          expandCurrentlySelectedNode();
        }
      };

    action.putValue(Action.NAME, "Expand branch");
    action.putValue(
      Action.SHORT_DESCRIPTION,
      "Expands all the child nodes of the currently selected node, recursively");
    action.setEnabled(false);

    return action;
  }

  /**
   * Given the currently selected nodes
   * collapses all the children of those nodes.
   *
   */
  private void collapseCurrentlySelectedNode() {
    TreePath[] paths = logTree.getSelectionPaths();

    if (paths == null) {
      return;
    }

    LogLog.debug("Collapsing all children of selected node");

    for (int i = 0; i < paths.length; i++) {
      TreePath path = paths[i];
      DefaultMutableTreeNode node =
        (DefaultMutableTreeNode) path.getLastPathComponent();
      Enumeration enumeration = node.depthFirstEnumeration();

      while (enumeration.hasMoreElements()) {
        DefaultMutableTreeNode child =
          (DefaultMutableTreeNode) enumeration.nextElement();

        if (child.getParent() != null) {
          TreeNode[] nodes =
            ((DefaultMutableTreeNode) child.getParent()).getPath();

          TreePath treePath = new TreePath(nodes);

          while ((treePath != null) && (treePath.getPathCount() > 0)) {
            DefaultMutableTreeNode potentialRoot =
              (DefaultMutableTreeNode) treePath.getPathComponent(0);
            logTree.collapsePath(treePath);
            treePath = treePath.getParentPath();
          }
        }
      }
    }

    ensureRootExpanded();
  }

  /**
   * Expands the currently selected node (if any)
   * including all the children.
   *
   */
  private void expandCurrentlySelectedNode() {
    TreePath[] paths = logTree.getSelectionPaths();

    if (paths == null) {
      return;
    }

    LogLog.debug("Expanding all children of selected node");

    for (int i = 0; i < paths.length; i++) {
      TreePath path = paths[i];
      DefaultMutableTreeNode treeNode =
        (DefaultMutableTreeNode) path.getLastPathComponent();

      Enumeration depthEnum = treeNode.depthFirstEnumeration();

      if (!depthEnum.hasMoreElements()) {
        break;
      }

      List depths = new ArrayList();

      while (depthEnum.hasMoreElements()) {
        depths.add(
          new Integer(
            ((DefaultMutableTreeNode) depthEnum.nextElement()).getDepth()));
      }

      Collections.sort(depths);
      Collections.reverse(depths);

      int maxDepth = ((Integer) depths.get(0)).intValue();

      if (maxDepth > WARN_DEPTH) {
        LogLog.warn("Should warn user, depth=" + maxDepth);
      }

      depthEnum = treeNode.depthFirstEnumeration();

      while (depthEnum.hasMoreElements()) {
        DefaultMutableTreeNode node =
          (DefaultMutableTreeNode) depthEnum.nextElement();

        if (node.isLeaf()) {
          TreeNode[] nodes =
            ((DefaultMutableTreeNode) node.getParent()).getPath();
          TreePath treePath = new TreePath(nodes);

          LogLog.debug("Expanding path:" + treePath);

          logTree.expandPath(treePath);
        }
      }
    }
  }

  /**
     * configures all the components that are used in the mini-toolbar of this
     * component
     */
  private void configureToolbarPanel() {
    toolbar.setFloatable(false);

    
    expandButton.setAction(expandAction);
    expandButton.setText("+");
    collapseButton.setAction(collapseAction);
    collapseButton.setText("-");

    expandButton.setFont(expandButton.getFont().deriveFont(Font.BOLD));
    collapseButton.setFont(collapseButton.getFont().deriveFont(Font.BOLD));
    
    editLoggerButton.setAction(editLoggerAction);
    editLoggerButton.setText(null);
    closeButton.setAction(closeAction);
    closeButton.setText(null);

    toolbar.add(expandButton);
    toolbar.add(collapseButton);
    toolbar.add(editLoggerButton);
    toolbar.addSeparator();

    toolbar.add(Box.createHorizontalGlue());
    toolbar.add(closeButton);
    toolbar.add(Box.createHorizontalStrut(5));
  }

  /**
        *
        * @author Paul Smith <psmith@apache.org>
        *
        */
  private static class LoggerNameTreeCellRenderer
    extends DefaultTreeCellRenderer {
    private JPanel panel = new JPanel();

    private LoggerNameTreeCellRenderer() {
      super();
      panel.setBackground(UIManager.getColor("Tree.textBackground"));

      Icon leafIcon = getDefaultLeafIcon();
      Icon icon = new ImageIcon(ChainsawIcons.WINDOW_ICON);

      panel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
      panel.add(this);

      setLeafIcon(null);
      setOpaque(false);
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.TreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int, boolean)
     */
    public Component getTreeCellRendererComponent(
      JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
      int row, boolean hasFocus) {
      Component component =
        super.getTreeCellRendererComponent(
          tree, value, sel, expanded, leaf, row, hasFocus);

      if (row == 0) {
      } else {
      }

      return panel;
    }
  }
}
