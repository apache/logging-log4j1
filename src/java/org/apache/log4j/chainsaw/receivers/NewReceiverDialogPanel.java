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

package org.apache.log4j.chainsaw.receivers;

import org.apache.log4j.chainsaw.help.HelpManager;
import org.apache.log4j.chainsaw.helper.OkCancelPanel;
import org.apache.log4j.chainsaw.messages.MessageCenter;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.net.SocketHubReceiver;
import org.apache.log4j.plugins.Plugin;
import org.apache.log4j.plugins.Receiver;

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
     * @return
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
            LogLog.error(
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
