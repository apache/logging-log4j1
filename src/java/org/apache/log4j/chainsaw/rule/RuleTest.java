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

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.chainsaw.LoggingEventFieldResolver;
import org.apache.log4j.spi.LoggingEvent;


public class RuleTest extends JFrame {
  /**
   * Simple infix/postfix conversion and evaluation...work in progress..a ui test tool to work through expression building issues.
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
  public RuleTest(String booleanPostFixExpression, String inFixExpression) {
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    getContentPane().setLayout(new BorderLayout());

	final LoggingEventFieldResolver resolver = LoggingEventFieldResolver.getInstance();

    final List eventList = new ArrayList();
    
    eventList.add(new LoggingEvent("org.apache.log4j.chainsaw", Logger.getLogger("logger1"), System.currentTimeMillis(), Level.DEBUG, "message1", new Exception("test")));
	eventList.add(new LoggingEvent("org.apache.log4j.chainsaw", Logger.getLogger("logger2"), System.currentTimeMillis(), Level.DEBUG, "message2", new Exception("test2")));
	eventList.add(new LoggingEvent("org.apache.log4j.net", Logger.getLogger("logger3"), System.currentTimeMillis(), Level.DEBUG, "message3", new Exception("test3")));
	eventList.add(new LoggingEvent("org.apache.log4j.chainsaw", Logger.getLogger("logger4"), System.currentTimeMillis(), Level.WARN, "message4", new Exception("test4")));

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
		}
	  });

	JPanel resultsPanel = new JPanel(new BorderLayout());

	JButton resultsButton = new JButton("Evaluate postfix expression against collection of events: ");
	resultsPanel.add(resultsButton, BorderLayout.NORTH);

	final JTextArea results = new JTextArea(5, 50);
	resultsPanel.add(results, BorderLayout.CENTER);

	resultsButton.addActionListener(
	  new AbstractAction() {
		public void actionPerformed(ActionEvent evt) {
			results.setText("");
			Iterator iter = eventList.iterator();
			EvaluateBooleanPostFix evaluator = new EvaluateBooleanPostFix();
			while (iter.hasNext()) {
				LoggingEvent event = (LoggingEvent)iter.next();
				results.setText(results.getText() + (results.getText().length()==0?"":"\n") + "level: " + event.getLevel() + ", logger: " + event.getLoggerName() + " - result: " + evaluator.evaluate(inFixResult.getText(), event));
			}
		}
	  });

    getContentPane().add(fieldPanel, BorderLayout.NORTH);
    getContentPane().add(resultsPanel, BorderLayout.CENTER);
  }
  
  
  

  public static void main(String[] args) {
    RuleTest test =
      new RuleTest(
        "level debug ~= BLAH test == || logger logger1 == && ",
        "( ( level ~= debug ) || ( BLAH == test ) ) && logger  == logger1");
    test.pack();
    test.setVisible(true);
  }

  boolean convertToBoolean(String param) {
    boolean result = false;

    if (param == null) {
      return result;
    }

    try {
      result = Boolean.valueOf(param).booleanValue();
	  System.out.println("convert to boolean: " + param + "..result " + result);
    } catch (Exception e) {
      e.printStackTrace();
      return result;
    }

    return result;
  }

  abstract class BooleanOperator {
	//the evaluate method usually uses the 2nd item on the stack first
    abstract boolean evaluate(Stack stack);
  }

  abstract class EventOperator {
	//the evaluate method usually uses the 2nd item on the stack first
    abstract boolean evaluate(Stack stack, LoggingEvent event);
  }

  class AndOperator extends BooleanOperator {
    boolean evaluate(Stack stack) {
	  String firstParam = stack.pop().toString();
	  String secondParam = stack.pop().toString();
	  System.out.println(
		"and op " + firstParam + ".." + secondParam);
	  boolean result = (convertToBoolean(firstParam) && convertToBoolean(secondParam));
	  System.out.println("result is " + result); 
      return result;
    }
  }

  class OrOperator extends BooleanOperator {
    boolean evaluate(Stack stack) {
		String firstParam = stack.pop().toString();
		String secondParam = stack.pop().toString();
		System.out.println(
		  "or op " + firstParam + ".." + secondParam);
	    boolean result = (convertToBoolean(firstParam) || convertToBoolean(secondParam));
	    System.out.println("result is " + result); 
      return result;
    }
  }

  class PartialTextMatchOperator extends EventOperator {
	LoggingEventFieldResolver resolver = LoggingEventFieldResolver.getInstance();
    boolean evaluate(Stack stack, LoggingEvent event) {
		String firstParam = stack.pop().toString();
		String secondParam = resolver.getValue(stack.pop().toString(), event).toString();
		System.out.println(
		  "partial text match op " + firstParam + ".." + secondParam);
		boolean result = ((secondParam != null && firstParam != null) && 
      	(secondParam.toLowerCase().indexOf(firstParam.toLowerCase()) > -1)); 
		System.out.println("result is " + result);
      return result;
    }
  }

  class EqualsOperator extends EventOperator {
	LoggingEventFieldResolver resolver = LoggingEventFieldResolver.getInstance();
    boolean evaluate(Stack stack, LoggingEvent event) {
	  String firstParam = stack.pop().toString();
      String secondParam = resolver.getValue(stack.pop().toString(), event).toString();
	  System.out.println("equals op " + firstParam + ".." + secondParam);
	  boolean result = ((secondParam != null) && secondParam.equals(firstParam));
	  System.out.println("result is " + result); 
      return result;
    }
  }

  /**
   * Evaluate a boolean postfix expression.
   *
   */
  class EvaluateBooleanPostFix {
    private final Map booleanOperatorMap = new HashMap();
	private final Map eventOperatorMap = new HashMap();
    private final Stack stack = new Stack();
    String result = null;

    EvaluateBooleanPostFix() {
      booleanOperatorMap.put("&&", new AndOperator());
      booleanOperatorMap.put("||", new OrOperator());
      eventOperatorMap.put("==", new EqualsOperator());
      eventOperatorMap.put("~=", new PartialTextMatchOperator());
    }

    String evaluate(String expression, LoggingEvent event) {
      String result = null;
      Enumeration tokenizer = new StringTokenizer(expression);

      while (tokenizer.hasMoreElements()) {
        //examine each token
        String nextToken = ((String) tokenizer.nextElement());

        //if a symbol is found, pop 2 off the stack, evaluate and push the result 
        if (booleanOperatorMap.containsKey(nextToken)) {
          BooleanOperator op = (BooleanOperator) booleanOperatorMap.get(nextToken);
		  //the operator is responsible for popping the stack
          stack.push(Boolean.valueOf(op.evaluate(stack)));
        } else if (eventOperatorMap.containsKey(nextToken)) {
			EventOperator op = (EventOperator)eventOperatorMap.get(nextToken);
			stack.push(Boolean.valueOf(op.evaluate(stack, event)));
		} else { 
          //variables or constants are pushed onto the stack
          stack.push(nextToken);
        }
      }

      if (stack.size() > 0) {
        result = stack.pop().toString();
      } else {
        result = "ERRROR";
      }

      return result;
    }
  }

  /**
   * precedence: !, &, ^, |, &&, ||
   * Convert an infix expression to postfix.  Supports parentheses, ||, &&, == and ~=
   *
   */
  public class InFixToPostFix {
    private final Stack stack = new Stack();
    private final Map precedenceMap = new HashMap();
    private final List operators = new Vector();

    public InFixToPostFix() {
      //parentheses are treated as quasi-operators
      operators.add("(");
      operators.add(")");

      //boolean operators
      operators.add("==");
      operators.add("~=");
      operators.add("||");
      operators.add("&&");

      //boolean precedence
      precedenceMap.put("==", new Integer(2));
      precedenceMap.put("~=", new Integer(2));
      precedenceMap.put("||", new Integer(3));
      precedenceMap.put("&&", new Integer(4));
    }

    public String convert(String expression) {
      return infixToPostFix(expression);
    }

    boolean isOperand(String symbol) {
      return (!operators.contains(symbol));
    }

    boolean precedes(String symbol1, String symbol2) {
      if (!precedenceMap.keySet().contains(symbol1)) {
        return false;
      }

      if (!precedenceMap.keySet().contains(symbol2)) {
        return false;
      }

      int index1 = ((Integer) precedenceMap.get(symbol1)).intValue();
      int index2 = ((Integer) precedenceMap.get(symbol2)).intValue();

      boolean precedesResult = (index1 < index2);
      System.out.println(
        "SYMBOL1: " + symbol1 + "SYMBOL2: " + symbol2 + " index1: " + index1
        + " index2: " + index2 + " precedesresult: " + precedesResult);

      return precedesResult;
    }

    String infixToPostFix(String infixParam) {
      String SPACE = " ";
      StringBuffer postfix = new StringBuffer();
      StringTokenizer tokenizer = new StringTokenizer(infixParam);

      for (int i = 0, j = tokenizer.countTokens(); i < j; i++) {
        String token = tokenizer.nextToken();
        System.out.println("FOUND TOKEN " + token);

        if (isOperand(token)) {
          postfix.append(token + SPACE);
          System.out.println("OPERAND - APPENDING TO POSTFIX " + postfix);
        } else {
          System.out.println("OPERATOR - ITERATING THROUGH STACK");

          while (
            (!(stack.size() == 0))
              && (precedes(stack.peek().toString(), token))) {
            postfix.append(stack.pop().toString() + SPACE);
            System.out.println(
              "appending to postfix and popping from stack - postfix: "
              + postfix + "..stack: " + stack);
          }

          if ((!(stack.size() == 0)) && (")".equals(token))) {
            if ("(".equals(stack.peek())) {
              System.out.println(
                "found left paren - popping without adding to output - result: "
                + stack);
              stack.pop();
            } else {
              postfix.append(stack.pop().toString() + SPACE);
              System.out.println(
                "FOUND RIGHT PAREN - POPPING - result: " + stack);
            }
          } else {
            stack.push(token);
            System.out.println("NOT RIGHT PAREN - PUSHING - result: " + stack);
          }
        }
      }

      System.out.println("OUT OF TOKEN LOOP - remaining stack is " + stack);

      while (!(stack.size() == 0)) {
        if ("(".equals(stack.peek().toString())) {
          //pop off the stack but don't process
          stack.pop();
          System.out.println(
            "popping left paren off stack. stack is " + stack);
        } else {
          postfix.append(stack.pop().toString() + SPACE);
          System.out.println(
            "appending to postfix and popping from stack - postfix: "
            + postfix + "..stack: " + stack);
        }
      }

      System.out.println("RETURNING " + postfix);
      stack.clear();

      return postfix.toString();
    }
  }
}
