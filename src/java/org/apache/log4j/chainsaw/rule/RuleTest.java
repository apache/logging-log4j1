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
import org.apache.log4j.chainsaw.LoggingEventFieldResolver;
import org.apache.log4j.spi.LoggingEvent;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
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

  public RuleTest(String booleanPostFixExpression, String inFixExpression) {
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    getContentPane().setLayout(new BorderLayout());

    final LoggingEventFieldResolver resolver =
      LoggingEventFieldResolver.getInstance();

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

    JPanel fieldPanel = new JPanel(new GridLayout(5, 1));

    fieldPanel.add(
      new JLabel("Enter infix expression to convert to postfix: "));

    final JTextField inFixTextField = new JTextField(inFixExpression);
    fieldPanel.add(inFixTextField);

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
        "level deb ~=  BLAH test ==  ||  logger logger[1-3] like MDC.entry1 234 >= ||  && ",
        "( ( level ~= deb ) || ( BLAH == test ) ) && ( logger like logger[1-3] || MDC.entry1 >= 234 )");
    test.pack();
    test.setVisible(true);
  }
}
