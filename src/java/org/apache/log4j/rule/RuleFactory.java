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

import java.util.Collection;
import java.util.LinkedList;
import java.util.Stack;

import org.apache.log4j.helpers.LogLog;

/**
 * A Factory class which, given a string representation of the rule, and a context stack, will
 * return a Rule ready for evaluation against events.  If an operator is requested that isn't supported, 
 * or if a LIKE rule is requested and the ORO package is not available, an IllegalArgumentException is thrown. 
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
    try {
    	Class.forName("org.apache.oro.text.regex.Perl5Compiler");
    	rules.add(LIKE_RULE);
    } catch (Exception e) {
    	LogLog.info("ORO classes not found - Like rule not supported");
    }
    	
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

    if (rules.contains(LIKE_RULE) && LIKE_RULE.equalsIgnoreCase(symbol)) {
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
    throw new IllegalArgumentException("Invalid rule: " + symbol);
  }
}
