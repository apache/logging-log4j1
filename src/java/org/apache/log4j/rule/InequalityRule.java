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

import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.LoggingEventFieldResolver;

import java.util.Stack;


/**
 * A Rule class implementing inequality evaluation - expects to be able to convert two values to longs.
 * If a specific inequality evaluation class has been provided for the event field, the appropriate rule is returned.
 * (For example, if the expression is Level &lt DEBUG, a LevelInequalityRule is returned).
 *
 * @author Scott Deboy <sdeboy@apache.org>
 */
public class InequalityRule extends AbstractRule {
  private static final LoggingEventFieldResolver resolver = LoggingEventFieldResolver.getInstance();
  private final String field;
  private final String value;
  private final String inequalitySymbol;

  private InequalityRule(
    String inequalitySymbol, String field, String value) {
    this.inequalitySymbol = inequalitySymbol;
    if (!resolver.isField(field)) {
        throw new IllegalArgumentException("Invalid " + inequalitySymbol + " rule - " + field + " is not a supported field");
    }

    this.field = field;
    this.value = value;
  }
  
  public static Rule getRule(String inequalitySymbol, Stack stack) {
      if (stack.size() < 2) {
          throw new IllegalArgumentException("Invalid " + inequalitySymbol + " rule - expected two parameters but received " + stack.size());
      }  

      String p2 = stack.pop().toString();
      String p1 = stack.pop().toString();
      return getRule(inequalitySymbol, p1, p2);
  }
  
  public static Rule getRule(String inequalitySymbol, String field, String value) {
    if (field.equalsIgnoreCase(LoggingEventFieldResolver.LEVEL_FIELD)) {
      //push the value back on the stack and allow the level-specific rule pop values
      return LevelInequalityRule.getRule(inequalitySymbol, value);
    } else if (field.equalsIgnoreCase(LoggingEventFieldResolver.TIMESTAMP_FIELD)){
      return TimestampInequalityRule.getRule(inequalitySymbol, value);
    } else {
      return new InequalityRule(inequalitySymbol, field, value);
    }
  }

  public boolean evaluate(LoggingEvent event) {
    long first = 0;

    try {
      first =
        new Long(resolver.getValue(field, event).toString()).longValue();
    } catch (NumberFormatException nfe) {
      return false;
    }

    long second = 0;

    try {
      second = new Long(value).longValue();
    } catch (NumberFormatException nfe) {
      return false;
    }

    boolean result = false;

    if ("<".equals(inequalitySymbol)) {
      result = first < second;
    } else if (">".equals(inequalitySymbol)) {
      result = first > second;
    } else if ("<=".equals(inequalitySymbol)) {
      result = first <= second;
    } else if (">=".equals(inequalitySymbol)) {
      result = first >= second;
    }

    return result;
  }
}
