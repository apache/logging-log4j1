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

package org.apache.log4j.filter;

import org.apache.log4j.rule.ExpressionRule;
import org.apache.log4j.rule.Rule;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;


/**
 *A filter supporting complex expressions - supports both infix and postfix 
 * expressions (infix expressions must first be converted to postfix prior 
 * to processing).
 *
 * <p>See <code>org.apache.log4j.chainsaw.LoggingEventFieldResolver.java</code> 
 * for the correct names for logging event fields used when building expressions.
 *
 * <p>See <org.apache.log4j.chainsaw.rule</code> package for a list of available
 * rules which can be applied using the expression syntax.
 *
 * <p>See <code>org.apache.log4j.chainsaw.RuleFactory</code> for the symbols 
 * used to activate the corresponding rules.
 *
 *NOTE:  Grouping using parentheses is supported - all tokens must be separated by spaces, and
 *operands which contain spaces are not yet supported.
 *
 *Example:
 *
 *In order to build a filter that displays all messages with infomsg-45 or infomsg-44 in the message,
 *as well as all messages with a level of WARN or higher, build an expression using 
 *the LikeRule (supports ORO-based regular expressions) and the InequalityRule. 
 * <b> ( MSG LIKE infomsg-4[4,5] ) && ( LEVEL >= WARN ) </b>
 *  
 *Three options are required:
 *  <b>Expression</b> - the expression to match
 *  <b>ConvertInFixToPostFix</b> - convert from infix to posfix (default true)
 *  <b>AcceptOnMatch</b> - true or false (default true)
 *
 * Meaning of <b>AcceptToMatch</b>:
 * If there is a match between the value of the
 * Expression option and the {@link LoggingEvent} and AcceptOnMatch is true,
 * the {@link #decide} method returns {@link Filter#ACCEPT}.
 *
 * If there is a match between the value of the
 * Expression option and the {@link LoggingEvent} and AcceptOnMatch is false,
 * {@link Filter#DENY} is returned.
 *
 * If there is no match, {@link Filter#NEUTRAL} is returned.
 *
 * @author Scott Deboy sdeboy@apache.org
 */
public class ExpressionFilter extends Filter {
  boolean acceptOnMatch = true;
  boolean convertInFixToPostFix = true;
  String expression;
  Rule expressionRule;

  public void activateOptions() {
    expressionRule =
      ExpressionRule.getRule(expression, !convertInFixToPostFix);
  }

  public void setExpression(String expression) {
    this.expression = expression;
  }

  public String getExpression() {
    return expression;
  }

  public void setConvertInFixToPostFix(boolean convertInFixToPostFix) {
    this.convertInFixToPostFix = convertInFixToPostFix;
  }

  public boolean getConvertInFixToPostFix() {
    return convertInFixToPostFix;
  }

  public void setAcceptOnMatch(boolean acceptOnMatch) {
    this.acceptOnMatch = acceptOnMatch;
  }

  public boolean getAcceptOnMatch() {
    return acceptOnMatch;
  }

  /**
     Returns {@link Filter#NEUTRAL} is there is no string match.
   */
  public int decide(LoggingEvent event) {
    if (expressionRule.evaluate(event)) {
      return (acceptOnMatch?Filter.ACCEPT:Filter.DENY);
    }
    return Filter.NEUTRAL;
  }
}
