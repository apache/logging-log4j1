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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.chainsaw.icons.ChainsawIcons;


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

    final JLabel info =
      new JLabel("<html>Chainsaw 2.0alpha<p><p>" +
        "Brought to you by the Log4J team:<p>" +
        "<b>http://logging.apache.org/log4j</b><p><p>" +
      "Bug report, mailing list and wiki information:<p>" +
      "<b>http://logging.apache.org/site/bugreport.html</b><p><p>" +
      "<b>Contributors:</b><ul><li>Scott Deboy &lt;sdeboy@apache.org&gt;</li><li>Paul Smith &lt;psmith@apache.org&gt;</li><li>Ceki G&uuml;lc&uuml; &lt;ceki@apache.org&gt;</li><li>Oliver Burn</li></ul></html>");

      JButton button = new JButton("Copy bug report link to clipboard");
      button.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent event) {
              Toolkit tk = getToolkit();
              Clipboard cb = tk.getSystemClipboard();
              cb.setContents(new StringSelection("http://logging.apache.org/site/bugreport.html"), null);
              }});
              panel.add(info, c);

    JLabel title = new JLabel(ChainsawIcons.ICON_LOG4J);
    c.gridy = 1;

    panel.add(button, c);

    c.gridy = 2;
    panel.add(title, c);

    c.gridy = 3;
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
