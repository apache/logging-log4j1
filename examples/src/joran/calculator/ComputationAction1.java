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

import org.apache.joran.ExecutionContext;
import org.apache.joran.action.Action;
import org.apache.joran.helper.Option;

import org.xml.sax.Attributes;


/**
 * The ComputationAction will print the result of the compuration made by 
 * children elements but only if the compuration itself is named, that is if the
 * name attribute of the associated computation element is not null. In other
 * words, anonymous computations will not print their result.
 * 
 * @author Ceki G&uuml;lc&uuml;
 */
public class ComputationAction1 extends Action {
  public static String NAME_ATR = "name";

  String nameStr;

  /**
   * Store the value of the name attribute for future use.
   */
  public void begin(ExecutionContext ec, String name, Attributes attributes) {
    nameStr = attributes.getValue(NAME_ATR);
  }

  /**
   * Children elements have been processed. The sesults should be an integer 
   * placed at the top of the execution stack.
   * 
   * This value will be printed on the console but only if the action is 
   * named. Anonymous computation will not print their result.
   */
  public void end(ExecutionContext ec, String name) {
    if (Option.isEmpty(nameStr)) {
      // nothing to do
    } else {
      Integer i = (Integer) ec.peekObject();
      System.out.println(
        "The computation named [" + nameStr + "] resulted in the value " + i);
    }
  }
}
