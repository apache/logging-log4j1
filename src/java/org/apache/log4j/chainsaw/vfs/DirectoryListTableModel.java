package org.apache.log4j.chainsaw.vfs;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.log4j.chainsaw.SortTableModel;
import org.apache.log4j.helpers.LogLog;

/**=
 * Displays the file objects in a List in a TableModel.
 * @author psmith
 *
 */
public class DirectoryListTableModel extends AbstractTableModel implements SortTableModel{
	
	private final String[] COLUMN_NAMES = new String[] {"Name", "Size", "Last Modified"};
	
	private List data = new ArrayList();
	private NumberFormat nf = NumberFormat.getIntegerInstance();

	private int currentSortColumn;

	private boolean currentSortAscending;

	private boolean sortEnabled;
	
	
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
//		// TODO format should come from a preference model
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
        sort();
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

	/* (non-Javadoc)
	 * @see org.apache.log4j.chainsaw.SortTableModel#sortColumn(int, boolean)
	 */
	public void sortColumn(int col, boolean ascending) {
		this.currentSortColumn = col;
        this.currentSortAscending = ascending;
        this.sortEnabled = true;
        sort();
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.chainsaw.SortTableModel#sort()
	 */
	public void sort() {
           if (isSortEnabled()) {
              synchronized (data) {
                Collections.sort(
                  data,new Comparator() {

					public int compare(Object o1, Object o2) {
                        FileObject fo1 = (FileObject) o1;
                        FileObject fo2 = (FileObject) o2;
                        int sort = 0;
                        try {
							switch (currentSortColumn) {
								case 0 :
									sort = fo1
											.getName()
											.getBaseName()
											.compareTo(
													fo2.getName().getBaseName());
                                    break;
								case 1 :
									sort = new Long(fo1.getContent().getSize())
											.compareTo(new Long(fo2
													.getContent().getSize()));
                                    break;
                                case 2:
                                    sort = new Long(fo1.getContent().getLastModifiedTime()).compareTo(new Long(fo2.getContent().getLastModifiedTime()));
                                    break;
							}
						} catch (Exception e) {
                            LogLog.error("Error during sort", e);
                            sort = -1;
						}
                        sort = (currentSortAscending)?sort:-sort;
                        return sort;
					}});
              }

              fireTableRowsUpdated(0, Math.max(data.size() - 1, 0));
            }		
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.chainsaw.SortTableModel#isSortable(int)
	 */
	public boolean isSortable(int col) {
//        TODO should all columns be sorted?  I think so...
		return true;
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.chainsaw.SortTableModel#isSortEnabled()
	 */
	public boolean isSortEnabled() {
		return sortEnabled;
	}
}
