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

package org.apache.log4j.chainsaw.messages;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.TTCCLayout;
import org.apache.log4j.chainsaw.ChainsawConstants;
import org.apache.log4j.chainsaw.PopupListener;
import org.apache.log4j.chainsaw.SmallButton;
import org.apache.log4j.chainsaw.icons.ChainsawIcons;
import org.apache.log4j.spi.LoggingEvent;


/**
 * The MessageCenter is central place for all elements within Chainsaw to
 * notify the user of important information.
 *
 * This class uses log4j itself quite significantly.  All user message
 * are sent to this classes log4j Logger (org.apache.log4j.chainsaw.message.MessageCenter).
 *
 * To register a message with the user, you can use the addMessage(String) style methods on
 * this class, or just as easily, get a handle to this class' logger, and log
 * it as you would normally do.
 *
 * All events to this logger are trapped within a Custom appender (additivity
 * will be switched OFF), which stores the events in a ListModel.
 *
 * You can invoke the setVisible() method to display all the messages
 *
 * @author Paul Smith <psmith@apache.org>
 *
 */
public class MessageCenter {
  private static final MessageCenter instance = new MessageCenter();
  private final Logger logger = Logger.getLogger(MessageCenter.class);
  private Layout layout = new TTCCLayout();
  private final JList messageList = new JList();
  private final MessageCenterAppender appender = new MessageCenterAppender();
  private ListCellRenderer listCellRenderer =
    new LayoutListCellRenderer(layout);
  private PropertyChangeSupport propertySupport =
    new PropertyChangeSupport(this);
  private JScrollPane pane = new JScrollPane(messageList);
  private final JToolBar toolbar = new JToolBar();
  private JPopupMenu popupMenu = new JPopupMenu();
  private PopupListener popupListener = new PopupListener(popupMenu);
  private Action clearAction;
  private final JPanel componentPanel = new JPanel(new BorderLayout());

  private MessageCenter() {
    setupActions();
    setupComponentPanel();
    setupLogger();
    setupListeners();
    setupPopMenu();
    setupToolbar();
  }

  /**
   *
   */
  private void setupPopMenu() {
    popupMenu.add(clearAction);
  }

  /**
   *
   */
  private void setupToolbar() {
    JButton clearButton = new SmallButton(clearAction);
    clearButton.setText(null);
    toolbar.add(clearButton);

    toolbar.setFloatable(false);
  }

  private void setupActions() {
    clearAction =
      new AbstractAction("Clear") {
          public void actionPerformed(ActionEvent e) {
            appender.clearModel();
          }
        };
    clearAction.putValue(
      Action.SMALL_ICON, new ImageIcon(ChainsawIcons.DELETE));
  }

  private void setupListeners() {
    propertySupport.addPropertyChangeListener(
      "layout",
      new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
          Layout newLayout = (Layout) evt.getNewValue();
          messageList.setCellRenderer(new LayoutListCellRenderer(newLayout));
        }
      });
    messageList.addMouseListener(popupListener);

    appender.getModel().addListDataListener(
      new ListDataListener() {
        public void contentsChanged(ListDataEvent e) {
          updateActions();
        }

        public void intervalAdded(ListDataEvent e) {
          updateActions();
        }

        public void intervalRemoved(ListDataEvent e) {
          updateActions();
        }
      });
  }

  /**
   *
   */
  private void updateActions() {
    clearAction.putValue(
      "enabled",
      (appender.getModel().getSize() > 0) ? Boolean.TRUE : Boolean.FALSE);
  }

  private void setupLogger() {
    logger.addAppender(appender);
    logger.setAdditivity(false);
    logger.setLevel(Boolean.getBoolean("log4j.debug")?Level.DEBUG:Level.INFO);
  }

  private void setupComponentPanel() {
    messageList.setModel(appender.getModel());
    messageList.setCellRenderer(listCellRenderer);

    componentPanel.add(this.toolbar, BorderLayout.NORTH);
    componentPanel.add(this.pane, BorderLayout.CENTER);
  }
  
  public final JComponent getGUIComponent() {
    return componentPanel;
  }

  public ListModel getModel() {
    return messageList.getModel();
  }

  public static MessageCenter getInstance() {
    return instance;
  }

  public void addMessage(String message) {
    logger.info(message);
  }


  /**
   * @return Returns the layout used by the MessageCenter.
   */
  public final Layout getLayout() {
    return layout;
  }

  /**
   * @param layout Sets the layout to be used by the MessageCenter .
   */
  public final void setLayout(Layout layout) {
    Layout oldValue = this.layout;
    this.layout = layout;
    propertySupport.firePropertyChange("layout", oldValue, this.layout);
  }

  /**
   * Returns the logger that can be used to log
   * messages to display within the Message Center.
   * @return
   */
  public final Logger getLogger() {
    return this.logger;
  }

  /**
   * This class simply renders an event by delegating the effort to a
   * Log4j layout instance.
   * 
   * @author Paul Smith <psmith@apache.org>
   */
  private static class LayoutListCellRenderer extends DefaultListCellRenderer
    implements ListCellRenderer {
    private Layout layout;

    /**
     * @param layout
     */
    public LayoutListCellRenderer(Layout layout) {
      super();
      this.layout = layout;
    }

    /* (non-Javadoc)
     * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
     */
    public Component getListCellRendererComponent(
      JList list, Object value, int index, boolean isSelected,
      boolean cellHasFocus) {
      value = layout.format((LoggingEvent) value);

      Component c =
        super.getListCellRendererComponent(
          list, value, index, isSelected, cellHasFocus);
      c.setBackground(
        ((index % 2) == 0) ? ChainsawConstants.COLOR_EVEN_ROW
                           : ChainsawConstants.COLOR_ODD_ROW);

      return c;
    }
  }
}
