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

import org.apache.log4j.LogManager;
import org.apache.log4j.chainsaw.icons.ChainsawIcons;
import org.apache.log4j.net.SocketNodeEventListener;
import org.apache.log4j.net.SocketReceiver;
import org.apache.log4j.net.UDPReceiver;
import org.apache.log4j.plugins.Pauseable;
import org.apache.log4j.plugins.PluginRegistry;
import org.apache.log4j.plugins.Receiver;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;


/**
 * This panel is used to manage all the Receivers configured within Log4j
 *
 *
 * @author Paul Smith <psmith@apache.org>
 * @author Scott Debogy <sdeboy@apache.org>
 */
class ReceiversPanel extends JPanel {
  private final ReceiverToolbar buttonPanel;
  private final JTree receiversTree = new JTree();
  private final LogUI logui;
  private final Runnable updateReceiverTree;
  private final JPopupMenu popupMenu = new ReceiverPopupMenu();
  private final NewReceiverPopupMenu newReceiverPopup =
    new NewReceiverPopupMenu();
  private final Action startAllAction;
  final Action playReceiverButtonAction;
  final Action shutdownReceiverButtonAction;
  final Action pauseReceiverButtonAction;
  final Action newReceiverButtonAction;
  final Action editReceiverButtonAction;

  ReceiversPanel(final LogUI logui) {
    super(new BorderLayout());
    this.logui = logui;
    setBorder(BorderFactory.createEtchedBorder());

    setPreferredSize(new Dimension(250, 400));
    setMinimumSize(getPreferredSize());
    setMaximumSize(getPreferredSize());

    receiversTree.setModel(new ReceiversTreeModel());

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

    editReceiverButtonAction =
      new AbstractAction() {
          public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(logui, "Not Implemented yet, sorry");
          }
        };
    editReceiverButtonAction.putValue(
      Action.SMALL_ICON, new ImageIcon(ChainsawIcons.ICON_EDIT_RECEIVER));
    editReceiverButtonAction.putValue(
      Action.SHORT_DESCRIPTION,
      "Edits the configuration of the selected Receiver");
    editReceiverButtonAction.putValue(Action.NAME, "Edit Receiver");
    editReceiverButtonAction.setEnabled(false);
    editReceiverButtonAction.putValue(
      Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_E));

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

    startAllAction =
      new AbstractAction(
        "(Re)start All Receivers", new ImageIcon(ChainsawIcons.ICON_RESTART)) {
          public void actionPerformed(ActionEvent e) {
            if (
              JOptionPane.showConfirmDialog(
                  logui,
                  "This will cause any active Receiver to stop, and disconnect.  Is this ok?",
                  "Confirm", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
              new Thread(
                new Runnable() {
                  public void run() {
                    Collection allReceivers =
                      PluginRegistry.getPlugins(
                        LogManager.getLoggerRepository(), Receiver.class);

                    for (Iterator iter = allReceivers.iterator();
                        iter.hasNext();) {
                      Receiver item = (Receiver) iter.next();
                      PluginRegistry.stopPlugin(item);
                      PluginRegistry.startPlugin(item);
                    }

                    updateReceiverTreeInDispatchThread();
                    logui.getStatusBar().setMessage(
                      "All Receivers have been (re)started");
                  }
                }).start();
            }
          }
        };

    startAllAction.putValue(
      Action.SHORT_DESCRIPTION,
      "Ensures that any Receiver that isn't active, is started.");

    /**
     * We need to setup a runnable that updates the tree
     * any time a Socket event happens (opening/closing of a socket).
     *
     * We do this by installing a SocketNodeEventListener in ALL the
     * registered SocketReceivers
     */
    updateReceiverTree =
      new Runnable() {
          public void run() {
            ReceiversTreeModel model =
              (ReceiversTreeModel) receiversTree.getModel();

            model.refresh();
          }
        };

    receiversTree.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    receiversTree.setCellRenderer(new ReceiverTreeCellRenderer());

    buttonPanel = new ReceiverToolbar();
    receiversTree.addTreeSelectionListener(buttonPanel);

    PopupListener popupListener = new PopupListener(popupMenu);
    receiversTree.addMouseListener(popupListener);
    this.addMouseListener(popupListener);

    JComponent component = receiversTree;

    add(new JScrollPane(component), BorderLayout.CENTER);
    add(buttonPanel, BorderLayout.NORTH);

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
      PluginRegistry.getPlugins(
        LogManager.getLoggerRepository(), SocketReceiver.class);

    for (Iterator iter = socketReceivers.iterator(); iter.hasNext();) {
      SocketReceiver element = (SocketReceiver) iter.next();
      element.addSocketNodeEventListener(listener);
    }
  }

  /**
   * Ensures that the Receiver tree is updated with the latest information
   * and that this operation occurs in the Swing Event Dispatch thread.
   *
   */
  private void updateReceiverTreeInDispatchThread() {
    if (SwingUtilities.isEventDispatchThread()) {
      updateReceiverTree.run();
    } else {
      SwingUtilities.invokeLater(updateReceiverTree);
    }
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

          ((ReceiversTreeModel) receiversTree.getModel()).reload(node);
          updateActions();
        }
      });
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
          logui,
          "Are you sure you wish to shutdown this receiver?\n\nThis will disconnect any network resources, and remove it from the PluginRegistry.",
          "Confirm stop of Receiver", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
      new Thread(
        new Runnable() {
          public void run() {
            Receiver receiver = getCurrentlySelectedReceiver();

            if (receiver != null) {
              PluginRegistry.stopPlugin(receiver);

              updateReceiverTreeInDispatchThread();
            }
          }
        }).start();
    }
  }

  /**
   *
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
      editReceiverButtonAction.setEnabled(true);
      newReceiverButtonAction.setEnabled(true);
      shutdownReceiverButtonAction.setEnabled(true);
    } else {
      editReceiverButtonAction.setEnabled(false);
      shutdownReceiverButtonAction.setEnabled(false);
      newReceiverButtonAction.setEnabled(true);
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

      SmallButton playReceiverButton =
        new SmallButton(playReceiverButtonAction);
      playReceiverButton.setText(null);

      SmallButton pauseReceiverButton =
        new SmallButton(pauseReceiverButtonAction);
      pauseReceiverButton.setText(null);

      SmallButton shutdownReceiverButton =
        new SmallButton(shutdownReceiverButtonAction);
      shutdownReceiverButton.setText(null);

      SmallButton restartAllButton = new SmallButton(startAllAction);
      restartAllButton.setText(null);

      newReceiverButton = new SmallButton(newReceiverButtonAction);
      newReceiverButton.setText(null);
      newReceiverButton.addMouseListener(new PopupListener(newReceiverPopup));

      SmallButton editReceiverButton =
        new SmallButton(editReceiverButtonAction);
      editReceiverButton.setText(null);

      add(newReceiverButton);
      add(editReceiverButton);
      addSeparator();

      add(playReceiverButton);
      add(pauseReceiverButton);
      add(shutdownReceiverButton);

      addSeparator();
      add(restartAllButton);

      Action closeAction =
        new AbstractAction(null, new CloseIcon(8, 1, 1)) {
          public void actionPerformed(ActionEvent e) {
            logui.toggleReceiversPanel();
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

  class NewReceiverPopupMenu extends JPopupMenu {
    NewReceiverPopupMenu() {
      Class[] receivers =
        new Class[] { SocketReceiver.class, UDPReceiver.class };

      for (int i = 0; i < receivers.length; i++) {
        final Class toCreate = receivers[i];
        Package thePackage = toCreate.getPackage();
        final String name =
          toCreate.getName().substring(thePackage.getName().length() + 1);
        add(
          new AbstractAction("New " + name + "...") {
            public void actionPerformed(ActionEvent e) {
              JOptionPane.showMessageDialog(
                logui,
                "You wanted a " + name
                + " but this is not finished yet, sorry.");
            }
          });
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

    private JMenuItem createNotDoneYet() {
      final JMenuItem notDoneYet = new JMenuItem("Not Implemented Yet, sorry");
      notDoneYet.setEnabled(false);

      return notDoneYet;
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
      } else {
        buildForReceiverNode();
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
      final Action pauseReceiver =
        new AbstractAction(
          "Pause this Receiver", new ImageIcon(ChainsawIcons.PAUSE)) {
          public void actionPerformed(ActionEvent e) {
            pauseCurrentlySelectedReceiver();
          }
        };

      add(playReceiverButtonAction);
      add(pauseReceiverButtonAction);
      add(shutdownReceiverButtonAction);
      addSeparator();
      add(editReceiverButtonAction);
    }

    /**
     * Builds a relevant set of menus for when the Root node in the Receiver
     * tree has been selected
     *
     */
    private void buildForReceiversRoot() {
      JMenuItem startAll = new JMenuItem(startAllAction);

      add(newReceiverButtonAction);
      add(editReceiverButtonAction);

      addSeparator();
      add(startAll);

      final JDialog dialog = new JDialog(logui, "Set Threshold", true);
      Container container = dialog.getContentPane();
      container.add(new ThresholdSlider());
      dialog.setResizable(false);
      dialog.pack();
      

      Action setThresholdAction = new AbstractAction("Set Threshold..."){

        public void actionPerformed(ActionEvent e) {
          dialog.setLocationRelativeTo(receiversTree);
          dialog.show();    
        }};
      
      add(setThresholdAction);
    }
  }

  /**
   * A nice and simple 'X' style icon that is used to indicate a 'close' operation.
   *
   * @author Scott Deboy <sdeboy@apache.org>
   *
   */
  class CloseIcon implements Icon {
    int size;
    int xOffSet;
    int yOffSet;

    public CloseIcon(int size, int xOffSet, int yOffSet) {
      this.size = size;
      this.xOffSet = xOffSet;
      this.yOffSet = yOffSet;
    }

    public int getIconHeight() {
      return size;
    }

    public int getIconWidth() {
      return size;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
      Graphics2D g2D = (Graphics2D) g;
      g2D.setStroke(new BasicStroke(1.5f));
      g2D.setRenderingHint(
        RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
      g2D.setColor(Color.black);
      g2D.drawLine(
        x + xOffSet, y + yOffSet, x + size + xOffSet, y + size + yOffSet);
      g2D.drawLine(
        x + xOffSet, y + size + yOffSet, x + size + xOffSet, y + yOffSet);
    }
  }
}
