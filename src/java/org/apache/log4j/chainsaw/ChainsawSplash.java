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
 * @author Paul Smith <psmith@apache.org>
 *
*/
package org.apache.log4j.chainsaw;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;

import org.apache.log4j.chainsaw.icons.ChainsawIcons;


/**
 * A simple splash screen to be used at startup, while everything get's initialized.
 * @author Paul Smith <psmith@apache.org>
 *
 */
class ChainsawSplash extends JWindow {
  ChainsawSplash() {
    super();

    Container container = getContentPane();
    JPanel panel = new JPanel(new BorderLayout());
    JLabel logo = new JLabel(ChainsawIcons.ICON_LOG4J);

    JLabel text = new JLabel("Chainsaw v2", JLabel.CENTER);
    Font textFont = null;
    String[] preferredFontNames =
      new String[] { "Arial", "Helvetica", "SansSerif" };

    Set availableFontNames = new HashSet();
    Font[] allFonts =
      GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();

    for (int i = 0; i < allFonts.length; i++) {
      availableFontNames.add(allFonts[i].getName());
    }

    for (int i = 0; i < preferredFontNames.length; i++) {
      if (availableFontNames.contains(preferredFontNames[i])) {
        textFont = new Font(preferredFontNames[i], Font.PLAIN, 12);

        System.out.println("Using font=" + textFont.getName());

        break;
      }
    }

    if (textFont == null) {
      System.out.println("Using basic font");
      textFont = text.getFont();
    }

    text.setFont(textFont.deriveFont(16f).deriveFont(Font.BOLD));
    text.setBackground(Color.white);
    text.setForeground(Color.black);
    text.setBorder(BorderFactory.createLoweredBevelBorder());
    panel.add(logo, BorderLayout.CENTER);
    panel.add(text, BorderLayout.SOUTH);
    panel.setBorder(BorderFactory.createLineBorder(Color.black, 1));

    container.add(panel);
    pack();
  }
}
