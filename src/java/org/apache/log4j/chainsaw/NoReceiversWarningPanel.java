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

import org.apache.log4j.helpers.LogLog;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import java.io.File;

import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.filechooser.FileFilter;


/**
 * A dialog panel to inform the user that they do not have
 * Receiver's defined, and prompting them to either
 * load a Log4j Log file, search for a Log4j configuration file
 * or use the GUI to define the Receivers
 *
 * @author Paul Smith
 */
class NoReceiversWarningPanel extends JPanel {
  private final JComboBox previousConfigs = new JComboBox();
  private final JRadioButton justLoadingFile =
    new JRadioButton("I'm fine thanks, don't worry");
  private final JRadioButton searchOption =
    new JRadioButton("Let me search for a configuration file");
  private final JRadioButton manualOption =
    new JRadioButton("Let me define Receivers manually");
  private final JButton okButton = new JButton("Ok");
  private final PanelModel model = new PanelModel();
  final DefaultComboBoxModel configModel = new DefaultComboBoxModel();

  NoReceiversWarningPanel() {
    initComponents();
  }

  /**
   * Returns the current Model/state of the chosen options by the user.
   * @return
   */
  PanelModel getModel() {
    return model;
  }

  /**
   * Clients of this panel can configure the ActionListener to be used
   * when the user presses the OK button, so they can read
   * back this Panel's model top determine what to do.
   * @param actionListener
   */
  void setOkActionListener(ActionListener actionListener) {
    okButton.addActionListener(actionListener);
  }

  /**
   * Sets up all the GUI components for this paenl
   */
  private void initComponents() {
    setLayout(new GridBagLayout());

    GridBagConstraints gc = new GridBagConstraints();

    setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

    gc.gridx = 1;
    gc.fill = gc.BOTH;
    gc.weightx = 1.0;
    gc.weighty = 1.0;

    JTextArea label =
      new JTextArea(
        "You will not be able to receive events from a Remote source unless you define one in the Log4J configuration file.\n");
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

    searchOption.setToolTipText(
      "Allows you to choose a Log4J Configuration file that contains Receiver definitions");

    searchOption.setMnemonic('S');

    manualOption.setToolTipText(
      "Opens the Receivers panel so you can define them via a GUI");

    manualOption.setMnemonic('m');

    justLoadingFile.setToolTipText(
      "Use this if you just want to view a Log4J Log file stored somewhere");

    justLoadingFile.setMnemonic('I');

    searchOption.setOpaque(false);
    manualOption.setOpaque(false);
    justLoadingFile.setOpaque(false);

    optionGroup.add(searchOption);
    optionGroup.add(manualOption);
    optionGroup.add(justLoadingFile);

    gc.gridy = 3;

    configModel.removeAllElements();

    previousConfigs.setModel(configModel);
    previousConfigs.setOpaque(false);
    previousConfigs.setBackground(getBackground());
    previousConfigs.setToolTipText(
      "Previously loaded configurations can be chosen here");

    previousConfigs.setEditable(true);

    previousConfigs.getModel().addListDataListener(
      new ListDataListener() {
        private void validateUrl() {
          okButton.setEnabled(isValidConfigURL());
        }

        public void contentsChanged(ListDataEvent e) {
          validateUrl();
        }

        public void intervalAdded(ListDataEvent e) {
          validateUrl();
        }

        public void intervalRemoved(ListDataEvent e) {
          validateUrl();
        }
      });

    previousConfigs.setMaximumSize(
      new Dimension(200, (int) previousConfigs.getPreferredSize().getHeight()));
    previousConfigs.setPreferredSize(previousConfigs.getMaximumSize());
    previousConfigs.getEditor().getEditorComponent().addFocusListener(
      new FocusListener() {
        public void focusGained(FocusEvent e) {
          selectAll();
        }

        private void selectAll() {
          previousConfigs.getEditor().selectAll();
        }

        public void focusLost(FocusEvent e) {
        }
      });

    final JButton searchButton =
      new JButton(
        new AbstractAction("Browse...") {
          public void actionPerformed(ActionEvent e) {
            try {
              URL url = browseForConfig();

              if (url != null) {
                getModel().configUrl = url;
                configModel.addElement(url);
                previousConfigs.getModel().setSelectedItem(url);
              }
            } catch (Exception ex) {
              LogLog.error("Error browswing for Configuration file", ex);
            }
          }
        });

    searchButton.setToolTipText(
      "Shows a File Open dialog to allow you to find a configuration file");

    /**
     * This listener activates/deactivates certain controls based on the current
     * state of the options
     */
    ActionListener al =
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          previousConfigs.setEnabled(e.getSource() == searchOption);
          searchButton.setEnabled(e.getSource() == searchOption);

          if (optionGroup.isSelected(searchOption.getModel())) {
            okButton.setEnabled(isValidConfigURL());
          } else {
            okButton.setEnabled(true);
          }
        }
      };

    searchOption.addActionListener(al);
    manualOption.addActionListener(al);
    justLoadingFile.addActionListener(al);

    justLoadingFile.doClick();

    JPanel searchOptionPanel = new JPanel(new GridBagLayout());

    searchOptionPanel.setOpaque(false);

    GridBagConstraints searchGCC = new GridBagConstraints();

    searchGCC.fill = GridBagConstraints.HORIZONTAL;
    searchGCC.gridx = 1;

    searchOptionPanel.add(searchOption, searchGCC);
    searchGCC.gridx = 2;
    searchOptionPanel.add(Box.createHorizontalStrut(5), searchGCC);

    searchGCC.weightx = 0.0;
    searchGCC.gridx = 3;
    searchGCC.fill = GridBagConstraints.NONE;
    searchOptionPanel.add(previousConfigs, searchGCC);

    searchGCC.weightx = 0.0;
    searchGCC.gridx = 4;
    searchOptionPanel.add(Box.createHorizontalStrut(5), searchGCC);
    searchGCC.gridx = 5;
    searchOptionPanel.add(searchButton, searchGCC);

    optionpanel.add(searchOptionPanel);
    optionpanel.add(manualOption);
    optionpanel.add(justLoadingFile);

    add(optionpanel, gc);

    gc.gridy = gc.RELATIVE;
    gc.weightx = 0;
    gc.fill = gc.NONE;
    gc.anchor = gc.SOUTHEAST;

    add(Box.createVerticalStrut(20), gc);

    okButton.setMnemonic('O');
    add(okButton, gc);
  }

  /**
   * Returns the URL chosen by the user for a Configuration file
   * or null if they cancelled.
   */
  private URL browseForConfig() throws MalformedURLException {
    JFileChooser chooser = new JFileChooser();
    chooser.setDialogTitle("Search for Log4j configuration...");
    chooser.setDialogType(JFileChooser.OPEN_DIALOG);
    chooser.setFileFilter(
      new FileFilter() {
        public boolean accept(File f) {
          return f.isDirectory() || f.getName().endsWith(".properties")
          || f.getName().endsWith(".xml");
        }

        public String getDescription() {
          return "Log4j Configuration file";
        }
      });

    chooser.showOpenDialog(this);

    File selectedFile = chooser.getSelectedFile();

    if (selectedFile == null) {
      return null;
    }

    if (!selectedFile.exists() || !selectedFile.canRead()) {
      return null;
    }

    return chooser.getSelectedFile().toURL();
  }

  /**
   * Determions if the Configuration URL is a valid url.
   */
  private boolean isValidConfigURL() {
    if (previousConfigs.getSelectedItem() == null) {
      return false;
    }

    String urlString = previousConfigs.getSelectedItem().toString();

    try {
      getModel().configUrl = new URL(urlString);

      return true;
    } catch (Exception ex) {
    }

    return false;
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame();
    frame.getContentPane().add(new NoReceiversWarningPanel());
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.show();
  }

  /**
   * This class represents the model of the chosen options the user
   * has configured.
   *
   */
  class PanelModel {
    private URL fileUrl;
    private URL configUrl;

    boolean isLoadLogFile() {
      return justLoadingFile.isSelected();
    }

    boolean isLoadConfig() {
      return searchOption.isSelected();
    }

    boolean isManualMode() {
      return manualOption.isSelected();
    }

    public Object[] getRememberedConfigs() {
      Object[] urls = new Object[configModel.getSize()];

      for (int i = 0; i < configModel.getSize(); i++) {
        urls[i] = configModel.getElementAt(i);
      }

      return urls;
    }

    public void setRememberedConfigs(final Object[] configs) {
      SwingUtilities.invokeLater(
        new Runnable() {
          public void run() {
            configModel.removeAllElements();

            for (int i = 0; i < configs.length; i++) {
              configModel.addElement(configs[i]);
            }
          }
        });
    }

    URL getConfigToLoad() {
      return configUrl;
    }
  }
}
