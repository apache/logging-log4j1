package org.apache.log4j.chainsaw.vfs;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * @author psmith
 *
 */
class FileSystemTreePanel extends JPanel {
 
  private final JTree tree = new JTree();
  
  FileSystemTreePanel(){
    initGUI();
  }

  /**
   * 
   */
  private void initGUI() {
    
    tree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("VFS")));
    
    setLayout(new BorderLayout());
    
    add(tree, BorderLayout.CENTER);  }
  
}
