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

package org.apache.log4j.chainsaw;

import org.apache.log4j.chainsaw.icons.ChainsawIcons;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;


/**
 * A general purpose status bar for all Frame windows
 *
 * @author Paul Smith <psmith@apache.org>
 *
 */
class ChainsawStatusBar extends JPanel {
  private static final int DELAY_PERIOD = 5000;
  private final String DEFAULT_MSG = "Welcome to Chainsaw v2!";
  private final JLabel statusMsg = new JLabel(DEFAULT_MSG);
  private final JLabel pausedLabel = new JLabel("", JLabel.CENTER);
  private final JLabel lineSelectionLabel = new JLabel("", JLabel.CENTER);
  private final JLabel eventCountLabel = new JLabel("", JLabel.CENTER);
  private final JLabel receivedEventLabel = new JLabel("0.0", JLabel.CENTER);
  private final JLabel receivedConnectionlabel = new JLabel("", JLabel.CENTER);
  private volatile long lastReceivedEvent = System.currentTimeMillis();
  private volatile long lastReceivedConnection = System.currentTimeMillis();
  private final Thread connectionThread;
  private final Icon pausedIcon = new ImageIcon(ChainsawIcons.PAUSE);
  private final Icon radioTowerIcon =
    new ImageIcon(ChainsawIcons.ANIM_RADIO_TOWER);
  private final Icon netConnectIcon =
    new ImageIcon(ChainsawIcons.ANIM_NET_CONNECT);
  private final NumberFormat nf = NumberFormat.getNumberInstance();
  private final Border statusBarComponentBorder =
    BorderFactory.createLineBorder(statusMsg.getBackground().darker());

  ChainsawStatusBar() {
    setLayout(new GridBagLayout());

    nf.setMaximumFractionDigits(1);
    nf.setMinimumFractionDigits(1);
    nf.setGroupingUsed(false);

    JPanel statusMsgPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));

    statusMsgPanel.add(statusMsg);
    statusMsgPanel.setBorder(statusBarComponentBorder);

    pausedLabel.setBorder(statusBarComponentBorder);
    pausedLabel.setMinimumSize(
      new Dimension(pausedIcon.getIconWidth(), pausedIcon.getIconHeight()));

    pausedLabel.setToolTipText(
      "Shows whether the current Log panel is paused or not");

    receivedEventLabel.setBorder(statusBarComponentBorder);
    receivedEventLabel.setToolTipText(
      "Indicates whether Chainsaw is receiving events, and how fast it is processing them");
    receivedEventLabel.setMinimumSize(
      new Dimension(
        receivedEventLabel.getFontMetrics(receivedEventLabel.getFont())
                          .stringWidth("999.9/s") + 5,
        (int) receivedEventLabel.getPreferredSize().getHeight()));

	eventCountLabel.setBorder(statusBarComponentBorder);
	eventCountLabel.setToolTipText("<# viewable events>:<# total events>");
	eventCountLabel.setMinimumSize(
	new Dimension(
	eventCountLabel.getFontMetrics(eventCountLabel.getFont())
						.stringWidth("9999:9999") + 5,
	  (int) eventCountLabel.getPreferredSize().getHeight()));
	  
    receivedConnectionlabel.setBorder(statusBarComponentBorder);
    receivedConnectionlabel.setToolTipText(
      "Indicates whether Chainsaw has received a remote connection");
    receivedConnectionlabel.setMinimumSize(
      new Dimension(
        netConnectIcon.getIconWidth() + 4,
        (int) receivedConnectionlabel.getPreferredSize().getHeight()));

    lineSelectionLabel.setBorder(statusBarComponentBorder);
    lineSelectionLabel.setMinimumSize(
      new Dimension(
        lineSelectionLabel.getFontMetrics(lineSelectionLabel.getFont())
                          .stringWidth("999999"),
        (int) lineSelectionLabel.getPreferredSize().getHeight()));
    lineSelectionLabel.setToolTipText(
      "The current line # selected");

    JComponent[] toFix =
      new JComponent[] {
		eventCountLabel,
        receivedConnectionlabel, lineSelectionLabel, receivedEventLabel,
        pausedLabel
      };

    for (int i = 0; i < toFix.length; i++) {
      toFix[i].setPreferredSize(toFix[i].getMinimumSize());
      toFix[i].setMaximumSize(toFix[i].getMinimumSize());
    }

    statusMsg.setMinimumSize(pausedLabel.getPreferredSize());
    statusMsg.setToolTipText("Shows messages from Chainsaw");

    GridBagConstraints c = new GridBagConstraints();
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 1.0;
    c.weighty = 1.0;
    c.ipadx = 2;
    c.ipady = 2;
    c.gridx = 0;
    c.gridy = 0;
    c.fill = GridBagConstraints.BOTH;
    c.anchor = GridBagConstraints.WEST;

    add(statusMsgPanel, c);

    c.weightx = 0.0;
    c.weighty = 0.0;
    c.gridx = 1;
    add(receivedConnectionlabel, c);

	c.weightx = 0.0;
	c.weighty = 0.0;
	c.gridx = 2;
	add(lineSelectionLabel, c);
	
	c.weightx = 0.0;
	c.weighty = 0.0;
	c.gridx = 3;
	add(eventCountLabel, c);

    c.weightx = 0.0;
    c.weighty = 0.0;
    c.gridx = 4;
    add(receivedEventLabel, c);

    c.weightx = 0.0;
    c.weighty = 0.0;
    c.gridx = 5;

    add(pausedLabel, c);

    connectionThread =
      new Thread(
        new Runnable() {
          public void run() {
            while (true) {
              try {
                Thread.sleep(DELAY_PERIOD);
              } catch (InterruptedException e) {
              }

              Icon icon = null;

              if (
                (System.currentTimeMillis() - lastReceivedConnection) < DELAY_PERIOD) {
                icon = netConnectIcon;
              }

              final Icon theIcon = icon;
              SwingUtilities.invokeLater(
                new Runnable() {
                  public void run() {
                    receivedConnectionlabel.setIcon(theIcon);
                  }
                });
            }
          }
        });
    connectionThread.start();
  }

  void setDataRate(final double dataRate) {
    SwingUtilities.invokeLater(
      new Runnable() {
        public void run() {
          receivedEventLabel.setText(nf.format(dataRate) + "/s");
        }
      });
  }

  /**
   * Indicates a new connection has been established between
   * Chainsaw and some remote host
   * @param source
   */
  void remoteConnectionReceived(String source) {
    lastReceivedConnection = System.currentTimeMillis();
    setMessage("Connection received from " + source);
    connectionThread.interrupt();

    //    TODO and maybe play a sound?
  }

  /**
   * Called when the paused state of the LogPanel has been updated
   * @param isPaused
   */
  void setPaused(final boolean isPaused) {
    Runnable runnable =
      new Runnable() {
        public void run() {
          pausedLabel.setIcon(isPaused ? pausedIcon : null);
          pausedLabel.setToolTipText(
            isPaused ? "This Log panel is currently paused"
                     : "This Log panel is not paused");
        }
      };

    SwingUtilities.invokeLater(runnable);
  }

  void setSelectedLine(
    final int selectedLine, final int lineCount, final int total) {
    SwingUtilities.invokeLater(
      new Runnable() {
        public void run() {
          lineSelectionLabel.setText(
            selectedLine+"");
			eventCountLabel.setText(lineCount + ":" + total);
        }
      });
  }

  void setNothingSelected() {
    SwingUtilities.invokeLater(
      new Runnable() {
        public void run() {
          lineSelectionLabel.setText("");
        }
      });
  }

  void clear() {
    setMessage(DEFAULT_MSG);
    setNothingSelected();
    SwingUtilities.invokeLater(
      new Runnable() {
        public void run() {
          receivedEventLabel.setText("");
        }
      });
  }

  void setMessage(final String msg) {
    SwingUtilities.invokeLater(
      new Runnable() {
        public void run() {
          statusMsg.setText(" " + msg);
        }
      });
  }
}
