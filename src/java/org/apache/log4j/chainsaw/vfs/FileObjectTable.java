package org.apache.log4j.chainsaw.vfs;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 * @author psmith
 *
 */
public class FileObjectTable extends JPanel {
    
    private final DirectoryListTableModel tableModel = new DirectoryListTableModel();
    private final JTable table = new JTable(tableModel);
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
	}
	/**
	 * @return Returns the tableModel.
	 */
	public final DirectoryListTableModel getTableModel() {
		return tableModel;
	}
}
