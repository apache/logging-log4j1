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
 * ComputationAction2 will print the result of the compuration made by 
 * children elements but only if the compuration itself is named, that is if the
 * name attribute of the associated computation element is not null. In other
 * words, anonymous computations will not print their result.
 * 
 * ComputationAction2 differs from ComputationAction1 in its handling of
 * instance variables. ComputationAction1 has a simple <Code>nameStr</code>
 * instance variable. This variable is set when the begin() method is called 
 * and then later used within the end() method. 
 * 
 * This simple approach works properly if the begin() and end()
 * method of a given action are expected to be called in sequence. However,
 * there are situations where the begin() method of the same action instance is 
 * invoked multiple times before the matching end() method is invoked. 
 * 
 * When this happens, the second call to begin() overwrites values set by
 * the first invocation to begin(). The solution is to save parameter values 
 * into a separate stack. The well-formedness of XML will guarantee that a value
 * saved by one begin() will be consumed only by the matching end() method.
 * 
 * Note that in the vast majority of cases there is no need to resort to a 
 * separate stack for each variable. The situation of successibe begin() 
 * invocations can only occur if: 
 * 
 * 1) the associated pattern contains a wildcard, i.e. the &#42; character
 * 
 * and
 * 
 * 2) the associated element tag can contain itself as a child 
 *  
 * For example, "&#42;/computation" pattern means that computations can contain
 * other computation elements as children. 
 * 
 * @author Ceki G&uuml;lc&uuml;
 */
public class ComputationAction2 extends Action {
  public static String NAME_ATR = "name";

  Stack nameStrStack = new Stack();
  
  
  public void begin(ExecutionContext ec, String name, Attributes attributes) {
    String nameStr = attributes.getValue(NAME_ATR);
    // save nameStr value in a special stack. Note that the value is saved
    // even if it is empty or null.
    nameStrStack.push(nameStr);
  }

  public void end(ExecutionContext ec, String name) {
    // pop nameStr value from the special stack
    String nameStr = (String) nameStrStack.pop();
    
    if (Option.isEmpty(nameStr)) {
      // nothing to do
    } else {
      Integer i = (Integer) ec.peekObject();
      System.out.println(
        "The computation named [" + nameStr + "] resulted in the value " + i);
    }
  }
}
