/*
 * Copyright 1999,2006 The Apache Software Foundation.
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

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.PatternLayout;
import org.apache.log4j.helpers.Option;
import org.apache.log4j.joran.spi.ExecutionContext;
import org.apache.log4j.spi.ErrorItem;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.LoggerRepositoryEx;


import org.xml.sax.Attributes;


public class ConversionRuleAction extends Action {
  
  /**
   * Instantiates an layout of the given class and sets its name.
   *
   */
  public void begin(ExecutionContext ec, String localName, Attributes attributes) {

    String errorMsg;
    String conversionWord =
      attributes.getValue(ActionConst.CONVERSION_WORD_ATTRIBUTE);
    String converterClass =
      attributes.getValue(ActionConst.CONVERTER_CLASS_ATTRIBUTE);

    if (Option.isEmpty(conversionWord)) {
      errorMsg = "No 'conversionWord' attribute in <conversionRule>";
      getLogger().warn(errorMsg);
      ec.addError(new ErrorItem(errorMsg));

      return;
    }

    if (Option.isEmpty(converterClass)) {
      errorMsg = "No 'converterClass' attribute in <conversionRule>";
      getLogger().warn(errorMsg);
      ec.addError(new ErrorItem(errorMsg));

      return;
    }

    try {
      getLogger().debug(
        "About to add conversion rule [{}, {}] to layout", conversionWord, converterClass);

      LoggerRepository repository = (LoggerRepository) ec.getObjectStack().get(0);

      //
      //   cast may fail with user supplied repository
      Map ruleRegistry = (Map) ((LoggerRepositoryEx) repository).getObject(PatternLayout.PATTERN_RULE_REGISTRY);
      if(ruleRegistry == null) {
        ruleRegistry = new HashMap();
        ((LoggerRepositoryEx) repository).putObject(PatternLayout.PATTERN_RULE_REGISTRY, ruleRegistry);
      }
      // put the new rule into the rule registry
      ruleRegistry.put(conversionWord, converterClass);
  
    } catch (Exception oops) {
      errorMsg = "Could not add conversion rule to PatternLayout.";
      getLogger().error(errorMsg, oops);
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
