package org.apache.log4j.chainsaw.vfs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.vfs.FileObject;
import org.apache.log4j.helpers.LogLog;

/**
 * @author psmith
 *
 */
public class DirectoryListTableModel extends AbstractTableModel {

    private final String[] COLUMN_NAMES = new String[] {"Name", "Size", "Last Modified"};
    
    private List data = new ArrayList();
    
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
        return COLUMN_NAMES.length;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return data.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
        FileObject fileObject = (FileObject) data.get(rowIndex);
        
        try {
			switch (columnIndex) {
				case 0 :
					return fileObject.getName();
				case 1 :
					return new Long(fileObject.getContent().getSize());
				case 2 :
					return new Date(fileObject.getContent()
							.getLastModifiedTime());
			}
		} catch (Exception e) {
			LogLog.error("error determining value", e);
		}
        return "{Error}";
        
	}
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	public String getColumnName(int column) {
		return COLUMN_NAMES[column];
	}
}
