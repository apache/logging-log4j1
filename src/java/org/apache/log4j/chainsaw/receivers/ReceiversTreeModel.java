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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.apache.log4j.LogManager;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.net.SocketReceiver;
import org.apache.log4j.plugins.Plugin;
import org.apache.log4j.plugins.PluginEvent;
import org.apache.log4j.plugins.PluginListener;
import org.apache.log4j.plugins.Receiver;


/**
 * A TreeModel that encapsulates the details of all the Receivers and their
 * related information in the Log4j framework
 *
 * @author Paul Smith <psmith@apache.org>
 */
public class ReceiversTreeModel extends DefaultTreeModel
  implements PluginListener {
  private static final String ROOTNODE_LABEL = "Receivers";
  final DefaultMutableTreeNode NoReceiversNode =
    new DefaultMutableTreeNode("No Receivers defined");
  final DefaultMutableTreeNode RootNode;
  private Map pluginNodeMap = new HashMap();

  ReceiversTreeModel() {
    super(new DefaultMutableTreeNode(ROOTNODE_LABEL));
    RootNode = (DefaultMutableTreeNode) getRoot();
    refresh();
  }

  /**
   * Creates a new ReceiversTreeModel by querying the Log4j Plugin Repository
   * and building up the required information.
   *
   * @return ReceiversTreeModel
   */
  public final synchronized ReceiversTreeModel refresh() {
    RootNode.removeAllChildren();

    Collection receivers = LogManager.getLoggerRepository().getPluginRegistry().getPlugins(Receiver.class);

    updateRootDisplay();

    if (receivers.size() == 0) {
      getRootNode().add(NoReceiversNode);
    } else {
      for (Iterator iter = receivers.iterator(); iter.hasNext();) {
        final Receiver item = (Receiver) iter.next();
        final DefaultMutableTreeNode receiverNode = new DefaultMutableTreeNode(item);

        item.addPropertyChangeListener(creatPluginPropertyChangeListener(item, receiverNode));
        if (item instanceof SocketReceiver) {
          for (
            Iterator iterator =
              ((SocketReceiver) item).getConnectedSocketDetails().iterator();
              iterator.hasNext();) {
            Object details = (Object) iterator.next();
            receiverNode.add(new DefaultMutableTreeNode(details));
          }
        }

        getRootNode().add(receiverNode);
      }
    }

    reload();

    return this;
  }

  private PropertyChangeListener creatPluginPropertyChangeListener(final Receiver item, final DefaultMutableTreeNode receiverNode)
  {
    return new PropertyChangeListener() {

      public void propertyChange(PropertyChangeEvent evt)
      {
        LogLog.debug(evt.toString());
        ReceiversTreeModel.this.fireTreeNodesChanged(item, receiverNode.getPath(), null, null);
        
      }};
  }

  /**
   * Ensure the Root node of this tree is updated with the latest information
   * and that listeners are notified.
   */
  void updateRootDisplay() {
    getRootNode().setUserObject(ROOTNODE_LABEL);
    nodeChanged(getRootNode());
  }

  DefaultMutableTreeNode getRootNode() {
    return (DefaultMutableTreeNode) getRoot();
  }

  /* (non-Javadoc)
   * @see org.apache.log4j.plugins.PluginListener#pluginStarted(org.apache.log4j.plugins.PluginEvent)
   */
  public void pluginStarted(PluginEvent e) {
    if (e.getPlugin() instanceof Receiver) {
      if (NoReceiversNode.getParent() == getRootNode()) {
        int index = getRootNode().getIndex(NoReceiversNode);
        getRootNode().remove(NoReceiversNode);
        nodesWereRemoved(
          getRootNode(), new int[] { index }, new Object[] { NoReceiversNode });
      }

      Receiver receiver = (Receiver) e.getPlugin();
      DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(receiver);
      getRootNode().add(newNode);
      receiver.addPropertyChangeListener(creatPluginPropertyChangeListener(receiver, newNode));
      nodesWereInserted(
        getRootNode(), new int[] { getRootNode().getIndex(newNode) });
      pluginNodeMap.put(receiver, newNode);
    }
  }

	TreeNode resolvePluginNode(Plugin p){
		return (TreeNode) pluginNodeMap.get(p);
	}
  /* (non-Javadoc)
   * @see org.apache.log4j.plugins.PluginListener#pluginStopped(org.apache.log4j.plugins.PluginEvent)
   */
  public void pluginStopped(PluginEvent e) {
    if (e.getPlugin() instanceof Receiver) {
      Receiver receiver = (Receiver) e.getPlugin();
      DefaultMutableTreeNode node =
        (DefaultMutableTreeNode) resolvePluginNode(receiver);
        if (node != null) {
            int index = getRootNode().getIndex(node);
            getRootNode().remove(node);
            nodesWereRemoved(
                getRootNode(), new int[] { index }, new Object[] { node });
            pluginNodeMap.remove(receiver);
        }

      if (getRootNode().getChildCount() == 0) {
        getRootNode().add(NoReceiversNode);

        int index = getRootNode().getIndex(NoReceiversNode);
        nodesWereInserted(getRootNode(), new int[] { index });
      }
    }
  }
}
