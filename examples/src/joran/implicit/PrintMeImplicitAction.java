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

package joran.implicit;

import org.apache.joran.ExecutionContext;
import org.apache.joran.Pattern;
import org.apache.joran.action.ImplicitAction;

import org.xml.sax.Attributes;



/**
 *
 * A rather trivial implicit action which is applicable as soon as an
 * element has a printme attribute set to true. 
 *
 * @author Ceki G&uuml;lc&uuml;
 */
public class PrintMeImplicitAction extends ImplicitAction {
  
  public boolean isApplicable(
    Pattern pattern, Attributes attributes, ExecutionContext ec) {
    String printmeStr = attributes.getValue("printme");

    return Boolean.valueOf(printmeStr).booleanValue();
  }

  public void begin(ExecutionContext ec, String name, Attributes attributes) {
    System.out.println("Element <"+name+"> asked to be printed.");
   }

 
  public void end(ExecutionContext ec, String name) {
  }
}
