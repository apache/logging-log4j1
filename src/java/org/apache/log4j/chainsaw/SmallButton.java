package org.apache.log4j.chainsaw;

import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
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
 *
 */
class SmallButton extends JButton implements MouseListener {
  protected Border m_inactive = new EmptyBorder(3, 3, 3, 3);
  protected Border m_border = m_inactive;
  protected Border m_lowered = new SoftBevelBorder(BevelBorder.LOWERED);
  protected Border m_raised = new SoftBevelBorder(BevelBorder.RAISED);
  protected Insets m_ins = new Insets(4, 4, 4, 4);

  public SmallButton() {
    super();
    setBorder(m_inactive);
    setMargin(m_ins);
    setRequestFocusEnabled(false);
    addMouseListener(this);
  }

  public SmallButton(Action act) {
    this();
    setAction(act);
    setRequestFocusEnabled(false);

    //      addActionListener(act);
    addMouseListener(this);
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
  public void mouseClicked(MouseEvent e) {
  }

  /**
   * DOCUMENT ME!
   *
   * @param e DOCUMENT ME!
   */
  public void mouseEntered(MouseEvent e) {
    if (isEnabled()) {
      m_border = m_raised;
      setBorder(m_raised);
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
    
    if (isEnabled()) {
      m_border = m_lowered;
      setBorder(m_lowered);
    }
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