package org.apache.log4j.chainsaw.vfs;

/**
 * @author psmith
 *
 */
public class VFSPluginPreferenceModel {
    
    private boolean loadAllRootsOnStart = true;
    
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
}
