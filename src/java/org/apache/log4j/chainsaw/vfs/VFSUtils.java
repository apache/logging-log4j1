package org.apache.log4j.chainsaw.vfs;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.apache.commons.vfs.FileObject;
import org.apache.log4j.helpers.LogLog;
/**
 * @author psmith
 *  
 */
class VFSUtils {
	/**
	 * Given a tree node, first determine if the user object is a VFSNode, and
	 * if not, ignores the request and returns immediately. Otherwise a new,
	 * low-priority thread is started to go look for any potential children of
	 * the fileObject. As each child is located, an new child TreeNode is added
	 * to the passed in node, and done within the Swing's EventDispatchThread.
	 * 
	 * Child TreeNodes' that are added to this node will have a VFSNode as it's
	 * UserObject.
	 * 
	 * @param node
	 */
	static void lookForChildren(final JTree tree,
			final DefaultMutableTreeNode node) {
		Object object = node.getUserObject();
		if (!(object instanceof VFSNode)) {
			return;
		}
		final VFSNode vfsNode = (VFSNode) object;
		Thread thread = new Thread(new BackgroundChildFileObjectPopulator(tree,
				node, vfsNode));
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
	}
	/**
	 * A background task that scans a vfsNode for Children, and creates Child
	 * nodes for them dynamically.
	 * 
	 * @author psmith
	 *  
	 */
	private static final class BackgroundChildFileObjectPopulator
			implements
				Runnable {
		private final VFSNode vfsNode;
		private final DefaultMutableTreeNode node;
		private final JTree tree;
		/**
		 * @param node
		 * @param vfsNode
		 */
		public BackgroundChildFileObjectPopulator(JTree tree,
				DefaultMutableTreeNode node, VFSNode vfsNode) {
			this.tree = tree;
			this.node = node;
			this.vfsNode = vfsNode;
		}
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			LogLog.debug("Looking for children of node " + vfsNode.getName());
			// first, lets add a tempopary node that says "Pending..." in it
			// while we work out what's going on.
			final DefaultMutableTreeNode pendingNode = new DefaultMutableTreeNode(
					"Pending...");
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					node.add(pendingNode);
					tree.makeVisible(new TreePath(pendingNode.getPath()));
				}
			});
			try {
				List children = new ArrayList(Arrays.asList(this.vfsNode
						.getFileObject().getChildren()));
				Collections.sort(children, FILE_OBJECT_COMPARATOR);
				LogLog.debug("Found " + children.size() + " children");
				for (Iterator iter = children.iterator(); iter.hasNext();) {
					FileObject child = (FileObject) iter.next();
					// we only add non-leaf nodes, as the leaf nodes get
					// displayed in the table
					if (child.getType().hasChildren()) {
						final DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(
								new VFSNode(child.getName().getBaseName(),
										child));
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								node.add(childNode);
							}
						});
					}
				}
			} catch (Exception e) {
				// TODO feedback to the user about the error...
				LogLog.error("Failed to populate Children", e);
			} finally {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						node.remove(pendingNode);
						((DefaultTreeModel) tree.getModel()).reload(node);
					}
				});
			}
		}
	}
	private VFSUtils() {
	}
    /**
     * Compares FileObject instances by their baseName, case SENSITIVE.
     */
	static final Comparator FILE_OBJECT_COMPARATOR = new Comparator() {
		public int compare(Object o1, Object o2) {
			FileObject fo1 = (FileObject) o1;
			FileObject fo2 = (FileObject) o2;
			return fo1.getName().getBaseName().compareTo(
					fo2.getName().getBaseName());
		}
	};
}