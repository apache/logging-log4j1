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

import java.util.Collection;
import java.util.LinkedList;
import java.util.Stack;

/**
 * A Factory class which, given a string representation of the rule, and a context stack, will
 * return a Rule ready for evaluation against events. 
 * 
 * @author Scott Deboy <sdeboy@apache.org>
 */
public class RuleFactory {
  private static final RuleFactory factory_ = new RuleFactory(); 
  private static final Collection rules = new LinkedList();
  private static final String AND_RULE = "&&";
  private static final String OR_RULE = "||";
  private static final String NOT_RULE = "!";
  private static final String NOT_EQUALS_RULE = "!=";
  private static final String EQUALS_RULE = "==";
  private static final String PARTIAL_TEXT_MATCH_RULE = "~=";
  private static final String LIKE_RULE = "like";
  private static final String EXISTS_RULE = "exists";
  private static final String LESS_THAN_RULE = "<";
  private static final String GREATER_THAN_RULE = ">";
  private static final String LESS_THAN_EQUALS_RULE = "<=";
  private static final String GREATER_THAN_EQUALS_RULE = ">=";
  static {
    rules.add(AND_RULE);
    rules.add(OR_RULE);
    rules.add(NOT_RULE);
    rules.add(NOT_EQUALS_RULE);
    rules.add(EQUALS_RULE);
    rules.add(PARTIAL_TEXT_MATCH_RULE);
    rules.add(LIKE_RULE);
    rules.add(EXISTS_RULE);
    rules.add(LESS_THAN_RULE);
    rules.add(GREATER_THAN_RULE);
    rules.add(LESS_THAN_EQUALS_RULE);
    rules.add(GREATER_THAN_EQUALS_RULE);
  }

  private RuleFactory() {}
  
  public static RuleFactory getInstance() {
      return factory_;
  }
  
  public boolean isRule(String symbol) {
    return ((symbol != null) && (rules.contains(symbol.toLowerCase())));
  }

  public Rule getRule(String symbol, Stack stack) {
    if (AND_RULE.equals(symbol)) {
      return AndRule.getRule(stack);
    }

    if (OR_RULE.equals(symbol)) {
      return OrRule.getRule(stack);
    }

    if (NOT_RULE.equals(symbol)) {
      return NotRule.getRule(stack);
    }

    if (NOT_EQUALS_RULE.equals(symbol)) {
      return NotEqualsRule.getRule(stack);
    }

    if (EQUALS_RULE.equals(symbol)) {
      return EqualsRule.getRule(stack);
    }

    if (PARTIAL_TEXT_MATCH_RULE.equals(symbol)) {
      return PartialTextMatchRule.getRule(stack);
    }

    if (LIKE_RULE.equalsIgnoreCase(symbol)) {
      return LikeRule.getRule(stack);
    }

    if (EXISTS_RULE.equalsIgnoreCase(symbol)) {
      return ExistsRule.getRule(stack);
    }

    if (LESS_THAN_RULE.equals(symbol)) {
      return InequalityRule.getRule(LESS_THAN_RULE, stack);
    }

    if (GREATER_THAN_RULE.equals(symbol)) {
      return InequalityRule.getRule(GREATER_THAN_RULE, stack);
    }

    if (LESS_THAN_EQUALS_RULE.equals(symbol)) {
      return InequalityRule.getRule(LESS_THAN_EQUALS_RULE, stack);
    }

    if (GREATER_THAN_EQUALS_RULE.equals(symbol)) {
      return InequalityRule.getRule(GREATER_THAN_EQUALS_RULE, stack);
    }
    return null;
  }
}
