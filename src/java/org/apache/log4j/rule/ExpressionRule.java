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

import org.apache.log4j.spi.LoggingEvent;

import java.util.Stack;
import java.util.StringTokenizer;


/**
 * A Rule class supporting both infix and postfix expressions, accepting any rule which
 * is supported by the <code>RuleFactory</code>.
 *
 * NOTE: parsing is supported through the use of <code>StringTokenizer</code>, which
 * implies two limitations:
 * 1: all tokens in the expression must be separated by spaces, including parenthese
 * 2: operands which contain spaces MUST be wrapped in single quotes. 
 *    For example, the expression:
 *      msg == 'some msg'
 *    is a valid expression.
 * 3: To group expressions, use parentheses.
 *    For example, the expression:
 *      level >= INFO || ( msg == 'some msg' || logger == 'test' ) 
 *    is a valid expression.
 * See org.apache.log4j.rule.InFixToPostFix for a description of supported operators.
 * See org.apache.log4j.spi.LoggingEventFieldResolver for field keywords.
 *
 * @author Scott Deboy <sdeboy@apache.org>
 */
public class ExpressionRule extends AbstractRule {
  private static final InFixToPostFix convertor = new InFixToPostFix();
  private static final PostFixExpressionCompiler compiler = new PostFixExpressionCompiler();
  private final Rule rule;

  private ExpressionRule(Rule rule) {
    this.rule = rule;
  }

  public static Rule getRule(String expression) {
      return getRule(expression, false);
  }
  
  public static Rule getRule(String expression, boolean isPostFix) {
    if (!isPostFix) {
      expression = convertor.convert(expression);
    }

    return new ExpressionRule(compiler.compileExpression(expression));
  }

  public boolean evaluate(LoggingEvent event) {
    return rule.evaluate(event);
  }
  
  public String toString() {
      return rule.toString();
  }

  /**
   * Evaluate a boolean postfix expression.
   *
   */
  static class PostFixExpressionCompiler {
    Rule compileExpression(String expression) {
      RuleFactory factory = RuleFactory.getInstance();

      Stack stack = new Stack();
      StringTokenizer tokenizer = new StringTokenizer(expression);

      while (tokenizer.hasMoreTokens()) {
        //examine each token
        String token = tokenizer.nextToken();
        if ((token.startsWith("'")) && (token.endsWith("'") && (token.length() > 2))) {
            token = token.substring(1, token.length() - 1);
        }

        boolean inText = token.startsWith("'");
        if (inText) {
            token=token.substring(1);
            while (inText && tokenizer.hasMoreTokens()) {
              token = token + " " + tokenizer.nextToken();
              inText = !(token.endsWith("'"));
          }
          token = token.substring(0, token.length() - 1);
        }

        //if a symbol is found, pop 2 off the stack, evaluate and push the result 
        if (factory.isRule(token)) {
          Rule r = (Rule) factory.getRule(token, stack);
          stack.push(r);
        } else {
          //variables or constants are pushed onto the stack
          stack.push(token);
        }
      }

      if ((stack.size() == 0) || (!(stack.peek() instanceof Rule))) {
        throw new IllegalArgumentException("invalid expression: " + expression);
      } else {
        return (Rule) stack.pop();
      }
    }
  }
}


