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

import java.util.Stack;

import org.apache.joran.ExecutionContext;
import org.apache.joran.action.Action;
import org.apache.joran.helper.Option;

import org.xml.sax.Attributes;


/**
 * The ComputationAction will print the result of the compuration made by 
 * children elements but only if the compuration itself is named.
 * 
 * @author Ceki G&uuml;lc&uuml;
 */
public class ComputationAction2 extends Action {
  public static String NAME_ATR = "name";

  Stack paremeterStack = new Stack();
  
  
  public void begin(ExecutionContext ec, String name, Attributes attributes) {
    String nameStr = attributes.getValue(NAME_ATR);
    paremeterStack.push(nameStr);
  }

  public void end(ExecutionContext ec, String name) {
    String nameStr = (String) paremeterStack.pop();
    if (Option.isEmpty(nameStr)) {
      // nothing to do
    } else {
      Integer i = (Integer) ec.peekObject();
      System.out.println(
        "The computation named [" + nameStr + "] resulted in the value " + i);
    }
  }
}
