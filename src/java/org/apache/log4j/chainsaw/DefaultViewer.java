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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;


/**
 * The default viewer.
 *
 * @author <a href="mailto:oliver@puppycrawl.com">Oliver Burn</a>
 */
public class DefaultViewer extends JFrame implements ChainsawViewer {
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
  private static final Preferences PREFS = Preferences.getInstance();
  private JSplitPane aDetailsDivider;
  private MyTableColumnModel mColumnModel;

  /**
   * Creates a new <code>Main</code> instance.
   */
  public DefaultViewer() {
    super("CHAINSAW - Log4J Log Viewer");
  }

  public void activateViewer(ChainsawAppender model) {
    ExitAction.INSTANCE.addShutdownHook(new Thread(new Shutdown()));

    // create the all important models
    mColumnModel = new MyTableColumnModel(model);

    buildMenus(model);
    buildComponents(model);

    addWindowListener(
      new WindowAdapter() {
        public void windowClosing(WindowEvent aEvent) {
          ExitAction.INSTANCE.actionPerformed(null);
        }
      });

    loadGuiPrefs();
    setVisible(true);
  }
  /**
   * Constructs the JTable used for displaying the Events logs
   * @param tableModel
   * @param tableColumnModel
   * @return
   */
  private JTable buildTable(
    TableModel tableModel, TableColumnModel tableColumnModel) {
    final JTable table = new JTable(tableModel, mColumnModel);
    table.setAutoCreateColumnsFromModel(true);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    return table;
  }

  /**
   * Constructs all the components required for this frame
   * and attaches the ChainsawAppender to components that require it
   * @param model
   */
  private void buildComponents(ChainsawAppender model) {
    // Add control panel
    final ControlPanel cp = new ControlPanel(model.getWrappedModel());
    getContentPane().add(cp, BorderLayout.NORTH);

    // Create the table
    final JTable table = buildTable(model, mColumnModel);
    final JScrollPane scrollPane = new JScrollPane(table);
    scrollPane.setBorder(BorderFactory.createTitledBorder("Events: "));
    scrollPane.setPreferredSize(new Dimension(900, 300));

    // Create the details
    final JPanel details = new DetailPanel(table, model.getWrappedModel());
    details.setPreferredSize(new Dimension(900, 100));

    // Add the table and stack trace into a splitter
    aDetailsDivider =
      new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPane, details);
    getContentPane().add(aDetailsDivider, BorderLayout.CENTER);
  }

  /**
   * Initialises the Menu bar for this frame, and bind
   * actions
   * @param eventSink
   */
  private void buildMenus(EventDetailSink eventSink) {
    //Create the menu bar.
    final JMenuBar menuBar = new JMenuBar();
    setJMenuBar(menuBar);

    final JMenu menu = new JMenu("File");
    menu.setMnemonic('F');
    menuBar.add(menu);

    try {
      final LoadXMLAction lxa = new LoadXMLAction(this, eventSink);
      final JMenuItem loadMenuItem = new JMenuItem("Load file...");
      loadMenuItem.setMnemonic('L');
      menu.add(loadMenuItem);
      loadMenuItem.addActionListener(lxa);
    } catch (NoClassDefFoundError e) {
      System.err.println("Missing classes for XML parser :" + e);
      JOptionPane.showMessageDialog(
        this, "XML parser not in classpath - unable to load XML events.",
        "CHAINSAW", JOptionPane.ERROR_MESSAGE);
    } catch (Exception e) {
      System.err.println(
        "Unable to create the action to load XML files:" + e.getMessage());
      JOptionPane.showMessageDialog(
        this, "Unable to create a XML parser - unable to load XML events.",
        "CHAINSAW", JOptionPane.ERROR_MESSAGE);
    }

    final RecentFilesMenu recent = new RecentFilesMenu(eventSink);
    recent.setMnemonic('R');
    menu.add(recent);
    PREFS.setRecentFilesMenu(recent);
    recent.rebuild();

    final JMenuItem prefsMenuItem = new JMenuItem("Preferences");
    prefsMenuItem.setMnemonic('P');
    menu.add(prefsMenuItem);
    prefsMenuItem.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
          new PreferencesDialog(DefaultViewer.this, mColumnModel).show();
        }
      });

    final JMenuItem exitMenuItem = new JMenuItem("Exit");
    exitMenuItem.setMnemonic('x');
    menu.add(exitMenuItem);
    exitMenuItem.addActionListener(ExitAction.INSTANCE);
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

    PREFS.setInteger(
      DETAILS_SEPARATOR_PROPERTY, aDetailsDivider.getDividerLocation());
    PREFS.setInteger(X_POSITION_PROPERTY, getX());
    PREFS.setInteger(Y_POSITION_PROPERTY, getY());
    PREFS.setInteger(WIDTH_PROPERTY, getWidth());
    PREFS.setInteger(HEIGHT_PROPERTY, getHeight());
  }

  ////////////////////////////////////////////////////////////////////////////
  // static methods
  ////////////////////////////////////////////////////////////////////////////
  private class Shutdown implements Runnable {
    public void run() {
      saveGuiPrefs();
    }
  }
}
