package org.apache.log4j.chainsaw.vfs;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.apache.commons.vfs.FileObject;
import org.apache.log4j.chainsaw.ChainsawConstants;
import org.apache.log4j.chainsaw.JSortTable;

/**
 * @author psmith
 *
 */
public class FileObjectTable extends JPanel {
    
    private final DirectoryListTableModel tableModel = new DirectoryListTableModel();
    private final JSortTable table = new JSortTable(tableModel);
    private final JScrollPane scrollPane = new JScrollPane(table);
    
    public FileObjectTable(){
     initGUI();   
    }


	/**
	 * 
	 */
	private void initGUI() {
		setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        table.setShowGrid(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setDefaultRenderer(Object.class, new DirectoryListCellRenderer());
        
        
	}
	/**
	 * @return Returns the tableModel.
	 */
	public final DirectoryListTableModel getTableModel() {
		return tableModel;
	}
    /**
     * Simple Row striping renderer
     */
	private static final class DirectoryListCellRenderer extends DefaultTableCellRenderer{
		
		/* (non-Javadoc)
		 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
		 */
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			Component component = super.getTableCellRendererComponent(table, value,
					isSelected, hasFocus, row, column);
            JLabel label =((JLabel)component);
			
			switch(column) {
                case 0:
                	label.setHorizontalAlignment(JLabel.LEFT);
                    break;
				default:
                    label.setHorizontalAlignment(JLabel.RIGHT);
                    break;
            }

			/**
			 * Colourize based on row striping
			 */
			if (!isSelected && (row % 2) != 0) {
				component.setBackground(ChainsawConstants.COLOR_ODD_ROW);
			} else if(!isSelected){
				component.setBackground(ChainsawConstants.COLOR_EVEN_ROW);
			}
			return component;
		}
}
    /**
     * @return
     */
    public JTable getTable() {
        return this.table;
    }


    /**
     * @param i
     * @return
     */
    public FileObject getFileObject(int row) {
        return tableModel.getFileObject(row);
    }
}
