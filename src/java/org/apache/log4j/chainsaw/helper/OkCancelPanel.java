package org.apache.log4j.chainsaw.helper;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;


public class OkCancelPanel extends JPanel {
  private final JButton cancelButton = new JButton("Cancel");
  private final JButton okButton = new JButton("Ok");

  public OkCancelPanel() {
    setLayout(new GridBagLayout());

    cancelButton.setDefaultCapable(true);

    GridBagConstraints c = new GridBagConstraints();

    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1.0;

    add(Box.createHorizontalGlue(), c);

    c.insets = new Insets(5, 5, 5, 5);
    c.weightx = 0.0;
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.SOUTHEAST;

    add(okButton, c);
    add(cancelButton, c);
//    add(Box.createHorizontalStrut(6));
  }

  /**
   * @return Returns the cancelButton.
   */
  public final JButton getCancelButton() {
    return cancelButton;
  }

  /**
   * @return Returns the okButton.
   */
  public final JButton getOkButton() {
    return okButton;
  }

}