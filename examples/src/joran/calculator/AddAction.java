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

import org.xml.sax.Attributes;

import java.util.EmptyStackException;


/**
 * A trivial action that writes "Hello world" on the console.
 *
 * See the HelloWorld class for integrating with Joran.
 *
 * @author Ceki G&uuml;lc&uuml;
 */
public class AddAction extends Action {
  public void begin(ExecutionContext ec, String name, Attributes attributes) {
    int first = fetchInteger(ec);
    int second = fetchInteger(ec);
    ec.pushObject(new Integer(first+second));
  }

  int fetchInteger(ExecutionContext ec) {
    int result = 0;

    try {
      Object o1 = ec.popObject();

      if (o1 instanceof Integer) {
        result = ((Integer) o1).intValue();
      } else {
        String errMsg =
          "Object [" + o1
          + "] currently at the top of the stack is not an integer.";
        ec.addError(new ErrorItem(errMsg));
        throw new IllegalArgumentException(errMsg);
      }
    } catch (EmptyStackException ese) {
      ec.addError(
        new ErrorItem(
          "Expecting an integer on the execution stack."));
      throw ese;
    }
    return result;
  }

  public void end(ExecutionContext ec, String name) {
    // Nothing to do here.
    // In general, the end() method of actions associated with elements
    // having no children, do not need to perform any processing in their
    // end() method.
    
    // The add computation/add element is not expected to have any children.
  }
}
