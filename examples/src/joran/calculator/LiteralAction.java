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

package joran.calculator;

import org.apache.joran.ErrorItem;
import org.apache.joran.ExecutionContext;
import org.apache.joran.action.Action;
import org.apache.joran.helper.Option;

import org.xml.sax.Attributes;


/**
 *
 * @author Ceki G&uuml;lc&uuml;
 */
public class LiteralAction extends Action {
  public static String VALUE_ATR = "value";

  public void begin(ExecutionContext ec, String name, Attributes attributes) {
    String valueStr = attributes.getValue(VALUE_ATR);

  
    if (Option.isEmpty(valueStr)) {
      ec.addError(
        new ErrorItem(
          "The literal element requires a value attribute"));
    }

    try {
      Integer i = Integer.valueOf(valueStr);
      ec.pushObject(i);
    } catch (NumberFormatException nfe) {
      ec.addError(
        new ErrorItem(
          "The value [" + valueStr + "] could not be converter to an Integer",
          nfe));
      throw nfe;
    }
  }

  public void end(ExecutionContext ec, String name) {
    // Nothing to do here.
    // In general, the end() method of actions associated with elements
    // having no children do not need to perform any processing in their
    // end() method.
  }
}
