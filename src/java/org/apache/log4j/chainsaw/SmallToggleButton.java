package org.apache.log4j.chainsaw;

import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.SoftBevelBorder;


/**
 * A better button class that has nice roll over effects.
 *
 * This class is borrowed (quite heavily, but with modifications)
 * from the "Swing: Second Edition"
 * book by Matthew Robinson and Pavel Vorobeiv. An excellent book on Swing.
 *
 * @author Matthew Robinson
 * @author Pavel Vorobeiv
 * @author Paul Smith <psmith@apache.org>
 */
class SmallToggleButton extends JToggleButton implements ItemListener,
  MouseListener {
  protected Border m_highlighted = new SoftBevelBorder(BevelBorder.RAISED);
  protected Border m_raised = BorderFactory.createEmptyBorder();
  protected Border m_inactive = m_raised;
  protected Border m_border = m_inactive;
  protected Border m_lowered = new SoftBevelBorder(BevelBorder.LOWERED);
  protected Insets m_ins = new Insets(4, 4, 4, 4);

  public SmallToggleButton() {
    super();
    setHorizontalAlignment(SwingConstants.CENTER);
    setBorder(isSelected() ? m_lowered : m_raised);
    setMargin(m_ins);
    setRequestFocusEnabled(false);
    addItemListener(this);
    addMouseListener(this);
    setText("");
  }

  public SmallToggleButton(Action action) {
    this();
    setAction(action);
  }

  /**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  public float getAlignmentY() {
    return 0.5f;
  }

  /**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  public Border getBorder() {
    return m_border;
  }

  /**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  public Insets getInsets() {
    return m_ins;
  }

  /**
   * DOCUMENT ME!
   *
   * @param e DOCUMENT ME!
   */
  public void itemStateChanged(ItemEvent e) {
    setBorder(isSelected() ? m_lowered : m_raised);
  }

  /**
   * DOCUMENT ME!
   *
   * @param e DOCUMENT ME!
   */
  public void mouseClicked(MouseEvent e) {
  }

  /**
   * DOCUMENT ME!
   *
   * @param e DOCUMENT ME!
   */
  public void mouseEntered(MouseEvent e) {
    if (isEnabled()) {
      m_border = m_highlighted;
      setBorder(m_highlighted);
    }
  }

  /**
   * DOCUMENT ME!
   *
   * @param e DOCUMENT ME!
   */
  public void mouseExited(MouseEvent e) {
    m_border = m_inactive;
    setBorder(m_inactive);
  }

  /**
   * DOCUMENT ME!
   *
   * @param e DOCUMENT ME!
   */
  public void mousePressed(MouseEvent e) {
    m_border = m_lowered;
    setBorder(m_lowered);
  }

  /**
   * DOCUMENT ME!
   *
   * @param e DOCUMENT ME!
   */
  public void mouseReleased(MouseEvent e) {
    m_border = m_inactive;
    setBorder(m_inactive);
  }
}