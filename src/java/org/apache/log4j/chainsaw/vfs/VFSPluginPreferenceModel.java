package org.apache.log4j.chainsaw.vfs;

/**
 * @author psmith
 *
 */
public class VFSPluginPreferenceModel {

//    TODO other ideas for preview preferences : "Auto Preview" and "No Auto Preview if size > X"
//    Some of these preferences might need to be VFS Repository specific
    
    private boolean loadAllRootsOnStart = true;
    private int previewSize;
    
	/**
	 * @return Returns the loadAllRootsOnStart.
	 */
	public final boolean isLoadAllRootsOnStart() {
		return loadAllRootsOnStart;
	}
	/**
	 * @param loadAllRootsOnStart The loadAllRootsOnStart to set.
	 */
	public final void setLoadAllRootsOnStart(boolean loadAllRootsOnStart) {
		this.loadAllRootsOnStart = loadAllRootsOnStart;
	}
    
    /**
     * @return
     */
    public final int getPreviewSize() {
        return this.previewSize;
    }
    
    
    /**
     * @param previewSize The previewSize to set.
     */
    public final void setPreviewSize(int previewSize) {
        this.previewSize = previewSize;
    }
}
