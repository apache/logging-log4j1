package org.apache.log4j.chainsaw.vfs;

import java.awt.BorderLayout;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JSplitPane;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
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
	
	private Set supportedSchemes = new HashSet();
	
	public VFSPlugin() {
		setName("VFS");
		initGUI();
	}
    
	/* (non-Javadoc)
	 * @see org.apache.log4j.plugins.Plugin#shutdown()
	 */
	public void shutdown() {
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
     * This probably shouldn't be here after we've completed all the VFS preference loading stuff.
     * 
	 */
	private void loadLocalFileSystem() {
		try {
			FileObject fileObject = this.fileSystemManager.createVirtualFileSystem(new File("").toURL().toExternalForm());
			
			this.fileSystemTree.addFileObject("local", fileObject);
		} catch (Exception e) {
			LogLog.error("error creating local VFS",e);
		}
		
	}
	/**
	 * Works out which of the supported File Systems are available.
	 */
	private void determineSupportedFileSystems() {
//		TODO This seems really lame to have to do this, but there you go...
		String[] schemes = new String[] {"file","zip", "jar", "sftp", "http", "https", "ftp", "CIFS" };
		for (int i = 0; i < schemes.length; i++) {
			String scheme = schemes[i];
			try {
				if(fileSystemManager.hasProvider(scheme)) {
					supportedSchemes.add(scheme);
					LogLog.info("VFS scheme '" + scheme + "' supported");
				}else {
					LogLog.error("VFS scheme '" + scheme + "' NOT supported");
				}
			} catch (Exception e) {
				LogLog.error("Failed test for VFS scheme '" + scheme + "'", e);
			}
		}
	}
	/**
	 * 
	 */
	private void initGUI() {
		
		setLayout(new BorderLayout());

        splitPane.add(this.fileSystemTree, JSplitPane.LEFT);
        splitPane.add(this.fileObjectTable, JSplitPane.RIGHT);
		add(splitPane, BorderLayout.CENTER);
	}
	
	
}
