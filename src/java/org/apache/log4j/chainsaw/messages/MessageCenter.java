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

package org.apache.log4j.chainsaw.messages;

import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.TTCCLayout;
import org.apache.log4j.chainsaw.ChainsawConstants;
import org.apache.log4j.chainsaw.PopupListener;
import org.apache.log4j.chainsaw.icons.ChainsawIcons;
import org.apache.log4j.spi.LoggingEvent;

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
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;


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
  private boolean visible;
  private PropertyChangeSupport propertySupport =
    new PropertyChangeSupport(this);
  private JFrame window = new JFrame("Message Center");
  private JScrollPane pane = new JScrollPane(messageList);
  private final JToolBar toolbar = new JToolBar();
  private JPopupMenu popupMenu = new JPopupMenu();
  private PopupListener popupListener = new PopupListener(popupMenu);
  private Action clearAction;

  private MessageCenter() {
    setupActions();
    setupFrame();
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
    JButton clearButton = new JButton(clearAction);
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
      "visible",
      new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
          boolean value = ((Boolean) evt.getNewValue()).booleanValue();
          window.setVisible(value);
        }
      });

    propertySupport.addPropertyChangeListener(
      "layout",
      new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
          Layout layout = (Layout) evt.getNewValue();
          messageList.setCellRenderer(new LayoutListCellRenderer(layout));
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
    logger.setLevel(Level.DEBUG);
  }

  private void setupFrame() {
    window.getContentPane().setLayout(new BorderLayout());

    messageList.setModel(appender.getModel());
    messageList.setCellRenderer(listCellRenderer);

    window.getContentPane().add(pane, BorderLayout.CENTER);
    window.getContentPane().add(toolbar, BorderLayout.NORTH);

    window.setSize(480, 240);
    window.setIconImage(new ImageIcon(ChainsawIcons.WINDOW_ICON).getImage());
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

  public void setVisible(boolean vis) {
    boolean oldValue = this.visible;
    this.visible = vis;
    propertySupport.firePropertyChange("visible", oldValue, this.visible);
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
   * @return Returns the visible.
   */
  public final boolean isVisible() {
    return visible;
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
