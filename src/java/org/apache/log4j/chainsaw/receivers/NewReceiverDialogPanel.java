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

package org.apache.log4j.chainsaw.receivers;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URL;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.UIManager;

import org.apache.log4j.LogManager;
import org.apache.log4j.chainsaw.help.HelpManager;
import org.apache.log4j.chainsaw.helper.OkCancelPanel;
import org.apache.log4j.chainsaw.messages.MessageCenter;
import org.apache.log4j.net.SocketHubReceiver;
import org.apache.log4j.plugins.Plugin;
import org.apache.log4j.plugins.Receiver;


/**
 * A panel that allows a user to configure a new Plugin, and
 * view that plugins javadoc at the same time
 *
 * @author Paul Smith <psmith@apache.org>
 *
 */
public class NewReceiverDialogPanel extends JPanel {

    private PluginPropertyEditorPanel pluginEditorPanel =
        new PluginPropertyEditorPanel();
    private final OkCancelPanel okPanel = new OkCancelPanel();
    private final JEditorPane javaDocPane = new JEditorPane();
    private final JScrollPane javaDocScroller = new JScrollPane(javaDocPane);
    private final JSplitPane splitter = new JSplitPane();

    private NewReceiverDialogPanel() {
        setupComponents();
        setupListeners();
    }

    /**
     *
     */
    private void setupListeners() {

        /**
         * We listen for the plugin change, and modify the editor panes
         * url to be the Help resource for that class
         */
        pluginEditorPanel.addPropertyChangeListener("plugin",
            new PropertyChangeListener() {

                public void propertyChange(PropertyChangeEvent evt) {

                    Plugin plugin = (Plugin) evt.getNewValue();
                    URL url = HelpManager.getInstance().getHelpForClass(
                            plugin.getClass());

                    try {
                        javaDocPane.setPage(url);
                    } catch (IOException e) {
                        MessageCenter.getInstance().getLogger().error(
                            "Failed to load the Help resource for " +
                            plugin.getClass(), e);
                    }
                }
            });
    }

    /**
     *
     */
    private void setupComponents() {
        setLayout(new BorderLayout());

        setupJavadoc();

        setupPluginPropertyPanel();

        setupSplitter();

        add(splitter, BorderLayout.CENTER);
        add(okPanel, BorderLayout.SOUTH);
        setMinimumSize(new Dimension(600, 600));
        setPreferredSize(getMinimumSize());
    }

    private void setupPluginPropertyPanel() {
        pluginEditorPanel.setMinimumSize(new Dimension(320, 160));
        pluginEditorPanel.setPreferredSize(pluginEditorPanel.getMinimumSize());
    }

    private void setupSplitter() {
        splitter.setTopComponent(javaDocScroller);
        splitter.setBottomComponent(pluginEditorPanel);
        splitter.setResizeWeight(0.8);
        splitter.setOrientation(JSplitPane.VERTICAL_SPLIT);
    }

    private void setupJavadoc() {
        javaDocPane.setEditable(false);
    }

    /**
     * Creates a new panel, with the contents configured to allow the editing
     * of a NEW instance of the specified class (which must implement the Receiver
     * interface)
     * @param receiverClass
     * @return NewReceiverDialogPanel
     * @throws IllegalArgumentException if the specified class is not a Receiver
     */
    public static NewReceiverDialogPanel create(Class receiverClass) {

        if (!Receiver.class.isAssignableFrom(receiverClass)) {
            throw new IllegalArgumentException(receiverClass.getName() +
                " is not a Receiver");
        }

        Receiver receiverInstance = null;

        try {
            receiverInstance = (Receiver) receiverClass.newInstance();

        } catch (Exception e) {
        	LogManager.getLogger(NewReceiverDialogPanel.class).error(
                "Failed to create a new Receiver instance, this exception is unexpected",
                e);
        }

        NewReceiverDialogPanel panel = new NewReceiverDialogPanel();

        panel.pluginEditorPanel.setPlugin(receiverInstance);

        return panel;
    }

    public static void main(String[] args) throws Exception {

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        NewReceiverDialogPanel panel = NewReceiverDialogPanel.create(
                SocketHubReceiver.class);

        JDialog dialog = new JDialog((JFrame) null, true);
        dialog.getContentPane().add(panel);

        ActionListener al = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.exit(1);
                }
            };

        panel.okPanel.getOkButton().addActionListener(al);
        panel.okPanel.getCancelButton().addActionListener(al);

        dialog.pack();

        dialog.show();
    }

    /**
     * @return Returns the okPanel.
     */
    public final OkCancelPanel getOkPanel() {

        return okPanel;
    }

    /**
     *
     */
    public Plugin getPlugin() {

        return this.pluginEditorPanel.getPlugin();
    }

}
