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
/*
 */
package org.apache.log4j.chainsaw;

import org.apache.log4j.chainsaw.icons.ChainsawIcons;
import org.apache.log4j.chainsaw.icons.LineIconFactory;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.rule.AbstractRule;
import org.apache.log4j.rule.Rule;
import org.apache.log4j.spi.LoggingEvent;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
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


/**
 * A panel that encapsulates the Logger Name tree, with associated actions
 * and implements the Rule interface so that it can filter in/out events
 * that do not match the users request for refining the view based on Loggers.
 *
 * @author Paul Smith <psmith@apache.org>
 */
final class LoggerNameTreePanel extends JPanel implements Rule
{
  //~ Static fields/initializers ==============================================

  private static final int WARN_DEPTH = 4;

  //~ Instance fields =========================================================

  private LoggerNameTreeCellRenderer cellRenderer =
    new LoggerNameTreeCellRenderer();
  private final Action clearIgnoreListAction;
  private final Action closeAction;
  private final JButton closeButton = new SmallButton();
  private final Action collapseAction;
  private final JButton collapseButton = new SmallButton();
  private final Action editLoggerAction;
  private final JButton editLoggerButton = new SmallButton();
  private final Action expandAction;
  private final JButton expandButton = new SmallButton();
  private final Action focusOnAction;
  private final SmallToggleButton focusOnLoggerButton =
    new SmallToggleButton();
  private final Set hiddenSet = new HashSet();
  private final Action hideAction;
  private final LogPanelPreferenceModel preferenceModel;

  private final JList ignoreList = new JList();
  private final JScrollPane ignoreListScroll = new JScrollPane(ignoreList);
  private final SmallToggleButton ignoreLoggerButton = new SmallToggleButton();
  private final EventListenerList listenerList = new EventListenerList();
  private final JTree logTree;

  //  private final EventListenerList focusOnActionListeners =
  //    new EventListenerList();
  private final LogPanelLoggerTreeModel logTreeModel;
  private final PopupListener popupListener;
  private final LoggerTreePopupMenu popupMenu;
  private Rule ruleDelegate = new AbstractRule()
    {
      public boolean evaluate(LoggingEvent e)
      {
        return true;
      }
    };

  private final JScrollPane scrollTree;
  private final JToolBar toolbar = new JToolBar();

  //~ Constructors ============================================================

  /**
   * Creates a new LoggerNameTreePanel object.
   *
   * @param logTreeModel
   */
  LoggerNameTreePanel(LogPanelLoggerTreeModel logTreeModel, LogPanelPreferenceModel preferenceModel)
  {
    super();
    this.logTreeModel = logTreeModel;
    this.preferenceModel = preferenceModel;

    setLayout(new BorderLayout());
    setBorder(BorderFactory.createEtchedBorder());

    logTree =
    new JTree(logTreeModel)
      {
        public String getToolTipText(MouseEvent ev)
        {
          if (ev == null)
          {
            return null;
          }

          TreePath path = logTree.getPathForLocation(ev.getX(), ev.getY());

          String loggerName = getLoggerName(path);

          if (hiddenSet.contains(loggerName))
          {
            loggerName += " (you are ignoring this logger)";
          }

          return loggerName;
        }
      };

    ToolTipManager.sharedInstance().registerComponent(logTree);
    logTree.setCellRenderer(cellRenderer);

    //	============================================
    logTreeModel.addTreeModelListener(new TreeModelListener()
      {
        private boolean latched = false;

        public void treeNodesChanged(TreeModelEvent e)
        {
        }

        public void treeNodesInserted(TreeModelEvent e)
        {
          if (!latched)
          {
            ensureRootExpanded();
            latched = true;
          }
        }

        public void treeNodesRemoved(TreeModelEvent e)
        {
        }

        public void treeStructureChanged(TreeModelEvent e)
        {
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
    focusOnAction = createFocusOnAction();
    hideAction = createIgnoreAction();
    clearIgnoreListAction = createClearIgnoreListAction();

    popupMenu = new LoggerTreePopupMenu();
    popupListener = new PopupListener(popupMenu);

    setupListeners();
    configureToolbarPanel();

    add(toolbar, BorderLayout.NORTH);
    add(scrollTree, BorderLayout.CENTER);

    add(ignoreListScroll, BorderLayout.SOUTH);

    CheckListCellRenderer cellRenderer = new CheckListCellRenderer()
      {
        protected boolean isSelected(Object value)
        {
          return true;
        }
      };

    ignoreList.setCellRenderer(cellRenderer);

    ignoreList.addMouseListener(new MouseAdapter()
      {
        public void mouseClicked(MouseEvent e)
        {
          if (
            (e.getClickCount() > 1)
              && ((e.getModifiers() & InputEvent.BUTTON1_MASK) > 0))
          {
            int index = ignoreList.locationToIndex(e.getPoint());

            if (index >= 0)
            {
              String string =
                ignoreList.getModel().getElementAt(index).toString();
              toggleHiddenLogger(string);
              fireChangeEvent();

              /**
               * TODO this needs to get the node that has this logger and fire a visual update
               */
              LoggerNameTreePanel.this.logTreeModel.nodeStructureChanged(
                (TreeNode) LoggerNameTreePanel.this.logTreeModel.getRoot());
            }
          }
        }
      });
  }

  //~ Methods =================================================================

  /**
   * Adds a change Listener to this LoggerNameTreePanel to be notfied
   * when the State of the Focus or Hidden details have changed.
   *
   * @param l
   */
  public void addChangeListener(ChangeListener l)
  {
    listenerList.add(ChangeListener.class, l);
  }

  /* (non-Javadoc)
   * @see org.apache.log4j.chainsaw.rule.Rule#evaluate(org.apache.log4j.spi.LoggingEvent)
   */
  /**
   * DOCUMENT ME!
   *
   * @param e DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  public boolean evaluate(LoggingEvent e)
  {
    return ruleDelegate.evaluate(e);
  }

  /**
   * DOCUMENT ME!
   *
   * @param l DOCUMENT ME!
   */
  public void removeChangeListener(ChangeListener l)
  {
    listenerList.remove(ChangeListener.class, l);
  }

  /**
   * Ensures the Focus is set to a specific logger name
   * @param logger
   */
  public void setFocusOn(String logger)
  {
    DefaultMutableTreeNode node = logTreeModel.lookupLogger(logger);

    if (node != null)
    {
      TreeNode[] nodes = node.getPath();
      TreePath treePath = new TreePath(nodes);
      logTree.setSelectionPath(treePath);

      if (!focusOnLoggerButton.isSelected())
      {
        focusOnLoggerButton.doClick();
      }
    }
    else
    {
      LogLog.error("failed to lookup logger " + logger);
    }
  }

  /**
   * DOCUMENT ME!
   *
   * @param logger
   */
  protected void toggleHiddenLogger(String logger)
  {
    if (!hiddenSet.contains(logger))
    {
      hiddenSet.add(logger);
    }
    else
    {
      hiddenSet.remove(logger);
    }

    firePropertyChange("hiddenSet", (Object) null, (Object) null);
  }

  /**
   * Returns the full name of the Logger that is represented by
   * the currently selected Logger node in the tree.
   *
   * This is the dotted name, of the current node including all it's parents.
   *
   * If multiple Nodes are selected, the first path is used
   * @return Logger Name or null if nothing selected
   */
  String getCurrentlySelectedLoggerName()
  {
    TreePath[] paths = logTree.getSelectionPaths();

    if ((paths == null) || (paths.length == 0))
    {
      return null;
    }

    TreePath firstPath = paths[0];

    return getLoggerName(firstPath);
  }

  /**
   * Returns an unmodifiable set of those Loggers marked as hidden.
   * @return
   */
  Set getHiddenSet()
  {
    return Collections.unmodifiableSet(hiddenSet);
  }

  /**
   * Returns the full
   * @param path DOCUMENT ME!
   * @return
   */
  String getLoggerName(TreePath path)
  {
    if (path != null)
    {
      Object[] objects = path.getPath();
      StringBuffer buf = new StringBuffer();

      for (int i = 1; i < objects.length; i++)
      {
        buf.append(objects[i].toString());

        if (i < (objects.length - 1))
        {
          buf.append(".");
        }
      }

      return buf.toString();
    }

    return null;
  }

  /**
   * adds a Collection of Strings to the ignore List and notifise all listeners of
   * both the "hiddenSet" property and those expecting the Rule to change
   * via the ChangeListener interface 
   * @param fqnLoggersToIgnore
   */
  void ignore(Collection fqnLoggersToIgnore)
  {
    hiddenSet.addAll(fqnLoggersToIgnore);
    firePropertyChange("hiddenSet", null, null);
    fireChangeEvent();
  }

  /**
   * Returns true if the FocusOn element has been selected
   * @return true if the FocusOn action/lement has been selected
   */
  boolean isFocusOnSelected()
  {
    return focusOnAction.getValue("checked") != null;
  }

  void setFocusOnSelected(boolean selected)
  {
    if (selected)
    {
      focusOnAction.putValue("checked", Boolean.TRUE);
    }
    else
    {
      focusOnAction.putValue("checked", null);
    }
  }

  /**
   * Given the currently selected nodes
   * collapses all the children of those nodes.
   *
   */
  private void collapseCurrentlySelectedNode()
  {
    TreePath[] paths = logTree.getSelectionPaths();

    if (paths == null)
    {
      return;
    }

      LogLog.debug("Collapsing all children of selected node");

    for (int i = 0; i < paths.length; i++)
    {
      TreePath path = paths[i];
      DefaultMutableTreeNode node =
        (DefaultMutableTreeNode) path.getLastPathComponent();
      Enumeration enumeration = node.depthFirstEnumeration();

      while (enumeration.hasMoreElements())
      {
        DefaultMutableTreeNode child =
          (DefaultMutableTreeNode) enumeration.nextElement();

        if ((child.getParent() != null) && (child != node))
        {
          TreeNode[] nodes =
            ((DefaultMutableTreeNode) child.getParent()).getPath();

          TreePath treePath = new TreePath(nodes);
          logTree.collapsePath(treePath);
        }
      }
    }

    ensureRootExpanded();
  }

  /**
     * configures all the components that are used in the mini-toolbar of this
     * component
     */
  private void configureToolbarPanel()
  {
    toolbar.setFloatable(false);

    expandButton.setAction(expandAction);
    expandButton.setText(null);
    collapseButton.setAction(collapseAction);
    collapseButton.setText(null);
    focusOnLoggerButton.setAction(focusOnAction);
    focusOnLoggerButton.setText(null);
    ignoreLoggerButton.setAction(hideAction);
    ignoreLoggerButton.setText(null);

    expandButton.setFont(expandButton.getFont().deriveFont(Font.BOLD));
    collapseButton.setFont(collapseButton.getFont().deriveFont(Font.BOLD));

    editLoggerButton.setAction(editLoggerAction);
    editLoggerButton.setText(null);
    closeButton.setAction(closeAction);
    closeButton.setText(null);

    toolbar.add(expandButton);
    toolbar.add(collapseButton);
    toolbar.addSeparator();
    toolbar.add(focusOnLoggerButton);
    toolbar.add(ignoreLoggerButton);

    //    toolbar.add(editLoggerButton);
    toolbar.addSeparator();

    toolbar.add(Box.createHorizontalGlue());
    toolbar.add(closeButton);
    toolbar.add(Box.createHorizontalStrut(5));
  }

  /**
   * DOCUMENT ME!
   *
   * @return
  */
  private Action createClearIgnoreListAction()
  {
    Action action = new AbstractAction("Clear Ignore list", null)
      {
        public void actionPerformed(ActionEvent e)
        {
          ignoreLoggerButton.setSelected(false);
          logTreeModel.reload();
          hiddenSet.clear();
          fireChangeEvent();
        }
      };

    action.putValue(
      Action.SHORT_DESCRIPTION,
      "Removes all entries from the Ignore list so you can see their events in the view");

    return action;
  }

  /**
     * An action that closes (hides) this panel
    * @return
    */
  private Action createCloseAction()
  {
    Action action = new AbstractAction()
      {
        public void actionPerformed(ActionEvent e)
        {
            preferenceModel.setLogTreePanelVisible(false);
        }
      };

    action.putValue(Action.NAME, "Close");
    action.putValue(Action.SHORT_DESCRIPTION, "Closes the Logger panel");
    action.putValue(Action.SMALL_ICON, LineIconFactory.createCloseIcon());

    return action;
  }

  /**
   * DOCUMENT ME!
   *
   * @return
  */
  private Action createCollapseAction()
  {
    Action action = new AbstractAction()
      {
        public void actionPerformed(ActionEvent e)
        {
          collapseCurrentlySelectedNode();
        }
      };

    action.putValue(Action.SMALL_ICON, LineIconFactory.createCollapseIcon());
    action.putValue(Action.NAME, "Collapse Branch");
    action.putValue(
      Action.SHORT_DESCRIPTION,
      "Collapses all the children of the currently selected node");
    action.setEnabled(false);

    return action;
  }

  private Action createEditLoggerAction()
  {
    Action action = new AbstractAction()
      {
        public void actionPerformed(ActionEvent e)
        {
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
  private Action createExpandAction()
  {
    Action action = new AbstractAction()
      {
        public void actionPerformed(ActionEvent e)
        {
          expandCurrentlySelectedNode();
        }
      };

    action.putValue(Action.SMALL_ICON, LineIconFactory.createExpandIcon());
    action.putValue(Action.NAME, "Expand branch");
    action.putValue(
      Action.SHORT_DESCRIPTION,
      "Expands all the child nodes of the currently selected node, recursively");
    action.setEnabled(false);

    return action;
  }

  /**
   * DOCUMENT ME!
   *
   * @return
  */
  private Action createFocusOnAction()
  {
    final Action action = new AbstractAction()
      {
        public void actionPerformed(ActionEvent e)
        {
          toggleFocusOnState();
        }
      };

    action.putValue(Action.NAME, "Focus");
    action.putValue(
      Action.SHORT_DESCRIPTION,
      "Allows you to Focus on the selected logger by setting a filter that discards all but this Logger");
    action.putValue(
      Action.SMALL_ICON, new ImageIcon(ChainsawIcons.WINDOW_ICON));

    action.setEnabled(false);

    return action;
  }

  /**
   * DOCUMENT ME!
   *
   * @return
    */
  private Action createIgnoreAction()
  {
    Action action =
      new AbstractAction(
        "Ignore this Logger", new ImageIcon(ChainsawIcons.ICON_COLLAPSE))
      {
        public void actionPerformed(ActionEvent e)
        {
          String logger = getCurrentlySelectedLoggerName();

          if (logger != null)
          {
            toggleHiddenLogger(logger);
            logTreeModel.nodeChanged(
              (TreeNode) logTree.getSelectionPath().getLastPathComponent());
            ignoreLoggerButton.setSelected(hiddenSet.contains(logger));
            focusOnAction.setEnabled(!hiddenSet.contains(logger));
            popupMenu.hideCheck.setSelected(hiddenSet.contains(logger));
          }

          fireChangeEvent();
        }
      };

    action.putValue(
      Action.SHORT_DESCRIPTION,
      "Adds the selected Logger to your Ignore list, filtering those events from view");

    return action;
  }

  private void ensureRootExpanded()
  {
      LogLog.debug("Ensuring Root node is expanded.");

    final DefaultMutableTreeNode root =
      (DefaultMutableTreeNode) logTreeModel.getRoot();
    SwingUtilities.invokeLater(new Runnable()
      {
        public void run()
        {
          logTree.expandPath(new TreePath(root));
        }
      });
  }

  /**
   * Expands the currently selected node (if any)
   * including all the children.
   *
   */
  private void expandCurrentlySelectedNode()
  {
    TreePath[] paths = logTree.getSelectionPaths();

    if (paths == null)
    {
      return;
    }

      LogLog.debug("Expanding all children of selected node");

    for (int i = 0; i < paths.length; i++)
    {
      TreePath path = paths[i];

      /**
       * TODO this is commented out, right now it expands all nodes including the root, so if there is a large tree..... look out.
       */

      //      /**
      //       * Handle an expansion of the Root node by only doing the first level.
      //       * Safe...
      //       */
      //      if (path.getPathCount() == 1) {
      //        logTree.expandPath(path);
      //
      //        return;
      //      }

      DefaultMutableTreeNode treeNode =
        (DefaultMutableTreeNode) path.getLastPathComponent();

      Enumeration depthEnum = treeNode.depthFirstEnumeration();

      if (!depthEnum.hasMoreElements())
      {
        break;
      }

      List depths = new ArrayList();

      while (depthEnum.hasMoreElements())
      {
        depths.add(
          new Integer(
            ((DefaultMutableTreeNode) depthEnum.nextElement()).getDepth()));
      }

      Collections.sort(depths);
      Collections.reverse(depths);

      int maxDepth = ((Integer) depths.get(0)).intValue();

      if (maxDepth > WARN_DEPTH)
      {
        LogLog.warn("Should warn user, depth=" + maxDepth);
      }

      depthEnum = treeNode.depthFirstEnumeration();

      while (depthEnum.hasMoreElements())
      {
        DefaultMutableTreeNode node =
          (DefaultMutableTreeNode) depthEnum.nextElement();

        if (node.isLeaf())
        {
          TreeNode[] nodes =
            ((DefaultMutableTreeNode) node.getParent()).getPath();
          TreePath treePath = new TreePath(nodes);

          LogLog.debug("Expanding path:" + treePath);

          logTree.expandPath(treePath);
        }
      }
    }
  }

  private void fireChangeEvent()
  {
    ChangeListener[] listeners =
      (ChangeListener[]) listenerList.getListeners(ChangeListener.class);
    ChangeEvent e = null;

    for (int i = 0; i < listeners.length; i++)
    {
      if (e == null)
      {
        e = new ChangeEvent(this);
      }

      listeners[i].stateChanged(e);
    }
  }

  private void reconfigureMenuText()
  {
    String logger = getCurrentlySelectedLoggerName();

    if ((logger == null) || (logger.length() == 0))
    {
      focusOnAction.putValue(Action.NAME, "Focus On...");
      hideAction.putValue(Action.NAME, "Ignore ...");
    }
    else
    {
      focusOnAction.putValue(Action.NAME, "Focus On '" + logger + "'");
      hideAction.putValue(Action.NAME, "Ignore '" + logger + "'");
    }

    // need to ensure the button doens't update itself with the text, looks stupid otherwise
    focusOnLoggerButton.setText(null);
    ignoreLoggerButton.setText(null);
  }

  /**
    * Configures varoius listeners etc for the components within
    * this Class.
    */
  private void setupListeners()
  {
    logTree.addMouseMotionListener(new MouseKeyIconListener());

    /**
       * Enable the actions depending on state of the tree selection
       */
    logTree.addTreeSelectionListener(new TreeSelectionListener()
      {
        public void valueChanged(TreeSelectionEvent e)
        {
          TreePath path = e.getNewLeadSelectionPath();
          TreeNode node = null;

          if (path != null)
          {
            node = (TreeNode) path.getLastPathComponent();
          }

          //          editLoggerAction.setEnabled(path != null);
          String logger = getCurrentlySelectedLoggerName();
          focusOnAction.setEnabled(
            (path != null) && (node != null) && (node.getParent() != null)
            && !hiddenSet.contains(logger));
          hideAction.setEnabled(
            (path != null) && (node != null) && (node.getParent() != null)
            && !isFocusOnSelected());

          if (!focusOnAction.isEnabled())
          {
            setFocusOnSelected(false);
          }
          else
          {
          }

          expandAction.setEnabled(path != null);

          if (logger != null)
          {
            boolean isHidden = hiddenSet.contains(logger);
            popupMenu.hideCheck.setSelected(isHidden);
            ignoreLoggerButton.setSelected(isHidden);
          }

          collapseAction.setEnabled(path != null);

          reconfigureMenuText();
        }
      });

    logTree.addMouseListener(popupListener);

    /**
     * This listener ensures the Tool bar toggle button and popup menu check box
     * stay in sync, plus notifies all the ChangeListeners that
     * an effective filter criteria has been modified
     */
    focusOnAction.addPropertyChangeListener(new PropertyChangeListener()
      {
        public void propertyChange(PropertyChangeEvent evt)
        {
          popupMenu.focusOnCheck.setSelected(isFocusOnSelected());
          focusOnLoggerButton.setSelected(isFocusOnSelected());

          if (logTree.getSelectionPath() != null)
          {
            logTreeModel.nodeChanged(
              (TreeNode) logTree.getSelectionPath().getLastPathComponent());
          }

          fireChangeEvent();
        }
      });

    hideAction.addPropertyChangeListener(new PropertyChangeListener()
      {
        public void propertyChange(PropertyChangeEvent evt)
        {
          if (logTree.getSelectionPath() != null)
          {
            logTreeModel.nodeChanged(
              (TreeNode) logTree.getSelectionPath().getLastPathComponent());
          }

          fireChangeEvent();
        }
      });

    //    /**
    //     * Now add a MouseListener that fires the expansion
    //     * action if CTRL + DBL CLICK is done.
    //     */
    //    logTree.addMouseListener(
    //      new MouseAdapter() {
    //        public void mouseClicked(MouseEvent e) {
    //          if (
    //            (e.getClickCount() > 1)
    //              && ((e.getModifiers() & InputEvent.CTRL_MASK) > 0)
    //              && ((e.getModifiers() & InputEvent.BUTTON1_MASK) > 0)) {
    //            expandCurrentlySelectedNode();
    //            e.consume();
    //          } else if (e.getClickCount() > 1) {
    //            super.mouseClicked(e);
    //            LogLog.debug("Ignoring dbl click event " + e);
    //          }
    //        }
    //      });

    logTree.addMouseListener(new MouseFocusOnListener());

    /**
     * We listen for when the FocusOn action changes, and then  translate
     * that to a RuleChange
     */
    addChangeListener(new ChangeListener()
      {
        public void stateChanged(ChangeEvent evt)
        {
          final String currentlySelectedLoggerName =
            getCurrentlySelectedLoggerName();

          ruleDelegate =
          new AbstractRule()
            {
              public boolean evaluate(LoggingEvent e)
              {
                boolean isHidden = getHiddenSet().contains(e.getLoggerName());
                boolean result = (e.getLoggerName() != null) && (!isHidden);

                if (result && isFocusOnSelected())
                {
                  result = result &&  (e.getLoggerName().startsWith(currentlySelectedLoggerName+".") || e.getLoggerName().endsWith(currentlySelectedLoggerName)) ;
                }

                return result;
              }
            };
          firePropertyChange("rule", null, null);
        }
      });

    addPropertyChangeListener("hiddenSet", new PropertyChangeListener()
      {
        public void propertyChange(PropertyChangeEvent arg0)
        {
          DefaultListModel model = new DefaultListModel();

          List sortedIgnoreList = new ArrayList(getHiddenSet());
          Collections.sort(sortedIgnoreList);

          for (Iterator iter = sortedIgnoreList.iterator(); iter.hasNext();)
          {
            String string = (String) iter.next();
            model.addElement(string);
          }

          ignoreList.setModel(model);
        }
      });
  }

  private void toggleFocusOnState()
  {
    setFocusOnSelected(!isFocusOnSelected());
    hideAction.setEnabled(!isFocusOnSelected());
  }

  //~ Inner Classes ===========================================================

  /**
   * DOCUMENT ME!
   *
   * @author $author$
   * @version $Revision$, $Date$
   *
   * @author Paul Smith <psmith@apache.org>
        *
        */
  private class LoggerNameTreeCellRenderer extends DefaultTreeCellRenderer
  {
    //~ Constructors ==========================================================

    //    private JPanel panel = new JPanel();
    private LoggerNameTreeCellRenderer()
    {
      super();

      //      panel.setBackground(UIManager.getColor("Tree.textBackground"));
      Icon leafIcon = getDefaultLeafIcon();
      Icon icon = new ImageIcon(ChainsawIcons.WINDOW_ICON);

      //      panel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
      //      panel.add(this);
      setLeafIcon(null);
      setOpaque(false);
    }

    //~ Methods ===============================================================

    /* (non-Javadoc)
     * @see javax.swing.tree.TreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int, boolean)
     */
    /**
     * DOCUMENT ME!
     *
     * @param tree DOCUMENT ME!
     * @param value DOCUMENT ME!
     * @param sel DOCUMENT ME!
     * @param expanded DOCUMENT ME!
     * @param leaf DOCUMENT ME!
     * @param row DOCUMENT ME!
     * @param hasFocus DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Component getTreeCellRendererComponent(
      JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
      int row, boolean hasFocus)
    {
      JLabel component =
        (JLabel) super.getTreeCellRendererComponent(
          tree, value, sel, expanded, leaf, row, hasFocus);

      Font originalFont = component.getFont();

      int style = Font.PLAIN;

      if (sel && focusOnLoggerButton.isSelected())
      {
        style = style | Font.BOLD;
      }

      String logger =
        getLoggerName(
          new TreePath(((DefaultMutableTreeNode) value).getPath()));

      if (hiddenSet.contains(logger))
      {
        //        component.setEnabled(false);
        //        component.setIcon(leaf?null:getDefaultOpenIcon());
        style = style | Font.ITALIC;

        //        LogLog.debug("TreeRenderer: '" + logger + "' is in hiddenSet, italicizing");
      }
      else
      {
        //          LogLog.debug("TreeRenderer: '" + logger + "' is NOT in hiddenSet, leaving plain");
        //        component.setEnabled(true);
      }

      if (originalFont != null)
      {
        Font font2 = originalFont.deriveFont(style);

        if (font2 != null)
        {
          component.setFont(font2);
        }
      }

      return component;
    }
  }

  private class LoggerTreePopupMenu extends JPopupMenu
  {
    //~ Instance fields =======================================================

    JCheckBoxMenuItem focusOnCheck = new JCheckBoxMenuItem();
    JCheckBoxMenuItem hideCheck = new JCheckBoxMenuItem();

    //~ Constructors ==========================================================

    private LoggerTreePopupMenu()
    {
      initMenu();
    }

    //~ Methods ===============================================================

    /* (non-Javadoc)
     * @see javax.swing.JPopupMenu#show(java.awt.Component, int, int)
     */
    /**
     * DOCUMENT ME!
     *
     * @param invoker DOCUMENT ME!
     * @param x DOCUMENT ME!
     * @param y DOCUMENT ME!
     */
    public void show(Component invoker, int x, int y)
    {
      DefaultMutableTreeNode node =
        (DefaultMutableTreeNode) logTree.getLastSelectedPathComponent();

      if (node == null)
      {
        return;
      }

      super.show(invoker, x, y);
    }

    /**
     * DOCUMENT ME!
    */
    private void initMenu()
    {
      add(expandAction);
      add(collapseAction);
      addSeparator();
      focusOnCheck.setAction(focusOnAction);
      hideCheck.setAction(hideAction);
      add(focusOnCheck);
      add(hideCheck);

      //      add(editLoggerAction);
      addSeparator();
      add(clearIgnoreListAction);
    }
  }

  private final class MouseFocusOnListener extends MouseAdapter
  {
    //~ Methods ===============================================================

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     */
    public void mouseClicked(MouseEvent e)
    {
      if (
        (e.getClickCount() > 1)
          && ((e.getModifiers() & InputEvent.CTRL_MASK) > 0)
          && ((e.getModifiers() & InputEvent.SHIFT_MASK) > 0))
      {
        ignoreLoggerAtPoint(e.getPoint());
        e.consume();
        fireChangeEvent();
      }
      else if (
        (e.getClickCount() > 1)
          && ((e.getModifiers() & InputEvent.CTRL_MASK) > 0))
      {
        focusAnLoggerAtPoint(e.getPoint());
        e.consume();
        fireChangeEvent();
      }
    }

    /**
     * DOCUMENT ME!
     *
     * @param point
     */
    private void focusAnLoggerAtPoint(Point point)
    {
      String logger = getLoggerAtPoint(point);

      if (logger != null)
      {
        toggleFocusOnState();
      }
    }

    /**
     * DOCUMENT ME!
     *
     * @param point
     * @return
     */
    private String getLoggerAtPoint(Point point)
    {
      TreePath path = logTree.getPathForLocation(point.x, point.y);

      if (path != null)
      {
        return getLoggerName(path);
      }

      return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param point
     */
    private void ignoreLoggerAtPoint(Point point)
    {
      String logger = getLoggerAtPoint(point);

      if (logger != null)
      {
        toggleHiddenLogger(logger);
      }
    }
  }

  private final class MouseKeyIconListener extends MouseMotionAdapter
    implements MouseMotionListener
  {
    //~ Instance fields =======================================================

    Cursor focusOnCursor =
      Toolkit.getDefaultToolkit().createCustomCursor(
        ChainsawIcons.FOCUS_ON_ICON.getImage(), new Point(10, 10), "");
    Cursor ignoreCursor =
      Toolkit.getDefaultToolkit().createCustomCursor(
        ChainsawIcons.IGNORE_ICON.getImage(), new Point(10, 10), "");

    //~ Methods ===============================================================

    /* (non-Javadoc)
     * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
     */
    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     */
    public void mouseMoved(MouseEvent e)
    {
      //      LogLog.debug(e.toString());
      if (
        ((e.getModifiers() & InputEvent.CTRL_MASK) > 0)
          && ((e.getModifiers() & InputEvent.SHIFT_MASK) > 0))
      {
        logTree.setCursor(ignoreCursor);
      }
      else if ((e.getModifiers() & InputEvent.CTRL_MASK) > 0)
      {
        logTree.setCursor(focusOnCursor);
      }
      else
      {
        logTree.setCursor(Cursor.getDefaultCursor());
      }
    }
  }
}
