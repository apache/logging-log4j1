/*
 */
package org.apache.log4j.chainsaw;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;


/**
 * A ListCellRenderer that display a check box if the value
 * has been "checked".
 * 
 * Borrowed heavily from the excellent book "Swing, 2nd Edition" by
 * Matthew Robinson  & Pavel Vorobiev.
 * 
 * @author Paul Smith
 *
 */
public abstract class CheckListCellRenderer extends JCheckBox
  implements ListCellRenderer {
  private final Border noFocusBorder =
    BorderFactory.createEmptyBorder(1, 1, 1, 1);

  /**
   *
   */
  public CheckListCellRenderer() {
    super();
    setOpaque(true);
    setBorder(noFocusBorder);
  }

  /* (non-Javadoc)
   * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
   */
  public Component getListCellRendererComponent(
    JList list, Object value, int index, boolean isSelected,
    boolean cellHasFocus) {
    setText(value.toString());
    setBackground(
      isSelected ? list.getSelectionBackground() : list.getBackground());
    setForeground(
      isSelected ? list.getSelectionForeground() : list.getForeground());
    setFont(list.getFont());
    setBorder(
      cellHasFocus ? UIManager.getBorder("List.focusCellHighlightBorder")
                   : noFocusBorder);

    setSelected(isSelected(value));
    return this;
  }

/**
 * @param value
 * @return selected flag
 */
protected abstract boolean isSelected(Object value);
}