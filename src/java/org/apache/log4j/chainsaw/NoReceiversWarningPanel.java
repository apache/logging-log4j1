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

import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.net.PortBased;
import org.apache.log4j.net.SocketAppender;
import org.apache.log4j.net.SocketHubReceiver;
import org.apache.log4j.net.SocketReceiver;

import java.awt.Component;
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
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
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

    private final JRadioButton simpleReceiver = new JRadioButton(
            "Let me use a simple Receiver:");

    private final JRadioButton justLoadingFile = new JRadioButton(
            "I'm fine thanks, don't worry");
    private final JRadioButton searchOption = new JRadioButton(
            "Let me search for a configuration file");
    private final JRadioButton manualOption = new JRadioButton(
            "Let me define Receivers manually");
    private final JButton okButton = new JButton("Ok");
    private final PanelModel model = new PanelModel();
    final DefaultComboBoxModel configModel = new DefaultComboBoxModel();

    final DefaultComboBoxModel simpleReceiverModel = new DefaultComboBoxModel();
    final DefaultComboBoxModel simplePortModel = new DefaultComboBoxModel();

    private boolean dontWarnMeAgain = false;

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
        gc.fill = GridBagConstraints.BOTH;
        gc.weightx = 1.0;
        gc.weighty = 1.0;

        JTextArea label = new JTextArea(
                "You will not be able to receive events from a Remote source unless you define one in the Log4J configuration file.\n");
        label.setWrapStyleWord(true);
        label.setLineWrap(true);
        label.setEditable(false);
        label.setOpaque(false);
        label.setFont(getFont());

        add(label, gc);

        gc.weightx = 0;
        gc.weighty = 0;
        gc.gridy = 2;
        add(Box.createVerticalStrut(20), gc);

        JPanel optionpanel = new JPanel();
        optionpanel.setLayout(new GridLayout(4, 1, 3, 3));
        optionpanel.setBackground(getBackground());
        optionpanel.setBorder(BorderFactory.createEtchedBorder());

        final ButtonGroup optionGroup = new ButtonGroup();

        simpleReceiver.setToolTipText(
            "Creates one of the standard Receivers on one of the standard port");
        simpleReceiver.setMnemonic('p');

        searchOption.setToolTipText(
            "Allows you to choose a Log4J Configuration file that contains Receiver definitions");

        searchOption.setMnemonic('S');

        manualOption.setToolTipText(
            "Opens the Receivers panel so you can define them via a GUI");

        manualOption.setMnemonic('m');

        justLoadingFile.setToolTipText(
            "Use this if you just want to view a Log4J Log file stored somewhere");

        justLoadingFile.setMnemonic('I');

//    searchOption.setOpaque(false);
        manualOption.setOpaque(false);
        justLoadingFile.setOpaque(false);

        optionGroup.add(searchOption);
        optionGroup.add(manualOption);
        optionGroup.add(justLoadingFile);
        optionGroup.add(simpleReceiver);


        gc.gridy = 3;


        configModel.removeAllElements();

        previousConfigs.setModel(configModel);
        previousConfigs.setOpaque(false);
        previousConfigs.setBackground(getBackground());
        previousConfigs.setToolTipText(
            "Previously loaded configurations can be chosen here");

        previousConfigs.setEditable(true);

        previousConfigs.getModel().addListDataListener(new ListDataListener() {
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

        previousConfigs.setMaximumSize(new Dimension(200,
                (int) previousConfigs.getPreferredSize().getHeight()));
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

        final JButton searchButton = new JButton(new AbstractAction(
                    "Browse...") {
                    public void actionPerformed(ActionEvent e) {

                        try {

                            URL url = browseForConfig();

                            if (url != null) {
                                getModel().configUrl = url;
                                configModel.addElement(url);
                                previousConfigs.getModel().setSelectedItem(
                                    url);
                            }
                        } catch (Exception ex) {
                            LogLog.error(
                                "Error browswing for Configuration file", ex);
                        }
                    }
                });

        searchButton.setToolTipText(
            "Shows a File Open dialog to allow you to find a configuration file");


        simplePortModel.addElement(new PortBased() {

                private void unsupported() {
                    throw new UnsupportedOperationException(
                        "Should not be used in this context");
                }

                public String getName() {
                    unsupported();

                    return null;
                }

                public boolean isActive() {
                    unsupported();

                    return false;
                }

                public int getPort() {

                    return 4445;
                }

                public String toString() {

                    return getPort() + " (Old style/standard Chainsaw port)";
                }
            });

        simplePortModel.addElement(new PortBased() {

                private void unsupported() {
                    throw new UnsupportedOperationException(
                        "Should not be used in this context");
                }

                public String getName() {
                    unsupported();

                    return null;
                }

                public boolean isActive() {
                    unsupported();

                    return false;
                }

                public int getPort() {

                    return SocketAppender.DEFAULT_PORT;
                }

                public String toString() {

                    return getPort() + " (Default SocketAppender port)";
                }
            });

        JPanel simpleSocketPanel = new JPanel(new GridBagLayout());

        GridBagConstraints simpleSocketGC = new GridBagConstraints();

        simpleSocketPanel.add(simpleReceiver, simpleSocketGC);

        final JComboBox socketCombo = new JComboBox(simplePortModel);


        simpleReceiverModel.addElement(SocketReceiver.class);
        simpleReceiverModel.addElement(SocketHubReceiver.class);

        final JComboBox receiverCombo = new JComboBox(simpleReceiverModel);
        receiverCombo.setEditable(false);
        receiverCombo.setRenderer(new DefaultListCellRenderer() {
                public Component getListCellRendererComponent(JList list,
                    Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {

                    Component c = super.getListCellRendererComponent(list,
                            value, index, isSelected, cellHasFocus);

                    if (value instanceof Class) {

                        Class receiverClass = (Class) value;
                        JLabel cellLabel = (JLabel) c;
                        String shortenedName = receiverClass.getName()
                            .substring(
                                receiverClass.getName().lastIndexOf('.') + 1);
                        cellLabel.setText(shortenedName);
                    }

                    return c;
                }

            });

        simpleSocketPanel.add(receiverCombo);
        simpleSocketPanel.add(new JLabel(" on port "));
        simpleSocketPanel.add(socketCombo, simpleSocketGC);

        /**
         * This listener activates/deactivates certain controls based on the current
         * state of the options
         */
        ActionListener al = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    previousConfigs.setEnabled(e.getSource() == searchOption);
                    searchButton.setEnabled(e.getSource() == searchOption);
                    socketCombo.setEnabled(e.getSource() == simpleReceiver);
                    receiverCombo.setEnabled(e.getSource() == simpleReceiver);

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
        simpleReceiver.addActionListener(al);

        justLoadingFile.doClick();

        JPanel searchOptionPanel = new JPanel(new GridBagLayout());

        searchOptionPanel.setOpaque(false);

        GridBagConstraints searchGCC = new GridBagConstraints();

        searchGCC.fill = GridBagConstraints.HORIZONTAL;
        searchGCC.gridx = 1;
        searchGCC.weightx = 0.0;
        searchGCC.anchor = GridBagConstraints.WEST;
        searchOptionPanel.add(searchOption, searchGCC);

        searchGCC.fill = GridBagConstraints.NONE;
        searchGCC.weightx = 1.0;
        searchGCC.gridx = 2;
        searchOptionPanel.add(Box.createHorizontalStrut(5), searchGCC);

        searchGCC.gridx = 3;
        searchGCC.weightx = 0.0;
        searchOptionPanel.add(previousConfigs, searchGCC);

        searchGCC.weightx = 0.0;
        searchGCC.gridx = 4;
        searchOptionPanel.add(Box.createHorizontalStrut(5), searchGCC);
        searchGCC.gridx = 5;
        searchOptionPanel.add(searchButton, searchGCC);

//    searchGCC.gridx = 6;
//    searchGCC.fill = GridBagConstraints.HORIZONTAL;
//    searchGCC.weightx = 1.0;
//    searchOptionPanel.add(Box.createHorizontalGlue(), searchGCC);


        optionpanel.add(justLoadingFile);
        optionpanel.add(simpleSocketPanel);
        optionpanel.add(searchOptionPanel);
        optionpanel.add(manualOption);

        add(optionpanel, gc);

        gc.gridy = GridBagConstraints.RELATIVE;
        gc.weightx = 0;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.SOUTHEAST;

        add(Box.createVerticalStrut(20), gc);

        okButton.setMnemonic('O');

        final JCheckBox dontwarnIfNoReceiver = new JCheckBox(
                "Don't show me this again");
        JPanel okPanel = new JPanel();

        okPanel.add(dontwarnIfNoReceiver);
        okPanel.add(okButton);
        add(okPanel, gc);

        okButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    dontWarnMeAgain = dontwarnIfNoReceiver.isSelected();
                }
            });
    }

    /**
     * Returns the URL chosen by the user for a Configuration file
     * or null if they cancelled.
     */
    private URL browseForConfig() throws MalformedURLException {

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Search for Log4j configuration...");
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setFileFilter(new FileFilter() {
                public boolean accept(File f) {

                    return f.isDirectory() ||
                    f.getName().endsWith(".properties") ||
                    f.getName().endsWith(".xml");
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
     * @return Returns the dontWarnMeAgain.
     */
    public final boolean isDontWarnMeAgain() {

        return dontWarnMeAgain;
    }

    /**
     * This class represents the model of the chosen options the user
     * has configured.
     *
     */
    class PanelModel {

        private URL configUrl;

        boolean isLoadLogFile() {

            return justLoadingFile.isSelected();
        }

        boolean isSimpleReceiverMode() {

            return simpleReceiver.isSelected();
        }

        int getSimplePort() {

            return ((PortBased) simplePortModel.getSelectedItem()).getPort();
        }

        Class getSimpleReceiverClass() {

            return (Class) simpleReceiverModel.getSelectedItem();
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
            SwingUtilities.invokeLater(new Runnable() {
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
