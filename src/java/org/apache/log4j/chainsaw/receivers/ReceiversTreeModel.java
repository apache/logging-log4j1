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
import org.apache.log4j.plugins.PluginRegistry;
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

    Collection receivers =
      PluginRegistry.getPlugins(
        LogManager.getLoggerRepository(), Receiver.class);

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
