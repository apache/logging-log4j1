package org.apache.log4j.chainsaw.vfs;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.log4j.helpers.LogLog;

/**=
 * Displays the file objects in a List in a TableModel.
 * @author psmith
 *
 */
public class DirectoryListTableModel extends AbstractTableModel {
	
	private final String[] COLUMN_NAMES = new String[] {"Name", "Size", "Last Modified"};
	
	private List data = new ArrayList();
	private NumberFormat nf = NumberFormat.getIntegerInstance();
	
	
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
			if (fileObject.getType().hasChildren() || !fileObject.getType().hasContent()) {
				return handleDirectory(columnIndex, fileObject);
			} else {
				return handleFile(columnIndex, fileObject);
			}
		} catch (Exception e) {
			LogLog.error("error retrieving value", e);
		}
		return "{Error}";
		
		
	}
	/**
	 * @param columnIndex
	 * @param fileObject
	 */
	private Object handleDirectory(int columnIndex, FileObject fileObject) throws FileSystemException {
		switch (columnIndex) {
			case 0 :
				return fileObject.getName().getBaseName();
			case 1 :
				return "";
			case 2 :
				return new Date(fileObject.getContent()
						.getLastModifiedTime());
		}
		return null;		
	}
	
	/**
	 * @param columnIndex
	 * @param fileObject
	 * @return
	 */
	private Object handleFile(int columnIndex, FileObject fileObject) throws FileSystemException {
		switch (columnIndex) {
			case 0 :
				return fileObject.getName().getBaseName();
			case 1 :
				return formatFileSize(fileObject.getContent().getSize());
			case 2 :
				return new Date(fileObject.getContent()
						.getLastModifiedTime());
		}
		return null;
	}
	
	/**
	 * Returns a formatted version of the file size
	 * @param l
	 * @return
	 */
	private Object formatFileSize(double size) {
		// TODO format should come from a preference model
		if(size <1024) {
			return nf.format(size);
		}else if(size < 1024*1024) {
			return nf.format((size/1024)) + "kb";   
		}else {
			return nf.format(size/(1024*1024))+"Mb";   
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	public String getColumnName(int column) {
		return COLUMN_NAMES[column];
	}
	
	/**
	 * Clears the underlying model and replaces it with the array, firing a TableDataChanged event in the process.
	 * @param objects
	 */
	public void setFiles(Collection objects) {
		this.data.clear();
		this.data.addAll(objects);
		fireTableDataChanged();
	}
	
	/**
	 * Clears the underlying model, and fires a TableDataChanged event.
	 */
	public void clear() {
		data.clear();
		fireTableDataChanged();
		
	}
	/**
	 * 
	 */
	public DirectoryListTableModel() {
		super();
		nf.setGroupingUsed(true);
		nf.setMaximumFractionDigits(2);
	}
}
