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

package org.apache.log4j.chainsaw.rule;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.log4j.chainsaw.ChainsawConstants;
import org.apache.log4j.chainsaw.LoggingEventFieldResolver;
import org.apache.log4j.chainsaw.filter.FilterModel;
import org.apache.log4j.spi.LoggingEvent;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;


public class RuleTest extends JFrame {
  /**
   * UI for demonstrating infix/postfix conversion and expression rule evaluation...work in progress...
   *
   * Infix to postfix conversion routines and evaluation methods for boolean expressions.
   * See http://www.qiksearch.com/articles/cs/infix-postfix/
   * and http://www.spsu.edu/cs/faculty/bbrown/web_lectures/postfix/
   *
   * for more information.
   *
   * @author Scott Deboy <sdeboy@apache.org>
   *
   */
  Rule rule;
  FilterModel filterModel;

  public RuleTest(String inFixText) {
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    getContentPane().setLayout(new BorderLayout());

    LoggingEventFieldResolver resolver =
      LoggingEventFieldResolver.getInstance();

    filterModel = new FilterModel();

    final List eventList = new ArrayList();
    MDC.put("entry1", "123");
    eventList.add(
      new LoggingEvent(
        "org.apache.log4j.chainsaw", Logger.getLogger("logger1"),
        System.currentTimeMillis(), Level.DEBUG, "message1",
        new Exception("test")));
    MDC.put("entry2", "test1");
    eventList.add(
      new LoggingEvent(
        "org.apache.log4j.chainsaw", Logger.getLogger("logger2"),
        System.currentTimeMillis(), Level.DEBUG, "message2",
        new Exception("test2")));
    eventList.add(
      new LoggingEvent(
        "org.apache.log4j.net", Logger.getLogger("logger3"),
        System.currentTimeMillis(), Level.DEBUG, "message3",
        new Exception("test3")));

    MDC.put("test", "234");
    eventList.add(
      new LoggingEvent(
        "org.apache.log4j.chainsaw", Logger.getLogger("logger4"),
        System.currentTimeMillis(), Level.WARN, "message4",
        new Exception("test4")));

    Iterator iter = eventList.iterator();

    while (iter.hasNext()) {
      LoggingEvent event = (LoggingEvent) iter.next();
      filterModel.processNewLoggingEvent(
        ChainsawConstants.LOG4J_EVENT_TYPE, event);
    }

    JPanel fieldPanel = new JPanel(new GridLayout(5, 1));

    fieldPanel.add(
      new JLabel("Enter infix expression to convert to postfix: "));

    final JTextField inFixTextField = new JTextField(inFixText);
    fieldPanel.add(inFixTextField);

    ContextListener listener = new ContextListener(inFixTextField);
    inFixTextField.addKeyListener(listener);

    JButton inFixButton = new JButton("Convert InFix to PostFix");
    fieldPanel.add(inFixButton);

    JLabel resultsLabel = new JLabel("Results:");
    fieldPanel.add(resultsLabel);

    final JTextField inFixResult = new JTextField();
    fieldPanel.add(inFixResult);
    inFixButton.addActionListener(
      new AbstractAction() {
        public void actionPerformed(ActionEvent evt) {
          InFixToPostFix inFixConverter = new InFixToPostFix();
          inFixResult.setText(
            inFixConverter.convert(inFixTextField.getText()));
          rule = ExpressionRule.getRule(inFixResult.getText(), true);
        }
      });

    JPanel resultsPanel = new JPanel(new BorderLayout());

    JButton resultsButton =
      new JButton(
        "Evaluate postfix expression against collection of events: ");
    resultsPanel.add(resultsButton, BorderLayout.NORTH);

    final JTextArea results = new JTextArea(5, 50);
    resultsPanel.add(results, BorderLayout.CENTER);

    resultsButton.addActionListener(
      new AbstractAction() {
        public void actionPerformed(ActionEvent evt) {
          results.setText("");

          Iterator iter = eventList.iterator();

          while (iter.hasNext()) {
            LoggingEvent event = (LoggingEvent) iter.next();
            Iterator iter2 = event.getMDCKeySet().iterator();
            StringBuffer mdc = new StringBuffer();

            while (iter2.hasNext()) {
              String mdcKey = (String) iter2.next();
              mdc.append(mdcKey);
              mdc.append(":");
              mdc.append(event.getMDC(mdcKey));
            }

            results.setText(
              results.getText()
              + ((results.getText().length() == 0) ? "" : "\n") + "level: "
              + event.getLevel() + ", logger: " + event.getLoggerName()
              + ", MDC: " + mdc.toString() + " - result: "
              + rule.evaluate(event));
          }
        }
      });

    getContentPane().add(fieldPanel, BorderLayout.NORTH);
    getContentPane().add(resultsPanel, BorderLayout.CENTER);
  }

  private void setRule(Rule rule) {
    this.rule = rule;
  }

  public static void main(String[] args) {
    RuleTest test =
      new RuleTest(
        "( level ~= deb ) && ( logger like logger[1-2] || MDC.entry1 >= 234 )");
    test.pack();
    test.setVisible(true);
  }

  class ContextListener extends KeyAdapter {
    LoggingEventFieldResolver resolver =
      LoggingEventFieldResolver.getInstance();
    String lastField = null;
    JPopupMenu contextMenu = new JPopupMenu();
    JList list = new JList();
 
    JScrollPane scrollPane = new JScrollPane(list);
    final JTextField textField;
    private DefaultListModel fieldModel = new DefaultListModel();
    private DefaultListModel operatorModel = new DefaultListModel();

    public ContextListener(final JTextField textField) {
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
      operatorModel.addElement("<");
      operatorModel.addElement(">");
      operatorModel.addElement("<=");
      operatorModel.addElement(">=");

      //make as large as operator list to avoid narrow list scrollbar issues
      list.setVisibleRowCount(11);

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
      String text = textField.getText();
      int startPosition = textField.getSelectionStart();
      int endPosition = textField.getSelectionEnd();
      String spacer = "";

      if (startPosition == endPosition) {
        spacer = " ";
      }

      textField.setText(
        text.substring(0, startPosition) + value + spacer
        + text.substring(endPosition));
      textField.setCaretPosition(
        startPosition + value.length() + spacer.length());
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

        contextMenu.show(textField, p.x, (p.y + (textField.getHeight() - 5)));
        list.requestFocus();
      } else {
        if (isOperatorContextValid()) {
          list.setModel(operatorModel);
          list.setSelectedIndex(0);

          Point p = textField.getCaret().getMagicCaretPosition();
          contextMenu.show(
            textField, p.x, (p.y + (textField.getHeight() - 5)));
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

          contextMenu.show(
            textField, p.x, (p.y + (textField.getHeight() - 5)));
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
        RuleFactory.isRule(lastSymbol)
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
}
