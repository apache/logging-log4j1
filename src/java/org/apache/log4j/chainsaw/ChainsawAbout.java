/*
 * Copyright 1999,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * @author Paul Smith <psmith@apache.org>
 *
*/
package org.apache.log4j.chainsaw;

import org.apache.log4j.chainsaw.icons.ChainsawIcons;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


/**
 * A simple About box telling people stuff about this project
 *
 * @author Paul Smith <psmith@apache.org>
 *
 */
class ChainsawAbout extends JDialog {
  ChainsawAbout(JFrame parent) {
    super(parent, "About Chainsaw v2", true);
//    setResizable(false);
    setBackground(Color.white);
	getContentPane().setLayout(new BorderLayout());
    JPanel panel = new JPanel(new GridBagLayout());
//    panel.setOpaque(false);
    panel.setBackground(Color.white);
    panel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

    GridBagConstraints c = new GridBagConstraints();

    c.anchor = GridBagConstraints.WEST;
    c.gridx = 0;
    c.gridy = 0;

    JLabel info =
      new JLabel("TODO");
//        TODO insert the appropriate text
    panel.add(info, c);

    JLabel title = new JLabel(ChainsawIcons.ICON_LOG4J);
    c.gridy = 1;

    panel.add(title, c);

    c.gridy = 2;
    c.anchor = GridBagConstraints.EAST;

    JButton closeButton = new JButton("Close");
    closeButton.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          setVisible(false);
        }
      });
    closeButton.setDefaultCapable(true);
    panel.add(closeButton, c);

	getContentPane().add(panel, BorderLayout.CENTER);
    pack();
    setLocationRelativeTo(parent);
  }
}
