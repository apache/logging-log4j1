package org.apache.log4j.chainsaw.vfs;

import org.apache.commons.vfs.FileObject;

/**
 * @author psmith
 *
 */
public class VFSNode {
    
    private String name="";
    private FileObject fileObject;
    
	/**
	 * @return Returns the fileObject.
	 */
	public final FileObject getFileObject() {
		return fileObject;
	}
	/**
	 * @return Returns the name.
	 */
	public final String getName() {
		return name;
	}
    
    public String toString() {
        // TODO display name, but with Schema too
     return getName();   
    }
    
	/**
	 * @param name
	 * @param fileObject
	 */
	public VFSNode(String name, FileObject fileObject) {
		super();
		this.name = name;
		this.fileObject = fileObject;
	}
}
