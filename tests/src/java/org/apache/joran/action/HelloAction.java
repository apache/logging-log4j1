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

package org.apache.joran.action;

import org.apache.joran.ExecutionContext;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;

import org.w3c.dom.Element;


public class HelloAction extends Action {
  static final Logger logger = Logger.getLogger(HelloAction.class);
  Layout layout;


  public HelloAction() {
  }
  /**
   * Instantiates an layout of the given class and sets its name.
   *
   */
  public void begin(ExecutionContext ec, Element element) {

    String name = element.getAttribute("name");
    String str = "Hello "+name+".";
    ec.getObjectMap().put("hello", str);
  }

  /**
   * Once the children elements are also parsed, now is the time to activate
   * the appender options.
   */
  public void end(ExecutionContext ec, Element e) {
  }

  public void finish(ExecutionContext ec) {
  }
}
