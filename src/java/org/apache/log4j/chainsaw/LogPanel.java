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
 * Created on Sep 8, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.apache.log4j.chainsaw;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
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
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
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
import javax.swing.ComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
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
import javax.swing.SwingUtilities;
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
import org.apache.log4j.chainsaw.prefs.LoadSettingsEvent;
import org.apache.log4j.chainsaw.prefs.SaveSettingsEvent;
import org.apache.log4j.chainsaw.prefs.SettingsListener;
import org.apache.log4j.chainsaw.prefs.SettingsManager;
import org.apache.log4j.chainsaw.rule.AbstractRule;
import org.apache.log4j.chainsaw.rule.Rule;
import org.apache.log4j.helpers.ISO8601DateFormat;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;


/**
   * LogPanel encapsulates all the necessary bits and pieces of a
   * floating window of Events coming from a specific Location.
   *
   * This is where most of the Swing components are constructed and laid out.
   */
public class LogPanel extends DockablePanel implements SettingsListener,
  EventBatchListener {
  private final JFrame preferencesFrame = new JFrame();
  private final JFrame colorFrame = new JFrame();
  private ThrowableRenderPanel throwableRenderPanel;
  private MouseFocusOnAdaptor mouseFocusOnAdaptor = new MouseFocusOnAdaptor();
  private boolean paused = false;
  private final FilterModel filterModel = new FilterModel();
  private final RuleMediator ruleMediator = new RuleMediator();
  private final FocusOnMenu focusOnMenu = new FocusOnMenu();
  final EventContainer tableModel;
  final JEditorPane detail;
  final JSplitPane lowerPanel;
  final DetailPaneUpdater detailPaneUpdater;
  final JPanel upperPanel;
  final JPanel eventsAndStatusPanel;
  final JFrame undockedFrame;
  final DockablePanel externalPanel;
  final Action dockingAction;
  final JSortTable table;
  private final LogPanelPreferenceModel preferenceModel =
    new LogPanelPreferenceModel();
  private final LogPanelPreferencePanel preferencesPanel =
    new LogPanelPreferencePanel(preferenceModel);
  private final ColorPanel colorPanel;
  private String profileName = null;
  private final JDialog detailDialog = new JDialog((JFrame) null, true);
  final JPanel detailPanel = new JPanel(new BorderLayout());
  private final TableColorizingRenderer renderer;
  String identifier;
  final Map columnDisplayMap = new HashMap();
  final Map colorDisplayMap = new HashMap();

  //    final ColorDisplaySelector colorDisplaySelector;
  ScrollToBottom scrollToBottom;
  private final LogPanelLoggerTreeModel logTreeModel =
    new LogPanelLoggerTreeModel();
  private Layout detailPaneLayout = new EventDetailLayout();
  private Layout toolTipLayout = detailPaneLayout;
  private Point currentPoint;
  private final JSplitPane nameTreeAndMainPanelSplit;
  private final LoggerNameTreePanel logTreePanel;
  private boolean tooltipsEnabled;
  private final ChainsawStatusBar statusBar;
  private final JToolBar undockedToolbar;

  public LogPanel(
    final ChainsawStatusBar statusBar, final String ident, String eventType) {
    identifier = ident;
    this.statusBar = statusBar;

    preferencesFrame.setTitle("'" + ident + "' Log Panel Preferences");
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
    preferenceModel.addPropertyChangeListener(
      "levelIcons",
      new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
          renderer.setLevelUseIcons(
            ((Boolean) evt.getNewValue()).booleanValue());
          table.tableChanged(new TableModelEvent(getModel()));
        }
      });
    setDetailPaneConversionPattern(
      DefaultLayoutFactory.getDefaultPatternLayout());
    ((EventDetailLayout) toolTipLayout).setConversionPattern(
      DefaultLayoutFactory.getDefaultPatternLayout());

    preferenceModel.addPropertyChangeListener(
      "detailPaneVisible",
      new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
          boolean newValue = ((Boolean) evt.getNewValue()).booleanValue();

          if (newValue) {
            lowerPanel.setDividerLocation(400);
          }

          detailPanel.setVisible(newValue);
          lowerPanel.setOneTouchExpandable(newValue);
        }
      });

    preferenceModel.addPropertyChangeListener(
      "logTreePanelVisible",
      new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
          boolean newValue = ((Boolean) evt.getNewValue()).booleanValue();

          logTreePanel.setVisible(newValue);
        }
      });
    tableModel = new ChainsawCyclicBufferTableModel();

    colorFrame.setTitle("'" + ident + "' Color Filter");
    colorFrame.setIconImage(
      ((ImageIcon) ChainsawIcons.ICON_PREFERENCES).getImage());
    RuleColorizer colorizer = new RuleColorizer();
    colorizer.addPropertyChangeListener(new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equalsIgnoreCase("colorrule")) {
                if (table != null) {
                    table.repaint();
                }
            }
        }}
    );
    renderer = new TableColorizingRenderer(colorizer);
    colorPanel = new ColorPanel(colorizer, filterModel);
    colorFrame.getContentPane().add(colorPanel);

    preferencesFrame.setSize(640, 480);

    table = new JSortTable(tableModel);
    table.getColumnModel().addColumnModelListener(
      new ChainsawTableColumnModelListener(table));

    table.setAutoCreateColumnsFromModel(false);

    throwableRenderPanel = new ThrowableRenderPanel(table);

    table.getSelectionModel().addListSelectionListener(
      new ListSelectionListener() {
        public void valueChanged(ListSelectionEvent e) {
          if (e.getValueIsAdjusting() || !isVisible()) {
            return;
          }

          LoggingEvent event = null;
          int row = table.getSelectedRow();

          if (row > -1) {
            event = tableModel.getRow(row);
          }

          rebuildFocusOnMenuFromEvent(event);
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

    /**
             * We listen for new Key's coming in so we can get them automatically added as columns
            */
    tableModel.addNewKeyListener(
      new NewKeyListener() {
        public void newKeyAdded(NewKeyEvent e) {
          table.addColumn(new TableColumn(e.getNewModelIndex()));
        }
      });
    tableModel.addPropertyChangeListener(
      "cyclic",
      new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent arg0) {
          if (tableModel.isCyclic()) {
            statusBar.setMessage(
              "Changed to Cyclic Mode. Maximum # events kept: "
              + tableModel.getMaxSize());
          } else {
            statusBar.setMessage(
              "Changed to Unlimited Mode. Warning, you may run out of memory.");
          }
        }
      });

    table.addMouseListener(mouseFocusOnAdaptor);
    table.addMouseMotionListener(mouseFocusOnAdaptor);

    table.setRowHeight(20);
    table.setShowGrid(false);

    scrollToBottom = new ScrollToBottom(true);

    // ==========================================
    tableModel.addLoggerNameListener(logTreeModel);

    /**
     * Set the Display rule to use the mediator, the model will add itself as a property
     * change listener and update itself when the rule changes.
     */
    tableModel.setDisplayRule(ruleMediator);

    logTreePanel = new LoggerNameTreePanel(logTreeModel);

  /**
   * Set the LoggerRule to be the LoggerTreePanel, as this visual component
   * is a rule itself, and the RuleMediator will automatically listen
   * when it's rule state changes.
   */
    ruleMediator.setLoggerRule(logTreePanel);

    /***
     * Setup a popup menu triggered for Timestamp column to allow time stamp format changes
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

    setLayout(new BorderLayout());
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

    PropertyChangeListener datePrefsChangeListener =
      new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
          LogPanelPreferenceModel model =
            (LogPanelPreferenceModel) evt.getSource();

          isoButton.setSelected(model.isUseISO8601Format());
          simpleTimeButton.setSelected(!model.isUseISO8601Format());

          if (model.isUseISO8601Format()) {
            renderer.setDateFormatter(new ISO8601DateFormat());
          } else {
            renderer.setDateFormatter(
              new SimpleDateFormat(model.getDateFormatPattern()));
          }

          table.tableChanged(new TableModelEvent(getModel()));
        }
      };

      PropertyChangeListener loggerPrecisionChangeListener =
        new PropertyChangeListener() {
          public void propertyChange(PropertyChangeEvent evt) {
            LogPanelPreferenceModel model =
              (LogPanelPreferenceModel) evt.getSource();

              renderer.setLoggerPrecision(model.getLoggerPrecision());

            table.tableChanged(new TableModelEvent(getModel()));
          }
        };
        preferenceModel.addPropertyChangeListener(
          "loggerPrecision", loggerPrecisionChangeListener);

    preferenceModel.addPropertyChangeListener(
      "dateFormatPattern", datePrefsChangeListener);
    preferenceModel.addPropertyChangeListener(
      "dateFormatPattern", datePrefsChangeListener);

    //      TODO reload new Display rule for this panel
    //      displayFilter = loadDisplayFilter(ident);
    //      tableModel.setDisplayRule(displayFilter);
    //      displayFilter.addFilterChangedListener(tableModel);
    SettingsManager.getInstance().addSettingsListener(renderer);

    table.setDefaultRenderer(Object.class, renderer);

    final ColumnSelector columnSelector =
      new ColumnSelector(
        ident, new Vector(ChainsawColumns.getColumnsNames()), table);
    table.getColumnModel().addColumnModelListener(columnSelector);

    columnSelector.setIconImage(
      new ImageIcon(ChainsawIcons.WINDOW_ICON).getImage());

    JMenu menuColumnDisplayFilter =
      new JMenu("Apply display filter for column");

    JMenu menuColumnColorFilter = new JMenu("Apply color filter for column");

    ButtonGroup bg = new ButtonGroup();

    //      TODO fix this menu so that columns can be hidden/displayed
    //      Iterator iter = logUI.getFilterableColumns().iterator();
    //
    //      while (iter.hasNext()) {
    //        final String colName = (String) iter.next();
    //        JRadioButtonMenuItem thisItem = new JRadioButtonMenuItem(colName);
    //        thisItem.setFont(thisItem.getFont().deriveFont(Font.PLAIN));
    //        thisItem.addActionListener(
    //          new ActionListener() {
    //            public void actionPerformed(ActionEvent evt) {
    //              LoggingEvent lastSelected = null;
    //
    //              if (table.getSelectedRow() > -1) {
    //                lastSelected = tableModel.getRow(table.getSelectedRow());
    //              }
    //
    ////              colorDisplaySelector.applyColorUpdateForColumn(colName);
    ////              colorDisplaySelector.applyColorFilters(colName);
    //
    //              if (lastSelected != null) {
    //                int newIndex = tableModel.getRowIndex(lastSelected);
    //
    //                if (newIndex > -1) {
    //                  table.scrollToRow(
    //                    newIndex,
    //                    table.columnAtPoint(table.getVisibleRect().getLocation()));
    //                }
    //              }
    //            }
    //          });
    //        bg.add(thisItem);
    //        menuColumnColorFilter.add(thisItem);
    //        colorDisplayMap.put(colName, thisItem);
    //      }
    //
    //      ButtonGroup bg2 = new ButtonGroup();
    //      Iterator iter2 = logUI.getFilterableColumns().iterator();
    //
    //      while (iter2.hasNext()) {
    //        final String colName = (String) iter2.next();
    //        JRadioButtonMenuItem thisItem = new JRadioButtonMenuItem(colName);
    //        thisItem.setFont(thisItem.getFont().deriveFont(Font.PLAIN));
    //        thisItem.addActionListener(
    //          new ActionListener() {
    //            public void actionPerformed(ActionEvent evt) {
    //              LoggingEvent lastSelected = null;
    //
    //              if (table.getSelectedRow() > -1) {
    //                lastSelected = tableModel.getRow(table.getSelectedRow());
    //              }
    //
    ////              colorDisplaySelector.applyDisplayUpdateForColumn(colName);
    ////              colorDisplaySelector.applyDisplayFilters(colName);
    //
    //              if (lastSelected != null) {
    //                int newIndex = tableModel.getRowIndex(lastSelected);
    //
    //                if (newIndex > -1) {
    //                  table.scrollToRow(
    //                    newIndex,
    //                    table.columnAtPoint(table.getVisibleRect().getLocation()));
    //                }
    //              }
    //            }
    //          });
    //        bg2.add(thisItem);
    //        menuColumnDisplayFilter.add(thisItem);
    //        columnDisplayMap.put(colName, thisItem);
    //      }
    table.addMouseMotionListener(
      new MouseMotionAdapter() {
        int currentRow = -1;

        public void mouseMoved(MouseEvent evt) {
          currentPoint = evt.getPoint();

          if (tooltipsEnabled) {
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

    addPropertyChangeListener(
      "detailPaneConversionPattern", detailPaneUpdater);
    upperPanel = new JPanel(new BorderLayout());
    upperPanel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 0));

    final JLabel filterLabel = new JLabel("Refine focus on: ");
    filterLabel.setFont(filterLabel.getFont().deriveFont(Font.BOLD));

    JPanel upperLeftPanel =
      new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 0));
    upperLeftPanel.add(filterLabel);

    final JComboBox customFilterList =
      new JComboBox(ChainsawColumns.getColumnsNames().toArray());
    customFilterList.setFont(customFilterList.getFont().deriveFont(10f));

    final JTextField filterText = new JTextField();

    ruleMediator.addPropertyChangeListener(
      new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
          Rule rule = ruleMediator.getRefinementRule();

          //			TODO need to work out how to suspend the DocumentChangeListener reFilter temporarily while this bit updates
          if ((rule != null) && rule instanceof RefinementFocusRule) {
            RefinementFocusRule refineRule =
              (RefinementFocusRule) ruleMediator.getRefinementRule();
            filterText.setText(refineRule.getExpression());

            ComboBoxModel model = customFilterList.getModel();

            for (int i = 0; i < model.getSize(); i++) {
              if (
                model.getElementAt(i).toString().equals(
                    refineRule.getColumnName())) {
                customFilterList.setSelectedIndex(i);

                break;
              }
            }
          } else {
            filterText.setText("");
          }
        }
      });

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
          LogLog.warn(
            "setFilter() is not re-implemented in terms of the new Display rule yet");

          //          	TODO Display rule stuff
          if (filterText.getText().equals("")) {
            //              displayFilter.setCustomFilter(null);
          } else {
            //              detailPaneUpdater.setSelectedRow(-1);
            //
            //              displayFilter.setCustomFilter(
            //                new DisplayFilterEntry(
            //                  (String) customFilterList.getSelectedItem(),
            //                  filterText.getText(), ChainsawConstants.GLOBAL_MATCH));
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
            //            	TODO more Rule implementation here.
            //              displayFilter.setCustomFilter(
            //                new DisplayFilterEntry(
            //                  (String) customFilterList.getSelectedItem(),
            //                  filterText.getText(), ChainsawConstants.GLOBAL_MATCH));
          }
        }
      });

    upperPanel.add(filterText, BorderLayout.CENTER);
    upperPanel.add(upperLeftPanel, BorderLayout.WEST);

    //    final JCheckBox override = new JCheckBox();
    //    override.addActionListener(
    //      new ActionListener() {
    //        public void actionPerformed(ActionEvent evt) {
    //          LoggingEvent lastSelected = null;
    //
    //          if (table.getSelectedRow() > -1) {
    //            lastSelected = tableModel.getRow(table.getSelectedRow());
    //          }
    //
    //          //            displayFilter.setCustomFilterOverride(override.isSelected());
    //          if (lastSelected != null) {
    //            int newIndex = tableModel.getRowIndex(lastSelected);
    //
    //            if (newIndex > -1) {
    //              table.scrollToRow(
    //                newIndex,
    //                table.columnAtPoint(table.getVisibleRect().getLocation()));
    //            }
    //          }
    //        }
    //      });
    //    override.setToolTipText(
    //      "<html>Unchecked: Apply QuickFilter to displayed rows<br>Checked: Apply QuickFilter to ALL rows (override display filter setting)</html>");
    JPanel upperRightPanel =
      new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));

    //    upperRightPanel.add(override);
    upperPanel.add(upperRightPanel, BorderLayout.EAST);

    eventsAndStatusPanel = new JPanel();
    eventsAndStatusPanel.setLayout(new BorderLayout());

    final JScrollPane eventsPane = new JScrollPane(table);

    eventsPane.setPreferredSize(new Dimension(900, 300));

    eventsAndStatusPanel.add(eventsPane, BorderLayout.CENTER);

    final JPanel statusLabelPanel = new JPanel();
    statusLabelPanel.setLayout(new BorderLayout());

    //    final JLabel statusPaneLabel = new JLabel();
    //    statusPaneLabel.setFont(statusPaneLabel.getFont().deriveFont(Font.BOLD));
    //    statusPaneLabel.setHorizontalAlignment(JLabel.LEFT);
    //    statusPaneLabel.setVerticalAlignment(JLabel.CENTER);
    //    statusLabelPanel.add(statusPaneLabel, BorderLayout.WEST);
    //    statusLabelPanel.setBorder(BorderFactory.createEtchedBorder());
    statusLabelPanel.add(upperPanel, BorderLayout.CENTER);
    eventsAndStatusPanel.add(statusLabelPanel, BorderLayout.NORTH);

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

    final JToolBar detailToolbar = new JToolBar(JToolBar.HORIZONTAL);
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
          toggleDetailPanel();
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

    lowerPanel =
      new JSplitPane(
        JSplitPane.VERTICAL_SPLIT, eventsAndStatusPanel, detailPanel);
    lowerPanel.setBorder(null);
    lowerPanel.setDividerLocation(400);
    lowerPanel.setLastDividerLocation(-1);
    lowerPanel.setOneTouchExpandable(true);

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
          getPreferenceModel().setLogTreePanelVisible(false);
        }

        public void componentMoved(ComponentEvent e) {
        }

        public void componentResized(ComponentEvent e) {
        }

        public void componentShown(ComponentEvent e) {
          nameTreeAndMainPanelSplit.setEnabled(true);
          nameTreeAndMainPanelSplit.setOneTouchExpandable(true);
          nameTreeAndMainPanelSplit.setDividerLocation(-1);
          getPreferenceModel().setLogTreePanelVisible(true);
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

    final JMenuItem menuItemToggleDock = new JMenuItem("Undock/dock");

    undockedFrame = new JFrame(ident);
    undockedFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

    if (ChainsawIcons.UNDOCKED_ICON != null) {
      undockedFrame.setIconImage(
        new ImageIcon(ChainsawIcons.UNDOCKED_ICON).getImage());
    }

    externalPanel = new DockablePanel();
    externalPanel.setLayout(new BorderLayout());
    undockedFrame.getContentPane().add(externalPanel);

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
    undockedFrame.addWindowListener(
      new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          dock();
        }
      });

      JMenuItem menuItemColorPanel =
        new JMenuItem("LogPanel Color Filter...");
      menuItemColorPanel.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            showColorPanel();
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

    final JCheckBoxMenuItem menuItemToggleToolTips =
      new JCheckBoxMenuItem("Show ToolTips", tooltipsEnabled);
    menuItemToggleToolTips.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          tooltipsEnabled = menuItemToggleToolTips.isSelected();
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

            if (colName.equalsIgnoreCase(ChainsawConstants.TIMESTAMP_COL_NAME)) {
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

    //    JMenuItem menuItemRemoveColorFilter =
    //      new JMenuItem("Remove all color filters");
    //    menuItemRemoveColorFilter.addActionListener(
    //      new ActionListener() {
    //        public void actionPerformed(ActionEvent evt) {
    //          //            colorDisplaySelector.clearColors();
    //          colorFilter.clear();
    //        }
    //      });
    //    JMenuItem menuItemColumnSelector =
    //      new JMenuItem("Select display columns...");
    //    menuItemColumnSelector.addActionListener(
    //      new ActionListener() {
    //        public void actionPerformed(ActionEvent evt) {
    //          columnSelector.show();
    //        }
    //      });
    JMenuItem menuItemRemoveDisplayFilter =
      new JMenuItem("Remove all display filters");
    menuItemRemoveDisplayFilter.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          //            colorDisplaySelector.clearDisplay();
          tableModel.setDisplayRule(null);
        }
      });

    final JPopupMenu p = new JPopupMenu();

    final JCheckBoxMenuItem menuItemToggleDetails =
      new JCheckBoxMenuItem("Show Detail Pane");
    menuItemToggleDetails.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          toggleDetailPanel();
          menuItemToggleDetails.getModel().setSelected(isDetailPaneVisible());
        }
      });
    lowerPanel.addPropertyChangeListener(
      "dividerLocation",
      new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
          menuItemToggleDetails.getModel().setSelected(isDetailPaneVisible());
        }
      });
    menuItemToggleDetails.setIcon(new ImageIcon(ChainsawIcons.INFO));

    /**
     * We set this to true first, because the Split pane hasn't been laid
     * out yet, and isDetailPaneVisible() will therefore return false.
     */
    menuItemToggleDetails.getModel().setSelected(true);

    JMenuItem focusOnLoggerMenuItem =
      new JMenuItem(focusOnMenu.focusOnLoggerAction);
    p.add(focusOnLoggerMenuItem);
    p.add(focusOnMenu);

    final Action clearFocusAction =
      new AbstractAction("Clear refinement focus") {
        public void actionPerformed(ActionEvent e) {
          focusOnMenu.removeFocus();
        }
      };

    clearFocusAction.setEnabled(false);
    clearFocusAction.putValue(
      Action.SHORT_DESCRIPTION,
      "Removes any refinement focus you have currently set");
    ruleMediator.addPropertyChangeListener(
      new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
          clearFocusAction.setEnabled(
            (ruleMediator.getRefinementRule() != null)
            || (ruleMediator.getLoggerRule() != null));
        }
      });
    p.add(clearFocusAction);
    p.add(new JSeparator());

    p.add(menuItemToggleDetails);
    p.add(menuItemToggleToolTips);
    p.add(menuItemScrollBottom);

    p.add(new JSeparator());
    p.add(menuItemToggleDock);

    //	p.add(new JSeparator());
    //    p.add(menuDefineCustomFilter);
    p.add(new JSeparator());
    p.add(menuItemColorPanel);
    p.add(menuItemLogPanelPreferences);

    //    p.add(menuColumnDisplayFilter);
    //    p.add(menuColumnColorFilter);
    //    p.add(new JSeparator());
    //    JMenu removeSubMenu = new JMenu("Remove");
    //    JMenu selectSubMenu = new JMenu("Select");
    //    selectSubMenu.add(menuItemColumnSelector);
    //    removeSubMenu.add(menuItemRemoveColorFilter);
    //    removeSubMenu.add(menuItemRemoveDisplayFilter);
    //    p.add(menuItemColumnSelector);
    //    p.add(selectSubMenu);
    //    p.add(removeSubMenu);
    final PopupListener popupListener = new PopupListener(p);

    eventsPane.addMouseListener(popupListener);
    table.addMouseListener(popupListener);

    tableModel.addEventCountListener(
      new EventCountListener() {
        public void eventCountChanged(int currentCount, int totalCount) {
          if (LogPanel.this.isVisible()) {
            statusBar.setSelectedLine(0, currentCount, totalCount);
          }
        }
      });

    final ChainsawCyclicBufferTableModel cyclicModel =
      (ChainsawCyclicBufferTableModel) tableModel;
    tableModel.addEventCountListener(
      new EventCountListener() {
        final NumberFormat formatter = NumberFormat.getPercentInstance();
        boolean warning75 = false;
        boolean warning100 = false;

        public void eventCountChanged(int currentCount, int totalCount) {
          if (tableModel.isCyclic()) {
            double percent = ((double) totalCount) / cyclicModel.getMaxSize();
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
              statusBar.setMessage(msg);
            }
          }
        }
      });

    undockedToolbar = createDockwindowToolbar();
    externalPanel.add(undockedToolbar, BorderLayout.NORTH);
    undockedFrame.pack();

    Container container = detailDialog.getContentPane();
    final JTextArea detailArea = new JTextArea(10, 40);
    detailArea.setEditable(false);
    container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
    container.add(new JScrollPane(detailArea));
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
            detailArea.setText(o==null?"":o.toString());
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
    detailDialog.pack();
  }

  /**
  * @param event
  */
  protected void rebuildFocusOnMenuFromEvent(LoggingEvent event) {
    focusOnMenu.setEvent(event);
  }

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
      Action.SHORT_DESCRIPTION, "Define display and color filters...");
    dockShowPrefsAction.putValue(
      Action.SMALL_ICON, ChainsawIcons.ICON_PREFERENCES);

    Action dockToggleLogTreeAction =
      new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          toggleLogTreePanel();
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
    toggleLogTreeButton.setSelected(isLogTreePanelVisible());
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

    final JTextField findField = ChainsawToolBarAndMenus.createFindField();
    findField.getDocument().addDocumentListener(
      new DocumentListener() {
        public void insertUpdate(DocumentEvent e) {
          findInUndocked(false);
        }

        public void removeUpdate(DocumentEvent e) {
          findInUndocked(false);
        }

        public void changedUpdate(DocumentEvent e) {
          findInUndocked(false);
        }

        private void findInUndocked(boolean next) {
          if (next) {
            findNext(findField.getText());
          } else {
            find(findField.getText());
          }
        }
      });

    final Action undockedFindAction =
      new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          findNext(findField.getText());
        }
      };

    undockedFindAction.putValue(Action.NAME, "Find");
    undockedFindAction.putValue(
      Action.SHORT_DESCRIPTION, "Finds the next occurrence within this view");
    undockedFindAction.putValue(
      Action.SMALL_ICON, new ImageIcon(ChainsawIcons.FIND));

    SmallButton undockedFindNextButton = new SmallButton(undockedFindAction);

    undockedFindNextButton.setAction(undockedFindAction);
    undockedFindNextButton.setText("");
    undockedFindNextButton.getActionMap().put(
      undockedFindAction.getValue(Action.NAME), undockedFindAction);
    undockedFindNextButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
      KeyStroke.getKeyStroke("F3"), undockedFindAction.getValue(Action.NAME));

    toolbar.add(undockedFindNextButton);
    toolbar.add(findField);

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

  public void addEventCountListener(EventCountListener l) {
    tableModel.addEventCountListener(l);
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
            table.getSelectedRow() + 1, getModel().getRowCount(),
            getModel().size());
        }
      });
  }

  void setDetailPaneConversionPattern(String conversionPattern) {
    String oldPattern = getDetailPaneConversionPattern();
    ((EventDetailLayout) detailPaneLayout).setConversionPattern(
      conversionPattern);
    firePropertyChange(
      "detailPaneConversionPattern", oldPattern,
      getDetailPaneConversionPattern());
  }

  String getDetailPaneConversionPattern() {
    return ((EventDetailLayout) detailPaneLayout).getConversionPattern();
  }

  void showPreferences() {
    preferencesPanel.updateModel();
    preferencesFrame.show();
  }

  void showColorPanel() {
    colorFrame.pack();
    colorFrame.show();
  }

  EventContainer getModel() {
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
    undockedFrame.setVisible(false);
    removeAll();

    //      add(lowerPanel, BorderLayout.CENTER);
    add(nameTreeAndMainPanelSplit, BorderLayout.CENTER);
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
   * returns true if the DetailPane is viewable, that is
   * if the SplitPane's properties are such that the lower pane
   * would be seen, and so returns a boolean expression
   * of the SplitPanes divider location
   * @return
   */
  boolean isDetailPaneVisible() {
    return getPreferenceModel().isDetailPaneVisible();
  }

  /**
   * Shows or hides the Detail Pane depending on the Last
   * known position
   *
   */
  void toggleDetailPanel() {
    getPreferenceModel().setDetailPaneVisible(!isDetailPaneVisible());
  }

  void toggleLogTreePanel() {
    getPreferenceModel().setLogTreePanelVisible(
      !getPreferenceModel().isLogTreePanelVisible());
  }

  public void saveSettings() {
    saveColumnSettings(identifier, table.getColumnModel());

    //      colorDisplaySelector.save();
    //      TODO save display rule settings
    //      displayFilter.save();
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
          + File.separator + ident + LogUI.COLUMNS_EXTENSION);
      o = new ObjectOutputStream(
          new BufferedOutputStream(new FileOutputStream(f)));

      Enumeration e = model.getColumns();

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

  /* (non-Javadoc)
   * @see org.apache.log4j.chainsaw.prefs.Profileable#loadSettings(org.apache.log4j.chainsaw.prefs.LoadSettingsEvent)
   */
  public void loadSettings(LoadSettingsEvent event) {
    File f =
      new File(
        SettingsManager.getInstance().getSettingsDirectory() + File.separator
        + identifier + LogUI.COLUMNS_EXTENSION);

    renderer.loadSettings(event);

    if (f.exists()) {
      loadColumnSettings(identifier, table.getColumnModel());
    } else {
      loadDefaultColumnSettings(event);
    }
  }

  void loadDefaultColumnSettings(LoadSettingsEvent event) {
    String columnOrder = event.getSetting(LogUI.TABLE_COLUMN_ORDER);

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
          "Moving column " + element + " from index " + column.getModelIndex()
          + " to index " + index++);
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
    String columnWidths = event.getSetting(LogUI.TABLE_COLUMN_WIDTHS);

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
        SettingsManager.getInstance().getSettingsDirectory() + File.separator
        + ident + LogUI.COLUMNS_EXTENSION);

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

  public boolean isLogTreePanelVisible() {
    return getPreferenceModel().isLogTreePanelVisible();
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

      updateOtherModels(entry);

      boolean isCurrentRowAdded = tableModel.isAddRow(entry.getEvent(), true);
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
  private void updateOtherModels(ChainsawEventBatchEntry entry) {
    LoggingEvent event = entry.getEvent();
    String eventType = entry.getEventType();
    String level = event.getLevel().toString();

    /**
     * EventContainer is a LoggerNameModel imp, use that for notifing
     */
    tableModel.addLoggerName(event.getLoggerName());

    filterModel.processNewLoggingEvent(eventType, event);
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
    Layout oldLayout = this.detailPaneLayout;
    this.detailPaneLayout = detailPaneLayout;
    firePropertyChange("detailPaneLayout", oldLayout, detailPaneLayout);
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

  /**
   *
   */
  public void toggleCyclic() {
    getModel().setCyclic(!getModel().isCyclic());
  }

  /**
   *
   * @param column the column index matching those in ChainsawColumns class
   * @param value the value to focus on
   */
  private void focusOnColumnValue(int column, final Object value) {
    if ((column) == ChainsawColumns.INDEX_LOGGER_COL_NAME) {
      logTreePanel.setFocusOn(value.toString());
    } else {
      if (value == null) {
        ruleMediator.setRefinementRule(null);
      } else {
        ruleMediator.setRefinementRule(
          new RefinementFocusRule(
            ChainsawColumns.getColumnName(column), value.toString()) {
            public boolean evaluate(LoggingEvent e) {
              Object object =
                LoggingEventFieldResolver.getInstance().getValue(
                  getColumnName(), e);

              if (object == null) {
                return false;
              }

              return object.equals(value);
            }
          });
      }
    }
  }

  /**
   * Returns this LogPanels Preerence model currently in Use
   * @return
   */
  public final LogPanelPreferenceModel getPreferenceModel() {
    return preferenceModel;
  }

  private abstract class RefinementFocusRule extends AbstractRule {
    private String expression;
    private String columnName;

    private RefinementFocusRule(String columnName, String expression) {
      this.columnName = columnName;
      this.expression = expression;
    }

    public String getColumnName() {
      return this.columnName;
    }

    public String getExpression() {
      return this.expression;
    }
  }

  private class MouseFocusOnAdaptor extends MouseAdapter
    implements MouseListener, MouseMotionListener {
    boolean isFocusableColumn(int columnIndex) {
      TableColumn column = table.getColumnModel().getColumn(columnIndex);

      switch (column.getModelIndex() + 1) {
      case ChainsawColumns.INDEX_LEVEL_COL_NAME:
      case ChainsawColumns.INDEX_THREAD_COL_NAME:
      case ChainsawColumns.INDEX_LOGGER_COL_NAME:

        //			TODO ensure these columns are refine focus filters
        //			case ChainsawColumns.INDEX_CLASS_COL_NAME:
        //			case ChainsawColumns.INDEX_FILE_COL_NAME:
        //			case ChainsawColumns.INDEX_METHOD_COL_NAME:
        return true;

      default:
        return false;
      }
    }

    public void mouseMoved(MouseEvent e) {
      //        LogLog.debug(e.toString());
      int col = table.columnAtPoint(e.getPoint());

      //    TODO This is a Bug or something, but InputEvent.CTRL_DOWN_MASK only works
      // in JDK 1.4.2 when the LoggerTreePanel is open, if it is closed, it doesn't work... CTRL_DOWN_MASK
      // is ok though... Strange. Copied the mask from 1.4.2 here
      if (
        ((e.getModifiers() & (1 << 7)) > 0)
          || (((e.getModifiers() & InputEvent.CTRL_MASK) > 0)
          && isFocusableColumn(col))) {
        table.setCursor(ChainsawColumns.CURSOR_FOCUS_ON);
      } else {
        //          LogLog.debug("MouseMoved,  ((e.getModifiers() & InputEvent.CTRL_MASK) > 0)=" +  ((e.getModifiers() & InputEvent.CTRL_MASK) > 0) + ", isFocusableColumn(col)=" + isFocusableColumn(col));
        //        LogLog.debug(e.toString());
        table.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      }
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent e) {
      if (
        (e.getClickCount() > 1)
          && ((e.getModifiers() & InputEvent.CTRL_MASK) > 0)) {
        int row = table.rowAtPoint(e.getPoint());
        int col = table.columnAtPoint(e.getPoint());
        TableColumn column = table.getColumnModel().getColumn(col);
        Object value = getModel().getValueAt(row, column.getModelIndex());

        if (isFocusableColumn(col)) {
          LogLog.debug(
            "Wanted to focus on Column " + col + ", value=" + value);
          focusOnColumnValue(column.getModelIndex() + 1, value);
        }
      }
    }

    public void mouseDragged(MouseEvent e) {
    }
  }

  /**
   * This class provides a Sub menu so Users can Focus on specific elements of a LoggingEvent.
   *
   * The Focus On Logger action is subtle different, it is not visible inside this menu, but is used in an outter menu (Logger focus
   * is not a real Refinement filter), but it still benefits from being inside this class so that it can change it's display
   * based on the current event's Logger name.  Not ideal, but there you go.
   *
   * @author Paul Smith
   */
  private class FocusOnMenu extends JMenu {
    private Action focusOnLoggerAction =
      new AbstractAction("...logger...") {
        public void actionPerformed(ActionEvent e) {
          if (event != null) {
            focusOnColumnValue(
              ChainsawColumns.INDEX_LOGGER_COL_NAME, event.getLoggerName());
          }
        }
      };

    private Action focusOnThreadAction =
      new FocusOnAction(ChainsawColumns.INDEX_THREAD_COL_NAME);
    private Action focusOnLevelAction =
      new FocusOnAction(ChainsawColumns.INDEX_LEVEL_COL_NAME);
    private Action[] allActions =
      new Action[] { focusOnLoggerAction, focusOnThreadAction, focusOnLevelAction };
    private LoggingEvent event;

    private FocusOnMenu() {
      super("Refine focus on...");
      setIcon(new ImageIcon(ChainsawIcons.WINDOW_ICON));
      add(focusOnThreadAction);
      add(focusOnLevelAction);

      //      TODO add the other refinement focus stuff
      focusOnLoggerAction.putValue(Action.SMALL_ICON, getIcon());
    }

    private void removeFocus() {
      setEvent(null);

      ruleMediator.setRefinementRule(null);
    }

    private void setEvent(LoggingEvent event) {
      this.event = event;

      boolean enabled = event != null;
      setEnabled(enabled);

      for (int i = 0; i < allActions.length; i++) {
        allActions[i].setEnabled(enabled);
      }

      focusOnLoggerAction.putValue(
        Action.NAME,
        (event == null) ? "Focus on logger..."
                        : ("Focus on logger '" + event.getLoggerName() + "'"));
      focusOnThreadAction.putValue(
        Action.NAME,
        (event == null) ? "Thread..." : ("Thread '" + event.getThreadName()
        + "'"));

      focusOnLevelAction.putValue(
        Action.NAME,
        (event == null) ? "Level..." : ("Level '" + event.getLevel() + "'"));
    }

    private class FocusOnAction extends AbstractAction {
      private int column;
      private String columnName;

      private FocusOnAction(int column) {
        this.column = column;
        this.columnName = ChainsawColumns.getColumnName(this.column);
      }

      public void actionPerformed(ActionEvent e) {
        Object value =
          (event == null) ? null
                          : LoggingEventFieldResolver.getInstance().getValue(
            columnName, event);
        focusOnColumnValue(column, value);
      }
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

  //if columnmoved or columnremoved callback received, re-apply table's sort index based
  //sort column name
  class ChainsawTableColumnModelListener implements TableColumnModelListener {
    private JSortTable table;

    public ChainsawTableColumnModelListener(JSortTable table) {
      this.table = table;
    }

    public void columnAdded(TableColumnModelEvent e) {
      //      LogLog.debug("Detected columnAdded" + e);
      TableColumnModel columnModel = (TableColumnModel) e.getSource();
      Enumeration enum = table.getColumnModel().getColumns();

      while (enum.hasMoreElements()) {
        TableColumn column = (TableColumn) enum.nextElement();

        if (
          (column.getModelIndex() + 1) == ChainsawColumns.INDEX_THROWABLE_COL_NAME) {
          column.setCellEditor(throwableRenderPanel);
        }
      }
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

  /**
   * Thread that periodically checks if the selected row has changed, and if
   * it was, updates the Detail Panel with the detailed Logging information
   */
  class DetailPaneUpdater implements PropertyChangeListener {
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
      updateDetailPane(false);
    }

    private void updateDetailPane(boolean force) {
      String text = null;

      /**
       * Don't bother doing anything if it's not visible
       */
      if (!pane.isVisible()) {
        return;
      }

      if ((selectedRow != lastRow) || force) {
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

    /* (non-Javadoc)
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent arg0) {
      SwingUtilities.invokeLater(
        new Runnable() {
          public void run() {
            updateDetailPane(true);
          }
        });
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
}
