package org.apache.log4j.chainsaw.vfs;

import java.awt.BorderLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JSplitPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.SystemInfo;
import org.apache.commons.vfs.VFS;
import org.apache.commons.vfs.impl.StandardFileSystemManager;
import org.apache.log4j.chainsaw.plugins.GUIPluginSkeleton;
import org.apache.log4j.helpers.LogLog;

/**
 * GUI interface to the Jarkata Commons VFS project.
 *
 * This is currently a Work In Progress
 *  
 * @see http://jakarta.apache.org/commons/
 *  
 * @author psmith
 *
 */
public class VFSPlugin extends GUIPluginSkeleton {
	
	
	private final FileSystemTreePanel fileSystemTree = new FileSystemTreePanel();
	private final FileObjectTable fileObjectTable = new FileObjectTable();
	private final JSplitPane splitPane = new JSplitPane();
    
	private StandardFileSystemManager fileSystemManager;
    
    private final AOFileTablePopulater aoTablePopulator = new AOFileTablePopulater(fileObjectTable.getTableModel());
	
	private Set supportedSchemes = new HashSet();
	
	public VFSPlugin() {
		setName("VFS");
		initGUI();
	}
    
	/* (non-Javadoc)
	 * @see org.apache.log4j.plugins.Plugin#shutdown()
	 */
	public void shutdown() {
        if(fileSystemManager!=null) {
         fileSystemManager.close();   
        }
	}
	
	/* (non-Javadoc)
	 * @see org.apache.log4j.spi.OptionHandler#activateOptions()
	 */
	public void activateOptions() {
		try {
			this.fileSystemManager = (StandardFileSystemManager) VFS.getManager();
			
		} catch (FileSystemException e) {
			LogLog.error("Failed to initialise VFS", e);
			e.printStackTrace();
			setActive(false);
			return;
		}
		
		determineSupportedFileSystems();
		loadLocalFileSystem();
		setActive(true);
	}
	
	/**
	 * Ensures that there is at least a Local FileSystem with the Current directory loaded.
     * 
     * TODO This probably shouldn't be here after we've completed all the VFS preference loading stuff.
     * 
	 */
	private void loadLocalFileSystem() {
		try {
			FileObject fileObject = this.fileSystemManager.resolveFile(new File("").toURL().toExternalForm());
			
			DefaultMutableTreeNode node = this.fileSystemTree.addFileObject("local", fileObject);
            
            VFSUtils.lookForChildren(this.fileSystemTree.getTree(), node);
            
		} catch (Exception e) {
			LogLog.error("error creating local VFS",e);
		}
		
	}
	/**
	 * Works out which of the supported File Systems are available.
	 */
	private void determineSupportedFileSystems() {
        SystemInfo info = fileSystemManager.getSystemInfo();
        String[] schemes = info.getSchemes();
        supportedSchemes.addAll(Arrays.asList(schemes));
        
        LogLog.info("Supported schemes: " + supportedSchemes);
	}

    /**
	 * 
	 */
	private void initGUI() {
		
		setLayout(new BorderLayout());

        splitPane.add(this.fileSystemTree, JSplitPane.LEFT);
        splitPane.add(this.fileObjectTable, JSplitPane.RIGHT);
		add(splitPane, BorderLayout.CENTER);
        
        fileSystemTree.getTree().addTreeSelectionListener(this.aoTablePopulator);
	}
	
	
    /**
     * Triggered when the user selects a node in the tree, it automatically populates all the child node 
     * information in the table
     * 
     * @author psmith
     *
     */
    private static class AOFileTablePopulater implements TreeSelectionListener{

        private DirectoryListTableModel tableModel;
        
        /**
         * @param tableModel
         */
        public AOFileTablePopulater(DirectoryListTableModel tableModel) {
            this.tableModel = tableModel;
        }
        
		/* (non-Javadoc)
		 * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
		 */
		public void valueChanged(TreeSelectionEvent e) {
			Object object = e.getSource();
			TreePath path = e.getNewLeadSelectionPath();
            // if there is no path, then there is nothing selected, so we need to clear the table model... that's it!
            if(path == null) {
              this.tableModel.clear();
              return;
            }
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) path.getLastPathComponent();
            
            Object userObject = treeNode.getUserObject();
            if(!(userObject instanceof VFSNode)) {
             LogLog.error("User Object of selected item is not a VFSNode, ignoring");   
             return;
            }
            final VFSNode vfsNode = (VFSNode) userObject;
            
            // IN a background thread, we now populate the children in the tableModel
            Runnable runnable = new Runnable() {

				public void run() {
                    try {
                        FileObject[] fos = vfsNode.getFileObject().getChildren();
                        Collection objects = new ArrayList(Arrays.asList(fos));
                        for (Iterator iter = objects.iterator(); iter.hasNext();) {
							FileObject fo = (FileObject) iter.next();
							if(fo.getType().hasChildren()) {
								iter.remove();
                            }
						}
                        tableModel.setFiles(objects);
                    } catch (FileSystemException ex) {
                        LogLog.error("Failed to retrieve children for " + vfsNode,ex);
                        return;
                    }
				}};
                
            Thread thread = new Thread(runnable);
            thread.setPriority(Thread.MIN_PRIORITY);
            thread.start();
            
		}
        
    }
}
