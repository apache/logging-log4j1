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

import org.apache.log4j.HTMLLayout;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Priority;
import org.apache.log4j.UtilLoggingLevel;
import org.apache.log4j.chainsaw.icons.ChainsawIcons;
import org.apache.log4j.chainsaw.prefs.LoadSettingsEvent;
import org.apache.log4j.chainsaw.prefs.SaveSettingsEvent;
import org.apache.log4j.chainsaw.prefs.SettingsListener;
import org.apache.log4j.chainsaw.prefs.SettingsManager;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.net.SocketNodeEventListener;
import org.apache.log4j.net.SocketReceiver;
import org.apache.log4j.plugins.PluginRegistry;
import org.apache.log4j.plugins.Receiver;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import java.net.URL;

import java.text.NumberFormat;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;


/**
 * The main entry point for Chainsaw, this class represents the first frame
 * that is used to display a Welcome panel, and any other panels that
 * are generated because Logging Events are streamed via a Receiver, or other
 * mechanism.
 *
 * If a system property 'chainsaw.usecyclicbuffer' is set to 'true', each panel will use a cyclic
 * buffer for displaying events and once events reach the buffer limit, the oldest events
 * are removed from the table.
 *
 * If the property is not provided, there is no limit on the table's buffer size.
 *
 * If 'chainsaw.usecyclicbuffer' is set to 'true' and a system
 * property 'chainsaw.cyclicbuffersize' is set to some integer value, that value will
 * be used as the buffer size - if the buffersize is not provided, a default
 * size of 500 is used.
 *
 *
 * @author Scott Deboy <sdeboy@apache.org>
 * @author Paul Smith <psmith@apache.org>
 *
 */
public class LogUI extends JFrame implements ChainsawViewer, SettingsListener {
  private static final String CONFIG_FILE_TO_USE = "config.file";
  private static final String USE_CYCLIC_BUFFER_PROP_NAME =
    "chainsaw.usecyclicbuffer";
  private static final String CYCLIC_BUFFER_SIZE_PROP_NAME =
    "chainsaw.cyclicbuffersize";
  private static final String MAIN_WINDOW_HEIGHT = "main.window.height";
  private static final String MAIN_WINDOW_WIDTH = "main.window.width";
  private static final String MAIN_WINDOW_Y = "main.window.y";
  private static final String MAIN_WINDOW_X = "main.window.x";
  private static final String TABLE_COLUMN_ORDER = "table.columns.order";
  private static final String TABLE_COLUMN_WIDTHS = "table.columns.widths";
  private static final String LOOK_AND_FEEL = "LookAndFeel";
  private static final String STATUS_BAR = "StatusBar";
  private static final String COLUMNS_EXTENSION = ".columns";
  private static ChainsawSplash splash;
  private URL configURLToUse;
  private boolean noReceiversDefined;
  private ReceiversPanel receiversPanel;
  ChainsawTabbedPane tabbedPane;
  private JToolBar toolbar;
  private ChainsawStatusBar statusBar;
  private final Map tableModelMap = new HashMap();
  private final Map tableMap = new HashMap();

  //  final List pausedList = new Vector();
  private final List filterableColumns = new ArrayList();
  private final Map entryMap = new HashMap();
  private final Map panelMap = new HashMap();
  private final Map scrollMap = new HashMap();
  private final Map levelMap = new HashMap();
  ChainsawAppenderHandler handler;
  private ChainsawToolBarAndMenus tbms;
  private ChainsawAbout aboutBox;
  private final SettingsManager sm = SettingsManager.getInstance();
  private String lookAndFeelClassName;
  private final NoReceiversWarningPanel noReceiversWarningPanel =
    new NoReceiversWarningPanel();

  /**
   * Set to true, if and only if the GUI has completed
   * it's full initialization.  Any logging events
   * that come in must wait until this is true, and
   * if it is false, should wait on the initializationLock
   * object until notified.
   */
  private boolean isGUIFullyInitialized = false;
  private Object initializationLock = new Object();

  /**
   * The shutdownAction is called when the user requests to exit
   * Chainsaw, and by default this exits the VM, but
   * a developer may replace this action with something that better suits
   * their needs
   */
  private Action shutdownAction =
    new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        System.exit(0);
      }
    };

  /**
   * Clients can register a ShutdownListener to be notified
   * when the user has requested Chainsaw to exit.
   */
  private EventListenerList shutdownListenerList = new EventListenerList();

  /**
   * Constructor which builds up all the visual elements of the frame
   * including the Menu bar
   */
  public LogUI() {
    super("Chainsaw v2 - Log Viewer");

    if (ChainsawIcons.WINDOW_ICON != null) {
      setIconImage(new ImageIcon(ChainsawIcons.WINDOW_ICON).getImage());
    }
  }

  private static final void showSplash() {
    splash = new ChainsawSplash();

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    splash.setLocation(
      (screenSize.width / 2) - (splash.getWidth() / 2),
      (screenSize.height / 2) - (splash.getHeight() / 2));

    splash.setVisible(true);
  }

  private static final void removeSplash() {
    if (splash != null) {
      splash.setVisible(false);
      splash.dispose();
    }
  }

  /**
   * Registers a ShutdownListener with this calss so that
   * it can be notified when the user has requested
   * that Chainsaw exit.
   *
   * @param l
   */
  public void addShutdownListener(ShutdownListener l) {
    shutdownListenerList.add(ShutdownListener.class, l);
  }

  /**
   * Removes the registered ShutdownListener so
   * that the listener will not be notified on a shutdown.
   *
   * @param l
   */
  public void removeShutdownListener(ShutdownListener l) {
    shutdownListenerList.remove(ShutdownListener.class, l);
  }

  /**
   * Starts Chainsaw by attaching a new instance to the Log4J
   * main root Logger via a ChainsawAppender, and activates itself
   * @param args
   */
  public static void main(String[] args) {

//    TODO remove this when ready
    JOptionPane.showMessageDialog(
      null,
      "Chainsaw v2 is currently going through some refactoring work at present.\n\n" +
      "Some features, most notably filtering and colouring, may be inoperable at this time.\n\n" +
      "The Log4J Dev team apologises for this inconvenience, but be assured this functionality will be back very shortly.", "Apologise", JOptionPane.WARNING_MESSAGE);

    showSplash();

    LogUI logUI = new LogUI();

    logUI.handler = new ChainsawAppenderHandler();
    logUI.handler.addEventBatchListener(logUI.new NewTabEventBatchReceiver());
    LogManager.getRootLogger().addAppender(logUI.handler);
    logUI.activateViewer();
  }

  public void activateViewer(ChainsawAppender appender) {
    handler = new ChainsawAppenderHandler(appender);
    handler.addEventBatchListener(new NewTabEventBatchReceiver());
    activateViewer();
  }

  /**
   * Initialises the menu's and toolbars, but does not actually
   * create any of the main panel components.
   *
   */
  private void initGUI() {
    statusBar = new ChainsawStatusBar();
    receiversPanel = new ReceiversPanel(this);
    tbms = new ChainsawToolBarAndMenus(this);
    toolbar = tbms.getToolbar();
    setJMenuBar(tbms.getMenubar());
    tabbedPane = new ChainsawTabbedPane();
    tabbedPane.addChangeListener(tbms.getPanelListener());
  }

  /**
   * Given the load event, configures the size/location of the main window
   * etc etc.
   */
  public void loadSettings(LoadSettingsEvent event) {
    if (event.asBoolean(LogUI.STATUS_BAR)) {
      addStatusBar();
    } else {
      removeStatusBar();
    }

    setLocation(
      event.asInt(LogUI.MAIN_WINDOW_X), event.asInt(LogUI.MAIN_WINDOW_Y));
    setSize(
      event.asInt(LogUI.MAIN_WINDOW_WIDTH),
      event.asInt(LogUI.MAIN_WINDOW_HEIGHT));

    tbms.stateChange();
  }

  /**
   * Ensures the location/size of the main window is stored with the settings
   */
  public void saveSettings(SaveSettingsEvent event) {
    event.saveSetting(LogUI.MAIN_WINDOW_X, (int) getLocation().getX());
    event.saveSetting(LogUI.MAIN_WINDOW_Y, (int) getLocation().getY());

    event.saveSetting(LogUI.MAIN_WINDOW_WIDTH, getWidth());
    event.saveSetting(LogUI.MAIN_WINDOW_HEIGHT, getHeight());

    if (lookAndFeelClassName != null) {
      event.saveSetting(LogUI.LOOK_AND_FEEL, lookAndFeelClassName);
    }

    event.saveSetting(
      LogUI.STATUS_BAR, isStatusBarVisible() ? Boolean.TRUE : Boolean.FALSE);

    if (configURLToUse != null) {
      event.saveSetting(LogUI.CONFIG_FILE_TO_USE, configURLToUse.toString());
    }
  }

  /**
   * Activates itself as a viewer by configuring Size, and location of
   * itself, and configures the default Tabbed Pane elements with the correct
   * layout, table columns, and sets itself viewable.
   */
  public void activateViewer() {
    sm.configure(
      new SettingsListener() {
        public void loadSettings(LoadSettingsEvent event) {
          lookAndFeelClassName = event.getSetting(LogUI.LOOK_AND_FEEL);

          if (lookAndFeelClassName != null) {
            applyLookAndFeel(lookAndFeelClassName);
          }
        }

        public void saveSettings(SaveSettingsEvent event) {
          //required because of SettingsListener interface..not used during load
        }
      });

    sm.configure(
      new SettingsListener() {
        public void loadSettings(LoadSettingsEvent event) {
          String configFile = event.getSetting(LogUI.CONFIG_FILE_TO_USE);

          //if both a config file are defined and a log4j.configuration property are set,  
          //don't use configFile's configuration
          if (
            (configFile != null) && !configFile.trim().equals("")
              && (System.getProperty("log4j.configuration") == null)) {
            try {
              URL url = new URL(configFile);
              OptionConverter.selectAndConfigure(
                url, null, LogManager.getLoggerRepository());

              if (LogUI.this.getStatusBar() != null) {
                LogUI.this.getStatusBar().setMessage(
                  "Configured Log4j using remembered URL :: " + url);
              }

              LogUI.this.configURLToUse = url;
            } catch (Exception e) {
              LogLog.error("error occurred initializing log4j", e);
            }
          }
        }

        public void saveSettings(SaveSettingsEvent event) {
          //required because of SettingsListener interface..not used during load
        }
      });

    /**
     * This listener sets up the NoReciversWarningPanel and
     * loads saves the configs/logfiles
     */
    sm.addSettingsListener(
      new SettingsListener() {
        public void loadSettings(LoadSettingsEvent event) {
          int size = event.asInt("SavedConfigs.Size");
          Object[] configs = new Object[size];

          for (int i = 0; i < size; i++) {
            configs[i] = event.getSetting("SavedConfigs." + i);
          }

          noReceiversWarningPanel.getModel().setRememberedConfigs(configs);
        }

        public void saveSettings(SaveSettingsEvent event) {
          Object[] configs =
            noReceiversWarningPanel.getModel().getRememberedConfigs();
          event.saveSetting("SavedConfigs.Size", configs.length);

          for (int i = 0; i < configs.length; i++) {
            event.saveSetting("SavedConfigs." + i, configs[i].toString());
          }
        }
      });

    if (
      PluginRegistry.getPlugins(
          LogManager.getLoggerRepository(), Receiver.class).size() == 0) {
      noReceiversDefined = true;
    }

    initGUI();

    /**
     * Get all the SocketReceivers and configure a new SocketNodeEventListener
     * so we can get notified of new Sockets
     */
    List list =
      PluginRegistry.getPlugins(
        LogManager.getLoggerRepository(), SocketReceiver.class);
    final SocketNodeEventListener socketListener =
      new SocketNodeEventListener() {
        public void socketOpened(String remoteInfo) {
          statusBar.remoteConnectionReceived(remoteInfo);
        }

        public void socketClosedEvent(Exception e) {
          statusBar.setMessage("Collection lost! :: " + e.getMessage());
        }
      };

    for (Iterator iter = list.iterator(); iter.hasNext();) {
      SocketReceiver item = (SocketReceiver) iter.next();
      LogLog.debug("Adding listener for " + item.getName());
      item.addSocketNodeEventListener(socketListener);
    }

    List utilList = UtilLoggingLevel.getAllPossibleLevels();

    // TODO: Replace the array list creating with the standard way of retreiving the Level set. (TBD)
    Priority[] priorities =
      new Level[] { Level.FATAL, Level.ERROR, Level.WARN, Level.INFO, Level.DEBUG };
    List priorityLevels = new ArrayList();

    for (int i = 0; i < priorities.length; i++) {
      priorityLevels.add(priorities[i].toString());
    }

    List utilLevels = new ArrayList();

    for (Iterator iterator = utilLevels.iterator(); iterator.hasNext();) {
      utilLevels.add(iterator.next().toString());
    }

    levelMap.put(ChainsawConstants.UTIL_LOGGING_EVENT_TYPE, utilLevels);
    levelMap.put(ChainsawConstants.LOG4J_EVENT_TYPE, priorityLevels);

    filterableColumns.add(ChainsawConstants.LEVEL_COL_NAME);
    filterableColumns.add(ChainsawConstants.LOGGER_COL_NAME);
    filterableColumns.add(ChainsawConstants.THREAD_COL_NAME);
    filterableColumns.add(ChainsawConstants.NDC_COL_NAME);
    filterableColumns.add(ChainsawConstants.MDC_COL_NAME);
    filterableColumns.add(ChainsawConstants.CLASS_COL_NAME);
    filterableColumns.add(ChainsawConstants.METHOD_COL_NAME);
    filterableColumns.add(ChainsawConstants.FILE_COL_NAME);
    filterableColumns.add(ChainsawConstants.NONE_COL_NAME);

    JPanel panePanel = new JPanel();
    panePanel.setLayout(new BorderLayout(2, 2));

    getContentPane().setLayout(new BorderLayout());

    tabbedPane.addChangeListener(tbms);
    tabbedPane.addChangeListener(
      new ChangeListener() {
        //received a statechange event - selection changed - remove icon from selected index
        public void stateChanged(ChangeEvent e) {
          if (tabbedPane.getSelectedComponent() instanceof ChainsawTabbedPane) {
            if (tabbedPane.getSelectedIndex() > -1) {
              tabbedPane.setIconAt(tabbedPane.getSelectedIndex(), null);
            }
          }
        }
      });

    KeyStroke ksRight =
      KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, Event.CTRL_MASK);
    KeyStroke ksLeft =
      KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, Event.CTRL_MASK);

    tabbedPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
      ksRight, "MoveRight");
    tabbedPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
      ksLeft, "MoveLeft");

    Action moveRight =
      new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          int temp = tabbedPane.getSelectedIndex();
          ++temp;

          if (temp != tabbedPane.getTabCount()) {
            tabbedPane.setSelectedTab(temp);
          }
        }
      };

    Action moveLeft =
      new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          int temp = tabbedPane.getSelectedIndex();
          --temp;

          if (temp > -1) {
            tabbedPane.setSelectedTab(temp);
          }
        }
      };

    tabbedPane.getActionMap().put("MoveRight", moveRight);
    tabbedPane.getActionMap().put("MoveLeft", moveLeft);

    /**
     * We listen for double clicks, and auto-undock currently
     * selected Tab if the mouse event location matches the currently selected
     * tab
     */
    tabbedPane.addMouseListener(
      new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
          super.mouseClicked(e);

          if (
            (e.getClickCount() > 1)
              && ((e.getModifiers() & InputEvent.BUTTON1_MASK) > 0)) {
            int tabIndex = tabbedPane.getSelectedIndex();

            if (
              (tabIndex != -1) && (tabIndex == tabbedPane.getSelectedIndex())) {
              LogPanel logPanel = getCurrentLogPanel();

              if (logPanel != null) {
                logPanel.undock();
              }
            }
          }
        }
      });

    addWelcomePanel();
    panePanel.add(tabbedPane);

    getContentPane().add(toolbar, BorderLayout.NORTH);
    getContentPane().add(panePanel, BorderLayout.CENTER);
    getContentPane().add(statusBar, BorderLayout.SOUTH);

    addWindowListener(
      new WindowAdapter() {
        public void windowClosing(WindowEvent event) {
          exit();
        }
      });

    pack();

    sm.addSettingsListener(this);
    sm.addSettingsListener(tbms);
    sm.loadSettings();

    setVisible(true);

    removeSplash();

    synchronized (initializationLock) {
      isGUIFullyInitialized = true;
      initializationLock.notifyAll();
    }

    if (noReceiversDefined) {
      showNoReceiversWarningPanel();
    }
  }

  /**
   * Displays a warning dialog about having no Receivers defined
   * and allows the user to choose some options for configuration
   */
  private void showNoReceiversWarningPanel() {
    SwingUtilities.invokeLater(
      new Runnable() {
        public void run() {
          final JDialog dialog = new JDialog(LogUI.this, true);
          dialog.setTitle("Warning: You have no Receivers defined...");
          dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

          dialog.setResizable(false);

          noReceiversWarningPanel.setOkActionListener(
            new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
              }
            });

          dialog.getContentPane().add(noReceiversWarningPanel);

          dialog.pack();

          Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
          dialog.setLocation(
            (screenSize.width / 2) - (dialog.getWidth() / 2),
            (screenSize.height / 2) - (dialog.getHeight() / 2));
          dialog.show();

          dialog.dispose();

          if (noReceiversWarningPanel.getModel().isManualMode()) {
            toggleReceiversPanel();
          } else if (noReceiversWarningPanel.getModel().isSimpleSocketMode()) {
            int port = noReceiversWarningPanel.getModel().getSimplePort();
            SocketReceiver simpleSocketReceiver = new SocketReceiver(port);
            simpleSocketReceiver.setName("Simple Socket Receiver");
            PluginRegistry.startPlugin(simpleSocketReceiver);
            receiversPanel.updateReceiverTreeInDispatchThread();
            getStatusBar().setMessage(
              "Simple Socket Receiver created, started, and listening on port "
              + port);
          } else if (noReceiversWarningPanel.getModel().isLoadConfig()) {
            final URL url =
              noReceiversWarningPanel.getModel().getConfigToLoad();

            if (url != null) {
              LogLog.debug("Initialiazing Log4j with " + url.toExternalForm());

              new Thread(
                new Runnable() {
                  public void run() {
                    try {
                      OptionConverter.selectAndConfigure(
                        url, null, LogManager.getLoggerRepository());
                    } catch (Exception e) {
                      LogLog.error("Error initializing Log4j", e);
                    }

                    receiversPanel.updateReceiverTreeInDispatchThread();
                  }
                }).start();
            }
          }
        }
      });
  }

  /**
   * Exits the application, ensuring Settings are saved.
   *
   */
  void exit() {
    //    TODO Ask the user if they want to save the settings via a dialog.
    sm.saveSettings();

    int tabCount = tabbedPane.getTabCount();

    for (int i = 0; i < tabCount; i++) {
      Component c = tabbedPane.getComponentAt(i);

      if (c instanceof LogPanel) {
        ((LogPanel) c).saveSettings();
      }
    }

    shutdown();
  }

  void addWelcomePanel() {
    tabbedPane.addANewTab(
      "Welcome", WelcomePanel.getInstance(), new ImageIcon(
        ChainsawIcons.ABOUT), "Welcome/Help");
  }

  void removeWelcomePanel() {
    tabbedPane.remove(
      tabbedPane.getComponentAt(tabbedPane.indexOfTab("Welcome")));
  }

  void toggleReceiversPanel() {
    SwingUtilities.invokeLater(
      new Runnable() {
        public void run() {
          if (getContentPane().isAncestorOf(receiversPanel)) {
            getContentPane().remove(receiversPanel);
          } else {
            getContentPane().add(receiversPanel, BorderLayout.EAST);
          }

          getContentPane().invalidate();
          getContentPane().validate();

          tbms.stateChange();
        }
      });
  }

  boolean isReceiverPanelVisible() {
    return getContentPane().isAncestorOf(receiversPanel);
  }

  ChainsawStatusBar getStatusBar() {
    return statusBar;
  }

  void showAboutBox() {
    if (aboutBox == null) {
      aboutBox = new ChainsawAbout(this);
    }

    aboutBox.setVisible(true);
  }

  Map getPanels() {
    Map m = new HashMap();
    Set panelSet = panelMap.entrySet();
    Iterator iter = panelSet.iterator();

    while (iter.hasNext()) {
      Map.Entry entry = (Map.Entry) iter.next();
      m.put(
        entry.getKey(),
        Boolean.valueOf(((DockablePanel) entry.getValue()).isDocked()));
    }

    return m;
  }

  void displayPanel(String panelName, boolean display) {
    Object o = panelMap.get(panelName);

    if (o instanceof LogPanel) {
      LogPanel p = (LogPanel) o;

      int index = tabbedPane.indexOfTab(panelName);

      if ((index == -1) && display) {
        tabbedPane.addTab(panelName, p);
      }

      if ((index > -1) && !display) {
        tabbedPane.removeTabAt(index);
      }
    }
  }

  /**
   * Shutsdown by ensuring the Appender gets a chance to close.
   */
  private void shutdown() {
    JWindow progress = new JWindow();
    final ProgressPanel panel = new ProgressPanel(1, 3, "Shutting down");
    progress.getContentPane().add(panel);
    progress.pack();

    Point p = new Point(getLocation());
    p.move((int) getSize().getWidth() >> 1, (int) getSize().getHeight() >> 1);
    progress.setLocation(p);
    progress.setVisible(true);

    Runnable runnable =
      new Runnable() {
        public void run() {
          try {
            int progress = 1;
            final int delay = 25;

            handler.close();
            panel.setProgress(progress++);

            Thread.sleep(delay);

            PluginRegistry.stopAllPlugins();
            panel.setProgress(progress++);

            Thread.sleep(delay);

            panel.setProgress(progress++);
            Thread.sleep(delay);
          } catch (Exception e) {
            e.printStackTrace();
          }

          fireShutdownEvent();
          performShutdownAction();
        }
      };

    new Thread(runnable).start();
  }

  /**
   * Ensures all the registered ShutdownListeners are notified.
   */
  private void fireShutdownEvent() {
    ShutdownListener[] listeners =
      (ShutdownListener[]) shutdownListenerList.getListeners(
        ShutdownListener.class);

    for (int i = 0; i < listeners.length; i++) {
      listeners[i].shuttingDown();
    }
  }

  /**
   * Configures LogUI's with an action to execute when the user
   * requests to exit the application, the default action
   * is to exit the VM.
   * This Action is called AFTER all the ShutdownListeners have been notified
   *
   * @param shutdownAction
   */
  public final void setShutdownAction(Action shutdownAction) {
    this.shutdownAction = shutdownAction;
  }

  /**
   * Using the current thread, calls the registed Shutdown action's
   * actionPerformed(...) method.
   *
   */
  private void performShutdownAction() {
    LogLog.debug("Calling the shutdown Action. Goodbye!");
    shutdownAction.actionPerformed(
      new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Shutting Down"));
  }

  /**
   * Returns the currently selected LogPanel, if there is one, otherwise null
   * @return
   */
  LogPanel getCurrentLogPanel() {
    Component selectedTab = tabbedPane.getSelectedComponent();

    if (selectedTab instanceof LogPanel) {
      return (LogPanel) selectedTab;
    } else {
      //      System.out.println(selectedTab);
    }

    return null;
  }

  void removeStatusBar() {
    SwingUtilities.invokeLater(
      new Runnable() {
        public void run() {
          getContentPane().remove(statusBar);
          getContentPane().validate();
          getContentPane().repaint();
        }
      });
  }

  boolean isStatusBarVisible() {
    return getContentPane().isAncestorOf(statusBar);
  }

  void addStatusBar() {
    removeStatusBar();
    SwingUtilities.invokeLater(
      new Runnable() {
        public void run() {
          getContentPane().add(statusBar, BorderLayout.SOUTH);
          getContentPane().validate();
          getContentPane().repaint();
        }
      });
  }

  public String getActiveTabName() {
    int index = tabbedPane.getSelectedIndex();

    if (index == -1) {
      return null;
    } else {
      return tabbedPane.getTitleAt(index);
    }
  }

  /**
   * Formats the individual elements of an LoggingEvent by ensuring that
   * there are no null bits, replacing them with EMPTY_STRING
   * @param v
   * @return
   */
  private Vector formatFields(Vector v) {
    for (int i = 0; i < v.size(); i++) {
      if (v.get(i) == null) {
        v.set(i, ChainsawConstants.EMPTY_STRING);
      }
    }

    return v;
  }

  /**
   * Regurgitates a DisplayFilter for a specific machine identifier
   * by deserializing the settings from a file.
   * DisplayFilter serializes tool tip fields and enabled flag.
   * @param ident
   * @return
   */
  private DisplayFilter loadDisplayFilter(String ident) {
    DisplayFilter d = null;
    ObjectInputStream s = null;
    File f =
      new File(
        SettingsManager.getInstance().getSettingsDirectory() + File.separator
        + ident + ChainsawConstants.SETTINGS_EXTENSION);

    if (f.exists()) {
      try {
        s = new ObjectInputStream(
            new BufferedInputStream(new FileInputStream(f)));
        d = (DisplayFilter) s.readObject();
      } catch (IOException ioe) {
        ioe.printStackTrace();
      } catch (ClassNotFoundException cnfe) {
        cnfe.printStackTrace();
      } finally {
        if (s != null) {
          try {
            s.close();
          } catch (IOException ioe) {
            ioe.printStackTrace();
          }
        }
      }
    }

    if (d == null) {
      d = new DisplayFilter(ident);
    }

    return d;
  }

  /**
   * Modify the saved Look And Feel - does not update the currently used Look And Feel
   * @param string The FQN of the LookAndFeel
   */
  public void setLookAndFeel(String lookAndFeelClassName) {
    this.lookAndFeelClassName = lookAndFeelClassName;
    JOptionPane.showMessageDialog(
      getContentPane(),
      "Restart application for the new Look and Feel to take effect.",
      "Look and Feel Updated", JOptionPane.INFORMATION_MESSAGE);
  }

  /**
   * Changes the currently used Look And Feel of the App
   * @param string The FQN of the LookANdFeel
   */
  private void applyLookAndFeel(String lookAndFeelClassName) {
    LogLog.debug("Setting L&F -> " + lookAndFeelClassName);

    try {
      UIManager.setLookAndFeel(lookAndFeelClassName);
      SwingUtilities.updateComponentTreeUI(this);
    } catch (Exception e) {
      LogLog.error("Failed to change L&F", e);
    }
  }

  /**
   * Causes the Welcome Panel to become visible, and shows the URL
   * specified as it's contents
   * @param url for content to show
   */
  void showHelp(URL url) {
    removeWelcomePanel();
    addWelcomePanel();

    //    TODO ensure the Welcome Panel is the selected tab
    WelcomePanel.getInstance().setURL(url);
  }

  /**
   * @return
   */
  public boolean isLogTreePanelVisible() {
    if (getCurrentLogPanel() == null) {
      return false;
    }

    return getCurrentLogPanel().isLogTreePanelVisible();
  }

  /* (non-Javadoc)
   * @see org.apache.log4j.chainsaw.EventBatchListener#getInterestedIdentifier()
   */
  public String getInterestedIdentifier() {
    //    this instance is interested in ALL event batches, as we determine how to route things
    return null;
  }

  /**
   * This class handles the recption of the Event batches
   * and creates new LogPanels if the identifier is not in use
   * otherwise it ignores the event batch.
   * @author Paul Smith <psmith@apache.org>
   *
   */
  private class NewTabEventBatchReceiver implements EventBatchListener {
    public void receiveEventBatch(
      final String ident, final List eventBatchEntrys) {
      if (eventBatchEntrys.size() == 0) {
        return;
      }

      EventContainer tableModel;
      JSortTable table;
      ScrollToBottom scrollToBottom;
      HashMap map = null;

      if (!isGUIFullyInitialized) {
        synchronized (initializationLock) {
          while (!isGUIFullyInitialized) {
            System.out.println(
              "Wanting to add a row, but GUI not initialized, waiting...");

            /**
             * Lets wait 1 seconds and recheck.
             */
            try {
              initializationLock.wait(1000);
            } catch (InterruptedException e) {
            }
          }
        }
      }

      /**
       * notify the status bar we received an event
       */
      statusBar.receivedEvent();

      if (tableModelMap.containsKey(ident)) {
        /**
         * we ignore this since we assume the LogPanel has been registered itself a listener
         * and will receive it's own event batches directly
         */
      } else {
        final String eventType =
          ((ChainsawEventBatchEntry) eventBatchEntrys.get(0)).getEventType();
        final LogPanel thisPanel = new LogPanel(ident, eventType);

        /**
         * Let the new LogPanel receive this batch
         */
        thisPanel.receiveEventBatch(ident, eventBatchEntrys);

        /**
         * Now add the panel as a batch listener so it can handle it's own batchs
         */
        handler.addEventBatchListener(thisPanel);

        SwingUtilities.invokeLater(
          new Runnable() {
            public void run() {
              tabbedPane.addANewTab(
                ident, thisPanel, new ImageIcon(ChainsawIcons.TOOL_TIP));
            }
          });

        sm.configure(thisPanel);

        String msg = "added tab " + ident;
        LogLog.debug(msg);
        statusBar.setMessage(msg);
      }
    }

    /* (non-Javadoc)
     * @see org.apache.log4j.chainsaw.EventBatchListener#getInterestedIdentifier()
     */
    public String getInterestedIdentifier() {
      // we are interested in all batches so we can detect new identifiers
      return null;
    }
  }

  /**
   * LogPanel encapsulates all the necessary bits and pieces of a
   * floating window of Events coming from a specific Location.
   *
   * This is where most of the Swing components are constructed and laid out.
   */
  class LogPanel extends DockablePanel implements SettingsListener,
    EventBatchListener {
    private boolean paused = false;
    final ColorFilter colorFilter = new ColorFilter();
    final DisplayFilter displayFilter;
    final EventContainer tableModel;
    final JEditorPane detail;
    final JSplitPane lowerPanel;
    final DetailPaneUpdater detailPaneUpdater;
    final JPanel upperPanel;
    final JPanel eventsAndStatusPanel;
    final JFrame f;
    final DockablePanel externalPanel;
    final Action dockingAction;
    final JSortTable table;
    private String profileName = null;
    boolean isDocked = true;
    String identifier;
    final Map columnDisplayMap = new HashMap();
    final Map colorDisplayMap = new HashMap();
    final Set loggerSet = new HashSet();
    final ColorDisplaySelector colorDisplaySelector;
    Set MDCSet = new HashSet();
    Set NDCSet = new HashSet();
    Set threadSet = new HashSet();
    Set classSet = new HashSet();
    Set methodSet = new HashSet();
    Set fileSet = new HashSet();
    Set levelSet = new HashSet();
    ScrollToBottom scrollToBottom;
    private final LogPanelLoggerTreeModel logTreeModel =
      new LogPanelLoggerTreeModel();
    private Layout detailPaneLayout = new EventDetailLayout();
    private Layout toolTipLayout = new EventDetailLayout();

    //used for consistency - stays empty - used to allow none set in the colordisplay selector and right click
    Set noneSet = new HashSet();
    Point currentPoint;
    private final JSplitPane nameTreeAndMainPanelSplit;
    private final LoggerNameTreePanel logTreePanel;

    public LogPanel(final String ident, String eventType) {
      identifier = ident;

      Map map = new HashMap();
      entryMap.put(ident, map);

      int bufferSize = 500;

      //if buffer size not provided, set default buffer size to 500 (only used if usecyclicbuffer true)
      if (System.getProperty(CYCLIC_BUFFER_SIZE_PROP_NAME) != null) {
        bufferSize =
          Integer.valueOf(System.getProperty(CYCLIC_BUFFER_SIZE_PROP_NAME))
                 .intValue();
      }

      tableModel =
        new ChainsawCyclicBufferTableModel(
          Boolean.valueOf(System.getProperty(USE_CYCLIC_BUFFER_PROP_NAME))
                 .booleanValue(), bufferSize);

      table = new JSortTable(tableModel);
      table.getColumnModel().addColumnModelListener(
        new ChainsawTableColumnModelListener(table));

      table.setAutoCreateColumnsFromModel(false);

      table.setRowHeight(20);
      table.setShowGrid(false);

      scrollToBottom = new ScrollToBottom(true);

      // ==========================================
      tableModel.addLoggerNameListener(logTreeModel);
      logTreePanel = new LoggerNameTreePanel(logTreeModel);

      levelSet = new HashSet((List) levelMap.get(eventType));

      map.put(ChainsawConstants.LEVEL_COL_NAME, levelSet);

      map.put(ChainsawConstants.LOGGER_COL_NAME, loggerSet);
      map.put(ChainsawConstants.THREAD_COL_NAME, threadSet);
      map.put(ChainsawConstants.NDC_COL_NAME, NDCSet);
      map.put(ChainsawConstants.MDC_COL_NAME, MDCSet);
      map.put(ChainsawConstants.CLASS_COL_NAME, classSet);
      map.put(ChainsawConstants.METHOD_COL_NAME, methodSet);
      map.put(ChainsawConstants.FILE_COL_NAME, fileSet);
      map.put(ChainsawConstants.NONE_COL_NAME, noneSet);

      setLayout(new BorderLayout());
      displayFilter = loadDisplayFilter(ident);
      tableModel.setDisplayFilter(displayFilter);
      displayFilter.addFilterChangedListener(tableModel);

      scrollMap.put(ident, scrollToBottom);

      TableColorizingRenderer renderer = new TableColorizingRenderer();
      sm.addSettingsListener(renderer);
      sm.configure(renderer);

      renderer.setColorFilter(colorFilter);

      table.setDefaultRenderer(Object.class, renderer);

      //if the color filter changes, trigger the tablemodel update
      colorFilter.addFilterChangedListener(
        new FilterChangedListener() {
          public void filterChanged() {
            if (tableModel instanceof AbstractTableModel) {
              ((AbstractTableModel) tableModel).fireTableDataChanged();
            }
          }
        });

      final DetailFieldSelector detailFieldSelector =
        new DetailFieldSelector(
          ident, new Vector(ChainsawColumns.getColumnsNames()), displayFilter);

      final ColumnSelector columnSelector =
        new ColumnSelector(
          ident, new Vector(ChainsawColumns.getColumnsNames()), table,
          displayFilter);
      table.getColumnModel().addColumnModelListener(columnSelector);
      columnSelector.setIconImage(getIconImage());
      detailFieldSelector.setIconImage(getIconImage());

      JMenu menuColumnDisplayFilter =
        new JMenu("Apply display filter for column");

      JMenu menuColumnColorFilter = new JMenu("Apply color filter for column");

      ButtonGroup bg = new ButtonGroup();
      Iterator iter = filterableColumns.iterator();

      while (iter.hasNext()) {
        final String colName = (String) iter.next();
        JRadioButtonMenuItem thisItem = new JRadioButtonMenuItem(colName);
        thisItem.setFont(thisItem.getFont().deriveFont(Font.PLAIN));
        thisItem.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
              LoggingEvent lastSelected = null;

              if (table.getSelectedRow() > -1) {
                lastSelected = tableModel.getRow(table.getSelectedRow());
              }

              colorDisplaySelector.applyColorUpdateForColumn(colName);
              colorDisplaySelector.applyColorFilters(colName);

              if (lastSelected != null) {
                int newIndex = tableModel.getRowIndex(lastSelected);

                if (newIndex > -1) {
                  table.scrollToRow(
                    newIndex,
                    table.columnAtPoint(table.getVisibleRect().getLocation()));
                }
              }
            }
          });
        bg.add(thisItem);
        menuColumnColorFilter.add(thisItem);
        colorDisplayMap.put(colName, thisItem);
      }

      ButtonGroup bg2 = new ButtonGroup();
      Iterator iter2 = filterableColumns.iterator();

      while (iter2.hasNext()) {
        final String colName = (String) iter2.next();
        JRadioButtonMenuItem thisItem = new JRadioButtonMenuItem(colName);
        thisItem.setFont(thisItem.getFont().deriveFont(Font.PLAIN));
        thisItem.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
              LoggingEvent lastSelected = null;

              if (table.getSelectedRow() > -1) {
                lastSelected = tableModel.getRow(table.getSelectedRow());
              }

              colorDisplaySelector.applyDisplayUpdateForColumn(colName);
              colorDisplaySelector.applyDisplayFilters(colName);

              if (lastSelected != null) {
                int newIndex = tableModel.getRowIndex(lastSelected);

                if (newIndex > -1) {
                  table.scrollToRow(
                    newIndex,
                    table.columnAtPoint(table.getVisibleRect().getLocation()));
                }
              }
            }
          });
        bg2.add(thisItem);
        menuColumnDisplayFilter.add(thisItem);
        columnDisplayMap.put(colName, thisItem);
      }

      colorDisplaySelector =
        new ColorDisplaySelector(
          ident, map, colorFilter, displayFilter, colorDisplayMap,
          columnDisplayMap, ChainsawColumns.getColumnsNames(),
          filterableColumns, (List) levelMap.get(eventType));
      colorDisplaySelector.setIconImage(
        ((ImageIcon) ChainsawIcons.ICON_PREFERENCES).getImage());

      table.addMouseMotionListener(
        new MouseMotionAdapter() {
          int currentRow = -1;

          public void mouseMoved(MouseEvent evt) {
            currentPoint = evt.getPoint();

            if (displayFilter.isToolTipsEnabled()) {
              int row = table.rowAtPoint(evt.getPoint());

              if ((row == currentRow) || (row == -1)) {
                return;
              }

              currentRow = row;

              LoggingEvent event = tableModel.getRow(currentRow);
              Layout layout = getToolTipLayout();

              if (event != null) {
                StringBuffer buf = new StringBuffer();
                buf.append(layout.getHeader()).append(layout.format(event))
                   .append(layout.getFooter());
                table.setToolTipText(buf.toString());
              }
            } else {
              table.setToolTipText(null);
            }
          }
        });

      //HACK - fix the way columns are sized..should be saved off and loaded later
      table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

      detail = new JEditorPane(ChainsawConstants.DETAIL_CONTENT_TYPE, "");
      detail.setEditable(false);

      detailPaneUpdater =
        new DetailPaneUpdater(this, detail, (EventContainer) tableModel);

      upperPanel = new JPanel(new BorderLayout());
      upperPanel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 0));

      final JLabel filterLabel = new JLabel("QuickFilter: ");
      filterLabel.setFont(filterLabel.getFont().deriveFont(Font.BOLD));

      JPanel upperLeftPanel =
        new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 0));
      upperLeftPanel.add(filterLabel);

      final JComboBox customFilterList =
        new JComboBox(ChainsawColumns.getColumnsNames().toArray());
      customFilterList.setFont(customFilterList.getFont().deriveFont(10f));

      final JTextField filterText = new JTextField();

      filterText.getDocument().addDocumentListener(
        new DocumentListener() {
          public void insertUpdate(DocumentEvent e) {
            setFilter();
          }

          public void removeUpdate(DocumentEvent e) {
            setFilter();
          }

          public void changedUpdate(DocumentEvent e) {
            setFilter();
          }

          public void setFilter() {
            if (filterText.getText().equals("")) {
              displayFilter.setCustomFilter(null);
            } else {
              detailPaneUpdater.setSelectedRow(-1);

              displayFilter.setCustomFilter(
                new DisplayFilterEntry(
                  (String) customFilterList.getSelectedItem(),
                  filterText.getText(), ChainsawConstants.GLOBAL_MATCH));
            }
          }
        });

      String evaluator =
        ExpressionEvaluatorFactory.newInstance().getEvaluatorClassName();
      filterText.setToolTipText(
        "See " + evaluator + " documentation for expression rules");

      customFilterList.setMaximumRowCount(15);

      upperLeftPanel.add(customFilterList);

      customFilterList.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            if (!(filterText.getText().equals(""))) {
              displayFilter.setCustomFilter(
                new DisplayFilterEntry(
                  (String) customFilterList.getSelectedItem(),
                  filterText.getText(), ChainsawConstants.GLOBAL_MATCH));
            }
          }
        });

      upperPanel.add(filterText, BorderLayout.CENTER);
      upperPanel.add(upperLeftPanel, BorderLayout.WEST);

      final JCheckBox override = new JCheckBox();
      override.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            LoggingEvent lastSelected = null;

            if (table.getSelectedRow() > -1) {
              lastSelected = tableModel.getRow(table.getSelectedRow());
            }

            displayFilter.setCustomFilterOverride(override.isSelected());

            if (lastSelected != null) {
              int newIndex = tableModel.getRowIndex(lastSelected);

              if (newIndex > -1) {
                table.scrollToRow(
                  newIndex,
                  table.columnAtPoint(table.getVisibleRect().getLocation()));
              }
            }
          }
        });

      override.setToolTipText(
        "<html>Unchecked: Apply QuickFilter to displayed rows<br>Checked: Apply QuickFilter to ALL rows (override display filter setting)</html>");

      JPanel upperRightPanel =
        new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
      upperRightPanel.add(override);

      upperPanel.add(upperRightPanel, BorderLayout.EAST);

      eventsAndStatusPanel = new JPanel();
      eventsAndStatusPanel.setLayout(new BorderLayout());

      final JScrollPane eventsPane = new JScrollPane(table);

      eventsPane.setPreferredSize(new Dimension(900, 300));

      eventsAndStatusPanel.add(eventsPane, BorderLayout.CENTER);

      final JPanel statusLabelPanel = new JPanel();
      statusLabelPanel.setLayout(new BorderLayout());

      final JLabel statusPaneLabel = new JLabel();
      statusPaneLabel.setFont(statusPaneLabel.getFont().deriveFont(Font.BOLD));
      statusLabelPanel.setBorder(BorderFactory.createEtchedBorder());
      statusPaneLabel.setHorizontalAlignment(JLabel.LEFT);
      statusPaneLabel.setVerticalAlignment(JLabel.CENTER);
      statusLabelPanel.add(statusPaneLabel, BorderLayout.WEST);
      statusLabelPanel.add(upperPanel, BorderLayout.CENTER);
      eventsAndStatusPanel.add(statusLabelPanel, BorderLayout.NORTH);

      final JPanel detailPanel = new JPanel(new BorderLayout());

      //set valueisadjusting if holding down a key - don't process setdetail events
      table.addKeyListener(
        new KeyListener() {
          public void keyTyped(KeyEvent e) {
          }

          public void keyPressed(KeyEvent e) {
            synchronized (detail) {
              table.getSelectionModel().setValueIsAdjusting(true);
              detail.notify();
            }
          }

          public void keyReleased(KeyEvent e) {
            synchronized (detail) {
              table.getSelectionModel().setValueIsAdjusting(false);
              detail.notify();
            }
          }
        });

      final JScrollPane detailPane = new JScrollPane(detail);
      detailPane.setPreferredSize(new Dimension(900, 50));

      detailPanel.add(detailPane, BorderLayout.CENTER);

      lowerPanel =
        new JSplitPane(
          JSplitPane.VERTICAL_SPLIT, eventsAndStatusPanel, detailPanel);
      lowerPanel.setBorder(null);
      lowerPanel.setDividerLocation(150);
      lowerPanel.setLastDividerLocation(-1);
      lowerPanel.setOneTouchExpandable(true);
      lowerPanel.addPropertyChangeListener(
        "dividerLocation",
        new PropertyChangeListener() {
          public void propertyChange(PropertyChangeEvent evt) {
            tbms.stateChange();
          }
        });

      nameTreeAndMainPanelSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
      nameTreeAndMainPanelSplit.add(logTreePanel);
      nameTreeAndMainPanelSplit.add(lowerPanel);
      nameTreeAndMainPanelSplit.setOneTouchExpandable(true);
      nameTreeAndMainPanelSplit.setToolTipText("Still under development....");
      nameTreeAndMainPanelSplit.setDividerLocation(-1);

      add(nameTreeAndMainPanelSplit, BorderLayout.CENTER);

      /**
       * This listener deals with when the user hides the LogPanel,
       * by disabling the use of the splitpane
       */
      logTreePanel.addComponentListener(
        new ComponentListener() {
          public void componentHidden(ComponentEvent e) {
            nameTreeAndMainPanelSplit.setEnabled(false);
            nameTreeAndMainPanelSplit.setOneTouchExpandable(false);
            tbms.stateChange();
          }

          public void componentMoved(ComponentEvent e) {
          }

          public void componentResized(ComponentEvent e) {
          }

          public void componentShown(ComponentEvent e) {
            nameTreeAndMainPanelSplit.setEnabled(true);
            nameTreeAndMainPanelSplit.setOneTouchExpandable(true);
            nameTreeAndMainPanelSplit.setDividerLocation(-1);
            tbms.stateChange();
          }
        });

      //      add(lowerPanel, BorderLayout.CENTER);
      table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

      table.getSelectionModel().addListSelectionListener(
        new ListSelectionListener() {
          public void valueChanged(ListSelectionEvent evt) {
            if (
              ((evt.getFirstIndex() == evt.getLastIndex())
                && (evt.getFirstIndex() > 0)) || (evt.getValueIsAdjusting())) {
              return;
            }

            final ListSelectionModel lsm =
              (ListSelectionModel) evt.getSource();

            if (lsm.isSelectionEmpty()) {
              if (getIdentifier().equals(getActiveTabName())) {
                statusBar.setNothingSelected();
              }

              if (detail.getDocument().getDefaultRootElement() != null) {
                detailPaneUpdater.setSelectedRow(-1);
              }
            } else {
              if (table.getSelectedRow() > -1) {
                int selectedRow = table.getSelectedRow();

                if (getIdentifier().equals(getActiveTabName())) {
                  updateStatusBar();
                }

                try {
                  if (tableModel.getRowCount() >= selectedRow) {
                    detailPaneUpdater.setSelectedRow(table.getSelectedRow());
                  } else {
                    detailPaneUpdater.setSelectedRow(-1);
                  }
                } catch (Exception e) {
                  e.printStackTrace();
                  detailPaneUpdater.setSelectedRow(-1);
                }
              }
            }
          }
        });

      final JMenuItem menuItemToggleDock = new JMenuItem("Undock/dock");

      f = new JFrame(ident);
      f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

      if (ChainsawIcons.UNDOCKED_ICON != null) {
        f.setIconImage(new ImageIcon(ChainsawIcons.UNDOCKED_ICON).getImage());
      }

      externalPanel = new DockablePanel();
      externalPanel.setLayout(new BorderLayout());
      f.getContentPane().add(externalPanel);
      f.getContentPane().add(
        tbms.createDockwindowToolbar(f, this), BorderLayout.NORTH);

      dockingAction =
        new AbstractAction("Undock") {
            public void actionPerformed(ActionEvent evt) {
              if (isDocked()) {
                undock();
              } else {
                dock();
              }
            }
          };
      dockingAction.putValue(
        Action.SMALL_ICON, new ImageIcon(ChainsawIcons.UNDOCK));
      menuItemToggleDock.setAction(dockingAction);
      f.addWindowListener(
        new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            dock();
          }
        });

      JMenuItem menuItemDisplayFilter =
        new JMenuItem("Define display and color filters...");
      menuItemDisplayFilter.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            showPreferences();
          }
        });
      menuItemDisplayFilter.setIcon(ChainsawIcons.ICON_PREFERENCES);

      final JCheckBoxMenuItem menuItemToggleToolTips =
        new JCheckBoxMenuItem(
          "Show ToolTips", displayFilter.isToolTipsEnabled());
      menuItemToggleToolTips.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            displayFilter.enableToolTips(menuItemToggleToolTips.isSelected());
          }
        });
      menuItemToggleToolTips.setIcon(new ImageIcon(ChainsawIcons.TOOL_TIP));

      final JMenuItem menuDefineCustomFilter =
        new JMenuItem("Custom filter from mouse location");
      menuDefineCustomFilter.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            if (currentPoint != null) {
              int column = table.columnAtPoint(currentPoint);
              int row = table.rowAtPoint(currentPoint);
              String colName = table.getColumnName(column);
              String value = "";

              if (
                colName.equalsIgnoreCase(ChainsawConstants.TIMESTAMP_COL_NAME)) {
                JComponent comp =
                  (JComponent) table.getCellRenderer(row, column);

                if (comp instanceof JLabel) {
                  value = ((JLabel) comp).getText();
                }
              } else {
                value = table.getValueAt(row, column).toString();
              }

              customFilterList.setSelectedItem(colName);
              filterText.setText(value);
            }
          }
        });

      final JCheckBoxMenuItem menuItemScrollBottom =
        new JCheckBoxMenuItem("Scroll to bottom", scrollToBottom.isScrolled());
      menuItemScrollBottom.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            scrollToBottom.scroll(menuItemScrollBottom.isSelected());
          }
        });
      menuItemScrollBottom.setIcon(
        new ImageIcon(ChainsawIcons.SCROLL_TO_BOTTOM));

      JMenuItem menuItemRemoveColorFilter =
        new JMenuItem("Remove all color filters");
      menuItemRemoveColorFilter.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            colorDisplaySelector.clearColors();
            colorFilter.clear();
          }
        });

      JMenuItem menuItemDetailFieldSelector =
        new JMenuItem("Select tooltip/detail columns...");
      menuItemDetailFieldSelector.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            detailFieldSelector.show();
          }
        });

      JMenuItem menuItemColumnSelector =
        new JMenuItem("Select display columns...");
      menuItemColumnSelector.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            columnSelector.show();
          }
        });

      JMenuItem menuItemRemoveDisplayFilter =
        new JMenuItem("Remove all display filters");
      menuItemRemoveDisplayFilter.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            colorDisplaySelector.clearDisplay();
            displayFilter.clear();
          }
        });

      final JPopupMenu p = new JPopupMenu();

      p.add(menuItemToggleDock);
      p.add(new JSeparator());

      final JCheckBoxMenuItem menuItemToggleDetails =
        new JCheckBoxMenuItem("Show Detail Pane");
      menuItemToggleDetails.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            toggleDetailPanel();
            menuItemToggleDetails.getModel().setSelected(
              isDetailPaneVisible());
          }
        });
      lowerPanel.addPropertyChangeListener(
        "dividerLocation",
        new PropertyChangeListener() {
          public void propertyChange(PropertyChangeEvent evt) {
            menuItemToggleDetails.getModel().setSelected(
              isDetailPaneVisible());
          }
        });
      menuItemToggleDetails.setIcon(new ImageIcon(ChainsawIcons.INFO));

      /**
       * We set this to true first, because the Split pane hasn't been laid
       * out yet, and isDetailPaneVisible() will therefore return false.
       */
      menuItemToggleDetails.getModel().setSelected(true);
      menuItemToggleDetails.addChangeListener(tbms);

      p.add(menuItemToggleDetails);
      p.add(menuItemToggleToolTips);
      p.add(menuItemScrollBottom);

      p.add(new JSeparator());

      p.add(menuDefineCustomFilter);
      p.add(new JSeparator());

      p.add(menuItemDisplayFilter);
      p.add(menuColumnDisplayFilter);
      p.add(menuColumnColorFilter);
      p.add(new JSeparator());

      JMenu removeSubMenu = new JMenu("Remove");
      JMenu selectSubMenu = new JMenu("Select");

      selectSubMenu.add(menuItemColumnSelector);
      selectSubMenu.add(menuItemDetailFieldSelector);

      removeSubMenu.add(menuItemRemoveColorFilter);
      removeSubMenu.add(menuItemRemoveDisplayFilter);

      p.add(selectSubMenu);
      p.add(removeSubMenu);

      final PopupListener popupListener = new PopupListener(p);

      eventsPane.addMouseListener(popupListener);
      table.addMouseListener(popupListener);
      detail.addMouseListener(popupListener);

      tableMap.put(ident, table);
      tableModelMap.put(ident, tableModel);
      tabbedPane.add(ident, this);
      panelMap.put(ident, this);

      tableModel.addEventCountListener(
        new EventCountListener() {
          public void eventCountChanged(int currentCount, int totalCount) {
            statusPaneLabel.setText(
              " Events " + currentCount + " of " + totalCount);
          }
        });

      if (tableModel.isCyclic()) {
        final ChainsawCyclicBufferTableModel cyclicModel =
          (ChainsawCyclicBufferTableModel) tableModel;
        tableModel.addEventCountListener(
          new EventCountListener() {
            final NumberFormat formatter = NumberFormat.getPercentInstance();
            boolean warning75 = false;
            boolean warning100 = false;

            public void eventCountChanged(int currentCount, int totalCount) {
              double percent =
                ((double) totalCount) / cyclicModel.getMaxSize();
              String msg = null;

              if ((percent > 0.75) && (percent < 1.0) && !warning75) {
                msg =
                  "Warning :: " + formatter.format(percent) + " of the '"
                  + getIdentifier() + "' buffer has been used";
                warning75 = true;
              } else if ((percent >= 1.0) && !warning100) {
                msg =
                  "Warning :: " + formatter.format(percent) + " of the '"
                  + getIdentifier()
                  + "' buffer has been used.  Older events are being discarded.";
                warning100 = true;
              }

              if (msg != null) {
                getStatusBar().setMessage(msg);
              }
            }
          });
      }

      tableModel.addEventCountListener(new TabIconHandler(ident));
      f.pack();
    }

    /**
     * @return
     */
    public boolean isPaused() {
      return paused;
    }

    /**
    * Modifies the Paused property and notifies the listeners
     * @param paused
     */
    public void setPaused(boolean paused) {
      boolean oldValue = this.paused;
      this.paused = paused;
      firePropertyChange("paused", oldValue, paused);
    }

    void updateStatusBar() {
      SwingUtilities.invokeLater(
        new Runnable() {
          public void run() {
            statusBar.setSelectedLine(
              table.getSelectedRow() + 1, table.getModel().getRowCount());
          }
        });
    }

    void showPreferences() {
      colorDisplaySelector.show();
    }

    TableModel getModel() {
      return tableModel;
    }

    String getIdentifier() {
      return identifier;
    }

    void clearModel() {
      tableModel.clearModel();

      synchronized (detail) {
        detailPaneUpdater.setSelectedRow(-1);
        detail.notify();
      }

      statusBar.setNothingSelected();
    }

    public int getCurrentRow() {
      return table.getSelectedRow();
    }

    /**
     * Find from the top
     * @param text
     */
    void findFromTop(String text) {
      find(0, text);
    }

    /**
     * Finds the row with the specified text, and ensures it is made visible
     * @param text
     */
    void find(String text) {
      find(table.getSelectedRow(), text);
    }

    /**
     * Finds the next row with the specified text, and ensures it is made visible
     * @param text
     */
    void findNext(String text) {
      find(table.getSelectedRow() + 1, text);
    }

    /**
     * Finds the row with the specified text starting at the specified row, and ensures it is made visible
     * @param text
     */
    void find(int row, String text) {
      final int newRow = tableModel.find(row, text);

      if (text.length() == 0) {
        scrollToBottom.bypass(false);
      } else {
        scrollToBottom.bypass(true);
      }

      table.scrollToRow(
        newRow, table.columnAtPoint(table.getVisibleRect().getLocation()));
    }

    /**
     * Docks this DockablePanel by hiding the JFrame and placing the
     * Panel back inside the LogUI window.
     *
     */
    void dock() {
      setDocked(true);
      f.setVisible(false);
      removeAll();

      //      add(lowerPanel, BorderLayout.CENTER);
      add(nameTreeAndMainPanelSplit, BorderLayout.CENTER);
      panelMap.put(getIdentifier(), LogPanel.this);
      tabbedPane.addANewTab(getIdentifier(), LogPanel.this, null);
      externalPanel.setDocked(true);
      dockingAction.putValue(Action.NAME, "Undock");
      dockingAction.putValue(Action.SMALL_ICON, ChainsawIcons.ICON_UNDOCK);
    }

    /**
     * Undocks this DockablePanel by removing the panel from the LogUI
     * window and placing it inside it's own JFrame.
     *
     */
    void undock() {
      setDocked(false);
      externalPanel.removeAll();
      externalPanel.add(nameTreeAndMainPanelSplit, BorderLayout.CENTER);
      tabbedPane.remove(LogPanel.this);
      externalPanel.setDocked(false);
      panelMap.put(getIdentifier(), externalPanel);
      f.setSize(getSize());

      f.setLocation(getBounds().x, getBounds().y);

      f.setVisible(true);
      dockingAction.putValue(Action.NAME, "Dock");
      dockingAction.putValue(Action.SMALL_ICON, ChainsawIcons.ICON_DOCK);
    }

    /**
     * returns true if the DetailPane is viewable, that is
     * if the SplitPane's properties are such that the lower pane
     * would be seen, and so returns a boolean expression
     * of the SplitPanes divider location
     * @return
     */
    boolean isDetailPaneVisible() {
      double currentLoc = lowerPanel.getDividerLocation();
      double max = lowerPanel.getMaximumDividerLocation();

      return currentLoc < max;
    }

    /**
     * Shows or hides the Detail Pane depending on the Last
     * known position
     *
     */
    void toggleDetailPanel() {
      int currentPosition = lowerPanel.getDividerLocation();
      int lastPosition = lowerPanel.getLastDividerLocation();

      if (lastPosition == -1) {
        lowerPanel.setDividerLocation(1.0d);
        lowerPanel.setLastDividerLocation(currentPosition);
      } else {
        lowerPanel.setDividerLocation(lowerPanel.getLastDividerLocation());
        lowerPanel.setLastDividerLocation(currentPosition);
      }
    }

    void toggleLogTreePanel() {
      LogLog.debug(
        "Toggling logPanel, currently isVisible=" + logTreePanel.isVisible());
      logTreePanel.setVisible(!logTreePanel.isVisible());
      LogLog.debug(
        "Toggling logPanel, now isVisible=" + logTreePanel.isVisible());
    }

    public void saveSettings() {
      saveColumnSettings(identifier, table.getColumnModel());
      colorDisplaySelector.save();
      displayFilter.save();
    }

    public void saveSettings(SaveSettingsEvent event) {
      //not used..save of columns performed via tablecolumnmodellistener event callback
    }

    void saveColumnSettings(String ident, TableColumnModel model) {
      ObjectOutputStream o = null;

      try {
        File f =
          new File(
            SettingsManager.getInstance().getSettingsDirectory()
            + File.separator + ident + COLUMNS_EXTENSION);
        o = new ObjectOutputStream(
            new BufferedOutputStream(new FileOutputStream(f)));

        Enumeration e = model.getColumns();

        while (e.hasMoreElements()) {
          TableColumn c = (TableColumn) e.nextElement();
          o.writeObject(
            new TableColumnData(
              (String) c.getHeaderValue(), c.getModelIndex(), c.getWidth()));
        }

        o.flush();
      } catch (FileNotFoundException fnfe) {
        fnfe.printStackTrace();
      } catch (IOException ioe) {
        ioe.printStackTrace();
      } finally {
        try {
          if (o != null) {
            o.close();
          }
        } catch (IOException ioe) {
          ioe.printStackTrace();
        }
      }
    }

    /* (non-Javadoc)
     * @see org.apache.log4j.chainsaw.prefs.Profileable#loadSettings(org.apache.log4j.chainsaw.prefs.LoadSettingsEvent)
     */
    public void loadSettings(LoadSettingsEvent event) {
      File f =
        new File(
          SettingsManager.getInstance().getSettingsDirectory()
          + File.separator + identifier + COLUMNS_EXTENSION);

      if (f.exists()) {
        loadColumnSettings(identifier, table.getColumnModel());
      } else {
        loadDefaultColumnSettings(event);
      }
    }

    void loadDefaultColumnSettings(LoadSettingsEvent event) {
      String columnOrder = event.getSetting(TABLE_COLUMN_ORDER);

      TableColumnModel columnModel = table.getColumnModel();

      Map columnNameMap = new HashMap();

      for (int i = 0; i < columnModel.getColumnCount(); i++) {
        columnNameMap.put(table.getColumnName(i), columnModel.getColumn(i));
      }

      int index = 0;
      StringTokenizer tok = new StringTokenizer(columnOrder, ",");
      List sortedColumnList = new ArrayList();

      //remove all columns from the table that exist in the model
      //and add in the correct order to a new arraylist
      //(may be a subset of possible columns)
      while (tok.hasMoreElements()) {
        String element = (String) tok.nextElement();
        TableColumn column = (TableColumn) columnNameMap.get(element);

        if (column != null) {
          System.out.println(
            "Moving column " + element + " from index "
            + column.getModelIndex() + " to index " + index++);
          sortedColumnList.add(column);
          table.removeColumn(column);
        }
      }

      //re-add columns to the table in the order provided from the list
      for (Iterator iter = sortedColumnList.iterator(); iter.hasNext();) {
        TableColumn element = (TableColumn) iter.next();
        table.addColumn(element);
      }

      //    TODO Rest of the load settings
      String columnWidths = event.getSetting(TABLE_COLUMN_WIDTHS);

      //    System.out.println("Column widths=" + columnWidths);
      tok = new StringTokenizer(columnWidths, ",");
      index = 0;

      while (tok.hasMoreElements()) {
        String element = (String) tok.nextElement();

        try {
          int width = Integer.parseInt(element);

          if (index > (columnModel.getColumnCount() - 1)) {
            System.out.println(
              "loadsettings - failed attempt to set width for index " + index
              + ", width " + element);
          } else {
            columnModel.getColumn(index).setPreferredWidth(width);
          }

          index++;
        } catch (NumberFormatException e) {
          LogLog.error("Error decoding a Table width", e);
        }
      }

      SwingUtilities.invokeLater(
        new Runnable() {
          public void run() {
            repaint();
          }
        });
    }

    void loadColumnSettings(String ident, TableColumnModel model) {
      File f =
        new File(
          SettingsManager.getInstance().getSettingsDirectory()
          + File.separator + ident + COLUMNS_EXTENSION);

      if (f.exists()) {
        ArrayList newColumns = new ArrayList();

        TableColumnData temp = null;
        ObjectInputStream s = null;

        try {
          s = new ObjectInputStream(
              new BufferedInputStream(new FileInputStream(f)));

          while (true) {
            temp = (TableColumnData) s.readObject();

            TableColumn tc = new TableColumn(temp.getIndex(), temp.getWidth());
            tc.setHeaderValue(temp.getColName());
            newColumns.add(tc);
          }
        } catch (EOFException eof) { //end of file - ignore..
        }catch (IOException ioe) {
          ioe.printStackTrace();
        } catch (ClassNotFoundException cnfe) {
          cnfe.printStackTrace();
        } finally {
          if (s != null) {
            try {
              s.close();
            } catch (IOException ioe) {
              ioe.printStackTrace();
            }
          }
        }

        //only remove columns and add serialized columns if 
        //at least one column was read from the file
        if (newColumns.size() > 0) {
          //remove columns from model - will be re-added in the correct order
          for (int i = model.getColumnCount() - 1; i > -1; i--) {
            model.removeColumn(model.getColumn(i));
          }

          for (Iterator iter = newColumns.iterator(); iter.hasNext();) {
            model.addColumn((TableColumn) iter.next());
          }
        }
      }
    }

    /**
     * @return
     */
    public boolean isLogTreePanelVisible() {
      return logTreePanel.isVisible();
    }

    /* (non-Javadoc)
     * @see org.apache.log4j.chainsaw.EventBatchListener#getInterestedIdentifier()
     */
    public String getInterestedIdentifier() {
      return getIdentifier();
    }

    /* (non-Javadoc)
     * @see org.apache.log4j.chainsaw.EventBatchListener#receiveEventBatch(java.lang.String, java.util.List)
     */
    public void receiveEventBatch(String identifier, List eventBatchEntrys) {
      /**
       * if this panel is paused, we totally ignore events
       */
      if (isPaused()) {
        return;
      }

      table.getSelectionModel().setValueIsAdjusting(true);

      boolean rowAdded = false;
      LoggingEvent lastSelected = null;

      if (table.getSelectedRow() > -1) {
        lastSelected = tableModel.getRow(table.getSelectedRow());
      }

      for (Iterator iter = eventBatchEntrys.iterator(); iter.hasNext();) {
        ChainsawEventBatchEntry entry = (ChainsawEventBatchEntry) iter.next();

        //        Vector v = formatFields(entry.getEventVector());
        final String eventType = entry.getEventType();

        updateEntryMap(entry);

        boolean isCurrentRowAdded =
          tableModel.isAddRow(entry.getEvent(), true);
        rowAdded = rowAdded ? true : isCurrentRowAdded;
      }

      table.getSelectionModel().setValueIsAdjusting(false);

      //tell the model to notify the count listeners
      tableModel.notifyCountListeners();

      if (rowAdded) {
        tableModel.sort();

        if (scrollToBottom.isScrolled() && !scrollToBottom.isBypassed()) {
          table.scrollToBottom(
            table.columnAtPoint(table.getVisibleRect().getLocation()));
        } else {
          if (lastSelected != null) {
            table.scrollToRow(
              tableModel.getRowIndex(lastSelected),
              table.columnAtPoint(table.getVisibleRect().getLocation()));
          }
        }
      }
    }

    /**
     * ensures the Entry map of all the unque logger names etc, that is used for the Filter panel is
     * updated with any new information from the event
     * @param v
     * @param eventType
     * @param level
     */
    private void updateEntryMap(ChainsawEventBatchEntry entry) {
      LoggingEvent event = entry.getEvent();
      String eventType = entry.getEventType();
      String level = event.getLevel().toString();

      //add the level to the appropriate list if it didn't previously exist
      if (!((List) levelMap.get(eventType)).contains(level)) {
        ((List) levelMap.get(eventType)).add(level);
      }

      Map map = (HashMap) entryMap.get(getIdentifier());

      //        TODO fix up this Set Cast-O-Rama
      //also add it to the unique values list
      ((Set) map.get(ChainsawConstants.LEVEL_COL_NAME)).add(level);

      Object loggerName = event.getLoggerName();
      ((Set) map.get(ChainsawConstants.LOGGER_COL_NAME)).add(loggerName);

      /**
       * EventContainer is a LoggerNameModel imp, use that for notifing
       */
      tableModel.addLoggerName(loggerName.toString());

      ((Set) map.get(ChainsawConstants.THREAD_COL_NAME)).add(
        event.getThreadName());
      ((Set) map.get(ChainsawConstants.NDC_COL_NAME)).add(event.getNDC());

      //          TODO MDC event stuff is not being output correctly
      ((Set) map.get(ChainsawConstants.MDC_COL_NAME)).add(
        event.getMDCKeySet());

      if (event.getLocationInformation() != null) {
        LocationInfo info = event.getLocationInformation();
        ((Set) map.get(ChainsawConstants.CLASS_COL_NAME)).add(
          info.getClassName());
        ((Set) map.get(ChainsawConstants.METHOD_COL_NAME)).add(
          info.getMethodName());
        ((Set) map.get(ChainsawConstants.FILE_COL_NAME)).add(
          info.getFileName());
      }
    }

    /**
     * @return
     */
    public final Layout getDetailPaneLayout() {
      return detailPaneLayout;
    }

    /**
     * @param detailPaneLayout
     */
    public final void setDetailPaneLayout(Layout detailPaneLayout) {
      this.detailPaneLayout = detailPaneLayout;
    }

    /**
     * @return
     */
    public final Layout getToolTipLayout() {
      return toolTipLayout;
    }

    /**
     * @param toolTipLayout
     */
    public final void setToolTipLayout(Layout toolTipLayout) {
      this.toolTipLayout = toolTipLayout;
    }
  }

  class TableColumnData implements Serializable {
    static final long serialVersionUID = 5350440293110513986L;
    private String colName;
    private int index;
    private int width;

    public TableColumnData(String colName, int index, int width) {
      this.colName = colName;
      this.index = index;
      this.width = width;
    }

    public String getColName() {
      return colName;
    }

    public int getIndex() {
      return index;
    }

    public int getWidth() {
      return width;
    }

    private void readObject(java.io.ObjectInputStream in)
      throws IOException, ClassNotFoundException {
      colName = (String) in.readObject();
      index = in.readInt();
      width = in.readInt();
    }

    private void writeObject(java.io.ObjectOutputStream out)
      throws IOException {
      out.writeObject(colName);
      out.writeInt(index);
      out.writeInt(width);
    }
  }

  /**
   * Thread that periodically checks if the selected row has changed, and if
   * it was, updates the Detail Panel with the detailed Logging information
   */
  class DetailPaneUpdater {
    private int selectedRow = -1;
    private int lastRow = -1;
    private final JEditorPane pane;
    private final EventContainer model;
    private final LogPanel panel;

    public DetailPaneUpdater(
      LogPanel panel, JEditorPane pane, EventContainer model) {
      this.pane = pane;
      this.model = model;
      this.panel = panel;
    }

    public void setSelectedRow(int row) {
      if (row == -1) {
        lastRow = 0;
      }

      selectedRow = row;
      updateDetailPane();
    }

    private void updateDetailPane() {
      String text = null;

      if (selectedRow != lastRow) {
        if (selectedRow == -1) {
          text = "Nothing selected";
        } else {
          LoggingEvent event = model.getRow(selectedRow);

          if (event != null) {
            Layout layout = panel.getDetailPaneLayout();
            StringBuffer buf = new StringBuffer();
            buf.append(layout.getHeader()).append(layout.format(event)).append(
              layout.getFooter());
            text = buf.toString();
          }
        }

        if (!((text != null) && !text.equals(""))) {
          text = "Nothing selected";
        }

        lastRow = selectedRow;

        final String text2 = text;
        SwingUtilities.invokeLater(
          new Runnable() {
            public void run() {
              pane.setText(text2);
            }
          });
        SwingUtilities.invokeLater(
          new Runnable() {
            public void run() {
              pane.setCaretPosition(0);
            }
          });
      }
    }
  }

  class TabIconHandler implements EventCountListener {
    private final String ident;
    private int lastCount;
    private int currentCount;

    //the tabIconHandler is associated with a new tab, and a new tab always
    //has new events
    private boolean hasNewEvents = true;
    ImageIcon NEW_EVENTS = new ImageIcon(ChainsawIcons.TOOL_TIP);
    ImageIcon HAS_EVENTS = new ImageIcon(ChainsawIcons.INFO);

    public TabIconHandler(final String ident) {
      this.ident = ident;

      new Thread(
        new Runnable() {
          public void run() {
            while (true) {
              //if this tab is active, remove the icon
              if (
                (tabbedPane.getSelectedIndex() > -1)
                  && (tabbedPane.getSelectedIndex() == tabbedPane.indexOfTab(
                    ident))) {
                tabbedPane.setIconAt(tabbedPane.indexOfTab(ident), null);

                //reset fields so no icon will display 
                lastCount = currentCount;
                hasNewEvents = false;
              } else {
                //don't process undocked tabs
                if (tabbedPane.indexOfTab(ident) > -1) {
                  //if the tab is not active and the counts don't match, set the new events icon
                  if (lastCount != currentCount) {
                    tabbedPane.setIconAt(
                      tabbedPane.indexOfTab(ident), NEW_EVENTS);
                    lastCount = currentCount;
                    hasNewEvents = true;
                  } else {
                    if (hasNewEvents) {
                      tabbedPane.setIconAt(
                        tabbedPane.indexOfTab(ident), HAS_EVENTS);
                    }
                  }
                }
              }

              try {
                Thread.sleep(handler.getQueueInterval() + 1000);
              } catch (InterruptedException ie) {
              }
            }
          }
        }).start();
    }

    public void eventCountChanged(int currentCount, int totalCount) {
      this.currentCount = currentCount;
    }
  }

  class ScrollToBottom extends Thread {
    boolean scrollToBottom;
    boolean bypassed;

    public ScrollToBottom(boolean scrollToBottom) {
      this.scrollToBottom = scrollToBottom;
    }

    public void scroll(boolean scrollToBottom) {
      this.scrollToBottom = scrollToBottom;
    }

    public boolean isScrolled() {
      return scrollToBottom;
    }

    public void bypass(boolean bypassed) {
      this.bypassed = bypassed;
    }

    public boolean isBypassed() {
      return bypassed;
    }
  }

  //if columnmoved or columnremoved callback received, re-apply table's sort index based
  //sort column name
  class ChainsawTableColumnModelListener implements TableColumnModelListener {
    private JSortTable table;

    public ChainsawTableColumnModelListener(JSortTable table) {
      this.table = table;
    }

    public void columnAdded(TableColumnModelEvent e) {
    }

    public void columnRemoved(TableColumnModelEvent e) {
      table.updateSortedColumn();
    }

    public void columnMoved(TableColumnModelEvent e) {
      table.updateSortedColumn();
    }

    public void columnMarginChanged(ChangeEvent e) {
    }

    public void columnSelectionChanged(ListSelectionEvent e) {
    }
  }
}
