/*
 */
package org.apache.log4j.chainsaw.helper;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.table.TableCellEditor;

import org.apache.log4j.Level;

/**
 * @author Paul Smith <psmith@apache.org>
 *
 */
public class TableCellEditorFactory
{

  
  /**
   * Creates a TableCellEditor to be used for editing boolean values
   * @return TableCellEditor
   */ 
  public static final TableCellEditor createBooleanTableCellEditor() {
  
    JComboBox comboBox = new JComboBox(new Boolean[] {Boolean.TRUE, Boolean.FALSE});
    return new DefaultCellEditor(comboBox);
    
  }
  
  /**
   * 
   */
  private TableCellEditorFactory()
  {
  }

  /**
   * @return table cell editor
   */
  public static Object createLevelTableCellEditor()
  {
    JComboBox comboBox = new JComboBox(new Level[] {Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR, Level.FATAL, Level.OFF, Level.ALL});
    return new DefaultCellEditor(comboBox); 
  }

}
