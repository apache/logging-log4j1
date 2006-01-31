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

package org.apache.log4j.rule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * A helper class which converts infix expressions to postfix expressions
 * Currently grouping is supported, as well as all of the Rules supported by <code>RuleFactory</code>
 * 
 * NOTE: parsing is supported through the use of <code>StringTokenizer</code>, which means 
 * all tokens in the expression must be separated by spaces.
 * 
 * Supports grouping via parens, mult-word operands using single quotes, and these operators:
 * 
 * !        NOT operator
 * !=       NOT EQUALS operator
 * ==       EQUALS operator 
 * ~=       CASE-INSENSITIVE equals operator
 * ||       OR operator
 * &&       AND operator
 * like     REGEXP operator
 * exists   NOT NULL operator
 * &lt      LESS THAN operator
 * &gt      GREATER THAN operator
 * &lt=     LESS THAN EQUALS operator
 * &gt=     GREATER THAN EQUALS operator
 * 
 * @author Scott Deboy <sdeboy@apache.org>
 */

public class InFixToPostFix {
  private final Map precedenceMap = new HashMap();
  private final List operators = new Vector();

  public InFixToPostFix() {
    //boolean operators
    operators.add("!");
    operators.add("!=");
    operators.add("==");
    operators.add("~=");
    operators.add("||");
    operators.add("&&");
    operators.add("like");
    operators.add("exists");
    operators.add("<");
    operators.add(">");
    operators.add("<=");
    operators.add(">=");
    
    //boolean precedence
    precedenceMap.put("<", new Integer(3));
    precedenceMap.put(">", new Integer(3));
    precedenceMap.put("<=", new Integer(3));
    precedenceMap.put(">=", new Integer(3));
    
    precedenceMap.put("!", new Integer(3));
    precedenceMap.put("!=", new Integer(3));
    precedenceMap.put("==", new Integer(3));
    precedenceMap.put("~=", new Integer(3));
    precedenceMap.put("like", new Integer(3));
    precedenceMap.put("exists", new Integer(3));
    
    precedenceMap.put("||", new Integer(2));
    precedenceMap.put("&&", new Integer(2));
  }

  public String convert(String expression) {
    return infixToPostFix(new StringTokenizer(expression));
  }

  boolean isOperand(String symbol) {
    symbol = symbol.toLowerCase();

    return (!operators.contains(symbol));
  }

  boolean precedes(String symbol1, String symbol2) {
    symbol1 = symbol1.toLowerCase();
    symbol2 = symbol2.toLowerCase();

    if (!precedenceMap.keySet().contains(symbol1)) {
      return false;
    }

    if (!precedenceMap.keySet().contains(symbol2)) {
      return false;
    }

    int index1 = ((Integer) precedenceMap.get(symbol1)).intValue();
    int index2 = ((Integer) precedenceMap.get(symbol2)).intValue();

    boolean precedesResult = (index1 < index2);

    return precedesResult;
  }

  String infixToPostFix(StringTokenizer tokenizer) {
    String SPACE = " ";
    StringBuffer postfix = new StringBuffer();

    Stack stack = new Stack();

    while (tokenizer.hasMoreTokens()) {
      String token = tokenizer.nextToken();

      boolean inText = (token.startsWith("'") && (!token.endsWith("'")));
      if (inText) {
          while (inText && tokenizer.hasMoreTokens()) {
            token = token + " " + tokenizer.nextToken();
            inText = !(token.endsWith("'"));
        }
      }

      if ("(".equals(token)) {
        //recurse
        postfix.append(infixToPostFix(tokenizer));
        postfix.append(SPACE);
      } else if (")".equals(token)) {
        //exit recursion level
        while (stack.size() > 0) {
          postfix.append(stack.pop().toString());
          postfix.append(SPACE);
        }

        return postfix.toString();
      } else if (isOperand(token)) {
        postfix.append(token);
        postfix.append(SPACE);
      } else {
        //operator..
        //peek the stack..if the top element has a lower precedence than token
        //(peeked + has lower precedence than token *), push token onto the stack
        //otherwise, pop top element off stack and add to postfix string
        //in a loop until lower precedence or empty..then push token
        if (stack.size() > 0) {

          String peek = stack.peek().toString();

          if (precedes(peek, token)) {
            stack.push(token);
          } else {
            boolean bypass = false;

            do {
              if (
                (stack.size() > 0)
                  && !precedes(stack.peek().toString(), token)) {
                postfix.append(stack.pop().toString());
                postfix.append(SPACE);
              } else {
                bypass = true;
              }
            } while (!bypass);

            stack.push(token);
          }
        } else {
          stack.push(token);
        }
      }
    }

    while (stack.size() > 0) {
      postfix.append(stack.pop().toString());
      postfix.append(SPACE);
    }

    return postfix.toString();
  }
}
