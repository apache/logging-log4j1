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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
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
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
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
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.chainsaw.color.ColorPanel;
import org.apache.log4j.chainsaw.color.RuleColorizer;
import org.apache.log4j.chainsaw.filter.FilterModel;
import org.apache.log4j.chainsaw.icons.ChainsawIcons;
import org.apache.log4j.chainsaw.icons.LineIconFactory;
import org.apache.log4j.chainsaw.layout.DefaultLayoutFactory;
import org.apache.log4j.chainsaw.layout.EventDetailLayout;
import org.apache.log4j.chainsaw.layout.LayoutEditorPane;
import org.apache.log4j.chainsaw.messages.MessageCenter;
import org.apache.log4j.chainsaw.prefs.LoadSettingsEvent;
import org.apache.log4j.chainsaw.prefs.Profileable;
import org.apache.log4j.chainsaw.prefs.SaveSettingsEvent;
import org.apache.log4j.chainsaw.prefs.SettingsManager;
import org.apache.log4j.helpers.ISO8601DateFormat;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.rule.ExpressionRule;
import org.apache.log4j.rule.ExpressionRuleContext;
import org.apache.log4j.rule.Rule;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.LoggingEventFieldResolver;


/**
 * A LogPanel provides a view to a collection of LoggingEvents.<br>
 * <br>
 * As events are received, the keywords in the 'tab identifier' application
 * preference  are replaced with the values from the received event.  The
 * main application uses  this expression to route received LoggingEvents to
 * individual LogPanels which  match each event's resolved expression.<br>
 * <br>
 * The LogPanel's capabilities can be broken up into four areas:<br>
 * <ul><li> toolbar - provides 'find' and 'refine focus' features
 * <li> logger tree - displays a tree of the logger hierarchy, which can be used
 * to filter the display
 * <li> table - displays the events which pass the filtering rules
 * <li>detail panel - displays information about the currently selected event
 * </ul>
 * Here is a complete list of LogPanel's capabilities:<br>
 * <ul><li>display selected LoggingEvent row number and total LoggingEvent count
 * <li>pause or unpause reception of LoggingEvents
 * <li>configure, load and save column settings (displayed columns, order, width)
 * <li>configure, load and save color rules
 * filter displayed LoggingEvents based on the logger tree settings
 * <li>filter displayed LoggingEvents based on a 'refine focus' expression
 * (evaluates only those LoggingEvents which pass the logger tree filter
 * <li>colorize LoggingEvents based on expressions
 * <li>hide, show and configure the detail pane and tooltip
 * <li>configure the formatting of the logger, level and timestamp fields
 * <li>dock or undock
 * <li>table displays first line of exception, but when cell is clicked, a
 * popup opens to display the full stack trace
 * <li>find
 * <li>scroll to bottom
 * <li>sort
 * <li>provide a context menu which can be used to build color or display expressions
 * <li>hide or show the logger tree
 * <li>toggle the container storing the LoggingEvents to use either a
 * CyclicBuffer (defaults to max size of 5000,  but configurable  through
 * CHAINSAW_CAPACITY system property) or ArrayList (no max size)
 * <li>use the mouse context menu to 'best-fit' columns, define display
 * expression filters based on mouse location and access other capabilities
 *</ul>
 *
 *@see org.apache.log4j.chainsaw.color.ColorPanel
 *@see org.apache.log4j.rule.ExpressionRule
 *@see org.apache.log4j.spi.LoggingEventFieldResolver
 *
 *@author Scott Deboy (sdeboy at apache.org)
 *@author Paul Smith (psmith at apache.org)
 *
 */
public class LogPanel extends DockablePanel implements EventBatchListener,
  Profileable {
  private static final double DEFAULT_DETAIL_SPLIT_LOCATION = .5;
  private static final double DEFAULT_LOG_TREE_SPLIT_LOCATION = .25;
  private final String identifier;
  private final ChainsawStatusBar statusBar;
  private final JFrame preferencesFrame = new JFrame();
  private final JFrame colorFrame = new JFrame();
  private final JFrame undockedFrame;
  private final DockablePanel externalPanel;
  private final Action dockingAction;
  private final JToolBar undockedToolbar;
  private final JSortTable table;
  private final TableColorizingRenderer renderer;
  private final EventContainer tableModel;
  private final ThrowableRenderPanel throwableRenderPanel;
  private final JEditorPane detail;
  private final JSplitPane lowerPanel;
  private final DetailPaneUpdater detailPaneUpdater;
  private final JPanel detailPanel = new JPanel(new BorderLayout());
  private final JSplitPane nameTreeAndMainPanelSplit;
  private final LoggerNameTreePanel logTreePanel;
  private final LogPanelPreferenceModel preferenceModel =
    new LogPanelPreferenceModel();
  private final LogPanelPreferencePanel preferencesPanel =
    new LogPanelPreferencePanel(preferenceModel);
  private final FilterModel filterModel = new FilterModel();
  private final RuleColorizer colorizer = new RuleColorizer();
  private final RuleMediator ruleMediator = new RuleMediator();
  private Layout detailLayout = new EventDetailLayout();
  private double lastDetailPanelSplitLocation = DEFAULT_DETAIL_SPLIT_LOCATION;
  private double lastLogTreePanelSplitLocation =
    DEFAULT_LOG_TREE_SPLIT_LOCATION;
  private boolean bypassScrollFind;
  private boolean bypassScrollSelection;
  private Point currentPoint;
  private boolean scroll;
  private boolean paused = false;
  private Rule findRule;
  private final JPanel findPanel;
  private JTextField findField;
  private int dividerSize;
  static final String TABLE_COLUMN_ORDER = "table.columns.order";
  static final String TABLE_COLUMN_WIDTHS = "table.columns.widths";
  static final String COLUMNS_EXTENSION = ".columns";
  static final String COLORS_EXTENSION = ".colors";
  private int previousLastIndex = -1;

  /**
   * Creates a new LogPanel object.  If a LogPanel with this identifier has
   * been loaded previously, reload settings saved on last exit.
   *
   * @param statusBar shared status bar, provided by main application
   * @param identifier used to load and save settings
   */
  public LogPanel(final ChainsawStatusBar statusBar, final String identifier, int cyclicBufferSize) {
    this.identifier = identifier;
    this.statusBar = statusBar;

    setLayout(new BorderLayout());
    scroll = true;

    findPanel = new JPanel();

    final Map columnNameKeywordMap = new HashMap();
    columnNameKeywordMap.put(
      ChainsawConstants.CLASS_COL_NAME, LoggingEventFieldResolver.CLASS_FIELD);
    columnNameKeywordMap.put(
      ChainsawConstants.FILE_COL_NAME, LoggingEventFieldResolver.FILE_FIELD);
    columnNameKeywordMap.put(
      ChainsawConstants.LEVEL_COL_NAME, LoggingEventFieldResolver.LEVEL_FIELD);
    columnNameKeywordMap.put(
      ChainsawConstants.LINE_COL_NAME, LoggingEventFieldResolver.LINE_FIELD);
    columnNameKeywordMap.put(
      ChainsawConstants.LOGGER_COL_NAME, LoggingEventFieldResolver.LOGGER_FIELD);
    columnNameKeywordMap.put(
      ChainsawConstants.NDC_COL_NAME, LoggingEventFieldResolver.NDC_FIELD);
    columnNameKeywordMap.put(
      ChainsawConstants.MESSAGE_COL_NAME, LoggingEventFieldResolver.MSG_FIELD);
    columnNameKeywordMap.put(
      ChainsawConstants.THREAD_COL_NAME, LoggingEventFieldResolver.THREAD_FIELD);
    columnNameKeywordMap.put(
      ChainsawConstants.THROWABLE_COL_NAME,
      LoggingEventFieldResolver.EXCEPTION_FIELD);
    columnNameKeywordMap.put(
      ChainsawConstants.TIMESTAMP_COL_NAME,
      LoggingEventFieldResolver.TIMESTAMP_FIELD);

    preferencesFrame.setTitle("'" + identifier + "' Log Panel Preferences");
    preferencesFrame.setIconImage(
      ((ImageIcon) ChainsawIcons.ICON_PREFERENCES).getImage());
    preferencesFrame.getContentPane().add(preferencesPanel);

    preferencesFrame.setSize(640, 480);

    preferencesPanel.setOkCancelActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          preferencesFrame.setVisible(false);
        }
      });

    setDetailPaneConversionPattern(
      DefaultLayoutFactory.getDefaultPatternLayout());
    ((EventDetailLayout) detailLayout).setConversionPattern(
      DefaultLayoutFactory.getDefaultPatternLayout());

    undockedFrame = new JFrame(identifier);
    undockedFrame.setDefaultCloseOperation(
      WindowConstants.DO_NOTHING_ON_CLOSE);

    if (ChainsawIcons.UNDOCKED_ICON != null) {
      undockedFrame.setIconImage(
        new ImageIcon(ChainsawIcons.UNDOCKED_ICON).getImage());
    }

    externalPanel = new DockablePanel();
    externalPanel.setLayout(new BorderLayout());
    undockedFrame.getContentPane().add(externalPanel);

    undockedFrame.addWindowListener(
      new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          dock();
        }
      });

    undockedToolbar = createDockwindowToolbar();
    externalPanel.add(undockedToolbar, BorderLayout.NORTH);
    undockedFrame.pack();

    /*
     * Menus on which the preferencemodels rely
     */

    /**
     * Setup a popup menu triggered for Timestamp column to allow time stamp
     * format changes
     */
    final JPopupMenu dateFormatChangePopup = new JPopupMenu();
    final JRadioButtonMenuItem isoButton =
      new JRadioButtonMenuItem(
        new AbstractAction("Use ISO8601Format") {
          public void actionPerformed(ActionEvent e) {
            preferenceModel.setDateFormatPattern("ISO8601");
          }
        });
    final JRadioButtonMenuItem simpleTimeButton =
      new JRadioButtonMenuItem(
        new AbstractAction("Use simple time") {
          public void actionPerformed(ActionEvent e) {
            preferenceModel.setDateFormatPattern("HH:mm:ss");
          }
        });

    ButtonGroup dfBG = new ButtonGroup();
    dfBG.add(isoButton);
    dfBG.add(simpleTimeButton);
    isoButton.setSelected(true);
    dateFormatChangePopup.add(isoButton);
    dateFormatChangePopup.add(simpleTimeButton);

    final JCheckBoxMenuItem menuItemToggleToolTips =
      new JCheckBoxMenuItem("Show ToolTips");
    menuItemToggleToolTips.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          preferenceModel.setToolTips(menuItemToggleToolTips.isSelected());
        }
      });
    menuItemToggleToolTips.setIcon(new ImageIcon(ChainsawIcons.TOOL_TIP));

    final JCheckBoxMenuItem menuItemLoggerTree =
      new JCheckBoxMenuItem("Show Logger Tree panel");
    menuItemLoggerTree.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          preferenceModel.setLogTreePanelVisible(
            menuItemLoggerTree.isSelected());
        }
      });
    menuItemLoggerTree.setIcon(new ImageIcon(ChainsawIcons.WINDOW_ICON));

    final JCheckBoxMenuItem menuItemScrollBottom =
      new JCheckBoxMenuItem("Scroll to bottom");
    menuItemScrollBottom.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          preferenceModel.setScrollToBottom(menuItemScrollBottom.isSelected());
        }
      });

    menuItemScrollBottom.setIcon(
      new ImageIcon(ChainsawIcons.SCROLL_TO_BOTTOM));

    final JCheckBoxMenuItem menuItemToggleDetails =
      new JCheckBoxMenuItem("Show Detail Pane");
    menuItemToggleDetails.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          preferenceModel.setDetailPaneVisible(
            menuItemToggleDetails.isSelected());
        }
      });

    menuItemToggleDetails.setIcon(new ImageIcon(ChainsawIcons.INFO));

    /*
     * add preferencemodel listeners
     */
    preferenceModel.addPropertyChangeListener(
      "levelIcons",
      new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
          renderer.setLevelUseIcons(
            ((Boolean) evt.getNewValue()).booleanValue());
          table.tableChanged(new TableModelEvent(tableModel));
        }
      });

    preferenceModel.addPropertyChangeListener(
      "detailPaneVisible",
      new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
          boolean newValue = ((Boolean) evt.getNewValue()).booleanValue();

          if (newValue) {
            showDetailPane();
          } else {
            hideDetailPane();
          }
        }
      });

    preferenceModel.addPropertyChangeListener(
      "logTreePanelVisible",
      new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
          boolean newValue = ((Boolean) evt.getNewValue()).booleanValue();

          if (newValue) {
            showLogTreePanel();
          } else {
            hideLogTreePanel();
          }
        }
      });

    preferenceModel.addPropertyChangeListener(
      "toolTips",
      new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
          renderer.setToolTipsVisible(
            ((Boolean) evt.getNewValue()).booleanValue());
        }
      });

    preferenceModel.addPropertyChangeListener(
      "visibleColumns",
      new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
          TableColumnModel columnModel = table.getColumnModel();

          for (int i = 0; i < columnModel.getColumnCount(); i++) {
            TableColumn column = columnModel.getColumn(i);

            if (
              !preferenceModel.isColumnVisible(
                  column.getHeaderValue().toString())) {
              columnModel.removeColumn(column);
            }
          }

          Set columnSet = new HashSet();
          Enumeration enumeration = columnModel.getColumns();

          while (enumeration.hasMoreElements()) {
            TableColumn column = (TableColumn) enumeration.nextElement();

            columnSet.add(column.getHeaderValue());
          }

          for (
            Iterator iter = ChainsawColumns.getColumnsNames().iterator();
              iter.hasNext();) {
            String column = (String) iter.next();

            if (
              preferenceModel.isColumnVisible(column)
                && !columnSet.contains(column)) {
              TableColumn newCol =
                new TableColumn(
                  ChainsawColumns.getColumnsNames().indexOf(column));
              newCol.setHeaderValue(column);
              columnModel.addColumn(newCol);
            }
          }
        }
      });

    PropertyChangeListener datePrefsChangeListener =
      new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
          LogPanelPreferenceModel model =
            (LogPanelPreferenceModel) evt.getSource();

          isoButton.setSelected(model.isUseISO8601Format());
          simpleTimeButton.setSelected(
            !model.isUseISO8601Format() && !model.isCustomDateFormat());

          if (model.isUseISO8601Format()) {
            renderer.setDateFormatter(new ISO8601DateFormat());
          } else {
            renderer.setDateFormatter(
              new SimpleDateFormat(model.getDateFormatPattern()));
          }

          table.tableChanged(new TableModelEvent(tableModel));
        }
      };

    preferenceModel.addPropertyChangeListener(
      "dateFormatPattern", datePrefsChangeListener);
    preferenceModel.addPropertyChangeListener(
      "dateFormatPattern", datePrefsChangeListener);

    preferenceModel.addPropertyChangeListener(
      "loggerPrecision",
      new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
          LogPanelPreferenceModel model =
            (LogPanelPreferenceModel) evt.getSource();

          renderer.setLoggerPrecision(model.getLoggerPrecision());

          table.tableChanged(new TableModelEvent(tableModel));
        }
      });

    preferenceModel.addPropertyChangeListener(
      "toolTips",
      new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
          boolean value = ((Boolean) evt.getNewValue()).booleanValue();
          menuItemToggleToolTips.setSelected(value);
        }
      });

    preferenceModel.addPropertyChangeListener(
      "logTreePanelVisible",
      new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
          boolean value = ((Boolean) evt.getNewValue()).booleanValue();
          menuItemLoggerTree.setSelected(value);
        }
      });

    preferenceModel.addPropertyChangeListener(
      "scrollToBottom",
      new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
          boolean value = ((Boolean) evt.getNewValue()).booleanValue();
          menuItemScrollBottom.setSelected(value);
          scroll = value;
        }
      });

    preferenceModel.addPropertyChangeListener(
      "detailPaneVisible",
      new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
          boolean value = ((Boolean) evt.getNewValue()).booleanValue();
          menuItemToggleDetails.setSelected(value);
        }
      });

    /*
     *End of preferenceModel listeners
     */
    tableModel = new ChainsawCyclicBufferTableModel(cyclicBufferSize);
    table = new JSortTable(tableModel);
    //add a listener to update the 'refine focus'
    tableModel.addNewKeyListener(new NewKeyListener() {
		public void newKeyAdded(NewKeyEvent e) {
            columnNameKeywordMap.put(e.getKey(), "PROP." + e.getKey());
		}
    });

    /*
     * Set the Display rule to use the mediator, the model will add itself as
     * a property change listener and update itself when the rule changes.
     */
    tableModel.setDisplayRule(ruleMediator);

    tableModel.addEventCountListener(
      new EventCountListener() {
        public void eventCountChanged(int currentCount, int totalCount) {
          if (LogPanel.this.isVisible()) {
            statusBar.setSelectedLine(
              table.getSelectedRow() + 1, currentCount, totalCount);
          }
        }
      });

    tableModel.addEventCountListener(
      new EventCountListener() {
        final NumberFormat formatter = NumberFormat.getPercentInstance();
        boolean warning75 = false;
        boolean warning100 = false;

        public void eventCountChanged(int currentCount, int totalCount) {
          if (tableModel.isCyclic()) {
            double percent =
              ((double) totalCount) / ((ChainsawCyclicBufferTableModel) tableModel)
              .getMaxSize();
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
              MessageCenter.getInstance().getLogger().info(msg);
            }
          }
        }
      });

    /*
     * Logger tree panel
     *
     */
    LogPanelLoggerTreeModel logTreeModel = new LogPanelLoggerTreeModel();
    logTreePanel = new LoggerNameTreePanel(logTreeModel, preferenceModel);
    tableModel.addLoggerNameListener(logTreeModel);

    /**
     * Set the LoggerRule to be the LoggerTreePanel, as this visual component
     * is a rule itself, and the RuleMediator will automatically listen when
     * it's rule state changes.
     */
    ruleMediator.setLoggerRule(logTreePanel);

    /*
     * Color rule frame and panel
     */
    colorFrame.setTitle("'" + identifier + "' Color Filter");
    colorFrame.setIconImage(
      ((ImageIcon) ChainsawIcons.ICON_PREFERENCES).getImage());

    final ColorPanel colorPanel = new ColorPanel(colorizer, filterModel);

    colorFrame.getContentPane().add(colorPanel);

    colorPanel.setCloseActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          colorFrame.setVisible(false);
        }
      });

    colorizer.addPropertyChangeListener(
      "colorrule",
      new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
          if (table != null) {
            table.repaint();
          }

          colorPanel.updateColors();
        }
      });

    /*
     * Table definition.  Actual construction is above (next to tablemodel)
     */
    table.setRowHeight(20);
    table.setShowGrid(false);

    table.getColumnModel().addColumnModelListener(
      new ChainsawTableColumnModelListener());

    table.setAutoCreateColumnsFromModel(false);

    table.addMouseMotionListener(new TableColumnDetailMouseListener());

    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

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

    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    table.getSelectionModel().addListSelectionListener(
      new ListSelectionListener() {
        public void valueChanged(ListSelectionEvent evt) {
          if (
            ((evt.getFirstIndex() == evt.getLastIndex())
              && (evt.getFirstIndex() > 0)) || (evt.getValueIsAdjusting())) {
            return;
          }
          boolean lastIndexOnLastRow = (evt.getLastIndex() == (table.getRowCount() - 1));
          boolean firstIndexOnLastRow = (evt.getFirstIndex() == (table.getRowCount() - 1));
          boolean lastIndexSame = (previousLastIndex == evt.getLastIndex());

          /*
          to bypass the scroll-to-bottom feature when the user selects a row other than the bottom row,
          one of two conditions must be met:
          1: neither the 'firstindex' nor the 'lastindex' are on the last row, or
          2: the last index value didn't change and the 'lastindex' is on the last row, and the last index and first index aren't the same
           */ 
          bypassScrollSelection = (!(lastIndexOnLastRow || firstIndexOnLastRow)) || (lastIndexSame && lastIndexOnLastRow && (evt.getFirstIndex() != evt.getLastIndex()));
          previousLastIndex = evt.getLastIndex();

          final ListSelectionModel lsm = (ListSelectionModel) evt.getSource();

          if (lsm.isSelectionEmpty()) {
            if (isVisible()) {
              statusBar.setNothingSelected();
            }

            if (detail.getDocument().getDefaultRootElement() != null) {
              detailPaneUpdater.setSelectedRow(-1);
            }
          } else {
            if (table.getSelectedRow() > -1) {
              int selectedRow = table.getSelectedRow();

              if (isVisible()) {
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

    renderer = new TableColorizingRenderer(colorizer);
    renderer.setToolTipsVisible(preferenceModel.isToolTips());

    table.setDefaultRenderer(Object.class, renderer);

    /*
     * Throwable popup
     */
    throwableRenderPanel = new ThrowableRenderPanel();

    final JDialog detailDialog = new JDialog((JFrame) null, true);
    Container container = detailDialog.getContentPane();
    final JTextArea detailArea = new JTextArea(10, 40);
    detailArea.setEditable(false);
    container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
    container.add(new JScrollPane(detailArea));

    detailDialog.pack();

    throwableRenderPanel.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          Object o =
            table.getValueAt(
              table.getSelectedRow(), table.getSelectedColumn());
          detailDialog.setTitle(
            table.getColumnName(table.getSelectedColumn()) + " detail...");

          if (o instanceof String[]) {
            StringBuffer buf = new StringBuffer();
            String[] ti = (String[]) o;
            buf.append(ti[0]).append("\n");

            for (int i = 1; i < ti.length; i++) {
              buf.append(ti[i]).append("\n    ");
            }

            detailArea.setText(buf.toString());
          } else {
            detailArea.setText((o == null) ? "" : o.toString());
          }

          detailDialog.setLocation(LogPanel.this.getLocationOnScreen());
          SwingUtilities.invokeLater(
            new Runnable() {
              public void run() {
                detailDialog.setVisible(true);
              }
            });
        }
      });

    /*
     * We listen for new Key's coming in so we can get them automatically
     * added as columns
     */
    tableModel.addNewKeyListener(
      new NewKeyListener() {
        public void newKeyAdded(NewKeyEvent e) {
          TableColumn col = new TableColumn(e.getNewModelIndex());
          col.setHeaderValue(e.getKey());
          table.addColumn(col);
        }
      });

    tableModel.addPropertyChangeListener(
      "cyclic",
      new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent arg0) {
          if (tableModel.isCyclic()) {
            MessageCenter.getInstance().getLogger().warn(
              "Changed to Cyclic Mode. Maximum # events kept: "
              + tableModel.getMaxSize());
          } else {
            MessageCenter.getInstance().getLogger().warn(
              "Changed to Unlimited Mode. Warning, you may run out of memory.");
          }
        }
      });

    table.getTableHeader().addMouseListener(
      new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
          checkEvent(e);
        }

        public void mousePressed(MouseEvent e) {
          checkEvent(e);
        }

        public void mouseReleased(MouseEvent e) {
          checkEvent(e);
        }

        private void checkEvent(MouseEvent e) {
          if (e.isPopupTrigger()) {
            TableColumnModel colModel = table.getColumnModel();
            int index = colModel.getColumnIndexAtX(e.getX());
            int modelIndex = colModel.getColumn(index).getModelIndex();

            if ((modelIndex + 1) == ChainsawColumns.INDEX_TIMESTAMP_COL_NAME) {
              dateFormatChangePopup.show(e.getComponent(), e.getX(), e.getY());
            }
          }
        }
      });

    /*
     * Upper panel definition
     */
    JPanel upperPanel = new JPanel(new BorderLayout());
    upperPanel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 0));

    final JLabel filterLabel = new JLabel("Refine focus on: ");
    filterLabel.setFont(filterLabel.getFont().deriveFont(Font.BOLD));

    JPanel upperLeftPanel =
      new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 0));
    upperLeftPanel.add(filterLabel);

    //hold a reference to the combobox model so that we can check to prevent duplicates
    final Vector v = new Vector();
    final JComboBox filterCombo = new JComboBox(v);
    final JTextField filterText;

    if (filterCombo.getEditor().getEditorComponent() instanceof JTextField) {
      String comboToolTipText =
        "Enter an expression, press enter to add to list";
      filterText = (JTextField) filterCombo.getEditor().getEditorComponent();
      filterText.setToolTipText(comboToolTipText);
      filterText.addKeyListener(
        new ExpressionRuleContext(filterModel, filterText));
      filterText.getDocument().addDocumentListener(
        new DelayedFilterTextDocumentListener(filterText));
      filterCombo.setEditable(true);
      filterCombo.addActionListener(
        new AbstractAction() {
          public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("comboBoxEdited")) {
              try {
                //verify the expression is valid
                ExpressionRule.getRule(
                  filterCombo.getSelectedItem().toString());
              } catch (IllegalArgumentException iae) {
                //don't add expressions that aren't valid
                return;
              }

              //should be 'valid expression' check
              if (!(v.contains(filterCombo.getSelectedItem()))) {
                filterCombo.addItem(filterCombo.getSelectedItem());
              }
            }
          }
        });
      upperPanel.add(filterCombo, BorderLayout.CENTER);
    } else {
      filterText = new JTextField();
      filterText.setToolTipText("Enter an expression");
      filterText.addKeyListener(
        new ExpressionRuleContext(filterModel, filterText));
      filterText.getDocument().addDocumentListener(
        new DelayedFilterTextDocumentListener(filterText));
      upperPanel.add(filterText, BorderLayout.CENTER);
    }

    upperPanel.add(upperLeftPanel, BorderLayout.WEST);

    JPanel upperRightPanel =
      new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));

    upperPanel.add(upperRightPanel, BorderLayout.EAST);

    /*
     * Detail pane definition
     */
    detail = new JEditorPane(ChainsawConstants.DETAIL_CONTENT_TYPE, "");
    detail.setEditable(false);

    detailPaneUpdater = new DetailPaneUpdater();

    addFocusListener(new FocusListener() {

        public void focusGained(FocusEvent e) {
            detailPaneUpdater.updateDetailPane();
        }

        public void focusLost(FocusEvent e) {
            
        }
    });


    addPropertyChangeListener(
      "detailPaneConversionPattern", detailPaneUpdater);

    final JScrollPane detailPane = new JScrollPane(detail);

    detailPane.setPreferredSize(new Dimension(900, 50));

    detailPanel.add(detailPane, BorderLayout.CENTER);

    JPanel eventsAndStatusPanel = new JPanel(new BorderLayout());

    final JScrollPane eventsPane = new JScrollPane(table);

    eventsAndStatusPanel.add(eventsPane, BorderLayout.CENTER);

    final JPanel statusLabelPanel = new JPanel();
    statusLabelPanel.setLayout(new BorderLayout());

    statusLabelPanel.add(upperPanel, BorderLayout.CENTER);
    eventsAndStatusPanel.add(statusLabelPanel, BorderLayout.NORTH);

    lowerPanel =
      new JSplitPane(
        JSplitPane.VERTICAL_SPLIT, eventsAndStatusPanel, detailPanel);

    dividerSize = lowerPanel.getDividerSize();
    lowerPanel.setDividerLocation(-1);

    lowerPanel.setResizeWeight(1.0);
    lowerPanel.setBorder(null);
    lowerPanel.setContinuousLayout(true);

    if (preferenceModel.isDetailPaneVisible()) {
      showDetailPane();
    } else {
      hideDetailPane();
    }
    
    /*
     * Detail panel layout editor
     */
    final JToolBar detailToolbar = new JToolBar(SwingConstants.HORIZONTAL);
    detailToolbar.setFloatable(false);

    final LayoutEditorPane layoutEditorPane = new LayoutEditorPane();
    final JDialog layoutEditorDialog =
      new JDialog((JFrame) null, "Pattern Editor");
    layoutEditorDialog.getContentPane().add(layoutEditorPane);
    layoutEditorDialog.setSize(640, 480);

    layoutEditorPane.addCancelActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          layoutEditorDialog.setVisible(false);
        }
      });

    layoutEditorPane.addOkActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          setDetailPaneConversionPattern(
            layoutEditorPane.getConversionPattern());
          layoutEditorDialog.setVisible(false);
        }
      });

    Action editDetailAction =
      new AbstractAction(
        "Edit...", new ImageIcon(ChainsawIcons.ICON_EDIT_RECEIVER)) {
        public void actionPerformed(ActionEvent e) {
          layoutEditorPane.setConversionPattern(
            getDetailPaneConversionPattern());

          Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
          Point p =
            new Point(
              ((int) ((size.getWidth() / 2)
              - (layoutEditorDialog.getSize().getWidth() / 2))),
              ((int) ((size.getHeight() / 2)
              - (layoutEditorDialog.getSize().getHeight() / 2))));
          layoutEditorDialog.setLocation(p);

          layoutEditorDialog.setVisible(true);
        }
      };

    editDetailAction.putValue(
      Action.SHORT_DESCRIPTION,
      "opens a Dialog window to Edit the Pattern Layout text");

    final SmallButton editDetailButton = new SmallButton(editDetailAction);
    editDetailButton.setText(null);
    detailToolbar.add(Box.createHorizontalGlue());
    detailToolbar.add(editDetailButton);
    detailToolbar.addSeparator();
    detailToolbar.add(Box.createHorizontalStrut(5));

    Action closeDetailAction =
      new AbstractAction(null, LineIconFactory.createCloseIcon()) {
        public void actionPerformed(ActionEvent arg0) {
          preferenceModel.setDetailPaneVisible(false);
        }
      };

    closeDetailAction.putValue(
      Action.SHORT_DESCRIPTION, "Hides the Detail Panel");

    SmallButton closeDetailButton = new SmallButton(closeDetailAction);
    detailToolbar.add(closeDetailButton);

    detailPanel.add(detailToolbar, BorderLayout.NORTH);

    JPopupMenu editDetailPopupMenu = new JPopupMenu();
    editDetailPopupMenu.add(editDetailAction);
    editDetailPopupMenu.addSeparator();

    final ButtonGroup layoutGroup = new ButtonGroup();

    JRadioButtonMenuItem defaultLayoutRadio =
      new JRadioButtonMenuItem(
        new AbstractAction("Set to Default Layout") {
          public void actionPerformed(ActionEvent e) {
            setDetailPaneConversionPattern(
              DefaultLayoutFactory.getDefaultPatternLayout());
          }
        });
    editDetailPopupMenu.add(defaultLayoutRadio);
    layoutGroup.add(defaultLayoutRadio);
    defaultLayoutRadio.setSelected(true);

    JRadioButtonMenuItem tccLayoutRadio =
      new JRadioButtonMenuItem(
        new AbstractAction("Set to TCCLayout") {
          public void actionPerformed(ActionEvent e) {
            setDetailPaneConversionPattern(
              PatternLayout.TTCC_CONVERSION_PATTERN);
          }
        });
    editDetailPopupMenu.add(tccLayoutRadio);
    layoutGroup.add(tccLayoutRadio);

    PopupListener editDetailPopupListener =
      new PopupListener(editDetailPopupMenu);
    detail.addMouseListener(editDetailPopupListener);

    /*
     * Logger tree splitpane definition
     */
    nameTreeAndMainPanelSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, logTreePanel, lowerPanel);
    
    nameTreeAndMainPanelSplit.setToolTipText("Still under development....");
    nameTreeAndMainPanelSplit.setDividerLocation(-1);

    add(nameTreeAndMainPanelSplit, BorderLayout.CENTER);

    if (isLogTreeVisible()) {
        showLogTreePanel();
    } else {
        hideLogTreePanel();
    }

    /*
     * Other menu items
     */
    final JMenuItem menuItemBestFit = new JMenuItem("Best fit column");
    menuItemBestFit.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          if (currentPoint != null) {
            int column = table.columnAtPoint(currentPoint);
            int maxWidth = getMaxColumnWidth(column);
            table.getColumnModel().getColumn(column).setPreferredWidth(
              maxWidth);
          }
        }
      });

    JMenuItem menuItemColorPanel = new JMenuItem("LogPanel Color Filter...");
    menuItemColorPanel.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          showColorPreferences();
        }
      });
    menuItemColorPanel.setIcon(ChainsawIcons.ICON_PREFERENCES);

    JMenuItem menuItemLogPanelPreferences =
      new JMenuItem("LogPanel Preferences...");
    menuItemLogPanelPreferences.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          showPreferences();
        }
      });
    menuItemLogPanelPreferences.setIcon(ChainsawIcons.ICON_PREFERENCES);

    final JMenuItem menuItemFocusOn =
      new JMenuItem("Set 'refine focus' field");
    menuItemFocusOn.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          if (currentPoint != null) {
            String operator = "==";
            int column = table.columnAtPoint(currentPoint);
            int row = table.rowAtPoint(currentPoint);
            String colName = table.getColumnName(column);
            String value = "";

            if (colName.equalsIgnoreCase(ChainsawConstants.TIMESTAMP_COL_NAME)) {
              JComponent comp =
                (JComponent) table.getCellRenderer(row, column);

              if (comp instanceof JLabel) {
                value = ((JLabel) comp).getText();
              }
            } else {
              Object o = table.getValueAt(row, column);

              if (o != null) {
                if (o instanceof String[]) {
                  value = ((String[]) o)[0];
                  operator = "~=";
                } else {
                  value = o.toString();
                }
              }
            }

            if (columnNameKeywordMap.containsKey(colName)) {
              filterText.setText(
                columnNameKeywordMap.get(colName).toString() + " " + operator
                + " '" + value + "'");
            }
          }
        }
      });

    final JMenuItem menuDefineAddCustomFilter =
      new JMenuItem("Add to 'refine focus' field");
    menuDefineAddCustomFilter.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          if (currentPoint != null) {
            String operator = "==";
            int column = table.columnAtPoint(currentPoint);
            int row = table.rowAtPoint(currentPoint);
            String colName = table.getColumnName(column);
            String value = "";

            if (colName.equalsIgnoreCase(ChainsawConstants.TIMESTAMP_COL_NAME)) {
              JComponent comp =
                (JComponent) table.getCellRenderer(row, column);

              if (comp instanceof JLabel) {
                value = ((JLabel) comp).getText();
              }
            } else {
              Object o = table.getValueAt(row, column).toString();

              if (o instanceof String[]) {
                value = ((String[]) o)[0];
                operator = "~=";
              } else {
                value = o.toString();
              }
            }

            if (columnNameKeywordMap.containsKey(colName)) {
              filterText.setText(
                filterText.getText() + " && "
                + columnNameKeywordMap.get(colName).toString() + " "
                + operator + " '" + value + "'");
            }
          }
        }
      });

    final JPopupMenu p = new JPopupMenu();

    final Action clearFocusAction =
      new AbstractAction("Clear 'refine focus' field") {
        public void actionPerformed(ActionEvent e) {
          filterText.setText(null);
          ruleMediator.setRefinementRule(null);
        }
      };

    final JMenuItem menuItemToggleDock = new JMenuItem("Undock/dock");

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

    /*
     * Popup definition
     */
    p.add(clearFocusAction);
    p.add(menuItemFocusOn);
    p.add(menuDefineAddCustomFilter);
    p.add(new JSeparator());

    p.add(menuItemBestFit);
    p.add(new JSeparator());

    p.add(menuItemToggleDetails);
    p.add(menuItemLoggerTree);
    p.add(menuItemToggleToolTips);
    p.add(new JSeparator());
    p.add(menuItemScrollBottom);

    p.add(new JSeparator());
    p.add(menuItemToggleDock);

    p.add(new JSeparator());
    p.add(menuItemColorPanel);
    p.add(menuItemLogPanelPreferences);

    final PopupListener popupListener = new PopupListener(p);

    eventsPane.addMouseListener(popupListener);
    table.addMouseListener(popupListener);
  }

  /**
   * Accessor
   *
   * @return namespace
   *
   * @see Profileable
   */
  public String getNamespace() {
    return getIdentifier();
  }

  /**
   * Accessor
   *
   * @return identifier
   *
   * @see EventBatchListener
   */
  public String getInterestedIdentifier() {
    return getIdentifier();
  }

  /**
   * Process events associated with the identifier.  Currently assumes it only
   * receives events which share this LogPanel's identifier
   *
   * @param ident identifier shared by events
   * @param eventBatchEntrys list of EventBatchEntry objects
   */
  public void receiveEventBatch(String ident, List eventBatchEntrys) {
    /*
     * if this panel is paused, we totally ignore events
     */
    if (isPaused()) {
      return;
    }

    //table.getSelectionModel().setValueIsAdjusting(true);
    boolean rowAdded = false;

    int first = tableModel.getLastAdded() + 1;

    for (Iterator iter = eventBatchEntrys.iterator(); iter.hasNext();) {
      ChainsawEventBatchEntry entry = (ChainsawEventBatchEntry) iter.next();

      updateOtherModels(entry);

      boolean isCurrentRowAdded = tableModel.isAddRow(entry.getEvent(), true);
      rowAdded = rowAdded ? true : isCurrentRowAdded;
    }

    table.getSelectionModel().setValueIsAdjusting(false);

    //tell the model to notify the count listeners
    tableModel.notifyCountListeners();

    if (rowAdded) {
      if (tableModel.isSortEnabled()) {
        tableModel.sort();
      }

      tableModel.fireTableEvent(
        first, tableModel.getLastAdded(), eventBatchEntrys.size());

      if (scroll && !bypassScrollFind && !bypassScrollSelection) {
        table.scrollToBottom(
          table.columnAtPoint(table.getVisibleRect().getLocation()));
      }

      //always update detail pane (since we may be using a cyclic buffer which is full)
      detailPaneUpdater.setSelectedRow(table.getSelectedRow());
    }
  }

  /**
   * Load settings from the panel preference model
   *
   * @param event
   *
   * @see LogPanelPreferenceModel
   */
  public void loadSettings(LoadSettingsEvent event) {
    preferenceModel.setLevelIcons(event.asBoolean("levelIcons"));
    preferenceModel.setDateFormatPattern(
      event.getSetting("dateFormatPattern"));
    preferenceModel.setLoggerPrecision(event.getSetting("loggerPrecision"));
    preferenceModel.setToolTips(event.asBoolean("toolTips"));
    preferenceModel.setScrollToBottom(event.asBoolean("scrollToBottom"));
    scroll = event.asBoolean("scrollToBottom");
    preferenceModel.setLogTreePanelVisible(
      event.asBoolean("logTreePanelVisible"));
    preferenceModel.setDetailPaneVisible(event.asBoolean("detailPaneVisible"));

    logTreePanel.ignore(event.getSettingsStartingWith("Logger.Ignore."));

    File f =
      new File(
        SettingsManager.getInstance().getSettingsDirectory() + File.separator
        + identifier + COLUMNS_EXTENSION);

    if (f.exists()) {
      loadColumnSettings();
    } else {
      loadDefaultColumnSettings(event);
    }

    File f2 =
      new File(
        SettingsManager.getInstance().getSettingsDirectory() + File.separator
        + identifier + COLORS_EXTENSION);

    if (f2.exists()) {
      loadColorSettings();
    }
  }

  /**
   * Save preferences to the panel preference model
   *
   * @param event
   *
   * @see LogPanelPreferenceModel
   */
  public void saveSettings(SaveSettingsEvent event) {
    event.saveSetting("levelIcons", preferenceModel.isLevelIcons());
    event.saveSetting(
      "dateFormatPattern", preferenceModel.getDateFormatPattern());
    event.saveSetting("loggerPrecision", preferenceModel.getLoggerPrecision());
    event.saveSetting("toolTips", preferenceModel.isToolTips());
    event.saveSetting("scrollToBottom", preferenceModel.isScrollToBottom());
    event.saveSetting(
      "detailPaneVisible", preferenceModel.isDetailPaneVisible());
    event.saveSetting(
      "logTreePanelVisible", preferenceModel.isLogTreePanelVisible());

    Set set = logTreePanel.getHiddenSet();
    int index = 0;

    for (Iterator iter = set.iterator(); iter.hasNext();) {
      Object logger = iter.next();
      event.saveSetting("Logger.Ignore." + index++, logger.toString());
    }

    saveColumnSettings();
    saveColorSettings();
  }

  /**
   * Display the panel preferences frame
   */
  void showPreferences() {
    preferencesPanel.updateModel();
    preferencesFrame.show();
  }

  /**
   * Display the color rule frame
   */
  void showColorPreferences() {
    colorFrame.pack();
    colorFrame.show();
  }

  /**
   * Toggle panel preference for detail visibility on or off
   */
  void toggleDetailVisible() {
    preferenceModel.setDetailPaneVisible(
      !preferenceModel.isDetailPaneVisible());
  }

  /**
   * Accessor
   *
   * @return detail visibility flag
   */
  boolean isDetailVisible() {
    return preferenceModel.isDetailPaneVisible();
  }

  /**
   * Toggle panel preference for logger tree visibility on or off
   */
  void toggleLogTreeVisible() {
    preferenceModel.setLogTreePanelVisible(
      !preferenceModel.isLogTreePanelVisible());
  }

  /**
   * Accessor
   *
   * @return logger tree visibility flag
   */
  boolean isLogTreeVisible() {
    return preferenceModel.isLogTreePanelVisible();
  }

  /**
   * Return all events
   *
   * @return list of LoggingEvents
   */
  List getEvents() {
    return tableModel.getAllEvents();
  }

  List getMatchingEvents(Rule rule) {
    return tableModel.getMatchingEvents(rule);
  }

  /**
   * Remove all events
   */
  void clearEvents() {
    clearModel();
  }

  /**
   * Accessor
   *
   * @return identifier
   */
  String getIdentifier() {
    return identifier;
  }

  /**
   * Undocks this DockablePanel by removing the panel from the LogUI window
   * and placing it inside it's own JFrame.
   */
  void undock() {
    setDocked(false);
    externalPanel.removeAll();
    findPanel.removeAll();
    findPanel.add(findField);

    externalPanel.add(undockedToolbar, BorderLayout.NORTH);
    externalPanel.add(nameTreeAndMainPanelSplit, BorderLayout.CENTER);
    externalPanel.setDocked(false);
    undockedFrame.setSize(getSize());

    undockedFrame.setLocation(getBounds().x, getBounds().y);

    undockedFrame.setVisible(true);
    dockingAction.putValue(Action.NAME, "Dock");
    dockingAction.putValue(Action.SMALL_ICON, ChainsawIcons.ICON_DOCK);
  }

  /**
   * Add an eventCountListener
   *
   * @param l
   */
  void addEventCountListener(EventCountListener l) {
    tableModel.addEventCountListener(l);
  }

  /**
   * Accessor
   *
   * @return paused flag
   */
  boolean isPaused() {
    return paused;
  }

  /**
   * Modifies the Paused property and notifies the listeners
   *
   * @param paused
   */
  void setPaused(boolean paused) {
    boolean oldValue = this.paused;
    this.paused = paused;
    firePropertyChange("paused", oldValue, paused);
  }

  /**
   * Add a preference propertyChangeListener
   *
   * @param listener
   */
  void addPreferencePropertyChangeListener(PropertyChangeListener listener) {
    preferenceModel.addPropertyChangeListener(listener);
  }

  /**
   * Toggle the LoggingEvent container from either managing a cyclic buffer of
   * events or an ArrayList of events
   */
  void toggleCyclic() {
    tableModel.setCyclic(!tableModel.isCyclic());
  }

  /**
   * Accessor
   *
   * @return flag answering if LoggingEvent container is a cyclic buffer
   */
  boolean isCyclic() {
    return tableModel.isCyclic();
  }

  public boolean updateRule(String ruleText) {
    if ((ruleText == null) || ((ruleText != null) && ruleText.equals(""))) {
      findRule = null;
      colorizer.setFindRule(null);
      bypassScrollFind = false;
      findField.setToolTipText(
        "Enter expression - right click or ctrl-space for menu");

      return false;
    } else {
      bypassScrollFind = true;

      try {
        findField.setToolTipText(
          "Enter expression - right click or ctrl-space for menu");
        findRule = ExpressionRule.getRule(ruleText);
        colorizer.setFindRule(findRule);

        return true;
      } catch (IllegalArgumentException re) {
        findField.setToolTipText(re.getMessage());
        colorizer.setFindRule(null);

        return false;
      }
    }
  }

  /**
   * Display the detail pane, using the last known divider location
   */
  private void showDetailPane() {
    lowerPanel.setDividerSize(dividerSize);
    lowerPanel.setDividerLocation(lastDetailPanelSplitLocation);
    detailPanel.setVisible(true);
    lowerPanel.repaint();
  }

  /**
   * Hide the detail pane, holding the current divider location for later use
   */
  private void hideDetailPane() {
    int currentSize = lowerPanel.getHeight() - lowerPanel.getDividerSize();

    if (currentSize > 0) {
      lastDetailPanelSplitLocation =
        (double) lowerPanel.getDividerLocation() / currentSize;
     }

    lowerPanel.setDividerSize(0);
    detailPanel.setVisible(false);
    lowerPanel.repaint();
  }

  /**
   * Display the log tree pane, using the last known divider location
   */
  private void showLogTreePanel() {
    nameTreeAndMainPanelSplit.setDividerSize(dividerSize);
    nameTreeAndMainPanelSplit.setDividerLocation(
      lastLogTreePanelSplitLocation);
    logTreePanel.setVisible(true);
    nameTreeAndMainPanelSplit.repaint();
  }

  /**
   * Hide the log tree pane, holding the current divider location for later use
   */
  private void hideLogTreePanel() {
    //subtract one to make sizes match
    int currentSize = nameTreeAndMainPanelSplit.getWidth() - nameTreeAndMainPanelSplit.getDividerSize() - 1;

    if (currentSize > 0) {
      lastLogTreePanelSplitLocation =
        (double) nameTreeAndMainPanelSplit.getDividerLocation() / currentSize;
    }
    nameTreeAndMainPanelSplit.setDividerSize(0);
    logTreePanel.setVisible(false);
    nameTreeAndMainPanelSplit.repaint();
  }

  /**
   * Return a toolbar used by the undocked LogPanel's frame
   *
   * @return toolbar
   */
  private JToolBar createDockwindowToolbar() {
    final JToolBar toolbar = new JToolBar();
    toolbar.setFloatable(false);

    final Action dockPauseAction =
      new AbstractAction("Pause") {
        public void actionPerformed(ActionEvent evt) {
          setPaused(!isPaused());
        }
      };

    dockPauseAction.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_P));
    dockPauseAction.putValue(
      Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("F12"));
    dockPauseAction.putValue(
      Action.SHORT_DESCRIPTION,
      "Halts the display, while still allowing events to stream in the background");
    dockPauseAction.putValue(
      Action.SMALL_ICON, new ImageIcon(ChainsawIcons.PAUSE));

    final SmallToggleButton dockPauseButton =
      new SmallToggleButton(dockPauseAction);
    dockPauseButton.setText("");

    dockPauseButton.getModel().setSelected(isPaused());

    addPropertyChangeListener(
      "paused",
      new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
          dockPauseButton.getModel().setSelected(isPaused());
        }
      });
    toolbar.add(dockPauseButton);

    Action dockShowPrefsAction =
      new AbstractAction("") {
        public void actionPerformed(ActionEvent arg0) {
          showPreferences();
        }
      };

    dockShowPrefsAction.putValue(
      Action.SHORT_DESCRIPTION, "Define preferences...");
    dockShowPrefsAction.putValue(
      Action.SMALL_ICON, ChainsawIcons.ICON_PREFERENCES);

    Action dockToggleLogTreeAction =
      new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          toggleLogTreeVisible();
        }
      };

    dockToggleLogTreeAction.putValue(Action.SMALL_ICON, null);

    dockToggleLogTreeAction.putValue(Action.NAME, "Logger Tree");

    dockToggleLogTreeAction.putValue(
      Action.SHORT_DESCRIPTION, "Toggles the Log Tree panel");
    dockToggleLogTreeAction.putValue(Action.SMALL_ICON, null);

    toolbar.add(new SmallButton(dockShowPrefsAction));

    SmallToggleButton toggleLogTreeButton =
      new SmallToggleButton(dockToggleLogTreeAction);
    toggleLogTreeButton.setSelected(isLogTreeVisible());
    toolbar.add(toggleLogTreeButton);
    toolbar.addSeparator();

    final Action undockedClearAction =
      new AbstractAction("Clear") {
        public void actionPerformed(ActionEvent arg0) {
          clearModel();
        }
      };

    undockedClearAction.putValue(
      Action.SMALL_ICON, new ImageIcon(ChainsawIcons.DELETE));
    undockedClearAction.putValue(
      Action.SHORT_DESCRIPTION, "Removes all the events from the current view");

    final SmallButton dockClearButton = new SmallButton(undockedClearAction);
    dockClearButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
      KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, InputEvent.CTRL_MASK),
      undockedClearAction.getValue(Action.NAME));
    dockClearButton.getActionMap().put(
      undockedClearAction.getValue(Action.NAME), undockedClearAction);

    dockClearButton.setText("");
    toolbar.add(dockClearButton);
    toolbar.addSeparator();

    findField = new JTextField();
    findField.addKeyListener(
      new ExpressionRuleContext(filterModel, findField));

    final Action undockedFindNextAction =
      new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          findNext();
        }
      };

    undockedFindNextAction.putValue(Action.NAME, "Find next");
    undockedFindNextAction.putValue(
      Action.SHORT_DESCRIPTION,
      "Find the next occurrence of the rule from the current row");
    undockedFindNextAction.putValue(
      Action.SMALL_ICON, new ImageIcon(ChainsawIcons.DOWN));

    SmallButton undockedFindNextButton =
      new SmallButton(undockedFindNextAction);

    undockedFindNextButton.setAction(undockedFindNextAction);
    undockedFindNextButton.setText("");
    undockedFindNextButton.getActionMap().put(
      undockedFindNextAction.getValue(Action.NAME), undockedFindNextAction);
    undockedFindNextButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
      KeyStroke.getKeyStroke("F3"),
      undockedFindNextAction.getValue(Action.NAME));

    final Action undockedFindPreviousAction =
      new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          findPrevious();
        }
      };

    undockedFindPreviousAction.putValue(Action.NAME, "Find previous");
    undockedFindPreviousAction.putValue(
      Action.SHORT_DESCRIPTION,
      "Find the previous occurrence of the rule from the current row");
    undockedFindPreviousAction.putValue(
      Action.SMALL_ICON, new ImageIcon(ChainsawIcons.UP));

    SmallButton undockedFindPreviousButton =
      new SmallButton(undockedFindPreviousAction);

    undockedFindPreviousButton.setAction(undockedFindPreviousAction);
    undockedFindPreviousButton.setText("");
    undockedFindPreviousButton.getActionMap().put(
      undockedFindPreviousAction.getValue(Action.NAME),
      undockedFindPreviousAction);
    undockedFindPreviousButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                              .put(
      KeyStroke.getKeyStroke(KeyEvent.VK_F3, KeyEvent.SHIFT_MASK),
      undockedFindPreviousAction.getValue(Action.NAME));

    Dimension findSize = new Dimension(170, 22);
    Dimension findPanelSize = new Dimension(175, 30);
    findPanel.setPreferredSize(findPanelSize);
    findPanel.setMaximumSize(findPanelSize);
    findPanel.setMinimumSize(findPanelSize);
    findField.setPreferredSize(findSize);
    findField.setMaximumSize(findSize);
    findField.setMinimumSize(findSize);
    findPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
    findField.setAlignmentY(Component.CENTER_ALIGNMENT);
    
    toolbar.add(findPanel);
    toolbar.add(undockedFindNextButton);
    toolbar.add(undockedFindPreviousButton);

    toolbar.addSeparator();

    Action redockAction =
      new AbstractAction("", ChainsawIcons.ICON_DOCK) {
        public void actionPerformed(ActionEvent arg0) {
          dock();
        }
      };

    redockAction.putValue(
      Action.SHORT_DESCRIPTION,
      "Docks this window back with the main Chainsaw window");

    SmallButton redockButton = new SmallButton(redockAction);
    toolbar.add(redockButton);

    return toolbar;
  }

  /**
   * Update the status bar with current selected row and row count
   */
  private void updateStatusBar() {
    SwingUtilities.invokeLater(
      new Runnable() {
        public void run() {
          statusBar.setSelectedLine(
            table.getSelectedRow() + 1, tableModel.getRowCount(),
            tableModel.size());
        }
      });
  }

  /**
   * Update the detail pane layout text
   *
   * @param conversionPattern layout text
   */
  private void setDetailPaneConversionPattern(String conversionPattern) {
    String oldPattern = getDetailPaneConversionPattern();
    ((EventDetailLayout) detailLayout).setConversionPattern(conversionPattern);
    firePropertyChange(
      "detailPaneConversionPattern", oldPattern,
      getDetailPaneConversionPattern());
  }

  /**
   * Accessor
   *
   * @return conversionPattern layout text
   */
  private String getDetailPaneConversionPattern() {
    return ((EventDetailLayout) detailLayout).getConversionPattern();
  }

  /**
   * Reset the LoggingEvent container, detail panel and status bar
   */
  private void clearModel() {
    tableModel.clearModel();

    synchronized (detail) {
      detailPaneUpdater.setSelectedRow(-1);
      detail.notify();
    }

    statusBar.setNothingSelected();
  }

  /**
   * Finds the next row matching the current find rule, and ensures it is made
   * visible
   *
   */
  public void findNext() {
    updateRule(findField.getText());

    if (findRule != null) {
      try {
        final int nextRow =
          tableModel.find(findRule, table.getSelectedRow() + 1, true);

        if (nextRow > -1) {
          table.scrollToRow(
            nextRow, table.columnAtPoint(table.getVisibleRect().getLocation()));
          findField.setToolTipText("Enter an expression");
        }
      } catch (IllegalArgumentException iae) {
        findField.setToolTipText(iae.getMessage());
        colorizer.setFindRule(null);
      }
    }
  }

  /**
   * Finds the previous row matching the current find rule, and ensures it is made
   * visible
   *
   */
  public void findPrevious() {
    updateRule(findField.getText());

    if (findRule != null) {
      try {
        final int previousRow =
          tableModel.find(findRule, table.getSelectedRow() - 1, false);

        if (previousRow > -1) {
          table.scrollToRow(
            previousRow,
            table.columnAtPoint(table.getVisibleRect().getLocation()));
          findField.setToolTipText("Enter an expression");
        }
      } catch (IllegalArgumentException iae) {
        findField.setToolTipText(iae.getMessage());
      }
    }
  }

  /**
   * Docks this DockablePanel by hiding the JFrame and placing the Panel back
   * inside the LogUI window.
   */
  private void dock() {
    setDocked(true);
    undockedFrame.setVisible(false);
    removeAll();

    add(nameTreeAndMainPanelSplit, BorderLayout.CENTER);
    externalPanel.setDocked(true);
    dockingAction.putValue(Action.NAME, "Undock");
    dockingAction.putValue(Action.SMALL_ICON, ChainsawIcons.ICON_UNDOCK);
  }

  /**
   * Save panel column settings
   */
  private void saveColumnSettings() {
    ObjectOutputStream o = null;

    try {
      File f =
        new File(
          SettingsManager.getInstance().getSettingsDirectory()
          + File.separator + getIdentifier() + COLUMNS_EXTENSION);
      o = new ObjectOutputStream(
          new BufferedOutputStream(new FileOutputStream(f)));

      Enumeration e = this.table.getColumnModel().getColumns();

      while (e.hasMoreElements()) {
        TableColumn c = (TableColumn) e.nextElement();

        if (c.getModelIndex() < ChainsawColumns.getColumnsNames().size()) {
          o.writeObject(
            new TableColumnData(
              (String) c.getHeaderValue(), c.getModelIndex(), c.getWidth()));
        } else {
          LogLog.debug(
            "Not saving col ' " + c.getHeaderValue()
            + "' not part of standard columns");
        }
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

  /**
   * Save panel color settings
   */
  private void saveColorSettings() {
    ObjectOutputStream o = null;

    try {
      File f =
        new File(
          SettingsManager.getInstance().getSettingsDirectory()
          + File.separator + getIdentifier() + COLORS_EXTENSION);
      o = new ObjectOutputStream(
          new BufferedOutputStream(new FileOutputStream(f)));

      o.writeObject(colorizer.getRules());
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

  /**
   * Load panel column settings
   */
  private void loadColumnSettings() {
    File f =
      new File(
        SettingsManager.getInstance().getSettingsDirectory() + File.separator
        + identifier + COLUMNS_EXTENSION);

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
      TableColumnModel model = table.getColumnModel();

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
   * Load default column settings if no settings exist for this identifier
   *
   * @param event
   */
  private void loadDefaultColumnSettings(LoadSettingsEvent event) {
    String columnOrder = event.getSetting(TABLE_COLUMN_ORDER);

    TableColumnModel columnModel = table.getColumnModel();

    Map columnNameMap = new HashMap();

    for (int i = 0; i < columnModel.getColumnCount(); i++) {
      columnNameMap.put(table.getColumnName(i), columnModel.getColumn(i));
    }

    int index = 0;
    StringTokenizer tok = new StringTokenizer(columnOrder, ",");
    List sortedColumnList = new ArrayList();

    /*
       remove all columns from the table that exist in the model
       and add in the correct order to a new arraylist
       (may be a subset of possible columns)
     **/
    while (tok.hasMoreElements()) {
      String element = (String) tok.nextElement();
      TableColumn column = (TableColumn) columnNameMap.get(element);

      if (column != null) {
        sortedColumnList.add(column);
        table.removeColumn(column);
      }
    }

    //re-add columns to the table in the order provided from the list
    for (Iterator iter = sortedColumnList.iterator(); iter.hasNext();) {
      TableColumn element = (TableColumn) iter.next();
      table.addColumn(element);
    }

    String columnWidths = event.getSetting(TABLE_COLUMN_WIDTHS);

    tok = new StringTokenizer(columnWidths, ",");
    index = 0;

    while (tok.hasMoreElements()) {
      String element = (String) tok.nextElement();

      try {
        int width = Integer.parseInt(element);

        if (index > (columnModel.getColumnCount() - 1)) {
          LogLog.warn(
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

  public JTextField getFindTextField() {
    return findField;
  }

  /**
   * Load panel color settings
   */
  private void loadColorSettings() {
    File f =
      new File(
        SettingsManager.getInstance().getSettingsDirectory() + File.separator
        + identifier + COLORS_EXTENSION);

    if (f.exists()) {
      ObjectInputStream s = null;

      try {
        s = new ObjectInputStream(
            new BufferedInputStream(new FileInputStream(f)));

        Map map = (Map) s.readObject();
        colorizer.setRules(map);
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
    }
  }

  /**
   * Iterate over all values in the column and return the longest width
   *
   * @param index column index
   *
   * @return longest width - relies on FontMetrics.stringWidth for calculation
   */
  private int getMaxColumnWidth(int index) {
    FontMetrics metrics = getGraphics().getFontMetrics();
    int longestWidth =
      metrics.stringWidth("  " + table.getColumnName(index) + "  ")
      + (2 * table.getColumnModel().getColumnMargin());

    for (int i = 0, j = tableModel.getRowCount(); i < j; i++) {
      Component c =
        renderer.getTableCellRendererComponent(
          table, table.getValueAt(i, index), false, false, i, index);

      if (c instanceof JLabel) {
        longestWidth =
          Math.max(longestWidth, metrics.stringWidth(((JLabel) c).getText()));
      }
    }

    return longestWidth + 5;
  }

  /**
   * ensures the Entry map of all the unque logger names etc, that is used for
   * the Filter panel is updated with any new information from the event
   *
   * @param entry
   */
  private void updateOtherModels(ChainsawEventBatchEntry entry) {
    LoggingEvent event = entry.getEvent();

    /*
     * EventContainer is a LoggerNameModel imp, use that for notifing
     */
    tableModel.addLoggerName(event.getLoggerName());

    filterModel.processNewLoggingEvent(event);
  }

  /**
   * This class receives notification when the Refine focus text field is
   * updated, where a backgrounh thread periodically wakes up and checks if
   * they have stopped typing yet. This ensures that the filtering of the
   * model is not done for every single character typed.
   *
   * @author Paul Smith psmith
   */
  private final class DelayedFilterTextDocumentListener
    implements DocumentListener {
    private static final long CHECK_PERIOD = 1000;
    private final JTextField filterText;
    private long lastTimeStamp = System.currentTimeMillis();
    private final Thread delayThread;
    private final String defaultToolTip;
    private String lastFilterText = null;

    private DelayedFilterTextDocumentListener(final JTextField filterText) {
      super();
      this.filterText = filterText;
      this.defaultToolTip = filterText.getToolTipText();

      this.delayThread =
        new Thread(
          new Runnable() {
            public void run() {
              while (true) {
                try {
                  Thread.sleep(CHECK_PERIOD);
                } catch (InterruptedException e) {
                }

                if (
                  (System.currentTimeMillis() - lastTimeStamp) < CHECK_PERIOD) {
                  // They typed something since the last check. we ignor
                  // this for a sample period
                  //                LogLog.debug("Typed something since the last check");
                } else if (
                  (System.currentTimeMillis() - lastTimeStamp) < (2 * CHECK_PERIOD)) {
                  // they stopped typing recently, but have stopped for at least
                  // 1 sample period. lets apply the filter
                  //                LogLog.debug("Typed something recently applying filter");
                  if (filterText.getText() != lastFilterText) {
                    lastFilterText = filterText.getText();
                    setFilter();
                  }
                } else {
                  // they stopped typing a while ago, let's forget about it
                  //                LogLog.debug(
                  //                  "They stoppped typing a while ago, assuming filter has been applied");
                }
              }
            }
          });

      delayThread.setPriority(Thread.MIN_PRIORITY);
      delayThread.start();
    }

    /**
     * Update timestamp
     *
     * @param e
     */
    public void insertUpdate(DocumentEvent e) {
      notifyChange();
    }

    /**
     * Update timestamp
     *
     * @param e
     */
    public void removeUpdate(DocumentEvent e) {
      notifyChange();
    }

    /**
     * Update timestamp
     *
     * @param e
     */
    public void changedUpdate(DocumentEvent e) {
      notifyChange();
    }

    /**
     * Update timestamp
     */
    private void notifyChange() {
      this.lastTimeStamp = System.currentTimeMillis();
    }

    /**
     * Update refinement rule based on the entered expression
     */
    private void setFilter() {
      if (filterText.getText().equals("")) {
        ruleMediator.setRefinementRule(null);
        filterText.setToolTipText(defaultToolTip);
      } else {
        try {
          ruleMediator.setRefinementRule(
            ExpressionRule.getRule(filterText.getText()));
          filterText.setToolTipText(defaultToolTip);
        } catch (IllegalArgumentException iae) {
          filterText.setToolTipText(iae.getMessage());
        }
      }
    }
  }

  /**
   * Update active tooltip
   */
  private final class TableColumnDetailMouseListener extends MouseMotionAdapter {
    private int currentRow = -1;

    private TableColumnDetailMouseListener() {
    }

    /**
     * Update tooltip based on mouse position
     *
     * @param evt
     */
    public void mouseMoved(MouseEvent evt) {
      currentPoint = evt.getPoint();

      if (preferenceModel.isToolTips()) {
        int row = table.rowAtPoint(evt.getPoint());

        if ((row == currentRow) || (row == -1)) {
          return;
        }

        currentRow = row;

        LoggingEvent event = tableModel.getRow(currentRow);

        if (event != null) {
          StringBuffer buf = new StringBuffer();
          buf.append(detailLayout.getHeader())
             .append(detailLayout.format(event)).append(
            detailLayout.getFooter());
          table.setToolTipText(buf.toString());
        }
      } else {
        table.setToolTipText(null);
      }
    }
  }

  /**
   * Column data helper class - this class is serialized when saving column
   * settings
   */
  private class TableColumnData implements Serializable {
    static final long serialVersionUID = 5350440293110513986L;
    private String colName;
    private int index;
    private int width;

    /**
     * Creates a new TableColumnData object.
     *
     * @param colName
     * @param index
     * @param width
     */
    public TableColumnData(String colName, int index, int width) {
      this.colName = colName;
      this.index = index;
      this.width = width;
    }

    /**
     * Accessor
     *
     * @return col name
     */
    public String getColName() {
      return colName;
    }

    /**
     * Accessor
     *
     * @return displayed index
     */
    public int getIndex() {
      return index;
    }

    /**
     * Accessor
     *
     * @return width
     */
    public int getWidth() {
      return width;
    }

    /**
     * Deserialize the state of the object
     *
     * @param in
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(java.io.ObjectInputStream in)
      throws IOException, ClassNotFoundException {
      colName = (String) in.readObject();
      index = in.readInt();
      width = in.readInt();
    }

    /**
     * Serialize the state of the object
     *
     * @param out
     *
     * @throws IOException
     */
    private void writeObject(java.io.ObjectOutputStream out)
      throws IOException {
      out.writeObject(colName);
      out.writeInt(index);
      out.writeInt(width);
    }
  }

  //if columnmoved or columnremoved callback received, re-apply table's sort index based
  //sort column name
  private class ChainsawTableColumnModelListener
    implements TableColumnModelListener {
    private ChainsawTableColumnModelListener() {
    }

    /**
     * If a new column was added to the display and that column was the exception column,
     * set the cell editor to the throwablerenderer
     *
     * @param e
     */
    public void columnAdded(TableColumnModelEvent e) {
      Enumeration enumeration = table.getColumnModel().getColumns();

      while (enumeration.hasMoreElements()) {
        TableColumn column = (TableColumn) enumeration.nextElement();

        if (
          (column.getModelIndex() + 1) == ChainsawColumns.INDEX_THROWABLE_COL_NAME) {
          column.setCellEditor(throwableRenderPanel);
        }
        if (column.getModelIndex() > 0) {
            preferenceModel.setColumnVisible(column.getHeaderValue().toString(), true);
        }
      }
    }

    /**
     * Update sorted column
     *
     * @param e
     */
    public void columnRemoved(TableColumnModelEvent e) {
      table.updateSortedColumn();
    }

    /**
     * Update sorted column
     *
     * @param e
     */
    public void columnMoved(TableColumnModelEvent e) {
      table.updateSortedColumn();
    }

    /**
     * Ignore margin changed
     *
     * @param e
     */
    public void columnMarginChanged(ChangeEvent e) {
    }

    /**
     * Ignore selection changed
     *
     * @param e
     */
    public void columnSelectionChanged(ListSelectionEvent e) {
    }
  }

  /**
   * Thread that periodically checks if the selected row has changed, and if
   * it was, updates the Detail Panel with the detailed Logging information
   */
  private class DetailPaneUpdater implements PropertyChangeListener {
    private int selectedRow = -1;

    private DetailPaneUpdater() {
    }

    /**
     * Update detail pane to display information about the LoggingEvent at index row
     *
     * @param row
     */
    private void setSelectedRow(int row) {
      selectedRow = row;
      updateDetailPane();
    }

    /**
     * Update detail pane
     */
    private void updateDetailPane() {
      String text = null;

      /*
       * Don't bother doing anything if it's not visible
       */
      if (!detail.isVisible()) {
        return;
      }

      if (selectedRow != -1) {
        LoggingEvent event = tableModel.getRow(selectedRow);

        if (event != null) {
          StringBuffer buf = new StringBuffer();
          buf.append(detailLayout.getHeader())
             .append(detailLayout.format(event)).append(
            detailLayout.getFooter());
          text = buf.toString();
        }
      }

      if (!((text != null) && !text.equals(""))) {
        text = "<html>Nothing selected</html>";
      }

      final String text2 = text;
      SwingUtilities.invokeLater(
        new Runnable() {
          public void run() {
            try {
                detail.setText(text2);
                detail.validate();
                if (text2.length() > 0) {
                    detail.setCaretPosition(0);
                }
            } catch (Exception e){} //ignore
          }
        });
    }

    /**
     * Update detail pane layout if it's changed
     *
     * @param arg0
     */
    public void propertyChange(PropertyChangeEvent arg0) {
      SwingUtilities.invokeLater(
        new Runnable() {
          public void run() {
            updateDetailPane();
          }
        });
    }
  }
}
