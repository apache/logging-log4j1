/*
 * ============================================================================
 *                   The Apache Software License, Version 1.1
 * ============================================================================
 *
 *    Copyright (C) 1999 The Apache Software Foundation. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modifica-
 * tion, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of  source code must  retain the above copyright  notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any, must
 *    include  the following  acknowledgment:  "This product includes  software
 *    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
 *    Alternately, this  acknowledgment may  appear in the software itself,  if
 *    and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "log4j" and  "Apache Software Foundation"  must not be used to
 *    endorse  or promote  products derived  from this  software without  prior
 *    written permission. For written permission, please contact
 *    apache@apache.org.
 *
 * 5. Products  derived from this software may not  be called "Apache", nor may
 *    "Apache" appear  in their name,  without prior written permission  of the
 *    Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 * APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 * DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 * ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 * (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * This software  consists of voluntary contributions made  by many individuals
 * on  behalf of the Apache Software  Foundation.  For more  information on the
 * Apache Software Foundation, please see <http://www.apache.org/>.
 *
 */

/*
 */
package org.apache.log4j.chainsaw;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;


/**
 * A dialog panel to inform the user that they do not have
 * Receiver's defined, and prompting them to either
 * load a Log4j Log file, search for a Log4j configuration file
 * or use the GUI to define the Receivers
 * 
 * @author Paul Smith
 */
class NoReceiversWarningPanel extends JPanel {
  NoReceiversWarningPanel() {
    initComponents();
  }

  /**
   *
   */
  private void initComponents() {
    setLayout(new GridBagLayout());

    GridBagConstraints gc = new GridBagConstraints();

    //    setBackground(Color.white);
    setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

    gc.gridx = 1;
    gc.fill = gc.BOTH;
    gc.weightx = 1.0;
    gc.weighty = 1.0;

    JTextArea label =
      new JTextArea(
        "You have no Receivers defined.\n\nYou will not be able to receive events from a Remote source unless you define one in the Log4J configuration file.\n");
    label.setWrapStyleWord(true);
    label.setLineWrap(true);
    label.setEditable(false);
    label.setOpaque(false);

    add(label, gc);

    gc.weightx = 0;
    gc.weighty = 0;
    gc.gridy = 2;
    add(Box.createVerticalStrut(20), gc);

    JPanel optionpanel = new JPanel();
    optionpanel.setLayout(new GridLayout(3, 1, 3, 3));
    optionpanel.setBackground(getBackground());
    optionpanel.setBorder(BorderFactory.createEtchedBorder());

    final ButtonGroup optionGroup = new ButtonGroup();

    final JRadioButton searchOption =
      new JRadioButton("Let me locate a config file", false);

    searchOption.setToolTipText(
      "Allows you to choose a Log4J Configuration file that contains Receiver definitions");

    final JRadioButton manualOption =
      new JRadioButton("Let me define Receivers manually");

    manualOption.setToolTipText(
      "Opens the Receivers panel so you can define them via a GUI");

    final JRadioButton justLoadingFile =
      new JRadioButton("Let me load a Log4j Log file");

    justLoadingFile.setToolTipText(
      "Use this if you just want to view a Log4J Log file stored somewhere");

    searchOption.setOpaque(false);
    manualOption.setOpaque(false);
    justLoadingFile.setOpaque(false);

    optionGroup.add(searchOption);
    optionGroup.add(manualOption);
    optionGroup.add(justLoadingFile);

    gc.gridy = 3;

    String[] items =
      new String[] {
        "", "c:\\blah\blah.xml", "file:///var/doobie/blah/blah.xml",
      };

    final JComboBox previousConfigs = new JComboBox(items);
    previousConfigs.setOpaque(false);
    previousConfigs.setBackground(getBackground());
    previousConfigs.setToolTipText(
      "Previously loaded configurations can be chosen here");

    final JButton searchButton =
      new JButton(
        new AbstractAction("...") {
          public void actionPerformed(ActionEvent e) {
            
//            TODO close this dialog(?) and use the file open action
          }
        });

    searchButton.setToolTipText(
      "Shows a File Open dialog to allow you to find a config file");

    ActionListener al =
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          previousConfigs.setEnabled(e.getSource() == searchOption);
          searchButton.setEnabled(e.getSource() == searchOption);
        }
      };

    searchOption.addActionListener(al);
    manualOption.addActionListener(al);
    justLoadingFile.addActionListener(al);

    justLoadingFile.doClick();

    JPanel searchOptionPanel =
      new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    searchOptionPanel.setOpaque(false);

    searchOptionPanel.add(searchOption);
    searchOptionPanel.add(Box.createHorizontalStrut(5));
    searchOptionPanel.add(previousConfigs);
    searchOptionPanel.add(Box.createHorizontalStrut(5));
    searchOptionPanel.add(searchButton);

    optionpanel.add(justLoadingFile);
    optionpanel.add(searchOptionPanel);
    optionpanel.add(manualOption);

    add(optionpanel, gc);

    final JButton okButton =
      new JButton(
        new AbstractAction("Ok") {
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
          }
        });

    gc.gridy = gc.RELATIVE;
    gc.weightx = 0;
    gc.fill = gc.NONE;
    gc.anchor = gc.SOUTHEAST;

    add(Box.createVerticalStrut(20), gc);
    add(okButton, gc);
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame();
    frame.getContentPane().add(new NoReceiversWarningPanel());
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.show();
  }
}
