/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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
import org.apache.log4j.spi.LocationInfo;


/**
 * Location information is usually specified at the appender level -
 * all events associated
 * with an appender either create and parse stack traces or they do not.
 * This is an expensive operation and in some cases not needed
 * for all events associated with an appender.
 *
 * This filter creates event-level location information only
 * if the provided expression evaluates to true.
 *
 * For information on expression syntax,
 * see org.apache.log4j.rule.ExpressionRule
 *
 * @author Scott Deboy sdeboy@apache.org
 */
public class LocationInfoFilter extends Filter {
    /**
     * Convert to in-fix to post-fix.
     */
  boolean convertInFixToPostFix = true;
    /**
     * Expression.
     */
  String expression;
    /**
     * Compiled expression.
     */
  Rule expressionRule;
    /**
    HACK: Category is the last of the internal layers -
     pass this in as the class name
  in order for parsing to work correctly.
    */
  private String className = "org.apache.log4j.Category";

    /**
     * {@inheritDoc}
     */
  public void activateOptions() {
    expressionRule =
      ExpressionRule.getRule(expression, !convertInFixToPostFix);
  }

    /**
     * Set expression.
     * @param exp expression.
     */
  public void setExpression(final String exp) {
    this.expression = exp;
  }

    /**
     * Get expression.
     * @return expression.
     */
  public String getExpression() {
    return expression;
  }

    /**
     * Set whether in-fix expressions should be converted to post-fix.
     * @param newValue if true, convert/
     */
  public void setConvertInFixToPostFix(final boolean newValue) {
    this.convertInFixToPostFix = newValue;
  }

    /**
     * Set whether in-fix expressions should be converted to post-fix.
     * @return if true, expressions are converted.
     */
  public boolean getConvertInFixToPostFix() {
    return convertInFixToPostFix;
  }

  /**
   * If this event does not already contain location information,
   * evaluate the event against the expression.
   *
   * If the expression evaluates to true, generate a LocationInfo instance
   * by creating an exception and set this LocationInfo on the event.
   *
   * @param event event
   * @return Filter.NEUTRAL.
   */
  public int decide(final LoggingEvent event) {
    if (!event.locationInformationExists()) {
      if (expressionRule.evaluate(event)) {
          Throwable t = new Exception();
          event.setLocationInformation(new LocationInfo(t, className));
      }
    }
    return Filter.NEUTRAL;
  }
}
