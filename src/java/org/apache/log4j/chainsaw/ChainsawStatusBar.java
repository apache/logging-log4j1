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

package org.apache.log4j.chainsaw;

import java.awt.Dimension;
import java.awt.FlowLayout;
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

import org.apache.log4j.chainsaw.icons.ChainsawIcons;
import org.apache.log4j.chainsaw.messages.MessageCenter;


/**
 * A general purpose status bar for all Frame windows
 *
 * @author Paul Smith <psmith@apache.org>
 *
 */
public class ChainsawStatusBar extends JPanel {
  private static final int DELAY_PERIOD = 5000;
  private static final String DEFAULT_MSG = "Welcome to Chainsaw v2!";
  private final JLabel statusMsg = new JLabel(DEFAULT_MSG);
  private final JLabel pausedLabel = new JLabel("", JLabel.CENTER);
  private final JLabel lineSelectionLabel = new JLabel("", JLabel.CENTER);
  private final JLabel eventCountLabel = new JLabel("", JLabel.CENTER);
  private final JLabel receivedEventLabel = new JLabel("0.0", JLabel.CENTER);
  private final JLabel receivedConnectionlabel = new JLabel("", JLabel.CENTER);
  private volatile long lastReceivedConnection = System.currentTimeMillis();
  private final Thread connectionThread;
  private final Icon pausedIcon = new ImageIcon(ChainsawIcons.PAUSE);
  private final Icon netConnectIcon =
    new ImageIcon(ChainsawIcons.ANIM_NET_CONNECT);
  private final NumberFormat nf = NumberFormat.getNumberInstance();
  private final Border statusBarComponentBorder =
    BorderFactory.createLineBorder(statusMsg.getBackground().darker());

  public ChainsawStatusBar() {
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
                          .stringWidth("9999.9/s") + 5,
        (int) receivedEventLabel.getPreferredSize().getHeight()));

	eventCountLabel.setBorder(statusBarComponentBorder);
	eventCountLabel.setToolTipText("<# viewable events>:<# total events>");
	eventCountLabel.setMinimumSize(
	new Dimension(
	eventCountLabel.getFontMetrics(eventCountLabel.getFont())
						.stringWidth("99999:99999") + 5,
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
    MessageCenter.getInstance().getLogger().info("Connection received from " + source);
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

  public void setMessage(final String msg) {
    SwingUtilities.invokeLater(
      new Runnable() {
        public void run() {
          statusMsg.setText(" " + msg);
        }
      });
  }
}
