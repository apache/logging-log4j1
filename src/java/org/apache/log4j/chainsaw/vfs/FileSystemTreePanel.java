package org.apache.log4j.chainsaw.vfs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.apache.commons.vfs.FileObject;
import org.apache.log4j.chainsaw.icons.ChainsawIcons;

/**
 * @author psmith
 *
 */
class FileSystemTreePanel extends JPanel {
	
	private final DefaultTreeModel treeModel;
	private final JTree tree = new JTree();
	private final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("VFS");
	
	private static final String TOOLTIP = "Displays all the known VFS repositories";
    
	FileSystemTreePanel(){
		treeModel = new DefaultTreeModel(rootNode);
		initGUI();
	}
	
	/**
	 * 
	 */
	private void initGUI() {
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(150,400));
		add(new JScrollPane(tree), BorderLayout.CENTER);

        initTree();
	}
	
	/**
	 * Configures the tree component 
	 */
	private void initTree() {
		tree.setModel(treeModel);
        tree.expandPath(new TreePath(rootNode.getPath()));
        
		tree.setRootVisible(false);
        tree.putClientProperty("JTree.lineStyle", "Angled");
        tree.setShowsRootHandles(true);
        
        tree.setCellRenderer(new FileSystemTreeCellRenderer());
	}

	/**
     * Adds a FileObject with a label to the list of known VFS repositories, and makes sure the 
     * Tree gets updated.
	 * @param fileObject
     * @return DefaultMutableTreeNode that was created
	 */
	public DefaultMutableTreeNode addFileObject(String name, FileObject fileObject) {
		VFSNode vfsNode = new VFSNode(name, fileObject);
		final DefaultMutableTreeNode node = new DefaultMutableTreeNode(vfsNode);
		SwingUtilities.invokeLater(new Runnable() {
			
			public void run() {
				rootNode.add(node);
                ((DefaultTreeModel)tree.getModel()).reload(rootNode);
                tree.makeVisible(new TreePath(node.getPath()));
			}});
        
        return node;
	}

	/**
	 * @return
	 */
	JTree getTree() {
		return this.tree;
	}
    
    /**
     * Renders the tree by making sure the appropriate icon is used
     */
    private static final class FileSystemTreeCellRenderer extends DefaultTreeCellRenderer{
        private final Icon openFolderIcon;
        private final Icon closedFolderIcon;
        private FileSystemTreeCellRenderer() {
            openFolderIcon = getOpenIcon();
            closedFolderIcon = getClosedIcon();
        }
        
		/* (non-Javadoc)
		 * @see javax.swing.tree.TreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int, boolean)
		 */
		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean sel, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {

            // start off by doing the default bits 'n pieces
            Component  c = super.getTreeCellRendererComponent(
                      tree, value, sel, expanded, leaf, row, hasFocus);


            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            Object o = node.getUserObject();
            TreePath path = new TreePath(node.getPath());
            if(!(o instanceof VFSNode)) {
             return c;   
            }
            VFSNode vfsNode = (VFSNode) o;
            setText(o.toString());

            String tooltip = vfsNode.getFileObject().getName().getBaseName();

            setIcon(null);
            // if the path to root is only 2 length, then this node is a top-level (apart from root)
            // node, and we consider this the "Repository" root node, so we use the funky Server ICON
            if(node.getParent().equals(node.getRoot())) {
               setIcon(ChainsawIcons.ICON_SERVER);
            }else {
             setIcon(tree.isExpanded(path)?openFolderIcon:closedFolderIcon);   
            }
            
			return this;
		}
    }
	
}
