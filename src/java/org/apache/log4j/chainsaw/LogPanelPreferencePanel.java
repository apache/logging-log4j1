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
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
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
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.log4j.chainsaw.icons.ChainsawIcons;
import org.apache.log4j.helpers.LogLog;


/**
 * GUI panel used to manipulate the PreferenceModel for a Log Panel
 *
 * @author Paul Smith
 */
public class LogPanelPreferencePanel extends JPanel {
	/**
	 *
	 */
	private class ModifiableListModel extends DefaultListModel {
		public void fireContentsChanged(){
			fireContentsChanged(this,0, this.size());
		}

	}
  private final LogPanelPreferenceModel committedPreferenceModel;
  private final JLabel titleLabel = new JLabel("Selected Pref Panel");
  private final JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
  private final JPanel selectedPrefPanel = new JPanel(new BorderLayout(0, 3));
  private final LogPanelPreferenceModel uncommitedPreferenceModel =
    new LogPanelPreferenceModel();
  private ActionListener okCancelListener;
  private Component currentlyDisplayedPanel = null;

  public LogPanelPreferencePanel(LogPanelPreferenceModel model) {
    this.committedPreferenceModel = model;
    initComponents();
  }

  /**
   * Setup and layout for the components
   */
  private void initComponents() {
    //		setBorder(BorderFactory.createLineBorder(Color.red));
    setLayout(new BorderLayout(5, 5));
    setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

    final JTree prefTree = new JTree(createTreeModel());
    prefTree.setRootVisible(false);

    DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
    renderer.setLeafIcon(ChainsawIcons.ICON_PREFERENCES);
    prefTree.setCellRenderer(renderer);

    final JScrollPane treeScroll = new JScrollPane(prefTree);

    treeScroll.setPreferredSize(new Dimension(200, 240));

    titleLabel.setFont(titleLabel.getFont().deriveFont(16.0f));
    titleLabel.setBorder(BorderFactory.createEtchedBorder());
    titleLabel.setBackground(Color.white);
    titleLabel.setOpaque(true);

    selectedPrefPanel.add(titleLabel, BorderLayout.NORTH);

    mainPanel.add(treeScroll, BorderLayout.WEST);
    mainPanel.add(selectedPrefPanel, BorderLayout.CENTER);

    add(mainPanel, BorderLayout.CENTER);

    JButton okButton = new JButton("OK");

    okButton.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          committedPreferenceModel.apply(uncommitedPreferenceModel);
          hidePanel();
        }
      });

    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          hidePanel();
        }
      });

    Box buttonBox = Box.createHorizontalBox();
    buttonBox.add(Box.createHorizontalGlue());
    buttonBox.add(okButton);
    buttonBox.add(Box.createHorizontalStrut(5));
    buttonBox.add(cancelButton);

    add(buttonBox, BorderLayout.SOUTH);

    DefaultTreeSelectionModel treeSelectionModel =
      new DefaultTreeSelectionModel();
    treeSelectionModel.setSelectionMode(
      TreeSelectionModel.SINGLE_TREE_SELECTION);
    prefTree.setSelectionModel(treeSelectionModel);
    prefTree.addTreeSelectionListener(
      new TreeSelectionListener() {
        public void valueChanged(TreeSelectionEvent e) {
          TreePath path = e.getNewLeadSelectionPath();
          DefaultMutableTreeNode node =
            (DefaultMutableTreeNode) path.getLastPathComponent();
          setDisplayedPrefPanel((JComponent) node.getUserObject());
        }
      });

    // ensure the first pref panel is selected and displayed
    DefaultMutableTreeNode root =
      (DefaultMutableTreeNode) prefTree.getModel().getRoot();
    DefaultMutableTreeNode firstNode =
      (DefaultMutableTreeNode) root.getFirstChild();
    prefTree.setSelectionPath(new TreePath(firstNode.getPath()));
  }

  /**
   * Ensures a specific panel is displayed in the spot where
   * preferences can be selected.
   *
  * @param panel
  */
  protected void setDisplayedPrefPanel(JComponent panel) {
    if (currentlyDisplayedPanel != null) {
      selectedPrefPanel.remove(currentlyDisplayedPanel);
    }

    selectedPrefPanel.add(panel, BorderLayout.CENTER);
    currentlyDisplayedPanel = panel;
    titleLabel.setText(panel.toString());
    selectedPrefPanel.revalidate();
    selectedPrefPanel.repaint();
  }

  public void setOkCancelActionListener(ActionListener l) {
    this.okCancelListener = l;
  }

  public void hidePanel() {
    if (okCancelListener != null) {
      okCancelListener.actionPerformed(null);
    }
  }

  /**
   * Ensures this panels DISPLAYED model is in sync with
   * the model initially passed to the constructor.
   *
   */
  public void updateModel() {
    this.uncommitedPreferenceModel.apply(committedPreferenceModel);
  }

  private TreeModel createTreeModel() {
    final DefaultMutableTreeNode rootNode =
      new DefaultMutableTreeNode("Preferences");
    DefaultTreeModel model = new DefaultTreeModel(rootNode);

    DefaultMutableTreeNode formatting =
      new DefaultMutableTreeNode(new FormattingPanel());

    DefaultMutableTreeNode columns =
      new DefaultMutableTreeNode(new ColumnSelectorPanel());

    rootNode.add(formatting);
    rootNode.add(columns);

    return model;
  }

  /**
   * @return
   */
  private LogPanelPreferenceModel getModel() {
    return uncommitedPreferenceModel;
  }

  public static void main(String[] args) {
    JFrame f = new JFrame("Preferences Panel Test Bed");
    LogPanelPreferenceModel model = new LogPanelPreferenceModel();
    LogPanelPreferencePanel panel = new LogPanelPreferencePanel(model);
    f.getContentPane().add(panel);
    
    model.addPropertyChangeListener(new PropertyChangeListener(){

		public void propertyChange(PropertyChangeEvent evt) {
			LogLog.warn(evt.toString());
			
		}});
    panel.setOkCancelActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          System.exit(1);
        }
      });

    f.setSize(640, 480);
    f.show();
  }

  /**
   * All of the Preferences panels used in this class extend from
   * this, it is used to provide standard L&F required by all.
   * @author Paul Smith
   *
   */
  private static class BasicPrefPanel extends JPanel {
    private BasicPrefPanel() {
      //    	setBorder(BorderFactory.createLineBorder(Color.red));
    }
  }

  /**
   * Provides preference gui's for all the Formatting options
   * available for the columns etc.
   */
  private class FormattingPanel extends BasicPrefPanel {
    private FormattingPanel() {
      super();
      this.initComponents();
    }

    private void initComponents() {
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

      JPanel dateFormatPanel = new JPanel();
      dateFormatPanel.setBorder(
        BorderFactory.createTitledBorder(
          BorderFactory.createEtchedBorder(), "Timestamp"));
      dateFormatPanel.setLayout(
        new BoxLayout(dateFormatPanel, BoxLayout.Y_AXIS));

      ButtonGroup bgDateFormat = new ButtonGroup();
      final JRadioButton rdISO =
        new JRadioButton(
          "<html><b>Fast</b> ISO 8601 format (yyyy-MM-dd HH:mm:ss)</html>");
      rdISO.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            getModel().setDateFormatPattern("ISO8601");
          }
        });
      rdISO.setSelected(getModel().isUseISO8601Format());
      getModel().addPropertyChangeListener(
        "dateFormatPattern",
        new PropertyChangeListener() {
          public void propertyChange(PropertyChangeEvent evt) {
            rdISO.setSelected(getModel().isUseISO8601Format());
          }
        });
      bgDateFormat.add(rdISO);
      dateFormatPanel.add(rdISO);

      for (
        Iterator iter = LogPanelPreferenceModel.DATE_FORMATS.iterator();
          iter.hasNext();) {
        final String format = (String) iter.next();
        final JRadioButton rdFormat = new JRadioButton(format);
        bgDateFormat.add(rdFormat);
        rdFormat.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              getModel().setDateFormatPattern(format);
            }
          });
        getModel().addPropertyChangeListener(
          "dateFormatPattern",
          new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
              rdFormat.setSelected(
                getModel().getDateFormatPattern().equals(format));
            }
          });
        dateFormatPanel.add(rdFormat);
      }

      add(dateFormatPanel);

      JPanel levelFormatPanel = new JPanel();
      levelFormatPanel.setLayout(
        new BoxLayout(levelFormatPanel, BoxLayout.Y_AXIS));
      levelFormatPanel.setBorder(
        BorderFactory.createTitledBorder(
          BorderFactory.createEtchedBorder(), "Level"));

      ButtonGroup bgLevel = new ButtonGroup();
      final JRadioButton rdLevelIcons = new JRadioButton("Icons");
      final JRadioButton rdLevelText = new JRadioButton("Text");
      bgLevel.add(rdLevelIcons);
      bgLevel.add(rdLevelText);

      ActionListener levelIconListener =
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            getModel().setLevelIcons(rdLevelIcons.isSelected());
          }
        };

      rdLevelIcons.addActionListener(levelIconListener);
      rdLevelText.addActionListener(levelIconListener);

      rdLevelIcons.setSelected(getModel().isLevelIcons());

      levelFormatPanel.add(rdLevelIcons);
      levelFormatPanel.add(rdLevelText);

      add(levelFormatPanel);
      add(Box.createVerticalGlue());
    }

    public String toString() {
      return "Formatting";
    }
  }

	/**
	 * A ListCellRenderer that display a check box if the value
	 * has been "checked".
	 * 
	 * Borrowed heavily from the excellent book "Swing, 2nd Edition" by
	 * Matthew Robinson  & Pavel Vorobiev.
	 * 
	 * @author Paul Smith
	 *
	 */
  public abstract class CheckListCellRenderer extends JCheckBox
    implements ListCellRenderer {
    private final Border noFocusBorder =
      BorderFactory.createEmptyBorder(1, 1, 1, 1);

    /**
     *
     */
    public CheckListCellRenderer() {
      super();
      setOpaque(true);
      setBorder(noFocusBorder);
    }

    /* (non-Javadoc)
     * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
     */
    public Component getListCellRendererComponent(
      JList list, Object value, int index, boolean isSelected,
      boolean cellHasFocus) {
      setText(value.toString());
      setBackground(
        isSelected ? list.getSelectionBackground() : list.getBackground());
      setForeground(
        isSelected ? list.getSelectionForeground() : list.getForeground());
      setFont(list.getFont());
      setBorder(
        cellHasFocus ? UIManager.getBorder("List.focusCellHighlightBorder")
                     : noFocusBorder);

      setSelected(isSelected(value));
      return this;
    }

	/**
	 * @param value
	 * @return
	 */
	protected abstract boolean isSelected(Object value);
  }

	/**
	 * Allows the user to choose which columns to display.
	 * 
	 * @author Paul Smith
	 *
	 */
  public class ColumnSelectorPanel extends BasicPrefPanel {
    ColumnSelectorPanel() {
      initComponents();
    }

    private void initComponents() {
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

      Box columnBox = new Box(BoxLayout.Y_AXIS);

      //		columnBox.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Displayed Columns"));
      final JList columnList = new JList();
      columnList.setVisibleRowCount(10);

      final ModifiableListModel listModel = new ModifiableListModel();

      for (
        Iterator iter = ChainsawColumns.getColumnsNames().iterator();
          iter.hasNext();) {
        String name = (String) iter.next();
        listModel.addElement(name);
      }

      columnList.setModel(listModel);

      CheckListCellRenderer cellRenderer = new CheckListCellRenderer(){

		protected boolean isSelected(Object value) {
			return LogPanelPreferencePanel.this.getModel().isColumnVisible(value.toString());
		}};
		
	  getModel().addPropertyChangeListener("visibleColumns", new PropertyChangeListener(){

		public void propertyChange(PropertyChangeEvent evt) {
			listModel.fireContentsChanged();
			
		}});
	   
	  columnList.addMouseListener(new MouseAdapter(){
	  	public void mouseClicked(MouseEvent e){
	  		if(e.getClickCount()>1 && (e.getModifiers() & MouseEvent.BUTTON1_MASK)>0){
	  			int i = columnList.locationToIndex(e.getPoint());
	  			if(i>=0){
		  			Object column = listModel.get(i);
		  			getModel().toggleColumn(column.toString());
	  			}
	  		}else {
	  		}
	  	}
	  });
      columnList.setCellRenderer(cellRenderer);
      columnBox.add(new JScrollPane(columnList));

      add(columnBox);
      add(Box.createVerticalGlue());
    }

    public String toString() {
      return "Columns";
    }
  }
}
