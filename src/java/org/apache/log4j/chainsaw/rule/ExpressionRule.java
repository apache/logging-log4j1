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

import org.apache.log4j.spi.LoggingEvent;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.StringTokenizer;

/**
 * A Rule class supporting both infix and postfix expressions, accepting any rule which 
 * is supported by the <code>RuleFactory</code>.
 * 
 * NOTE: parsing is supported through the use of <code>StringTokenizer</code>, which means 
 * all tokens in the expression must be separated by spaces. 
 * 
 * @author Scott Deboy <sdeboy@apache.org>
 */

public class ExpressionRule extends AbstractRule {
  private static InFixToPostFix convertor = new InFixToPostFix();
  private static PostFixExpressionCompiler compiler =
    new PostFixExpressionCompiler();
  List list = null;
  Stack stack = new Stack();

  private ExpressionRule(List list) {
    this.list = list;
  }

  static Rule getRule(String expression, boolean isPostFix) {
    if (!isPostFix) {
      expression = convertor.convert(expression);
    }

    return new ExpressionRule(compiler.compileExpression(expression));
  }

  public boolean evaluate(LoggingEvent event) {
    stack.clear();

    boolean result = false;
    Iterator iter = list.iterator();

    while (iter.hasNext()) {
      //examine each token
      Object nextItem = iter.next();

      //if a symbol is found, pop 2 off the stack, evaluate and push the result 
      if (nextItem instanceof Rule) {
        Rule r = (Rule) nextItem;
        stack.push(new Boolean(r.evaluate(event)));
      } else {
        //variables or constants are pushed onto the stack
        stack.push(nextItem);
      }
    }

    if (stack.size() > 0) {
      result = new Boolean(stack.pop().toString()).booleanValue();
    }

    return result;
  }
}


/**
 * Evaluate a boolean postfix expression.
 *
 */
class PostFixExpressionCompiler {

  List compileExpression(String expression) {
    System.out.println("compiling expression: " + expression);

    List list = new LinkedList();
    Stack stack = new Stack();
    Enumeration tokenizer = new StringTokenizer(expression);

    while (tokenizer.hasMoreElements()) {
      //examine each token
      String nextToken = ((String) tokenizer.nextElement()).toLowerCase();

      //if a symbol is found, pop 2 off the stack, evaluate and push the result 
      if (RuleFactory.isRule(nextToken)) {
        Rule r = (Rule) RuleFactory.getRule(nextToken, stack);
        System.out.println("pushing rule " + r);
        stack.push(r);
      } else {
        System.out.println("pushing token " + nextToken);

        //variables or constants are pushed onto the stack
        stack.push(nextToken);
      }
    }

    while (!stack.isEmpty()) {
      list.add(stack.pop());
    }

    return list;
  }
}
