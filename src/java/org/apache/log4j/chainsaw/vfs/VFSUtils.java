package org.apache.log4j.chainsaw.vfs;
import java.util.Comparator;

import org.apache.commons.vfs.FileObject;
/**
 * @author psmith
 *  
 */
class VFSUtils {


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