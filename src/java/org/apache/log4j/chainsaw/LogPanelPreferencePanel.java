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

import org.apache.log4j.helpers.LogLog;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;


/**
 * GUI panel used to manipulate the PreferenceModel for a Log Panel
 *
 * @author Paul Smith
 */
public class LogPanelPreferencePanel extends AbstractPreferencePanel
{
  //~ Instance fields =========================================================

  private final LogPanelPreferenceModel committedPreferenceModel;
  private JTextField loggerPrecision = new JTextField(5);
  private final LogPanelPreferenceModel uncommittedPreferenceModel =
    new LogPanelPreferenceModel();

  //~ Constructors ============================================================

  public LogPanelPreferencePanel(LogPanelPreferenceModel model)
  {
    this.committedPreferenceModel = model;
    initComponents();

    getOkButton().addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          uncommittedPreferenceModel.setLoggerPrecision(
            loggerPrecision.getText());
          committedPreferenceModel.apply(uncommittedPreferenceModel);
          hidePanel();
        }
      });

    getCancelButton().addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          hidePanel();
        }
      });
  }

  //~ Methods =================================================================

  /**
   * DOCUMENT ME!
   *
   * @param args DOCUMENT ME!
   */
  public static void main(String[] args)
  {
    JFrame f = new JFrame("Preferences Panel Test Bed");
    LogPanelPreferenceModel model = new LogPanelPreferenceModel();
    LogPanelPreferencePanel panel = new LogPanelPreferencePanel(model);
    f.getContentPane().add(panel);

    model.addPropertyChangeListener(new PropertyChangeListener()
      {
        public void propertyChange(PropertyChangeEvent evt)
        {
          LogLog.warn(evt.toString());
        }
      });
    panel.setOkCancelActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          System.exit(1);
        }
      });

    f.setSize(640, 480);
    f.show();
  }

  /**
   * Ensures this panels DISPLAYED model is in sync with
   * the model initially passed to the constructor.
   *
   */
  public void updateModel()
  {
    this.uncommittedPreferenceModel.apply(committedPreferenceModel);
  }

  protected TreeModel createTreeModel()
  {
    final DefaultMutableTreeNode rootNode =
      new DefaultMutableTreeNode("Preferences");
    DefaultTreeModel model = new DefaultTreeModel(rootNode);

    DefaultMutableTreeNode visuals =
      new DefaultMutableTreeNode(new VisualsPrefPanel());
    DefaultMutableTreeNode formatting =
      new DefaultMutableTreeNode(new FormattingPanel());
    DefaultMutableTreeNode columns =
      new DefaultMutableTreeNode(new ColumnSelectorPanel());

    rootNode.add(visuals);
    rootNode.add(formatting);
    rootNode.add(columns);

    return model;
  }

  /**
   * DOCUMENT ME!
   *
   * @return
   */
  private LogPanelPreferenceModel getModel()
  {
    return uncommittedPreferenceModel;
  }

  //~ Inner Classes ===========================================================

  /**
   * Allows the user to choose which columns to display.
   *
   * @author Paul Smith
   *
   */
  public class ColumnSelectorPanel extends BasicPrefPanel
  {
    //~ Constructors ==========================================================

    ColumnSelectorPanel()
    {
      super("Columns");
      initComponents();
    }

    //~ Methods ===============================================================

    private void initComponents()
    {
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

      Box columnBox = new Box(BoxLayout.Y_AXIS);

      //		columnBox.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Displayed Columns"));
      final JList columnList = new JList();
      columnList.setVisibleRowCount(10);

      final ModifiableListModel listModel = new ModifiableListModel();

      for (
        Iterator iter = ChainsawColumns.getColumnsNames().iterator();
          iter.hasNext();)
      {
        String name = (String) iter.next();
        listModel.addElement(name);
      }

      columnList.setModel(listModel);

      CheckListCellRenderer cellRenderer = new CheckListCellRenderer()
        {
          protected boolean isSelected(Object value)
          {
            return LogPanelPreferencePanel.this.getModel().isColumnVisible(
              value.toString());
          }
        };

      getModel().addPropertyChangeListener(
        "visibleColumns", new PropertyChangeListener()
        {
          public void propertyChange(PropertyChangeEvent evt)
          {
            listModel.fireContentsChanged();
          }
        });

      columnList.addMouseListener(new MouseAdapter()
        {
          public void mouseClicked(MouseEvent e)
          {
            if (
              (e.getClickCount() > 1)
                && ((e.getModifiers() & MouseEvent.BUTTON1_MASK) > 0))
            {
              int i = columnList.locationToIndex(e.getPoint());

              if (i >= 0)
              {
                Object column = listModel.get(i);
                getModel().toggleColumn(column.toString());
              }
            }
            else
            {
            }
          }
        });
      columnList.setCellRenderer(cellRenderer);
      columnBox.add(new JScrollPane(columnList));

      add(columnBox);
      add(Box.createVerticalGlue());
    }
  }

  /**
   * Provides preference gui's for all the Formatting options
   * available for the columns etc.
   */
  private class FormattingPanel extends BasicPrefPanel
  {
    //~ Instance fields =======================================================

    private JTextField customFormatText = new JTextField("", 10);
    private JRadioButton rdCustom = new JRadioButton("Custom Format");
    private final JRadioButton rdISO =
      new JRadioButton(
        "<html><b>Fast</b> ISO 8601 format (yyyy-MM-dd HH:mm:ss)</html>");
    private final JRadioButton rdLevelIcons = new JRadioButton("Icons");
    private final JRadioButton rdLevelText = new JRadioButton("Text");

    //~ Constructors ==========================================================

    private FormattingPanel()
    {
      super("Formatting");
      this.initComponents();
      setupListeners();
    }

    //~ Methods ===============================================================

    private void initComponents()
    {
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

      JPanel dateFormatPanel = new JPanel();
      dateFormatPanel.setBorder(
        BorderFactory.createTitledBorder(
          BorderFactory.createEtchedBorder(), "Timestamp"));
      dateFormatPanel.setLayout(
        new BoxLayout(dateFormatPanel, BoxLayout.Y_AXIS));
      dateFormatPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

      customFormatText.setPreferredSize(new Dimension(100, 20));
      customFormatText.setMaximumSize(customFormatText.getPreferredSize());
      customFormatText.setMinimumSize(customFormatText.getPreferredSize());
      customFormatText.setEnabled(false);

      rdCustom.setSelected(getModel().isCustomDateFormat());

      ButtonGroup bgDateFormat = new ButtonGroup();

      rdISO.setAlignmentX(0);
      rdISO.setSelected(getModel().isUseISO8601Format());

      bgDateFormat.add(rdISO);
      dateFormatPanel.add(rdISO);

      for (
        Iterator iter = LogPanelPreferenceModel.DATE_FORMATS.iterator();
          iter.hasNext();)
      {
        final String format = (String) iter.next();
        final JRadioButton rdFormat = new JRadioButton(format);
        rdFormat.setAlignmentX(0);

        bgDateFormat.add(rdFormat);
        rdFormat.addActionListener(new ActionListener()
          {
            public void actionPerformed(ActionEvent e)
            {
              getModel().setDateFormatPattern(format);
              customFormatText.setEnabled(rdCustom.isSelected());
            }
          });
        getModel().addPropertyChangeListener(
          "dateFormatPattern", new PropertyChangeListener()
          {
            public void propertyChange(PropertyChangeEvent evt)
            {
              rdFormat.setSelected(
                getModel().getDateFormatPattern().equals(format));
            }
          });

        dateFormatPanel.add(rdFormat);
      }

      // add a custom date format
      if (getModel().isCustomDateFormat())
      {
        customFormatText.setText(getModel().getDateFormatPattern());
        customFormatText.setEnabled(true);
      }

      rdCustom.setAlignmentX(0);
      bgDateFormat.add(rdCustom);

      Box customBox = Box.createHorizontalBox();

      //      Following does not work in JDK 1.3.1
      //      customBox.setAlignmentX(0);
      customBox.add(rdCustom);
      customBox.add(customFormatText);
      customBox.add(Box.createHorizontalGlue());
      dateFormatPanel.add(customBox);

      //      dateFormatPanel.add(Box.createVerticalGlue());
      add(dateFormatPanel);

      JPanel levelFormatPanel = new JPanel();
      levelFormatPanel.setLayout(
        new BoxLayout(levelFormatPanel, BoxLayout.Y_AXIS));
      levelFormatPanel.setBorder(
        BorderFactory.createTitledBorder(
          BorderFactory.createEtchedBorder(), "Level"));
      levelFormatPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

      ButtonGroup bgLevel = new ButtonGroup();
      bgLevel.add(rdLevelIcons);
      bgLevel.add(rdLevelText);

      rdLevelIcons.setSelected(getModel().isLevelIcons());

      levelFormatPanel.add(rdLevelIcons);
      levelFormatPanel.add(rdLevelText);

      add(levelFormatPanel);

      JPanel loggerFormatPanel = new JPanel();
      loggerFormatPanel.setLayout(
        new BoxLayout(loggerFormatPanel, BoxLayout.Y_AXIS));
      loggerFormatPanel.setBorder(
        BorderFactory.createTitledBorder(
          BorderFactory.createEtchedBorder(), "Logger"));
      loggerFormatPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

      final JLabel precisionLabel =
        new JLabel("Precision (package depth displayed)");
      final JLabel precisionLabel2 =
        new JLabel("leave blank to display full logger");

      loggerFormatPanel.add(precisionLabel);
      loggerFormatPanel.add(precisionLabel2);

      JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));

      p.add(loggerPrecision);
      loggerFormatPanel.add(p);

      add(loggerFormatPanel);

      add(Box.createVerticalGlue());
    }

    /**
     * DOCUMENT ME!
    */
    private void setupListeners()
    {
      rdCustom.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            customFormatText.setEnabled(rdCustom.isSelected());
            customFormatText.setText("");
            customFormatText.grabFocus();
          }
        });
      getModel().addPropertyChangeListener(
        "dateFormatPattern", new PropertyChangeListener()
        {
          public void propertyChange(PropertyChangeEvent evt)
          {
            /**
             * we need to make sure we are not reacting to the user typing, so only do this
             * if the text box is not the same as the model
             */
            if (
              getModel().isCustomDateFormat()
                && !customFormatText.getText().equals(
                  evt.getNewValue().toString()))
            {
              customFormatText.setText(getModel().getDateFormatPattern());
              rdCustom.setSelected(true);
              customFormatText.setEnabled(true);
            }
            else
            {
              rdCustom.setSelected(false);
            }
          }
        });

      rdISO.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            getModel().setDateFormatPattern("ISO8601");
            customFormatText.setEnabled(rdCustom.isSelected());
          }
        });
      getModel().addPropertyChangeListener(
        "dateFormatPattern", new PropertyChangeListener()
        {
          public void propertyChange(PropertyChangeEvent evt)
          {
            rdISO.setSelected(getModel().isUseISO8601Format());
          }
        });

      customFormatText.getDocument().addDocumentListener(
        new DocumentListener()
        {
          public void textChanged()
          {
            getModel().setDateFormatPattern(customFormatText.getText());
          }

          public void changedUpdate(DocumentEvent e)
          {
            textChanged();
          }

          public void insertUpdate(DocumentEvent e)
          {
            textChanged();
          }

          public void removeUpdate(DocumentEvent e)
          {
            textChanged();
          }
        });

      ActionListener levelIconListener = new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            getModel().setLevelIcons(rdLevelIcons.isSelected());
          }
        };

      rdLevelIcons.addActionListener(levelIconListener);
      rdLevelText.addActionListener(levelIconListener);

      getModel().addPropertyChangeListener(
        "levelIcons", new PropertyChangeListener()
        {
          public void propertyChange(PropertyChangeEvent evt)
          {
            boolean value = ((Boolean) evt.getNewValue()).booleanValue();
            rdLevelIcons.setSelected(value);
            rdLevelText.setSelected(!value);
          }
        });
    }
  }

  /**
   * DOCUMENT ME!
   *
   * @author $author$
   * @version $Revision$, $Date$
   *
   * @author psmith
   *
   */
  private class VisualsPrefPanel extends BasicPrefPanel
  {
    //~ Instance fields =======================================================

    private final JCheckBox detailPanelVisible =
      new JCheckBox("Show Event Detail panel");

    private final JCheckBox loggerTreePanel =
      new JCheckBox("Show Logger Tree panel");
    private final JCheckBox scrollToBottom =
      new JCheckBox("Scroll to bottom (view tracks with new events)");
    private final JCheckBox toolTips =
      new JCheckBox("Show Event Detail Tooltips");

    //~ Constructors ==========================================================

    /**
     * Creates a new VisualsPrefPanel object.
    */
    private VisualsPrefPanel()
    {
      super("Visuals");
      initPanelComponents();
      setupListeners();
    }

    //~ Methods ===============================================================

    /**
     * DOCUMENT ME!
    */
    private void initPanelComponents()
    {
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

      add(toolTips);
      add(detailPanelVisible);
      add(loggerTreePanel);
      add(scrollToBottom);

      toolTips.setSelected(getModel().isToolTips());
      detailPanelVisible.setSelected(getModel().isDetailPaneVisible());
      loggerTreePanel.setSelected(getModel().isLogTreePanelVisible());
    }

    /**
     * DOCUMENT ME!
    */
    private void setupListeners()
    {
      toolTips.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            getModel().setToolTips(toolTips.isSelected());
          }
        });
      getModel().addPropertyChangeListener(
        "toolTips", new PropertyChangeListener()
        {
          public void propertyChange(PropertyChangeEvent evt)
          {
            boolean value = ((Boolean) evt.getNewValue()).booleanValue();
            toolTips.setSelected(value);
          }
        });

      detailPanelVisible.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            getModel().setDetailPaneVisible(detailPanelVisible.isSelected());
          }
        });

      getModel().addPropertyChangeListener(
        "detailPaneVisible", new PropertyChangeListener()
        {
          public void propertyChange(PropertyChangeEvent evt)
          {
            boolean value = ((Boolean) evt.getNewValue()).booleanValue();
            detailPanelVisible.setSelected(value);
          }
        });

      scrollToBottom.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            getModel().setScrollToBottom(scrollToBottom.isSelected());
          }
        });

      getModel().addPropertyChangeListener(
        "scrollToBottom", new PropertyChangeListener()
        {
          public void propertyChange(PropertyChangeEvent evt)
          {
            boolean value = ((Boolean) evt.getNewValue()).booleanValue();
            scrollToBottom.setSelected(value);
          }
        });

      loggerTreePanel.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            getModel().setLogTreePanelVisible(loggerTreePanel.isSelected());
          }
        });

      getModel().addPropertyChangeListener(
        "logTreePanelVisible", new PropertyChangeListener()
        {
          public void propertyChange(PropertyChangeEvent evt)
          {
            boolean value = ((Boolean) evt.getNewValue()).booleanValue();
            loggerTreePanel.setSelected(value);
          }
        });
    }
  }
}
