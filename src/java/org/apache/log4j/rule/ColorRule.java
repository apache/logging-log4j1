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

import java.awt.Color;
import java.io.Serializable;

import org.apache.log4j.spi.LoggingEvent;


/**
 * A Rule class which also holds a color
 *
 * @author Scott Deboy <sdeboy@apache.org>
 */
public class ColorRule extends AbstractRule implements Serializable {
  static final long serialVersionUID = -794434783372847773L;

  private final Rule rule;
  private final Color foregroundColor;
  private final Color backgroundColor;
  private final String expression;

  public ColorRule(String expression, Rule rule, Color backgroundColor, Color foregroundColor) {
    this.expression = expression;
    this.rule = rule;
    this.backgroundColor = backgroundColor;
    this.foregroundColor = foregroundColor;
  }

  public Rule getRule() {
      return rule;
  }
  
  public Color getForegroundColor() {
    return foregroundColor;
  }

  public Color getBackgroundColor() {
    return backgroundColor;
  }
  
  public String getExpression() {
      return expression;
  }

  public boolean evaluate(LoggingEvent event) {
    return (rule != null && rule.evaluate(event));
  }
  
  public String toString() {
      return "color rule - expression: " + expression+", rule: " + rule + " bg: " + backgroundColor + " fg: " + foregroundColor;
  }
}
