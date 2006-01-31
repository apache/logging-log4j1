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

import java.util.Stack;

/**
 * A Rule class implementing logical or. 
 * 
 * @author Scott Deboy <sdeboy@apache.org>
 */
public class OrRule extends AbstractRule {
  static final long serialVersionUID = 2088765995061413165L;    
  private final Rule rule1;
  private final Rule rule2;

  private OrRule(Rule firstParam, Rule secondParam) {
    this.rule1 = firstParam;
    this.rule2 = secondParam;
  }

  public static Rule getRule(Rule firstParam, Rule secondParam) {
      return new OrRule(firstParam, secondParam);
  }
  
  public static Rule getRule(Stack stack) {
      if (stack.size() < 2) {
          throw new IllegalArgumentException("Invalid OR rule - expected two rules but received " + stack.size());
      }  
      Object o2 = stack.pop();
      Object o1 = stack.pop();
      if ((o2 instanceof Rule) && (o1 instanceof Rule)) { 
          Rule p2 = (Rule) o2;
          Rule p1 = (Rule) o1;
          return new OrRule(p1, p2);
      }
      throw new IllegalArgumentException("Invalid OR rule: " + o2 + "..." + o1);
  }

  public boolean evaluate(LoggingEvent event) {
    return (rule1.evaluate(event) || rule2.evaluate(event));
  }
}
