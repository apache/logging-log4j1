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

package org.apache.log4j.joran.action;

import org.apache.joran.ErrorItem;
import org.apache.joran.ExecutionContext;
import org.apache.joran.action.Action;
import org.apache.joran.helper.Option;

import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;


import org.xml.sax.Attributes;


public class ConversionRuleAction extends Action {
  static final Logger logger = Logger.getLogger(ConversionRuleAction.class);
  Layout layout;

  /**
   * Instantiates an layout of the given class and sets its name.
   *
   */
  public void begin(ExecutionContext ec, String localName, Attributes attributes) {
    // Let us forget about previous errors (in this object)
    inError = false;

    String errorMsg;
    String conversionWord =
      attributes.getValue(ActionConst.CONVERSION_WORD_ATTRIBUTE);
    String converterClass =
      attributes.getValue(ActionConst.CONVERTER_CLASS_ATTRIBUTE);

    if (Option.isEmpty(conversionWord)) {
      inError = true;
      errorMsg = "No 'conversionWord' attribute in <conversionRule>";
      logger.warn(errorMsg);
      ec.addError(new ErrorItem(errorMsg));

      return;
    }

    if (Option.isEmpty(converterClass)) {
      inError = true;
      errorMsg = "No 'converterClass' attribute in <conversionRule>";
      logger.warn(errorMsg);
      ec.addError(new ErrorItem(errorMsg));

      return;
    }

    try {
      logger.debug(
        "About to add conversion rule [" + conversionWord + ", "
        + converterClass + "] to layout");

      Object o = ec.peekObject();

      if (o instanceof PatternLayout) {
        PatternLayout patternLayout = (PatternLayout) o;
        patternLayout.addConversionRule(conversionWord, converterClass);
      }
    } catch (Exception oops) {
      inError = true;
      errorMsg = "Could not add conversion rule to PatternLayout.";
      logger.error(errorMsg, oops);
      ec.addError(new ErrorItem(errorMsg));
    }
  }

  /**
   * Once the children elements are also parsed, now is the time to activate
   * the appender options.
   */
  public void end(ExecutionContext ec, String n) {
  }

  public void finish(ExecutionContext ec) {
  }
}
