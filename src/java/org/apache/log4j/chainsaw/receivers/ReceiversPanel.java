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
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.chainsaw.PopupListener;
import org.apache.log4j.chainsaw.SmallButton;
import org.apache.log4j.chainsaw.help.HelpManager;
import org.apache.log4j.chainsaw.helper.SwingHelper;
import org.apache.log4j.chainsaw.icons.ChainsawIcons;
import org.apache.log4j.chainsaw.icons.LevelIconFactory;
import org.apache.log4j.chainsaw.icons.LineIconFactory;
import org.apache.log4j.chainsaw.messages.MessageCenter;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.net.SocketNodeEventListener;
import org.apache.log4j.net.SocketReceiver;
import org.apache.log4j.plugins.Pauseable;
import org.apache.log4j.plugins.Plugin;
import org.apache.log4j.plugins.PluginRegistry;
import org.apache.log4j.plugins.Receiver;


/**
 * This panel is used to manage all the Receivers configured within Log4j
 *
 *
 * @author Paul Smith <psmith@apache.org>
 * @author Scott Debogy <sdeboy@apache.org>
 */
public class ReceiversPanel extends JPanel {
  final Action newReceiverButtonAction;
  final Action pauseReceiverButtonAction;
  final Action playReceiverButtonAction;
  final Action shutdownReceiverButtonAction;
  final Action restartReceiverButtonAction;
  private final Action showReceiverHelpAction;
  private final Action startAllAction;
  private final JPopupMenu popupMenu = new ReceiverPopupMenu();
  private final JTree receiversTree = new JTree();
  private final NewReceiverPopupMenu newReceiverPopup =
    new NewReceiverPopupMenu();
  private final ReceiverToolbar buttonPanel;
  private final JSplitPane splitter = new JSplitPane();
  private final PluginPropertyEditorPanel pluginEditorPanel =
    new PluginPropertyEditorPanel();
  
  private final PluginRegistry pluginRegistry;
  

  public ReceiversPanel() {
    super(new BorderLayout());
    pluginRegistry = LogManager.getLoggerRepository().getPluginRegistry();
    final ReceiversTreeModel model = new ReceiversTreeModel();
    
    pluginRegistry.addPluginListener(model);
    receiversTree.setModel(model);

    receiversTree.setExpandsSelectedPaths(true);
    model.addTreeModelListener(
      new TreeModelListener() {
        public void treeNodesChanged(TreeModelEvent e) {
          expandRoot();
        }

        public void treeNodesInserted(TreeModelEvent e) {
          expandRoot();
        }

        public void treeNodesRemoved(TreeModelEvent e) {
          expandRoot();
        }

        public void treeStructureChanged(TreeModelEvent e) {
          expandRoot();
        }

        private void expandRoot() {
          receiversTree.expandPath(
            new TreePath(model.getPathToRoot(model.RootNode)));
        }
      });
    receiversTree.expandPath(
      new TreePath(model.getPathToRoot(model.RootNode)));

    receiversTree.addTreeWillExpandListener(
      new TreeWillExpandListener() {
        public void treeWillCollapse(TreeExpansionEvent event)
          throws ExpandVetoException {
          if (event.getPath().getLastPathComponent() == model.RootNode) {
            throw new ExpandVetoException(event);
          }
        }

        public void treeWillExpand(TreeExpansionEvent event)
          throws ExpandVetoException {
        }
      });

    receiversTree.addTreeSelectionListener(
      new TreeSelectionListener() {
        public void valueChanged(TreeSelectionEvent e) {
          TreePath path = e.getNewLeadSelectionPath();

          if (path != null) {
            DefaultMutableTreeNode node =
              (DefaultMutableTreeNode) path.getLastPathComponent();

            if (
              (node != null) && (node.getUserObject() != null)
                && (node.getUserObject() instanceof Plugin)) {
              Plugin p = (Plugin) node.getUserObject();
              LogLog.debug("plugin=" + p);
              pluginEditorPanel.setPlugin(p);
            } else {
              pluginEditorPanel.setPlugin(null);
            }
          }
        }
      });

    receiversTree.setToolTipText("Allows you to manage Log4j Receivers");
    newReceiverButtonAction =
      new AbstractAction() {
          public void actionPerformed(ActionEvent e) {
            newReceiverPopup.show(
              buttonPanel.newReceiverButton, 0,
              buttonPanel.newReceiverButton.getHeight());
          }
        };
    newReceiverButtonAction.putValue(
      Action.SMALL_ICON, new ImageIcon(ChainsawIcons.ICON_NEW_RECEIVER));
    newReceiverButtonAction.putValue(
      Action.SHORT_DESCRIPTION, "Creates and configures a new Receiver");
    newReceiverButtonAction.putValue(Action.NAME, "New Receiver");
    newReceiverButtonAction.putValue(
      Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_N));

    newReceiverButtonAction.setEnabled(true);

    playReceiverButtonAction =
      new AbstractAction() {
          public void actionPerformed(ActionEvent e) {
            playCurrentlySelectedReceiver();
          }
        };

    playReceiverButtonAction.putValue(
      Action.SHORT_DESCRIPTION, "Resumes the selected Node");
    playReceiverButtonAction.putValue(Action.NAME, "Resume");
    playReceiverButtonAction.putValue(
      Action.SMALL_ICON, new ImageIcon(ChainsawIcons.ICON_RESUME_RECEIVER));
    playReceiverButtonAction.setEnabled(false);
    playReceiverButtonAction.putValue(
      Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_R));

    pauseReceiverButtonAction =
      new AbstractAction() {
          public void actionPerformed(ActionEvent e) {
            pauseCurrentlySelectedReceiver();
          }
        };

    pauseReceiverButtonAction.putValue(
      Action.SHORT_DESCRIPTION,
      "Pause the selected Receiver.  All events received will be discarded.");
    pauseReceiverButtonAction.putValue(Action.NAME, "Pause");

    pauseReceiverButtonAction.putValue(
      Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_P));

    pauseReceiverButtonAction.putValue(
      Action.SMALL_ICON, new ImageIcon(ChainsawIcons.PAUSE));
    pauseReceiverButtonAction.setEnabled(false);

    shutdownReceiverButtonAction =
      new AbstractAction() {
          public void actionPerformed(ActionEvent e) {
            shutdownCurrentlySelectedReceiver();
          }
        };

    shutdownReceiverButtonAction.putValue(
      Action.SHORT_DESCRIPTION,
      "Shuts down the selected Receiver, and removes it from the Plugin registry");
    shutdownReceiverButtonAction.putValue(Action.NAME, "Shutdown");

    shutdownReceiverButtonAction.putValue(
      Action.SMALL_ICON, new ImageIcon(ChainsawIcons.ICON_STOP_RECEIVER));
    shutdownReceiverButtonAction.putValue(
      Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_S));

    shutdownReceiverButtonAction.setEnabled(false);
    restartReceiverButtonAction =
        new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
              Receiver selectedReceiver = getCurrentlySelectedReceiver();
              if(selectedReceiver == null){
              	return;
              }
              selectedReceiver.shutdown();
              selectedReceiver.activateOptions();
            }
          };

      restartReceiverButtonAction.putValue(
        Action.SHORT_DESCRIPTION,
        "Restarts the selected Receiver");
      restartReceiverButtonAction.putValue(Action.NAME, "Restart");

      restartReceiverButtonAction.putValue(
        Action.SMALL_ICON, new ImageIcon(ChainsawIcons.ICON_RESTART));
      restartReceiverButtonAction.putValue(
        Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_R));

      restartReceiverButtonAction.setEnabled(false);

    showReceiverHelpAction =
      new AbstractAction("Help") {
          public void actionPerformed(ActionEvent e) {
            Receiver receiver = getCurrentlySelectedReceiver();

            if (receiver != null) {
              HelpManager.getInstance().showHelpForClass(receiver.getClass());
            }
          }
        };

    showReceiverHelpAction.putValue(
      Action.SMALL_ICON, new ImageIcon(ChainsawIcons.HELP));
    showReceiverHelpAction.putValue(
      Action.SHORT_DESCRIPTION, "Displays the JavaDoc page for this Plugin");

    startAllAction =
      new AbstractAction(
        "(Re)start All Receivers", new ImageIcon(ChainsawIcons.ICON_RESTART_ALL)) {
          public void actionPerformed(ActionEvent e) {
            if (
              JOptionPane.showConfirmDialog(
                  null,
                  "This will cause any active Receiver to stop, and disconnect.  Is this ok?",
                  "Confirm", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
              new Thread(
                new Runnable() {
                  public void run() {
                    Collection allReceivers =
                        pluginRegistry.getPlugins(Receiver.class);

                    for (Iterator iter = allReceivers.iterator();
                        iter.hasNext();) {
                      Receiver item = (Receiver) iter.next();
                      item.shutdown();
                      item.activateOptions();
                    }

                    updateReceiverTreeInDispatchThread();
                    MessageCenter.getInstance().getLogger().info(
                      "All Receivers have been (re)started");
                  }
                }).start();
            }
          }
        };

    startAllAction.putValue(
      Action.SHORT_DESCRIPTION,
      "Ensures that any Receiver that isn't active, is started, and any started action is stopped, and then restarted");

    receiversTree.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    receiversTree.setCellRenderer(new ReceiverTreeCellRenderer());

    buttonPanel = new ReceiverToolbar();
    receiversTree.addTreeSelectionListener(buttonPanel);

    PopupListener popupListener = new PopupListener(popupMenu);
    receiversTree.addMouseListener(popupListener);
    this.addMouseListener(popupListener);

    JComponent component = receiversTree;
    JScrollPane pane = new JScrollPane(component);

    splitter.setOrientation(JSplitPane.VERTICAL_SPLIT);

    splitter.setTopComponent(pane);
    splitter.setBottomComponent(pluginEditorPanel);

    splitter.setResizeWeight(0.7);
    add(buttonPanel, BorderLayout.NORTH);
    add(splitter, BorderLayout.CENTER);

    /**
     * This Tree likes to be notified when Socket's are accepted so
     * we listen for them and update the Tree.
     */
    SocketNodeEventListener listener =
      new SocketNodeEventListener() {
        public void socketOpened(String remoteInfo) {
          updateReceiverTreeInDispatchThread();
        }

        public void socketClosedEvent(Exception e) {
          updateReceiverTreeInDispatchThread();
        }
      };

    /**
     * add this listener to all SocketReceivers
     */
    List socketReceivers =
      pluginRegistry.getPlugins(SocketReceiver.class);

    for (Iterator iter = socketReceivers.iterator(); iter.hasNext();) {
      SocketReceiver element = (SocketReceiver) iter.next();
      element.addSocketNodeEventListener(listener);
    }
  }

  protected ReceiversTreeModel getReceiverTreeModel() {
    return ((ReceiversTreeModel) receiversTree.getModel());
  }

  /**
   *
   */
  protected void updateCurrentlySelectedNodeInDispatchThread() {
    SwingUtilities.invokeLater(
      new Runnable() {
        public void run() {
          DefaultMutableTreeNode node =
            (DefaultMutableTreeNode) receiversTree
            .getLastSelectedPathComponent();

          if (node == null) {
            return;
          }

          getReceiverTreeModel().nodeChanged(node);
          updateActions();
        }
      });
  }

  /**
   * Returns the currently selected Receiver, or null if there is no
   * selected Receiver (this could be because a) nothing at all is selected
   * or b) a non Receiver type node is selected
   *
   * @return Receiver or null
   */
  private Receiver getCurrentlySelectedReceiver() {
    DefaultMutableTreeNode node =
      (DefaultMutableTreeNode) receiversTree.getLastSelectedPathComponent();

    if (node == null) {
      return null;
    }

    Object userObject = node.getUserObject();

    if (userObject instanceof Receiver) {
      return (Receiver) userObject;
    }

    return null;
  }

  private Receiver[] getSelectedReceivers() {
    TreePath[] paths = receiversTree.getSelectionPaths();
    Collection receivers = new ArrayList();

    for (int i = 0; i < paths.length; i++) {
      TreePath path = paths[i];
      DefaultMutableTreeNode node =
        (DefaultMutableTreeNode) path.getLastPathComponent();

      if ((node != null) && node.getUserObject() instanceof Receiver) {
        receivers.add(node.getUserObject());
      }
    }

    return (Receiver[]) receivers.toArray(new Receiver[0]);
  }

  /**
   * Returns the currently seleted node's User Object, or null
   * if there is no selected node, or if the currently selected node has
   * not user Object
   * @return Object representing currently seleted Node's User Object
   */
  private Object getCurrentlySelectedUserObject() {
    DefaultMutableTreeNode node =
      (DefaultMutableTreeNode) receiversTree.getLastSelectedPathComponent();

    if (node == null) {
      return null;
    }

    return node.getUserObject();
  }

  /**
   * Takes the currently selected Receiver and pauess it, effectively
   * discarding any received event BEFORE it is even posted to the logger
   * repository.
   *
   * The user is NOT asked to confirm this operation
   *
   */
  private void pauseCurrentlySelectedReceiver() {
    new Thread(
      new Runnable() {
        public void run() {
          Object obj = getCurrentlySelectedUserObject();

          if ((obj != null) && obj instanceof Pauseable) {
            ((Pauseable) obj).setPaused(true);
            updateCurrentlySelectedNodeInDispatchThread();
          }
        }
      }).start();
  }

  /**
   * Ensures that the currently selected receiver active property is set to
   * true
   *
   */
  private void playCurrentlySelectedReceiver() {
    new Thread(
      new Runnable() {
        public void run() {
          Object obj = getCurrentlySelectedUserObject();

          if ((obj != null) && obj instanceof Pauseable) {
            ((Pauseable) obj).setPaused(false);

            updateCurrentlySelectedNodeInDispatchThread();
          }
        }
      }).start();
  }

  /**
   * Takes the currently selected Receiver and stops it, which effectively
   * removes it from the PluginRegistry.
   *
   * The user is asked to confirm this operation
   *
   */
  private void shutdownCurrentlySelectedReceiver() {
    if (
      JOptionPane.showConfirmDialog(
          null,
          "Are you sure you wish to shutdown this receiver?\n\nThis will disconnect any network resources, and remove it from the PluginRegistry.",
          "Confirm stop of Receiver", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
      new Thread(
        new Runnable() {
          public void run() {
            Receiver[] receivers = getSelectedReceivers();

            if (receivers != null) {
              for (int i = 0; i < receivers.length; i++) {
                pluginRegistry.stopPlugin(receivers[i].getName());
              }
            }
          }
        }).start();
    }
  }

  /**
   * Sets the state of actions depending on certain conditions (i.e what is
   * currently selected etc.)
   */
  private void updateActions() {
    Object object = getCurrentlySelectedUserObject();

    if ((object != null) && object instanceof Pauseable) {
      Pauseable pauseable = (Pauseable) object;

      if (!pauseable.isPaused()) {
        pauseReceiverButtonAction.setEnabled(true);
        playReceiverButtonAction.setEnabled(false);
      } else {
        pauseReceiverButtonAction.setEnabled(false);
        playReceiverButtonAction.setEnabled(true);
      }
    } else {
      pauseReceiverButtonAction.setEnabled(false);
      playReceiverButtonAction.setEnabled(false);
    }

    if (object instanceof Receiver) {
      newReceiverButtonAction.setEnabled(true);
      shutdownReceiverButtonAction.setEnabled(true);
      restartReceiverButtonAction.setEnabled(true);
    } else {
      shutdownReceiverButtonAction.setEnabled(false);
      restartReceiverButtonAction.setEnabled(false);
    }
  }

  /**
   * Ensures that the Receiver tree is updated with the latest information
   * and that this operation occurs in the Swing Event Dispatch thread.
   *
   */
  public void updateReceiverTreeInDispatchThread() {
    LogLog.debug(
      "updateReceiverTreeInDispatchThread, should not be needed now");

    //    if (SwingUtilities.isEventDispatchThread()) {
    //      updateReceiverTree.run();
    //    } else {
    //      SwingUtilities.invokeLater(updateReceiverTree);
    //    }
  }

  /* (non-Javadoc)
   * @see java.awt.Component#setVisible(boolean)
   */
  public void setVisible(boolean aFlag) {
    boolean oldValue = isVisible();
    super.setVisible(aFlag);
    firePropertyChange("visible", oldValue, isVisible());
  }

  /**
   * A popup menu that allows the user to choose which
   * style of Receiver to create, which spawns a relevant Dialog
   * to enter the information and create the Receiver
   *
   * @author Paul Smith <psmith@apache.org>
   *
   */
  class NewReceiverPopupMenu extends JPopupMenu {
    NewReceiverPopupMenu() {
      try {
        final List receiverList =
          ReceiversHelper.getInstance().getKnownReceiverClasses();
        String separatorCheck = null;

        for (Iterator iter = receiverList.iterator(); iter.hasNext();) {
          final Class toCreate = (Class) iter.next();
          Package thePackage = toCreate.getPackage();
          final String name =
            toCreate.getName().substring(thePackage.getName().length() + 1);

          if (separatorCheck == null) {
            separatorCheck = name.substring(0, 1);
          } else {
            String current = name.substring(0, 1);

            if (!current.equals(separatorCheck)) {
              addSeparator();
              separatorCheck = current;
            }
          }

          add(
            new AbstractAction("New " + name + "...") {
              public void actionPerformed(ActionEvent e) {
                Container container = SwingUtilities.getAncestorOfClass(JFrame.class, ReceiversPanel.this);
                final JDialog dialog = new JDialog((JFrame) container,"New " + toCreate.getName() + "..." ,true);
                
                try {
                  final NewReceiverDialogPanel panel =
                    NewReceiverDialogPanel.create(toCreate);
                  dialog.getContentPane().add(panel);
                  dialog.pack();
                  SwingHelper.centerOnScreen(dialog);

                  /**
                   * Make the default button the ok button
                   */
                  dialog.getRootPane().setDefaultButton(panel.getOkPanel().getOkButton());
                  
                  /**
                   * Use the standard Cancel metaphor
                   */
                  SwingHelper.configureCancelForDialog(dialog, panel.getOkPanel().getCancelButton());
                  

                  panel.getOkPanel().getOkButton().addActionListener(
                    new ActionListener() {
                      public void actionPerformed(ActionEvent e2) {
                        dialog.dispose();
                        Plugin plugin = panel.getPlugin();
                        pluginRegistry.addPlugin(plugin);
                        plugin.activateOptions();
                        MessageCenter.getInstance().addMessage("Plugin '" + plugin.getName() + "' started");
                      }
                    });
                  dialog.show();
                } catch (Exception e1) {
                  e1.printStackTrace();
                  MessageCenter.getInstance().getLogger().error(
                    "Failed to create the new Receiver dialog", e1);
                }
              }
            });
        }
      } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException(e.getMessage());
      }
    }
  }

  /**
   * A popup menu class for when the user uses the popup trigger action
   * on a node in the Receiver tree.
   *
   * @author Paul Smith <psmith@apache.org>
   *
   */
  class ReceiverPopupMenu extends JPopupMenu {
    ReceiverPopupMenu() {
    }

    /* (non-Javadoc)
     * @see javax.swing.JPopupMenu#show(java.awt.Component, int, int)
     */
    public void show(Component invoker, int x, int y) {
      DefaultMutableTreeNode node =
        (DefaultMutableTreeNode) receiversTree.getLastSelectedPathComponent();

      if (node == null) {
        return;
      }

      Object userObject = node.getUserObject();
      removeAll();

      if (userObject == getRootOfTree().getUserObject()) {
        buildForReceiversRoot();
      } else if (getCurrentlySelectedReceiver() != null) {
        buildForReceiverNode();
      } else {
        return;
      }

      this.invalidate();
      this.validate();

      super.show(invoker, x, y);
    }

    /**
     *
     */
    private DefaultMutableTreeNode getRootOfTree() {
      return (DefaultMutableTreeNode) receiversTree.getModel().getRoot();
    }

    /**
     * Builds the popup menu with relevant items for a selected
     * Receiver node in the Tree.
     */
    private void buildForReceiverNode() {

      add(playReceiverButtonAction);
      add(pauseReceiverButtonAction);
      add(restartReceiverButtonAction);
      add(shutdownReceiverButtonAction);
      addSeparator();

      final Receiver r = getCurrentlySelectedReceiver();
      add(createLevelRadioButton(r, Level.DEBUG));
      add(createLevelRadioButton(r, Level.INFO));
      add(createLevelRadioButton(r, Level.WARN));
      add(createLevelRadioButton(r, Level.ERROR));
      addSeparator();
      add(createLevelRadioButton(r, Level.OFF));
      add(createLevelRadioButton(r, Level.ALL));
      addSeparator();
      add(showReceiverHelpAction);
    }

    private JRadioButtonMenuItem createLevelRadioButton(
      final Receiver r, final Level l) {
      Map levelIconMap = LevelIconFactory.getInstance().getLevelToIconMap();

      Action action =
        new AbstractAction(
          l.toString(), (Icon) levelIconMap.get(l.toString())) {
          public void actionPerformed(ActionEvent e) {
            if (r != null) {
              r.setThreshold(l);
              updateCurrentlySelectedNodeInDispatchThread();
            }
          }
        };

      JRadioButtonMenuItem item = new JRadioButtonMenuItem(action);
      item.setSelected(r.getThreshold() == l);

      return item;
    }

    /**
     * Builds a relevant set of menus for when the Root node in the Receiver
     * tree has been selected
     *
     */
    private void buildForReceiversRoot() {
      JMenuItem startAll = new JMenuItem(startAllAction);

      add(newReceiverButtonAction);

      addSeparator();
      add(startAll);
    }
  }

  /**
   * A simple Panel that has toolbar buttons for restarting,
   * playing, pausing, and stoping receivers
   *
   * @author Paul Smith <psmith@apache.org>
   *
   */
  private class ReceiverToolbar extends JToolBar
    implements TreeSelectionListener {
    final SmallButton newReceiverButton;

    private ReceiverToolbar() {
      setFloatable(false);

      SmallButton restartReceiverButton = new SmallButton(restartReceiverButtonAction);
      restartReceiverButton.setText(null);
      
      SmallButton shutdownReceiverButton =
        new SmallButton(shutdownReceiverButtonAction);
      shutdownReceiverButton.setText(null);

      SmallButton restartAllButton = new SmallButton(startAllAction);
      restartAllButton.setText(null);
      
      

      newReceiverButton = new SmallButton(newReceiverButtonAction);
      newReceiverButton.setText(null);
      newReceiverButton.addMouseListener(new PopupListener(newReceiverPopup));

      add(newReceiverButton);
      add(restartAllButton);

      addSeparator();

      add(restartReceiverButton);
      add(shutdownReceiverButton);

      addSeparator();

      Action closeAction =
        new AbstractAction(null, LineIconFactory.createCloseIcon()) {
          public void actionPerformed(ActionEvent e) {
            ReceiversPanel.this.setVisible(false);
          }
        };

      closeAction.putValue(
        Action.SHORT_DESCRIPTION, "Closes the Receiver panel");

      add(Box.createHorizontalGlue());

      add(new SmallButton(closeAction));

      add(Box.createHorizontalStrut(5));
    }

    /**
     * Ensures the enabled property of the actions is set properly
     * according to the currently selected node in the tree
     */
    public void valueChanged(TreeSelectionEvent e) {
      updateActions();
    }
  }
}
