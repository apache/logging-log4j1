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

package org.apache.log4j.varia;

import org.apache.log4j.rule.ExpressionRule;
import org.apache.log4j.rule.Rule;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;


/**
 *A filter supporting complex expressions - supports both infix and postfix expressions 
 *(infix expressions must first be converted to postfix prior to processing).
 *
 *See <code>org.apache.log4j.chainsaw.LoggingEventFieldResolver.java</code> 
 *for the correct names for logging event fields used when building expressions.
 *
 *See <org.apache.log4j.chainsaw.rule</code> package for a list of available rules which can
 *be applied using the expression syntax.
 *
 *See <code>org.apache.log4j.chainsaw.RuleFactory</code> for the symbols used to 
 *activate the corresponding rules.
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
    if ((expression == null)) {
      return Filter.NEUTRAL;
    }

    if (expressionRule.evaluate(event)) {
      if (acceptOnMatch) {
        return Filter.ACCEPT;
      } else {
        return Filter.DENY;
      }
    } else {
      return Filter.NEUTRAL;
    }
  }
}
