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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.chainsaw.help.HelpManager;
import org.apache.log4j.chainsaw.help.Tutorial;
import org.apache.log4j.chainsaw.helper.SwingHelper;
import org.apache.log4j.chainsaw.icons.ChainsawIcons;
import org.apache.log4j.chainsaw.icons.LineIconFactory;
import org.apache.log4j.chainsaw.messages.MessageCenter;
import org.apache.log4j.chainsaw.plugins.ChainsawCentral;
import org.apache.log4j.chainsaw.plugins.PluginClassLoaderFactory;
import org.apache.log4j.chainsaw.prefs.LoadSettingsEvent;
import org.apache.log4j.chainsaw.prefs.SaveSettingsEvent;
import org.apache.log4j.chainsaw.prefs.SettingsListener;
import org.apache.log4j.chainsaw.prefs.SettingsManager;
import org.apache.log4j.chainsaw.receivers.ReceiversPanel;
import org.apache.log4j.chainsaw.version.VersionManager;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.joran.JoranConfigurator;
import org.apache.log4j.net.SocketNodeEventListener;
import org.apache.log4j.plugins.Plugin;
import org.apache.log4j.plugins.PluginEvent;
import org.apache.log4j.plugins.PluginListener;
import org.apache.log4j.plugins.PluginRegistry;
import org.apache.log4j.plugins.Receiver;
import org.apache.log4j.rule.ExpressionRule;
import org.apache.log4j.rule.Rule;
import org.apache.log4j.spi.LoggingEvent;


/**
 * The main entry point for Chainsaw, this class represents the first frame
 * that is used to display a Welcome panel, and any other panels that are
 * generated because Logging Events are streamed via a Receiver, or other
 * mechanism.
 *
 * @author Scott Deboy <sdeboy@apache.org>
 * @author Paul Smith  <psmith@apache.org>
 *
 */
public class LogUI extends JFrame implements ChainsawViewer, SettingsListener {
  private static final String CONFIG_FILE_TO_USE = "config.file";
  private static final String MAIN_WINDOW_HEIGHT = "main.window.height";
  private static final String MAIN_WINDOW_WIDTH = "main.window.width";
  private static final String MAIN_WINDOW_Y = "main.window.y";
  private static final String MAIN_WINDOW_X = "main.window.x";
  private static ChainsawSplash splash;
  private static final double DEFAULT_MAIN_RECEIVER_SPLIT_LOCATION = .8d;
  private final JFrame preferencesFrame = new JFrame();
  private URL configURLToUse;
  private boolean noReceiversDefined;
  private ReceiversPanel receiversPanel;
  private ChainsawTabbedPane tabbedPane;
  private JToolBar toolbar;
  private ChainsawStatusBar statusBar;
  private final ApplicationPreferenceModel applicationPreferenceModel =
    new ApplicationPreferenceModel();
  private final ApplicationPreferenceModelPanel applicationPreferenceModelPanel =
    new ApplicationPreferenceModelPanel(applicationPreferenceModel);
  private final Map tableModelMap = new HashMap();
  private final Map tableMap = new HashMap();
  private final List filterableColumns = new ArrayList();
  private final Map panelMap = new HashMap();
  ChainsawAppenderHandler handler;
  private ChainsawToolBarAndMenus tbms;
  private ChainsawAbout aboutBox;
  private final SettingsManager sm = SettingsManager.getInstance();
  private final JFrame tutorialFrame = new JFrame("Chainsaw Tutorial");
  private JSplitPane mainReceiverSplitPane;
  private double lastMainReceiverSplitLocation =
    DEFAULT_MAIN_RECEIVER_SPLIT_LOCATION;
  private final List identifierPanels = new ArrayList();
  private int dividerSize;
  private int cyclicBufferSize;

  /**
   * Set to true, if and only if the GUI has completed it's full
   * initialization. Any logging events that come in must wait until this is
   * true, and if it is false, should wait on the initializationLock object
   * until notified.
   */
  private boolean isGUIFullyInitialized = false;
  private Object initializationLock = new Object();

  /**
   * The shutdownAction is called when the user requests to exit Chainsaw, and
   * by default this exits the VM, but a developer may replace this action with
   * something that better suits their needs
   */
  private Action shutdownAction = null;

  /**
   * Clients can register a ShutdownListener to be notified when the user has
   * requested Chainsaw to exit.
   */
  private EventListenerList shutdownListenerList = new EventListenerList();
  private WelcomePanel welcomePanel;
  private PluginRegistry pluginRegistry;

  /**
   * Constructor which builds up all the visual elements of the frame including
   * the Menu bar
   */
  public LogUI() {
    super("Chainsaw v2 - Log Viewer");
    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

    if (ChainsawIcons.WINDOW_ICON != null) {
      setIconImage(new ImageIcon(ChainsawIcons.WINDOW_ICON).getImage());
    }
  }

  private static final void showSplash(Frame owner) {
    splash = new ChainsawSplash(owner);
    SwingHelper.centerOnScreen(splash);
    splash.setVisible(true);
  }

  private static final void removeSplash() {
    if (splash != null) {
      splash.setVisible(false);
      splash.dispose();
    }
  }

  /**
   * Registers a ShutdownListener with this calss so that it can be notified
   * when the user has requested that Chainsaw exit.
   *
   * @param l
   */
  public void addShutdownListener(ShutdownListener l) {
    shutdownListenerList.add(ShutdownListener.class, l);
  }

  /**
   * Removes the registered ShutdownListener so that the listener will not be
   * notified on a shutdown.
   *
   * @param l
   */
  public void removeShutdownListener(ShutdownListener l) {
    shutdownListenerList.remove(ShutdownListener.class, l);
  }

  /**
   * Starts Chainsaw by attaching a new instance to the Log4J main root Logger
   * via a ChainsawAppender, and activates itself
   *
   * @param args
   */
  public static void main(String[] args) {
    ApplicationPreferenceModel model = new ApplicationPreferenceModel();

    SettingsManager.getInstance().configure(model);

    applyLookAndFeel(model.getLookAndFeelClassName());

    createChainsawGUI(model, null);
  }

  /**
   * Creates, activates, and then shows the Chainsaw GUI, optionally showing
   * the splash screen, and using the passed shutdown action when the user
   * requests to exit the application (if null, then Chainsaw will exit the vm)
   *
   * @param showSplash
   * @param shutdownAction
   *                    DOCUMENT ME!
   */
  public static void createChainsawGUI(
    ApplicationPreferenceModel model, Action newShutdownAction) {
    
    if (model.isOkToRemoveSecurityManager()) {
			MessageCenter
					.getInstance()
					.addMessage(
							"User has authorised removal of Java Security Manager via preferences");
			System.setSecurityManager(null);
            // this SHOULD set the Policy/Permission stuff for any
            // code loaded from our custom classloader.  
            // crossing fingers...
			Policy.setPolicy(new Policy() {

				public void refresh() {
				}

				public PermissionCollection getPermissions(CodeSource codesource) {
					Permissions perms = new Permissions();
					perms.add(new AllPermission());
					return (perms);
				}
			});
    }
    LogLog.info("SecurityManager is now: " + System.getSecurityManager());
    
    
    LogUI logUI = new LogUI();

    if (model.isShowSplash()) {
      showSplash(logUI);
    }
    logUI.cyclicBufferSize = model.getCyclicBufferSize();

    logUI.handler = new ChainsawAppenderHandler();
    logUI.handler.addEventBatchListener(logUI.new NewTabEventBatchReceiver());
    
    
    /**
     * TODO until we work out how JoranConfigurator might be able to have
     * configurable class loader, if at all.  For now we temporarily replace the
     * TCCL so that Plugins that need access to resources in 
     * the Plugins directory can find them (this is particularly
     * important for the Web start version of Chainsaw
     */ 
    ClassLoader classLoader = PluginClassLoaderFactory.getInstance().getClassLoader();
    ClassLoader previousTCCL = Thread.currentThread().getContextClassLoader();
    
    String config = model.getConfigurationURL();
    if(config!=null && (!(config.trim().equals("")))) {
        config = config.trim();
        LogLog.info("Using '" + config + "' for auto-configuration");
        try {
          // we temporarily swap the TCCL so that plugins can find resources
          Thread.currentThread().setContextClassLoader(classLoader);
          JoranConfigurator jc = new JoranConfigurator();
          jc.doConfigure(new URL(config), LogManager.getLoggerRepository());
          jc.logErrors();
        } catch (MalformedURLException e) {
          LogLog.error("Failed to use the auto-configuration file", e);
        }finally{
            // now switch it back...
            Thread.currentThread().setContextClassLoader(previousTCCL);
        }
    }else {
        LogLog.info("No auto-configuration file found within the ApplicationPreferenceModel");
    }
    
    LogManager.getRootLogger().addAppender(logUI.handler);
    logUI.activateViewer();

    logUI.getApplicationPreferenceModel().apply(model);

    logUI.checkForNewerVersion();
    
    if (newShutdownAction != null) {
      logUI.setShutdownAction(newShutdownAction);
    } else {
      logUI.setShutdownAction(
        new AbstractAction() {
          public void actionPerformed(ActionEvent e) {
            System.exit(0);
          }
        });
    }
  }

  /**
   * Allow Chainsaw v2 to be ran in-process (configured as a ChainsawAppender)
   * NOTE: Closing Chainsaw will NOT stop the application generating the events.
   * @param appender
   *
   */
  public void activateViewer(ChainsawAppender appender) {
    ApplicationPreferenceModel model = new ApplicationPreferenceModel();
    SettingsManager.getInstance().configure(model);

    cyclicBufferSize = model.getCyclicBufferSize();
    applyLookAndFeel(model.getLookAndFeelClassName());

    handler = new ChainsawAppenderHandler(appender);
    handler.addEventBatchListener(new NewTabEventBatchReceiver());
    LogManager.getRootLogger().addAppender(appender);
    setShutdownAction(
      new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
        }
      });
    activateViewer();

    getApplicationPreferenceModel().apply(model);
  }

  /**
   * Initialises the menu's and toolbars, but does not actually create any of
   * the main panel components.
   *
   */
  private void initGUI() {
    setupHelpSystem();
    statusBar = new ChainsawStatusBar();
    setupReceiverPanel();

    setToolBarAndMenus(new ChainsawToolBarAndMenus(this));
    toolbar = getToolBarAndMenus().getToolbar();
    setJMenuBar(getToolBarAndMenus().getMenubar());
    setTabbedPane(new ChainsawTabbedPane());

    applicationPreferenceModelPanel.setOkCancelActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          preferencesFrame.setVisible(false);
        }
      });
  }

  private void initPlugins(PluginRegistry pluginRegistry) {
    pluginRegistry.addPluginListener(
      new PluginListener() {
        public void pluginStarted(PluginEvent e) {
          if (e.getPlugin() instanceof JComponent) {
            JComponent plugin = (JComponent) e.getPlugin();
            getTabbedPane().addANewTab(plugin.getName(), plugin, null, null);
          }
        }

        public void pluginStopped(PluginEvent e) {
          //TODO remove the plugin from the gui
        }
      });

    // TODO this should all be in a config file
    ChainsawCentral cc = new ChainsawCentral();
    pluginRegistry.addPlugin(cc);
    cc.activateOptions();
    
//    TODO this should also be fixed up, as VFS bits and pieces might not be built in an Ant build when they don't have all the VFS jars local
    try {
      Class vfsPluginClass = Class.forName("org.apache.log4j.chainsaw.vfs.VFSPlugin");
      Plugin vfsPlugin = (Plugin) vfsPluginClass.newInstance();
      vfsPlugin.activateOptions();
      pluginRegistry.addPlugin(vfsPlugin);
      MessageCenter.getInstance().getLogger().info("Looks like VFS is available... WooHoo!");
    } catch (Throwable e) {
      MessageCenter.getInstance().getLogger().error("Doesn't look like VFS is available", e);
    }
  }

  private void setupReceiverPanel() {
    receiversPanel = new ReceiversPanel();
    receiversPanel.addPropertyChangeListener(
      "visible",
      new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
          MessageCenter.getInstance().getLogger().debug(
            "Receiver's panel:" + evt.getNewValue());
          getApplicationPreferenceModel().setReceivers(
            ((Boolean) evt.getNewValue()).booleanValue());
        }
      });
  }

  /**
   * Initialises the Help system and the WelcomePanel
   *
   */
  private void setupHelpSystem() {
    welcomePanel = new WelcomePanel();

    JToolBar tb = welcomePanel.getToolbar();
    tb.add(
      new SmallButton(
        new AbstractAction("Tutorial", new ImageIcon(ChainsawIcons.HELP)) {
        public void actionPerformed(ActionEvent e) {
          setupTutorial();
        }
      }));
    tb.addSeparator();

    final Action exampleConfigAction =
      new AbstractAction("View example Receiver configuration") {
        public void actionPerformed(ActionEvent e) {
          HelpManager.getInstance().setHelpURL(
            ChainsawConstants.EXAMLE_CONFIG_URL);
        }
      };

    exampleConfigAction.putValue(
      Action.SHORT_DESCRIPTION,
      "Displays an example Log4j configuration file with several Receivers defined.");

    JButton exampleButton = new SmallButton(exampleConfigAction);
    tb.add(exampleButton);

    tb.add(Box.createHorizontalGlue());

    /**
     * Setup a listener on the HelpURL property and automatically change the WelcomePages URL
     * to it.
     */
    HelpManager.getInstance().addPropertyChangeListener(
      "helpURL",
      new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
          URL newURL = (URL) evt.getNewValue();

          if (newURL != null) {
            welcomePanel.setURL(newURL);
            ensureWelcomePanelVisible();
          }
        }
      });
  }

  private void ensureWelcomePanelVisible() {
      // ensure that the Welcome Panel is made visible
      if(getTabbedPane().getSelectedComponent()!=welcomePanel) {
          getTabbedPane().setSelectedIndex(getTabbedPane().indexOfComponent(welcomePanel));
      }
  }
  
  /**
   * Given the load event, configures the size/location of the main window etc
   * etc.
   *
   * @param event
   *                    DOCUMENT ME!
   */
  public void loadSettings(LoadSettingsEvent event) {
    setLocation(
      event.asInt(LogUI.MAIN_WINDOW_X), event.asInt(LogUI.MAIN_WINDOW_Y));
    setSize(
      event.asInt(LogUI.MAIN_WINDOW_WIDTH),
      event.asInt(LogUI.MAIN_WINDOW_HEIGHT));

    getToolBarAndMenus().stateChange();
  }

  /**
   * Ensures the location/size of the main window is stored with the settings
   *
   * @param event
   *                    DOCUMENT ME!
   */
  public void saveSettings(SaveSettingsEvent event) {
    event.saveSetting(LogUI.MAIN_WINDOW_X, (int) getLocation().getX());
    event.saveSetting(LogUI.MAIN_WINDOW_Y, (int) getLocation().getY());

    event.saveSetting(LogUI.MAIN_WINDOW_WIDTH, getWidth());
    event.saveSetting(LogUI.MAIN_WINDOW_HEIGHT, getHeight());

    if (configURLToUse != null) {
      event.saveSetting(LogUI.CONFIG_FILE_TO_USE, configURLToUse.toString());
    }
  }

  /**
   * Activates itself as a viewer by configuring Size, and location of itself,
   * and configures the default Tabbed Pane elements with the correct layout,
   * table columns, and sets itself viewable.
   */
  public void activateViewer() {
    
      getSettingsManager().configure(
              new SettingsListener() {
                public void loadSettings(LoadSettingsEvent event) {
                  String configFile = event.getSetting(LogUI.CONFIG_FILE_TO_USE);

                  //if both a config file are defined and a log4j.configuration property
                  // are set,
                  //don't use configFile's configuration
                  if (
                    (configFile != null) && !configFile.trim().equals("")
                      && (System.getProperty("log4j.configuration") == null)) {
                    try {
                      URL url = new URL(configFile);
                      OptionConverter.selectAndConfigure(
                        url, null, LogManager.getLoggerRepository());

                      if (LogUI.this.getStatusBar() != null) {
                        MessageCenter.getInstance().getLogger().info(
                          "Configured Log4j using remembered URL :: " + url);
                      }

                      LogUI.this.configURLToUse = url;
                    } catch (Exception e) {
                      MessageCenter.getInstance().getLogger().error(
                        "error occurred initializing log4j", e);
                    }
                  }
                }

                public void saveSettings(SaveSettingsEvent event) {
                  //required because of SettingsListener interface..not used during load
                }
              });

    this.pluginRegistry = LogManager.getLoggerRepository().getPluginRegistry();  
    initGUI();

    initPrefModelListeners();

    /**
     * We add a simple appender to the MessageCenter logger
     * so that each message is displayed in the Status bar
     */
    MessageCenter.getInstance().getLogger().addAppender(
      new AppenderSkeleton() {
        protected void append(LoggingEvent event) {
          getStatusBar().setMessage(event.getMessage().toString());
        }

        public void close() {
        }

        public boolean requiresLayout() {
          return false;
        }
      });



    initSocketConnectionListener();

    if (pluginRegistry.getPlugins(Receiver.class).size() == 0) {
      noReceiversDefined = true;
    }

    //List utilList = UtilLoggingLevel.getAllPossibleLevels();
    // TODO: Replace the array list creating with the standard way of
    // retreiving the Level set. (TBD)
    Level[] levels =
      new Level[] { Level.FATAL, Level.ERROR, Level.WARN, Level.INFO, Level.DEBUG };
    List priorityLevels = new ArrayList();

    for (int i = 0; i < levels.length; i++) {
      priorityLevels.add(levels[i].toString());
    }

    List utilLevels = new ArrayList();

    for (Iterator iterator = utilLevels.iterator(); iterator.hasNext();) {
      utilLevels.add(iterator.next().toString());
    }

    //    getLevelMap().put(ChainsawConstants.UTIL_LOGGING_EVENT_TYPE,
    // utilLevels);
    //    getLevelMap().put(ChainsawConstants.LOG4J_EVENT_TYPE, priorityLevels);
    getFilterableColumns().add(ChainsawConstants.LEVEL_COL_NAME);
    getFilterableColumns().add(ChainsawConstants.LOGGER_COL_NAME);
    getFilterableColumns().add(ChainsawConstants.THREAD_COL_NAME);
    getFilterableColumns().add(ChainsawConstants.NDC_COL_NAME);
    getFilterableColumns().add(ChainsawConstants.PROPERTIES_COL_NAME);
    getFilterableColumns().add(ChainsawConstants.CLASS_COL_NAME);
    getFilterableColumns().add(ChainsawConstants.METHOD_COL_NAME);
    getFilterableColumns().add(ChainsawConstants.FILE_COL_NAME);
    getFilterableColumns().add(ChainsawConstants.NONE_COL_NAME);

    JPanel panePanel = new JPanel();
    panePanel.setLayout(new BorderLayout(2, 2));

    getContentPane().setLayout(new BorderLayout());

    getTabbedPane().addChangeListener(getToolBarAndMenus());

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
         * We listen for double clicks, and auto-undock currently selected Tab if
         * the mouse event location matches the currently selected tab
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

    panePanel.add(getTabbedPane());
    addWelcomePanel();
    initPlugins(pluginRegistry);

    getContentPane().add(toolbar, BorderLayout.NORTH);
    getContentPane().add(statusBar, BorderLayout.SOUTH);

    mainReceiverSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panePanel, receiversPanel);
    dividerSize = mainReceiverSplitPane.getDividerSize();
    mainReceiverSplitPane.setDividerLocation(-1);

    getContentPane().add(mainReceiverSplitPane, BorderLayout.CENTER);

    mainReceiverSplitPane.setResizeWeight(1.0);
    addWindowListener(
      new WindowAdapter() {
        public void windowClosing(WindowEvent event) {
          exit();
        }
      });
    preferencesFrame.setTitle("'Application-wide Preferences");
    preferencesFrame.setIconImage(
      ((ImageIcon) ChainsawIcons.ICON_PREFERENCES).getImage());
    preferencesFrame.getContentPane().add(applicationPreferenceModelPanel);

    preferencesFrame.setSize(640, 520);

    Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
    preferencesFrame.setLocation(
      new Point(
        (screenDimension.width / 2) - (preferencesFrame.getSize().width / 2),
        (screenDimension.height / 2) - (preferencesFrame.getSize().height / 2)));

    pack();

    final JPopupMenu tabPopup = new JPopupMenu();
    final Action hideCurrentTabAction =
      new AbstractAction("Hide") {
        public void actionPerformed(ActionEvent e) {
          displayPanel(getCurrentLogPanel().getIdentifier(), false);
          tbms.stateChange();
        }
      };

    final Action hideOtherTabsAction =
      new AbstractAction("Hide Others") {
        public void actionPerformed(ActionEvent e) {
          String currentName = getCurrentLogPanel().getIdentifier();

          int count = getTabbedPane().getTabCount();
          int index = 0;

          for (int i = 0; i < count; i++) {
            String name = getTabbedPane().getTitleAt(index);

            if (
              getPanelMap().keySet().contains(name)
                && !name.equals(currentName)) {
              displayPanel(name, false);
              tbms.stateChange();
            } else {
              index++;
            }
          }
        }
      };

    Action showHiddenTabsAction =
      new AbstractAction("Show All Hidden") {
        public void actionPerformed(ActionEvent e) {
          for (Iterator iter = getPanels().entrySet().iterator();
              iter.hasNext();) {
          	Map.Entry entry = (Map.Entry)iter.next();
          	Boolean docked = (Boolean)entry.getValue();
          	if (docked.booleanValue()) {
	            String identifier = (String) entry.getKey();
	            int count = getTabbedPane().getTabCount();
	            boolean found = false;
	
	            for (int i = 0; i < count; i++) {
	              String name = getTabbedPane().getTitleAt(i);
	
	              if (name.equals(identifier)) {
	                found = true;
	
	                break;
	              }
	            }
	
	            if (!found) {
	              displayPanel(identifier, true);
	              tbms.stateChange();
	            }
          	}
          }
        }
      };

    tabPopup.add(hideCurrentTabAction);
    tabPopup.add(hideOtherTabsAction);
    tabPopup.addSeparator();
    tabPopup.add(showHiddenTabsAction);

    final PopupListener tabPopupListener = new PopupListener(tabPopup);
    getTabbedPane().addMouseListener(tabPopupListener);

    final ChangeListener actionEnabler =
      new ChangeListener() {
        public void stateChanged(ChangeEvent arg0) {
          boolean enabled = getCurrentLogPanel() != null;
          hideCurrentTabAction.setEnabled(enabled);
          hideOtherTabsAction.setEnabled(enabled);
        }
      };

    getTabbedPane().addChangeListener(actionEnabler);

    getTabbedPane().addContainerListener(
      new ContainerListener() {
        public void componentAdded(ContainerEvent arg0) {
          actionEnabler.stateChanged(null);
        }

        public void componentRemoved(ContainerEvent arg0) {
          actionEnabler.stateChanged(null);
        }
      });

    this.handler.addPropertyChangeListener(
      "dataRate",
      new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
          double dataRate = ((Double) evt.getNewValue()).doubleValue();
          statusBar.setDataRate(dataRate);
        }
      });

    getSettingsManager().addSettingsListener(this);
    getSettingsManager().addSettingsListener(applicationPreferenceModel);
    getSettingsManager().loadSettings();

    setVisible(true);

    if (applicationPreferenceModel.isReceivers()) {
      showReceiverPanel();
    } else {
      hideReceiverPanel();
    }

    removeSplash();

    synchronized (initializationLock) {
      isGUIFullyInitialized = true;
      initializationLock.notifyAll();
    }

    
    
    if (
      noReceiversDefined
        && applicationPreferenceModel.isShowNoReceiverWarning()) {
      showNoReceiversWarningPanel();
    }

    Container container = tutorialFrame.getContentPane();
    final JEditorPane tutorialArea = new JEditorPane();
    tutorialArea.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
    tutorialArea.setEditable(false);
    container.setLayout(new BorderLayout());

    try {
      tutorialArea.setPage(ChainsawConstants.TUTORIAL_URL);
      container.add(new JScrollPane(tutorialArea), BorderLayout.CENTER);
    } catch (Exception e) {
      MessageCenter.getInstance().getLogger().error(
        "Error occurred loading the Tutorial", e);
    }

    tutorialFrame.setIconImage(new ImageIcon(ChainsawIcons.HELP).getImage());
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
                  PluginRegistry pluginRegistry = LogManager.getLoggerRepository().getPluginRegistry();
                  List list = pluginRegistry.getPlugins(Generator.class);

                  for (Iterator iter = list.iterator(); iter.hasNext();) {
                    Plugin plugin = (Plugin) iter.next();
                    pluginRegistry.stopPlugin(plugin.getName());
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
            ((Boolean) startTutorial.getValue("TutorialStarted")).equals(
              Boolean.TRUE));
          startButton.setSelected(stopTutorial.isEnabled());
        }
      };

    startTutorial.addPropertyChangeListener(pcl);
    stopTutorial.addPropertyChangeListener(pcl);

    pluginRegistry.addPluginListener(
      new PluginListener() {
        public void pluginStarted(PluginEvent e) {
        }

        public void pluginStopped(PluginEvent e) {
          List list = pluginRegistry.getPlugins(Generator.class);

          if (list.size() == 0) {
            startTutorial.putValue("TutorialStarted", Boolean.FALSE);
          }
        }
      });

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
                MessageCenter.getInstance().getLogger().error(
                  "Failed to change the URL for the Tutorial", e1);
              }
            }
          }
        }
      });
  }

  /**
   * Checks the last run version number against this compiled version number and prompts the user
   * to view the release notes if the 2 strings are different.
   */
  private void checkForNewerVersion()
  {
      /**
       * Now check if the version they last used (if any) is
       * different than the version that is currently running
       */
      
      String lastUsedVersion = getApplicationPreferenceModel().getLastUsedVersion();
      String currentVersionNumber = VersionManager.getInstance().getVersionNumber();
      if(lastUsedVersion==null || !lastUsedVersion.equals(currentVersionNumber)) {
          if(JOptionPane.showConfirmDialog(this, "This version looks like it is different than the version you last ran. (" + lastUsedVersion + " vs " + currentVersionNumber+")\n\nWould you like to view the Release Notes?", "Newer Version?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
              SwingUtilities.invokeLater(new Runnable() {
                  
                  public void run()
                  {
                      HelpManager.getInstance().setHelpURL(ChainsawConstants.RELEASE_NOTES_URL);
                      
                  }});
          }
      }
      // Lets set this new version as the current version so we don't get nagged all the time...
      getApplicationPreferenceModel().setLastUsedVersion(currentVersionNumber);
  }

/**
   * Display the log tree pane, using the last known divider location
   */
  private void showReceiverPanel() {
    mainReceiverSplitPane.setDividerSize(dividerSize);
    mainReceiverSplitPane.setDividerLocation(lastMainReceiverSplitLocation);
    receiversPanel.setVisible(true);
    mainReceiverSplitPane.repaint();
  }

  /**
   * Hide the log tree pane, holding the current divider location for later use
   */
  private void hideReceiverPanel() {
    //subtract one to make sizes match
    int currentSize = mainReceiverSplitPane.getWidth() - mainReceiverSplitPane.getDividerSize();
    if (mainReceiverSplitPane.getDividerLocation() > -1) {
        if (!(((mainReceiverSplitPane.getDividerLocation() + 1) == currentSize)
                || ((mainReceiverSplitPane.getDividerLocation() - 1) == 0))) {
                    lastMainReceiverSplitLocation = ((double) mainReceiverSplitPane
                        .getDividerLocation() / currentSize);
        }
    }
    mainReceiverSplitPane.setDividerSize(0);
    receiversPanel.setVisible(false);
    mainReceiverSplitPane.repaint();
  }

  private void initSocketConnectionListener() {
    final SocketNodeEventListener socketListener =
      new SocketNodeEventListener() {
        public void socketOpened(String remoteInfo) {
          statusBar.remoteConnectionReceived(remoteInfo);
        }

        public void socketClosedEvent(Exception e) {
          MessageCenter.getInstance().getLogger().info(
            "Connection lost! :: " + e.getMessage());
        }
      };

    PluginListener pluginListener =
      new PluginListener() {
        public void pluginStarted(PluginEvent e) {
          MessageCenter.getInstance().getLogger().info(
            e.getPlugin().getName() + " started!");

          Method method = getAddListenerMethod(e.getPlugin());

          if (method != null) {
            try {
              method.invoke(e.getPlugin(), new Object[] { socketListener });
            } catch (Exception ex) {
              MessageCenter.getInstance().getLogger().error(
                "Failed to add a SocketNodeEventListener", ex);
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
              MessageCenter.getInstance().getLogger().error(
                "Failed to remove SocketNodeEventListener", ex);
            }
          }

          MessageCenter.getInstance().getLogger().info(
            e.getPlugin().getName() + " stopped!");
        }
      };

    pluginRegistry.addPluginListener(pluginListener);
  }

  private void initPrefModelListeners() {
    applicationPreferenceModel.addPropertyChangeListener(
      "identifierExpression",
      new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
          handler.setIdentifierExpression(evt.getNewValue().toString());
        }
      });

    applicationPreferenceModel.addPropertyChangeListener(
      "toolTipDisplayMillis",
      new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
          ToolTipManager.sharedInstance().setDismissDelay(
            ((Integer) evt.getNewValue()).intValue());
        }
      });
    ToolTipManager.sharedInstance().setDismissDelay(
      applicationPreferenceModel.getToolTipDisplayMillis());

    applicationPreferenceModel.addPropertyChangeListener(
      "responsiveness",
      new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
          int value = ((Integer) evt.getNewValue()).intValue();
          handler.setQueueInterval((value * 1000) - 750);
        }
      });

    applicationPreferenceModel.addPropertyChangeListener(
      "tabPlacement",
      new PropertyChangeListener() {
        public void propertyChange(final PropertyChangeEvent evt) {
          SwingUtilities.invokeLater(
            new Runnable() {
              public void run() {
                int placement = ((Integer) evt.getNewValue()).intValue();

                switch (placement) {
                case SwingConstants.TOP:
                case SwingConstants.BOTTOM:
                  tabbedPane.setTabPlacement(placement);

                  break;

                default:
                  break;
                }
              }
            });
        }
      });

    applicationPreferenceModel.addPropertyChangeListener(
      "statusBar",
      new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
          boolean value = ((Boolean) evt.getNewValue()).booleanValue();
          setStatusBarVisible(value);
        }
      });

    applicationPreferenceModel.addPropertyChangeListener(
      "receivers",
      new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
          boolean value = ((Boolean) evt.getNewValue()).booleanValue();

          if (value) {
            showReceiverPanel();
          } else {
            hideReceiverPanel();
          }
        }
      });

    applicationPreferenceModel.addPropertyChangeListener(
      "toolbar",
      new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
          boolean value = ((Boolean) evt.getNewValue()).booleanValue();
          toolbar.setVisible(value);
        }
      });
    toolbar.setVisible(applicationPreferenceModel.isToolbar());

    setStatusBarVisible(applicationPreferenceModel.isStatusBar());
  }

  /**
   * Displays a warning dialog about having no Receivers defined and allows the
   * user to choose some options for configuration
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
         * This listener sets up the NoReciversWarningPanel and loads saves the
         * configs/logfiles
         */
    getSettingsManager().addSettingsListener(sl);
    getSettingsManager().configure(sl);

    SwingUtilities.invokeLater(
      new Runnable() {
        public void run() {
          final JDialog dialog = new JDialog(LogUI.this, true);
          dialog.setTitle("Warning: You have no Receivers defined...");
          dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

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

          applicationPreferenceModel.setShowNoReceiverWarning(
            !noReceiversWarningPanel.isDontWarnMeAgain());

          if (noReceiversWarningPanel.getModel().isManualMode()) {
            applicationPreferenceModel.setReceivers(true);
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

              pluginRegistry.addPlugin(simpleReceiver);
              simpleReceiver.activateOptions();
              receiversPanel.updateReceiverTreeInDispatchThread();
            } catch (Exception e) {
              MessageCenter.getInstance().getLogger().error(
                "Error creating Receiver", e);
              MessageCenter.getInstance().getLogger().info(
                "An error occurred creating your Receiver");
            }
          } else if (noReceiversWarningPanel.getModel().isLoadConfig()) {
            final URL url =
              noReceiversWarningPanel.getModel().getConfigToLoad();

            if (url != null) {
              MessageCenter.getInstance().getLogger().debug(
                "Initialiazing Log4j with " + url.toExternalForm());

              new Thread(
                new Runnable() {
                  public void run() {
                    try {
                      OptionConverter.selectAndConfigure(
                        url, null, LogManager.getLoggerRepository());
                    } catch (Exception e) {
                      MessageCenter.getInstance().getLogger().error(
                        "Error initializing Log4j", e);
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

  ChainsawStatusBar getStatusBar() {
    return statusBar;
  }

  void showApplicationPreferences() {
    applicationPreferenceModelPanel.updateModel();
    preferencesFrame.show();
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
    if (getApplicationPreferenceModel().isConfirmExit()) {
      if (
        JOptionPane.showConfirmDialog(
            LogUI.this, "Are you sure you want to exit Chainsaw?",
            "Confirm Exit", JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE) != JOptionPane.YES_OPTION) {
        return;
      }
    }

    final JWindow progressWindow = new JWindow();
    final ProgressPanel panel = new ProgressPanel(1, 3, "Shutting down");
    progressWindow.getContentPane().add(panel);
    progressWindow.pack();

    Point p = new Point(getLocation());
    p.move((int) getSize().getWidth() >> 1, (int) getSize().getHeight() >> 1);
    progressWindow.setLocation(p);
    progressWindow.setVisible(true);

    Runnable runnable =
      new Runnable() {
        public void run() {
          try {
            int progress = 1;
            final int delay = 25;

            handler.close();
            panel.setProgress(progress++);

            Thread.sleep(delay);

            pluginRegistry.stopAllPlugins();
            panel.setProgress(progress++);

            Thread.sleep(delay);

            panel.setProgress(progress++);
            Thread.sleep(delay);
          } catch (Exception e) {
            e.printStackTrace();
          }

          fireShutdownEvent();
          performShutdownAction();
          progressWindow.setVisible(false);
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
   * Configures LogUI's with an action to execute when the user requests to
   * exit the application, the default action is to exit the VM. This Action is
   * called AFTER all the ShutdownListeners have been notified
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
    MessageCenter.getInstance().getLogger().debug(
      "Calling the shutdown Action. Goodbye!");

    shutdownAction.actionPerformed(
      new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Shutting Down"));
  }

  /**
   * Returns the currently selected LogPanel, if there is one, otherwise null
   *
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

  /**
   * @param b
   */
  private void setStatusBarVisible(final boolean visible) {
    MessageCenter.getInstance().getLogger().debug(
      "Setting StatusBar to " + visible);
    SwingUtilities.invokeLater(
      new Runnable() {
        public void run() {
          statusBar.setVisible(visible);
        }
      });
  }

  boolean isStatusBarVisible() {
    return statusBar.isVisible();
  }

  /**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  public String getActiveTabName() {
    int index = getTabbedPane().getSelectedIndex();

    if (index == -1) {
      return null;
    } else {
      return getTabbedPane().getTitleAt(index);
    }
  }

  /**
   * Changes the currently used Look And Feel of the App
   *
   * @param lookAndFeelClassName
   *                    The FQN of the LookANdFeel
   */
  private static void applyLookAndFeel(String lookAndFeelClassName) {
    if (
      UIManager.getLookAndFeel().getClass().getName().equals(
          lookAndFeelClassName)) {
      //MessageCenter.getInstance().getLogger().debug("No need to change L&F, already the same");
      return;
    }

    if (
      (lookAndFeelClassName == null) || lookAndFeelClassName.trim().equals("")) {
      //      MessageCenter.getInstance().getLogger().info("Using System native L&F");
      lookAndFeelClassName = UIManager.getSystemLookAndFeelClassName();
    }

    try {
      UIManager.setLookAndFeel(lookAndFeelClassName);

      //MessageCenter.getInstance().getLogger().debug("Setting L&F -> " + lookAndFeelClassName);
    } catch (Exception e) {
      //MessageCenter.getInstance().getLogger().error("Failed to change L&F", e);
    }
  }

  /**
   * Causes the Welcome Panel to become visible, and shows the URL specified as
   * it's contents
   *
   * @param url
   *                    for content to show
   */
  public void showHelp(URL url) {
    removeWelcomePanel();
    addWelcomePanel();

    //    TODO ensure the Welcome Panel is the selected tab
    getWelcomePanel().setURL(url);
  }

  /**
   * DOCUMENT ME!
   *
   * @return
   */
  private WelcomePanel getWelcomePanel() {
    return welcomePanel;
  }

  /**
   * DOCUMENT ME!
   *
   * @return
   */
  public boolean isLogTreePanelVisible() {
    if (getCurrentLogPanel() == null) {
      return false;
    }

    return getCurrentLogPanel().isLogTreeVisible();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.apache.log4j.chainsaw.EventBatchListener#getInterestedIdentifier()
   */

  /**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  public String getInterestedIdentifier() {
    //    this instance is interested in ALL event batches, as we determine how to
    // route things
    return null;
  }

  //  public Map getEntryMap() {
  //    return entryMap;
  //  }
  //  public Map getScrollMap() {
  //    return scrollMap;
  //  }

  /**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  public Map getPanelMap() {
    return panelMap;
  }

  //  public Map getLevelMap() {
  //    return levelMap;
  //  }

  /**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  public SettingsManager getSettingsManager() {
    return sm;
  }

  /**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  public List getFilterableColumns() {
    return filterableColumns;
  }

  /**
   * DOCUMENT ME!
   *
   * @param tbms
   *                    DOCUMENT ME!
   */
  public void setToolBarAndMenus(ChainsawToolBarAndMenus tbms) {
    this.tbms = tbms;
  }

  /**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  public ChainsawToolBarAndMenus getToolBarAndMenus() {
    return tbms;
  }

  /**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  public Map getTableMap() {
    return tableMap;
  }

  /**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  public Map getTableModelMap() {
    return tableModelMap;
  }

  /**
   * DOCUMENT ME!
   *
   * @param tabbedPane
   *                    DOCUMENT ME!
   */
  public void setTabbedPane(ChainsawTabbedPane tabbedPane) {
    this.tabbedPane = tabbedPane;
  }

  /**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  public ChainsawTabbedPane getTabbedPane() {
    return tabbedPane;
  }

  /**
   * @return Returns the applicationPreferenceModel.
   */
  public final ApplicationPreferenceModel getApplicationPreferenceModel() {
    return applicationPreferenceModel;
  }

  /**
   * DOCUMENT ME!
   */
  public void setupTutorial() {
    SwingUtilities.invokeLater(
      new Runnable() {
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
          tutorialFrame.setSize(
            (int) (screen.width * tutorialwidth), size.height);
          tutorialFrame.setLocation(loc.x + size.width, loc.y);
          tutorialFrame.setVisible(true);
        }
      });
  }

  private void buildLogPanel(
    boolean customExpression, final String ident, final List eventBatchEntrys)
    throws IllegalArgumentException {
    final LogPanel thisPanel = new LogPanel(getStatusBar(), ident, cyclicBufferSize);

    /**
             * Now add the panel as a batch listener so it can handle it's own
             * batchs
             */
    if (customExpression) {
      handler.addCustomEventBatchListener(ident, thisPanel);
    } else {
      identifierPanels.add(thisPanel);
      handler.addEventBatchListener(thisPanel);
    }

    TabIconHandler iconHandler = new TabIconHandler(ident);
    thisPanel.addEventCountListener(iconHandler);
    tabbedPane.addChangeListener(iconHandler);

    PropertyChangeListener toolbarMenuUpdateListener =
      new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
          tbms.stateChange();
        }
      };

    thisPanel.addPropertyChangeListener(toolbarMenuUpdateListener);
    thisPanel.addPreferencePropertyChangeListener(toolbarMenuUpdateListener);

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

    getSettingsManager().addSettingsListener(thisPanel);
    getSettingsManager().configure(thisPanel);

    /**
             * Let the new LogPanel receive this batch
             */
    thisPanel.receiveEventBatch(ident, eventBatchEntrys);

    SwingUtilities.invokeLater(
      new Runnable() {
        public void run() {
          getTabbedPane().addANewTab(
            ident, thisPanel, new ImageIcon(ChainsawIcons.ANIM_RADIO_TOWER));
        }
      });

    String msg = "added tab " + ident;
    MessageCenter.getInstance().getLogger().debug(msg);
  }

  public void createCustomExpressionLogPanel(String ident) {
    //collect events matching the rule from all of the tabs
    try {
      List list = new ArrayList();
      Rule rule = ExpressionRule.getRule(ident);
      Iterator iter = identifierPanels.iterator();

      while (iter.hasNext()) {
        LogPanel panel = (LogPanel) iter.next();
        Iterator iter2 = panel.getMatchingEvents(rule).iterator();

        while (iter2.hasNext()) {
          LoggingEvent e = (LoggingEvent) iter2.next();
          list.add(
            new ChainsawEventBatchEntry(
              ident,
              (e.getProperty(ChainsawConstants.EVENT_TYPE_KEY) == null)
              ? ChainsawConstants.LOG4J_EVENT_TYPE
              : e.getProperty(ChainsawConstants.EVENT_TYPE_KEY), e));
        }
      }

      buildLogPanel(true, ident, list);
    } catch (IllegalArgumentException iae) {
      MessageCenter.getInstance().getLogger().info(
        "Unable to add tab using expression: " + ident + ", reason: "
        + iae.getMessage());
    }
  }

  /**
   * This class handles the recption of the Event batches and creates new
   * LogPanels if the identifier is not in use otherwise it ignores the event
   * batch.
   *
   * @author Paul Smith
   *                <psmith@apache.org>
   *
   */
  private class NewTabEventBatchReceiver implements EventBatchListener {
    /**
         * DOCUMENT ME!
         *
         * @param ident
         *                    DOCUMENT ME!
         * @param eventBatchEntrys
         *                    DOCUMENT ME!
         */
    public void receiveEventBatch(
      final String ident, final List eventBatchEntrys) {
      if (eventBatchEntrys.size() == 0) {
        return;
      }

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
        try {
          buildLogPanel(false, ident, eventBatchEntrys);
        } catch (IllegalArgumentException iae) {
          //should not happen - not a custom expression panel
        }
      }
    }

    /*
         * (non-Javadoc)
         *
         * @see org.apache.log4j.chainsaw.EventBatchListener#getInterestedIdentifier()
         */

    /**
         * DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
    public String getInterestedIdentifier() {
      // we are interested in all batches so we can detect new identifiers
      return null;
    }
  }

  private class TabIconHandler implements EventCountListener, ChangeListener {
    //the tabIconHandler is associated with a new tab, and a new tab always
    //shows the 'new events' icon
    private boolean newEvents = true;
    private boolean seenEvents = false;
    private final String ident;
    ImageIcon NEW_EVENTS = new ImageIcon(ChainsawIcons.ANIM_RADIO_TOWER);
    ImageIcon HAS_EVENTS = new ImageIcon(ChainsawIcons.INFO);
    Icon SELECTED = LineIconFactory.createBlankIcon();

    public TabIconHandler(String identifier) {
      ident = identifier;

      new Thread(
        new Runnable() {
          public void run() {
            while (true) {
              //if this tab is active, remove the icon
              //don't process undocked tabs
              if (
                getTabbedPane().getSelectedIndex() == getTabbedPane()
                                                          .indexOfTab(ident)) {
                getTabbedPane().setIconAt(
                  getTabbedPane().indexOfTab(ident), SELECTED);
                newEvents = false;
                seenEvents = true;
              } else if (getTabbedPane().indexOfTab(ident) > -1) {
                if (newEvents) {
                  getTabbedPane().setIconAt(
                    getTabbedPane().indexOfTab(ident), NEW_EVENTS);
                  newEvents = false;
                  seenEvents = false;
                } else if (!seenEvents) {
                  getTabbedPane().setIconAt(
                    getTabbedPane().indexOfTab(ident), HAS_EVENTS);
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

    /**
         * DOCUMENT ME!
         *
         * @param currentCount
         *                    DOCUMENT ME!
         * @param totalCount
         *                    DOCUMENT ME!
         */
    public void eventCountChanged(int currentCount, int totalCount) {
      newEvents = true;
    }

    public void stateChanged(ChangeEvent event) {
      if (
        getTabbedPane().indexOfTab(ident) == getTabbedPane().getSelectedIndex()) {
        getTabbedPane().setIconAt(getTabbedPane().indexOfTab(ident), SELECTED);
      }
    }
  }
}
