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

package org.apache.log4j.rule;

import org.apache.log4j.chainsaw.LoggingEventFieldResolver;
import org.apache.log4j.chainsaw.filter.FilterModel;

import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
 * A popup menu which assists in building expression rules.  Completes event keywords, operators and 
 * context if available.
 * 
 * @author Scott Deboy <sdeboy@apache.org>
 */
public class ExpressionRuleContext extends KeyAdapter {
  RuleFactory factory = RuleFactory.getInstance();
  LoggingEventFieldResolver resolver = LoggingEventFieldResolver.getInstance();
  JPopupMenu contextMenu = new JPopupMenu();
  JList list = new JList();
  FilterModel filterModel;
  JScrollPane scrollPane = new JScrollPane(list);
  final JTextField textField;
  private DefaultListModel fieldModel = new DefaultListModel();
  private DefaultListModel operatorModel = new DefaultListModel();

  public ExpressionRuleContext(
    final FilterModel filterModel, final JTextField textField) {
    this.filterModel = filterModel;
    this.textField = textField;
    fieldModel.addElement("LOGGER");
    fieldModel.addElement("LEVEL");
    fieldModel.addElement("CLASS");
    fieldModel.addElement("FILE");
    fieldModel.addElement("LINE");
    fieldModel.addElement("METHOD");
    fieldModel.addElement("MSG");
    fieldModel.addElement("NDC");
    fieldModel.addElement("EXCEPTION");
    fieldModel.addElement("TIMESTAMP");
    fieldModel.addElement("THREAD");
    fieldModel.addElement("MDC");
    fieldModel.addElement("PROP");

    operatorModel.addElement("&&");
    operatorModel.addElement("||");
    operatorModel.addElement("!");
    operatorModel.addElement("!=");
    operatorModel.addElement("==");
    operatorModel.addElement("~=");
    operatorModel.addElement("LIKE");
    operatorModel.addElement("EXISTS");
    operatorModel.addElement("<");
    operatorModel.addElement(">");
    operatorModel.addElement("<=");
    operatorModel.addElement(">=");

    //make long to avoid scrollbar 
    list.setVisibleRowCount(13);

    PopupListener popupListener = new PopupListener();
    textField.addMouseListener(popupListener);

    list.addKeyListener(
      new KeyAdapter() {
        public void keyPressed(KeyEvent e) {
          if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            updateField(list.getSelectedValue().toString());
            contextMenu.setVisible(false);
          }
        }
      });

    list.addMouseListener(
      new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
          if (e.getClickCount() == 2) {
            updateField(list.getSelectedValue().toString());
            contextMenu.setVisible(false);
          }
        }
      });

    contextMenu.insert(scrollPane, 0);
  }

  private void updateField(String value) {
    if (textField.getSelectedText() == null) {
      value = value + " ";
    }

    textField.replaceSelection(value);
  }

  public void keyPressed(KeyEvent e) {
    if (
      (e.getKeyCode() == KeyEvent.VK_SPACE)
        && (e.getModifiers() == KeyEvent.CTRL_MASK)) {
      displayContext();
    }
  }

  public void displayContext() {
    String lastField = getContextKey();

    if (lastField != null) {
      list.setModel(filterModel.getContainer().getModel(lastField));
      list.setSelectedIndex(0);

      Point p = textField.getCaret().getMagicCaretPosition();
      contextMenu.doLayout();
      contextMenu.show(textField, p.x, (p.y + (textField.getHeight() - 5)));
      list.requestFocus();
    } else {
      if (isOperatorContextValid()) {
        list.setModel(operatorModel);
        list.setSelectedIndex(0);

        Point p = textField.getCaret().getMagicCaretPosition();
        contextMenu.doLayout();
        contextMenu.show(textField, p.x, (p.y + (textField.getHeight() - 5)));
        list.requestFocus();
      } else if (isFieldContextValid()) {
        list.setModel(fieldModel);
        list.setSelectedIndex(0);

        Point p = textField.getCaret().getMagicCaretPosition();

        if (p == null) {
          p = new Point(
              textField.getLocation().x,
              (textField.getLocation().y - textField.getHeight() + 5));
        }
        contextMenu.doLayout();
        contextMenu.show(textField, p.x, (p.y + (textField.getHeight() - 5)));
        list.requestFocus();
      }
    }
  }

  private boolean isFieldContextValid() {
    String text = textField.getText();
    int currentPosition = textField.getSelectionStart();

    return ((currentPosition == 0)
    || (text.charAt(currentPosition - 1) == ' '));
  }

  private String getContextKey() {
    String field = getField();

    if (field == null) {
      field = getSubField();
    }

    return field;
  }

  private boolean isOperatorContextValid() {
    String text = textField.getText();

    int currentPosition = textField.getSelectionStart();

    if ((currentPosition < 1) || (text.charAt(currentPosition - 1) != ' ')) {
      return false;
    }

    int lastFieldPosition = text.lastIndexOf(" ", currentPosition - 1);

    if (lastFieldPosition == -1) {
      return false;
    }

    int lastFieldStartPosition =
      Math.max(0, text.lastIndexOf(" ", lastFieldPosition - 1));
    String field =
      text.substring(lastFieldStartPosition, lastFieldPosition).toUpperCase()
          .trim();

    if (field.startsWith("MDC.")) {
      return true;
    }

    if (resolver.isField(field)) {
      return true;
    }

    return false;
  }

  //returns the currently active field which can be used to display a context menu
  //the field returned is the left hand portion of an expression (for example, logger == )
  //logger is the field that is returned
  private String getField() {
    String text = textField.getText();

    int currentPosition = textField.getSelectionStart();

    if ((currentPosition < 1) || (text.charAt(currentPosition - 1) != ' ')) {
      return null;
    }

    int symbolPosition = text.lastIndexOf(" ", currentPosition - 1);

    if (symbolPosition < 0) {
      return null;
    }

    int lastFieldPosition = text.lastIndexOf(" ", symbolPosition - 1);

    if (lastFieldPosition < 0) {
      return null;
    }

    int lastFieldStartPosition =
      Math.max(0, text.lastIndexOf(" ", lastFieldPosition - 1));
    String lastSymbol =
      text.substring(lastFieldPosition + 1, symbolPosition).trim();

    String lastField =
      text.substring(lastFieldStartPosition, lastFieldPosition).trim();

    if (
      factory.isRule(lastSymbol)
        && filterModel.getContainer().modelExists(lastField)) {
      return lastField;
    }

    return null;
  }

  //subfields allow the key portion of a field to provide context menu support
  //and are available after the fieldname and a . (for example, MDC.)
  private String getSubField() {
    int currentPosition = textField.getSelectionStart();
    String text = textField.getText();

    if (text.substring(0, currentPosition).toUpperCase().endsWith("MDC.")) {
      return "MDC";
    }
    return null;
  }

  class PopupListener extends MouseAdapter {
    PopupListener() {
    }

    public void mousePressed(MouseEvent e) {
      checkPopup(e);
    }

    public void mouseReleased(MouseEvent e) {
      checkPopup(e);
    }

    private void checkPopup(MouseEvent e) {
      if (e.isPopupTrigger()) {
        displayContext();
      }
    }
  }
}
