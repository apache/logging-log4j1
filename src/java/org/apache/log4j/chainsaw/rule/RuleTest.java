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
import java.util.Enumeration;
import java.util.HashMap;
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
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

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
	private final Vector data = new Vector();
	private final Vector colnames = new Vector();
	
	public RuleTest(String booleanPostFixExpression, String inFixExpression) {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		JPanel fieldPanel = new JPanel(new GridLayout(8, 1));
		
		fieldPanel.add(
			new JLabel("Enter boolean postfix expression to evaluate: "));
		final JTextField booleanPostFixTextField = new JTextField(booleanPostFixExpression);
		fieldPanel.add(booleanPostFixTextField);
		JButton booleanPostFixButton = new JButton("Evaluate Boolean PostFix");
		fieldPanel.add(booleanPostFixButton);
		final JTextField booleanResult = new JTextField();
		fieldPanel.add(booleanResult);
		booleanPostFixButton.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				EvaluateBooleanPostFix booleanPostFixEvaluator = new EvaluateBooleanPostFix();
				booleanResult.setText(booleanPostFixEvaluator.evaluate(booleanPostFixTextField.getText()));
			}
		});


		fieldPanel.add(
			new JLabel("Enter infix expression to convert to postfix: "));
		final JTextField inFixTextField = new JTextField(inFixExpression);
		fieldPanel.add(inFixTextField);
		JButton inFixButton = new JButton("Convert InFix to PostFix");
		fieldPanel.add(inFixButton);
		final JTextField inFixResult = new JTextField();
		fieldPanel.add(inFixResult);
		inFixButton.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				InFixToPostFix inFixConverter = new InFixToPostFix();
				inFixResult.setText(inFixConverter.convert(inFixTextField.getText()));
			}
		});

		colnames.add("level");
		colnames.add("logger");
		colnames.add("message");
		colnames.add("result");		

		data.add(createEvent("DEBUG", "org.apache.log4j.chainsaw", "TEST MESSAGE 1"));
		data.add(createEvent("DEBUG", "test logger", "TEST MESSAGE 2"));
		data.add(createEvent("INFO", "org.apache.log4j.chainsaw", "TEST MESSAGE 3"));
		data.add(createEvent("INFO", "org.aache.log4j.chainsaw", "TEST MESSAGE 4"));
		data.add(createEvent("WARN", "test logger", "TEST MESSAGE 5"));
		data.add(createEvent("WARN", "test logger 2", "TEST MESSAGE 6"));
		data.add(createEvent("WARN", "test logger 2", "TEST MESSAGE 7"));

		TableModel tm = new EventTableModel(data, colnames);
		JPanel tablePanel = new JPanel();
		JTable table = new JTable(tm);
		tablePanel.add(table);
		getContentPane().add(fieldPanel, BorderLayout.NORTH);
		getContentPane().add(tablePanel, BorderLayout.CENTER);
	}

	private Vector createEvent(String level, String logger, String message) {
		Vector v = new Vector();
		v.add(level);
		v.add(logger);
		v.add(message);
		return v;
	}
	
	public static void main(String[] args) {
		RuleTest test = new RuleTest("level debug == BLAH test == logger org.apache == && ||", "( ( level == debug ) || ( BLAH == test ) ) && logger == org.apache");
		test.pack();
		test.setVisible(true);
	}

	abstract class BooleanOperator {
		abstract boolean evaluate(String firstParam, String secondParam);
	}
	 
	class AndOperator extends BooleanOperator {
		boolean evaluate(String firstParam, String secondParam) {
			System.out.println("and op");
			return (convertToBoolean(firstParam) && convertToBoolean(secondParam));
		}
	}

	boolean convertToBoolean(String param) {
		//use tostring to convert params to boolean.boolvalues
		boolean result = false;
		if (param == null) {
			return result;
		}
		try {
			result = Boolean.valueOf(param).booleanValue();
		} catch (Exception e) {
			e.printStackTrace();
			return result;
		}
		return result;				
	}
	
	class OrOperator extends BooleanOperator {
		boolean evaluate(String firstParam, String secondParam) {
			System.out.println("or op");
			return (convertToBoolean(firstParam) || convertToBoolean(secondParam));
		}
	}
	
	class PartialTextMatchOperator extends BooleanOperator {
		boolean evaluate(String firstParam, String secondParam) {
			System.out.println("part text match op " + firstParam +".."+secondParam);
			return (secondParam != null && secondParam.indexOf(firstParam) > -1) ? true:false;
		} 
	}

	class TextMatchOperator extends BooleanOperator {
		boolean evaluate(String firstParam, String secondParam) {
			System.out.println("text match op " + firstParam +".."+secondParam);
			boolean result = false;
				//second parameter is field name
				//first parameter is value
				//fake out logic here to examine passed in parameters and value retrieval from table
				if ((secondParam.equalsIgnoreCase("level") && firstParam.equalsIgnoreCase("debug")) ||
				(secondParam.equalsIgnoreCase("logger") && firstParam.equalsIgnoreCase("org.apache")))
				 {
					result = true;
				} else {
					result = ((secondParam != null && secondParam.equals(firstParam))?true:false);
				}
			return result;
		} 
	}
	
/**
 * Evaluate a boolean postfix expression.
 *
 */
	class EvaluateBooleanPostFix {
		private final Map symbolMap = new HashMap();
		private final Stack stack = new Stack();
		String result = null;

		EvaluateBooleanPostFix() {
			symbolMap.put("&&", new AndOperator());
			symbolMap.put("||", new OrOperator());
			symbolMap.put("==", new TextMatchOperator());
			symbolMap.put("~=", new PartialTextMatchOperator());
		}

		String evaluate(String expression) {
			String result = null;
			Enumeration tokenizer = new StringTokenizer(expression);
			while (tokenizer.hasMoreElements()) {
				//examine each token
				String nextToken = ((String) tokenizer.nextElement());
				//if a symbol is found, pop 2 off the stack, evaluate and push the result 
				if (symbolMap.containsKey(nextToken)) {
					BooleanOperator op = (BooleanOperator) symbolMap.get(nextToken);
					Object o = stack.pop();
					Object p = stack.pop();
					//notice the evaluate takes the 2nd parameter as the first field 
					Boolean output = Boolean.valueOf(op.evaluate(o.toString(), p.toString()));
					System.out.println("o, p,output is " + o + ".." + p + ".." + output);
					stack.push(output);
				} else {
					//variables or constants are pushed onto the stack
					stack.push(nextToken);
					//stack.push("T".equalsIgnoreCase(nextToken)?Boolean.TRUE:Boolean.FALSE);
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
			precedenceMap.put("&&", new Integer(3));			
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

			int index1 = ((Integer)precedenceMap.get(symbol1)).intValue();
			int index2 = ((Integer)precedenceMap.get(symbol2)).intValue();

			//boolean precedesResult = !(((index1 == 0) || (index1 == 1)) && (index2 > 1)); 

			boolean precedesResult = (index1 < index2); 
			System.out.println("SYMBOL1: " + symbol1 + "SYMBOL2: " + symbol2 + " index1: " + index1 + " index2: " + index2 + " precedesresult: " + precedesResult);
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
					postfix.append(token+SPACE);
					System.out.println("OPERAND - APPENDING TO POSTFIX " + postfix);
				} else {
					System.out.println("OPERATOR - ITERATING THROUGH STACK");
					while ((!(stack.size() == 0))
						&& (precedes(stack.peek().toString(), token))) {
							postfix.append(stack.pop().toString()+SPACE);
							System.out.println("appending to postfix and popping from stack - postfix: " + postfix + "..stack: " + stack);
					}
					if ((!(stack.size() == 0)) && (")".equals(token))) {
						if ("(".equals(stack.peek())) {
							System.out.println("found left paren - popping without adding to output - result: " + stack);
							stack.pop();
						} else {
							postfix.append(stack.pop().toString()+SPACE);
							System.out.println("FOUND RIGHT PAREN - POPPING - result: " + stack);
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
					System.out.println("popping left paren off stack. stack is " + stack);
				} else {
					postfix.append(stack.pop().toString()+SPACE);
					System.out.println("appending to postfix and popping from stack - postfix: " + postfix + "..stack: " + stack);
				}			
			}
			System.out.println("RETURNING " + postfix);
			stack.clear();
			return postfix.toString();
		}
	}
	
	class EventTableModel extends DefaultTableModel {
		Vector data;
		Vector colnames;
		EventTableModel(Vector data, Vector colnames) {
			super(data, colnames);
			this.data = data;
			this.colnames = colnames;
		}
	}
}