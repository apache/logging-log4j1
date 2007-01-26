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


import org.apache.log4j.helpers.Option;
import org.apache.log4j.joran.spi.ExecutionContext;
import org.apache.log4j.joran.spi.Pattern;
import org.apache.log4j.spi.ErrorItem;
import org.xml.sax.Attributes;


public class NewRuleAction extends Action {
  
  /**
   * Instantiates an layout of the given class and sets its name.
   *
   */
  public void begin(ExecutionContext ec, String localName, Attributes attributes) {
    String errorMsg;
    String pattern =  attributes.getValue(Action.PATTERN_ATTRIBUTE);
    String actionClass =  attributes.getValue(Action.ACTION_CLASS_ATTRIBUTE);

    if(Option.isEmpty(pattern)) {
       errorMsg = "No 'pattern' attribute in <newRule>";
       getLogger().warn(errorMsg);
       ec.addError(new ErrorItem(errorMsg));
       return;
     }
    
     if(Option.isEmpty(actionClass)) {
         errorMsg = "No 'actionClass' attribute in <newRule>";
         getLogger().warn(errorMsg);
         ec.addError(new ErrorItem(errorMsg));
         return;
     }
       
    try {
      getLogger().debug("About to add new Joran parsing rule ["+pattern+","+actionClass+"].");
      ec.getJoranInterpreter().getRuleStore().addRule(new Pattern(pattern), actionClass);
    } catch (Exception oops) {
      errorMsg =  "Could not add new Joran parsing rule ["+pattern+","+actionClass+"]"; 
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
