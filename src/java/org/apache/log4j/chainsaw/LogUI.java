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

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Priority;
import org.apache.log4j.UtilLoggingLevel;
import org.apache.log4j.chainsaw.help.Tutorial;
import org.apache.log4j.chainsaw.icons.ChainsawIcons;
import org.apache.log4j.chainsaw.prefs.LoadSettingsEvent;
import org.apache.log4j.chainsaw.prefs.SaveSettingsEvent;
import org.apache.log4j.chainsaw.prefs.SettingsListener;
import org.apache.log4j.chainsaw.prefs.SettingsManager;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.net.SocketNodeEventListener;
import org.apache.log4j.plugins.Plugin;
import org.apache.log4j.plugins.PluginEvent;
import org.apache.log4j.plugins.PluginListener;
import org.apache.log4j.plugins.PluginRegistry;
import org.apache.log4j.plugins.Receiver;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import java.lang.reflect.Method;

import java.net.URL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;


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
  static final String USE_CYCLIC_BUFFER_PROP_NAME = "chainsaw.usecyclicbuffer";
  static final String CYCLIC_BUFFER_SIZE_PROP_NAME =
    "chainsaw.cyclicbuffersize";
  private static final String MAIN_WINDOW_HEIGHT = "main.window.height";
  private static final String MAIN_WINDOW_WIDTH = "main.window.width";
  private static final String MAIN_WINDOW_Y = "main.window.y";
  private static final String MAIN_WINDOW_X = "main.window.x";
  static final String TABLE_COLUMN_ORDER = "table.columns.order";
  static final String TABLE_COLUMN_WIDTHS = "table.columns.widths";
  private static final String LOOK_AND_FEEL = "LookAndFeel";
  private static final String STATUS_BAR = "StatusBar";
  static final String COLUMNS_EXTENSION = ".columns";
  private static ChainsawSplash splash;
  private URL configURLToUse;
  private boolean noReceiversDefined;
  private ReceiversPanel receiversPanel;
  private ChainsawTabbedPane tabbedPane;
  private JToolBar toolbar;
  private ChainsawStatusBar statusBar;
  private final Map tableModelMap = new HashMap();
  private final Map tableMap = new HashMap();
  private final List filterableColumns = new ArrayList();
  private final Map panelMap = new HashMap();
  ChainsawAppenderHandler handler;
  private ChainsawToolBarAndMenus tbms;
  private ChainsawAbout aboutBox;
  private final SettingsManager sm = SettingsManager.getInstance();
  private String lookAndFeelClassName;
  private final JFrame tutorialFrame = new JFrame("Chainsaw Tutorial");

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
  private WelcomePanel welcomePanel;

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
    setToolBarAndMenus(new ChainsawToolBarAndMenus(this));
    toolbar = getToolBarAndMenus().getToolbar();
    setJMenuBar(getToolBarAndMenus().getMenubar());
    setTabbedPane(new ChainsawTabbedPane());
    getTabbedPane().addChangeListener(getToolBarAndMenus().getPanelListener());
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

    getToolBarAndMenus().stateChange();
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
    welcomePanel = new WelcomePanel(this);

    final SocketNodeEventListener socketListener =
      new SocketNodeEventListener() {
        public void socketOpened(String remoteInfo) {
          statusBar.remoteConnectionReceived(remoteInfo);
        }

        public void socketClosedEvent(Exception e) {
          statusBar.setMessage("Collection lost! :: " + e.getMessage());
        }
      };

    PluginListener pluginListener =
      new PluginListener() {
        public void pluginStarted(PluginEvent e) {
          statusBar.setMessage(e.getPlugin().getName() + " started!");

          Method method = getAddListenerMethod(e.getPlugin());

          if (method != null) {
            try {
              method.invoke(e.getPlugin(), new Object[] { socketListener });
            } catch (Exception ex) {
              LogLog.error("Failed to add a SocketNodeEventListener", ex);
            }
          }
        }

        Method getRemoveListenerMethod(Plugin p) {
          try {
            return p.getClass().getMethod(
              "removeSocketNodeEventListener",
              new Class[] { SocketNodeEventListener.class });
          } catch (Exception e) {
            return null;
          }
        }

        Method getAddListenerMethod(Plugin p) {
          try {
            return p.getClass().getMethod(
              "addSocketNodeEventListener",
              new Class[] { SocketNodeEventListener.class });
          } catch (Exception e) {
            return null;
          }
        }

        public void pluginStopped(PluginEvent e) {
          Method method = getRemoveListenerMethod(e.getPlugin());

          if (method != null) {
            try {
              method.invoke(e.getPlugin(), new Object[] { socketListener });
            } catch (Exception ex) {
              LogLog.error("Failed to remove SocketNodeEventListener", ex);
            }
          }

          statusBar.setMessage(e.getPlugin().getName() + " stopped!");
        }
      };

    PluginRegistry.addPluginListener(pluginListener);

    getSettingsManager().configure(
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

    getSettingsManager().configure(
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

    if (
      PluginRegistry.getPlugins(
          LogManager.getLoggerRepository(), Receiver.class).size() == 0) {
      noReceiversDefined = true;
    }

    initGUI();

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

    //    getLevelMap().put(ChainsawConstants.UTIL_LOGGING_EVENT_TYPE, utilLevels);
    //    getLevelMap().put(ChainsawConstants.LOG4J_EVENT_TYPE, priorityLevels);
    getFilterableColumns().add(ChainsawConstants.LEVEL_COL_NAME);
    getFilterableColumns().add(ChainsawConstants.LOGGER_COL_NAME);
    getFilterableColumns().add(ChainsawConstants.THREAD_COL_NAME);
    getFilterableColumns().add(ChainsawConstants.NDC_COL_NAME);
    getFilterableColumns().add(ChainsawConstants.MDC_COL_NAME);
    getFilterableColumns().add(ChainsawConstants.CLASS_COL_NAME);
    getFilterableColumns().add(ChainsawConstants.METHOD_COL_NAME);
    getFilterableColumns().add(ChainsawConstants.FILE_COL_NAME);
    getFilterableColumns().add(ChainsawConstants.NONE_COL_NAME);

    JPanel panePanel = new JPanel();
    panePanel.setLayout(new BorderLayout(2, 2));

    getContentPane().setLayout(new BorderLayout());

    getTabbedPane().addChangeListener(getToolBarAndMenus());
    getTabbedPane().addChangeListener(
      new ChangeListener() {
        //received a statechange event - selection changed - remove icon from selected index
        public void stateChanged(ChangeEvent e) {
          if (
            getTabbedPane().getSelectedComponent() instanceof ChainsawTabbedPane) {
            if (getTabbedPane().getSelectedIndex() > -1) {
              getTabbedPane().setIconAt(
                getTabbedPane().getSelectedIndex(), null);
            }
          }
        }
      });

    KeyStroke ksRight =
      KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, Event.CTRL_MASK);
    KeyStroke ksLeft =
      KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, Event.CTRL_MASK);

    getTabbedPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
      ksRight, "MoveRight");
    getTabbedPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
      ksLeft, "MoveLeft");

    Action moveRight =
      new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          int temp = getTabbedPane().getSelectedIndex();
          ++temp;

          if (temp != getTabbedPane().getTabCount()) {
            getTabbedPane().setSelectedTab(temp);
          }
        }
      };

    Action moveLeft =
      new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          int temp = getTabbedPane().getSelectedIndex();
          --temp;

          if (temp > -1) {
            getTabbedPane().setSelectedTab(temp);
          }
        }
      };

    getTabbedPane().getActionMap().put("MoveRight", moveRight);
    getTabbedPane().getActionMap().put("MoveLeft", moveLeft);

    /**
     * We listen for double clicks, and auto-undock currently
     * selected Tab if the mouse event location matches the currently selected
     * tab
     */
    getTabbedPane().addMouseListener(
      new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
          super.mouseClicked(e);

          if (
            (e.getClickCount() > 1)
              && ((e.getModifiers() & InputEvent.BUTTON1_MASK) > 0)) {
            int tabIndex = getTabbedPane().getSelectedIndex();

            if (
              (tabIndex != -1)
                && (tabIndex == getTabbedPane().getSelectedIndex())) {
              LogPanel logPanel = getCurrentLogPanel();

              if (logPanel != null) {
                logPanel.undock();
              }
            }
          }
        }
      });

    addWelcomePanel();
    panePanel.add(getTabbedPane());

    getContentPane().add(toolbar, BorderLayout.NORTH);
    getContentPane().add(panePanel, BorderLayout.CENTER);
    getContentPane().add(statusBar, BorderLayout.SOUTH);
    receiversPanel.setVisible(false);
    getContentPane().add(receiversPanel, BorderLayout.EAST);

    addWindowListener(
      new WindowAdapter() {
        public void windowClosing(WindowEvent event) {
          exit();
        }
      });

    pack();

    this.handler.addPropertyChangeListener(
      "dataRate",
      new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
          double dataRate = ((Double) evt.getNewValue()).doubleValue();
          statusBar.setDataRate(dataRate);
        }
      });

    getSettingsManager().addSettingsListener(this);
    getSettingsManager().addSettingsListener(getToolBarAndMenus());
    getSettingsManager().loadSettings();

    setVisible(true);

    removeSplash();

    synchronized (initializationLock) {
      isGUIFullyInitialized = true;
      initializationLock.notifyAll();
    }

    if (noReceiversDefined) {
      showNoReceiversWarningPanel();
    }

    Container container = tutorialFrame.getContentPane();
    final JEditorPane tutorialArea = new JEditorPane();
    tutorialArea.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
    tutorialArea.setEditable(false);
    container.setLayout(new BorderLayout());

    try {
      tutorialArea.setPage(getWelcomePanel().getTutorialURL());
      container.add(new JScrollPane(tutorialArea), BorderLayout.CENTER);
    } catch (Exception e) {
      LogLog.error("Error occurred loading the Tutorial", e);
    }

    tutorialFrame.setSize(new Dimension(640, 480));

    final Action startTutorial =
      new AbstractAction(
        "Start Tutorial", new ImageIcon(ChainsawIcons.ICON_RESUME_RECEIVER)) {
        public void actionPerformed(ActionEvent e) {
          if (
            JOptionPane.showConfirmDialog(
                null,
                "This will start 3 \"Generator\" receivers for use in the Tutorial.  Is that ok?",
                "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            new Thread(new Tutorial()).start();
            putValue("TutorialStarted", Boolean.TRUE);
          } else {
            putValue("TutorialStarted", Boolean.FALSE);
          }
        }
      };

    final Action stopTutorial =
      new AbstractAction(
        "Stop Tutorial", new ImageIcon(ChainsawIcons.ICON_STOP_RECEIVER)) {
        public void actionPerformed(ActionEvent e) {
          if (
            JOptionPane.showConfirmDialog(
                null,
                "This will stop all of the \"Generator\" receivers used in the Tutorial, but leave any other Receiver untouched.  Is that ok?",
                "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            new Thread(
              new Runnable() {
                public void run() {
                  List list =
                    PluginRegistry.getPlugins(
                      LogManager.getLoggerRepository(), Generator.class);

                  for (Iterator iter = list.iterator(); iter.hasNext();) {
                    Plugin plugin = (Plugin) iter.next();
                    PluginRegistry.stopPlugin(plugin);
                  }
                }
              }).start();
            setEnabled(false);
            startTutorial.putValue("TutorialStarted", Boolean.FALSE);
          }
        }
      };

    stopTutorial.putValue(
      Action.SHORT_DESCRIPTION,
      "Removes all of the Tutorials Generator Receivers, leaving all other Receivers untouched");
    startTutorial.putValue(
      Action.SHORT_DESCRIPTION,
      "Begins the Tutorial, starting up some Generator Receivers so you can see Chainsaw in action");
    stopTutorial.setEnabled(false);

    final SmallToggleButton startButton = new SmallToggleButton(startTutorial);
    PropertyChangeListener pcl =
      new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
          stopTutorial.setEnabled(
            ((Boolean) startTutorial.getValue("TutorialStarted")) == Boolean.TRUE);
          startButton.setSelected(stopTutorial.isEnabled());
        }
      };

    startTutorial.addPropertyChangeListener(pcl);
    stopTutorial.addPropertyChangeListener(pcl);

    final SmallButton stopButton = new SmallButton(stopTutorial);

    final JToolBar tutorialToolbar = new JToolBar();
    tutorialToolbar.setFloatable(false);
    tutorialToolbar.add(startButton);
    tutorialToolbar.add(stopButton);
    container.add(tutorialToolbar, BorderLayout.NORTH);
    tutorialArea.addHyperlinkListener(
      new HyperlinkListener() {
        public void hyperlinkUpdate(HyperlinkEvent e) {
          if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            if (e.getDescription().equals("StartTutorial")) {
              startTutorial.actionPerformed(null);
            } else if (e.getDescription().equals("StopTutorial")) {
              stopTutorial.actionPerformed(null);
            } else {
              try {
                tutorialArea.setPage(e.getURL());
              } catch (IOException e1) {
                LogLog.error("Failed to change the URL for the Tutorial", e1);
              }
            }
          }
        }
      });
  }

  /**
    * Displays a warning dialog about having no Receivers defined
    * and allows the user to choose some options for configuration
    */
  private void showNoReceiversWarningPanel() {
    final NoReceiversWarningPanel noReceiversWarningPanel =
      new NoReceiversWarningPanel();

    final SettingsListener sl =
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
      };

    /**
         * This listener sets up the NoReciversWarningPanel and
         * loads saves the configs/logfiles
         */
    getSettingsManager().addSettingsListener(sl);
    getSettingsManager().configure(sl);

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
          } else if (noReceiversWarningPanel.getModel().isSimpleReceiverMode()) {
            int port = noReceiversWarningPanel.getModel().getSimplePort();
            Class receiverClass =
              noReceiversWarningPanel.getModel().getSimpleReceiverClass();

            try {
              Receiver simpleReceiver = (Receiver) receiverClass.newInstance();
              simpleReceiver.setName("Simple Receiver");

              Method portMethod =
                simpleReceiver.getClass().getMethod(
                  "setPort", new Class[] { int.class });
              portMethod.invoke(
                simpleReceiver, new Object[] { new Integer(port) });

              simpleReceiver.setThreshold(Level.DEBUG);

              PluginRegistry.startPlugin(simpleReceiver);
              receiversPanel.updateReceiverTreeInDispatchThread();
            } catch (Exception e) {
              LogLog.error("Error creating Receiver", e);
              getStatusBar().setMessage(
                "An error occurred creating your Receiver");
            }
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

                    LogManager.getLoggerRepository().getRootLogger()
                              .addAppender(handler);

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
    getSettingsManager().saveSettings();

    int tabCount = getTabbedPane().getTabCount();

    for (int i = 0; i < tabCount; i++) {
      Component c = getTabbedPane().getComponentAt(i);

      if (c instanceof LogPanel) {
        ((LogPanel) c).saveSettings();
      }
    }

    shutdown();
  }

  void addWelcomePanel() {
    getTabbedPane().addANewTab(
      "Welcome", welcomePanel, new ImageIcon(ChainsawIcons.ABOUT),
      "Welcome/Help");
  }

  void removeWelcomePanel() {
    if (getTabbedPane().containsWelcomePanel()) {
      getTabbedPane().remove(
        getTabbedPane().getComponentAt(getTabbedPane().indexOfTab("Welcome")));
    }
  }

  void toggleReceiversPanel() {
    SwingUtilities.invokeLater(
      new Runnable() {
        public void run() {
          receiversPanel.setVisible(!receiversPanel.isVisible());
          receiversPanel.invalidate();
          receiversPanel.validate();

          getToolBarAndMenus().stateChange();
        }
      });
  }

  boolean isReceiverPanelVisible() {
    return receiversPanel.isVisible();
  }

  public ChainsawStatusBar getStatusBar() {
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
    Set panelSet = getPanelMap().entrySet();
    Iterator iter = panelSet.iterator();

    while (iter.hasNext()) {
      Map.Entry entry = (Map.Entry) iter.next();
      m.put(
        entry.getKey(),
        new Boolean(((DockablePanel) entry.getValue()).isDocked()));
    }

    return m;
  }

  void displayPanel(String panelName, boolean display) {
    Object o = getPanelMap().get(panelName);

    if (o instanceof LogPanel) {
      LogPanel p = (LogPanel) o;

      int index = getTabbedPane().indexOfTab(panelName);

      if ((index == -1) && display) {
        getTabbedPane().addTab(panelName, p);
      }

      if ((index > -1) && !display) {
        getTabbedPane().removeTabAt(index);
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
    Component selectedTab = getTabbedPane().getSelectedComponent();

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
    int index = getTabbedPane().getSelectedIndex();

    if (index == -1) {
      return null;
    } else {
      return getTabbedPane().getTitleAt(index);
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
    if (
      UIManager.getLookAndFeel().getClass().getName().equals(
          lookAndFeelClassName)) {
      LogLog.debug("No need to change L&F, already the same");

      return;
    }

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
    getWelcomePanel().setURL(url);
  }

  /**
  * @return
  */
  private WelcomePanel getWelcomePanel() {
    return welcomePanel;
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

  //  public Map getEntryMap() {
  //    return entryMap;
  //  }
  //  public Map getScrollMap() {
  //    return scrollMap;
  //  }
  public Map getPanelMap() {
    return panelMap;
  }

  //  public Map getLevelMap() {
  //    return levelMap;
  //  }
  public SettingsManager getSettingsManager() {
    return sm;
  }

  public List getFilterableColumns() {
    return filterableColumns;
  }

  public void setToolBarAndMenus(ChainsawToolBarAndMenus tbms) {
    this.tbms = tbms;
  }

  public ChainsawToolBarAndMenus getToolBarAndMenus() {
    return tbms;
  }

  public Map getTableMap() {
    return tableMap;
  }

  public Map getTableModelMap() {
    return tableModelMap;
  }

  public void setTabbedPane(ChainsawTabbedPane tabbedPane) {
    this.tabbedPane = tabbedPane;
  }

  public ChainsawTabbedPane getTabbedPane() {
    return tabbedPane;
  }

  /**
   *
   */
  public void setupTutorial() {
    SwingUtilities.invokeLater(new Runnable(){

      public void run() {
    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    setLocation(0, getLocation().y);

    double chainsawwidth = 0.7;
    double tutorialwidth = 1 - chainsawwidth;
    setSize((int) (screen.width * chainsawwidth), getSize().height);
    invalidate();
    validate();    
    Dimension size = getSize();
    Point loc = getLocation();
    tutorialFrame.setSize((int) (screen.width * tutorialwidth), size.height);
    tutorialFrame.setLocation(loc.x + size.width, loc.y);
    tutorialFrame.setVisible(true);
      }});
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

      if (!getPanelMap().containsKey(ident)) {
        final String eventType =
          ((ChainsawEventBatchEntry) eventBatchEntrys.get(0)).getEventType();

        final LogPanel thisPanel =
          new LogPanel(getStatusBar(), ident, eventType);

        thisPanel.addEventCountListener(new TabIconHandler(ident));

        thisPanel.addPropertyChangeListener(
          new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
              tbms.stateChange();
            }
          });
        thisPanel.addPropertyChangeListener(
          "docked",
          new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
              LogPanel logPanel = (LogPanel) evt.getSource();

              if (logPanel.isDocked()) {
                getPanelMap().put(logPanel.getIdentifier(), logPanel);
                getTabbedPane().addANewTab(
                  logPanel.getIdentifier(), logPanel, null);
              } else {
                getTabbedPane().remove(logPanel);
              }
            }
          });

        getTabbedPane().add(ident, thisPanel);
        getPanelMap().put(ident, thisPanel);

        getSettingsManager().configure(thisPanel);

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
              getTabbedPane().addANewTab(
                ident, thisPanel, new ImageIcon(
                  ChainsawIcons.ANIM_RADIO_TOWER));
            }
          });

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

  class TabIconHandler implements EventCountListener {
    private final String ident;
    private int lastCount;
    private int currentCount;

    //the tabIconHandler is associated with a new tab, and a new tab always
    //has new events
    private boolean hasNewEvents = true;
    ImageIcon NEW_EVENTS = new ImageIcon(ChainsawIcons.ANIM_RADIO_TOWER);
    ImageIcon HAS_EVENTS = new ImageIcon(ChainsawIcons.INFO);

    public TabIconHandler(final String ident) {
      this.ident = ident;

      new Thread(
        new Runnable() {
          public void run() {
            while (true) {
              //if this tab is active, remove the icon
              if (
                (getTabbedPane().getSelectedIndex() > -1)
                  && (getTabbedPane().getSelectedIndex() == getTabbedPane()
                                                                .indexOfTab(
                    ident))) {
                getTabbedPane().setIconAt(
                  getTabbedPane().indexOfTab(ident), null);

                //reset fields so no icon will display 
                lastCount = currentCount;
                hasNewEvents = false;
              } else {
                //don't process undocked tabs
                if (getTabbedPane().indexOfTab(ident) > -1) {
                  //if the tab is not active and the counts don't match, set the new events icon
                  if (lastCount != currentCount) {
                    getTabbedPane().setIconAt(
                      getTabbedPane().indexOfTab(ident), NEW_EVENTS);
                    lastCount = currentCount;
                    hasNewEvents = true;
                  } else {
                    if (hasNewEvents) {
                      getTabbedPane().setIconAt(
                        getTabbedPane().indexOfTab(ident), HAS_EVENTS);
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
}
