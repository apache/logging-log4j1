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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Properties;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import org.apache.log4j.Category;
import org.apache.log4j.PropertyConfigurator;

/**
 * The main application.
 *
 * @author <a href="mailto:oliver@puppycrawl.com">Oliver Burn</a>
 */
public class Main
    extends JFrame
{
    /** the default port number to listen on **/
    private static final int DEFAULT_PORT = 4445;

    /** name of property for port name **/
    public static final String PORT_PROP_NAME = "chainsaw.port";

    /** Window x-position property */
    public static final String X_POSITION_PROPERTY =
        Preferences.PROP_PREFIX + ".x";
    /** Window y-position property */
    public static final String Y_POSITION_PROPERTY =
        Preferences.PROP_PREFIX + ".y";
    /** Window width property */
    public static final String WIDTH_PROPERTY =
        Preferences.PROP_PREFIX + ".width";
    /** Window height property */
    public static final String HEIGHT_PROPERTY =
        Preferences.PROP_PREFIX + ".height";
    /** Details/table separator position property */
    public static final String DETAILS_SEPARATOR_PROPERTY =
        Preferences.PROP_PREFIX + ".details.separator";

    /** use to log messages **/
    private static final Category LOG = Category.getInstance(Main.class);

    private static final Preferences PREFS = Preferences.getInstance();

    private final JSplitPane aDetailsDivider;
    private final MyTableColumnModel mColumnModel;

    /**
     * Creates a new <code>Main</code> instance.
     */
    private Main() {
        super("CHAINSAW - Log4J Log Viewer");

        ExitAction.INSTANCE.addShutdownHook(new Thread(new Shutdown()));

        // create the all important model
        final MyTableModel model = new MyTableModel();
        mColumnModel = new MyTableColumnModel(model);

        //Create the menu bar.
        final JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        final JMenu menu = new JMenu("File");
        menu.setMnemonic('F');
        menuBar.add(menu);

        try {
            final LoadXMLAction lxa = new LoadXMLAction(this, model);
            final JMenuItem loadMenuItem = new JMenuItem("Load file...");
            loadMenuItem.setMnemonic('L');
            menu.add(loadMenuItem);
            loadMenuItem.addActionListener(lxa);
        } catch (NoClassDefFoundError e) {
            LOG.info("Missing classes for XML parser", e);
            JOptionPane.showMessageDialog(
                this,
                "XML parser not in classpath - unable to load XML events.",
                "CHAINSAW",
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            LOG.info("Unable to create the action to load XML files", e);
            JOptionPane.showMessageDialog(
                this,
                "Unable to create a XML parser - unable to load XML events.",
                "CHAINSAW",
                JOptionPane.ERROR_MESSAGE);
        }

        final RecentFilesMenu recent = new RecentFilesMenu(model);
        recent.setMnemonic('R');
        menu.add(recent);
        PREFS.setRecentFilesMenu(recent);
        recent.rebuild();

        final JMenuItem prefsMenuItem = new JMenuItem("Preferences");
        prefsMenuItem.setMnemonic('P');
        menu.add(prefsMenuItem);
        prefsMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                new PreferencesDialog(Main.this, mColumnModel).show();
            }
        });

        final JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setMnemonic('x');
        menu.add(exitMenuItem);
        exitMenuItem.addActionListener(ExitAction.INSTANCE);

        // Add control panel
        final ControlPanel cp = new ControlPanel(model);
        getContentPane().add(cp, BorderLayout.NORTH);

        // Create the table
        final JTable table = new JTable(model, mColumnModel);
        table.setAutoCreateColumnsFromModel(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        final JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Events: "));
        scrollPane.setPreferredSize(new Dimension(900, 300));

        // Create the details
        final JPanel details = new DetailPanel(table, model);
        details.setPreferredSize(new Dimension(900, 100));

        // Add the table and stack trace into a splitter
        aDetailsDivider = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
            scrollPane, details);
        getContentPane().add(aDetailsDivider, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent aEvent) {
                    ExitAction.INSTANCE.actionPerformed(null);
                }
            });

        loadGuiPrefs();
        setVisible(true);

        setupReceiver(model);
    }

    /**
     * Setup recieving messages.
     *
     * @param aModel a <code>MyTableModel</code> value
     */
    private void setupReceiver(MyTableModel aModel) {
        int port = DEFAULT_PORT;
        final String strRep = System.getProperty(PORT_PROP_NAME);
        if (strRep != null) {
            try {
                port = Integer.parseInt(strRep);
            } catch (NumberFormatException nfe) {
                LOG.fatal("Unable to parse " + PORT_PROP_NAME +
                          " property with value " + strRep + ".");
                JOptionPane.showMessageDialog(
                    this,
                    "Unable to parse port number from '" + strRep +
                    "', quitting.",
                    "CHAINSAW",
                    JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        }

        try {
            final LoggingReceiver lr = new LoggingReceiver(aModel, port);
            lr.start();
        } catch (IOException e) {
            LOG.fatal("Unable to connect to socket server, quiting", e);
            JOptionPane.showMessageDialog(
                this,
                "Unable to create socket on port " + port + ", quitting.",
                "CHAINSAW",
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void loadGuiPrefs() {
        // table prefs
        mColumnModel.loadPrefs();

        final int divider = PREFS.getInteger(DETAILS_SEPARATOR_PROPERTY, -1);
        if (divider > 0) {
            aDetailsDivider.setDividerLocation(divider);
        }

        final int x = PREFS.getInteger(X_POSITION_PROPERTY, 0);
        final int y = PREFS.getInteger(Y_POSITION_PROPERTY, 0);
        setLocation(x, y);

        final int width = PREFS.getInteger(WIDTH_PROPERTY, 0);
        final int height = PREFS.getInteger(HEIGHT_PROPERTY, 0);
        if ((width > 0) && (height > 0)) {
            setSize(width, height);
        } else {
            pack();
        }
    }

    private void saveGuiPrefs() {
        mColumnModel.savePrefs();

        PREFS.setInteger(DETAILS_SEPARATOR_PROPERTY,
            aDetailsDivider.getDividerLocation());
        PREFS.setInteger(X_POSITION_PROPERTY, getX());
        PREFS.setInteger(Y_POSITION_PROPERTY, getY());
        PREFS.setInteger(WIDTH_PROPERTY, getWidth());
        PREFS.setInteger(HEIGHT_PROPERTY, getHeight());
    }


    ////////////////////////////////////////////////////////////////////////////
    // static methods
    ////////////////////////////////////////////////////////////////////////////


    /** initialise log4j **/
    private static void initLog4J() {
        final Properties props = new Properties();
        props.setProperty("log4j.rootCategory", "DEBUG, A1");
        props.setProperty("log4j.appender.A1",
                          "org.apache.log4j.ConsoleAppender");
        props.setProperty("log4j.appender.A1.layout",
                          "org.apache.log4j.TTCCLayout");
        PropertyConfigurator.configure(props);
    }

    /**
     * The main method.
     *
     * @param aArgs ignored
     */
    public static void main(String[] aArgs) {
        initLog4J();
        new Main();
    }

    private class Shutdown implements Runnable {
        public void run() {
            saveGuiPrefs();
        }
    }
}
